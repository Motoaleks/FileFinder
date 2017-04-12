/*
 * Created by Aleksandr Smilyanskiy
 * Date: 03.03.17 15:47
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

/**
 * Created by: Aleksandr
 * Date: 24.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class FileVisitorIndexer extends SimpleFileVisitor<Path> {

  //  protected final int FILE_INDEX_PERMITS = 3;
  //  protected final Semaphore semaphore;
  protected final ExecutorService service = Executors.newFixedThreadPool(3);
//  protected final Executor executor;


  protected final String exclude = "[!@#$%^&*()_+1234567890-=|/.,<>]";
  protected ObservableList extensions;
  protected Logger log;
  protected IndexingRequest request;
  protected IndexParameters parameters;
  protected IndexStorage storage;
  // todo: delete, only in debug
  private AtomicLong counter = new AtomicLong();
  private Timer timer;

  public FileVisitorIndexer(IndexingRequest request) {
    this.request = request;
    this.parameters = request.getIndex().getParameters();
    this.storage = request.getIndex().getStorage();
    this.extensions = (ObservableList) parameters.getStorage().get(Parameter.FORMATS);
    this.log = Logger.getLogger(FileVisitorIndexer.class.getName());
//    this.executor =

    timer = new Timer();
    counter = new AtomicLong();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        System.out.println("Indexed: " + counter.getAndSet(0));
      }
    }, 0, 30000);
  }

  private static String getExtension(String filename) {
    if (filename == null) {
      return null;
    }
    int extensionPos = filename.lastIndexOf(".");
    int lastUnixPos = filename.lastIndexOf("/");
    int lastWindowsPos = filename.lastIndexOf("\\");
    int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

    int index = lastSeparator > extensionPos ? -1 : extensionPos;
    if (index == -1) {
      return "";
    } else {
      return filename.substring(index + 1);
    }
  }

  @Override
  public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
      throws IOException {
    if (request.isCancelled()) {
      return FileVisitResult.TERMINATE;
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    // terminate mechanism
    if (request.isCancelled()) {
      return FileVisitResult.TERMINATE;
    }

    // index filepath and optionally content (if property is set to true)
    try {
      indexNameAndContent(file);
    } catch (ExecutionException | InterruptedException e) {
      log.fine("Indexing file <" + file.toString() + "> failed: " + e.getMessage());
      e.printStackTrace();
    }

    return FileVisitResult.CONTINUE;
  }

  private void indexNameAndContent(Path file) throws ExecutionException, InterruptedException {
    // index filename and separately - extension
    String filename = file.getFileName().toString(); // pre-ready filename with extension
    String extension = getExtension(filename); // file extension
    filename = filename.replaceAll(extension, ""); // excluding extension from filename
    indexWord(filename, file.toAbsolutePath().toString(), -1); // index filename
    indexWord(extension, file.toAbsolutePath().toString(), -2); // index extension

    // check extension --FORMATS--
    if ("".equals(extension)) {
      request.incrementFileCounter(1);
      return;
    }

    // index file content --FILE_INDEX--
    ObservableValue fileIndexing = parameters.get(Parameter.FILE_INDEX);
    // check if parameter exists, is true and extension is acceptable
    if ((fileIndexing != null && (Boolean) fileIndexing.getValue())
        && (extensions.contains(extension) || extensions.contains("*"))) {
      indexFile(file);
    } else {
      request.incrementFileCounter(1);
    }
  }

  protected void indexFile(Path file) {
    service.submit(() -> {
      try {
        String filepath = file.toAbsolutePath().toString();
        // acquire semaphore for file index operation

        FileReader reader = new FileReader(file.toFile());
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        // set to lowercase, cause it is no reason in separating lowercase and simple words
        tokenizer.lowerCaseMode(true);

        int token = -1;
        while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
          switch (token) {
            // checking --NUMBERS--
            case StreamTokenizer.TT_NUMBER: {
              // check for --NUMBERS--
              ObservableValue numberIndexing = parameters.get(Parameter.NUMBERS);
              // if it exists and is true
              if (numberIndexing != null && (Boolean) numberIndexing.getValue()) {
                indexWord(String.valueOf(tokenizer.nval), filepath, tokenizer.lineno());
              }
              break;
            }
            case StreamTokenizer.TT_WORD: {
              // checking --WORDS--
              ObservableValue wordsIndexing = parameters.get(Parameter.WORDS);
              // if it exists and is true
              if (wordsIndexing != null && (Boolean) wordsIndexing.getValue()) {
                indexWord(tokenizer.sval, filepath, tokenizer.lineno());
              }
              break;
            }
          }
        }

        request.incrementFileCounter(1);
      } catch (InterruptedException e) {
        log.log(Level.SEVERE, "File indexing interrupted: {}", file.toAbsolutePath().toString());
      } catch (FileNotFoundException e) {
        log.log(Level.SEVERE, "File not found: {}", file.toAbsolutePath().toString());
      } catch (IOException | ExecutionException e) {
        log.log(Level.SEVERE, "File cannot be opened: {}", file.toAbsolutePath().toString());
      }
    });
  }

  protected String handle(String word) {
    counter.incrementAndGet();
    String temp = word.toLowerCase().replaceAll(exclude, "");
    if (temp.length() > 250) {
      temp = temp.substring(0, 250);
    }
    return temp;
  }

  private void indexWord(String word, String filepath, int description) {
    /*
     * description:
     * -2: File extension
     * -1: Filename
     * 0>=: Line number
     */
    word = handle(word);
    if (!"".equals(word)) {
      storage.put(word, filepath, description);
    }
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    // unavailable file - just skip
    if (!(exc instanceof AccessDeniedException)) {
      log.log(Level.SEVERE, "File visit failed: {0}\nReason: {1}",
              new Object[]{file.toAbsolutePath().toString(), exc.getMessage()});
    }
    return FileVisitResult.CONTINUE;
  }

  public void waitUntilQueueEnds() {
    try {
      service.shutdown();
      service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.severe("Error in waiting for task indexed: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public void stopCounter() {
    System.out.println("Indexed: " + counter.getAndSet(0));
    timer.cancel();
    timer.purge();
  }
}
