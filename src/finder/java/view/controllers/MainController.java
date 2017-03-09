package view.controllers;

import index.Index;
import index.IndexParameters;
import index.IndexingRequest;
import index.SearchRequest;
import index.Storages.Node;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import view.views.PathCell;


public class MainController {

  Index index;
  ObservableList<Path> paths;

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private Button btn_Search;

  @FXML
  private Button btn_createIndex;

  @FXML
  private Button btn_showIndex;

  @FXML
  private Label lb_status;

  @FXML
  private ListView<Path> lv_files;

  @FXML
  private ListView<?> lv_indexes;

  @FXML
  private ProgressBar pb_progress;

  @FXML
  private TextArea ta_preview;

  @FXML
  private Tab tab_indexes;

  @FXML
  private Tab tab_search;

  @FXML
  private TextField txt_search;


  @FXML
  void onCreateIndex(ActionEvent event) {
    index = new Index(new IndexParameters());

    IndexingRequest request;
    IndexingRequest.Builder builder = IndexingRequest.getBuilder();
    builder.setIndex(index)
           .setPath(Paths.get("../"));
    request = builder.build();
    request.execute();
  }

  @FXML
  void onSearch(ActionEvent event) {
    if (index == null) {
      return;
    }

    SearchRequest request;
    SearchRequest.Builder builder = SearchRequest.getBuilder();
    builder.setIndex(index)
           .setSearchFor("test");
    request = builder.build();

    request.execute();
    ObservableSet<Node> set = request.getResult();
    set.addListener((SetChangeListener<? super Node>) change -> {
      if (change.wasAdded()) {
        Set<String> filenames = ((Node) change.getElementAdded()).getFilenames();
        List<Path> filepaths = filenames.stream().map(s -> Paths.get(s))
                                        .collect(Collectors.toList());
        paths.addAll(filepaths);
      }
    });
  }

  @FXML
  void onSearchChanged(ActionEvent event) {

  }

  @FXML
  void initialize() {
    assert btn_Search
        != null : "fx:id=\"btn_Search\" was not injected: check your FXML file 'main.fxml'.";
    assert btn_createIndex
        != null : "fx:id=\"btn_createIndex\" was not injected: check your FXML file 'main.fxml'.";
    assert btn_showIndex
        != null : "fx:id=\"btn_showIndex\" was not injected: check your FXML file 'main.fxml'.";
    assert lb_status
        != null : "fx:id=\"lb_status\" was not injected: check your FXML file 'main.fxml'.";
    assert
        lv_files != null : "fx:id=\"lv_files\" was not injected: check your FXML file 'main.fxml'.";
    assert lv_indexes
        != null : "fx:id=\"lv_indexes\" was not injected: check your FXML file 'main.fxml'.";
    assert pb_progress
        != null : "fx:id=\"pb_progress\" was not injected: check your FXML file 'main.fxml'.";
    assert ta_preview
        != null : "fx:id=\"ta_preview\" was not injected: check your FXML file 'main.fxml'.";
    assert tab_indexes
        != null : "fx:id=\"tab_indexes\" was not injected: check your FXML file 'main.fxml'.";
    assert tab_search
        != null : "fx:id=\"tab_search\" was not injected: check your FXML file 'main.fxml'.";
    assert txt_search
        != null : "fx:id=\"txt_search\" was not injected: check your FXML file 'main.fxml'.";

    initializeList();
  }

  private void initializeList() {
    lv_files.setCellFactory(param -> new PathCell());
    paths = FXCollections.observableArrayList();
    lv_files.setItems(paths);
  }

}
