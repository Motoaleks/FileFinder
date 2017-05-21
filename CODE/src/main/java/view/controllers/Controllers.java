/*
 * Created by Aleksandr Smilyanskiy
 * Date: 02.04.17 22:59
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package view.controllers;

import index.IndexParameters;
import index.Parameter;
import java.util.Map.Entry;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.DefaultStringConverter;

/**
 * Created by: Aleksandr
 * Date: 02.04.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class Controllers {

  public static Node getParamList(IndexParameters parameters) {
    // create vbox for scroll view
    VBox content = new VBox();
    content.setPadding(new Insets(4, 10, 4, 10));
    content.setSpacing(10);

    // fill vbox with values
    for (Entry<Parameter, ObservableValue> parameterEntry : parameters.getStorage().entrySet()) {
      Node value = null;
      switch (parameterEntry.getKey().getType()) {
        case 0: { // case boolean
          CheckBox temp = new CheckBox(parameterEntry.getKey().name());
          temp.selectedProperty().bindBidirectional((Property<Boolean>) parameterEntry.getValue());
          value = temp;
          Label description = new Label(parameterEntry.getKey().getDescription());
          value = new VBox(temp, description);
          break;
        }
        case 2: { // case list
          Label key = new Label(parameterEntry.getKey().name());
          Label description = new Label(parameterEntry.getKey().getDescription());
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
          value = new VBox(key, description, temp);
          break;
        }
      }

      if (value != null) {
        content.getChildren().add(value);
      }
    }
    return content;
  }
}
