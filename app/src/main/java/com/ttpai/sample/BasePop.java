package com.ttpai.sample;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * <li>Package:com.ttpc.bidding_hall.widget</li>
 * <li>Author: yehaijian </li>
 * <li>Date: 2019/2/17</li>
 * <li>Description:   </li>
 */
public class BasePop extends PopupWindow {

    private Activity context;
    private float mShowAlpha = 0.5f;
    private boolean isShowAtLocation;
//    private OnDismissListener onDismissListener;

    public BasePop(Activity context) {
        this.context = context;
       /* View view = LayoutInflater.from(context).inflate(R.layout.qczj_pop, null);
        ButterKnife.bind(this, view);
        this.setContentView(view);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);*/
        this.setBackgroundDrawable(null);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        isShowAtLocation= true;
        showAnimator().start();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (isShowAtLocation){
            dismissAnimator().start();
        }
//        onDismissListener.onDismiss();
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT == 24 || Build.VERSION.SDK_INT == 25) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);// 以屏幕 左上角 为参考系的
            setHeight(getDisplayHeight(context) - rect.bottom);
        }
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT == 24 || Build.VERSION.SDK_INT == 25) {
            Rect rect = new Rect();
            anchor.getGlobalVisibleRect(rect);// 以屏幕 左上角 为参考系的
            setHeight(getDisplayHeight(context) - rect.bottom - yoff);
            yoff = 0;
        }
        super.showAsDropDown(anchor, xoff, yoff);
    }

    private ValueAnimator showAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, mShowAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        return animator;
    }


    private ValueAnimator dismissAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(mShowAlpha, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        return animator;
    }


    private void setWindowBackgroundAlpha(float alpha) {
        Window window = context.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
    }

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
//        this.onDismissListener = onDismissListener;
    }

    /**
     * 获得屏幕高
     *
     * @param context
     * @return
     */
    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }
}
