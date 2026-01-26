package org.pt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private final TabPane tabPane = new TabPane();
    private Editor editor = new Editor(this.tabPane);

    @Override
    public void start(Stage stage) {
        editor.loadRecentData();

        BorderPane root = new BorderPane();
        root.setTop(editor.createMenuBar(stage));
        root.setCenter(tabPane);

        stage.setTitle("Text Editor");
        stage.setScene(new Scene(root, 900, 600));
        stage.setOnCloseRequest(e -> {
            if (!editor.closeAllTabs()) e.consume();
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
