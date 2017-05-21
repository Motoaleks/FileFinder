/*
 * Created by Aleksandr Smilyanskiy
 * Date: 03.03.17 12:45
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import index.Storages.Inclusion;
import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;

/**
 * Created by: Aleksandr
 * Date: 03.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class SearchRequest extends Task<Long> {

  private String searchFor;
  private Index index;
  private ObservableSet<Inclusion> result;
  private boolean substringSearch;

  private SearchRequest() {
    result = FXCollections.observableSet(new HashSet<Inclusion>());
    substringSearch = false;
  }

  @Override
  protected Long call() throws Exception {
    updateMessage("Searching files");
    long found = index.search(this);
    updateProgress(1, 1);
    return found;
  }

  public static Builder getBuilder() {
    return new SearchRequest().new Builder();
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


  public Boolean getSubstringSearch() {
    return substringSearch;
  }

  @Override
  public String toString() {
    return searchFor;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    SearchRequest other = (SearchRequest) obj;
    if (other.getIndex() != index) {
      return false;
    }
    if (other.getSearchFor() == null || !other.getSearchFor().equals(getSearchFor())) {
      return false;
    }
    return true;
  }

  /**
   * 'Builder' pattern, to correctly initialize all fields.
   */
  public class Builder {

    /**
     * Initializing with Pending state - nothing is ready.
     */
    private Builder() {

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
