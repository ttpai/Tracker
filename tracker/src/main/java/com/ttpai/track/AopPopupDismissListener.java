package com.ttpai.track;

import android.widget.PopupWindow;

/**
 * FileName: AopPopupDismissListener
 * Author: zhihao.wu@ttpai.cn
 * Date: 12/23/20
 * Description: 为了监听 popup.onDismiss
 */
class AopPopupDismissListener implements PopupWindow.OnDismissListener {

    private PopupWindow.OnDismissListener orig;
    private PopupWindow popup;

    public AopPopupDismissListener(PopupWindow.OnDismissListener orig, PopupWindow popup) {
        this.orig = orig;
        this.popup = popup;
    }

    @Override
    public void onDismiss() {
        if(orig!=null){
            orig.onDismiss();
        }
        TrackManager.getInstance().popupDismiss(popup);
    }

    public static boolean isAopDismiss(PopupWindow.OnDismissListener listener){
        return listener instanceof AopPopupDismissListener;
    }
}
