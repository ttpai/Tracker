package com.ttpai.track;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;
import com.ttpai.track.node.NodeSpec;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.ref.WeakReference;

/**
 * FileName: AopAspect
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-16
 * Description:
 * //call 调用这个方法，指代码里写了调用，但实际上还没执行
 * //execution 执行这个方法
 * //joinPoint.getTarget(); 调用者
 * //joinPoint.getThis();//当前的this
 */
@Aspect
public class AopAspect {

    private static final String TAG = "AopAspect";

    private AopAspect() {
    }

    //可以兼容lambda.
    @Around("call(public void android.view.View+.setOnClickListener(android.view.View.OnClickListener+)) && target(android.view.View) && args(click)")
    public void setOnClickListenerAround(final ProceedingJoinPoint joinPoint, View.OnClickListener click) throws Throwable {
        if (click != null && !filterMethodRepeatEventWithArg(joinPoint, click)) {
            joinPoint.proceed(new Object[]{new AopClickListener(click)});
        } else {
            joinPoint.proceed(new Object[]{click});
        }
    }

    //兼容ClickableSpan
    @Before("execution(public void android.text.style.ClickableSpan+.onClick(android.view.View+)) && args(view)")
    public void clickableSpanOnClick(final JoinPoint joinPoint, View view) throws Throwable {
        if (!filterMethodRepeatEventWithArg(joinPoint, view)) {
            TrackManager.getInstance().viewClick(view);
        }
    }

    /*
     @After("call(public void android.view.View+.setVisibility(int)) && target(android.view.View)")
     public void setViewVisibilityAfter(JoinPoint joinPoint) throws Throwable {
         if (filterMethodRepeatEvent(joinPoint)) return;
 //        Log.d(TAG, "setViewVisibilityAfter:" + joinPoint.getSourceLocation().getLine() + " " + joinPoint.getTarget() + " " + joinPoint.getThis());
         View view = (View) joinPoint.getTarget();//after
         TrackManager.getInstance().viewSetVisibility(view);
     }
 */
    @Around("call(public void android.view.View+.setOnLongClickListener(android.view.View.OnLongClickListener+)) && target(android.view.View) && args(click)")
    public void setOnLongClickListenerAround(final ProceedingJoinPoint joinPoint, final View.OnLongClickListener click) throws Throwable {
        if (click != null && !filterMethodRepeatEventWithArg(joinPoint, click)) {
            joinPoint.proceed(new Object[]{new AopLongClickListener(click)});
        } else {
            joinPoint.proceed(new Object[]{click});
        }
    }

    @Pointcut("execution(public void android.app.Activity+.startActivityForResult(android.content.Intent,int,android.os.Bundle)) && this(android.app.Activity)")
    public void startActivityForResult3() {
    }


    @Before("startActivityForResult3()")
    public void startActivityForResultAround(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, joinPoint.getArgs()[0])) return;

        Intent intent = (Intent) joinPoint.getArgs()[0];
        Object thiz = joinPoint.getThis();
        ComponentName to = intent.getComponent();
        if (to != null)//before
            TrackManager.getInstance().startActivity(thiz, to.getClassName(), intent);
    }

    //兼容 application.startActivity
    @Before("call(public void android.content.Context.startActivity(android.content.Intent,..)) && target(android.app.Application)")
    public void startActivityApplicationBefore(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, joinPoint.getArgs()[0])) return;
        Intent intent = (Intent) joinPoint.getArgs()[0];
        Application target = (Application) joinPoint.getTarget();
        ComponentName to = intent.getComponent();
        if (to != null)//before
            TrackManager.getInstance().startActivityByApplication(target, to.getClassName(), intent);
    }

    @Before("call(public void android.app.Activity.finish()) && target(android.app.Activity+)")
    public void onFinish(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEvent(joinPoint)) return;
        TrackManager.getInstance().onFinishActivity((Activity) joinPoint.getTarget());
    }

    //dialog
    @Around("call(public void android.app.Dialog+.show()) && target(android.app.Dialog)")
    public void dialogShowAround(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEvent(joinPoint)) return;
        Dialog dialog = (Dialog) joinPoint.getTarget();
        setDialogDismissMessage(dialog);
        joinPoint.proceed();//after
        TrackManager.getInstance().dialogShow(dialog);
    }

    //兼容AlertDialog.Builder.show
    @AfterReturning(value = "call(public android.app.AlertDialog android.app.AlertDialog.Builder.show())", returning = "dialog")
    public void showAppAlertDialogBuilderAround(final JoinPoint joinPoint, Dialog dialog) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, dialog)) return;
        setDialogDismissMessage(dialog);
        TrackManager.getInstance().dialogShow(dialog);

    }

    private void setDialogDismissMessage(Dialog dialog) {
        try {//监听onDismiss
            Message message = Reflect.on(dialog).get("mDismissMessage");
            if (!AopDialogMessage.isAopMessage(message)) {
                Message aopMessage = TrackManager.getInstance().getDispatcher().obtain(new AopDialogMessage(message, dialog));
                if (aopMessage != null)
                    dialog.setDismissMessage(aopMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Around("call(public void android.app.Dialog+.setDismissMessage(android.os.Message+)) && target(android.app.Dialog) && args(msg)")
    public void dialogSetDismissMessageAround(final ProceedingJoinPoint joinPoint, Message msg) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, msg)) {
            joinPoint.proceed(new Object[]{msg});
        } else {
            Dialog dialog = (Dialog) joinPoint.getTarget();
            if (!AopDialogMessage.isAopMessage(msg)) {
                Message aopMessage = TrackManager.getInstance().getDispatcher().obtain(new AopDialogMessage(msg, dialog));
                joinPoint.proceed(new Object[]{aopMessage});
            } else {
                joinPoint.proceed(new Object[]{msg});
            }
        }
    }

    @Around("call(public void android.app.Dialog+.setOnDismissListener(android.content.DialogInterface.OnDismissListener+)) && target(android.app.Dialog) && args(listen)")
    public void dialogSetOnDismissListenerAround(final ProceedingJoinPoint joinPoint, DialogInterface.OnDismissListener listen) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, listen)) {
            joinPoint.proceed(new Object[]{listen});
        } else {
            Dialog dialog = (Dialog) joinPoint.getTarget();
            joinPoint.proceed(new Object[]{new AopDialogMessage(dialog, listen)});

        }
    }

    //dialog.dismiss 切不到自动调用dismiss
//    @After("call(public void android.app.Dialog+.dismiss()) && target(android.app.Dialog)")


    //dialog button click
    @After(value = "execution(public void android.content.DialogInterface.OnClickListener+.onClick(android.content.DialogInterface+,int))")
    public void dialogButtonClick(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, joinPoint.getArgs()[0])) return;
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof Dialog)
            TrackManager.getInstance().dialogButtonClick((Dialog) args[0], (int) args[1]);
    }

    @Before("execution(@com.ttpai.track.annotation.OnMethodCall * *..*.*(..))")
    public void onMethodCall(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEvent(joinPoint)) return;
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return;
        }

        MethodSignature methodSignature = (MethodSignature) signature;
        Class returnClass = methodSignature.getReturnType();
        String methodName = methodSignature.getName();
        Object target = joinPoint.getTarget();
        Class[] params = methodSignature.getParameterTypes();

        Object[] args = joinPoint.getArgs();
        TrackManager.getInstance().onMethodCall(returnClass, target, methodName, params, args);
    }

    //popupWindow
    @Pointcut("call(public void android.widget.PopupWindow+.showAsDropDown(..)) && target(android.widget.PopupWindow)")
    public void showAsDropDownPoint() {
    }

    @Pointcut("call(public void android.widget.PopupWindow+.showAtLocation(..)) && target(android.widget.PopupWindow)")
    public void showAtLocationPoint() {
    }

    @Before("showAsDropDownPoint() || showAtLocationPoint()")
    public void popupWindowShow(JoinPoint point) throws Throwable {
        if (filterMethodRepeatEvent(point)) return;
        PopupWindow popup = (PopupWindow) point.getTarget();
        try{
            PopupWindow.OnDismissListener listener=Reflect.on(popup).get("mOnDismissListener");
            if(!AopPopupDismissListener.isAopDismiss(listener))
                popup.setOnDismissListener(new AopPopupDismissListener(listener,popup));
        }catch (Exception e){
            e.printStackTrace();
        }
        TrackManager.getInstance().popupShow(popup);
    }


    @Around("call(public void android.widget.PopupWindow+.setOnDismissListener(android.widget.PopupWindow.OnDismissListener+)) && target(android.widget.PopupWindow) && args(listen)")
    public void popupSetOnDismissListenerAround(final ProceedingJoinPoint joinPoint, PopupWindow.OnDismissListener listen) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, listen)) {
            joinPoint.proceed(new Object[]{listen});
        } else {
            PopupWindow popup = (PopupWindow) joinPoint.getTarget();
            joinPoint.proceed(new Object[]{new AopPopupDismissListener(listen,popup)});
        }
    }


    @After("call(public void androidx.fragment.app.Fragment.onCreate(android.os.Bundle)) && target(androidx.fragment.app.Fragment)")
    public void fragmentOnCreate(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONCREATE, (Fragment) point.getTarget());
    }

    @After(value = "call(public android.view.View androidx.fragment.app.Fragment.onCreateView(..)) && target(androidx.fragment.app.Fragment)")
    public void fragmentOnCreateView(final JoinPoint point) throws Throwable {
        if (filterMethodRepeatEvent(point)) return;
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONCREATEView, (Fragment) point.getTarget());
    }

    @After("call(public void androidx.fragment.app.Fragment.onStart()) && target(androidx.fragment.app.Fragment)")
    public void fragmentOnStart(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONSTART, (Fragment) point.getTarget());
    }

    @After("call(public void androidx.fragment.app.Fragment.onResume()) && target(androidx.fragment.app.Fragment)")
    public void fragmentOnResume(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONRESUMED, (Fragment) point.getTarget());
    }

    @After("call(public void androidx.fragment.app.Fragment.onPause()) && target(androidx.fragment.app.Fragment)")
    public void fragmentPause(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONPAUSE, (Fragment) point.getTarget());

    }

    @After("call(public void androidx.fragment.app.Fragment.onStop()) && target(androidx.fragment.app.Fragment)")
    public void fragmentOnStop(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONSTOP, (Fragment) point.getTarget());
    }

    @Before("call(public void androidx.fragment.app.Fragment.onDestroy()) && target(androidx.fragment.app.Fragment)")
    public void fragmentOnDestroy(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONDESTROY, (Fragment) point.getTarget());
    }

    @After("call(public void androidx.fragment.app.Fragment.onHiddenChanged(boolean)) && target(androidx.fragment.app.Fragment)")
    public void fragmentOnHiddenChanged(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONHIDDENCHANGED, (Fragment) point.getTarget());
    }

    @After("call(public void androidx.fragment.app.Fragment.setUserVisibleHint(boolean)) && target(androidx.fragment.app.Fragment)")
    public void fragmentSetUserVisible(JoinPoint point) {
        //set 优先，所以after
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_SET_USER_VISIBLE, (Fragment) point.getTarget());
    }


    WeakReference<Object> lastTarget;
    WeakReference<Object> lastArgs = new WeakReference<>(null);
    String lastMethodName;

    //过滤重写方法会调用多次的问题
    private boolean filterMethodRepeatEvent(JoinPoint joinPoint) throws Throwable {
        Object thisTarget = joinPoint.getTarget();
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint.getSignature().getName();
        if (lastTarget != null && lastTarget.get() == thisTarget && lastArgs.get() == args) {
            if (TextUtils.equals(lastMethodName, methodName)) {
                if (joinPoint instanceof ProceedingJoinPoint) {
                    ((ProceedingJoinPoint) joinPoint).proceed();
                }
                return true;
            }
        }
        if (lastTarget != null)
            lastTarget.clear();
        lastMethodName = methodName;
        lastArgs = new WeakReference<>((Object) args);
        lastTarget = new WeakReference<>(thisTarget);
        return false;
    }

    //过滤重写方法会调用多次的问题
    private boolean filterMethodRepeatEventWithArg(JoinPoint joinPoint, Object arg) throws Throwable {
        Object thisTarget = joinPoint.getTarget();
        String methodName = joinPoint.getSignature().getName();
        if (lastTarget != null && lastTarget.get() == thisTarget && lastArgs.get() == arg) {
            if (TextUtils.equals(lastMethodName, methodName)) {
                return true;
            }
        } else if (arg != null && arg.getClass().getSimpleName().startsWith("Aop")) {
            return true;
        }
        if (lastTarget != null)
            lastTarget.clear();
        lastMethodName = methodName;
        lastArgs = new WeakReference<>(arg);
        lastTarget = new WeakReference<>(thisTarget);
        return false;
    }

}
