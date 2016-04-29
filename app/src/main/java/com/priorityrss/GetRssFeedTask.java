package com.priorityrss;

import android.os.AsyncTask;

import com.priorityrss.feeds.Feed;
import com.priorityrss.feeds.FeedAggregator;
import com.priorityrss.feeds.Item;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nbp184 on 2016/04/29.
 */
public abstract class GetRssFeedTask extends AsyncTask<URL, Integer, Feed[]> {

    @Override
    protected Feed[] doInBackground(URL... urls) {
        int count = urls.length;
        Feed[] result = new Feed[count];
        for(int i = 0; i < count; i++) {
            try {
                result[i] = getRssFeed(urls[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.publishProgress(i+1, count);
            if(isCancelled()) {
                break;
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Feed[] result) {
        FeedAggregator.getInstance().mergeFeeds(result);
        onDone();
    }

    protected abstract void onDone();

    @Override
    protected abstract void onProgressUpdate(Integer... values);

    private Feed getRssFeed(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        InputStream in = null;
        Feed feed = new Feed(url.toString());
        try {
            in = conn.getInputStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(in, "UTF_8");
            int eventType = xpp.getEventType();
            boolean insideItem = false;
            boolean insideImage = false;
            Item item = null;
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                        item = new Item();
                    } else if(xpp.getName().equalsIgnoreCase("title")) {
                        if(insideItem) {
                            item.setTitle(xpp.nextText());
                        } else if(!insideImage) {
                            feed.setTitle(xpp.nextText());
                        }
                    } else if(xpp.getName().equalsIgnoreCase("image") && !insideItem) {
                        insideImage = true;
                    } else if(xpp.getName().equalsIgnoreCase("url") && insideImage) {
                        feed.setImageURL(xpp.nextText());
                    } else if(xpp.getName().equalsIgnoreCase("link") && insideItem) {
                        item.setLink(xpp.nextText());
                    } else if(xpp.getName().equalsIgnoreCase("description") && insideItem) {
                        item.setDescription(xpp.nextText());
                    } else if(xpp.getName().equalsIgnoreCase("pubDate") && insideItem) {
                        item.setPublishedDate(xpp.nextText());
                    } else if(xpp.getName().equalsIgnoreCase("guid") && insideItem) {
                        item.setGuid(xpp.nextText());
                    } else if(insideItem) {
                        item.addOther(xpp.getName(), xpp.nextText());
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    if(xpp.getName().equalsIgnoreCase("item")) {
                        feed.addItem(item);
                        item = null;
                        insideItem = false;
                    } else if(xpp.getName().equalsIgnoreCase("image")) {
                        insideImage = false;
                    }
                }
                eventType = xpp.next();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            if(in != null) {
                in.close();
            }
        }
        return feed;
    }

}
