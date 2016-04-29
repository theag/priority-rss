package com.priorityrss;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by nbp184 on 2016/04/29.
 */
public class ListImageButton extends ImageButton {
    private int position;

    public ListImageButton(Context context) {
        super(context);
        init();
    }

    public ListImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
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
