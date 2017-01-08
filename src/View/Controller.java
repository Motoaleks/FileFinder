package View;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ListView<?> lv_files;

    @FXML
    private RadioButton rb_fileSearch;

    @FXML
    private TextArea ta_preview;

    @FXML
    private TextField txt_search;


    @FXML
    void initialize() {
        assert lv_files != null : "fx:id=\"lv_files\" was not injected: check your FXML file 'main.fxml'.";
        assert rb_fileSearch != null : "fx:id=\"rb_fileSearch\" was not injected: check your FXML file 'main.fxml'.";
        assert ta_preview != null : "fx:id=\"ta_preview\" was not injected: check your FXML file 'main.fxml'.";
        assert txt_search != null : "fx:id=\"txt_search\" was not injected: check your FXML file 'main.fxml'.";


    }

}
