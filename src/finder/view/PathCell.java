/*
 * Created by: Aleksandr
 * Date: 12.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
package finder.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * Cell controller.
 */
public class PathCell extends ListCell<Path> {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    /**
     * Global pane.
     */
    @FXML
    private AnchorPane ap_cell;

    /**
     * Thumbnail imageview.
     */
    @FXML
    private ImageView iv_thumbnail;

    /**
     * Created at label.
     */
    @FXML
    private Label lb_created;

    /**
     * File path label.
     */
    @FXML
    private Label lb_name;

    /**
     * File size label.
     */
    @FXML
    private Label lb_size;


    /**
     * Creates cell.
     */
    public PathCell() {
        configureFile();
    }

    /**
     * Setting short name to file name field (just filename).
     */
    private void setShortName() {
        if (getItem() != null) {
            lb_name.setText(getItem().getFileName().normalize().toString());
        }
    }

    /**
     * Setting full name to file name field (full path).
     */
    private void setExpandedName() {
        if (getItem() != null) {
            lb_name.setText(getItem().normalize().toString());
        }
    }

    /**
     * Mouse enter on cell.
     * @param event
     */
    @FXML
    void onElementEntered(MouseEvent event) {
        setExpandedName();
    }

    /**
     * Mouse exit from cell.
     * @param event
     */
    @FXML
    void onElementExited(MouseEvent event) {
        setShortName();
    }

    /**
     * Sets and asserts fxml values.
     */
    @FXML
    void initialize() {
        assert ap_cell != null : "fx:id=\"ap_cell\" was not injected: check your FXML file 'listCell.fxml'.";
        assert iv_thumbnail != null : "fx:id=\"iv_thumbnail\" was not injected: check your FXML file 'listCell.fxml'.";
        assert lb_created != null : "fx:id=\"lb_created\" was not injected: check your FXML file 'listCell.fxml'.";
        assert lb_name != null : "fx:id=\"lb_name\" was not injected: check your FXML file 'listCell.fxml'.";
        assert lb_size != null : "fx:id=\"lb_size\" was not injected: check your FXML file 'listCell.fxml'.";
    }

    /**
     * Loading cell file.
     */
    private void configureFile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("listCell.fxml"));
            loader.setController(this);
            loader.load();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * On cell update - sets all fields.
     */
    @Override
    protected void updateItem(Path item, boolean empty) {
        // needed for correct listview updating (flickering bug).
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            Platform.runLater(() -> {
                iv_thumbnail.setImage(null);
                setText(null);
                setGraphic(ap_cell);
                // update name field
                if (isSelected()) {
                    setExpandedName();
                } else {
                    setShortName();
                }
            });
        }
    }

    private Image getThumbnail(Path file) {
        // todo: make thumbnail image
        return null;
    }
}
