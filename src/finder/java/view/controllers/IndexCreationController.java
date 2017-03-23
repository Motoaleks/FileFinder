package view.controllers;

import index.IndexParameters;
import index.Parameter;
import java.io.IOException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.DefaultStringConverter;


public class IndexCreationController {

  private final String indexCreationFileName = "../fxml/indexCreation.fxml";
  private IndexParameters parameters;
  private Node view;
  private boolean confirmed;

  @FXML
  private ResourceBundle resources;

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
    toConfirmation();
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

    // start with editing
    toEditing();
  }

  private void initializeParamList() {
    // create vbox for scroll view
    VBox content = new VBox();
    content.setPadding(new Insets(4, 10, 4, 10));
    content.setSpacing(10);
    sp_parameters.widthProperty().addListener((observable, oldValue, newValue) -> {
      content.prefWidthProperty().setValue(newValue.intValue() - 20);
    });
    sp_parameters.setContent(content);

    // fill vbox with values
    for (Entry<Parameter, ObservableValue> parameterEntry : parameters.getStorage().entrySet()) {
      Node value = null;
      switch (parameterEntry.getKey().getType()) {
        case 0: { // case boolean
          CheckBox temp = new CheckBox(parameterEntry.getKey().name());
          temp.selectedProperty().bindBidirectional((Property<Boolean>) parameterEntry.getValue());
          value = temp;
          break;
        }
        case 2: { // case list
          Label label = new Label(parameterEntry.getKey().name());
          ListView<String> temp = new ListView<>();
          temp.setEditable(true);
          temp.setMaxHeight(100);
          temp.setCellFactory(param -> {
            TextFieldListCell tf = new TextFieldListCell(new DefaultStringConverter());
            tf.setEditable(true);
            return tf;
          });
          temp.setOnEditCommit(event -> {
            temp.getItems().set(event.getIndex(), event.getNewValue());
          });
          temp.itemsProperty().bind(parameterEntry.getValue());
          value = new VBox(label, temp);
          break;
        }
      }

      if (value != null) {
        content.getChildren().add(value);
      }
    }
  }

  public Node getView() {
    if (view == null) {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(indexCreationFileName));
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