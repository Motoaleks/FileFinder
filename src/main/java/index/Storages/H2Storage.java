/*
 * Created by Aleksandr Smilyanskiy
 * Date: 27.03.17 21:58
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages;

import index.IndexParameters;
import index.IndexStorageWithLevels;
import index.SearchRequest;
import index.Storages.entities.Occurrence;
import index.Storages.entities.Path;
import index.Storages.entities.Word;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

/**
 * Created by: Aleksandr
 * Date: 27.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class H2Storage extends IndexStorageWithLevels {

  private final EntityManagerFactory managerFactory;
  public static final int BATCH_SIZE = 130;
  public static final int MAX_TRIES = 10;

  public H2Storage(IndexParameters parameters, String name) {
    super(parameters);

//    managerFactory = Persistence.createEntityManagerFactory("myDbFile.odb");
    Map<String, Object> properties = new HashMap<>();
    properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
    properties.put("javax.persistence.jdbc.url", "jdbc:h2:file:./indices/" + name + ";MVCC=true");
    properties.put("javax.persistence.jdbc.user", "sa");
    properties.put("javax.persistence.jdbc.password", "");
    properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    properties.put("hibernate.hbm2ddl.auto", "update");
    properties.put("javax.persistence.lock.timeout", 15000);
//    properties.put("hibernate.show_sql", "true");
    managerFactory = Persistence.createEntityManagerFactory("MainEntities", properties);
  }

  public void put(Set<Inclusion> inclusions, int tryNumber) {
    if (inclusions.size() <= 0) {
      return;
    }
    EntityManager manager = createEntityManager();
    try {
      manager.getTransaction().begin();
      int counter = 0;
      Path path = manager.merge(new Path(inclusions.toArray(new Inclusion[]{})[0].getPath().toString()));
      for (Inclusion inclusion : inclusions) {
//        manager.lock(path, LockModeType.PESSIMISTIC_WRITE);
        Word word = manager.merge(new Word(inclusion.getWord()));
//        manager.lock(word, LockModeType.PESSIMISTIC_WRITE);
        Occurrence occurrence = new Occurrence();
        occurrence.setWord(word);
        occurrence.setPath(path);
        occurrence.setPlace((int) inclusion.getPlace());
        manager.persist(occurrence);
        counter += 1;
        if (counter % BATCH_SIZE == 0) {
          counter = 0;
          manager.flush();
          manager.clear();
        }
      }
      if (!manager.isOpen()) {
        return;
      }
      manager.getTransaction().commit();
      manager.close();
    } catch (RollbackException e) {
      manager.getTransaction().rollback();
      if (tryNumber >= MAX_TRIES) {
        e.printStackTrace();
      } else {
        put(inclusions, ++tryNumber);
      }
    } catch (Exception e) {
//      e.printStackTrace();
    }
  }

  @Override
  public void put(String key, String filepath, int description) {
    try {
      EntityManager manager = managerFactory.createEntityManager();
      manager.getTransaction().begin();

      Path path = manager.merge(new Path(filepath));
      Word word = manager.merge(new Word(key));
      Occurrence occurrence = new Occurrence();
      occurrence.setPlace(description);
      occurrence.setWord(word);
      occurrence.setPath(path);
      manager.persist(occurrence);

      manager.getTransaction().commit();
      manager.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public long remove(Set<java.nio.file.Path> temp) {
    EntityManager manager = managerFactory.createEntityManager();
    manager.getTransaction().begin();
    long deleted = 0;
    for (java.nio.file.Path path : temp) {
      // here we can see leak in word table
      List<Path> paths = manager.createQuery("SELECT p FROM Path p WHERE p.path LIKE :path")
                                .setParameter("path", path.toString().replace("\\", "\\\\") + "%")
                                .getResultList();
      for (Path realPath : paths) {
        manager.remove(realPath);
      }
    }
    manager.getTransaction().commit();
    manager.close();
    return deleted;
  }

  public EntityManager createEntityManager() {
    if (!managerFactory.isOpen()) {
      return null;
    }
    return managerFactory.createEntityManager();
  }

  @Override
  protected Set<String> getKeys() {
    EntityManager manager = managerFactory.createEntityManager();
    List<Word> resultSet = (List<Word>) manager.createQuery("SELECT w FROM Word w").getResultList();
    manager.close();
    return resultSet.stream().map(Word::getWord).collect(Collectors.toSet());
  }

  @Override
  protected Set<Inclusion> get(String key) {
    EntityManager manager = managerFactory.createEntityManager();
    if (!manager.isOpen()) {
      return null;
    }

    List<Occurrence> resultList = (List<Occurrence>) manager
        .createQuery("SELECT o FROM Occurrence o WHERE o.word.word = :word")
        .setParameter("word", key)
        .getResultList();

    Set<Inclusion> inclusions = new HashSet<>();
    for (Occurrence entry : resultList) {
      inclusions
          .add(
              new Inclusion(key, Paths.get(entry.getPath().getPath()), entry.getPlace(), entry.getPath().getUpdated()));
    }
    manager.close();

    return inclusions;
  }

  protected void get(SearchRequest request) {
    try{
      EntityManager manager = managerFactory.createEntityManager();
      if (!manager.isOpen()) {
        return;
      }

      Query query = manager.createQuery(
          "SELECT o.word.word, o.path.path, o.place, o.path.updated  FROM Occurrence o WHERE o.word.word = :word");
      query.setParameter("word", request.getSearchFor());
      List<Object[]> found = query.getResultList();

      Set<Inclusion> compressed = found.stream().map(
          objects -> new Inclusion((String) objects[0], Paths.get((String) objects[1]), (long) objects[2],
                                   (Date) objects[3])).collect(Collectors.toSet());

      request.addResult(compressed);
    } catch (Exception e){
      e.printStackTrace();
    }

  }

  @Override
  protected void searchConcrete(SearchRequest request) {
    get(request);
//    request.addResult(get(request.getSearchFor()));
  }

  @Override
  public void exit() {
    managerFactory.close();
  }

  @Override
  public void changeName(String name) {
    super.changeName(name);

  }
}
