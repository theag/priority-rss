package com.priorityrss;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.priorityrss.feeds.FeedAggregator;
import com.priorityrss.feeds.Item;

public class ViewItemActivity extends AppCompatActivity {

    public static final String ARG_FEED_URL = "feed index";
    public static final String ARG_GUID = "guid";
    public static final String ARG_ITEM_URL = "item url";

    private Item feedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FeedAggregator aggregator = FeedAggregator.getInstance();
        if(getIntent().getStringExtra(ARG_GUID) != null) {
            feedItem = aggregator.getItemByGuid(getIntent().getStringExtra(ARG_FEED_URL), getIntent().getStringExtra(ARG_GUID));
        } else {
            feedItem = aggregator.getItemByURL(getIntent().getStringExtra(ARG_FEED_URL), getIntent().getStringExtra(ARG_ITEM_URL));
        }

        WebView webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(feedItem.getLink());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_item, menu);
        MenuItem item = menu.findItem(R.id.switch_star);
        item.setChecked(feedItem.isStarred());
        if(item.isChecked()) {
            item.setIcon(R.drawable.ic_star_24dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.switch_is_read:
                item.setChecked(!item.isChecked());
                if(item.isChecked()) {
                    item.setIcon(R.drawable.circle_24dp);
                } else {
                    item.setIcon(R.drawable.circle_outline_24dp);
                }
                feedItem.setRead(item.isChecked());
                return true;
            case R.id.switch_star:
                item.setChecked(!item.isChecked());
                if(item.isChecked()) {
                    item.setIcon(R.drawable.ic_star_24dp);
                } else {
                    item.setIcon(R.drawable.ic_star_outline_24dp);
                }
                feedItem.setStarred(item.isChecked());
                return true;
            case R.id.action_open_in_browser:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedItem.getLink()));
                startActivity(intent);
                return true;
            case R.id.action_share:
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, feedItem.getLink());
                intent.setType("text/plain");
                startActivity(intent);
                return true;
            case R.id.action_copy_url:
                ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", feedItem.getLink());
                clipboard.setPrimaryClip(clip);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
