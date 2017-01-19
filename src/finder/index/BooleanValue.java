package finder.index;

/**
 * Created by: Aleksandr
 * Date: 20.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class BooleanValue implements Value {

  private final static int TYPE = 0;
  private boolean value;

  public BooleanValue() {
    this.value = false;
  }

  public BooleanValue(boolean value) {
    this.value = value;
  }

  @Override
  public Value set(Object value) {
    this.value = (boolean) value;
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
