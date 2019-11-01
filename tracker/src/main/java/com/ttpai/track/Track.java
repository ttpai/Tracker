package com.ttpai.track;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.PopupWindow;

import com.ttpai.track.annotation.AnyClass;
import com.ttpai.track.annotation.DialogButtonID;
import com.ttpai.track.callback.IFilter;
import com.ttpai.track.callback.OnEvent;
import com.ttpai.track.callback.OnSubscribe;
import com.ttpai.track.node.FromNode;
import com.ttpai.track.node.FromObjectNode;
import com.ttpai.track.node.Node;
import com.ttpai.track.node.NodeSpec;
import com.ttpai.track.node.OnMethodCallNode;
import com.ttpai.track.node.SubscribeNode;
import com.ttpai.track.node.activity.ActivityLifeCycleNode;
import com.ttpai.track.node.activity.FromActivityNode;
import com.ttpai.track.node.activity.StartActivityNode;
import com.ttpai.track.node.application.FromApplicationNode;
import com.ttpai.track.node.dialog.DialogButtonClickNode;
import com.ttpai.track.node.dialog.DismissDialogNode;
import com.ttpai.track.node.dialog.ShowDialogNode;
import com.ttpai.track.node.fragment.FragmentArgsNode;
import com.ttpai.track.node.fragment.FragmentLifeCycleNode;
import com.ttpai.track.node.other.FilterNode;
import com.ttpai.track.node.other.JoinNode;
import com.ttpai.track.node.popup.DismissPopupNode;
import com.ttpai.track.node.popup.ShowPopupNode;
import com.ttpai.track.node.view.ViewClickNode;
import com.ttpai.track.node.view.ViewLongClickNode;
import com.ttpai.track.node.view.ViewVisibilityNode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * FileName: Track
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public class Track<T> {
    private Node mNode;

    private Track(Node node) {
        this.mNode = node;
    }

    Node getNode() {

        return mNode;
    }

    private static Track createTrack(Node node) {
        return new Track(node);
    }

    /**
     * 初始化Track
     *
     * @param application
     */
    public static void initTrack(Application application) {
        TrackManager.getInstance().initTrack(application);
    }


    public static Track<?> from(Class<? extends Activity> fromClass) {
        checkActivityClass(fromClass);

        TrackManager.getInstance().registerActivityClass(fromClass);

        Track track = createTrack(new FromActivityNode(fromClass));
        return track;
    }

    /**
     * 起点为application
     *
     * @return
     */
    public static Track<?> fromApplication() {
        checkApplication();

        Class applicationClass = TrackManager.getInstance().getApplication().getClass();
        TrackManager.getInstance().registerApplicationClass(applicationClass);

        return createTrack(new FromApplicationNode(applicationClass));
    }

    /**
     * 检查application 是否初始化
     */
    private static void checkApplication() {
        if (TrackManager.getInstance().getApplication() == null)
            throw new RuntimeException("please call Track.initTrack(getApplication()); in your Application first;");
    }

    /**
     * 起点为某对象，后面的事件只支持onMethodCall,表示此类下的方法被调用时
     *
     * @param objClass
     * @return
     */
    public static Track<?> fromObject(Class objClass) {
        TrackManager.getInstance().registerObjectClass(objClass);

        return createTrack(new FromObjectNode(objClass));
    }


    /**
     * 从任意Activity开始
     *
     * @return
     */
    public static Track<?> fromAnyActivity() {
        TrackManager.getInstance().registerActivityClass(Activity.class);
        return createTrack(new FromActivityNode(Activity.class));
    }

    /**
     * 跳转到任意Activity
     *
     * @return
     */
    public Track<Intent> toAnyActivity() {
        TrackManager.getInstance().registerActivityClass(Activity.class);
        Class fromClass = getFromClass();
        return add(new StartActivityNode(fromClass, Activity.class));
    }

    /**
     * 跳转的目标class
     * 会影响后面的结点的上下文，后面所有结点的操作都是此于此activity
     */
    public Track<Intent> to(Class<? extends Activity> toClass) {
        checkActivityClass(toClass);

        TrackManager.getInstance().registerActivityClass(toClass);
        Class fromClass = getFromClass();

        Node node = new StartActivityNode(fromClass, toClass);
        return add(node);
    }

    //view click
    public Track<View> viewClick(int viewId) {
        TrackManager.getInstance().registerViewId(viewId);
        Class fromClass = getFromClass();

        return add(new ViewClickNode(fromClass, viewId));
    }

    //view setVisibility
    public Track<View> viewVisibility(int viewId) {
        TrackManager.getInstance().registerViewId(viewId);
        Class fromClass = getFromClass();
        return add(new ViewVisibilityNode(fromClass, viewId));
    }

    public Track<View> viewLongClick(int viewId) {
        TrackManager.getInstance().registerViewId(viewId);
        Class fromClass = getFromClass();
        return add(new ViewLongClickNode(fromClass, viewId));
    }

    private Class getFromClass() {
        return getFromClass(mNode);
    }

    //从上一结点获取fromClass
    static Class getFromClass(Node node) {
        if (node == null)
            throw new RuntimeException("thisNode must be fromNode after");
        Class fromClass = null;
        if (node instanceof StartActivityNode)
            fromClass = ((StartActivityNode) node).getToClass();
        else if (node instanceof ActivityLifeCycleNode //destroy或finish 之后，寻找上一个可用结点
                && (((ActivityLifeCycleNode) node).getLifeCycleType() == NodeSpec.TYPE_ONDESTROY
                || ((ActivityLifeCycleNode) node).getLifeCycleType() == NodeSpec.TYPE_ONFINISH)) {
            Class thisActivity = node.getFromClass();
            node = node.parent();
            while (node != null) {
                if (node instanceof StartActivityNode) {
                    fromClass = node.getFromClass();
                    break;
                } else {
                    node = node.parent();
                }
            }
            if (fromClass == null)
                throw new RuntimeException("此操作不合法，" + thisActivity.getSimpleName() + " onDestroyed 结点之后无可用Activity 执行此操作");
        } else {
            fromClass = node.getFromClass();
        }
        if (fromClass == null)
            throw new RuntimeException(node + ".getFromClass()==null");
        return fromClass;
    }

    /**
     * 返回 根 node
     * @param node
     * @return
     */
    static Node getRootNode(Node node){
        if (node == null)
            return node;
        while (node.parent() != null) {
            node = node.parent();
        }
        return node;
    }

    /**
     * class 必须是activity Class
     *
     * @param fromClass
     */
    private static void checkActivityClass(Class fromClass) {
        checkClass(Activity.class, fromClass);
    }

    /**
     * class 检查
     *
     * @param targetClass 必须要传的class 或其子class
     * @param fromClass   要检查的class
     */
    private static void checkClass(Class targetClass, Class fromClass) {
        if (fromClass == null)
            throw new RuntimeException("fromClass is Null");
        Class clazz = fromClass;
        while (clazz != null) {
            if (clazz == targetClass)
                return;
            clazz = clazz.getSuperclass();
        }
        throw new RuntimeException(fromClass + " must be extends " + targetClass);
    }

    public Track<T> subscribe(@NonNull OnSubscribe<T> subscribe) {
        if (subscribe == null)
            throw new RuntimeException("OnSubscribe can't be null");

        if (mNode instanceof FromNode) {
            throw new RuntimeException("subscribe node before can't be FromNode");
        }
        add(new SubscribeNode(subscribe));

        TrackManager.getInstance().subscribe(mNode);

        return this;
    }

    private <T> Track<T> add(Node node) {
        /*while (mNode instanceof SubscribeNode) {
            mNode = mNode.parent();
        }*/
        node.setParent(mNode);

        List<Node> children = this.mNode.children();
        if (children == null) {
            children = new LinkedList<>();
            this.mNode.setChildren(children);
        }
        children.add(node);
        return createTrack(node);
    }

    public Track<T> subscribe(@NonNull OnEvent event) {
        return this.subscribe((OnSubscribe) event);
    }

    //show dialog event
    public Track<Dialog> dialogShow(Class<? extends Dialog> dialogClass) {
        checkClass(Dialog.class, dialogClass);

        TrackManager.getInstance().registerDialogClass(dialogClass);
        Class fromClass = getFromClass();
        checkActivityClass(fromClass);
        return add(new ShowDialogNode(fromClass, dialogClass));
    }

    //dialog.dismiss event
    //TODO 目前只能监听用户主动调用 .dismiss的事件，如果点击positive 按钮或点击外面消失，则无法监听
    public Track<Dialog> dialogDismiss(Class<? extends Dialog> dialogClass) {
        checkClass(Dialog.class, dialogClass);

        TrackManager.getInstance().registerDialogClass(dialogClass);
        Class fromClass = getFromClass();
        checkActivityClass(fromClass);

        return add(new DismissDialogNode(fromClass, dialogClass));
    }

    //dialog button click
    public Track<Dialog> dialogButtonClick(@DialogButtonID int dialogButtonId) {
        if (dialogButtonId != DialogButtonID.BUTTON_POSITIVE && dialogButtonId != DialogButtonID.BUTTON_NEGATIVE && dialogButtonId != DialogButtonID.BUTTON_NEUTRAL)
            throw new RuntimeException(dialogButtonId + " must be DialogButtonID.BUTTON_POSITIVE or BUTTON_NEGATIVE or BUTTON_NEUTRAL");
        Class fromClass = getFromClass();
        checkActivityClass(fromClass);

        return add(new DialogButtonClickNode(fromClass, dialogButtonId));
    }
/*
    public Track<T> copyTrack() {
        return Track.createTrack(this);
    }*/

    public Track<Activity> activityOnCreated() {
        return addActivityLifeCycleNode(NodeSpec.TYPE_ONCREATE);
    }

    public Track<Activity> activityOnStarted() {
        return addActivityLifeCycleNode(NodeSpec.TYPE_ONSTART);
    }

    public Track<Activity> activityOnResumed() {
        return addActivityLifeCycleNode(NodeSpec.TYPE_ONRESUMED);
    }

    public Track<Activity> activityOnPaused() {
        return addActivityLifeCycleNode(NodeSpec.TYPE_ONPAUSE);
    }

    public Track<Activity> activityOnStoped() {
        return addActivityLifeCycleNode(NodeSpec.TYPE_ONSTOP);
    }

    public Track<Activity> activityOnSaveInstanceState() {
        return addActivityLifeCycleNode(NodeSpec.TYPE_ONSAVEINSTANCE);
    }

    /**
     * 会影响后面结点的 上下文，改为上一个activity
     *
     * @return
     */
    public Track<Activity> activityOnDestroyed() {
        return addActivityLifeCycleNode(NodeSpec.TYPE_ONDESTROY);
    }

    /**
     * activity 调用.finish 时触发
     * 会影响后面结点的 上下文，改为上一个activity
     *
     * @return
     */
    public Track<Activity> activityFinish() {
        return addActivityLifeCycleNode(NodeSpec.TYPE_ONFINISH);
    }

    private Track<Activity> addActivityLifeCycleNode(int lifeCycleType) {
        checkApplication();

        Class fromClass = getFromClass();
        checkActivityClass(fromClass);

        return add(new ActivityLifeCycleNode(fromClass, lifeCycleType));
    }

    /**
     * 当 添加了@OnMethodCall 注解方法被执行时触发
     * 返回值不限定
     * 方法名不限定
     *
     * @param args 方法参数,如无不填，用来匹配方法
     */
    public Track<Object[]> onMethodCall(Class... args) {
        return onMethodCall(TrackManager.ANY_METHOD, args);
    }


    /**
     * 当 添加了@OnMethodCall
     * 注解方法 $methodName 被执行时触发
     * <p>
     *  methodName args，参数越多，匹配越细越准确
     *
     * @param methodName  方法名，必填，用来匹配方法
     * @param args        方法参数,如无不填，用来匹配方法
     * @return Object[] 表示方法调用时的参数（注：是参数，不是返回值）
     * 如：Track.from(MainActivity.class).onMethodCall("getHeight")
     * 表示在MainActivity, 后调用了 * * getHeight() 方法时。
     * 注：* * getHeight() 此方法可以在定义在任何地方
     */
    public Track<Object[]> onMethodCall(String methodName, Class... args) {
        Class fromClass = getFromClass();
        TrackManager.getInstance().registerObjectClass(fromClass);

        return add(new OnMethodCallNode(fromClass, AnyClass.class, methodName, args));
    }

    /**
     * 解除订阅
     */
    public void unsubscribe() {
        TrackManager.getInstance().unsubscribe(mNode);
    }


    /**
     * 一次性的订阅
     * 此订阅事件只会触发一次，触发后立即解除注册
     *
     * @param onSubscribe
     */
    public Track<T> subscribeAndUn(final OnSubscribe<T> onSubscribe) {
        return subscribe(new OnSubscribe<T>() {
            @Override
            public void call(T t) {
                try {
                    onSubscribe.call(t);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    unsubscribe();
                }
            }
        });
    }

    /**
     * 熄灭该路径，意味着从头开始点亮
     */
    public void lightOff() {
        TrackManager.getInstance().lightOff(mNode);
    }


    /**
     * 点亮后立即熄灭,回到初始状态
     *
     * @param onSubscribe
     */
    public Track<T> disposable(final OnSubscribe<T> onSubscribe) {
        return subscribe(new OnSubscribe<T>() {
            @Override
            public void call(T t) {
                try {
                    onSubscribe.call(t);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lightOff();
                }
            }
        });
    }

    public Track<T> disposable(final OnEvent onEvent) {
        return disposable((OnSubscribe) onEvent);
    }

    public Track<PopupWindow> popupWindowShow(Class<? extends PopupWindow> popupWindowClass) {
        TrackManager.getInstance().registerPopupClass(popupWindowClass);
        Class fromClass = getFromClass();
        checkActivityClass(fromClass);
        return add(new ShowPopupNode(fromClass, popupWindowClass));
    }

    public Track<PopupWindow> popupWindowDismiss(Class<? extends PopupWindow> popupWindowClass) {
        TrackManager.getInstance().registerPopupClass(popupWindowClass);
        Class fromClass = getFromClass();
        checkActivityClass(fromClass);
        return add(new DismissPopupNode(fromClass, popupWindowClass));
    }

    //<!-- fragment 生命周期监听 -->
    public Track<Fragment> fragmentOnCreate(Class<? extends Fragment> fragmentClass) {
        return addFragmentLifeCycleNode(fragmentClass, NodeSpec.TYPE_ONCREATE);
    }

    public Track<Fragment> fragmentOnStart(Class<? extends Fragment> fragmentClass) {
        return addFragmentLifeCycleNode(fragmentClass, NodeSpec.TYPE_ONSTART);
    }

    public Track<Fragment> fragmentOnResumed(Class<? extends Fragment> fragmentClass) {
        return addFragmentLifeCycleNode(fragmentClass, NodeSpec.TYPE_ONRESUMED);
    }

    public Track<Fragment> fragmentOnPaused(Class<? extends Fragment> fragmentClass) {
        return addFragmentLifeCycleNode(fragmentClass, NodeSpec.TYPE_ONPAUSE);
    }

    public Track<Fragment> fragmentOnStop(Class<? extends Fragment> fragmentClass) {
        return addFragmentLifeCycleNode(fragmentClass, NodeSpec.TYPE_ONSTOP);
    }

    public Track<Fragment> fragmentOnDestroyed(Class<? extends Fragment> fragmentClass) {
        return addFragmentLifeCycleNode(fragmentClass, NodeSpec.TYPE_ONDESTROY);
    }

    private Track<Fragment> addFragmentLifeCycleNode(Class<? extends Fragment> fragmentClass, int lifeCycleType) {

        Class fromClass = getFromClass();
        checkClass(FragmentActivity.class, fromClass);
        checkClass(Fragment.class, fragmentClass);

        TrackManager.getInstance().registerObject(TrackManager.FRAGMENT_CLASS, fragmentClass);

        return add(new FragmentLifeCycleNode(fromClass, fragmentClass, lifeCycleType));
    }

    /**
     * 调用onHiddenChanged 就会触发
     *
     * @param fragmentClass
     * @return
     */
    public Track<Fragment> fragmentOnHiddenChanged(Class<? extends Fragment> fragmentClass) {
        return addFragmentArgsNode(fragmentClass, NodeSpec.TYPE_ONHIDDENCHANGED, AnyClass.class);
    }

    /**
     * 调用SetUserVisibleHint 就会触发
     *
     * @param fragmentClass
     * @return
     */
    public Track<Fragment> fragmentSetUserVisibleHint(Class<? extends Fragment> fragmentClass) {
        return addFragmentArgsNode(fragmentClass, NodeSpec.TYPE_SET_USER_VISIBLE, AnyClass.class);
    }

    private <A> Track<Fragment> addFragmentArgsNode(Class<? extends Fragment> fragmentClass, int lifeCycleType, A args) {

        Class fromClass = getFromClass();
        checkClass(FragmentActivity.class, fromClass);
        checkClass(Fragment.class, fragmentClass);

        TrackManager.getInstance().registerObject(TrackManager.FRAGMENT_CLASS, fragmentClass);

        return add(new FragmentArgsNode<A>(fromClass, fragmentClass, lifeCycleType, args));
    }

    /**
     * 路径互斥
     * 互斥：当有一条路径点亮时，其它路径全部熄灭
     *
     * @param tracks
     * @return
     */
    public Track<T> mutexWith(final Track... tracks) {
        Track[] copyTracks = Arrays.copyOf(tracks, tracks.length + 1);
        copyTracks[copyTracks.length - 1] = this;
        mutex(copyTracks);
        return this;
    }

    /**
     * 路径互斥
     * 互斥：当有一条路径点亮时，其它路径全部熄灭
     *
     * @param tracks
     * @return
     */
    public static void mutex(final Track... tracks) {
        for (final Track track : tracks) {
            track.subscribe(new OnEvent() {
                @Override
                public void onEvent() {
                    for (Track t : tracks) {
                        if (t != track) {
                            t.lightOff();
                        }
                    }
                }
            });
        }

    }

    /**
     * 条件过滤
     * 只针对前一结点有效
     * 前一结点事件触发时，但是条件过滤返回false，则忽略此事件
     *
     * @param iFilter
     * @return
     */
    public Track<T> filter(@NonNull IFilter<T> iFilter) {
        Class fromClass = getFromClass();
        return add(new FilterNode(fromClass, iFilter));
    }

    /**
     * 链接，把trackB 路径 链接 到当前路径之后
     * @param trackB
     * @param <B>
     * @return 返回新路径
     */
    public <B> Track<B> join(@NonNull Track<B> trackB){
        Node trackBRootNode=getRootNode(trackB.mNode);
        add(new JoinNode(trackBRootNode));
        return trackB;
    }
}
