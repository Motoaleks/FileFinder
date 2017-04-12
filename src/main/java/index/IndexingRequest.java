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
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;


/**
 * Created by: Aleksandr
 * Date: 03.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexingRequest extends Task<Long> {

  private FutureTask<Long> fileCount;
  private AtomicLong indexed = new AtomicLong(0);

  private UUID id;
  private List<Path> pathsToIndex;
  private Index index;

  private IndexingRequest() {
    pathsToIndex = new LinkedList<>();
    id = UUID.randomUUID();
  }

  @Override
  protected Long call() throws Exception {
    updateMessage("Counting files");
    // counting files
    fileCount = countFiles(pathsToIndex);
    new Thread(() -> {
      fileCount.run();
      updateMessage("Indexing files");
    }).start();
    long indexed = index.index(this);
    return indexed;
  }


  public static Builder getBuilder() {
    return new IndexingRequest().new Builder();
  }

//  public void execute() {
//    new Thread(this::run).start();
//  }
//
//  public void run() {
//    setState(State.RUNNING);
//    // count total number of files
//    new Thread(this::countFileCount).start();
//    index.index(this);
//    setState(State.COMPLETED);
//  }

  private FutureTask<Long> countFiles(Collection<Path> paths) {
    FutureTask<Long> task = new FutureTask<Long>(() -> {
      long sum = 0;
      for (Path current : paths) {
        sum += count(current);
      }
      return sum;
    });
    return task;
  }

  private long count(Path path) throws IOException {
    // if method call is invalid
    if (path == null) {
      return 0;
    }
    // if path is single file
    if (!path.toFile().isDirectory()) {
      return 1;
    }
    long sum = 0;
    for (Path iterPath : Files.list(path).collect(Collectors.toList())) {
      if (iterPath.toFile().isDirectory()) {
        sum += count(iterPath);
      } else {
        sum += 1;
      }
    }
    return sum;
  }

  public Index getIndex() {
    return index;
  }

  public List<Path> getPaths() {
    return pathsToIndex;
  }

  public UUID getId() {
    return id;
  }

  public void incrementFileCounter(int indexed) throws ExecutionException, InterruptedException {
    long currentlyIndexed = this.indexed.addAndGet(indexed);
    if (fileCount.isDone()) {
      updateProgress(currentlyIndexed, fileCount.get());
    }
  }

  public void setStatus(String message) {
    updateMessage(message);
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
    }

    public Builder setIndex(Index targetIndex) {
      IndexingRequest.this.index = targetIndex;
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
      if (index != null
          && pathsToIndex != null
          && pathsToIndex.size() > 0) {
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