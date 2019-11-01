package com.ttpai.track.callback;

/**
 * FileName: OnEvent
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public abstract class OnEvent implements OnSubscribe<Object>{
    @Override
    public final void call(Object o) {
        onEvent();
    }
    public abstract void onEvent();
}
