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
import java.util.HashSet;
import java.util.List;
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

  public H2Storage(IndexParameters parameters) {
    super(parameters);
    managerFactory = Persistence.createEntityManagerFactory("MainEntities");
  }

  @Override
  public void put(String word, String filepath, int description) {
    EntityManager manager = managerFactory.createEntityManager();
    manager.getTransaction().begin();

    Path path = manager.merge(new Path(filepath));
    put(word, path, description, manager);

    manager.getTransaction().commit();
  }

  public void put(String word, Path path, int description, EntityManager manager) {
    Word wordEntity = manager.merge(new Word(word));
    Occurrence occurrenceEntity = new Occurrence();
    occurrenceEntity.setPath(path);
    occurrenceEntity.setWord(wordEntity);
    occurrenceEntity.setPlace(description);
    manager.persist(occurrenceEntity);
  }

  public EntityManager createEntityManager() {
    return managerFactory.createEntityManager();
  }

  @Override
  protected Set<String> getKeys() {
    EntityManager manager = managerFactory.createEntityManager();
    List<Word> resultSet = (List<Word>) manager.createQuery("SELECT w FROM Word w").getResultList();
    return resultSet.stream().map(Word::getWord).collect(Collectors.toSet());
  }

  @Override
  protected Set<Inclusion> get(String key) {
    EntityManager manager = managerFactory.createEntityManager();

    List<Occurrence> resultList = manager.createQuery(
        "SELECT o FROM Occurrence o INNER JOIN o.word w INNER JOIN o.path p WHERE w.word = :word")
                                         .setParameter("word", key)
                                         .getResultList();

    Set<Inclusion> inclusions = new HashSet<>();
    for (Occurrence entry : resultList) {
      inclusions.add(new Inclusion(Paths.get(entry.getPath().getPath()), entry.getPlace(), entry.getPath().getUpdated()));
    }

    return inclusions;
  }

  @Override
  protected void searchConcrete(SearchRequest request) {
    request.addResult(get(request.getSearchFor()));
  }
}
