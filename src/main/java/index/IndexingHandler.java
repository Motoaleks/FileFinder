/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:02
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.H2Storage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;


/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexingHandler {

  public static final int INDEX_REQUESTS_PERMITS = 3;
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
    semaphore = new Semaphore(INDEX_REQUESTS_PERMITS, false);

    // Unpack h2 and parameters
    this.storage = index.getStorage();
    this.parameters = index.getParameters();
  }

  public List<Parameter> getAvailableParameters() {
    return available;
  }

  public long index(IndexingRequest request) throws IOException, InterruptedException {
    try {
      semaphore.acquire();

      FileVisitorIndexer visitor;
      if (storage instanceof H2Storage) {
        visitor = new FileVisitorIndexerDB(request);
      } else {
        visitor = new FileVisitorIndexer(request);
      }
      // start file walking
      for (Path pathToIndex : request.getPaths()) {
        Files.walkFileTree(pathToIndex, visitor);
      }
      request.setStatus("Indexing file content");
      visitor.waitUntilQueueEnds();
      visitor.stopCounter();
    } finally {
      semaphore.release();
    }
    return -1;
  }
}
