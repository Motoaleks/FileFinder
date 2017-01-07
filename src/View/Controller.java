package View;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label lb_progressName;

    @FXML
    private HBox lv_files;

    @FXML
    private ProgressBar pb_progressStatus;

    @FXML
    private RadioButton rb_fileSearch;

    @FXML
    private TextField txt_search;


    @FXML
    void initialize() {
        assert lb_progressName != null : "fx:id=\"lb_progressName\" was not injected: check your FXML file 'View.fxml'.";
        assert lv_files != null : "fx:id=\"lv_files\" was not injected: check your FXML file 'View.fxml'.";
        assert pb_progressStatus != null : "fx:id=\"pb_progressStatus\" was not injected: check your FXML file 'View.fxml'.";
        assert rb_fileSearch != null : "fx:id=\"rb_fileSearch\" was not injected: check your FXML file 'View.fxml'.";
        assert txt_search != null : "fx:id=\"txt_search\" was not injected: check your FXML file 'View.fxml'.";


    }

}
