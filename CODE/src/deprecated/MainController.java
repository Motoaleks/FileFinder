///*
// * Created by Aleksandr Smilyanskiy
// * Date: 19.02.17 17:02
// * Project: FileFinder
// *
// * "The more we do, the more we can do"
// * Copyright (c) 2017.
// */
//
//package view.controllers;
//
//import index.algorithms.InvertedIndex;
//import java.io.File;
//import java.net.URL;
//import java.nio.file.Path;
//import java.util.ResourceBundle;
//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.Occurrence;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.ListView;
//import javafx.scene.control.Tab;
//import javafx.scene.control.TextArea;
//import javafx.scene.control.TextField;
//import javafx.stage.Stage;
//import search.Result;
//import search.SearchRequest;
//import view.views.PathCell;
//
///**
// * Controller for main window.
// */
//public class MainController {
//
//  InvertedIndex invertedIndex;
//
//  @FXML
//  private ResourceBundle resources;
//
//  @FXML
//  private URL location;
//
//  @FXML
//  private Button btn_Search;
//
//
//  @FXML
//  private Button btn_createIndex;
//
//  @FXML
//  private Button btn_showIndex;
//
//  @FXML
//  private CheckBox cb_searchInFile;
//
//  @FXML
//  private ListView<Path> lv_files;
//
//  @FXML
//  private TextArea ta_preview;
//
//  @FXML
//  private Tab tab_indexes;
//
//  @FXML
//  private Tab tab_search;
//
//  @FXML
//  private TextField txt_search;
//  /**
//   * Results list.
//   */
//  private ObservableList<Path> resultList;
//
//  @FXML
//  void initialize() {
//    assert btn_Search
//        != null : "fx:id=\"btn_Search\" was not injected: check your FXML file 'main.fxml'.";
//    assert cb_searchInFile
//        != null : "fx:id=\"cb_searchInFile\" was not injected: check your FXML file 'main.fxml'.";
//    assert
//        lv_files != null : "fx:id=\"lv_files\" was not injected: check your FXML file 'main.fxml'.";
//    assert ta_preview
//        != null : "fx:id=\"ta_preview\" was not injected: check your FXML file 'main.fxml'.";
//    assert tab_indexes
//        != null : "fx:id=\"tab_indexes\" was not injected: check your FXML file 'main.fxml'.";
//    assert tab_search
//        != null : "fx:id=\"tab_search\" was not injected: check your FXML file 'main.fxml'.";
//    assert txt_search
//        != null : "fx:id=\"txt_search\" was not injected: check your FXML file 'main.fxml'.";
//
//
//  }
//
//  @FXML
//  void onSearch(ActionEvent event) {
//    // getting search text pattern
//    String searchFor = txt_search.getText();
//
//    //creates searchRequest builder ('builder' design pattern)
//    SearchRequest.Builder requestBuilder = SearchRequest.getBuilder();
//    // todo: add directory adder
//    // initialize searchRequest builder with proper fields
//    invertedIndex = new InvertedIndex();
//    requestBuilder.setSearchFor(searchFor)
//                  .setSearchIn(File.listRoots()[0].getAbsolutePath())
//                  .setSearchInFiles(false)
//                  .setSearcher(invertedIndex);
//    // todo: add checking
//    // build searchRequest
//    SearchRequest searchRequest = requestBuilder.build();
//    // todo: add result saving
//    // get result reference
//    Result result = searchRequest.toIndex();
//    resultList = FXCollections.observableArrayList(result.toIndex());
//    // setting items to listview
//    lv_files.setItems(resultList);
//    // setting cell factory, initializing with special cell caching class.
//    lv_files.setCellFactory(cell -> new PathCell());
//
//    // adding result observer - to add new items when they are found.
//    result.addObserver((a1, a2) -> {
//      System.out.println("------------------------------");
//      Path founded = (Path) a2;
//      System.out.println("file found: " + founded.normalize().toString());
//      System.out.println("size: " + resultList.size());
//      System.out.println("------------------------------");
//
//      // adding objects to observerList will update the view, that's why it should be in special thread.
//      Platform.runLater(() -> {
//        resultList.add((Path) a2);
//      });
//    });
//    // execute searchRequest
//    searchRequest.execute(null);
//  }
//
//  @FXML
//  void onCreateIndex(ActionEvent event) {
//    IndexCreationController icc = new IndexCreationController();
//    Occurrence view = icc.getView();
//    Stage stage = new Stage();
//    stage.setTitle("Create index");
//    stage.setScene(new Scene((Parent) view));
//    stage.show();
//  }
//
//  @FXML
//  void onSearchChanged(ActionEvent event) {
//  }
//
//  @FXML
//  void onSearchInFileChanged(ActionEvent event) {
//  }
//}
