/*
 * Created by Aleksandr Smilyanskiy
 * Date: 10.04.17 0:44
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.FileVisitorIndexer;
import index.IndexingRequest;
import index.Parameter;
import index.Storages.H2Storage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

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

  private boolean numbers =
      parameters.get(Parameter.NUMBERS) != null && (boolean) parameters.get(Parameter.NUMBERS).getValue();
  private boolean words =
      parameters.get(Parameter.WORDS).getValue() != null && (boolean) parameters.get(Parameter.WORDS).getValue();

  public FileVisitorIndexerDB(IndexingRequest request) {
    super(request);
  }

  @Override
  protected void indexFile(Path file) {
    service.submit(() -> {
      H2Storage storage = (H2Storage) this.storage;
      try {
        String filepath = file.toAbsolutePath().toString();
        // acquire semaphore for file index operation

        FileReader reader = new FileReader(file.toFile());
        StreamTokenizer tokenizer = new StreamTokenizer(reader);
        // set to lowercase, cause it is no reason in separating lowercase and simple words
        tokenizer.lowerCaseMode(true);

        EntityManager manager = storage.createEntityManager();
        if (manager == null) {
          return;
        }
        EntityTransaction transaction = manager.getTransaction();
        index.Storages.entities.Path path = new index.Storages.entities.Path(filepath);
        transaction.begin();
        manager.persist(path);

        int token = -1;
        int counter = 0;
        while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
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
            storage.put(handle(string_token), path, tokenizer.lineno(), manager);
          }
          if (counter % BATCHING_SIZE == 0) {
            counter = 0;
            if (!manager.isOpen()) {
              return;
            }
            manager.flush();
            manager.clear();
          }
        }

        transaction.commit();
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
}
