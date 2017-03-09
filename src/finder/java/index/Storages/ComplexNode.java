/*
 * Created by Aleksandr Smilyanskiy
 * Date: 04.03.17 14:35
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages;

import java.util.HashSet;
import java.util.LinkedList;
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
  public void add(String filepath, int desription) {
    Optional<NodeRow> found;
    synchronized (rows) {
      found = rows.stream().filter(nodeRow -> nodeRow.filepath.equals(filepath)).findFirst();

      if (found.isPresent()) {
        found.get().add(desription);
      } else {
        rows.add(new NodeRow(filepath, desription));
      }
    }
  }

  @Override
  public Set<String> getFilenames() {
    synchronized (rows) {
      return rows.stream().map(nodeRow -> nodeRow.filepath).collect(Collectors.toSet());
    }
  }

  @Override
  public Set<Integer> getLinenums(String filepath) {
    synchronized (rows) {
      return rows.stream().filter(nodeRow -> nodeRow.filepath.equals(filepath))
                 .map(nodeRow -> nodeRow.rows).findAny().get();
    }
  }

  private class NodeRow {

    String filepath;
    Set<Integer> rows;

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
}
