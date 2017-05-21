/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 19:40
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.parameters;

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
