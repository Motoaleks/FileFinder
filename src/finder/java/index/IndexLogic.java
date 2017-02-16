/*
 * Created by Aleksandr Smilyanskiy
 * Date: 24.01.17 1:03
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;

/**
 * Contains Indexing algorithm logic.
 */
public abstract class IndexLogic implements Searcher {


  protected LinkedList<IndexRequest> taskQueue;

  private Thread currentWorker;

  protected IndexLogic() {
    taskQueue = new LinkedList<>();
  }

  private synchronized void queueChanged() {
    if (currentWorker == null) {
      // start worker
      currentWorker = new Thread(() -> {
        // do work, while have it
        while (taskQueue.size() > 0) {
          // get request
          IndexRequest ir = taskQueue.pop();
          try {
            // complete it
            Files.walkFileTree(ir.getIndexPath(), new SimpleIndexer(ir));
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        // delete itself if work is done
        synchronized (IndexLogic.this) {
          currentWorker = null;
        }
      });
    }
  }

  /**
   * Add specified directory/file to index.
   *
   * @param path Path to add.
   */
  protected abstract void indexPath(Path path);

  /**
   * Execute specified indexRequest.
   *
   * @param indexRequest Request to be executed.
   */
  public void index(IndexRequest indexRequest) {
    taskQueue.add(indexRequest);
    queueChanged();
  }
}
