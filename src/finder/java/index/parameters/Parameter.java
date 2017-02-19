/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 19:42
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.parameters;

/**
 * Represents all available indexing algorithms parameters. Each algorithm can support not all of
 * the parameters, so every algorithm has set of available parameters.
 */
public enum Parameter {
  // MOST VALUABLE --------------
  FILE_INDEX(0),
  NUMBERS(0),
  WORDS(0),
  FORMATS(2),
  LANGUAGES(2),

  // OPTIMIZATION ---------------
  LEMMATISATION(0),
  TOKENIZATION(0),
  STEMMING(0),

  //CONTINUES OPRIMIZATIONS -----
  STOPWORDS(0),
  WEIGHTING(0);

  private final int type;

  Parameter(int type) {
    this.type = type;
  }

  int getType() {
    return type;
  }
}
