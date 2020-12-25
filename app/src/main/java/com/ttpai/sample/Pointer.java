package com.ttpai.sample;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ttpai.sample.fragment.MainFragment;
import com.ttpai.sample.fragment.TabFragment;
import com.ttpai.sample.fragment.TabFragmentActivity;
import com.ttpai.track.Track;
import com.ttpai.track.annotation.DialogButtonID;
import com.ttpai.track.callback.IFilter;
import com.ttpai.track.callback.OnEvent;
import com.ttpai.track.callback.OnMainThreadSubscribe;
import com.ttpai.track.callback.OnSubscribe;

import java.util.Arrays;

/**
 * FileName: Pointer
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description: 集中埋点类
 */

public class Pointer {

    private static Pointer sInstance;
    private String TAG = "Pointer";

    private Pointer() {

    }

    public static Pointer getInstance() {
        if (sInstance == null) {
            synchronized (Pointer.class) {
                if (sInstance == null)
                    sInstance = new Pointer();
            }
        }
        return sInstance;
    }


    public void registEvent() {


        Track.from(AActivity.class).to(BActivity.class).subscribe(new OnEvent() {
            @Override
            public void onEvent() {
                Log.d(TAG, "A->B");

            }
        }).viewClick(R.id.button).subscribe(new OnEvent() {
            @Override
            public void onEvent() {
                Log.d(TAG, "A->B.c(R.id.button3)");

            }
        }).to(CActivity.class).subscribe(new OnEvent() {
            @Override
            public void onEvent() {
                Log.d(TAG, "A->B.c(R.id.button3)->C");
            }
        }).viewClick(R.id.button).subscribe(new OnEvent() {
            @Override
            public void onEvent() {
                Log.d(TAG, "A->B.c(R.id.button3)->C.c(R.id.button)");
            }
        }).subscribe(new OnEvent() {
            @Override
            public void onEvent() {
                Log.d(TAG, "A->B.c(R.id.button3)->C.c(R.id.button) 2");
            }
        });

        Track.from(AActivity.class).fragmentOnHiddenChanged(MainFragment.class).filter(new IFilter<Fragment>() {
            @Override
            public boolean filter(Fragment fragment) {
                return !fragment.isHidden();
            }
        });

        Track<Intent> track = Track.from(AActivity.class).to(BActivity.class).subscribe(new com.ttpai.track.callback.OnTargetSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, "A->B subscribe intent=" + intent + " t=" + Thread.currentThread() + " target=" + getTarget().get());

            }
        });
//        track.unsubscribe();
        track.dialogShow(Dialog.class).subscribe(new OnMainThreadSubscribe<Dialog>() {
            @Override
            public void call(Dialog dialog) {
                Log.d(TAG, "A->B.dialogShow dialog=" + dialog + " t=" + Thread.currentThread());
            }
        });

        track.dialogButtonClick(DialogButtonID.BUTTON_POSITIVE).subscribe(new OnSubscribe<Dialog>() {
            @Override
            public void call(Dialog dialog) {
                Log.d(TAG, "A->B.dialogShow.positiveClick dialog=" + dialog + " t=" + Thread.currentThread());
            }
        });
        //这里对两个按钮分别监听
        track.dialogButtonClick(DialogButtonID.BUTTON_NEGATIVE).subscribe(new OnSubscribe<Dialog>() {
            @Override
            public void call(Dialog dialog) {
                Log.d(TAG, "A->B.dialogShow.negativeClick dialog=" + dialog + " t=" + Thread.currentThread());

            }
        });
        track.dialogDismiss(AlertDialog.class).subscribe(new OnSubscribe<Dialog>() {
            @Override
            public void call(Dialog dialog) {
                Log.d(TAG, "A->B.dialogDismiss dialog=" + dialog + " t=" + Thread.currentThread());
            }
        });

        Track.from(AActivity.class).viewClick(R.id.button).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A.c =" + view + " t=" + Thread.currentThread());
            }
        }).to(BActivity.class).subscribe(new OnEvent() {
            @Override
            public void onEvent() {
                Log.d(TAG, "A.c->B =" + Thread.currentThread());
            }
        }).viewClick(R.id.button).to(CActivity.class).subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, "A.c->B.c->C view:" + intent + " t=" + Thread.currentThread());
            }
        });

        Track.from(AActivity.class).to(BActivity.class).viewClick(R.id.button).to(CActivity.class).subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent view) {
                Log.d(TAG, "A->B.c->C view:" + view + " t=" + Thread.currentThread());
            }
        });
        Track.from(AActivity.class).viewLongClick(R.id.button5).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A.viewLongClick(R.id.button5) v:" + view + " t=" + Thread.currentThread());
            }
        });
        /*Track.from(AActivity.class).viewClick(R.id.tv_text).viewVisibility(R.id.tv_text).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A.viewClick(R.id.tv_text).viewVisibility(R.id.tv_text) view:" + view.getVisibility() + " v:" + view + " t=" + Thread.currentThread());
            }
        });
        Track.from(AActivity.class).viewVisibility(R.id.tv_text).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A.viewVisibility(R.id.tv_text) view:" + view.getVisibility() + " v:" + view + " t=" + Thread.currentThread());
            }
        });*/
        Track.fromAnyActivity().toAnyActivity().subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, "Any->Any intent:" + intent + " t=" + Thread.currentThread());
            }
        });
        Track.from(AActivity.class).toAnyActivity().subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, "A->Any intent:" + intent + " t=" + Thread.currentThread());
            }
        });
        Track.from(AActivity.class).viewClick(R.id.button3).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A.viewClick(R.id.button3) view: " + view + " t=" + Thread.currentThread());
            }
        });

        Track.fromObject(AActivity.class).onMethodCall("setVisibili", TextView.class)
                .subscribe(new OnSubscribe<Object[]>() {
                    @Override
                    public void call(Object[] objects) {
                        Log.d(TAG, "AActivity onMethodCall.setVisibili args:" + Arrays.toString(objects));

                    }
                });

       Track<Intent> trackB= Track.from(AActivity.class).to(CActivity.class);
       trackB.subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, "AActivity->CActivity args:" + intent);

            }
        });
        Track.from(Activity.class).onMethodCall("onClick", View.class)
                .subscribe(new OnSubscribe<Object[]>() {
            @Override
            public void call(Object[] objects) {
                Log.d(TAG, "OnClickListener.onClick " +objects);
            }
        });

        Track.from(AActivity.class).to(BActivity.class).activityOnResumed().activityOnDestroyed().viewClick(R.id.button2).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A->B.resume->destroy->click :" + view);
            }
        });

        Track.from(AActivity.class).to(BActivity.class)
                .viewClick(R.id.button2).activityOnDestroyed()
                .viewClick(R.id.button2).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A->B.resume->destroy->click :" + view);
            }
        }).to(CActivity.class).activityOnResumed().activityOnDestroyed().viewClick(R.id.button3).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A->B.resume.destroy->click->C.resume.destroy->click :" + view);
            }
        });


//        Track<Intent> toSecondTrack = Track.from(AActivity.class).to(BActivity.class);
//        Track<?> toSecondTrack = Track.from(AActivity.class);
        Track<?> toSecondTrack = Track.fromAnyActivity();
        toSecondTrack.activityOnCreated().subscribe(new OnSubscribe<Activity>() {
            @Override
            public void call(Activity activity) {
                Log.d(TAG, "A->B.onCreate " + activity);
            }
        });
  /*      toSecondTrack.activityOnStarted().subscribe(new OnSubscribe<Activity>() {
            @Override
            public void call(Activity activity) {
                Log.d(TAG, "A->B.onActivityStarted " + activity);
            }
        });

        toSecondTrack.activityOnResumed().subscribe(new OnSubscribe<Activity>() {
            @Override
            public void call(Activity activity) {
                Log.d(TAG, "A->B.onActivityResumed " + activity);
            }
        });
        toSecondTrack.activityOnPaused().subscribe(new OnSubscribe<Activity>() {
            @Override
            public void call(Activity activity) {
                Log.d(TAG, "A->B.onActivityPaused " + activity);
            }
        });
        toSecondTrack.activityOnStoped().subscribe(new OnSubscribe<Activity>() {
            @Override
            public void call(Activity activity) {
                Log.d(TAG, "A->B.onActivityStoped " + activity);
            }
        });

        toSecondTrack.activityOnDestroyed().subscribe(new OnSubscribe<Activity>() {
            @Override
            public void call(Activity activity) {
                Log.d(TAG, "A->B.onActivityDestroyed " + activity);
            }
        });
        toSecondTrack.activityOnSaveInstanceState().subscribe(new OnSubscribe<Activity>() {
            @Override
            public void call(Activity activity) {
                Log.d(TAG, "A->B.onActivitySaveInstanceState " + activity);
            }
        });*/
        Track.from(AActivity.class).onMethodCall( "getHeight").subscribe(new OnSubscribe<Object[]>() {
            @Override
            public void call(Object[] objects) {
                Log.d(TAG, "AActivity onMethodCall.getHeight args:" + Arrays.toString(objects));

            }
        });

        Track.fromObject(View.OnClickListener.class).onMethodCall("onClick", View.class).subscribe(new OnSubscribe<Object[]>() {
            @Override
            public void call(Object[] args) {
                Log.d(TAG, "AActivity OnClickListener args:" + Arrays.toString(args));
            }
        });

        track.activityFinish().subscribe(new OnSubscribe<Activity>() {
            @Override
            public void call(Activity activity) {
                Log.d(TAG, "A->B.finish :" + activity);
            }
        });

        Track.fromApplication().to(CActivity.class).subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, "Application->B :" + intent);
            }
        });

        Track.from(AActivity.class).popupWindowShow(PopupWindow.class).subscribe(new OnSubscribe<PopupWindow>() {
            @Override
            public void call(PopupWindow popupWindow) {
                Log.d(TAG, "A->onPopupWindowShow :" + popupWindow);
            }
        }).viewClick(R.id.bt_query).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "A->onPopupWindowShow.click(bt_query) :" + view);
            }
        });

        Track.from(AActivity.class).popupWindowDismiss(PopupWindow.class).subscribe(new OnSubscribe<PopupWindow>() {
            @Override
            public void call(PopupWindow popupWindow) {
                Log.d(TAG, "A->onPopupWindowDismiss :" + popupWindow);
            }
        });

        Track<?> frgActFrom = Track.from(TabFragmentActivity.class);
        frgActFrom.fragmentOnCreate(MainFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnCreate :" + fragment);
            }
        }).viewClick(R.id.text).subscribe(new OnSubscribe<View>() {
            @Override
            public void call(View view) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnCreate.click:R.id.text :" + view);
            }
        }).to(BActivity.class).subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnCreate.click:R.id.text->B :" + intent);
            }
        });
       /* frgActFrom.fragmentOnStart(MainFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnStart :" + fragment);
            }
        });
        frgActFrom.fragmentOnResumed(MainFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnResumed :" + fragment);
            }
        });
        frgActFrom.fragmentOnPaused(MainFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnPaused :" + fragment);
            }
        });
        frgActFrom.fragmentOnStop(MainFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnStop :" + fragment);
            }
        });
        frgActFrom.fragmentOnDestroyed(MainFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnDestroyed :" + fragment);
            }
        });

        frgActFrom.fragmentOnHiddenChanged(MainFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnHiddenChanged :" + fragment);
            }
        }).filter(new IFilter<Fragment>() {
            @Override
            public boolean filter(Fragment fragment) {
                return !fragment.isHidden();
            }
        }).filter(new IFilter<Fragment>() {
            Random random=new Random();
            @Override
            public boolean filter(Fragment fragment) {
                int i=random.nextInt(10);
                Log.d(TAG, ">filter i:" + i);
                return i%2==0;
            }
        }).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentOnHiddenChanged show random:" + fragment);
            }
        });

        frgActFrom.fragmentSetUserVisibleHint(MainFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "TabFragmentActivity->fragmentSetUserVisibleHint :" + fragment);
            }
        });

        Track.fromObject(TabFragmentActivity.class).fragmentOnStart(TabFragment.class).subscribe(new OnSubscribe<Fragment>() {
            @Override
            public void call(Fragment fragment) {
                Log.d(TAG, "fromObject->fragmentOnStart :" + fragment);
            }
        });
        track.fromObject(DialogInterface.OnDismissListener.class)
                .onMethodCall("onDismiss",DialogInterface.class).subscribe(new OnSubscribe<Object[]>() {
            @Override
            public void call(Object[] args) {
                Log.d(TAG, "A->B.onMethodCall(onDismiss) :"+args);
            }
        });
*/
        Track.from(AActivity.class).to(BActivity.class).subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, "A->B->C " + intent + " t=" + Thread.currentThread());
            }
        });


        Track<?> mainTrack = Track.from(TabFragmentActivity.class).fragmentSetUserVisibleHint(MainFragment.class,true)
                .mutexWith(Track.from(TabFragmentActivity.class).fragmentSetUserVisibleHint(MainFragment.class).filter(fragment -> !fragment.getUserVisibleHint()));

        mainTrack.viewClick(R.id.button6)
                .subscribe(view -> Log.d(TAG, "main.click(6) :" + view));

        mainTrack.viewClick(R.id.button8)
                .subscribe(view -> Log.d(TAG, "main.click(8) :" + view));

        Track<?> tabFragment = Track.from(TabFragmentActivity.class);
        Track<?> mainFr = tabFragment.fragmentOnResumed(TabFragment.class).mutexWith(tabFragment.fragmentOnPaused(TabFragment.class));

        mainFr.viewClick(R.id.button6)
                .subscribe(view -> Log.d(TAG, "TabFragment.click(6) :" + view));

        mainFr.viewClick(R.id.button8)
                .subscribe(view -> Log.d(TAG, "TabFragment.click(8) :" + view));


        Track<Intent> tacker = Track.from(Activity.class).to(CActivity.class);
        Track.fromObject(View.OnClickListener.class).onMethodCall("onClick", View.class)
                .subscribe(new OnSubscribe<Object[]>() {
                    @Override
                    public void call(Object[] objects) {
                        Log.d(TAG, "OnClickListener.onClick :" + Arrays.toString(objects));
                    }
                }).join(tacker).subscribe(new OnSubscribe<Intent>() {
            @Override
            public void call(Intent intent) {
                Log.d(TAG, ".onClick join A->C :" + intent);
            }
        });
        Track<Object[]> onclick=Track.fromObject(View.OnClickListener.class).onMethodCall("onClick", View.class)
                .subscribe(new OnSubscribe<Object[]>() {
                    @Override
                    public void call(Object[] objects) {
                        Log.d(TAG,"OnClickListener.onClick :"+ Arrays.toString(objects));
                    }
                });
        Track.from(AActivity.class).to(BActivity.class)
                .subscribe(new OnSubscribe<Intent>() {
                    @Override
                    public void call(Intent intent) {
                        Log.d(TAG,"A->B 1 :");
                    }
                })
                .join(onclick)
                .subscribe(new OnSubscribe<Object[]>() {
            @Override
            public void call(Object[] objects) {
                Log.d(TAG,"A->B join click :"+ Arrays.toString(objects));
            }
        });



        //全量埋点

    }
}
