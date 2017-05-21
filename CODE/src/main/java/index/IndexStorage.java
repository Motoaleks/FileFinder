/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:05
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.Inclusion;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Set;

/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public abstract class IndexStorage implements Serializable {

  public static final int SEARCH_REQUESTS_PERMITS = 5;
  protected transient IndexParameters parameters;
//  protected transient ExecutorService service = Executors.newFixedThreadPool(SEARCH_REQUESTS_PERMITS);

  public IndexStorage(IndexParameters parameters) {
    this.parameters = parameters;
  }

  public abstract long search(SearchRequest request);

  public abstract void put(String word, String filepath, int description);

  protected abstract Set<String> getKeys();

  protected abstract Set<Inclusion> get(String key);

  public void exit() {
  }

  public abstract long remove(Set<Path> temp);

  public void changeName(String name) {
  }
}
