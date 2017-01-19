package finder.index;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by: Aleksandr
 * Date: 19.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public abstract class Index extends IndexLogic {

  protected ParameterMap parameters;
  protected ArrayList<Parameter> availableParameters;

  protected Index() {
    parameters = new ParameterMap(this);
    availableParameters = new ArrayList<>();
    initializeAvailableParameters();
    initializeDefaultParameters();
  }

  protected abstract void initializeAvailableParameters();

  private void initializeDefaultParameters() {
    if (parameterAvailable(Parameter.FILE_INDEX)) {
      parameters.set(Parameter.FILE_INDEX, new BooleanValue(true));
    }
    if (parameterAvailable(Parameter.NUMBERS)) {
      parameters.set(Parameter.NUMBERS, new BooleanValue(true));
    }
    if (parameterAvailable(Parameter.WORDS)) {
      parameters.set(Parameter.WORDS, new BooleanValue(true));
    }
    if (parameterAvailable(Parameter.FORMATS)) {
      HashMap<String, Boolean> temp = new HashMap<String, Boolean>() {{
        put(".txt", true);
        put(".xml", false);
        put(".html", false);
        put(".docx", false);
      }};
      parameters.set(Parameter.FORMATS, new MapValue(temp));
    }
    if (parameterAvailable(Parameter.LANGUAGES)) {
      HashMap<String, Boolean> temp = new HashMap<String, Boolean>() {{
        put("russian", false);
        put("english", true);
      }};
      parameters.set(Parameter.LANGUAGES, new MapValue(temp));
    }
    if (parameterAvailable(Parameter.LEMMATISATION)) {
      parameters.set(Parameter.LEMMATISATION, new BooleanValue(false));
    }
    if (parameterAvailable(Parameter.TOKENIZATION)) {
      parameters.set(Parameter.TOKENIZATION, new BooleanValue(false));
    }
    if (parameterAvailable(Parameter.STEMMING)) {
      parameters.set(Parameter.STEMMING, new BooleanValue(false));
    }
    if (parameterAvailable(Parameter.STOPWORDS)) {
      parameters.set(Parameter.STOPWORDS, new BooleanValue(false));
    }
    if (parameterAvailable(Parameter.WEIGHTING)) {
      parameters.set(Parameter.WEIGHTING, new BooleanValue(false));
    }
  }

  protected boolean parameterAvailable(Parameter parameter) {
    return availableParameters.contains(parameter);
  }
}
