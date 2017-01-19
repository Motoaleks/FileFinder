package finder.index;

import java.util.Map;
import java.util.Set;

/**
 * Created by: Aleksandr
 * Date: 20.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class MapValue implements Value {

  private final static int TYPE = 1;
  private Map<String, Boolean> value;

  public MapValue() {
  }

  public MapValue(Map<String, Boolean> value) {
    this.value = value;
  }

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
