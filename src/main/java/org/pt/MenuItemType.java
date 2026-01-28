package org.pt;

public enum MenuItemType {
    NEW("New"),
    OPEN("Open"),
    SAVE("Save"),
    SAVE_AS("Save As"),
    CLOSE("Close"),
    RECENT("Recent"),
    EXIT("Exit"),

    SEPARATOR("");

    private final String title;

    MenuItemType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
