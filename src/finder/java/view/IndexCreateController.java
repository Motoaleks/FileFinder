package view;

import index.BooleanValue;
import index.Index;
import index.InvertedIndex;
import index.MapValue;
import index.Parameter;
import index.ParameterMap;
import index.Value;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;

/**
 * Created by: Aleksandr
 * Date: 16.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class IndexCreateController {

  private final String indexCreateFileName = "indexCreate.fxml";
  private Node view;
  private Index currentIndex;


  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private Button btn_createIndex;

  @FXML
  private Slider sl_indexType;

  @FXML
  private TextField tf_indexName;

  @FXML
  private TableView<Entry<Parameter, Value>> tv_parameters;


  @FXML
  void onCreateIndex(ActionEvent event) {
  }

  @FXML
  void onIndexChanged(DragEvent event) {
  }

  @FXML
  void initialize() {
    assert btn_createIndex
        != null : "fx:id=\"btn_createIndex\" was not injected: check your FXML file 'indexCreate.fxml'.";
    assert sl_indexType
        != null : "fx:id=\"sl_indexType\" was not injected: check your FXML file 'indexCreate.fxml'.";
    assert tf_indexName
        != null : "fx:id=\"tf_indexName\" was not injected: check your FXML file 'indexCreate.fxml'.";
    assert tv_parameters
        != null : "fx:id=\"tv_parameters\" was not injected: check your FXML file 'indexCreate.fxml'.";

    configureTableView();
    configureSlider();
  }


  private void configureTableView() {
    // add property name column and configure callbacks
    TableColumn<Entry<Parameter, Value>, String> column_name = new TableColumn<>("Property name");
    column_name.setCellFactory(param -> new TableCell<Entry<Parameter, Value>, String>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
          setText(null);
          setStyle("");
        } else {
          setText(item);
        }
      }
    });
    column_name.setCellValueFactory(
        param -> new SimpleStringProperty(param.getValue().getKey().name()));

    // add value column and configure callbacks
    TableColumn<Entry<Parameter, Value>, Value> column_value = new TableColumn<>("Value");
    column_value.setCellFactory(param -> new TableCell<Entry<Parameter, Value>, Value>() {
      @Override
      protected void updateItem(Value item, boolean empty) {
        super.updateItem(item, empty);

        // todo: Разнести на классы, рефакторинг
        if (item == null || empty) {
          setText(null);
          setStyle("");
        } else {
          Node content = null;
          if (item instanceof BooleanValue) {
            // Checkbox for boolean parameter values with binding to actual variable
            CheckBox temp = new CheckBox();
            temp.setSelected((Boolean) item.get());
            temp.selectedProperty().addListener((observable, oldValue, newValue) -> {
              item.set(newValue);
            });
            content = temp;
          } else {
            // Else - open dialog button
            Button temp = new Button("Configure");
            temp.setOnAction(event -> {
              // create dialog for mapping values
              Dialog<MapValue> dialog = new Dialog<>();
              dialog.setTitle("Settings");
              dialog.setHeaderText("PLease select needed values.");
              dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

              TextField textField = new TextField();
              textField.setPadding(new Insets(4, 5, 4, 5));
              dialog.getDialogPane().setContent(textField);

              dialog.showAndWait();
            });
            content = temp;
          }
          setGraphic(content);
        }
      }
    });
    column_value.setCellValueFactory(
        param -> new ObservableValueBase<Value>() {
          @Override
          public Value getValue() {
            return param.getValue().getValue();
          }
        });

    // adding all columns to table
    tv_parameters.getColumns().addAll(column_name, column_value);

    // set column fill width
    tv_parameters.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private void configureSlider() {
    sl_indexType.valueProperty().addListener(this::indexChanged);
  }

  private void indexChanged(ObservableValue<? extends Number> observable, Number oldValue,
      Number newValue) {
    if (newValue.intValue() - oldValue.intValue() < 1) {
      return;
    }

    switch (newValue.intValue()) {
      case 1:
        setIndex(new InvertedIndex());
        break;
    }
  }

  private void setIndex(Index index) {
    this.currentIndex = index;
    updateParameters();
  }

  private void updateParameters() {
    ParameterMap params = currentIndex.getCurrentParameters();
    ObservableList<Entry<Parameter, Value>> parameterList = FXCollections
        .observableList(new LinkedList<>(params.getParameterSet()));
    tv_parameters.setItems(parameterList);

    // todo: update
  }

  public Node getView() {
    if (view == null) {
      try {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(indexCreateFileName));
        loader.setController(this);
        view = loader.load();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return view;
  }
}
