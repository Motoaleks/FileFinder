/*
 * Created by Aleksandr Smilyanskiy
 * Date: 27.03.17 17:48
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package test;

import index.Index;
import index.IndexParameters;
import index.IndexingRequest;
import index.SearchRequest;
import index.Storages.FileVisitorIndexerDB;
import index.Storages.H2Storage;
import index.Storages.InvertedIndex;
import index.Storages.entities.Inclusion;
import index.Storages.entities.Occurrence;
import index.Storages.entities.Path;
import index.Storages.entities.Word;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.collections.ObservableSet;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by: Aleksandr
 * Date: 27.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class PJA_test {

  @BeforeClass
  public static void disableLogging() {
    try {
      LogManager.getLogManager().readConfiguration(PJA_test.class.getResourceAsStream("/META-INF/logging.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void PFN_test() {
    EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("MainEntities");
    EntityManager entitymanager = emfactory.createEntityManager();
    entitymanager.getTransaction().begin();

    Path p1 = new Path("C:/lol");
    Path p2 = new Path("/catch");

    Word w1 = new Word("duhast");
    Word w2 = new Word("engel");

    Occurrence n1 = new Occurrence(w1, p1, -1);
    Occurrence n2 = new Occurrence(w2, p1, -1);

    //storing all entities
    entitymanager.persist(p1);
    entitymanager.persist(p2);

    entitymanager.persist(w1);
    entitymanager.persist(w2);

    entitymanager.persist(n1);
    entitymanager.persist(n2);

    entitymanager.getTransaction().commit();

    Query query1 = entitymanager.createQuery("select p from Path p");
    List paths = query1.getResultList();
    for (Object path : paths) {
      System.out.println(path);
    }

//    Query query2 = entitymanager.createQuery("select f from File f");
//    List files = query2.getResultList();
//    for (Object file : files) {
//      System.out.println(file);
//    }

    Query query3 = entitymanager.createQuery("select n from Occurrence n");
    List nodes = query3.getResultList();
    for (Object node : nodes) {
      System.out.println(node);
    }

    Query query4 = entitymanager.createQuery("select w from Word w");
    List words = query4.getResultList();
    for (Object word : words) {
      System.out.println(word);
    }

    entitymanager.close();
    emfactory.close();
  }

  @Test
  public void test_index_h2() {
    IndexParameters parameters = new IndexParameters();
    Index index = new Index("test", parameters, new H2Storage(parameters));
    IndexingRequest.Builder builder = IndexingRequest.getBuilder();
    IndexingRequest request = builder.setIndex(index)
                                     .addPath(Paths.get("C:/Alex/Downloads"))
                                     .build();
    request.run();

    SearchRequest.Builder search_b = SearchRequest.getBuilder();
    SearchRequest search_r = search_b.setSearchFor("test")
                                     .setSubstringSearch(false)
                                     .setIndex(index)
                                     .build();
    search_r.run();
    ObservableSet<Inclusion> result = search_r.getResult();
  }

  @Test
  public void test_speed() {
    IndexParameters parameters = new IndexParameters();
    Index h2 = new Index("test", parameters, new H2Storage(parameters));
    Index inverted = new Index("test1", parameters, new InvertedIndex(parameters));

    final String TESTING_ROUTE = "C:/Alex/Downloads";

    // TESTING SPEED
    IndexingRequest.Builder builder = IndexingRequest.getBuilder();
    IndexingRequest request_h2 = builder.setIndex(h2)
                                        .addPath(Paths.get(TESTING_ROUTE))
                                        .build();

    long startTime = System.currentTimeMillis();
    request_h2.run();
    long endTime = System.currentTimeMillis();
    System.out.println("H2 indexing route <" + TESTING_ROUTE + "> speed: " + (((endTime - startTime) / 1000) % 60));

    builder = IndexingRequest.getBuilder();
    IndexingRequest request_inverted = builder.setIndex(inverted)
                                              .addPath(Paths.get(TESTING_ROUTE))
                                              .build();
    startTime = System.currentTimeMillis();
    request_inverted.run();
    endTime = System.currentTimeMillis();
    System.out.println(
        "InvertedIndex indexing route <" + TESTING_ROUTE + "> speed: " + (((endTime - startTime) / 1000) % 60));
  }

  @Test
  public void batch_size() {
    Logger.getGlobal().setLevel(Level.OFF);

    Map<Integer, Double> timing = new HashMap<>();
    final String TESTING_ROUTE = "C:/Alex/Downloads";
    final int MED_COUNT = 3;
    double startTime = -1;
    double endTime = -1;
    double sum = -1;
    for (int i = 10; i < 400; i += 10) {
      System.out.println("Batch: " + i);
      FileVisitorIndexerDB.BATCHING_SIZE = i;

      for (int j = 0; j < MED_COUNT; j++) {
        IndexParameters parameters = new IndexParameters();
        Index h2 = new Index("test", parameters, new H2Storage(parameters));
        // TESTING SPEED
        IndexingRequest.Builder builder = IndexingRequest.getBuilder();
        IndexingRequest request_h2 = builder.setIndex(h2)
                                            .addPath(Paths.get(TESTING_ROUTE))
                                            .build();

        startTime = System.currentTimeMillis();
        request_h2.run();
        endTime = System.currentTimeMillis();
        sum += (endTime - startTime) / 1000;
        new File("./test_database.mv.db").delete();
      }

      timing.put(i, sum / MED_COUNT);
      System.out.println("Time: " + sum / MED_COUNT);
      sum = 0;
    }
    timing.forEach((key, value) -> System.out.println(key + "\t" + value));
    Entry<Integer, Double> entry = timing.entrySet().stream().min(Comparator.comparing(Entry::getValue)).get();
    System.out.println("=================================================\n"
                       + "The best one: " + entry.getKey() + ": " + entry.getValue());
  }

  @Test
  public void iamretard() {
    for (int i = 0; i < 3; i++) {
      System.out.println(i);
    }
  }

}