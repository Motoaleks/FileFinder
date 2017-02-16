/*
 * Created by Aleksandr Smilyanskiy
 * Date: 10.02.17 22:50
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index;

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
