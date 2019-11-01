package com.ttpai.track.callback;

/**
 * FileName: OnSubscribe
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public abstract class OnTrackSubscribe<T> implements OnSubscribe<T>{

    public String getTrackName(){
        return null;
    }
}
