package com.ttpai.sample;

import android.app.Application;


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

        //适合 全量埋点 的示例
        FullPointer.getInstance().startPoints(this);
//        Track.initTrack(MyApplication.this);
        //适合 单事件与长事件 的示例
        Pointer.getInstance().registEvent();

    }
}
