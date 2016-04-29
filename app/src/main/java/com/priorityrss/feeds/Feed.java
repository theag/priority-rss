package com.priorityrss.feeds;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/04/28.
 */
public class Feed {

    private static final String record = ""+((char)30);

    public static Feed load(String line) {
        StringTokenizer tokens = new StringTokenizer(line, record);
        Feed rv = new Feed(tokens.nextToken());
        rv.title = tokens.nextToken();
        rv.imageURL = tokens.nextToken();
        Item item;
        while(tokens.hasMoreTokens()) {
            item = Item.load(tokens.nextToken());
            rv.items.add(item);
            item.parent = rv;
        }
        return rv;
    }

    private String title;
    private String link;
    protected Drawable d;
    private String imageURL;
    private ArrayList<Item> items;

    public Feed(String link) {
        this.link = link;
        title = "";
        imageURL = "";
        items = new ArrayList<>();
        d = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addItem(Item item) {
        for(Item mine : items) {
            if(mine.guid.equalsIgnoreCase(item.guid)) {
                return;
            }
        }
        item.parent = this;
        items.add(item);
    }

    public void setImageURL(String url) {
        this.imageURL = url;
    }

    public String getTextDisplay() {
        String output = title;
        for(Item item : items) {
            output += "\n\n" +item.title;
            output += "\n\tlink: " +item.link;
            output += "\n\tdescription: " +item.description;
            for(String[] other : item.others) {
                output += "\n\t" +other[0] +": " +other[1];
            }
        }
        return output;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Drawable getImageDrawable() {
        return d;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String save() {
        String rv = link +record +title +record +imageURL;
        for(Item item : items) {
            rv += record +item.save();
        }
        return rv;
    }

    public String getLink() {
        return link;
    }

    public void merge(Feed other) {
        for(Item otherItem : other.items) {
            addItem(otherItem);
        }
    }

    public Item getItemByGuid(String guid) {
        for(Item item : items) {
            if(item.guid.equalsIgnoreCase(guid)) {
                return item;
            }
        }
        return null;
    }

    public Item getItemByURL(String itemURL) {
        for(Item item : items) {
            if(item.link.equalsIgnoreCase(itemURL)) {
                return item;
            }
        }
        return null;
    }
}
