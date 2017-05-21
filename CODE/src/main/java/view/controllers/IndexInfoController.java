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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

  private Index oldIndex;
  private Index tempIndex;

  private Index resultIndex = null;
  private IndexingRequest resultRequest = null;
  private boolean validated = false;

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
  private MainController mainStage;

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
    validate();
    ((Stage) (btn_reindex.getScene().getWindow())).close();
  }

  private void validate() {
    // check name change
    String text = tf_name.getText();
    if (!oldIndex.getName().equals(tf_name.getText()) && !"".equals(text)) {
      oldIndex.changeName(tf_name.getText());
    }

    // changes in indexing paths
    if (tempIndex.getParameters().equals(oldIndex.getParameters())) {
      // retain new paths
      Set<Path> newPaths = new HashSet<>(paths);
      Set<Path> oldPaths = oldIndex.getIndexedPaths();

      // check if there is any changes in paths
      if (!oldPaths.containsAll(newPaths) || !newPaths.containsAll(oldPaths)) {
        // some changes

        Set<Path> temp = new HashSet<>(oldPaths);
        temp.removeAll(newPaths);
        // check if new paths contains not all old paths
        if (temp.size() > 0) {
          // smth got deleted
          oldIndex.remove(temp); // remove from index
        }

        temp = new HashSet<>(newPaths);
        temp.removeAll(oldPaths);
        // check if old paths contains not all new paths
        if (temp.size() > 0) {
          // smth got added
          resultRequest = IndexingRequest.getBuilder().addPaths(temp).setIndex(oldIndex).build(); // prepare request
        }
      }
    } else {
      // core changes - needs index recreating
      resultIndex = new Index(oldIndex.getName(), tempIndex.getParameters());
    }

    // clear all border references
    tempIndex = null;
    oldIndex = null;
    validated = true;
  }

  @FXML
  void initialize() {
    paths = FXCollections.observableArrayList();

    pathsListView.setPlaceholder(new Label("No paths found"));
    pathsListView.maxHeightProperty().bind(sp_paths.heightProperty());

    // set remove option to be enable only if something is selected
    btn_removePath.disableProperty().bind(pathsListView.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
  }

  public IndexingRequest toRequest() {
    if (!validated) {
      validate();
    }
    return resultRequest;
  }

  public Index toIndex() {
    if (!validated) {
      validate();
    }
    return resultIndex;
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
  }

  public void setMainStage(MainController mainStage) {
    this.mainStage = mainStage;
  }
}