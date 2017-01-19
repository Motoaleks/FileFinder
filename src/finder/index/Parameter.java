package finder.index;

/**
 * Created by: Aleksandr
 * Date: 19.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public enum Parameter {
  // MOST VALUABLE --------------
  FILE_INDEX(0),
  NUMBERS(0),
  WORDS(0),
  FORMATS(1),
  LANGUAGES(1),

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
