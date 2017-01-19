package finder.index;

/**
 * Created by: Aleksandr
 * Date: 20.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public interface Value {
  Value set(Object value);
  Object get();
  int getType();
}
