/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:02
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.FileVisitorIndexerDB;
import index.Storages.H2Storage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexingHandler {

  protected final IndexStorage storage;
  protected final IndexParameters parameters;
  protected final List<Parameter> available;
  private final Semaphore semaphore;
  Logger log = Logger.getLogger(IndexingHandler.class.getName());

  public IndexingHandler(Index index) {
    // Available parameters dummy creation
    available = new LinkedList<>();

    // The number of indexing tasks are not limited and can grow a lot.
    // That is why cached pool will be a good idea.
    semaphore = new Semaphore(10, false);

    // Unpack storage and parameters
    this.storage = index.getStorage();
    this.parameters = index.getParameters();
  }

  public List<Parameter> getAvailableParameters() {
    return available;
  }

  public void index(IndexingRequest request) {
    try {
      semaphore.acquire();
      // start file walking
      try {
        FileVisitorIndexer visitor;
        if (storage instanceof H2Storage){
          visitor = new FileVisitorIndexerDB(request);
        } else {
          visitor = new FileVisitorIndexer(request);
        }

        for (Path pathToIndex : request.getPaths()) {
          Files.walkFileTree(pathToIndex, visitor);
        }
        visitor.waitUntilQueueEnds();
        visitor.stopCounter();
      } catch (IOException e) {
        e.printStackTrace();
        log.log(Level.SEVERE, "Indexing file tree interrupted: {}", request.getId().toString());
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      log.log(Level.SEVERE, "Indexing path interrupted: {}", request.getId().toString());
    } finally {
      semaphore.release();
      log.info("Indexing with request \"" + request.getId().toString() + "\" completed");
    }
  }
}
