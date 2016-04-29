package com.priorityrss;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.priorityrss.feeds.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by nbp184 on 2016/04/28.
 */
public class FeedItemAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private ArrayList<Item> items;
    private Calendar now;
    private boolean unreadOnly;
    private Integer defaultTextColour;

    public FeedItemAdapter(Context context) {
        this.context = context;
        now = Calendar.getInstance();
        unreadOnly = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            defaultTextColour = context.getColor(android.R.color.secondary_text_light);
        } else {
            defaultTextColour = context.getResources().getColor(android.R.color.secondary_text_light);
        }
        updateList();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        //if(view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_feed_item, null);
        //}

        TextView tv = (TextView)view.findViewById(R.id.hidden_position);
        tv.setText(""+position);

        Item item = getItem(position);

        tv = (TextView)view.findViewById(R.id.text_title);
        if(!item.isRead()) {
            tv.setTextColor(0xFF000000);
        } else {
            tv.setTextColor(0xFF737373);
        }
        tv.setText(item.getTitle());


        tv = (TextView)view.findViewById(R.id.text_description);
        tv.setText(item.getDisplayDescription());

        if(item.getFeedImage() == null) {
            new ImageGetterTask(position).execute(item.getFeedImageURL());
        } else {
            ImageView iv = (ImageView)view.findViewById(R.id.image_logo);
            iv.setImageDrawable(item.getFeedImage());
        }

        tv = (TextView)view.findViewById(R.id.text_feed_title);
        tv.setText(item.getFeedTitle());

        if(item.isStarred()) {
            view.findViewById(R.id.image_star).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.image_star).setVisibility(View.INVISIBLE);
        }

        tv = (TextView)view.findViewById(R.id.text_time);
        long diff = Math.round(Math.floor((now.getTimeInMillis() - item.getPubDate().getTimeInMillis())/1000.0/60.0));
        if(diff == 1) {
            tv.setText("1 minute ago");
        } else if(diff < 60) {
            tv.setText(diff +" minutes ago");
        } else if(diff < 120) {
            tv.setText(Math.round(Math.floor(diff/60.0)) +" hour ago");
        } else if(diff < 1440) {
            tv.setText(Math.round(Math.floor(diff/60.0)) +" hours ago");
        } else if(diff < 2880) {
            tv.setText(Math.round(Math.floor(diff/60.0/24.0)) +" day ago");
        } else if(diff < 525600) {
            tv.setText(Math.round(Math.floor(diff/60.0/24.0)) +" days ago");
        } else if(diff < 1051200) {
            tv.setText(Math.round(Math.floor(diff/60.0/24.0/365.0)) +" year ago");
        } else {
            tv.setText(Math.round(Math.floor(diff/60.0/24.0/365.0)) +" years ago");
        }
        return view;
    }

    private void setImageDrawable(int position, Drawable result) {
        getItem(position).setFeedImage(result);
        this.notifyDataSetChanged();
    }

    public void setUnreadOnly(boolean unreadOnly) {
        this.unreadOnly = unreadOnly;
        updateList();
    }

    public void updateList() {
        FeedAggregator aggregator = FeedAggregator.getInstance();
        items = aggregator.getAllItems(unreadOnly);
        notifyDataSetInvalidated();
    }

    private class ImageGetterTask extends AsyncTask<String, Void, Drawable> {

        private int position;

        public ImageGetterTask(int position) {
            this.position = position;
        }

        @Override
        protected Drawable doInBackground(String... urls) {
            InputStream in = null;
            Drawable d = null;
            try {
                in = (InputStream)new URL(urls[0]).getContent();
                d = Drawable.createFromStream(in, "src name");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }
            return d;
        }

        protected void onPostExecute(Drawable result) {
            setImageDrawable(position, result);
        }

    }

}
