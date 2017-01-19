package finder.index;

import finder.indexing.IndexContainer;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by: Aleksandr
 * Date: 17.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class Indexer extends SimpleFileVisitor<Path> {

  private final Index indexContainer;

  public Indexer(Index indexContainer) {
    this.indexContainer = indexContainer;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    indexContainer.index(dir);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    indexContainer.index(file);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    if (!(exc instanceof AccessDeniedException)) {
      exc.printStackTrace();
    }
    return FileVisitResult.CONTINUE;
  }
}
