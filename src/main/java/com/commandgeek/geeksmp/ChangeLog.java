package com.commandgeek.geeksmp;

import java.util.ArrayList;
import java.util.List;

public class ChangeLog {

    public final String title;
    public final List<String> items;

    public ChangeLog() {
        this.title = Main.instance.getDescription().getVersion();

        items = new ArrayList<>();
    }

    public void addItem(String string) {
        items.add(string);
    }

    public void removeLastItem() {
        items.remove(items.size() - 1);
    }
}
