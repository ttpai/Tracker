package com.ttpai.track.annotation;

import android.content.DialogInterface;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ttpai.track.annotation.DialogButtonID.BUTTON_NEGATIVE;
import static com.ttpai.track.annotation.DialogButtonID.BUTTON_NEUTRAL;
import static com.ttpai.track.annotation.DialogButtonID.BUTTON_POSITIVE;

/**
 * FileName: DialogButtonID
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
@IntDef({BUTTON_POSITIVE, BUTTON_NEGATIVE, BUTTON_NEUTRAL})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface DialogButtonID {
    int BUTTON_POSITIVE = DialogInterface.BUTTON_POSITIVE;

    int BUTTON_NEGATIVE = DialogInterface.BUTTON_NEGATIVE;

    int BUTTON_NEUTRAL = DialogInterface.BUTTON_NEUTRAL;

}
