/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:04
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexParameters {

  private ObservableMap<Parameter, ObservableValue> storage;

  public IndexParameters(Collection<Parameter> available) {
    storage = FXCollections.observableMap(new HashMap<>());
    addAll(available);
  }

  public IndexParameters() {
    this(Arrays.asList(Parameter.values()));
  }

  public ObservableValue get(Parameter parameter) {
    return storage.get(parameter);
  }

  private void addAll(Collection<Parameter> collection) {
    for (Parameter parameter : collection) {
      storage.put(parameter, parameter.getDefaultValue());
    }
  }

  public ObservableMap<Parameter, ObservableValue> getStorage() {
    return storage;
  }
}
