/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 1:39
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;

/**
 * Represents all available indexing algorithms parameters. Each algorithm can support not all of
 * the parameters, so every algorithm has set of available parameters.
 */
public enum Parameter {
  // MOST VALUABLE --------------
  FILE_INDEX(true, "Index file content, not just name and extension."),
  NUMBERS(true, "Index numbers."),
  WORDS(true, "Index words."),
  FORMATS(new LinkedList<>(Arrays.asList("txt", "xml", "html")), "Extensions that would be indexed."),
  LANGUAGES(new LinkedList<>(Arrays.asList("rus", "eng")), "Supported file languages.");

  // OPTIMIZATION ---------------
//  LEMMATISATION(false, "test"),
//  TOKENIZATION(false, "test"),
//  STEMMING(false, "test"),

  //CONTINUES OPRIMIZATIONS -----
//  STOPWORDS(false, "test"),
//  WEIGHTING(false, "test");

  private final int type;
  private final ObservableValue defaultValue;
  private final String description;

  Parameter(Object defaultValue, String description) {
    // needed to define type of element and also initialize with correct-wrapped default value
    if (defaultValue instanceof Boolean) {
      type = 0;
      this.defaultValue = new SimpleBooleanProperty((Boolean) defaultValue);
    } else if (defaultValue instanceof List) {
      type = 2;
      this.defaultValue = new SimpleListProperty(
          FXCollections.observableList((List<String>) defaultValue));
    } else {
      type = -1;
      this.defaultValue = null;
    }
    this.description = description;
  }

  public int getType() {
    return type;
  }

  public ObservableValue getDefaultValue() {
    if (type == 0) {
      return new SimpleBooleanProperty((Boolean) defaultValue.getValue());
    }
    if (type == 2) {
      return defaultValue;
    }
    return defaultValue;
  }

  public String getDescription() {
    return description;
  }
}
