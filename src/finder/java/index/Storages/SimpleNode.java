/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 14:27
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by: Aleksandr
 * Date: 04.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class SimpleNode implements Node {

  private LinkedList<String> filepaths;

  public SimpleNode() {
    filepaths = new LinkedList<>();
  }

  public SimpleNode(String filepath) {
    this();
    filepaths.add(filepath);
  }

  @Override
  public void add(String filepath, int desription) {
    // only if found in filename
    if (desription == -1) {
      filepaths.add(filepath);
    }
  }

  @Override
  public Set<String> getFilenames() {
    return new HashSet<>(filepaths);
  }

  @Override
  public Set<Integer> getLinenums(String filepath) {
    return null;
  }
}
