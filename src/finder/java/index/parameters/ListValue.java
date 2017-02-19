/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 19:40
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by: Aleksandr
 * Date: 19.02.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class ListValue implements Value {

  /**
   * Unique value id = 2
   */
  private final static int TYPE = 2;

  /**
   * Container for values available by this parameter.
   */
  private List<String> value;

  public ListValue() {
    value = new ArrayList<>();
  }

  /**
   * Creates MapValue from map containing parameter_variant-boolean entries.
   * Example of such parameter: Languages
   * Parameter_variant - boolean
   * "Russian" - false,
   * "English" - true,
   * "Indonesian" - false, ...
   *
   * @param value Map containing parameter_variant-boolean entries
   */
  public ListValue(List<String> value) {
    this.value = value;
  }

  public void parseAndAdd(String tokens) {
    Collections.addAll(value, tokens.split(" "));
  }


  @Override
  public Value set(Object value) {
    this.value = (List<String>) value;
    return this;
  }

  @Override
  public Object get() {
    return value;
  }

  @Override
  public int getType() {
    return TYPE;
  }
}
