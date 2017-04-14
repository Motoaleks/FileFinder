package view.controllers;

import index.Index;
import index.IndexParameters;
import index.IndexingRequest;
import index.SearchRequest;
import index.Storages.Inclusion;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
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
  public final static String INDEX_FOLDERS_FXML = "/view/fxml/indexFolders.fxml";
  public final static String INDEX_INFO_FXML = "/view/fxml/indexInfo.fxml";
  private ObservableList<Path> paths;
  private ObservableList<Index> indices;
  //  private ObservableList<Request> requestQueue;
  private final ExecutorService requests = Executors.newCachedThreadPool();
  private final ObservableList<Task<Long>> taskQueue = FXCollections.observableArrayList();

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
    Index index = new Index(name, parameters);
    indices.add(index);

    openIndexFilesForm(index);
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
    if (request != null) {
      addTask(request);
    } else {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Search failed");
      alert.setHeaderText(null);
      alert.setContentText("Failed to create search request.");
      alert.showAndWait();
      return;
    }

    ObservableSet<Inclusion> requestResults = request.getResult();
    requestResults.addListener((SetChangeListener<? super Inclusion>) change -> {
      if (change.wasAdded()) {
        Platform.runLater(() -> paths.addAll(change.getElementAdded().getPath()));
      }
    });
  }

  @FXML
  void onSearchChanged(ActionEvent event) {

  }

  @FXML
  void onShowIndexInfo(ActionEvent event) {
    if (lv_indices.getSelectionModel().getSelectedItem() == null) {
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("");
      alert.setHeaderText("Index not selected");
      alert.setContentText("Please, select index in list to show info.");
      alert.showAndWait();
      return;
    }

    try {
      // load parent node
      FXMLLoader loader = new FXMLLoader(getClass().getResource(INDEX_INFO_FXML));
      Parent indexInfoNode = loader.load();
      // prepare stage
      Stage indexInfoStage = new Stage();
      indexInfoStage.setTitle("Index info");
      indexInfoStage.setScene(new Scene(indexInfoNode));
      // set up controller
      IndexInfoController controller = loader.getController();
      Index selectedIndex = lv_indices.getSelectionModel().getSelectedItem();
      controller.setIndex(selectedIndex);
      controller.setMainStage(this);
      indexInfoStage.showAndWait();

      IndexingRequest request = controller.toRequest();
      Index index = controller.toIndex();
      // check for core changes
      if (index != null) {
        // if core changes made - remake an index
        indices.remove(index);
        indices.add(index);
      }
      // check for checnges in pahts
      if (request != null) {
        addTask(request);
      }
      lv_indices.refresh();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void openIndexFilesForm(Index index) {
    // show indexing window
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(INDEX_FOLDERS_FXML));
      Parent indexFolders = fxmlLoader.load();

      IndexFoldersController controller = fxmlLoader.getController();
      controller.setIndex(index);
      controller.setMainController(this);

      Stage stage = new Stage();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initStyle(StageStyle.UTILITY);
      stage.setTitle("Index folders");
      stage.setScene(new Scene(indexFolders));
      stage.showAndWait();

      // add request to queue
      IndexingRequest request = controller.toRequest();
      if (request != null) {
        addTask(request);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void addTask(Task<Long> task) {
    requests.submit(task);
    taskQueue.add(task);
  }

  public void saveIndices() {
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
        requests.shutdown();
        index.save(INDICES_DIRECTORY);
        index.exit();
      });
    }).start();
  }

  public void loadIndices() {
    File indicesDirectory = new File(INDICES_DIRECTORY);
    // check for directory existence, if not - leave method (cause it's no indices dude)
    if (!indicesDirectory.exists()) {
      return;
    }
    // start thread for loading indices (cause it is long operation)
    Platform.runLater(() -> {
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
    });
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

    // initialize lists
    initializePathList();
    initializeIndexList();
    initializeTaskQueue();

    // connect properties
    btn_showIndex.disableProperty().bind(lv_indices.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
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

  private void initializeTaskQueue() {
    pb_progress.visibleProperty().bind(Bindings.size(taskQueue).greaterThan(0));
    lb_status.visibleProperty().bind(Bindings.size(taskQueue).greaterThan(0));
    taskQueue.addListener((ListChangeListener<? super Task<Long>>) c -> {
      c.next();
      Task newOne = null;
      if (c.wasAdded() && c.getAddedSize() > 0) {
        newOne = c.getAddedSubList().get(0);
      } else if (c.wasRemoved() && taskQueue.size() > 0) {
        newOne = taskQueue.get(0);
      } else {
        return;
      }

      pb_progress.progressProperty().bind(newOne.progressProperty());
      lb_status.textProperty().bind(newOne.messageProperty());

      final Task temp = newOne;
      newOne.setOnSucceeded(event -> {
        taskQueue.remove(temp);
      });

    });
  }
}
