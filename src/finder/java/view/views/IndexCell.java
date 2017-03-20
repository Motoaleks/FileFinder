/*
 * Created by Aleksandr Smilyanskiy
 * Date: 17.03.17 13:35
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package view.views;

import index.Index;
import javafx.scene.control.ListCell;
import view.controllers.IndexCellController;

/**
 * Created by: Aleksandr
 * Date: 17.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexCell extends ListCell<Index> {

  private IndexCellController controller;

  public IndexCell() {

  }

  @Override
  protected void updateItem(Index item, boolean empty) {
    super.updateItem(item, empty);

    if (empty || item == null) {
      setGraphic(null);
    } else {
      controller = new IndexCellController();
      controller.setIndex(item);
      setGraphic(controller.getGraphic());
    }
  }

  @Override
  public void updateSelected(boolean selected) {
    super.updateSelected(selected);
    if (controller != null) {
      controller.setSelected(selected);
    }
  }
}
