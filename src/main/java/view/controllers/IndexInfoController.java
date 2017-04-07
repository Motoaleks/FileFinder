package view.controllers;/*
 * Created by Aleksandr Smilyanskiy
 * Date: 01.04.17 22:19
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */


import index.Index;
import index.IndexingRequest;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import view.views.PathCell;

public class IndexInfoController {

  private final Logger log = Logger.getLogger(Index.class.getName());

  private final ListView<Path> pathsListView = new ListView<>();
  private ObservableList<Path> paths;
  private ObservableList<Path> parameters;
  /**
   * -1: not initialized
   * 0: processing
   * 1: old index changes
   * 2: new index created
   * 3: no changes (except name could changed)
   */
  private int status = -1;

  private Index oldIndex;
  private Index tempIndex;

  @FXML
  private URL location;

  @FXML
  private ScrollPane sp_parameters;

  @FXML
  private TextField tf_name;

  @FXML
  private ScrollPane sp_paths;

  @FXML
  private Button btn_reindex;

  @FXML
  private Button btn_removePath;

  @FXML
  void onAddPath() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Select directory to oldIndex");
    File folder = directoryChooser.showDialog(null);
    if (folder != null) {
      paths.add(folder.toPath());
    }
  }

  @FXML
  void onRemovePath() {
    paths.remove(pathsListView.getSelectionModel().getSelectedItem());
  }

  @FXML
  void onReindex() {
    // new name change
    if (!oldIndex.getName().equals(tf_name.getText())) {
      // should delete all savings
      new File("indices\\" + oldIndex.getName() + ".ser").delete();
      // set new name
      oldIndex.setName(tf_name.getText());
    }
    // parameters not changed
    if (tempIndex.getParameters().equals(oldIndex.getParameters())) {
      // retain new paths
      Set<Path> newPaths = new HashSet<>(paths);
      Set<Path> oldPaths = oldIndex.getIndexedPaths();
      if (oldPaths.containsAll(newPaths) && newPaths.containsAll(oldPaths)) {
        // nothing changed
        status = 3;
      } else {
        Set<Path> temp = oldPaths;
        temp.removeAll(newPaths);
        if (temp.size() > 0) {
          // smth got deleted
          oldIndex.remove(temp);
        }
        temp = newPaths;
        temp.removeAll(oldPaths);
        if (temp.size() > 0) {
          // smth got added
          IndexingRequest request = IndexingRequest.getBuilder().addPaths(temp).setIndex(oldIndex).build();
          request.execute();
        }
        // some changes in old index
        status = 1;
      }
      tempIndex = null;
      // exit window
      ((Stage) btn_reindex.getScene().getWindow()).close();
      return;
    }

    // new parameters - recreate index
    Index temp = new Index(oldIndex.getName(), tempIndex.getParameters());
    IndexingRequest request = IndexingRequest.getBuilder().setIndex(temp).addPaths(paths).build();
    request.execute();

    //set status to *new index created*
    status = 2;
    oldIndex = null;
  }

  @FXML
  void initialize() {
    paths = FXCollections.observableArrayList();
    parameters = FXCollections.observableArrayList();

    pathsListView.setPlaceholder(new Label("No paths found"));
    pathsListView.maxHeightProperty().bind(sp_paths.heightProperty());

    // set remove option to be enable only if something is selected
    btn_removePath.disableProperty().bind(pathsListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
  }

  public Index getResult() {
    if (status == 1) {
      return oldIndex;
    }
    if (status == 2) {
      return tempIndex;
    }
    return null;
  }

  public int getStatus() {
    return status;
  }

  public void setIndex(Index oldIndex) {
    if (oldIndex == null) {
      return;
    }
    this.oldIndex = oldIndex;
    this.tempIndex = oldIndex.clone(); // clones parameters, name and etc. add link to storage

    // initialize path list
    paths.addAll(tempIndex.getIndexedPaths());
    pathsListView.setItems(paths);
    pathsListView.setCellFactory(param -> new PathCell());
    pathsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    sp_paths.setContent(pathsListView);

    // initialize param list
    VBox parametersInContainer = (VBox) Controllers.getParamList(tempIndex.getParameters());
    sp_parameters.widthProperty().addListener((observable, oldValue, newValue) -> {
      parametersInContainer.prefWidthProperty().setValue(newValue.intValue() - 20);
    });
    sp_parameters.setContent(parametersInContainer);

    tf_name.setText(oldIndex.getName());

    // set status to operating
    status = 0;
  }
}