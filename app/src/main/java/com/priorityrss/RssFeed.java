package com.priorityrss;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nbp184 on 2016/04/28.
 */
public class RssFeed {

    private String title;
    private Drawable d;
    private String imageURL;
    private ArrayList<Item> items;

    public RssFeed() {
        title = "";
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

    public static class Item implements Comparable<Item> {
        private static final SimpleDateFormat date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

        private String title;
        private String link;
        private String description;
        private Calendar pubDate;
        private ArrayList<String[]> others;
        private RssFeed parent;
        private boolean read;

        public Item() {
            title = "";
            link = "";
            description = "";
            pubDate = null;
            others = new ArrayList<>();
            read = false;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getLink() {
            return link;
        }

        public boolean isRead() {
            return read;
        }

        public Calendar getPubDate() {
            return pubDate;
        }

        public String getFeedTitle() {
            return parent.title;
        }

        public Drawable getFeedImage() {
            return parent.d;
        }

        public String getFeedImageURL() {
            return parent.imageURL;
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

        public void setRead(boolean read) {
            this.read = read;
        }

        public void addOther(String name, String text) {
            others.add(new String[]{name, text});
        }

        public String getDisplayDescription() {
            int index = description.indexOf("<p>");
            if(index < 0) {
                return description;
            } else {
                int index2 = description.indexOf("</p>");
                return description.substring(index+3, index2);
            }
        }

        public void setPublishedDate(String s) {
            pubDate = Calendar.getInstance();
            try {
                pubDate.setTime(date.parse(s));
            } catch (ParseException e) {
                pubDate = null;
                e.printStackTrace();
            }
        }

        @Override
        public int compareTo(Item another) {
            if(pubDate == null && another.pubDate != null) {
                return another.pubDate.compareTo(pubDate);
            } else if(pubDate == null) {
                return 0;
            }
            return -pubDate.compareTo(another.pubDate);
        }

        public void setFeedImage(Drawable result) {
            parent.d = result;
        }
    }

}
