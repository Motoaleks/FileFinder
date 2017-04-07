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
import index.Storages.entities.Inclusion;
import index.Storages.entities.Occurrence;
import index.Storages.entities.Path;
import index.Storages.entities.Word;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by: Aleksandr
 * Date: 27.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class H2Storage extends IndexStorageWithLevels {

  private final EntityManagerFactory managerFactory;

  public H2Storage(IndexParameters parameters, String name) {
    super(parameters);

//    managerFactory = Persistence.createEntityManagerFactory("myDbFile.odb");
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("javax.persistence.jdbc.driver", "org.h2.Driver");
    properties.put("javax.persistence.jdbc.url", "jdbc:h2:file:./indices/" + name);
    properties.put("javax.persistence.jdbc.user", "sa");
    properties.put("javax.persistence.jdbc.password", "");
    properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    properties.put("hibernate.hbm2ddl.auto", "update");
//    properties.put("hibernate.show_sql", "true");
    managerFactory = Persistence.createEntityManagerFactory("MainEntities", properties);
  }

  @Override
  public void put(String word, String filepath, int description) {
    EntityManager manager = managerFactory.createEntityManager();
    manager.getTransaction().begin();

    Path path = manager.merge(new Path(filepath));
    put(word, path, description, manager);

    manager.getTransaction().commit();
    manager.close();
  }

  public void put(String word, Path path, int description, EntityManager manager) {
    Word wordEntity = manager.merge(new Word(word));
    Occurrence occurrenceEntity = new Occurrence();
    occurrenceEntity.setPath(path);
    occurrenceEntity.setWord(wordEntity);
    occurrenceEntity.setPlace(description);
    manager.persist(occurrenceEntity);
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

    List<Occurrence> occurances = manager.createQuery("SELECT o FROM Occurrence o").getResultList();
    List<Occurrence> resultList = (List<Occurrence>) manager.createQuery("SELECT o FROM Occurrence o WHERE o.word.word = :word")
                                                            .setParameter("word", key)
                                                            .getResultList();

    Set<Inclusion> inclusions = new HashSet<>();
    for (Occurrence entry : resultList) {
      inclusions
          .add(new Inclusion(Paths.get(entry.getPath().getPath()), entry.getPlace(), entry.getPath().getUpdated()));
    }
    manager.close();

    return inclusions;
  }

  @Override
  protected void searchConcrete(SearchRequest request) {
    request.addResult(get(request.getSearchFor()));
  }

  @Override
  public void exit() {
    managerFactory.close();
  }
}
