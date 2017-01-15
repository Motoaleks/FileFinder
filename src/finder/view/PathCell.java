/*
 * Created by: Aleksandr
 * Date: 15.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
package finder.view;

import java.nio.file.Path;
import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 * Cell for representing needed path fields.
 */
public class PathCell extends ListCell<Path> {

  /**
   * Controller for cell.
   */
  private final PathCellController pathCellController;
  /**
   * Caching node, for not creating a new every time.
   */
  private final Node view;

  /**
   * Initializing cell, without actual creating (loading) controller & view.
   */
  public PathCell() {
    pathCellController = new PathCellController();
    view = pathCellController.getView();

    //Setting hover elements
    this.setOnMouseEntered(event -> {
      pathCellController.setHovered(true).update();
    });
    this.setOnMouseExited(event -> {
      pathCellController.setHovered(false).update();
    });
  }

  /**
   * Updating item (loading if not load, otherwise just update fields).
   */
  @Override
  protected void updateItem(Path item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setGraphic(null);
    } else {
      pathCellController.setItem(item);
      pathCellController.update();
      setGraphic(view);
    }
  }

  @Override
  public void updateSelected(boolean selected) {
    super.updateSelected(selected);
    if (pathCellController != null) {
      pathCellController.setSelected(selected).update();
    }
  }
}
