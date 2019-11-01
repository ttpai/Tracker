package com.ttpai.track;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;

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
 * <p>
 * <p>
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
        if (filterMethodRepeatEvent(joinPoint)) return;
//        Log.d(TAG, "setOnClickListenerAround:" + joinPoint.getSourceLocation().getLine()+" "+joinPoint.getTarget()+" "+joinPoint.getThis());

        if(click!=null){
            View.OnClickListener nowClick = new AopClickListener(click);
            joinPoint.proceed(new Object[]{nowClick});
        }else{
            joinPoint.proceed(new Object[]{click});
        }
    }


    @After("call(public void android.view.View+.setVisibility(int)) && target(android.view.View)")
    public void setViewVisibilityAfter(JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEvent(joinPoint)) return;
//        Log.d(TAG, "setViewVisibilityAfter:" + joinPoint.getSourceLocation().getLine() + " " + joinPoint.getTarget() + " " + joinPoint.getThis());
        View view = (View) joinPoint.getTarget();//after
        TrackManager.getInstance().viewSetVisibility(view);
    }

    @Around("call(public void android.view.View+.setOnLongClickListener(android.view.View.OnLongClickListener+)) && target(android.view.View) && args(click)")
    public void setOnLongClickListenerAround(final ProceedingJoinPoint joinPoint,final View.OnLongClickListener click) throws Throwable {
        if (filterMethodRepeatEvent(joinPoint)) return;
        if(click!=null){
            View.OnLongClickListener nowClick = new AopLongClickListener(click);
            joinPoint.proceed(new Object[]{nowClick});
        }else{
            joinPoint.proceed(new Object[]{click});
        }
    }

    @Pointcut("execution(public void android.app.Activity+.startActivityForResult(android.content.Intent,int,android.os.Bundle)) && this(android.app.Activity)")
    public void startActivityForResult3() {
    }


    @Around("startActivityForResult3()")
    public void startActivityForResultAround(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, joinPoint.getArgs()[0])) return;
//        Log.d(TAG, "startActivityForResultAround:" + joinPoint.getSourceLocation().getLine() + " " + joinPoint.getTarget() + " " + joinPoint.getThis());

        Intent intent = (Intent) joinPoint.getArgs()[0];
        Object thiz = joinPoint.getThis();
        ComponentName to = intent.getComponent();
        if (to != null)//before
            TrackManager.getInstance().startActivity(thiz, to.getClassName(), intent);
        joinPoint.proceed();
    }

    //兼容 application.startActivity
    @Before("call(public void android.content.Context.startActivity(android.content.Intent,..)) && target(android.app.Application)")
    public void startActivityApplicationBefore(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, joinPoint.getArgs()[0])) return;
//        Log.d(TAG, "startActivityApplicationBefore:" + joinPoint.getSourceLocation().getLine() + " " + joinPoint.getTarget() + " " + joinPoint.getThis());
        Intent intent = (Intent) joinPoint.getArgs()[0];
        Application target = (Application) joinPoint.getTarget();
        ComponentName to = intent.getComponent();
        if (to != null)//before
            TrackManager.getInstance().startActivityByApplication(target, to.getClassName(), intent);
    }

    @Before("call(public void android.app.Activity.finish()) && target(android.app.Activity+)")
    public void onFinish(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEvent(joinPoint)) return;
//        Log.d(TAG, "onFinish:" + joinPoint.getSourceLocation().getLine() + " " + joinPoint.getTarget() + " " + joinPoint.getThis());

        TrackManager.getInstance().onFinishActivity((Activity) joinPoint.getTarget());
    }

    //其它dialog
    @Around("call(public void android.app.Dialog+.show()) && target(android.app.Dialog)")
    public void dialogShowAround(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEvent(joinPoint)) return;
//        Log.d(TAG, "dialogShowAround:" + joinPoint.getSourceLocation().getLine() + " " + joinPoint.getTarget() + " " + joinPoint.getThis());

        joinPoint.proceed();//after
        TrackManager.getInstance().dialogShow((Dialog) joinPoint.getTarget());
    }

    //兼容AlertDialog.Builder.show
    @AfterReturning(value = "call(public android.app.AlertDialog android.app.AlertDialog.Builder.show())", returning = "dialog")
    public void showAppAlertDialogBuilderAround(final JoinPoint joinPoint, Dialog dialog) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, dialog)) return;
//        Log.d(TAG, "showAppAlertDialogBuilderAround:" + dialog);
        TrackManager.getInstance().dialogShow(dialog);
    }

    //dialog.dismiss
    @After("call(public void android.app.Dialog+.dismiss()) && target(android.app.Dialog)")
    public void dialogDismissAfter(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEvent(joinPoint)) return;
//        Log.d(TAG, "dialogDismissAfter:" + joinPoint.getSourceLocation().getLine() + " " + joinPoint.getTarget() + " " + joinPoint.getThis());

        TrackManager.getInstance().dialogDismiss((Dialog) joinPoint.getTarget());
    }

    //dialog button click
    @After(value = "execution(public void android.content.DialogInterface.OnClickListener+.onClick(android.content.DialogInterface+,int))")
    public void dialogButtonClick(final JoinPoint joinPoint) throws Throwable {
        if (filterMethodRepeatEventWithArg(joinPoint, joinPoint.getArgs()[0])) return;
//        Log.d(TAG, "dialogButtonClick:" + Arrays.toString(joinPoint.getArgs()));
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
//        Log.d(TAG, "OnMethodCall:" + Arrays.toString(joinPoint.getArgs()) + " :" + joinPoint.getSignature().toLongString() + " " + joinPoint.getTarget() + " " + joinPoint.getThis());

        MethodSignature methodSignature = (MethodSignature) signature;
        Class returnClass = methodSignature.getReturnType();
        String methodName = methodSignature.getName();
        Object target = joinPoint.getTarget();
        Class[] params = methodSignature.getParameterTypes();

        Object[] args = joinPoint.getArgs();
        TrackManager.getInstance().onMethodCall(returnClass, target, methodName, params, args);
    }

    @Pointcut("call(public void android.widget.PopupWindow+.showAsDropDown(..)) && target(android.widget.PopupWindow)")
    public void showAsDropDownPoint() {
    }

    @Pointcut("call(public void android.widget.PopupWindow+.showAtLocation(..)) && target(android.widget.PopupWindow)")
    public void showAtLocationPoint() {
    }

    @Before("showAsDropDownPoint() || showAtLocationPoint()")
    public void popupWindowShow(JoinPoint point) throws Throwable {
        if (filterMethodRepeatEvent(point)) return;
//        Log.d(TAG, "popupShow:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());
        PopupWindow popup = (PopupWindow) point.getTarget();
        TrackManager.getInstance().popupShow(popup);
    }

    @Before("call(public void android.widget.PopupWindow+.dismiss()) && target(android.widget.PopupWindow)")
    public void popupWindowDismiss(JoinPoint point) throws Throwable {
        if (filterMethodRepeatEvent(point)) return;
//        Log.d(TAG, "popupWindowDismiss:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());
        PopupWindow popup = (PopupWindow) point.getTarget();
        TrackManager.getInstance().popupDismiss(popup);
    }

    @Before("call(public void android.support.v4.app.Fragment.onCreate(android.os.Bundle)) && target(android.support.v4.app.Fragment)")
    public void fragmentOnCreate(JoinPoint point) {
//        Log.d(TAG, "fragmentOnCreate:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONCREATE, (Fragment) point.getTarget());
    }

    @Before("call(public void android.support.v4.app.Fragment.onStart()) && target(android.support.v4.app.Fragment)")
    public void fragmentOnStart(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONSTART, (Fragment) point.getTarget());
//
//        Log.d(TAG, "fragmentOnStart:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());
    }

    @Before("call(public void android.support.v4.app.Fragment.onResume()) && target(android.support.v4.app.Fragment)")
    public void fragmentOnResume(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONRESUMED, (Fragment) point.getTarget());

//        Log.d(TAG, "fragmentOnResume:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());
    }

    @Before("call(public void android.support.v4.app.Fragment.onPause()) && target(android.support.v4.app.Fragment)")
    public void fragmentPause(JoinPoint point) {
//        Log.d(TAG, "fragmentPause:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONPAUSE, (Fragment) point.getTarget());

    }

    @Before("call(public void android.support.v4.app.Fragment.onStop()) && target(android.support.v4.app.Fragment)")
    public void fragmentOnStop(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONSTOP, (Fragment) point.getTarget());
//        Log.d(TAG, "fragmentOnStop:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());
    }

    @Before("call(public void android.support.v4.app.Fragment.onDestroy()) && target(android.support.v4.app.Fragment)")
    public void fragmentOnDestroy(JoinPoint point) {
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONDESTROY, (Fragment) point.getTarget());
//        Log.d(TAG, "fragmentOnDestroy:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());

    }

    @After("call(public void android.support.v4.app.Fragment.onHiddenChanged(boolean)) && target(android.support.v4.app.Fragment)")
    public void fragmentOnHiddenChanged(JoinPoint point) {
//        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONDESTROY, (Fragment) point.getTarget());
//        Log.d(TAG, "fragmentOnHiddenChanged:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_ONHIDDENCHANGED, (Fragment) point.getTarget());
    }


    @After("call(public void android.support.v4.app.Fragment.setUserVisibleHint(boolean)) && target(android.support.v4.app.Fragment)")
    public void fragmentSetUserVisible(JoinPoint point) {
        //set 优先，所以after
        TrackManager.getInstance().fragmentOnLifeCycle(NodeSpec.TYPE_SET_USER_VISIBLE, (Fragment) point.getTarget());
//        Log.d(TAG, "fragmentSetUserVisible:" + Arrays.toString(point.getArgs()) + " :" + point.getSignature().toLongString() + " " + point.getTarget() + " " + point.getThis());

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
                if (joinPoint instanceof ProceedingJoinPoint) {
                    ((ProceedingJoinPoint) joinPoint).proceed();
                }
                return true;
            }
        }
        if (lastTarget != null)
            lastTarget.clear();
        lastMethodName = methodName;
        lastArgs = new WeakReference<>(arg);
        lastTarget = new WeakReference<>(thisTarget);
        return false;
    }

}
