package org.pt;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    private final TabPane tabPane = new TabPane();
    private final Editor editor = new Editor(this.tabPane);

    @Override
    public void start(Stage stage) {
        Menu recentMenu = new Menu(MenuItemType.RECENT.getTitle());
        ObservableList<String> recentFiles = RecentList.getInstance().getList();

        recentMenu.getItems().addAll(MenuItemFactory.fillRecentList(editor, recentFiles));
        recentFiles.addListener((javafx.collections.ListChangeListener<String>) change -> {
            recentMenu.getItems().clear();
            recentMenu.getItems().addAll(MenuItemFactory.fillRecentList(editor, recentFiles));
        });

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(
                MenuItemFactory.create(MenuItemType.NEW, editor, stage),
                MenuItemFactory.create(MenuItemType.OPEN, editor, stage),
                MenuItemFactory.create(MenuItemType.SAVE, editor, stage),
                MenuItemFactory.create(MenuItemType.SAVE_AS, editor, stage),
                MenuItemFactory.create(MenuItemType.SEPARATOR, editor, stage),
                MenuItemFactory.create(MenuItemType.CLOSE, editor, stage),
                recentMenu,
                MenuItemFactory.create(MenuItemType.SEPARATOR, editor, stage),
                MenuItemFactory.create(MenuItemType.EXIT, editor, stage)
        );

        BorderPane root = new BorderPane();
        root.setTop(new MenuBar(fileMenu));

        root.setCenter(tabPane);
        stage.setTitle("Text Editor");
        stage.setScene(new Scene(root, 900, 600));
        stage.setOnCloseRequest(e -> {
            if (!editor.closeAllTabs()) {
                e.consume();
            }
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
