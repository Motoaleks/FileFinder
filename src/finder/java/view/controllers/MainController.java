package view.controllers;

import index.Index;
import index.IndexParameters;
import index.IndexingRequest;
import index.SearchRequest;
import index.Storages.Node;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.views.IndexCell;
import view.views.PathCell;


public class MainController {

  final String INDICES_DIRECTORY = "indices\\";
  //  Index index;
  ObservableList<Path> paths;
  ObservableList<Index> indices;

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
  private CheckBox cb_seachSubstring;

  @FXML
  private ListView<Path> lv_files;

  @FXML
  private ListView<Index> lv_indices;

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
  void onSubstringSearchChanged(ActionEvent event) {

  }

  @FXML
  void onCreateIndex(ActionEvent event) {
//    Index temp_index = new Index(new IndexParameters());
    // initialize stage
    Stage indexCreationStage = new Stage();
    indexCreationStage.setTitle("Create index");
    // initialize controller
    IndexCreationController icc = new IndexCreationController();
    javafx.scene.Node view = icc.getView();
    indexCreationStage.setScene(new Scene((Parent) view));
    // show as a modal
    indexCreationStage.initOwner(lv_files.getScene().getWindow());
    indexCreationStage.initModality(Modality.APPLICATION_MODAL);
    indexCreationStage.showAndWait();

    // result handling
    IndexParameters parameters = icc.getCreatedParameters();
    String name = icc.getCreatedName();
    if (parameters == null || name == null) {
      return;
    }
    Index temp_index = new Index(name, parameters);

    indices.add(temp_index);
    IndexingRequest.Builder builder = IndexingRequest.getBuilder();
    IndexingRequest request = builder.setIndex(temp_index)
                                     .setPath(Paths.get("../"))
                                     .build();
    request.execute();
  }

  @FXML
  void onSearch(ActionEvent event) {
    if (indices.get(0) == null) {
      return;
    }

    SearchRequest request;
    SearchRequest.Builder builder = SearchRequest.getBuilder();
    builder.setIndex(indices.get(0))
           .setSearchFor("test")
           .setSubstringSearch(cb_seachSubstring.isSelected());
    request = builder.build();

    paths.clear();
    request.execute();
    ObservableSet<Node> set = request.getResult();
    set.addListener((SetChangeListener<? super Node>) change -> {
      if (change.wasAdded()) {
        Set<String> filenames = change.getElementAdded().getFilenames();
        List<Path> filepaths = filenames.stream().map(s -> Paths.get(s))
                                        .collect(Collectors.toList());

        Platform.runLater(() -> {
          paths.addAll(filepaths);
        });
      }
    });
//    saveIndexes();
//    Index index = Index.load(INDICES_DIRECTORY + "test");
  }

  @FXML
  void onSearchChanged(ActionEvent event) {

  }

  public void saveIndexes() {
    // check for index existing
    if (indices.size() == 0) {
      return;
    }

    File indicesDirectory = new File(INDICES_DIRECTORY);
    // check for directory existence, if not - create
    if (!indicesDirectory.exists()) {
      indicesDirectory.mkdir();
    }

    // start thread for saving indices (cause it is long operation)
    new Thread(() -> {
      indices.forEach(index -> {
        index.save(INDICES_DIRECTORY);
      });
    }).start();
  }

  public void loadIndexes() {
    File indicesDirectory = new File(INDICES_DIRECTORY);
    // check for directory existence, if not - leave method (cause it's no indices dude)
    if (!indicesDirectory.exists()) {
      return;
    }
    // start thread for loading indices (cause it is long operation)
    new Thread(() -> {
      try (Stream<Path> paths = Files.walk(indicesDirectory.toPath().toAbsolutePath())) {
        paths.forEach(filePath -> {
          // check if name contain '.ser' - serialization extension
          if (filePath.getFileName().toString().contains(".ser")) {
            indices.add(Index.load(INDICES_DIRECTORY + filePath.getFileName().toString()));
          }
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
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
    assert lv_indices
        != null : "fx:id=\"lv_indices\" was not injected: check your FXML file 'main.fxml'.";
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
    assert cb_seachSubstring
        != null : "fx:id=\"cb_seachSubstring\" was not injected: check your FXML file 'main.fxml'.";

    initializePathList();
    initializeIndexList();
  }

  private void initializePathList() {
    lv_files.setCellFactory(param -> new PathCell());
    paths = FXCollections.observableArrayList();
    lv_files.setItems(paths);
  }

  private void initializeIndexList() {
    lv_indices.setCellFactory(param -> new IndexCell());
    indices = FXCollections.observableArrayList();
    lv_indices.setItems(indices);
  }

}