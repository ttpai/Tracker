package com.ttpai.sample;

import android.app.Application;

import com.ttpai.track.Track;


/**
 * FileName: MyApplication
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Track.initTrack(MyApplication.this);
        Pointer.getInstance().registEvent();

    }
}
