package view.controllers;

import com.sun.prism.paint.Color;
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
import java.util.Objects;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import view.views.InclusionCell;
import view.views.IndexCell;
import view.views.PathCell;


public class MainController {

  public final static String INDICES_DIRECTORY = "indices\\";
  public final static String INDEX_FOLDERS_FXML = "/view/fxml/indexFolders.fxml";
  public final static String INDEX_INFO_FXML = "/view/fxml/indexInfo.fxml";
  public final static String ERROR_CLASS = "error";
  public final static int MAX_HISTORY = 8;
  private ObservableList<Inclusion> paths;
  private ObservableList<Index> indices;
  private ObservableList<SearchRequest> history;
  private final ExecutorService requests = Executors.newCachedThreadPool();
  private final ObservableList<Task<Long>> taskQueue = FXCollections.observableArrayList();
  private Task currentTask = null;

  private Node stashedPane;

  @FXML
  private Button btn_cancel;

  @FXML
  private ComboBox<SearchRequest> cb_search;

  @FXML
  private Button btn_search;

  @FXML
  private SplitPane sp_main;

  @FXML
  private Button btn_createIndex;

  @FXML
  private Button btn_showIndex;

  @FXML
  private Label lb_status;

  @FXML
  private CheckBox cb_seachSubstring;

  @FXML
  private ListView<Inclusion> lv_files;

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
  void onSearch() {
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
    String searchFor = cb_search.getEditor().textProperty().get();
    if (searchFor == null || "".equals(searchFor)) {
      return;
    }

    SearchRequest request = cb_search.getConverter().fromString(cb_search.getEditor().getText());


    // todo: обновление списка найденных путей здесь
    paths.clear();

    // execute request
    if (request != null) {
      addTask(request);

      if (!history.contains(request)) {
        history.add(request);
      }
      cb_search.getSelectionModel().select(request);

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
        Platform.runLater(() -> {
          paths.add(change.getElementAdded());
        });
//        Platform.runLater(() -> paths.addAll(change.getElementAdded().getPath()));
      }
    });
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

  @FXML
  void onCancelTask(ActionEvent event) {
    if (taskQueue.size() == 0) {
      return;
    }
    currentTask.cancel();
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
    new Thread(() -> {
      try (Stream<Path> paths = Files.walk(indicesDirectory.toPath().toAbsolutePath())) {
        paths.forEach(filePath -> {
          // check if name contain '.ser' - serialization extension
          if (filePath.getFileName().toString().contains(".ser")) {
            Index temp = Index.load(INDICES_DIRECTORY + filePath.getFileName().toString());
            Platform.runLater(() -> {
              indices.add(temp);
            });
          }
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }).start();
  }

  public void setPreview(boolean active) {
    if (active) {
      if (stashedPane == null) {
        return;
      }
      sp_main.getItems().add(1, stashedPane);
      sp_main.setDividerPosition(1, 0.8);
      stashedPane = null;
    } else {
      stashedPane = sp_main.getItems().get(1);
      sp_main.getItems().remove(stashedPane);
    }
  }

  @FXML
  void initialize() {
    // initialize items
    initializePathList();
    initializeIndexList();
    initializeTaskQueue();
    initializeHistory();

    // connect properties
    initializeSmallBindings();

    // set hidable pane
    stashedPane = sp_main.getItems().get(1);
    bindPreview();
    setPreview(false);
  }

  private void initializeSmallBindings() {
    btn_showIndex.disableProperty().bind(lv_indices.getSelectionModel().selectedIndexProperty().isEqualTo(-1));
    btn_search.disableProperty().bind(lv_indices.getSelectionModel().selectedIndexProperty().isEqualTo(-1)
                                                .or(cb_search.getEditor().textProperty().isEqualTo("")));
    cb_search.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null || "".equals(newValue)) {
        cb_search.getStyleClass().add(ERROR_CLASS);
      } else {
        cb_search.getStyleClass().remove(ERROR_CLASS);
      }
    });
    cb_search.setOnKeyPressed(event -> {
      if (event.getCode() != KeyCode.ENTER) {
        return;
      }
      onSearch();
    });
  }

  private void bindPreview() {
    lv_files.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        setPreview(false);
      } else {
        setPreview(true);
      }
    });
  }

  private void initializePathList() {
//    lv_files.setCellFactory(param -> new PathCell());
    lv_files.setCellFactory(param -> {
      ListCell cell = new InclusionCell();
      SelectionModel selectionModel = lv_files.getSelectionModel();
      cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        lv_files.requestFocus();
        if (!cell.isEmpty() && selectionModel.getSelectedIndex() != cell.getIndex()) {
          selectionModel.select(cell.getIndex());
        } else {
          lv_files.getSelectionModel().clearSelection();
        }
        event.consume();
      });
      return cell;
    });
    paths = FXCollections.observableArrayList();
    lv_files.setItems(paths);
    lv_files.setPlaceholder(new Label("Select index to search in and word for search."));
    lv_files.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }

  private void initializeIndexList() {
    lv_indices.setCellFactory(param -> {
      ListCell cell = new IndexCell();
      SelectionModel selectionModel = lv_indices.getSelectionModel();
      cell.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        lv_indices.requestFocus();
        if (!cell.isEmpty() && selectionModel.getSelectedIndex() != cell.getIndex()) {
          selectionModel.select(cell.getIndex());
        } else {
          lv_indices.getSelectionModel().clearSelection();
        }
        event.consume();
      });
      return cell;
    });
    indices = FXCollections.observableArrayList();
    lv_indices.setItems(indices);
    lv_indices.setPlaceholder(new Label("No indices found."));
    lv_indices.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }

  private void initializeTaskQueue() {
    pb_progress.visibleProperty().bind(Bindings.size(taskQueue).greaterThan(0));
    lb_status.visibleProperty().bind(Bindings.size(taskQueue).greaterThan(0));
    btn_cancel.visibleProperty().bind(Bindings.size(taskQueue).greaterThan(0));
    taskQueue.addListener((ListChangeListener<? super Task<Long>>) c -> {
      c.next();
      if (c.wasAdded() && c.getAddedSize() > 0) {
        currentTask = c.getAddedSubList().get(0);
      } else if (c.wasRemoved() && taskQueue.size() > 0) {
        currentTask = taskQueue.get(0);
      } else {
        if (c.wasRemoved() && taskQueue.size() == 0) {
          currentTask = null;
        }
        return;
      }

      pb_progress.progressProperty().bind(currentTask.progressProperty());
      lb_status.textProperty().bind(currentTask.messageProperty());

      final Task temp = currentTask;
      currentTask.setOnSucceeded(event -> {
        taskQueue.remove(temp);
      });
      currentTask.setOnCancelled(event -> {
        taskQueue.remove(temp);
      });

    });
  }

  private void initializeHistory() {
    lv_indices.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        cb_search.setEditable(false);
        cb_search.setPromptText("Select index on index tab");
      } else {
        cb_search.setEditable(true);
        cb_search.setPromptText("Input text");
        cb_search.getEditor().setText("");
      }
    });
    cb_search.setEditable(false);
    cb_search.setPromptText("Select index on index tab");
    cb_search.setPlaceholder(new Label("No history"));

    cb_search.setCellFactory(param -> new TextFieldListCell<SearchRequest>() {
      @Override
      public void updateItem(SearchRequest item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setGraphic(null);
        } else {
          HBox container = new HBox();

          Label temp_searchFor = new Label("word: ");
          temp_searchFor.setTextFill(javafx.scene.paint.Color.GRAY);
          temp_searchFor.setOpacity(0.8);

          Label searchFor = new Label(item.getSearchFor());
          HBox.setHgrow(searchFor, Priority.ALWAYS);
          searchFor.setMaxWidth(Double.MAX_VALUE);

          Label temp_from = new Label("index: ");
          temp_from.setTextFill(javafx.scene.paint.Color.GRAY);
          temp_from.setOpacity(0.8);

          Label from = new Label(item.getIndex().getName());
          HBox.setHgrow(from, Priority.ALWAYS);
          from.setMaxWidth(Double.MAX_VALUE);
          from.setTextFill(javafx.scene.paint.Color.BLUE);

          container.getChildren().addAll(temp_searchFor, searchFor, temp_from, from);
          setGraphic(container);
          setText(null);
        }
      }
    });
    cb_search.setConverter(new StringConverter<SearchRequest>() {
      @Override
      public String toString(SearchRequest object) {
        if (object == null) {
          return null;
        }
        return object.toString();
      }

      @Override
      public SearchRequest fromString(String string) {
        if (string == null || "".equals(string)) {
          return null;
        }

        Index index = lv_indices.getSelectionModel().getSelectedItem();
        if (index == null) {
          return null;
        }

        SearchRequest request = SearchRequest.getBuilder().setIndex(index)
                                             .setSearchFor(string.toLowerCase())
                                             .setSubstringSearch(cb_seachSubstring.isSelected()).build();
        if (request == null) {
          return null;
        }
        return request;
      }
    });

    history = FXCollections.observableArrayList();
    history.addListener((ListChangeListener<? super SearchRequest>) c -> {
      c.next();
      if (c.wasAdded()) {
        if (history.size() >= MAX_HISTORY) {
          history.remove(0);
        }
      }
    });
    cb_search.setItems(history);
    cb_search.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }
      paths.clear();

      paths.setAll(newValue.getResult());
    });
  }
}
