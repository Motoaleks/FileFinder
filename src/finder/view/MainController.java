package finder.view;

import finder.search.Request;
import finder.search.Result;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * Controller for main window.
 */
public class MainController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btn_Search;

    @FXML
    private CheckBox cb_searchInFile;

    @FXML
    private ListView<Path> lv_files;

    @FXML
    private TextArea ta_preview;

    @FXML
    private Tab tab_indexes;

    @FXML
    private Tab tab_search;

    @FXML
    private TextField txt_search;
    /**
     * Results list.
     */
    private ObservableList<Path> resultList;

    @FXML
    void initialize() {
        assert btn_Search != null : "fx:id=\"btn_Search\" was not injected: check your FXML file 'main.fxml'.";
        assert cb_searchInFile != null : "fx:id=\"cb_searchInFile\" was not injected: check your FXML file 'main.fxml'.";
        assert lv_files != null : "fx:id=\"lv_files\" was not injected: check your FXML file 'main.fxml'.";
        assert ta_preview != null : "fx:id=\"ta_preview\" was not injected: check your FXML file 'main.fxml'.";
        assert tab_indexes != null : "fx:id=\"tab_indexes\" was not injected: check your FXML file 'main.fxml'.";
        assert tab_search != null : "fx:id=\"tab_search\" was not injected: check your FXML file 'main.fxml'.";
        assert txt_search != null : "fx:id=\"txt_search\" was not injected: check your FXML file 'main.fxml'.";


    }

    @FXML
    void onSearch(ActionEvent event) {
        // getting search text pattern
        String searchFor = txt_search.getText();

        //creates request builder ('builder' design pattern)
        Request.Builder requestBuilder = Request.getBuilder();
        // todo: add directory adder
        // initialize request builder with proper fields
        requestBuilder.setSearchFor(searchFor)
                .setSearchIn(File.listRoots()[0].getAbsolutePath())
                .setSearchInFiles(false);
        // todo: add checking
        // build request
        Request request = requestBuilder.build();
        // todo: add result saving
        // get result reference
        Result result = request.getResult();
        resultList = FXCollections.observableArrayList(result.getResult());
        // setting items to listview
        lv_files.setItems(resultList);
        // set custom cell factory
        lv_files.setCellFactory(pathCell -> new PathCell());
        // adding result observer - to add new items when they are found.
        result.addObserver((a1, a2) -> {
            System.out.println("------------------------------");
            Path founded = (Path) a2;
            System.out.println("file found: " + founded.normalize().toString());
            System.out.println("size: " + resultList.size());
            System.out.println("------------------------------");
            resultList.add((Path) a2);
        });
        // execute request
        request.execute(null);
    }

    @FXML
    void onSearchChanged(ActionEvent event) {
    }

    @FXML
    void onSearchInFileChanged(ActionEvent event) {
    }
}