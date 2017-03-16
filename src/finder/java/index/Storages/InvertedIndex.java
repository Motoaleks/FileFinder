/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 13:38
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages;

import index.IndexParameters;
import index.IndexStorageWithLevels;
import index.Parameter;
import index.SearchRequest;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import javafx.beans.value.ObservableValue;

/**
 * Created by: Aleksandr
 * Date: 04.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class InvertedIndex extends IndexStorageWithLevels {

  private final HashMap<String, Node> storage;


  public InvertedIndex(IndexParameters parameters) {
    super(parameters);
    storage = new HashMap<>();
    this.parameters = parameters;
  }

  @Override
  protected void searchConcrete(SearchRequest request) {
    synchronized (storage) {
      Node o = storage.get(request.getSearchFor());
      if (o != null) {
        request.addResult(o);
      }
    }
  }

  @Override
  public void saveToFile() {
    // todo: saving
  }

  @Override
  public void put(String word, String filepath, int description) {
    synchronized (storage) {
      Node entry = storage.get(word);
      // check if value doesn't exist - just create a new one
      if (entry == null) {
        // check for --FILE_INDEX--
        ObservableValue fileIndexing = parameters.get(Parameter.FILE_INDEX);
        if (fileIndexing != null && (Boolean) fileIndexing.getValue()) {
          entry = new ComplexNode(filepath, description);
        } else {
          entry = new SimpleNode(filepath);
        }
        storage.put(word, entry);
      }
      // else - add description to already existing one
      else {
        entry.add(filepath, description);
      }
    }
  }

  @Override
  protected Set<String> getKeys() {
    synchronized (storage) {
      return storage.keySet();
    }
  }

  @Override
  protected Node get(String key) {
    synchronized (storage) {
      return storage.get(key);
    }
  }

  @Override
  protected void searchSimilar(SearchRequest request) {
    synchronized (storage) {
      for (Entry<String, Node> entry : storage.entrySet()) {
        if (entry.getKey().contains(request.getSearchFor())) {
          request.addResult(entry.getValue());
        }
      }
    }
  }
}
