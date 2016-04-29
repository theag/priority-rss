package com.priorityrss;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by nbp184 on 2016/04/29.
 */
public class ListButton extends Button {

    private int position;

    public ListButton(Context context) {
        super(context);
        init();
    }

    public ListButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        position = -1;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
