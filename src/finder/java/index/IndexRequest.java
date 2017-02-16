/*
 * Created by Aleksandr Smilyanskiy
 * Date: 24.01.17 20:13
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.nio.file.Path;
import java.util.Observable;

/**
 * Contains encapsulated parameters for index path request.
 */
public class IndexRequest extends Observable {

  private Index indexAlg;
  private Path indexPath;
  private State currentState;

  private IndexRequest(IndexRequest indexRequest) {
    this();
    this.indexAlg = indexRequest.indexAlg;
    this.indexPath = indexRequest.indexPath;
    this.currentState = indexRequest.currentState;
  }

  private IndexRequest() {

  }

  public static Builder getBuilder() {
    return new IndexRequest().new Builder();
  }

  public Path getIndexPath() {
    return indexPath;
  }

  public Index getIndexAlg() {
    return indexAlg;
  }

  //   todo: state changing
//  public void stopIndexing() {
//    if (currentState.code() < 0 || currentState.code() > 3) {
//      return;
//    }
//    currentState = State.STOPPED;
//    notifyObservers(State.STOPPED);
//  }
//
//  public void resumeIndexing() {
//    if (currentState != State.PAUSED) {
//      return;
//    }
//    currentState = State.RUNNING;
//    notifyObservers(State.RUNNING);
//  }
//
//  public void pauseIndexing() {
//    if (currentState != State.RUNNING) {
//      return;
//    }
//    currentState = State.PAUSED;
//    notifyObservers(State.PAUSED);
//  }
//
//  public void startIndexing() {
//    if (currentState != State.PREPARED) {
//      return;
//    }
//    new Thread(() -> {
//      currentState = State.RUNNING;
//      indexAlg.index(this);
//    }).start();
//  }
  public void execute() {
    indexAlg.index(this);
  }

  public State getCurrentState() {
    return currentState;
  }

  public enum State {
    ERROR(-1),
    PENDING(0),
    PREPARED(1),
    RUNNING(2),
    PAUSED(3),
    STOPPED(4),
    COMPLETED(5);

    private int code;

    State(int stateCode) {
      code = stateCode;
    }

    int code() {
      return code;
    }
  }

  public class Builder {

    private Builder() {
      IndexRequest.this.currentState = State.PENDING;
    }

    public Builder setIndex(Index indexAlg) {
      IndexRequest.this.indexAlg = indexAlg;
      return this;
    }

    public Builder setPath(Path indexPath) {
      IndexRequest.this.indexPath = indexPath;
      return this;
    }

    public boolean checkPrepared() {
      if (indexAlg != null && indexPath != null) {
        IndexRequest.this.currentState = State.PREPARED;
        return true;
      }
      return false;
    }

    public IndexRequest build() {
      if (!checkPrepared()) {
        return null;
      }
      return IndexRequest.this;
    }
  }
}
