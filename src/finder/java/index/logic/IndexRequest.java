/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 19:48
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.logic;

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
