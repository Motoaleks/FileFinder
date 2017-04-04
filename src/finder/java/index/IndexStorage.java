/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:05
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.entities.Inclusion;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Semaphore;

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

  public IndexStorage(IndexParameters parameters) {
    this.parameters = parameters;
    this.semaphore = new Semaphore(20);
  }

  public abstract void search(SearchRequest request);

  public abstract void put(String word, String filepath, int description);

  protected abstract Set<String> getKeys();

  protected abstract Set<Inclusion> get(String key);

  private void readObject(java.io.ObjectInputStream in) throws IOException {
    this.semaphore = new Semaphore(20);
  }
}
