package com.ttpai.track.callback;

import com.ttpai.track.Track;

/**
 * FileName: ICopyTrack
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-09-09
 * Description:
 */
public interface Subscription<T> {
    /**
     * copy 一条的新的路径
     * @return
     */
    Track<T> copyTrack();

    /**
     * 解除订阅，以后再也不会收到事件
     */
    void unsubscribe();

    /**
     * 熄灭路径，如果当前路径已点亮，则熄灭此路径，从新开始点亮
     */
    void lightOff();


}
