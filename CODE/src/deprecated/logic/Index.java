/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 19:48
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.logic;

import index.parameters.BooleanValue;
import index.parameters.ListValue;
import index.parameters.Parameter;
import index.parameters.ParameterMap;
import java.util.ArrayList;

/**
 * Represents base index class, that already have IndexLogic and parameter container. Classes
 * extending this, should implement available parameter initializer method {@link
 * Index#initializeAvailableParameters()}.
 */
public abstract class Index extends IndexLogic {

  /**
   * Parameters available for concrete implementation. Before index initialization, every index
   * algorithm should initialize available parameters - to give a note for interface about available
   * configurations.
   */
  protected final ArrayList<Parameter> availableParameters;
  /**
   * Current parameters. This changes during initializing, and after instantiating should remain
   * constant.
   */
  protected ParameterMap parameters;

  /**
   * Initializes instances for parameters, and initializing default and available parameters.
   */
  protected Index() {
    parameters = new ParameterMap(this);
    availableParameters = new ArrayList<>();
    // initialize all available parameters, concrete method depends from realisation
    initializeAvailableParameters();
    // initializes all default parameters, depends on available ones
    initializeDefaultParameters();
  }

  /**
   * Initializes available for algorithm implementation parameters (aka configs).
   * Should just add to {@link Index#availableParameters} available {@link Parameter}'s.
   */
  protected abstract void initializeAvailableParameters();

  /**
   * Initializes available parameters with default values.
   */
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
      ArrayList<String> temp = new ArrayList<String>() {{
        add(".txt");
        add(".xml");
        add(".html");
        add(".docx");
      }};
      parameters.set(Parameter.FORMATS, new ListValue(temp));
    }
    if (parameterAvailable(Parameter.LANGUAGES)) {
      ArrayList<String> temp = new ArrayList<String>() {{
        add("russian");
        add("english");
      }};
      parameters.set(Parameter.LANGUAGES, new ListValue(temp));
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

  /**
   * Checks if parameter is available in current implementation.
   *
   * @param parameter Parameter to check.
   * @return Available or unavailable for implementation.
   */
  public boolean parameterAvailable(Parameter parameter) {
    return availableParameters.contains(parameter);
  }

  /**
   * Returns cloned list of available parameters.
   */
  public ArrayList<Parameter> getAvailableParameters() {
    return (ArrayList<Parameter>) availableParameters.clone();
  }

  /**
   * Getting current parameter instance.
   *
   * @return Current parameters.
   */
  public ParameterMap getCurrentParameters() {
    return parameters;
  }
}
