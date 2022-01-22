package com.commandgeek.GeekSMP;

import java.util.ArrayList;
import java.util.List;

public class ChangeLog {

    public String title;
    public List<String> items;

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
