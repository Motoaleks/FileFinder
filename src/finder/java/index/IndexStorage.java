/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:05
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.entities.Inclusion;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public abstract class IndexStorage implements Serializable {

  protected transient IndexParameters parameters;
  protected transient Semaphore       semaphore;
  protected transient Set<Record>     indexRecords;

  public IndexStorage(IndexParameters parameters) {
    this.parameters = parameters;
    this.semaphore = new Semaphore(20);
    this.indexRecords = new HashSet<>();
  }

  public abstract void search(SearchRequest request);

  public abstract void put(String word, String filepath, int description);

  protected abstract Set<String> getKeys();

  protected abstract Set<Inclusion> get(String key);


  private void readObject(java.io.ObjectInputStream in) throws IOException {
    this.semaphore = new Semaphore(20);
    this.indexRecords = new HashSet<>();
  }
}

class Record implements Serializable {

  private String path;
  private Date   indexingDate;

  Record(Path path) {
    this.path = path.toAbsolutePath().toString();
    indexingDate = new Date();
  }
}
