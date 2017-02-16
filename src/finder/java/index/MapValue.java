/*
 * Created by Aleksandr Smilyanskiy
 * Date: 10.02.17 22:50
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

import java.util.Map;
import java.util.Set;

/**
 * A value represented by parameters with several string-to-boolean values.
 * Example of such parameter: Languages
 * "Russian" - false,
 * "English" - true,
 * "Indonesian" - false, ...
 */
public class MapValue implements Value {

  /**
   * Unique value id = 1
   */
  private final static int TYPE = 1;
  /**
   * Container for values available by this parameter.
   */
  private Map<String, Boolean> value;

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
  public MapValue(Map<String, Boolean> value) {
    this.value = value;
  }

  /**
   * Return all parameter_variants.
   * @return Key set from container (parameter_variants).
   */
  public Set<String> getKeys() {
    return value.keySet();
  }

  @Override
  public Value set(Object value) {
    this.value = (Map<String, Boolean>) value;
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
