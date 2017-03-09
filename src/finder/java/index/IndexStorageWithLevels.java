/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 14:58
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

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

  private Logger log = Logger.getLogger(FileVisitorIndexer.class.getName());

  public IndexStorageWithLevels(IndexParameters parameters) {
    super(parameters);
  }

  @Override
  public void search(SearchRequest request) {
    int acquired = 0;
    try {
      semaphore.acquire();
      acquired++;
      searchConcrete(request);
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

  protected void searchStraightInFiles(SearchRequest request) {
    // todo: search in all indexed files
    log.log(Level.FINE, "Search in files.");
  }

  protected abstract void searchConcrete(SearchRequest request);
}
