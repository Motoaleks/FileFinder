/*
 * Created by Aleksandr Smilyanskiy
 * Date: 19.02.17 17:02
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
package view.controllers;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;


/**
 * Controller for PathCell. It includes loading the cell, item, and also updating its elements on
 * the view.
 */
public class PathCellController {

  /**
   * Path to fxml view file.
   */
  private final String cellFileName = "../fxml/pathCell.fxml";
  /**
   * Thumbnail for file.
   */
  @FXML
  private ImageView iv_thumbnail;
  /**
   * Label for "created at" parameter.
   */
  @FXML
  private Label lb_created;
  /**
   * Label for "name" parameter.
   */
  @FXML
  private Label lb_name;
  /**
   * Label for "file size" parameter.
   */
  @FXML
  private Label lb_size;
  /**
   * View for the cell. Empty if was not called {@link PathCellController#getView()} yet.
   * Otherwise, if loading is complete without errors - here will lay loaded view.
   */
  private Node view;
  /**
   * The main item - Path, which cell is representing.
   */
  private Path item;

  /**
   * Indicates if cell is selected.
   */
  private boolean selected;
  /**
   * Indicates if cell is hovered.
   */
  private boolean hovered;

  /**
   * Initializes cell, without item in it. Also doesn't load view.
   */
  public PathCellController() {
    selected = false;
    hovered = false;
  }

  /**
   * Initializes cell with item in it. Also doesn't load the view.
   *
   * @param item Item - Path, the cell to hold in.
   */
  public PathCellController(Path item) {
    this();
    this.item = item;
  }

  /**
   * Asserts, that all @JAVAFX values are loaded.
   */
  @FXML
  void initialize() {
    assert iv_thumbnail
        != null : "fx:id=\"iv_thumbnail\" was not injected: check your FXML file 'pathCell.fxml'.";
    assert lb_created
        != null : "fx:id=\"lb_created\" was not injected: check your FXML file 'pathCell.fxml'.";
    assert
        lb_name != null : "fx:id=\"lb_name\" was not injected: check your FXML file 'pathCell.fxml'.";
    assert
        lb_size != null : "fx:id=\"lb_size\" was not injected: check your FXML file 'pathCell.fxml'.";
  }

  /**
   * Loads the view if it is not loaded, otherwise - returns already loaded value.
   * If error on load occurred - returns null.
   */
  public Node getView() {
    if (view == null) {
      try {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(cellFileName));
        loader.setController(this);
        view = loader.load();
        Platform.runLater(this::update);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return view;
  }

  /**
   * Sets the item to cell and updates the view.
   *
   * @param path Cell item to hold.
   * @return this.
   */
  public synchronized PathCellController setItem(Path path) {
    this.item = path;
    update();
    return this;
  }

  /**
   * Setting is cell selected or not.
   *
   * @param selected Cell selection status.
   * @return this.
   */
  public synchronized PathCellController setSelected(boolean selected) {
    this.selected = selected;
    return this;
  }

  /**
   * Setting is cell hovered or not.
   *
   * @param hovered Cell hovering status.
   * @return this.
   */
  public synchronized PathCellController setHovered(boolean hovered) {
    this.hovered = hovered;
    return this;
  }

  /**
   * Updates the view. If view was not initialized, or item was not set just skips the operations.
   */
  public void update() {
    if (item == null || view == null) {
      return;
    }
    setName();
    setCreated();
    setSize();
  }

  /**
   * Updates {@link PathCellController#lb_name} according to item path.
   * If cell is hovered or selected - will show the full path, otherwise - just filepath.
   */
  private void setName() {
    String name = null;
    if (selected || hovered) {
      name = item.normalize().toString();
    } else {
      name = item.normalize().getFileName().toString();
    }
    lb_name.setText(name);
  }

  /**
   * Updates {@link PathCellController#lb_created} according to item creating date.
   */
  private void setCreated() {
    // todo: make created at field
    return;
  }

  /**
   * Updates {@link PathCellController#lb_size} according to file/folder size.
   */
  private void setSize() {
    try {
      lb_size.setText(String.valueOf(Files.size(item)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
