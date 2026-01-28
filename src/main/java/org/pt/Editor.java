package org.pt;

import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class Editor {

    private RecentList recentList = RecentList.getInstance();

    private TabPane tabPane;
    public Editor(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void newDoc() {
        Document doc = new Document();
        createTab(doc);
    }

    public void openDoc(Stage stage) {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        if (docOpened(file.getAbsolutePath())) {
            return;
        }

        Document doc = new Document();
        doc.open(file.getAbsolutePath());
        recentList.add(file.getAbsolutePath());
        createTab(doc);
    }

    public void saveDoc(Stage stage) {
        Document doc = getActiveDocument();
        if (doc == null) {
            return;
        }

        if (doc.hasName()) {
            doc.save();
        } else {
            saveDocAs(stage);
        }
        updateTabTitle();
    }

    public void saveDocAs(Stage stage) {
        Document doc = getActiveDocument();
        if (doc == null) {
            return;
        }

        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        doc.saveAs(file.getAbsolutePath());
        recentList.add(file.getAbsolutePath());
        updateTabTitle();
    }

    public void closeActiveTab() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        if (tab == null) {
            return;
        }

        Document doc = (Document) tab.getUserData();
        if (doc.isModified()) {
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Save changes?",
                    ButtonType.YES,
                    ButtonType.NO,
                    ButtonType.CANCEL
            );
            Optional<ButtonType> res = alert.showAndWait();
            if (res.isEmpty() || res.get() == ButtonType.CANCEL) {
                return;
            }
            if (res.get() == ButtonType.YES) {
                saveDoc((Stage) tabPane.getScene().getWindow());
            }
        }
        tabPane.getTabs().remove(tab);
    }

    public boolean closeAllTabs() {
        while (!tabPane.getTabs().isEmpty()) {
            tabPane.getSelectionModel().select(0);
            int before = tabPane.getTabs().size();
            closeActiveTab();
            if (tabPane.getTabs().size() == before) {
                return false;
            }
        }
        recentList.flush();
        return true;
    }

    private void createTab(Document doc) {
        TextArea area = new TextArea();
        area.setText(doc.getText());
        area.textProperty()
                .addListener(
                        (a, b, c) -> doc.setModified(true)
                );
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
        if (tab == null) {
            return;
        }
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
            if (fileName.equals(d.getName())) {
                return true;
            }
        }
        return false;
    }

    public void openRecentDoc(String fileName) {
        if (!docOpened(fileName)) {
            Document d = new Document();
            d.open(fileName);
            createTab(d);
        }
    }
}
