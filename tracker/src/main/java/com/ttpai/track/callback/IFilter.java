package com.ttpai.track.callback;

/**
 * FileName: IFilter
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-09-12
 * Description: 过滤器
 */
public interface IFilter<T> {
    boolean filter(T t);
}
