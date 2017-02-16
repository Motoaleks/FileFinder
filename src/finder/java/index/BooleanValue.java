/*
 * Created by Aleksandr Smilyanskiy
 * Date: 16.02.17 20:17
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

/**
 * Boolean value wrapper.
 */
public class BooleanValue implements Value {

  /**
   * Static type identification, type = 0 - means boolean value.
   */
  private final static int TYPE = 0;
  /**
   * Container for value.
   */
  private boolean value;

  /**
   * Creates object, default {@link BooleanValue#value} = false.
   */
  public BooleanValue() {
    this.value = false;
  }

  /**
   * Creates object with specified boolean {@link BooleanValue#value}.
   *
   * @param value value to be set.
   */
  public BooleanValue(boolean value) {
    this.value = value;
  }

  /**
   * Sets {@link BooleanValue#value} with specified value.
   *
   * @param value value to be set.
   * @return this.
   */
  @Override
  public Value set(Object value) {
    this.value = (boolean) value;
    return this;
  }

  /**
   * Returns current value.
   *
   * @return current boolean value.
   */
  @Override
  public Object get() {
    return value;
  }

  /**
   * Returns current type.
   *
   * @return type = 0.
   */
  @Override
  public int getType() {
    return TYPE;
  }
}
