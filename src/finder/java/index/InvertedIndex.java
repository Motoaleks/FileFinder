/*
 * Created by Aleksandr Smilyanskiy
 * Date: 24.01.17 22:43
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import search.Result;
import search.SearchRequest;

/**
 * Performs inverted index infrastructure to cache files in it, and after that - search.
 */
public class InvertedIndex extends Index {

  /**
   * Baseline container for cached files.
   * Stores like: "test" -> <"C:/tmp/test.txt", "C:/test", ...>
   */
  private final HashMap<String, Set<String>> container;
  /**
   * Symbols to be ignored and deleted in requests.
   */
  private String symbols = "[!@#$%^&*()_+1234567890-=|/.,<>]";

  /**
   * Creates index and initializes index container.
   */
  public InvertedIndex() {
    super();
    container = new HashMap<>();
  }

  @Override
  protected void indexPath(Path path) {
    if (path.getFileName() != null) {
      indexRow(path.getFileName().toString(), path.normalize().toString());
    }
  }

  /**
   * Add to index string-row from specified path.
   *
   * @param row String row to add to index.
   * @param path Coming from specified path.
   */
  private void indexRow(String row, String path) {
    for (String word : row.split(" ")) {
      // todo: stripping the string here, biatch
      addToIndex(word, path);
    }
  }

  /**
   * Add a word to index from specified path.
   *
   * @param string A word to add to index.
   * @param path Path, where word contains.
   */
  private void addToIndex(String string, String path) {
    string = string.toLowerCase();
    Set<String> paths = container.computeIfAbsent(string, k -> new TreeSet<String>());
    paths.add(path);
  }

  @Override
  protected void initializeAvailableParameters() {
    // Setting available parameters
    super.availableParameters.addAll(Arrays.asList(Parameter.WORDS,
                                                   Parameter.FORMATS));
  }

  /**
   * Search in container for indexed whole row of request.
   * + Very fast.
   * - If string-request is not totally equals to what contains in
   * container - results will be not found.
   *
   * @param searchRequest Request, containing string searching for.
   */
  private void searchConcrete(SearchRequest searchRequest) {
    if (!container.containsKey(searchRequest.getSearchFor().toLowerCase())) {
      return;
    }
    Result<Path> result = searchRequest.getResult();
    for (String path : container.get(searchRequest.getSearchFor().toLowerCase())) {
      result.addResult(Paths.get(path));
    }
  }

  /**
   * If a string searching for is a pattern, than looking over all container, trying to find any
   * similar to this strings.
   *
   * @param searchRequest Request, containing string searching for
   */
  private void searchEquality(SearchRequest searchRequest) {
    Result<Path> result = searchRequest.getResult();
    for (Entry<String, Set<String>> entry : container.entrySet()) {
      if (entry.getKey().matches(searchRequest.getSearchFor().toLowerCase())) {
        for (String path : container.get(searchRequest.getSearchFor().toLowerCase())) {
          result.addResult(Paths.get(path));
        }
      }
    }
  }

  private void searchSimple(SearchRequest searchRequest) {
    //todo: Call for simple searcher - looking over all files in specified path.
  }


  @Override
  public void search(SearchRequest searchRequest) {
    searchConcrete(searchRequest); // fast search in index by straight searchRequest
    searchEquality(
        searchRequest); // mediocre search for all keys and find ones, matching the pattern
    searchSimple(searchRequest); // very slow search by straight looking for files
  }
}
