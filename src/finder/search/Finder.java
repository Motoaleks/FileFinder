package finder.search;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by: Aleksandr
 * Date: 12.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
public class Finder extends SimpleFileVisitor<Path> {
    private final PathMatcher matcher;
    private final Result<Path> currentResult;

    public Finder(String pattern, Result<Path> result) {
        matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + pattern);
        currentResult = result;
    }

    private void find(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            currentResult.addResult(file);
        }
    }

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
        if (!(exc instanceof AccessDeniedException)){
            exc.printStackTrace();
        }
        return FileVisitResult.CONTINUE;
    }
}
