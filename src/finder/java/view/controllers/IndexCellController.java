/*
 * Created by Aleksandr Smilyanskiy
 * Date: 17.03.17 13:36
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package view.controllers;

import index.Index;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Created by: Aleksandr
 * Date: 17.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexCellController {

  private final String INDEX_CELL_FXML = "../fxml/indexCell.fxml";

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private Label lb_indexName;

  @FXML
  private Label lb_selectionStatus;

  @FXML
  private BorderPane bp_body;

  private Node graphic;
  private Index item;


  public IndexCellController() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(INDEX_CELL_FXML));
    fxmlLoader.setController(this);
    try {
      graphic = fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @FXML
  void initialize() {
    assert lb_indexName
        != null : "fx:id=\"lb_indexName\" was not injected: check your FXML file 'indexCell.fxml'.";
    assert lb_selectionStatus
        != null : "fx:id=\"lb_selectionStatus\" was not injected: check your FXML file 'indexCell.fxml'.";
    assert bp_body
        != null : "fx:id=\"bp_body\" was not injected: check your FXML file 'indexCell.fxml'.";
  }

  public void setIndex(Index item) {
    if (item == null) {
      return;
    }
    this.item = item;
    this.lb_indexName.setText(item.getName());
    setSelected(false);
  }

  public Node getGraphic() {
    return graphic;
  }

  public IndexCellController setSelected(boolean selected) {
    if (selected) {
      this.lb_selectionStatus.setText("SELECTED");
    } else {
      this.lb_selectionStatus.setText("");
    }
    return this;
  }
}
