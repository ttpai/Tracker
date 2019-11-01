package com.ttpai.track.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FileName: OnMethodCall
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-27
 * Description: 注解于方法上，标明此方法被调用时触发事件
 * Track.from(MainActivity.class).onMethodCall(int.class,"getHeight")...
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface OnMethodCall {
}
