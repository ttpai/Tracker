package com.ttpai.track;

import com.ttpai.track.callback.TrackRunnable;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * FileName: Dispatcher
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-09-25
 * Description:
 */
public final class Dispatcher {

    private ThreadPoolExecutor mExecutor;

    public Dispatcher() {

        mExecutor = new ThreadPoolExecutor(0, 1,
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        thread.setName("Track Thread");
                        return thread;
                    }
                });
    }

    public void execute(TrackRunnable runnable) {
        if (mExecutor != null)
            mExecutor.execute(runnable);
    }

    public void shoutDown() {
        if (mExecutor != null)
            mExecutor.shutdown();
        mExecutor=null;
    }
}
