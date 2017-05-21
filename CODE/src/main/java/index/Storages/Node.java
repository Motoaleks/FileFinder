/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 14:30
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by: Aleksandr
 * Date: 04.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public interface Node extends Serializable {

  void add(String filepath, int description);

  Set<String> files();

  Map<String, Set<Integer>> filesToPos();

}
