package com.priorityrss.feeds;

import android.graphics.drawable.Drawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/04/29.
 */
public class Item implements Comparable<Item> {
    private static final SimpleDateFormat date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static final String unit = ""+((char)31);
    private static final String nl = ""+((char)27);
    private static final String nul = ""+((char)0);

    public static Item load(String line) {
        StringTokenizer tokens = new StringTokenizer(line, unit);
        Item rv = new Item();
        rv.title = tokens.nextToken();
        rv.link = tokens.nextToken();
        rv.description = loadString(tokens.nextToken());
        rv.pubDate = loadDate(tokens.nextToken());
        rv.guid = tokens.nextToken();
        rv.read = Boolean.parseBoolean(tokens.nextToken());
        rv.favorite = Boolean.parseBoolean(tokens.nextToken());
        return rv;
    }

    private static String loadString(String s) {
        int index = s.indexOf(nl);
        while(index >= 0) {
            s = s.substring(0, index) +"\n" +s.substring(index+1);
            index = s.indexOf(nl);
        }
        return s;
    }

    private static String saveString(String s) {
        int index = s.indexOf("\n");
        while(index >= 0) {
            s = s.substring(0, index) +nl +s.substring(index+1);
            index = s.indexOf("\n");
        }
        return s;
    }

    private static Calendar loadDate(String s) {
        if(s.equalsIgnoreCase(nul)) {
            return null;
        }
        Calendar rv = Calendar.getInstance();
        try {
            rv.setTime(date.parse(s));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return rv;
    }

    private static String saveDate(Calendar cal) {
        if(cal == null) {
            return nul;
        }
        return date.format(cal.getTime());
    }

    protected String title;
    protected String link;
    protected String description;
    private Calendar pubDate;
    protected String guid;
    protected ArrayList<String[]> others;
    protected Feed parent;
    protected boolean read;
    protected boolean favorite;

    public Item() {
        title = "";
        link = "";
        description = "";
        pubDate = null;
        others = new ArrayList<>();
        read = false;
        favorite = false;
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
        return parent.getTitle();
    }

    public Drawable getFeedImage() {
        return parent.getImageDrawable();
    }

    public String getFeedImageURL() {
        return parent.getImageURL();
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

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String save() {
        return title +unit +link +unit +saveString(description) +unit +saveDate(pubDate) +unit
                +guid +unit +read +unit +favorite;
    }

    public String getFeedURL() {
        return parent.getLink();
    }

    public String getGuid() {
        return guid;
    }

    public void setStarred(boolean starred) {
        favorite = starred;
    }

    public boolean isStarred() {
        return favorite;
    }
}
