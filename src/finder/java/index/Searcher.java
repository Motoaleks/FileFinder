package index;

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
