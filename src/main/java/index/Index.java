/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:01
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.H2Storage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class Index implements Serializable {

  public static final String INDICES_DIRECTORY = "indices\\";
  public static final String INDEX_FILE_EXTENSION = ".ser";

  //  private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
  private transient static Logger log = Logger.getLogger(Index.class.getName());

  private transient IndexingHandler handler;
  private IndexStorage storage;
  private IndexParameters parameters;
  private String name;
  private Set<Path> indexedPaths;

  // ===============  Constructors
  private Index() {
    this.indexedPaths = new HashSet<>();
  }

  public Index(String name, IndexParameters parameters, IndexStorage storage) {
    this();
    this.parameters = parameters;
    this.storage = storage;
    this.handler = new IndexingHandler(this);
    this.name = name;
  }

  public Index(String name, IndexParameters parameters) {
    // h2 - default storage
    this(name, parameters, new H2Storage(parameters, name));
  }

  // ===============  Saving

  public static Index load(String name) {
    Index index = null;
    try {
      // creating streams
      FileInputStream fileIn = new FileInputStream(new RandomAccessFile(name, "rw").getFD());
      ObjectInputStream in = new ObjectInputStream(fileIn);

      // red name + parameters (it's fast operation)
      String indexName = (String) in.readObject();
      IndexParameters indexParameters = (IndexParameters) in.readObject();
      Set<String> indexedAsStrings = (Set<String>) in.readObject();
      Set<Path> indexedPaths = indexedAsStrings.stream().map(s -> Paths.get(s)).collect(Collectors.toSet());
      Class storageClass = (Class) in.readObject();
      // creating index with specified name + parameters
      index = new Index(indexName, indexParameters);
      index.indexedPaths = indexedPaths;

      if (storageClass == H2Storage.class) {
        log.info("Index " + name + " loaded.");
      } else {
        // make final index
        Index finalIndex = index;
        // run thread with long operation - loading index storage
        new Thread(() -> {
          try {
            // load storage
            finalIndex.storage = (IndexStorage) in.readObject();
            // say info
            log.info("Index " + name + " loaded.");
          } catch (IOException | ClassNotFoundException e) {
            log.severe("File-Index " + name + " was not loaded, problem acquired:" + e.getMessage());
          } finally {
            try {
              // closing streams
              fileIn.close();
              in.close();
            } catch (IOException e) {
              log.fine(
                  "File-Index " + name + " was loaded, but streams were not closed. Problem acquired:" + e
                      .getMessage());
            }
          }
        }).start();
      }
    } catch (Exception e) {
      log.severe("File-Index " + name + " was not loaded, problem acquired:" + e.getMessage());
    }
    return index;
  }

  public void save(String directory) {
    try {
      // create filename
      String filename = directory + name + ".ser";
      // open streams
      FileOutputStream fileOut = new FileOutputStream(new RandomAccessFile(filename, "rw").getFD());
      ObjectOutputStream out = new ObjectOutputStream(fileOut);

      storage.exit();
      // write objects in order - name, parameters, storage
      out.writeObject(name);
      out.writeObject(parameters);
      Set<String> indexedAsStrings = indexedPaths.stream().map(Path::toString).collect(Collectors.toSet());
      out.writeObject(indexedAsStrings);
      out.writeObject(storage.getClass());
      if (!(storage instanceof H2Storage)) {
        out.writeObject(storage);
      }

      // close streams
      out.flush();
      out.close();
      fileOut.flush();
      fileOut.close();
      log.info("Index " + name + " saved to " + filename);
    } catch (IOException i) {
      log.severe("Index " + name + " was not saved, problem acquired:" + i.getMessage());
    }
  }

  // ===============  Operations

  public long index(IndexingRequest request) {
    log.info("Indexing with request \"" + request.getId().toString() + "\" started");
    // add paths to history
    indexedPaths.addAll(request.getPaths());
    // index paths
    long indexed = -1;
    try {
      indexed = handler.index(request);
      log.info("Indexing with request \"" + request.getId().toString() + "\" completed.");
      return indexed;
    } catch (IOException | InterruptedException e) {
      log.severe("Indexing path \"" + request.getId().toString() + "\" interrupted.\n" + e.getMessage());
      e.printStackTrace();
    }
    return indexed;
  }

  public void search(SearchRequest request) {
    log.info("Searching with request \"" + request.getSearchFor() + "\" started");
    storage.search(request);
  }

  public void remove(Set<Path> temp) {
    UUID uuid = UUID.randomUUID();
    log.info("Removing paths from collection #" + uuid + " started.");
    long deleted = storage.remove(temp);
    log.info("Removing paths from collection #" + uuid + " ended. [deleted=" + deleted + "]");
  }

  // ===============  Getters/Setters
  public IndexStorage getStorage() {
    return storage;
  }

  public IndexParameters getParameters() {
    return parameters;
  }

  public String getName() {
    return name;
  }

  public Set<Path> getIndexedPaths() {
    return indexedPaths;
  }

  public Index clone() {
    Index cloned = new Index();
    cloned.handler = new IndexingHandler(cloned);
    cloned.storage = this.storage; // just a reference, not a real copy
    cloned.parameters = this.parameters.clone();
    cloned.name = this.name;
    cloned.indexedPaths = new HashSet<>(this.indexedPaths);
    return cloned;
  }

  public void changeName(String name) {
    if ("".equals(name)) {
      throw new IllegalArgumentException("Name is empty");
    }
    // should delete all savings
    new File(INDICES_DIRECTORY + getName() + INDEX_FILE_EXTENSION).delete();
    // set new name
    this.name = name;
    storage.changeName(name);
//    throw new UnsupportedOperationException("Not implemented");
  }
}
