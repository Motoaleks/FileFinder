/*
 * Created by Aleksandr Smilyanskiy
 * Date: 03.03.17 12:45
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.entities.Inclusion;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * Created by: Aleksandr
 * Date: 03.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class SearchRequest extends Observable {

  private String                   searchFor;
  private Index                    index;
  private ObservableSet<Inclusion> result;
  private State                    state;
  private boolean                  substringSearch;

  private SearchRequest() {
    result = FXCollections.observableSet(new HashSet<Inclusion>());
    substringSearch = false;
  }

  public static Builder getBuilder() {
    return new SearchRequest().new Builder();
  }

  public void execute() {
    new Thread(this::run).start();
  }

  public void run() {
    setState(State.RUNNING);
    index.search(this);
    setState(State.DONE);
  }

  public ObservableSet<Inclusion> getResult() {
    return result;
  }

  public void addResult(Inclusion inclusion) {
    if (!result.contains(inclusion)) {
      result.add(inclusion);
    }
  }

  public void addResult(Set<Inclusion> inclusionSet) {
    result.addAll(inclusionSet);
  }

  //================== GETTERS + SETTERS

  public Index getIndex() {
    return index;
  }

  public String getSearchFor() {
    return searchFor;
  }

  public State getState() {
    return state;
  }

  private void setState(State state) {
    this.state = state;
    notifyObservers(state);
  }

  public Boolean getSubstringSearch() {
    return substringSearch;
  }


  /**
   * SearchRequest stage state.
   */
  public enum State {
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

    State(int code) {
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
      return code >= State.PREPARED.code;
    }
  }

  /**
   * 'Builder' pattern, to correctly initialize all fields.
   */
  public class Builder {

    /**
     * Initializing with Pending state - nothing is ready.
     */
    private Builder() {
      setState(State.PENDING);
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
     * Setting index algorithm
     *
     * @param index Index to search in
     * @return this.
     */
    public Builder setIndex(Index index) {
      SearchRequest.this.index = index;
      return this;
    }

    public Builder setSubstringSearch(boolean substringSearch) {
      SearchRequest.this.substringSearch = substringSearch;
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
          && index != null) {
        setState(State.PREPARED);
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
      return SearchRequest.this;
    }
  }
}
