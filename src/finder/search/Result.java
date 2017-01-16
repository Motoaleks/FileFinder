/*
 * Created by: Aleksandr
 * Date: 08.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
package finder.search;

import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Contains search results, with thread safe adding new ones and notifying observers.
 *
 * @param <T> Store parameter.
 */
public class Result<T> extends Observable {

  /**
   * Store collection - set.
   */
  private Set<T> result;

  /**
   * Initialize with TreeSet.
   */
  public Result() {
    super();
    result = new TreeSet<T>();
  }

  /**
   * Adding new search result.
   *
   * @param newResult search result to add.
   */
  public synchronized void addResult(T newResult) {
    result.add(newResult);
    setChanged();
    notifyObservers(newResult);
  }

  /**
   * Clear all results.
   */
  public synchronized void clearResults() {
    result.clear();
    setChanged();
    notifyObservers();
  }

  /**
   * Get collection.
   *
   * @return Set of results.
   */
  public Set getResult() {
    return result;
  }

  /**
   * Count results current size.
   *
   * @return Results size.
   */
  public int count() {
    return result.size();
  }
}
