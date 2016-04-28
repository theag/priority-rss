package com.priorityrss;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //private TextView txtRss;
    private TextView txtProgress;
    private SwipeRefreshLayout refresh;
    private FeedItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //txtRss = (TextView)findViewById(R.id.text_rss);
        txtProgress = (TextView)findViewById(R.id.text_progress);
        refresh = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh.setRefreshing(true);
        updateFeed();
    }

    private void updateFeed() {
        try {
            URL url = new URL("http://rss.cbc.ca/lineup/topstories.xml");
            new GetRssFeedTaskv2().execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            txtProgress.setText("Sync Failed");

        } finally {
            refresh.setRefreshing(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh.setRefreshing(true);
                updateFeed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void feedItemClick(View view) {
        TextView tv = (TextView)view.findViewById(R.id.hidden_position);
        int position = Integer.parseInt(tv.getText().toString());
        RssFeed.Item item = adapter.getItem(position);
        item.setRead(true);
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(ViewItemActivity.ARG_URL, item.getLink());
        startActivity(intent);
    }

    private String getRssFeed(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        InputStream in = null;
        String output = "";
        try {
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            output = new String(response, "UTF-8");
        } catch(IOException ex) {
            ex.printStackTrace();
        } finally {
            if(in != null) {
                in.close();
            }
        }
        return output;
    }

    private RssFeed getRssFeedv2(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        InputStream in = null;
        RssFeed feed = new RssFeed();
        try {
            in = conn.getInputStream();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(in, "UTF_8");
            int eventType = xpp.getEventType();
            boolean insideItem = false;
            boolean insideImage = false;
            RssFeed.Item item = null;
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                        item = new RssFeed.Item();
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

    private void setProgress(int done, int total) {
        txtProgress.setText("Done " +done +"/" +total);
    }

    private void postFeed(String s) {
        //txtRss.setText(s);
    }

    private void postFeedv2(RssFeed feed) {
        //txtRss.setText(feed.getTextDisplay());
        adapter = new FeedItemAdapter(this, feed.getItems());
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }

    private class GetRssFeedTask extends AsyncTask<URL, Integer, String[]> {

        @Override
        protected String[] doInBackground(URL... urls) {
            int count = urls.length;
            String[] result = new String[count];
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

        protected void onProgressUpdate(Integer... progress) {
            setProgress(progress[0], progress[1]);
        }

        protected void onPostExecute(String[] result) {
            postFeed(result[0]);
        }

    }

    private class GetRssFeedTaskv2 extends AsyncTask<URL, Integer, RssFeed[]> {

        @Override
        protected RssFeed[] doInBackground(URL... urls) {
            int count = urls.length;
            RssFeed[] result = new RssFeed[count];
            for(int i = 0; i < count; i++) {
                try {
                    result[i] = getRssFeedv2(urls[i]);
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

        protected void onProgressUpdate(Integer... progress) {
            setProgress(progress[0], progress[1]);
        }

        protected void onPostExecute(RssFeed[] result) {
            postFeedv2(result[0]);
        }

    }
}
