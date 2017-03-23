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
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class Index implements Serializable {

  private transient static Logger log;
  private transient IndexingHandler handler;
  private IndexStorage storage;
  private IndexParameters parameters;
  private String name;

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
    log = Logger.getLogger(Index.class.getName());
    // todo: redo name generating
    name = "test";
  }

  public static Index load(String name) {
    String filename = name;

    Index index = null;
    try {
      FileInputStream fileIn = null;
      ObjectInputStream in = null;
      fileIn = new FileInputStream(filename);
      in = new ObjectInputStream(fileIn);
      index = (Index) in.readObject();

      index.handler = new IndexingHandler(index);
      index.storage.parameters = index.parameters;
      if (Index.log == null) {
        log = Logger.getLogger(Index.class.getName());
      }

      fileIn.close();
      in.close();

      log.info("Index " + name + " loaded.");
    } catch (IOException e) {
      e.printStackTrace();
      log.severe("File-Index " + filename + " was not loaded, problem acquired:" + e.getMessage());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return index;
  }

  public void index(IndexingRequest request) {
    log.info("Indexing with request \"" + request.getId().toString() + "\" started");
    handler.index(request);
  }

  public void search(SearchRequest request) {
    log.info("Searching with request \"" + request.getSearchFor() + "\" started");
    storage.search(request);
  }

  public IndexStorage getStorage() {
    return storage;
  }

  public IndexParameters getParameters() {
    return parameters;
  }

  public void save(String directory) {
    try {
      String filename = directory + name + ".ser";
      FileOutputStream fileOut = new FileOutputStream(filename);
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(this);
      out.close();
      fileOut.close();
      log.info("Index " + name + " saved to " + filename);
    } catch (IOException i) {
      log.severe("Index " + name + " was not saved, problem acquired:" + i.getMessage());
    }
  }

  public String getName() {
    return name;
  }
}
