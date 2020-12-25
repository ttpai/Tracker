package com.ttpai.track;

import android.view.View;

class AopLongClickListener implements View.OnLongClickListener {

    View.OnLongClickListener l;

    public AopLongClickListener(View.OnLongClickListener l) {
        this.l = l;
    }

    @Override
    public boolean onLongClick(View v) {
        TrackManager.getInstance().viewLongClick(v);
        return l.onLongClick(v);
    }
}