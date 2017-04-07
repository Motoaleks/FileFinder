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
  FILE_INDEX(true),
  NUMBERS(true),
  WORDS(true),
  FORMATS(new LinkedList<>(Arrays.asList("txt", "xml", "html"))),
  LANGUAGES(new LinkedList<>(Arrays.asList("rus", "eng"))),

  // OPTIMIZATION ---------------
  LEMMATISATION(false),
  TOKENIZATION(false),
  STEMMING(false),

  //CONTINUES OPRIMIZATIONS -----
  STOPWORDS(false),
  WEIGHTING(false);

  private final int type;
  private final ObservableValue defaultValue;

  Parameter(Object defaultValue) {
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
}
