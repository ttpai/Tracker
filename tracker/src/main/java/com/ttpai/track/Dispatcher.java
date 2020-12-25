package com.ttpai.track;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.ttpai.track.callback.TrackRunnable;

/**
 * FileName: Dispatcher
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-09-25
 * Description:
 */
public final class Dispatcher {

    private Handler mHandler;
    private HandlerThread thread;

    Dispatcher() {
        thread = new HandlerThread("Track Thread");
        thread.setDaemon(true);
        thread.start();
        mHandler= new Handler(thread.getLooper());
    }


    public void execute(TrackRunnable runnable) {
        if (mHandler != null)
            mHandler.post(runnable);
    }

    public Message obtain(TrackRunnable runnable) {
        if (mHandler != null){
            Message m = Message.obtain(mHandler,runnable);
            return m;
        }
        return null;
    }


    public void shoutDown() {
        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            thread.quitSafely();
        }else{
            thread.quit();
        }
    }
}
