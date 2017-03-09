package view;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  private void configLogger(){
    Logger log = Logger.getLogger(getClass().getName());
    log.setLevel(Level.ALL);
//    ConsoleHandler handler = new ConsoleHandler();
//    handler.setFormatter(new SimpleFormatter());
//    handler.setLevel(Level.ALL);
//    log.addHandler(handler);
    try {
      LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("fxml/main.fxml"));
    Scene scene = new Scene(root);

    configLogger();

    primaryStage.setTitle("Search files");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
