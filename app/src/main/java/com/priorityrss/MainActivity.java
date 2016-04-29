package com.priorityrss;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.priorityrss.feeds.*;

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

    private static final String PREF_UNREAD_ONLY = "unread only preference";
    private TextView txtProgress;
    private SwipeRefreshLayout refresh;
    private FeedItemAdapter adapter;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtProgress = (TextView)findViewById(R.id.text_progress);
        refresh = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateFeeds();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FeedAggregator.save(getFilesDir());
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_UNREAD_ONLY, menu.findItem(R.id.switch_unread_only).isChecked());
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh.setRefreshing(true);
        updateFeeds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        MenuItem item = menu.findItem(R.id.switch_unread_only);
        item.setChecked(sharedPreferences.getBoolean(PREF_UNREAD_ONLY, false));
        if(item.isChecked()) {
            item.setIcon(R.drawable.circle_24dp);
        }
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_unread_only:
                item.setChecked(!item.isChecked());
                if(item.isChecked()) {
                    item.setIcon(R.drawable.circle_24dp);
                } else {
                    item.setIcon(R.drawable.circle_outline_24dp);
                }
                adapter.setUnreadOnly(item.isChecked());
                return true;
            case R.id.action_edit_feeds:
                Intent intent = new Intent(this, FeedControlActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_refresh:
                refresh.setRefreshing(true);
                updateFeeds();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateFeeds() {
        FeedAggregator aggregator;
        if(FeedAggregator.isLoaded()) {
            aggregator = FeedAggregator.getInstance();
        } else {
            aggregator = FeedAggregator.load(getFilesDir());
        }
        try {
            new GetRssFeedTask() {
                @Override
                protected void onDone() {
                    loadFeeds();
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    setProgress(values[0], values[1]);
                }
            }.execute(aggregator.getFeedURLs());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            txtProgress.setText("Sync Failed");
        } finally {
            refresh.setRefreshing(false);
        }
    }

    public void feedItemClick(View view) {
        TextView tv = (TextView) view.findViewById(R.id.hidden_position);
        int position = Integer.parseInt(tv.getText().toString());
        Item item = adapter.getItem(position);
        item.setRead(true);
        Intent intent = new Intent(this, ViewItemActivity.class);
        intent.putExtra(ViewItemActivity.ARG_FEED_URL, item.getFeedURL());
        if(item.getGuid() != null) {
            intent.putExtra(ViewItemActivity.ARG_GUID, item.getGuid());
        } else {
            intent.putExtra(ViewItemActivity.ARG_ITEM_URL, item.getLink());
        }
        startActivity(intent);
    }

    private void setProgress(int done, int total) {
        txtProgress.setText("Done " +done +"/" +total);
    }

    private void loadFeeds() {
        if(adapter == null) {
            adapter = new FeedItemAdapter(this);
            adapter.setUnreadOnly(menu.findItem(R.id.switch_unread_only).isChecked());
            ListView lv = (ListView)findViewById(R.id.listView);
            lv.setAdapter(adapter);
        } else {
            adapter.updateList();
        }
    }
}
