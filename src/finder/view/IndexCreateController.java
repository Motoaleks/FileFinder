package finder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;

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
  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private Button btn_createIndex;


  @FXML
  void onCreateIndex(ActionEvent event) {
  }

  @FXML
  void initialize() {
    assert btn_createIndex
        != null : "fx:id=\"btn_createIndex\" was not injected: check your FXML file 'indexCreate.fxml'.";
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
