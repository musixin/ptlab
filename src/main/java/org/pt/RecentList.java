package org.pt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class RecentList {
    private static final int MAX = 5;
    private List<String> files = new ArrayList<>();
    private final File store = new File("recent.txt");

    public void add(String fileName) {
        files.remove(fileName);
        files.add(0, fileName);
        if (files.size() > MAX) files = files.subList(0, MAX);
    }

    public List<String> getFiles() {
        return files;
    }

    public void saveData() {
        try (PrintWriter pw = new PrintWriter(store)) {
            for (String f : files) pw.println(f);
        } catch (IOException ignored) {}
    }

    public void loadData() {
        if (!store.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(store))) {
            String line;
            while ((line = br.readLine()) != null) files.add(line);
        } catch (IOException ignored) {}
    }
}
