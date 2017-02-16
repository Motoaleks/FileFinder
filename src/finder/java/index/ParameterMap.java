/*
 * Created by Aleksandr Smilyanskiy
 * Date: 10.02.17 22:50
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Created by: Aleksandr
 * Date: 19.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class ParameterMap {

  private HashMap<Parameter, Value> currentParameters;
  private Index index;

  ParameterMap(Index index) {
    currentParameters = new HashMap<>();
    this.index = index;
  }

  public ParameterMap set(Parameter parameter, Value parameterValue) {
    if (parameter == null || parameterValue == null) {
      return this;
    }

    if (parameter.getType() == parameterValue.getType() // types identical
        && index.parameterAvailable(parameter)) { // parameter available in this algorithm
      currentParameters.put(parameter, parameterValue);
    }
    return this;
  }

  public Value get(Parameter parameter) {
    return currentParameters.get(parameter);
  }

  public Set<Entry<Parameter, Value>> getParameterSet() {
    return currentParameters.entrySet();
  }
}
