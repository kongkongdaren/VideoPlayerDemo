package com.wen.asyl.videoplayerdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

/**
 * Description：xx <br/>
 * Copyright (c) 2018<br/>
 * This program is protected by copyright laws <br/>
 * Date:2018-06-12 16:19
 *
 * @author 姜文莒
 * @version : 1.0
 */
public class CustomVideoView extends VideoView {
    int  defaultWidth=1920;
    int defaultHeighe=1080;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getDefaultSize(defaultWidth,widthMeasureSpec);
        int height=getDefaultSize(defaultHeighe,heightMeasureSpec);
        Log.e("sss","width"+width+","+"height"+height);
        setMeasuredDimension(width,height);
    }
}
