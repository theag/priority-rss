package com.priorityrss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.priorityrss.feeds.Feed;
import com.priorityrss.feeds.FeedAggregator;

/**
 * Created by nbp184 on 2016/04/29.
 */
public class FeedAdapter  extends BaseAdapter implements ListAdapter {

    private Context context;
    private FeedAggregator aggregator;

    public FeedAdapter(Context context) {
        this.context = context;
        aggregator = FeedAggregator.getInstance();
    }

    @Override
    public int getCount() {
        return aggregator.feedCount();
    }

    @Override
    public Feed getItem(int position) {
        return aggregator.getFeed(position);
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
            view = inflater.inflate(R.layout.list_item_feed, null);
        }

        ListImageButton lib = (ListImageButton)view.findViewById(R.id.btn_remove_feed);
        lib.setPosition(position);

        TextView tv = (TextView)view.findViewById(R.id.text_title);
        tv.setText(getItem(position).getTitle());

        return view;
    }
}
