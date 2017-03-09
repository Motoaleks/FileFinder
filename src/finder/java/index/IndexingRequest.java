/*
 * Created by Aleksandr Smilyanskiy
 * Date: 03.03.17 9:09
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.nio.file.Path;
import java.util.Observable;

/**
 * Created by: Aleksandr
 * Date: 03.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexingRequest extends Observable {

  private Path indexingPath;
  private State state;
  private Index targetIndex;

  private IndexingRequest() {
  }


  public static Builder getBuilder() {
    return new IndexingRequest().new Builder();
  }

  public void execute() {
    new Thread(() -> {
      setState(State.RUNNING);
      targetIndex.index(this);
      setState(State.COMPLETED);
    }).start();
  }

  public State getState() {
    return state;
  }

  private void setState(State state) {
    this.state = state;
    setChanged();
    notifyObservers(state);
  }

  public Index getTargetIndex() {
    return targetIndex;
  }

  public Path getIndexingPath() {
    return indexingPath.toAbsolutePath();
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
      setState(State.PENDING);
    }

    public Builder setIndex(Index targetIndex) {
      IndexingRequest.this.targetIndex = targetIndex;
      return this;
    }

    public Builder setPath(Path indexingPath) {
      IndexingRequest.this.indexingPath = indexingPath;
      return this;
    }

    public boolean checkPrepared() {
      if (targetIndex != null && indexingPath != null
          && state.code <= State.PREPARED.code
          && state.code != State.ERROR.code) {
        setState(State.PREPARED);
        return true;
      }
      return false;
    }

    public IndexingRequest build() {
      if (!checkPrepared()) {
        return null;
      }
      return IndexingRequest.this;
    }
  }
}
