package com.ttpai.track.callback;

import com.ttpai.track.TrackManager;

import java.lang.ref.WeakReference;

/**
 * FileName: OnSubscribe
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description: 可以获取 调用 的对象的订阅者
 * 配合 to/onMethodCall 操作符使用
 */
public abstract class OnTargetSubscribe<T> implements OnSubscribe<T> {

    /**
     * @return 调用的对象
     * 也许会为null
     */
    public WeakReference<Object> getTarget(){
        return TrackManager.getInstance().getTargetWeakRef();
    }

}
