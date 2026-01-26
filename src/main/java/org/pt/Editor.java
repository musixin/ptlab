package org.pt;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Editor {

    private RecentList recentList = new RecentList();
    private Menu recentMenu = new Menu("Recent");
    private TabPane tabPane;

    public Editor(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void loadRecentData(){
        recentList.loadData();
    }

    public MenuBar createMenuBar(Stage stage) {
        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New");
        newItem.setOnAction(e -> newDoc());

        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(e -> openDoc(stage));

        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction(e -> saveDoc(stage));

        MenuItem saveAsItem = new MenuItem("Save As");
        saveAsItem.setOnAction(e -> saveDocAs(stage));

        MenuItem closeItem = new MenuItem("Close");
        closeItem.setOnAction(e -> closeActiveTab());

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
            if (closeAllTabs()) System.exit(0);
        });

        updateRecentMenu(stage);

        fileMenu.getItems().addAll(
                newItem, openItem, saveItem, saveAsItem,
                new SeparatorMenuItem(), closeItem,
                recentMenu,
                new SeparatorMenuItem(), exitItem
        );

        return new MenuBar(fileMenu);
    }

    private void newDoc() {
        Document doc = new Document();
        createTab(doc);
    }

    private void openDoc(Stage stage) {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(stage);
        if (file == null) return;

        if (docOpened(file.getAbsolutePath())) return;

        Document doc = new Document();
        doc.open(file.getAbsolutePath());
        recentList.add(file.getAbsolutePath());
        updateRecentMenu(stage);
        createTab(doc);
    }

    private void saveDoc(Stage stage) {
        Document doc = getActiveDocument();
        if (doc == null) return;

        if (doc.hasName()) {
            doc.save();
        } else {
            saveDocAs(stage);
        }
        updateTabTitle();
    }

    private void saveDocAs(Stage stage) {
        Document doc = getActiveDocument();
        if (doc == null) return;

        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(stage);
        if (file == null) return;

        doc.saveAs(file.getAbsolutePath());
        recentList.add(file.getAbsolutePath());
        updateRecentMenu(stage);
        updateTabTitle();
    }




    private void closeActiveTab() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null) return;

        Document doc = (Document) tab.getUserData();
        if (doc.isModified()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Save changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isEmpty() || res.get() == ButtonType.CANCEL) return;
            if (res.get() == ButtonType.YES) saveDoc((Stage) tabPane.getScene().getWindow());
        }
        tabPane.getTabs().remove(tab);
    }

    public boolean closeAllTabs() {
        while (!tabPane.getTabs().isEmpty()) {
            tabPane.getSelectionModel().select(0);
            int before = tabPane.getTabs().size();
            closeActiveTab();
            if (tabPane.getTabs().size() == before) return false;
        }
        recentList.saveData();
        return true;
    }

    private void createTab(Document doc) {
        TextArea area = new TextArea();
        area.setText(doc.getText());
        area.textProperty().addListener((a, b, c) -> doc.setModified(true));
        doc.bindTextArea(area);

        Tab tab = new Tab(doc.getShortName(), area);
        tab.setUserData(doc);
        tab.setOnCloseRequest(e -> {
            tabPane.getSelectionModel().select(tab);
            closeActiveTab();
            e.consume();
        });

        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void updateTabTitle() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null) return;
        Document doc = (Document) tab.getUserData();
        tab.setText(doc.getShortName());
    }

    private Document getActiveDocument() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        return tab == null ? null : (Document) tab.getUserData();
    }

    private boolean docOpened(String fileName) {
        for (Tab t : tabPane.getTabs()) {
            Document d = (Document) t.getUserData();
            if (fileName.equals(d.getName())) return true;
        }
        return false;
    }

    private void updateRecentMenu(Stage stage) {
        recentMenu.getItems().clear();
        List<String> list = recentList.getFiles();
        for (int i = 0; i < list.size(); i++) {
            final int idx = i;
            MenuItem item = new MenuItem(list.get(i));
            item.setOnAction(e -> {
                String f = recentList.getFiles().get(idx);
                if (!docOpened(f)) {
                    Document d = new Document();
                    d.open(f);
                    createTab(d);
                }
            });
            recentMenu.getItems().add(item);
        }
    }
}
