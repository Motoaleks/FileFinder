/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 17:06
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

/*
 * Created by: Aleksandr
 * Date: 15.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
package view.views;

import java.io.IOException;
import java.nio.file.Path;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;

/**
 * Cell for representing needed path fields.
 */
public class PathCell extends ListCell<Path> {

  public static final String FXML_PATH_CELL = "/view/fxml/pathCell.fxml";
  FXMLLoader loader;
  private boolean hovered = false;
  private boolean selected = false;

  //  @FXML
//  private Label lbl_size;
  @FXML
  private Label lbl_filePath;
  @FXML
  private BorderPane bp_main;

  public PathCell() {
    selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }
      selected = newValue;
      refresh();
    });
    hoverProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }
      hovered = newValue;
      refresh();
    });
  }

  private void refresh() {
    if (getItem() == null) {
      return;
    }
    if (hovered || selected) {
      lbl_filePath.setText(getItem().toString());
    } else {
      Path filename = getItem().getFileName();
      String name = null;
      if (filename == null) {
        name = getItem().getFileName().getParent().getFileName().toString();
      } else {
        name = filename.toString();
      }
      lbl_filePath.setText(name);
    }
  }

  @Override
  protected void updateItem(Path item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
    } else {
      if (loader == null) {
        loader = new FXMLLoader(getClass().getResource(FXML_PATH_CELL));
        loader.setController(this);
        try {
          loader.load();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      setItem(item);
      refresh();
      setGraphic(bp_main);
    }
  }
}
