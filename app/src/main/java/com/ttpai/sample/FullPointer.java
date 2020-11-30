package com.ttpai.sample;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.util.Log;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ttpai.track.Track;

/**
 * FileName: FullPointer
 * Author: zhihao.wu@ttpai.cn
 * Date: 2020/11/12
 * Description: 全量埋点 的监听示例，
 * 1、监听 所有view 的点击
 * 2、监听 所有页面(activity+Fragment) 的 进入、退出、可见与不可见
 * 3、监听 所有 dialog/popupWindow 的show、dismiss
 * 如果有其它的事件可以参考实现
 */
public class FullPointer {

    public static final int LIFECYCLE_ENTER = 0,
            LIFECYCLE_SHOW = 1,
            LIFECYCLE_HIDE = 2,
            LIFECYCLE_EXIT = 3;


    private static FullPointer sInstance;
    private static final String TAG = "FullPointer";

    private FullPointer() {
    }

    public static FullPointer getInstance() {
        if (sInstance == null) {
            synchronized (FullPointer.class) {
                if (sInstance == null)
                    sInstance = new FullPointer();
            }
        }
        return sInstance;
    }

    public void startPoints(Application application) {

        Track.initTrack(application);

        //全view click 事件
        Track.fromAnyActivity().anyViewClick().subscribe(view -> {
            String actionId=TrackTools.getViewActionId(view);
            String pageId=TrackTools.getPageId(view);
            Object params=TrackTools.getViewParams(view);
            //插入事件
            Log.i(TAG, "anyViewClick actionId: " + actionId + " pageId=" + pageId + " map=" + params);
        });

        //页面 activity
        Track.fromAnyActivity().activityOnCreated().subscribe(activity -> {
            onPageLifeCycle(activity, LIFECYCLE_ENTER);
        }).activityOnResumed().subscribe(activity -> {
            onActivityShowHide(activity, true);
        }).activityOnPaused().subscribe(activity -> {
            onActivityShowHide(activity, false);
        }).activityOnDestroyed().subscribe(activity -> {
            onPageLifeCycle(activity, LIFECYCLE_EXIT);
            TrackTools.cleanPageParams(activity);
        });

        //fragment
        Track<?> fragmentTrack = Track.from(FragmentActivity.class);
        fragmentTrack.fragmentOnCreateView(Fragment.class).subscribe(fragment -> {
            if (fragment.getView() != null) {
                TrackTools.setViewFragmentTag(fragment.getView(), fragment);
            }
            onPageLifeCycle(fragment, LIFECYCLE_ENTER);
        }).fragmentOnDestroyed(Fragment.class).subscribe(fragment -> {
            onPageLifeCycle(fragment, LIFECYCLE_EXIT);
            TrackTools.cleanPageParams(fragment);
        });

        fragmentTrack.fragmentOnHiddenChanged(Fragment.class).subscribe(fragment ->
                onFragmentShowHide(fragment, !fragment.isHidden()));
        fragmentTrack.fragmentSetUserVisibleHint(Fragment.class).subscribe(fragment -> {
            if (fragment.isResumed()) {
                onFragmentShowHide(fragment, fragment.getUserVisibleHint());
            }
        });
        fragmentTrack.fragmentOnResumed(Fragment.class).subscribe(fragment -> {
            if (!fragment.isHidden() && fragment.getUserVisibleHint()) {
                onFragmentShowHide(fragment, true);
            }
        });
        fragmentTrack.fragmentOnPaused(Fragment.class).subscribe(fragment -> {
            if (!fragment.isHidden() && fragment.getUserVisibleHint()) {
                onFragmentShowHide(fragment, false);
            }
        });

        //any Dialog
        Track.fromAnyActivity().dialogShow(Dialog.class).subscribe(dialog -> {
            Log.d(TAG, "anyDialogShow " + dialog);

        }).dialogDismiss(Dialog.class).subscribe(dialog -> {
            Log.d(TAG, "anyDialogDismiss " + dialog);
        });
        //any popupWindow
        Track.fromAnyActivity().popupWindowShow(PopupWindow.class).subscribe(popup -> {
            Log.d(TAG, "anyPopupWindowShow " + popup);

        }).popupWindowDismiss(PopupWindow.class).subscribe(popup -> {
            Log.d(TAG, "anyPopupWindowDismiss " + popup);
        });

    }

    private void onActivityShowHide(Activity activity, boolean isShow) {
        onPageLifeCycle(activity, isShow ? LIFECYCLE_SHOW : LIFECYCLE_HIDE);
    }

    private void onFragmentShowHide(Fragment fragment, boolean isShow) {
        onPageLifeCycle(fragment, isShow ? LIFECYCLE_SHOW : LIFECYCLE_HIDE);
    }

    private void onPageLifeCycle(Object page, int lifeCycle) {
//        if (page instanceof Fragment && !TrackTools.isFragmentPage((Fragment) page)) return;//认为不是页面

        String pageId=TrackTools.getPageIdInner(page);
        Object params=TrackTools.getPageParams(page);
        Log.d(TAG, "onPageLifeCycle " + pageId + " " + page + " lifeCycle=" + lifeCycle + " params=" + params);

    }

}
