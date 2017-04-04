package view.controllers;

import index.Index;
import index.IndexParameters;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class IndexCreationController {

  // for errors in name
  private final String INDEX_CREATION_FXML = "../fxml/indexCreation.fxml";
  private final ObservableList<Index> indices;
  private       IndexParameters       parameters;
  private       Node                  view;
  private       boolean               confirmed;
  @FXML
  private       ResourceBundle        resources;

  @FXML
  private URL location;

  @FXML
  private Button btn_acceptIndexCreation;

  @FXML
  private Button btn_createIndex;

  @FXML
  private Button btn_returnToConfiguring;

  @FXML
  private HBox hb_bottomBox;

  @FXML
  private ScrollPane sp_parameters;

  @FXML
  private TextField tf_indexName;


  public IndexCreationController(ObservableList<Index> indices) {
    this.indices = indices;
  }

  @FXML
  void onAffirmIndexCreation(ActionEvent event) {
    // disable view
    view.setDisable(true);
    // confirm index creation
    confirmed = true;
    // return to main window
    ((Stage) (view.getScene().getWindow())).close();
  }

  @FXML
  void onCreateIndex(ActionEvent event) {
    String temp_name = tf_indexName.getText();
    // if name is not empty && is not (in runtime ||on disk) - go to confirmation
    if (!"".equals(temp_name) && nameAvailable(temp_name)) {
      tf_indexName.getStyleClass().remove("error");
      toConfirmation();
    } else {
      tf_indexName.getStyleClass().add("error");
    }
  }

  @FXML
  void onReturnToConfiguring(ActionEvent event) {
    toEditing();
  }

  private void toConfirmation() {
    inputEnabled(false);
  }

  private void toEditing() {
    inputEnabled(true);
  }

  private void inputEnabled(boolean enabled) {
    // set name changing disabled
    tf_indexName.setEditable(enabled);

    // disable all parameter choosers
    VBox sp_content = (VBox) sp_parameters.getContent();
    for (Node node : sp_content.getChildren()) {
      node.setDisable(!enabled);
    }

    // configure buttons
    btn_returnToConfiguring.setDisable(enabled);
    btn_acceptIndexCreation.setDisable(enabled);
    btn_createIndex.setDisable(!enabled);

    // configure buttons visibility
    // create index button visibility
    btn_createIndex.setVisible(enabled);
    btn_createIndex.setManaged(enabled);
    // returnToConfig button visibility
    btn_returnToConfiguring.setVisible(!enabled);
    btn_returnToConfiguring.setManaged(!enabled);
    // acceptIndexCreation button visibility
    btn_acceptIndexCreation.setVisible(!enabled);
    btn_acceptIndexCreation.setManaged(!enabled);
  }

  private boolean nameAvailable(String name) {
    boolean fileInMemory = new File(MainController.INDICES_DIRECTORY, name).exists();
    boolean fileInRuntime = indices.stream().anyMatch(index -> index.getName().equals(name));
    return !(fileInMemory || fileInRuntime);
  }

  @FXML
  void initialize() {
    assert btn_acceptIndexCreation
           != null : "fx:id=\"btn_acceptIndexCreation\" was not injected: check your FXML file 'indexCreation.fxml'.";
    assert btn_createIndex
           != null : "fx:id=\"btn_createIndex\" was not injected: check your FXML file 'indexCreation.fxml'.";
    assert btn_returnToConfiguring
           != null : "fx:id=\"btn_returnToConfiguring\" was not injected: check your FXML file 'indexCreation.fxml'.";
    assert hb_bottomBox
           != null : "fx:id=\"hb_bottomBox\" was not injected: check your FXML file 'indexCreation.fxml'.";
    assert sp_parameters
           != null : "fx:id=\"sp_parameters\" was not injected: check your FXML file 'indexCreation.fxml'.";
    assert tf_indexName
           != null : "fx:id=\"tf_indexName\" was not injected: check your FXML file 'indexCreation.fxml'.";

    parameters = new IndexParameters();
    // initialize paramlist
    initializeParamList();

    // validation textfield - index name
    tf_indexName.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        tf_indexName.getStyleClass().remove("error");
      }
    });

    // start with editing
    toEditing();
  }

  private void initializeParamList() {
    VBox paramList = (VBox) Controllers.getParamList(parameters);
    sp_parameters.widthProperty().addListener((observable, oldValue, newValue) -> {
      paramList.prefWidthProperty().setValue(newValue.intValue() - 20);
    });
    sp_parameters.setContent(paramList);
  }

  public Node getView() {
    if (view == null) {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(INDEX_CREATION_FXML));
        loader.setController(this);
        view = loader.load();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return view;
  }

  public IndexParameters getCreatedParameters() {
    // if confirmed return parameters
    if (confirmed) {
      return parameters;
    }
    // if not - just null
    return null;
  }

  public String getCreatedName() {
    if (confirmed) {
      return tf_indexName.getText();
    }
    return null;
  }
}