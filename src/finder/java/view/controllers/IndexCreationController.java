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
import javafx.util.converter.DefaultStringConverter;


public class IndexCreationController {

  private final String indexCreationFileName = "../fxml/indexCreation.fxml";
  IndexParameters parameters;
  private Node view;

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
  }

  @FXML
  void onCreateIndex(ActionEvent event) {
  }

  @FXML
  void onReturnToConfiguring(ActionEvent event) {
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
    VBox content = new VBox();
    content.setPadding(new Insets(4, 10, 4, 10));
    content.setSpacing(10);
    sp_parameters.widthProperty().addListener((observable, oldValue, newValue) -> {
      content.prefWidthProperty().setValue(newValue.intValue() - 20);
    });
    sp_parameters.setContent(content);

    for (Entry<Parameter, ObservableValue> parameterEntry : parameters.getStorage().entrySet()) {
      Node value = null;
      switch (parameterEntry.getKey().getType()) {
        case 0: {
          CheckBox temp = new CheckBox(parameterEntry.getKey().name());
          temp.selectedProperty().bindBidirectional((Property<Boolean>) parameterEntry.getValue());
          value = temp;
          break;
        }
        case 2: {
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


}

///*
// * Created by Aleksandr Smilyanskiy
// * Date: 19.02.17 17:02
// * Project: FileFinder
// *
// * "The more we do, the more we can do"
// * Copyright (c) 2017.
// */
//
//package view.controllers;
//
//import index.algorithms.InvertedIndex;
//import index.logic.Index;
//import index.parameters.BooleanValue;
//import index.parameters.ListValue;
//import index.parameters.Parameter;
//import index.parameters.ParameterMap;
//import index.parameters.Value;
//import java.io.IOException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.Map.Entry;
//import java.util.ResourceBundle;
//import java.util.stream.Collectors;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.value.ObservableValue;
//import javafx.beans.value.ObservableValueBase;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Node;
//import javafx.scene.control.Button;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.Slider;
//import javafx.scene.control.TableCell;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextField;
//import javafx.scene.layout.HBox;
//
//enum CreationState {
//  CONFIGURING,
//  CONFIRMATION,
//  INDEXING
//}
//
///**
// * Created by: Aleksandr
// * Date: 16.01.2017
// * Project: FileFinder
// *
// * "The more we do, the more we can do" ©
// */
//public class IndexCreationController {
//
//  // path to view-fxml file
//  private final String indexCreationFileName = "../fxml/indexCreation.fxml";
//  // view node
//  private Node view;
//  // current index algorithm
//  private Index currentIndex;
//  // view state
//  private CreationState creationState;
//
//  @FXML
//  private ResourceBundle resources;
//
//  @FXML
//  private URL location;
//
//  @FXML
//  private Button btn_createIndex;
//
//  @FXML
//  private Button btn_returnToConfiguring;
//
//  @FXML
//  private Button btn_acceptIndexCreation;
//
//  @FXML
//  private Slider sl_indexType;
//
//  @FXML
//  private TextField tf_indexName;
//
//  @FXML
//  private HBox hb_bottomBox;
//
//  @FXML
//  private TableView<Entry<Parameter, Value>> tv_parameters;
//
//  @FXML
//  void onCreateIndex(ActionEvent event) {
//    changeCreationState(CreationState.CONFIRMATION);
//  }
//
//  @FXML
//  void onReturnToConfiguring(ActionEvent event) {
//    changeCreationState(CreationState.CONFIGURING);
//  }
//
//  @FXML
//  void onAffirmIndexCreation(ActionEvent event) {
//    changeCreationState(CreationState.INDEXING);
//  }
//
//  @FXML
//  void initialize() {
//    assert btn_acceptIndexCreation
//        != null : "fx:id=\"btn_acceptIndexCreation\" was not injected: check your FXML file 'indexCreation.fxml'.";
//    assert btn_createIndex
//        != null : "fx:id=\"btn_createIndex\" was not injected: check your FXML file 'indexCreation.fxml'.";
//    assert btn_returnToConfiguring
//        != null : "fx:id=\"btn_returnToConfiguring\" was not injected: check your FXML file 'indexCreation.fxml'.";
//    assert sl_indexType
//        != null : "fx:id=\"sl_indexType\" was not injected: check your FXML file 'indexCreation.fxml'.";
//    assert tf_indexName
//        != null : "fx:id=\"tf_indexName\" was not injected: check your FXML file 'indexCreation.fxml'.";
//    assert tv_parameters
//        != null : "fx:id=\"tv_parameters\" was not injected: check your FXML file 'indexCreation.fxml'.";
//    assert hb_bottomBox
//        != null : "fx:id=\"hb_bottomBox\" was not injected: check your FXML file 'indexCreation.fxml'.";
//
//    changeCreationState(CreationState.CONFIGURING);
//
//    configureTableView();
//    configureSlider();
//  }
//
//  public void changeCreationState(CreationState state) {
//    switch (state) {
//      // set state to configuring - allow to config parameters
//      case CONFIGURING: {
//        setDisable(false);
//        hb_bottomBox.getChildren().clear();
//        hb_bottomBox.getChildren().add(btn_createIndex);
//        break;
//      }
//      // set state to confirmation - disable editing, refresh parameters, show info
//      case CONFIRMATION: {
//        evaluateData();
//        setDisable(true);
//        hb_bottomBox.getChildren().clear();
//        hb_bottomBox.getChildren().addAll(btn_returnToConfiguring, btn_acceptIndexCreation);
//        break;
//      }
//    }
//    this.creationState = state;
//  }
//
//  private void evaluateData() {
//
//  }
//
//  private void setDisable(boolean state) {
//    tf_indexName.setDisable(state);
//    tv_parameters.setDisable(state);
//    sl_indexType.setDisable(state);
//  }
//
//  private void configureTableView() {
//    // add columns
//    tv_parameters.getColumns().addAll(new PropertyNameColumn(), new PropertyValueColumn());
//
//    // set column fill width
//    tv_parameters.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//  }
//
//  private void configureSlider() {
//    sl_indexType.valueProperty().addListener(this::indexChanged);
//  }
//
//  private void indexChanged(ObservableValue<? extends Number> observable, Number oldValue,
//      Number newValue) {
//    // check if index not changed
//    if (Math.abs(newValue.intValue() - oldValue.intValue()) < 1) {
//      return;
//    }
//
//    // choose a new one
//    switch (newValue.intValue()) {
//      case 1:
//        setIndex(new InvertedIndex());
//        break;
//    }
//  }
//
//  private void setIndex(Index index) {
//    this.currentIndex = index;
//    updateParameters();
//  }
//
//  private void updateParameters() {
//    ParameterMap params = currentIndex.getCurrentParameters();
//    ObservableList<Entry<Parameter, Value>> parameterList = FXCollections
//        .observableList(new LinkedList<>(params.getParameterSet()));
//    tv_parameters.setItems(parameterList);
//
//    // todo: update
//  }
//
//  public Node getView() {
//    if (view == null) {
//      try {
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(indexCreationFileName));
//        loader.setController(this);
//        view = loader.load();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    }
//
//    return view;
//  }
//}
//
//class PropertyNameColumn extends TableColumn<Entry<Parameter, Value>, String> {
//
//  PropertyNameColumn() {
//    super("Name");
//    setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey().name()));
//  }
//}
//
//class PropertyValueColumn extends TableColumn<Entry<Parameter, Value>, Value> {
//
//  PropertyValueColumn() {
//    setText("Value");
//    setCellFactory(param -> new TableCell<Entry<Parameter, Value>, Value>() {
//      @Override
//      protected void updateItem(Value item, boolean empty) {
//        super.updateItem(item, empty);
//
//        if (item == null || empty) {
//          setText(null);
//          setStyle("");
//        } else {
//          // initialize empty node - view. In feature - initialize with concrete view.
//          Node content = null;
//
//          // if true-false value (TYPE 0) then - just checkbox
//          if (item instanceof BooleanValue) {
//            content = new BooleanNode((BooleanValue) item);
//          }
//          // else if list value (TYPE 2) (like formats) - then textfield
//          else if (item instanceof ListValue) {
//            content = new ListNode((ListValue) item);
//          }
//          setGraphic(content);
//        }
//      }
//    });
//    setCellValueFactory(param -> new ObservableValueBase<Value>() {
//      @Override
//      public Value getValue() {
//        return param.getValue().getValue();
//      }
//    });
//  }
//
//  private class BooleanNode extends CheckBox {
//
//    BooleanNode(BooleanValue item) {
//      super();
//      setSelected((Boolean) item.get());
//      selectedProperty().addListener((observable, oldValue, newValue) -> item.set(newValue));
//    }
//  }
//
//  private class ListNode extends TextField {
//
//    ListNode(ListValue item) {
//      super();
//      ArrayList temp = (ArrayList) item.get();
//      setText((String) temp.stream().map(Object::toString).collect(Collectors.joining(" ")));
//    }
//  }
//}