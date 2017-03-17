package view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.controllers.MainController;

public class Main extends Application {

  private static String MAIN_FXML = "fxml/main.fxml";

  public static void main(String[] args) {
    launch(args);
  }

  private void configLogger() {
    Logger log = Logger.getLogger(getClass().getName());
    log.setLevel(Level.ALL);
//    ConsoleHandler handler = new ConsoleHandler();
//    handler.setFormatter(new SimpleFormatter());
//    handler.setLevel(Level.ALL);
//    log.addHandler(handler);
    try {
      LogManager.getLogManager()
                .readConfiguration(getClass().getResourceAsStream("/logging.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_FXML));
    Parent root = loader.load();
    Scene scene = new Scene(root);

    MainController controller = loader.getController();
    primaryStage.setOnShown(event -> {
      controller.loadIndexes();
    });
    primaryStage.setOnCloseRequest(event -> {
      controller.saveIndexes();
    });

    configLogger();

    primaryStage.setTitle("Search files");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
