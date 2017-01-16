/*
 * Created by: Aleksandr
 * Date: 12.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */

package finder.search;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Class to perform actions while walking the system file tree.
 * This class perform pattern search and saving results.
 */
public class Finder extends SimpleFileVisitor<Path> {

  /**
   * The pattern which must match the files.
   */
  private final PathMatcher matcher;
  /**
   * {@link Result} reference, where found paths are saved to. Dynamically updating thought the
   * search.
   */
  private final Result<Path> currentResult;


  /**
   * Creates finder with concrete pattern and result, where found files saves to.
   *
   * @param pattern The pattern which must match the files
   * @param result {@link Result} reference, to save found paths.
   */
  Finder(String pattern, Result<Path> result) {
    // getting filesystem matcher
    matcher = FileSystems.getDefault()
                         .getPathMatcher("glob:" + pattern);
    currentResult = result;
  }

  /**
   * Perform match file-to-pattern operation.
   *
   * @param file {@link Path} to current file.
   */
  private void find(Path file) {
    Path name = file.getFileName();
    if (name != null && matcher.matches(name)) {
      currentResult.addResult(file);
    }
  }

  /**
   * Invoked on file visit, perform file to pattern compare.
   */
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    find(file);
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
    find(dir);
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
