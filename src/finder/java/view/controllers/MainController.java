package view.controllers;

import index.Index;
import index.IndexParameters;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import view.views.IndexCell;
import view.views.PathCell;


public class MainController {

  public final static String INDICES_DIRECTORY = "indices\\";
  public final static String INDEX_FOLDERS_FXML = "../fxml/indexFolders.fxml";
  private ObservableList<Path> paths;
  private ObservableList<Index> indices;

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
    // initialize stage
    Stage indexCreationStage = new Stage();
    indexCreationStage.setTitle("Create index");
    // initialize controller
    IndexCreationController icc = new IndexCreationController(indices);
    javafx.scene.Node view = icc.getView();
    indexCreationStage.setScene(new Scene((Parent) view));

    // show as a modal
    indexCreationStage.initOwner(lv_files.getScene().getWindow());
    indexCreationStage.initModality(Modality.APPLICATION_MODAL);
    indexCreationStage.showAndWait();

    // result handling - adding index to indices
    IndexParameters parameters = icc.getCreatedParameters();
    String name = icc.getCreatedName();
    if (parameters == null || name == null) {
      return;
    }
    Index temp_index = new Index(name, parameters);
    indices.add(temp_index);

    openIndexFolders(temp_index);
  }

  @FXML
  void onSearch(ActionEvent event) {
    // get selected index to search in
    Index selectedIndex = lv_indices.getSelectionModel().getSelectedItem();
    // if there is no index - show dialog and return
    if (selectedIndex == null) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("No index selected");
      alert.setHeaderText(null);
      alert.setContentText("Select or create-select an index to search in on indices tab.");
      alert.showAndWait();
      return;
    }

    // building request
    SearchRequest request;
    SearchRequest.Builder builder = SearchRequest.getBuilder();
    builder.setIndex(selectedIndex)
           .setSearchFor(txt_search.getText())
           .setSubstringSearch(cb_seachSubstring.isSelected());
    request = builder.build();

    // todo: обновление списка найденных путей здесь
    paths.clear();

    // execute request
    request.execute();
    ObservableSet<Node> requestResults = request.getResult();
    requestResults.addListener((SetChangeListener<? super Node>) change -> {
      if (change.wasAdded()) {
        Set<String> filenames = change.getElementAdded().getFilenames();
        List<Path> filepaths = filenames.stream().map(s -> Paths.get(s))
                                        .collect(Collectors.toList());

        Platform.runLater(() -> paths.addAll(filepaths));
      }
    });
  }

  @FXML
  void onSearchChanged(ActionEvent event) {

  }

  private void openIndexFolders(Index index) {
    // show indexing window
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(INDEX_FOLDERS_FXML));
      Parent indexFolders = (Parent) fxmlLoader.load();

      IndexFoldersController controller = fxmlLoader.getController();
      controller.setIndex(index);

      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initStyle(StageStyle.UTILITY);
      stage.setTitle("Index folders");
      stage.setScene(new Scene(indexFolders));
      stage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }
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
    lv_files.setPlaceholder(new Label("Select index to search in and word for search."));
  }

  private void initializeIndexList() {
    lv_indices.setCellFactory(param -> new IndexCell());
    indices = FXCollections.observableArrayList();
    lv_indices.setItems(indices);
    lv_indices.setPlaceholder(new Label("No indices found."));
    lv_indices.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }

}
