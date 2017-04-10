/*
 * Created by Aleksandr Smilyanskiy
 * Date: 03.03.17 9:09
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;


/**
 * Created by: Aleksandr
 * Date: 03.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexingRequest extends Observable implements Request {

  private final SimpleLongProperty processedNumberOfFiles = new SimpleLongProperty(0);
  private final SimpleBooleanProperty filesCounted = new SimpleBooleanProperty(false);
  private final DoubleBinding progress;
  private final SimpleBooleanProperty titlesIndexed = new SimpleBooleanProperty(false);
  private final StringBinding indexStatus;
  private long totalNumberOfFiles = 0;

  private UUID id;
  private List<Path> pathsToIndex;
  private State state;
  private Index targetIndex;

  private IndexingRequest() {
    pathsToIndex = new LinkedList<>();
    id = UUID.randomUUID();
    progress = Bindings.createDoubleBinding(() -> {
      if (totalNumberOfFiles == 0 || !filesCounted.getValue()) {
        return -1.;
      } else {
        return ((double) (processedNumberOfFiles.getValue())) / totalNumberOfFiles;
      }
    }, processedNumberOfFiles);
    indexStatus = Bindings.createStringBinding(() -> {
      if (!filesCounted.getValue()) {
        return "Counting files";
      }
      if (!titlesIndexed.getValue()) {
        return "Indexing files titles";
      }
      return "Indexing file content";
    }, filesCounted, titlesIndexed);
  }


  public static Builder getBuilder() {
    return new IndexingRequest().new Builder();
  }

  public void execute() {
    new Thread(this::run).start();
  }

  public void run() {
    setState(State.RUNNING);
    // count total number of files
    new Thread(this::countTotalNumber).start();
    targetIndex.index(this);
    setState(State.COMPLETED);
  }

  public void countTotalNumber() {
    for (Path path : pathsToIndex) {
      if (path.toFile().isDirectory()) {
        try {
          count(path);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        totalNumberOfFiles += 1;
      }
    }
    Platform.runLater(() -> {
      filesCounted.set(true);
    });
  }

  private void count(Path path) throws IOException {
    for (Path iterPath : Files.list(path).collect(Collectors.toList())) {
      if (iterPath.toFile().isDirectory()) {
        count(iterPath);
      } else {
        totalNumberOfFiles += 1;
      }
    }
  }

  public void incrementFileCounter(int count) {
    Platform.runLater(() -> {
//      synchronized (processedNumberOfFiles) {
        processedNumberOfFiles.set(processedNumberOfFiles.getValue() + count);
//      }
    });
//    synchronized (processedNumberOfFiles) {
//      Platform.runLater(() -> {
//        processedNumberOfFiles.set(processedNumberOfFiles.getValue() + count);
//      });
//    }
  }

  public void setTitlesIndexed(boolean titlesIndexed) {
    Platform.runLater(() -> {
      this.titlesIndexed.set(titlesIndexed);
      this.indexStatus.invalidate();
    });
//    this.titlesIndexed.set(titlesIndexed);
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

  public List<Path> getPaths() {
    return pathsToIndex;
  }

  public UUID getId() {
    return id;
  }

  @Override
  public StringBinding statusProperty() {
    return indexStatus;
  }

  @Override
  public DoubleBinding progressProperty() {
    return progress;
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

    public Builder addPath(Path path) {
      IndexingRequest.this.pathsToIndex.add(path);
      return this;
    }

    public Builder addPaths(Collection<Path> paths) {
      IndexingRequest.this.pathsToIndex.addAll(paths);
      return this;
    }

    public boolean checkPrepared() {
      if (targetIndex != null
          && pathsToIndex != null
          && pathsToIndex.size() > 0
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
