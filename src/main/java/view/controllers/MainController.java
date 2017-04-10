package view.controllers;

import index.Index;
import index.IndexParameters;
import index.IndexingRequest;
import index.Request;
import index.SearchRequest;
import index.Storages.entities.Inclusion;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
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
  public final static String INDEX_FOLDERS_FXML = "/view/fxml/indexFolders.fxml";
  public final static String INDEX_INFO_FXML = "/view/fxml/indexInfo.fxml";
  private ObservableList<Path> paths;
  private ObservableList<Index> indices;
  private ObservableList<Request> requestQueue;

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
    ObservableSet<Inclusion> requestResults = request.getResult();
    requestResults.addListener((SetChangeListener<? super Inclusion>) change -> {
      if (change.wasAdded()) {
        Platform.runLater(() -> paths.addAll(change.getElementAdded().getFile()));
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

      if (controller.getStatus() == 2) {
        // if index is redone - remove old add new one
        indices.remove(selectedIndex);
        indices.add(controller.getResult());
      }
      lv_indices.refresh();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void openIndexFolders(Index index) {
    // show indexing window
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(INDEX_FOLDERS_FXML));
      Parent indexFolders = (Parent) fxmlLoader.load();

      IndexFoldersController controller = fxmlLoader.getController();
      controller.setIndex(index);
      controller.setMainController(this);

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
        index.save(INDICES_DIRECTORY);
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

    // initialize lists
    initializePathList();
    initializeIndexList();
    initializeStatusQueue();

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

  private void initializeStatusQueue() {
    requestQueue = FXCollections.observableArrayList();
    lb_status.visibleProperty().bind(Bindings.size(requestQueue).greaterThan(0));
    pb_progress.visibleProperty().bind(Bindings.size(requestQueue).greaterThan(0));

//    // listeners for actions
//    ChangeListener<String> statusListener = (observable1, oldValue, newValue) -> Platform.runLater(() -> {
//      lb_status.setText(newValue);
//    });
//    ChangeListener<Number> progressListener = (observable1, oldValue, newValue) -> {
//      Platform.runLater(() -> {
//        pb_progress.setProgress((Double) newValue);
//      });
//      if ((double) newValue >= 1) {
//        requestQueue.remove(request);
//      }
//    };

    requestQueue.addListener((InvalidationListener) observable -> {
      if (requestQueue.size() <= 0) {
        return;
      }
//      if (requestQueue.size() >= 2) {
//        Request oldRequest = requestQueue.get(requestQueue.size() - 1);
//        oldRequest.statusProperty().removeListener();
//      }

      Request request = requestQueue.get(requestQueue.size() - 1);

//
//      request.statusProperty().addListener(statusListener);
//      request.progressProperty().addListener(progressListener);

      Platform.runLater(() -> {
        lb_status.textProperty().bind(request.statusProperty());
        pb_progress.progressProperty().bind(request.progressProperty());
        request.progressProperty().addListener((observable1, oldValue, newValue) -> {
          if (newValue.doubleValue() >= 1.) {
            requestQueue.remove(request);
          }
        });
      });
//      Platform.runLater(() -> {
//        lb_status.setText(request.statusProperty().getValue());
//        pb_progress.setProgress(request.progressProperty().getValue());
//      });
    });
  }

  public void registerRequest(IndexingRequest request) {
    requestQueue.add(request);
  }
}
