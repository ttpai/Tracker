package com.ttpai.track;

import android.view.View;

public class AopLongClickListener implements View.OnLongClickListener {

    View.OnLongClickListener l;

    public AopLongClickListener(View.OnLongClickListener l) {
        this.l = l;
    }

    public View.OnLongClickListener getOnLongClickener() {
        return l;
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if (id != View.NO_ID) {
            TrackManager.getInstance().viewLongClick(v);
        }
        return l.onLongClick(v);
    }
}