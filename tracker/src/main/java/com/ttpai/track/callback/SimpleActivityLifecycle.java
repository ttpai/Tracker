package com.ttpai.track.callback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * FileName: SimpleActivityLifecycle
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019/8/1
 * Description:
 */
public abstract class SimpleActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
