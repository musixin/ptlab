package org.pt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

class RecentList {

    private static final int MAX = 5;
    private static RecentList instance;

    private final File store = new File("recent.txt");
    private ObservableList<String> files = FXCollections.observableArrayList();

    public static RecentList getInstance() {
        if (instance == null) {
            instance = new RecentList();
        }
        return instance;
    }

    private RecentList() {
        if (!store.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(store))) {
            String line;
            while ((line = br.readLine()) != null) {
                files.add(line);
            }
        } catch (IOException ignored) {}
    }

    public void add(String path) {
        files.remove(path); // убираем дубли
        files.add(0, path); // добавляем в начало
        if (files.size() > MAX) files.remove(MAX, files.size());
    }

    public void flush() {
        try (PrintWriter pw = new PrintWriter(store)) {
            for (String f : files) {
                pw.println(f);
            }
        } catch (IOException ignored) {}
    }

    public ObservableList<String> getList() {
        return files;
    }
}
