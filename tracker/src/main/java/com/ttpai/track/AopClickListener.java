package com.ttpai.track;

import android.view.View;

class AopClickListener implements View.OnClickListener {

    private View.OnClickListener l;

    public AopClickListener(View.OnClickListener l) {
        this.l = l;
    }

    @Override
    public void onClick(View v) {
        TrackManager.getInstance().viewClick(v);
        l.onClick(v);
    }
}