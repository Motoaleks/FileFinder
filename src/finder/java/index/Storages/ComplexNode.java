/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 14:35
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by: Aleksandr
 * Date: 04.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class ComplexNode implements Node {

  private final LinkedList<NodeRow> rows;

  private ComplexNode() {
    rows = new LinkedList<>();
  }

  ComplexNode(String filepath, int description) {
    this();
    add(filepath, description);
  }

  @Override
  public void add(String filepath, int description) {
    Optional<NodeRow> found;
    synchronized (rows) {
      found = rows.stream().filter(nodeRow -> nodeRow.filepath.equals(filepath)).findFirst();

      if (found.isPresent()) {
        found.get().add(description);
      } else {
        rows.add(new NodeRow(filepath, description));
      }
    }
  }

  @Override
  public Set<String> files() {
    synchronized (rows) {
      return rows.stream().map(nodeRow -> nodeRow.filepath).collect(Collectors.toSet());
    }
  }

  @Override
  public Map<String, Set<Integer>> filesToPos() {
    synchronized (rows) {
      Map<String, Set<Integer>> result = new HashMap<>();
      rows.stream().forEach(nodeRow -> {
        result.put(nodeRow.filepath, nodeRow.rows);
      });
      return result;
    }
  }

}

class NodeRow implements Serializable {

  String           filepath;
  HashSet<Integer> rows;

  private NodeRow() {
    rows = new HashSet<>();
  }

  NodeRow(String filepath, int row) {
    this();
    this.filepath = filepath;
    rows.add(row);
  }

  protected void add(int row) {
    rows.add(row);
  }
}
