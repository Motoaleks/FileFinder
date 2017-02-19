/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 19:48
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.logic;

import search.SearchRequest;

/**
 * Created by: Aleksandr
 * Date: 20.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public interface Searcher {

  void search(SearchRequest searchRequest);
}
