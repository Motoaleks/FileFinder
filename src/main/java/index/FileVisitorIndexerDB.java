/*
 * Created by Aleksandr Smilyanskiy
 * Date: 10.04.17 0:44
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.H2Storage;
import index.Storages.Inclusion;
import index.Storages.entities.Occurrence;
import index.Storages.entities.Word;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * Created by: Aleksandr
 * Date: 29.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class FileVisitorIndexerDB extends FileVisitorIndexer {

  // 130 is a good size for batch based on testing value [10,400] with interval 10.
  // it's performance is around the best
  public static int BATCHING_SIZE = 130;
  private H2Storage h2 = (H2Storage) storage;
  private boolean numbers =
      parameters.get(Parameter.NUMBERS) != null && (boolean) parameters.get(Parameter.NUMBERS).getValue();
  private boolean words =
      parameters.get(Parameter.WORDS).getValue() != null && (boolean) parameters.get(Parameter.WORDS).getValue();

  public FileVisitorIndexerDB(IndexingRequest request) {
    super(request);
  }

  @Override
  protected void indexFile(Path file) {

    // submit a task
    service.submit(() -> {
      try {
        // config streams
        FileReader reader = new FileReader(file.toFile());
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        tokenizer
            .lowerCaseMode(true);// set to lowercase, cause it is no reason in separating lowercase and simple words

        Set<Inclusion> inclusions = new HashSet<>();
        int token;
        while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
          if (request.isCancelled()) {
            return;
          }
          String string_token = null;
          switch (token) {
            // checking --NUMBERS--
            case StreamTokenizer.TT_NUMBER: {
              // check for --NUMBERS--
              // if it exists and is true
              if (numbers) {
                string_token = String.valueOf(tokenizer.nval);
              }
              break;
            }
            case StreamTokenizer.TT_WORD: {
              // checking --WORDS--
              // if it exists and is true
              if (words) {
                string_token = String.valueOf(tokenizer.sval);
              }
              break;
            }
          }
          if (string_token != null) {
            inclusions.add(new Inclusion(handle(string_token), file, tokenizer.lineno(), null));
            if (inclusions.size() > H2Storage.BATCH_SIZE * 3) {
              h2.put(inclusions, 0);
              inclusions = new HashSet<>();
            }
          }
        }
        h2.put(inclusions, 0);
        request.incrementFileCounter(1);
      } catch (InterruptedException e) {
        if (request.isCancelled()) {
          return;
        }
        log.log(Level.FINE, "File indexing interrupted: {0}", file.toString());
      } catch (FileNotFoundException e) {
        log.log(Level.FINE, "File not found: {0}", file.toString());
      } catch (IOException | ExecutionException e) {
        if (e.getCause() instanceof AccessDeniedException) {
          return;
        }
        log.log(Level.FINE, "File cannot be opened: {0}", file.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
