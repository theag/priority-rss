package com.priorityrss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.xml.sax.XMLReader;

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
    private ArrayList<RssFeed.Item> items;
    private Calendar now;

    public FeedItemAdapter(Context context, ArrayList<RssFeed.Item> items) {
        this.context = context;
        this.items = new ArrayList<>();
        this.items.addAll(items);
        Collections.sort(this.items);
        now = Calendar.getInstance();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public RssFeed.Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_feed_item, null);
        }

        TextView tv = (TextView)view.findViewById(R.id.hidden_position);
        tv.setText(""+position);

        RssFeed.Item item = getItem(position);

        tv = (TextView)view.findViewById(R.id.text_title);
        tv.setText(item.getTitle());
        if(!item.isRead()) {
            tv.setTextColor(0xFF000000);
        }

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

        tv = (TextView)view.findViewById(R.id.text_time);
        long diff = Math.round(Math.floor((now.getTimeInMillis() - item.getPubDate().getTimeInMillis())/1000.0/60.0));
        if(diff == 1) {
            tv.setText("1 minute ago");
        } else if(diff < 60) {
            tv.setText(diff +" minutes ago");
        } else if(diff < 120) {
            tv.setText(Math.round(Math.floor(diff/60.0)) +" hour ago");
        } else {
            tv.setText(Math.round(Math.floor(diff/60.0)) +" hours ago");
        }
        return view;
    }

    private void setImageDrawable(int position, Drawable result) {
        getItem(position).setFeedImage(result);
        this.notifyDataSetChanged();
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
