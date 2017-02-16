/*
 * Created by: Aleksandr
 * Date: 11.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
package search;

import index.Searcher;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;

/**
 * Encapsulated file search request, containing: - {@link Path} where to search - {@link String} for
 * what search for - {@link Result} instance reference containing intermediate or final path
 * collection. It is also shared with view and all search algorithms. - {@link SearchRequest.Status}
 * with current search stage - search options (like "search in files enabled") <p> This class is
 * initialized by 'builder' pattern, so for initializing use {@link SearchRequest#getBuilder()}, and
 * after all settings are complete {@link Builder#build()}. Example:
 * <pre>
 *  {@code
 *  Builder builder = SearchRequest.getBuilder();
 *  SearchRequest request = builder.setSearchFor(...).setSearchIn(...).build();
 *  }
 *  </pre>
 * <p> {@link SearchRequest#execute(Result)} creates and run new thread for search. <p>
 * SearchRequest extends {@link Observable}, register now if you want to know when new value is
 * added!
 */
public class SearchRequest
    extends Observable
    implements Cloneable {

  /**
   * {@link Path} to search in.
   */
  private Path searchIn;
  /**
   * Pattern searching for.
   */
  private String searchFor;
  /**
   * Result for adding information.
   */
  private Result<Path> result = new Result<>();
  /**
   * Search IN files or not.
   */
  private Boolean searchInFiles = false;
  /**
   * Current request stage.
   */
  private Status currentSearchStatus;
  /**
   * Index to search in.
   */
  private Searcher searcher;

  private SearchRequest() {
    super();
  }

  /**
   * Returns 'Builder' pattern builder to build ('oh my gosh') the SearchRequest. Builder takes care
   * that all fields are correctly initialized.
   *
   * @return Builder instance for current instance of request.
   */
  public static Builder getBuilder() {
    return new SearchRequest().new Builder();
  }

  /**
   * Begins execution of the request. Creates and start a new Thread to work for search.
   *
   * @param previous previous result.
   * @return current instance result, that would be updating during the search.
   */
  public Result<Path> execute(Result previous) {
    new Thread(this::search).start();
    return result;
  }

  /**
   * Perform search for files action. Updates status of request (Working, Error, Done).
   */
  private void search() {
    currentSearchStatus = Status.RUNNING;
    searcher.search(this);
    if (currentSearchStatus != Status.ERROR) {
      currentSearchStatus = Status.DONE;
    }
  }

  /**
   * @return Pattern searching for.
   */
  public String getSearchFor() {
    return searchFor;
  }

  /**
   * @return Current request results.
   */
  public Result<Path> getResult() {
    return result;
  }

  /**
   * @return Search IN files or not.
   */
  public Boolean getSearchInFiles() {
    return searchInFiles;
  }

  /**
   * @return Path where search should be performed.
   */
  public Path getSearchIn() {
    return searchIn;
  }

  /**
   * Clone current request instance to a new one.
   * Result are copied by reference!
   *
   * @return Cloned object.
   */
  public SearchRequest clone() throws CloneNotSupportedException {
    super.clone();
    SearchRequest searchRequest = new SearchRequest();
    searchRequest.searchIn = searchIn;
    searchRequest.searchFor = searchFor;
    searchRequest.result = result;
    searchRequest.searchInFiles = searchInFiles;
    searchRequest.searcher = searcher;
    return searchRequest;
  }

  /**
   * SearchRequest stage status.
   */
  public enum Status {
    /**
     * Error occurred.
     */
    ERROR(-1),
    /**
     * Created, but not finally initialized.
     */
    PENDING(0),
    /**
     * Prepared, all fields correctly initialized, but not started.
     */
    PREPARED(1),
    /**
     * Search working.
     */
    RUNNING(2),
    /**
     * Search done.
     */
    DONE(3);

    /**
     * Stage code.
     */
    private int code;

    Status(int code) {
      this.code = code;
    }

    public int code() {
      return code;
    }

    /**
     * Indicates if all fields are correctly initialized.
     *
     * @return True if stage is above or equals PREPARED.
     */
    public boolean prepared() {
      return code >= Status.PREPARED.code;
    }
  }

  /**
   * 'Builder' pattern, to correctly initialize all fields.
   */
  public class Builder {

    /**
     * Initializing with Pending status - nothing is ready.
     */
    private Builder() {
      SearchRequest.this.currentSearchStatus = Status.PENDING;
    }

    /**
     * Setting search for pattern.
     *
     * @param searchFor Search for pattern.
     * @return this.
     */
    public Builder setSearchFor(String searchFor) {
      SearchRequest.this.searchFor = searchFor;
      return this;
    }

    /**
     * Setting search IN files indicator.
     *
     * @param searchInFiles Search IN indicator.
     * @return this.
     */
    public Builder setSearchInFiles(boolean searchInFiles) {
      SearchRequest.this.searchInFiles = searchInFiles;
      return this;
    }

    /**
     * Setting search in path.
     *
     * @param searchIn Search in string - path.
     * @return this.
     */
    public Builder setSearchIn(String searchIn) {
      SearchRequest.this.searchIn = Paths.get(searchIn);
      return this;
    }

    /**
     * Setting index algorithm
     *
     * @param index Index to search in
     * @return this.
     */
    public Builder setSearcher(Searcher index) {
      SearchRequest.this.searcher = index;
      return this;
    }

    /**
     * Indicates if all fields are correctly initialized.
     *
     * @return Initialized correctly or not.
     */
    public boolean checkPrepared() {
      if (searchFor != null
          && !"".equals(searchFor)
          && searchIn != null
          && !"".equals(searchIn.normalize().toString())
          && searcher != null) {
        SearchRequest.this.currentSearchStatus = Status.PREPARED;
        return true;
      }
      return false;
    }

    /**
     * Creates SearchRequest instance with initialized fields.
     *
     * @return Null if something went wrong, else - initialized instance.
     */
    public SearchRequest build() {
      if (!checkPrepared()) {
        return null;
      }
      try {
        return SearchRequest.this.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      return null;
    }
  }
}
