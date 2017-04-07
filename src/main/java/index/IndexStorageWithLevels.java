/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 14:58
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by: Aleksandr
 * Date: 04.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public abstract class IndexStorageWithLevels extends IndexStorage {

  private static transient Logger log;

  public IndexStorageWithLevels(IndexParameters parameters) {
    super(parameters);
    if (log == null) {
      log = Logger.getLogger(FileVisitorIndexer.class.getName());
    }
  }

  @Override
  public void search(SearchRequest request) {
    int acquired = 0;
    try {
      semaphore.acquire();
      acquired++;
      searchConcrete(request);
      if (request.getSubstringSearch()) {
        searchSimilar(request);
      }
      semaphore.acquire();
      acquired++;
      searchStraightInFiles(request);
    } catch (InterruptedException e) {
      log.log(Level.SEVERE, "Search interrupted: {}", request.getSearchFor());
    } finally {
      semaphore.release(acquired);
      log.info("Searching with request \"" + request.getSearchFor() + "\" completed");
    }
  }

  protected void searchSimilar(SearchRequest request) {
    Set<String> keys = getKeys();
    String searchFor = request.getSearchFor();
    for (String key : keys) {
      // if request is a part of some key - return node for this key
      if (key.contains(searchFor)) {
        request.addResult(get(key));
      }
    }
  }

  protected void searchStraightInFiles(SearchRequest request) {
    // todo: search in all indexed files
    log.log(Level.FINE, "Search in files.");
  }

  protected abstract void searchConcrete(SearchRequest request);

  private void readObject(java.io.ObjectInputStream in) throws IOException {
    if (log == null) {
      log = Logger.getLogger(FileVisitorIndexer.class.getName());
    }
  }
}
