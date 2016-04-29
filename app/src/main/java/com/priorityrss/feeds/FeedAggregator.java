package com.priorityrss.feeds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/04/29.
 */
public class FeedAggregator {

    private static FeedAggregator instance = null;

    public static boolean isLoaded() {
        return instance != null;
    }

    public static FeedAggregator load(File dir) {
        instance = new FeedAggregator();
        BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(new File(dir, "feeds.txt")));
            String line = inFile.readLine();
            while(line != null) {
                instance.feeds.add(Feed.load(line));
                line = inFile.readLine();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        } finally {
            if(inFile != null) {
                try {
                    inFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }

    public static FeedAggregator getInstance() {
        return instance;
    }

    public static void save(File dir) {
        if(instance != null) {
            PrintWriter outFile = null;
            try {
                outFile = new PrintWriter(new File(dir, "feeds.txt"));
                for(Feed feed : instance.feeds) {
                    outFile.println(feed.save());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(outFile != null) {
                    outFile.close();
                }
            }
        }
    }

    private ArrayList<Feed> feeds;

    private FeedAggregator() {
        feeds = new ArrayList<>();
    }

    public int feedCount() {
        return feeds.size();
    }

    public Feed getFeed(int index) {
        return feeds.get(index);
    }

    public void mergeFeeds(Feed[] otherFeeds) {
        boolean found;
        for(Feed other : otherFeeds) {
            found = false;
            for(Feed feed : feeds) {
                if(feed.getLink().equalsIgnoreCase(other.getLink())) {
                    feed.merge(other);
                    found = true;
                    break;
                }
            }
            if(!found) {
                feeds.add(other);
            }
        }
    }

    public ArrayList<Item> getAllItems(boolean unreadOnly) {
        ArrayList<Item> rv = new ArrayList<>();
        for(Feed feed : feeds) {
            if(unreadOnly) {
                for(Item item : feed.getItems()) {
                    if(!item.isRead()) {
                        rv.add(item);
                    }
                }
            } else {
                rv.addAll(feed.getItems());
            }
        }
        Collections.sort(rv);
        return rv;
    }

    public void removeFeed(int index) {
        feeds.remove(index);
    }

    public URL[] getFeedURLs() throws MalformedURLException {
        URL[] rv = new URL[feeds.size()];
        for(int i = 0; i < rv.length; i++) {
            rv[i] = new URL(feeds.get(i).getLink());
        }
        return rv;
    }

    public Item getItemByGuid(String feedURL, String guid) {
        for(Feed feed : feeds) {
            if(feed.getLink().equalsIgnoreCase(feedURL)) {
                return feed.getItemByGuid(guid);
            }
        }
        return null;
    }

    public Item getItemByURL(String feedURL, String itemURL) {
        for(Feed feed : feeds) {
            if(feed.getLink().equalsIgnoreCase(feedURL)) {
                return feed.getItemByURL(itemURL);
            }
        }
        return null;
    }
}
