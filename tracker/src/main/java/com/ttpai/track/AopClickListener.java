package com.ttpai.track;

import android.view.View;

public class AopClickListener implements View.OnClickListener {

    View.OnClickListener l;

    public AopClickListener(View.OnClickListener l) {
        this.l = l;
    }

    public View.OnClickListener getOnClickLiener(){
        return l;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id != View.NO_ID) {
            TrackManager.getInstance().viewClick(v);
        }
        l.onClick(v);
    }
}