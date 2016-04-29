package com.priorityrss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.priorityrss.feeds.FeedAggregator;

import java.net.MalformedURLException;
import java.net.URL;

public class FeedControlActivity extends AppCompatActivity implements MyOptionPane.OnClickListener {

    private static final String DIALOG_CONFIRM_REMOVE = "confirm remove dialog";

    private int position;
    private FeedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_control);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new FeedAdapter(this);
        ListView lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }

    public void buttonClick(View view) {
        switch(view.getId()) {
            case R.id.btn_add_feed:
                EditText et = (EditText)findViewById(R.id.edit_new_url);
                URL url = null;
                try {
                    url = new URL(et.getText().toString());
                    new GetRssFeedTask() {
                        @Override
                        protected void onDone() {
                            updateListView();
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {

                        }
                    }.execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    MyOptionPane.showMessageDialog(getSupportFragmentManager(), "dialog", "\"" +et.getText().toString() +"\" is not a valid URL.", "Adding Feed");
                }
                break;
            case R.id.btn_remove_feed:
                ListImageButton lib = (ListImageButton)view;
                position = lib.getPosition();
                MyOptionPane.showQuestionDialog(getSupportFragmentManager(), DIALOG_CONFIRM_REMOVE,
                        "Are you sure you wish to remove \"" +FeedAggregator.getInstance().getFeed(position).getTitle() +"\"?",
                        "Removing Feed", MyOptionPane.YES_NO_OPTION);
                break;
        }
    }

    private void updateListView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onOptionPaneClick(String tag, int returnValue) {
        switch(tag) {
            case DIALOG_CONFIRM_REMOVE:
                FeedAggregator.getInstance().removeFeed(position);
                updateListView();
                break;
        }
    }
}
