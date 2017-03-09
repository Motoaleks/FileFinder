/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 19:48
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.logic;

import index.logic.IndexRequest.State;
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
public class FileVisitorIndexer extends SimpleFileVisitor<Path> {

  private IndexRequest indexRequest;

  FileVisitorIndexer(IndexRequest indexRequest) {
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
