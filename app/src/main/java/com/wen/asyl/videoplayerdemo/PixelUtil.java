package com.wen.asyl.videoplayerdemo;

import android.content.Context;

/**
 * Description：像素转换累 <br/>
 * Copyright (c) 2018<br/>
 * This program is protected by copyright laws <br/>
 * Date:2018-06-11 17:32
 *
 * @author 姜文莒
 * @version : 1.0
 */
public class PixelUtil {
    private  static  Context mContext;
    public  static  void initContext(Context context){
        mContext=context;
    }
    /**
     * dp转px
     */
    public  static  int dp2dx(float value){
        final  float scale=mContext.getResources().getDisplayMetrics().densityDpi;
        return  (int)(value*(scale/160)+0.5f);
    }
}
