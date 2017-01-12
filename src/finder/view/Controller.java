package finder.view;

import finder.search.Request;
import finder.search.Result;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Observer;
import java.util.ResourceBundle;


public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btn_Search;

    @FXML
    private ListView<?> lv_files;

    @FXML
    private RadioButton rb_searchInFile;

    @FXML
    private TextArea ta_preview;

    @FXML
    private TextField txt_search;


    @FXML
    void onSearch(ActionEvent event) {
        String searchFor = txt_search.getText();
        Request.Builder requestBuilder = Request.getBuilder();
        // todo: add directory adder
        requestBuilder.setSearchFor(searchFor)
                .setSearchIn(File.listRoots()[0].getAbsolutePath())
                .setSearchInFiles(false);
        // todo: add checking
        Request request = requestBuilder.build();
        // todo: add result saving
        Result result = request.getResult();
        result.addObserver((a1, a2)->{
            Path founded = (Path) a2;
            System.out.println("file found: " + founded.normalize().toString());
        });
        request.execute(null);
    }

    @FXML
    void onSearchChanged(ActionEvent event) {
    }

    @FXML
    void onSearchInFileChanged(ActionEvent event) {
    }

    @FXML
    void initialize() {
        assert btn_Search != null : "fx:id=\"btn_Search\" was not injected: check your FXML file 'main.fxml'.";
        assert lv_files != null : "fx:id=\"lv_files\" was not injected: check your FXML file 'main.fxml'.";
        assert rb_searchInFile != null : "fx:id=\"rb_searchInFile\" was not injected: check your FXML file 'main.fxml'.";
        assert ta_preview != null : "fx:id=\"ta_preview\" was not injected: check your FXML file 'main.fxml'.";
        assert txt_search != null : "fx:id=\"txt_search\" was not injected: check your FXML file 'main.fxml'.";


    }

}
