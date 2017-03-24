/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:01
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.InvertedIndex;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.logging.Logger;
import org.nustaq.serialization.FSTConfiguration;

/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class Index implements Serializable {

  private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();
  private transient static Logger log;

  static {
    log = Logger.getLogger(Index.class.getName());
  }

  private transient IndexingHandler handler;
  private IndexStorage storage;
  private IndexParameters parameters;
  private String name;

  // ===============  Constructors
  private Index() {

  }

  public Index(String name, IndexParameters parameters) {
    this(parameters);
    this.name = name;
  }

  private Index(IndexParameters parameters) {
    this.parameters = parameters;
    handler = new IndexingHandler(this);
    storage = new InvertedIndex(parameters);
    // todo: redo name generating
    name = "test";
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
      // creating index with specified name + parameters
      index = new Index(indexName, indexParameters);

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
                "File-Index " + name + " was loaded, but streams were not closed. Problem acquired:" + e.getMessage());
          }
        }
      }).start();
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

      // write objects in order - name, parameters, storage
      out.writeObject(name);
      out.writeObject(parameters);
      out.writeObject(storage);

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

  public void index(IndexingRequest request) {
    log.info("Indexing with request \"" + request.getId().toString() + "\" started");
    handler.index(request);
  }

  public void search(SearchRequest request) {
    log.info("Searching with request \"" + request.getSearchFor() + "\" started");
    storage.search(request);
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
}
