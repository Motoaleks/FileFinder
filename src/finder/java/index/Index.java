/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:01
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.InvertedIndex;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class Index {

  private IndexingHandler handler;
  private IndexStorage storage;
  private IndexParameters parameters;

  public Index(IndexParameters parameters) {
    this.parameters = parameters;
    handler = new IndexingHandler(this);
    storage = new InvertedIndex(parameters);
  }

  public void index(IndexingRequest request) {
    handler.index(request);
  }

  public void search(SearchRequest request) {
    storage.search(request);
  }

  public IndexStorage getStorage() {
    return storage;
  }

  public IndexParameters getParameters() {
    return parameters;
  }
}
