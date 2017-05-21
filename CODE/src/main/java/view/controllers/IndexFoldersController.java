package view.controllers;

import index.Index;
import index.IndexingRequest;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import view.views.PathCell;


public class IndexFoldersController {

  private ObservableList<Path> pathList;
  private Index index;
  private volatile SimpleBooleanProperty hasFiles = new SimpleBooleanProperty(false);
  private MainController mainController;

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private Button btn_index;

  @FXML
  private Button btn_foldersSelection;


  @FXML
  private ListView<Path> lv_filesToIndex;
  private boolean validated;


  @FXML
  void onOpenFileDialog(ActionEvent event) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Open Resource File");
    File result = directoryChooser.showDialog(btn_foldersSelection.getScene().getWindow());
    if (result != null) {
      pathList.add(result.toPath());
    }
  }

  @FXML
  void onIndexFolders(ActionEvent event) {
    validated = true;
    ((Stage) (btn_index.getScene().getWindow())).close();
  }

  @FXML
  void initialize() {
    assert btn_index
           != null : "fx:id=\"btn_index\" was not injected: check your FXML file 'indexFolders.fxml'.";
    assert btn_foldersSelection
           != null : "fx:id=\"btn_foldersSelection\" was not injected: check your FXML file 'indexFolders.fxml'.";
    assert lv_filesToIndex
           != null : "fx:id=\"lv_filesToIndex\" was not injected: check your FXML file 'indexFolders.fxml'.";

    // initialize list
    pathList = FXCollections.observableArrayList();
    lv_filesToIndex.setItems(pathList);
    lv_filesToIndex.setCellFactory(param -> new PathCell());
    lv_filesToIndex.setPlaceholder(new Label("Click button \"Select files and folders\""));

    // setting flag (controlling count files)
    pathList.addListener((InvalidationListener) c -> {
      if (pathList.size() > 0) {
        hasFiles.set(true);
      } else {
        hasFiles.set(false);
      }
    });

    // set index button to be available only if directories is not empty
    btn_index.disableProperty().bind(Bindings.when(hasFiles.not())
                                             .then(true)
                                             .otherwise(false));

    // set selection button to be colourful only if directories if there is no directories in list
    btn_foldersSelection.backgroundProperty().bind(Bindings.when(hasFiles.not())
                                                           .then(new Background(
                                                               new BackgroundFill(Color.valueOf("#8CC152"), null,
                                                                                  null)))
                                                           .otherwise(new Background(
                                                               new BackgroundFill(Color.valueOf("#656D78"), null,
                                                                                  null))));
    // set index button to be colourful only if directories if there is no directories in list
    btn_index.backgroundProperty().bind(Bindings.when(hasFiles)
                                                .then(new Background(
                                                    new BackgroundFill(Color.valueOf("#8CC152"), null, null)))
                                                .otherwise(new Background(
                                                    new BackgroundFill(Color.valueOf("#656D78"), null, null))));
  }


  public IndexingRequest toRequest() {
    if (!validated) {
      return null;
    }
    IndexingRequest.Builder builder = IndexingRequest.getBuilder();
    for (Path path : pathList) {
      builder.addPath(path);
    }
    builder.setIndex(index);
    return builder.build();
  }

  public void setIndex(Index index) {
    this.index = index;
  }

  public void setMainController(MainController mainController) {
    this.mainController = mainController;
  }
}
