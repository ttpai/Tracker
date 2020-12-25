package com.ttpai.track;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Message;

import com.ttpai.track.callback.TrackRunnable;

/**
 * FileName: AopDismissListenerRunnable
 * Author: zhihao.wu@ttpai.cn
 * Date: 12/23/20
 * Description: 为了实现监听 dialog.onDismiss
 */

class AopDialogMessage extends TrackRunnable implements DialogInterface.OnDismissListener {
    private Message message;
    private Dialog dialog;

    private DialogInterface.OnDismissListener orig;

    public AopDialogMessage(Message message, Dialog dialog) {
        this.message = message;
        this.dialog = dialog;
    }

    public AopDialogMessage(Dialog dialog, DialogInterface.OnDismissListener orig) {
        this.orig = orig;
        this.dialog=dialog;
    }

    @Override
    public void execute() {
        if (message != null) {
            // Obtain a new message so this dialog can be re-used
            Message.obtain(message).sendToTarget();
        }
        TrackManager.getInstance().dialogDismiss(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if(orig!=null)
            orig.onDismiss(dialogInterface);
        TrackManager.getInstance().dialogDismiss(dialog);
    }


    public static boolean isAopMessage(Message message) {
        if(message==null) return false;
        return message.getCallback() instanceof AopDialogMessage || message.obj instanceof AopDialogMessage;
    }
}
