package com.ttpai.track.callback;

/**
 * FileName: TrackRunnable
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-09-16
 * Description:
 */
public abstract class TrackRunnable implements Runnable {
    @Override
    public void run() {
        try {
            execute();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public abstract void execute();
}
