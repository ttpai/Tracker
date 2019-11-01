package com.ttpai.track.callback;

/**
 * FileName: OnSubscribe
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public interface OnSubscribe<T> {
    /**
     * @Nullable
     * @param t
     */
    void call(T t);
}
