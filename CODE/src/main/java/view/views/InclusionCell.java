package view.views;

import index.Storages.Inclusion;
import java.io.IOException;
import java.nio.file.Path;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;

/**
 * Created by Александр on 15.04.2017.
 */
public class InclusionCell extends ListCell<Inclusion> {

  FXMLLoader loader;
  public static final String FXML_INCLUSION_CELL = "/view/fxml/inclusionCell.fxml";
  private boolean hovered = false;
  private boolean selected = false;

  @FXML
  private BorderPane bp_main;

  @FXML
  private Label lbl_date;

  @FXML
  private Label lbl_place;

  @FXML
  private Label lbl_name;

  public InclusionCell() {
    selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }
      selected = newValue;
      refresh();
    });
    hoverProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        return;
      }
      hovered = newValue;
      refresh();
    });

  }

  private void refresh() {
    if (getItem() == null) {
      return;
    }
    if (hovered || selected) {
      lbl_name.setText(getItem().getPath().toString());
    } else {
      Path filename = getItem().getPath().getFileName();
      String name = null;
      if (filename == null) {
        name = getItem().getPath().getParent().getFileName().toString();
      } else {
        name = filename.toString();
      }
      lbl_name.setText(name);
    }
  }

  @Override
  protected void updateItem(Inclusion item, boolean empty) {
    super.updateItem(item, empty);
    if (item == null || empty) {
      setGraphic(null);
    } else {
      if (loader == null) {
        loader = new FXMLLoader(getClass().getResource(FXML_INCLUSION_CELL));
        loader.setController(this);

        try {
          loader.load();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      setItem(item);
      refresh();
      lbl_date.setText(item.getUpdated().toString());
      switch ((int) item.getPlace()) {
        case -2:
          lbl_place.setText("extension");
          break;
        case -1:
          lbl_place.setText("filename");
          break;
        default:
          lbl_place.setText("line " + item.getPlace());
      }
      setGraphic(bp_main);
    }
  }
}
