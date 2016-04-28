package com.priorityrss;

import java.util.ArrayList;

/**
 * Created by nbp184 on 2016/04/28.
 */
public class RssFeed {

    private String title;
    private ArrayList<Item> items;

    public RssFeed() {
        title = "";
        items = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public String getTextDisplay() {
        String output = title;
        for(Item item : items) {
            output += "\n\n" +item.title;
            output += "\n\tlink: " +item.link;
            output += "\n\tdescription: " +item.description;
        }
        return output;
    }

    public static class Item {
        private String title;
        private String link;
        private String description;

        public Item() {
            title = "";
            link = "";
            description = "nothing";
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
