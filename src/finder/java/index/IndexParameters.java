/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.03.17 22:04
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 * Created by: Aleksandr
 * Date: 02.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexParameters implements Serializable {

  private transient ObservableMap<Parameter, ObservableValue> storage;

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

  // ---------- Serialization --------------

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    HashMap<Parameter, ObservableValue> map = new HashMap<>();
    (storage.entrySet()).forEach(parameterObservableValueEntry -> {
      map.put(parameterObservableValueEntry.getKey(), parameterObservableValueEntry.getValue());
    });

    HashMap<Parameter, ParameterValueSerialization> serializationMap = new HashMap<>();
    map.forEach((parameter, observableValue) -> {
      ParameterValueSerialization value = null;
      if (parameter.getType() == 0) {
        value = new ParameterValueBoolean((Boolean) observableValue.getValue());
      } else if (parameter.getType() == 2) {
        LinkedList<String> temp = new LinkedList();
        ((ObservableList<String>) observableValue.getValue()).forEach(temp::add);
        value = new ParameterValueList(temp);
      }
      serializationMap.put(parameter, value);
    });

    out.writeObject(serializationMap);
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException {
    try {
      HashMap<Parameter, ParameterValueSerialization> serializationMap;
      serializationMap = (HashMap<Parameter, ParameterValueSerialization>) in.readObject();

      HashMap<Parameter, ObservableValue> map = new HashMap<>();
      serializationMap.forEach((parameter, parameterValueSerialization) -> {
        ObservableValue value = null;
        if (parameter.getType() == 0) {
          value = new SimpleBooleanProperty(
              ((ParameterValueBoolean) parameterValueSerialization).value);
        } else if (parameter.getType() == 2) {
          value = new SimpleListProperty(FXCollections.observableList(
              ((ParameterValueList) parameterValueSerialization).value));
        }

        map.put(parameter, value);
      });

      this.storage = FXCollections.observableMap(map);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

  private interface ParameterValueSerialization extends Serializable {

  }

  private class ParameterValueBoolean implements ParameterValueSerialization {

    boolean value;

    ParameterValueBoolean(Boolean value) {
      this.value = value;
    }
  }

  private class ParameterValueList implements ParameterValueSerialization {

    LinkedList<String> value;

    ParameterValueList(LinkedList value) {
      this.value = value;
    }
  }
}
