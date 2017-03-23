/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 16:00
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package test;

import index.Index;
import index.IndexParameters;
import index.IndexingRequest;
import index.IndexingRequest.State;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by: Aleksandr
 * Date: 04.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexTest {

  @Before
  public void setLoggerInfo() {
    Logger logger = Logger.getAnonymousLogger();
    // LOG this level to the log
    logger.setLevel(Level.ALL);

    ConsoleHandler handler = new ConsoleHandler();
    // PUBLISH this level
    handler.setLevel(Level.ALL);
    logger.addHandler(handler);
  }

  public Index createDefault() {
    IndexParameters parameters = new IndexParameters();
    return new Index("test", parameters);
  }

  @Test
  public void createIndexWithDefaultParameters() {
    createDefault();
  }

  @Test
  public void simpleCRUD() {
    Index index = createDefault();
    IndexingRequest.Builder builder = IndexingRequest.getBuilder();
    builder.setIndex(index).addPathToIndex(Paths.get(""));
    IndexingRequest request = builder.build();

    Object ring = new Object();
    request.addObserver((o, arg) -> {
      if (arg == State.COMPLETED) {
        synchronized (ring) {
          ring.notifyAll();
        }
      }
    });
    request.execute();

    try {
      synchronized (ring) {
        ring.wait();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}