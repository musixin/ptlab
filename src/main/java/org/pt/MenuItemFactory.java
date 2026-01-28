package org.pt;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MenuItemFactory {

    public static List<MenuItem> fillRecentList(Editor editor, List<String> lastOpenedFiles) {
        List<MenuItem> result = new ArrayList<>();

        for (String fileName : lastOpenedFiles) {
            MenuItem item = new MenuItem(fileName);
            item.setOnAction(e -> editor.openRecentDoc(fileName));
            result.add(item);
        }

        return result;
    }

    public static MenuItem create(MenuItemType type, Editor editor, Stage stage) {
        if (type.equals(MenuItemType.SEPARATOR)) {
            return new SeparatorMenuItem();
        }

        MenuItem item = new MenuItem(type.getTitle());

        switch (type) {
            case NEW -> item.setOnAction(e -> editor.newDoc());
            case OPEN -> item.setOnAction(e -> editor.openDoc(stage));
            case SAVE -> item.setOnAction(e -> editor.saveDoc(stage));
            case SAVE_AS -> item.setOnAction(e -> editor.saveDocAs(stage));
            case CLOSE -> item.setOnAction(e -> editor.closeActiveTab());
            case EXIT -> item.setOnAction(e -> exitApplication(editor));
            default -> throw new IllegalArgumentException("Unknown menu item: " + type);
        }

        return item;
    }

    private static void exitApplication(Editor editor) {
        if (editor.closeAllTabs()) {
            System.exit(0);
        }
    }
}
