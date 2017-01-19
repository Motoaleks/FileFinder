package finder.index;

import java.util.HashMap;

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

  void set(Parameter parameter, Value parameterValue) {
    if (parameter == null || parameterValue == null) {
      return;
    }

    if (parameter.getType() == parameterValue.getType() // types identical
        && index.parameterAvailable(parameter)) { // parameter available in this algorithm
      currentParameters.put(parameter, parameterValue);
    }
  }

  Value get(Parameter parameter) {
    return currentParameters.get(parameter);
  }
}
