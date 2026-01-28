package org.pt;

import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

class Document {

    private String name;
    private TextArea area;
    private boolean modified;

    public void bindTextArea(TextArea area) {
        this.area = area;
    }

    public void open(String fileName) {
        try {
            name = fileName;
            area = new TextArea(Files.readString(new File(fileName).toPath()));
            modified = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try (FileWriter fw = new FileWriter(name)) {
            fw.write(area.getText());
            modified = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAs(String fileName) {
        name = fileName;
        save();
    }

    public boolean hasName() {
        return name != null;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        if (name == null) return "Без имени";
        return new File(name).getName();
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public String getText() {
        return area == null ? "" : area.getText();
    }
}
