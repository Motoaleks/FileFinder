/*
 * Created by Aleksandr Smilyanskiy
 * Date: 24.01.17 23:16
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.IndexRequest.State;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by: Aleksandr
 * Date: 24.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class SimpleIndexer extends SimpleFileVisitor<Path> {

  private IndexRequest indexRequest;

  SimpleIndexer(IndexRequest indexRequest) {
    this.indexRequest = indexRequest;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
      throws IOException {
    if (indexRequest.getCurrentState() == State.STOPPED) {
      return FileVisitResult.TERMINATE;
    }

    indexRequest.getIndexAlg().indexPath(path);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    if (indexRequest.getCurrentState() == State.STOPPED) {
      return FileVisitResult.TERMINATE;
    }

    indexRequest.getIndexAlg().indexPath(file);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    // unavailable file - just skip
    if (!(exc instanceof AccessDeniedException)) {
      exc.printStackTrace();
    }
    return FileVisitResult.CONTINUE;
  }
}
