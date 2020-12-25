package com.ttpai.track;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.PopupWindow;

import androidx.fragment.app.Fragment;

import com.ttpai.track.annotation.AnyClass;
import com.ttpai.track.callback.IFindTrack;
import com.ttpai.track.callback.OnMainThreadSubscribe;
import com.ttpai.track.callback.TrackRunnable;
import com.ttpai.track.node.FromNode;
import com.ttpai.track.node.FromObjectNode;
import com.ttpai.track.node.Node;
import com.ttpai.track.node.NodeSpec;
import com.ttpai.track.node.OnMethodCallNode;
import com.ttpai.track.node.SubscribeNode;
import com.ttpai.track.node.activity.ActivityLifeCycleNode;
import com.ttpai.track.node.activity.StartActivityNode;
import com.ttpai.track.node.dialog.DialogButtonClickNode;
import com.ttpai.track.node.dialog.DialogNode;
import com.ttpai.track.node.dialog.DismissDialogNode;
import com.ttpai.track.node.dialog.ShowDialogNode;
import com.ttpai.track.node.fragment.FragmentLifeCycleNode;
import com.ttpai.track.node.other.FilterNode;
import com.ttpai.track.node.other.JoinNode;
import com.ttpai.track.node.popup.DismissPopupNode;
import com.ttpai.track.node.popup.ShowPopupNode;
import com.ttpai.track.node.view.ViewClickNode;
import com.ttpai.track.node.view.ViewLongClickNode;
import com.ttpai.track.node.view.ViewNode;
import com.ttpai.track.node.view.ViewVisibilityNode;
import com.ttpai.tracker.R;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * FileName: TrackManager
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public class TrackManager {
    private static TrackManager sInstance;
    private Set<Node> mAllTrack;//所有已注册的路径

    private SparseArray<Set<?>> mAllRegisterSet;//所有已注册的Set

    //set 保证不重复添加,临时点亮的track
    private Set<Node> lightTrack;
    private Map<Node, Node> mGrayTrack;//置灰的路径,不可点亮，只能用起始操作重新唤醒

    private static final int ACTIVITY_CLASS = 1;
    private static final int DIALOG_CLASS = 2;
    private static final int VIEW_ID = 3;
    private static final int OBJECT_CLASS = 4;
    private static final int APPLICATION_CLASS = 5;
    private static final int POPUP_WINDOW_CLASS = 6;
    static final int FRAGMENT_CLASS = 7;
    static final int API_SERVICE_CODE = 8;

    private Application application;
    private Dispatcher mDispatcher;
    public static String ANY_METHOD = "*anyMethod*";
    public static int ANY_VIEW = R.integer.anyViewId;

    private static boolean isRegisterAnyViewId;

    private WeakReference<Object> targetWeakRef;//调用者弱引用
    private Handler mainHand;

    private TrackManager() {
        mAllRegisterSet = new SparseArray<>();
        Set activitySet = new HashSet<Class>();
        Set viewIdSet = new HashSet<Class>();
        mAllRegisterSet.put(ACTIVITY_CLASS, activitySet);
        mAllRegisterSet.put(VIEW_ID, viewIdSet);

        mAllTrack = new LinkedHashSet<>();
        lightTrack = new LinkedHashSet<>();
        mGrayTrack = new HashMap<>();

        mDispatcher = new Dispatcher();
        mainHand = new Handler(Looper.getMainLooper());
    }

    public static TrackManager getInstance() {
        if (sInstance == null) {
            synchronized (TrackManager.class) {
                if (sInstance == null)
                    sInstance = new TrackManager();
            }
        }
        return sInstance;
    }

    public Dispatcher getDispatcher(){
        return mDispatcher;
    }

    void initTrack(Application application) {
        if (this.application != null)
            return;
        this.application = application;
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            //registerActivityLifecycleCallbacks 会在应用层Activity.onCreate 之前。确保在 应用层Activity.onCreate 代码之后
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                dispatchToMain(activity, NodeSpec.TYPE_ONCREATE);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                dispatchToMain(activity, NodeSpec.TYPE_ONSTART);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                dispatchToMain(activity, NodeSpec.TYPE_ONRESUMED);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                dispatchToMain(activity, NodeSpec.TYPE_ONPAUSE);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                dispatchToMain(activity, NodeSpec.TYPE_ONSTOP);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                dispatchToMain(activity, NodeSpec.TYPE_ONSAVEINSTANCE);
            }

            //onDestroy 可以在之前
            @Override
            public void onActivityDestroyed(Activity activity) {
                activityLifeCycle(activity, NodeSpec.TYPE_ONDESTROY);
            }

        });

    }

    private void dispatchToMain(Activity activity,int lifeCycle) {
        mainHand.post(new TrackRunnable() {
            @Override
            public void execute() {
                activityLifeCycle(activity, lifeCycle);
            }
        });
    }

    private void postMainRunnable(TrackRunnable runnable) {
        mainHand.post(runnable);
    }

    Application getApplication() {
        return application;
    }

    void subscribe(final Node trackNode) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                Node rootNode = Track.getRootNode(trackNode);
                if (rootNode != null)
                    mAllTrack.add(rootNode);
            }
        });
    }

    void unsubscribe(final Node trackNode) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                Node rootNode = Track.getRootNode(trackNode);
                if (rootNode != null) {
                    removeChildLightTrack(rootNode);
                    mAllTrack.remove(rootNode);
                }
            }
        });
    }

    void registerActivityClass(Class<? extends Activity> clazz) {
        registerObject(ACTIVITY_CLASS, clazz);
    }

    void registerViewId(int viewId) {
        if (!isRegisterAnyViewId && viewId == ANY_VIEW) isRegisterAnyViewId = true;

        registerObject(VIEW_ID, viewId);
    }

    static boolean isRegisterAnyViewId() {
        return isRegisterAnyViewId;
    }

    void registerDialogClass(Class<? extends Dialog> clazz) {
        registerObject(DIALOG_CLASS, clazz);
    }

    void registerPopupClass(Class<? extends PopupWindow> popupWindowClass) {
        registerObject(POPUP_WINDOW_CLASS, popupWindowClass);
    }

    void registerServiceCode(int serviceCode) {
        registerObject(API_SERVICE_CODE, serviceCode);
    }

    void registerObjectClass(Class clazz) {
        registerObject(OBJECT_CLASS, clazz);
    }

    void registerObject(int type, Object obj) {
        Set set = (Set) mAllRegisterSet.get(type);
        if (set == null) {
            set = new HashSet<>();
            mAllRegisterSet.put(type, set);
        }
        set.add(obj);
    }

    //application only add once
    void registerApplicationClass(Class<Application> clazz) {
        if (mAllRegisterSet.get(APPLICATION_CLASS) == null) {
            Set<Class> set = new HashSet<>(1);
            set.add(clazz);
            mAllRegisterSet.put(APPLICATION_CLASS, set);
        }
    }


    void startActivity(final Object fromObj, final String toClassName, final Intent intent) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                if (fromObj == null || TextUtils.isEmpty(toClassName) || intent == null)
                    return;
                //检查是否有注册
                final Class from = fromObj.getClass();
                if (!isRegisterActivityClass(from))
                    return;
                final Class to;
                try {
                    to = Class.forName(toClassName);
                    if (!isRegisterActivityClass(to))
                        return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                setTargetWeakRef(fromObj);

                findTrack(from, intent, node -> node instanceof StartActivityNode && isSupportClass(((StartActivityNode) node).getToClass(), to));

            }
        });
    }

    private void setTargetWeakRef(Object fromObj) {
        targetWeakRef = new WeakReference<>(fromObj);
    }

    public WeakReference<Object> getTargetWeakRef() {
        return targetWeakRef;
    }

    void startActivityByApplication(final Application application, final String toClassName, final Intent intent) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                if (application == null || TextUtils.isEmpty(toClassName) || intent == null)
                    return;

                //检查是否有注册
                final Class from = application.getClass();
                if (isRegisterObject(APPLICATION_CLASS, from))
                    return;
                final Class to;
                try {
                    to = Class.forName(toClassName);
                    if (!isRegisterActivityClass(to))
                        return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
                setTargetWeakRef(application);

                findTrack(from, intent, new IFindTrack() {
                    @Override
                    public boolean equalsNode(Node node) {
                        return node instanceof StartActivityNode
                                && isSupportClass(((StartActivityNode) node).getToClass(), to);
                    }
                });

            }
        });
    }

    void activityLifeCycle(final Activity activity, final int lifeCycleType) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                if (activity == null)
                    return;

                //检查是否注册
                final Class from = activity.getClass();
                if (!isRegisterActivityClass(from))
                    return;

                findTrack(from, activity, new IFindTrack() {
                    @Override
                    public boolean equalsNode(Node node) {
                        return node instanceof ActivityLifeCycleNode
                                && ((ActivityLifeCycleNode) node).getLifeCycleType() == lifeCycleType;
                    }
                });

            }
        });
    }

    void onFinishActivity(final Activity activity) {
        activityLifeCycle(activity, NodeSpec.TYPE_ONFINISH);
    }

    /**
     * 当前class是否 支持 定义的class
     * 规则: 如果当前fromClass是定义时class的子类即支持
     */
    private boolean isSupportClass(Class defineClass, Class from) {
        if (defineClass == from)// || defineClass == AnyClass.class
            return true;
        if (defineClass == null || from == null)
            return false;
        if (defineClass.isInterface()) {//定义的接口
            Class[] interfaces = from.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i] == defineClass)
                    return true;
            }
        } else {
            while (from != null) {
                if (from == defineClass)
                    return true;
                from = from.getSuperclass();
            }
        }

        return false;
    }

    void viewClick(View v) {
        executeViewNodeEvent(v, ViewClickNode.class);
    }

    @Deprecated
    void viewSetVisibility(View v) {
        executeViewNodeEvent(v, ViewVisibilityNode.class);
    }

    void viewLongClick(View v) {
        executeViewNodeEvent(v, ViewLongClickNode.class);
    }

    /**
     * viewNode 的统一处理,逻辑一样
     *
     * @param v
     */
    private void executeViewNodeEvent(final View v, final Class<? extends ViewNode> clazz) {
        if (v == null)
            return;
        final View view = v;
        final int id = view.getId();
        if (!isRegisterAnyViewId() && !isRegisterViewId(id))
            return;

        executeAnyViewNode(v, clazz);
    }

    private void executeAnyViewNode(final View v, final Class<? extends ViewNode> clazz) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                final int id = v.getId();
                Context context = v.getContext();
                Class fromClass;
                //在DialogFragment中，会对activity 进行多层包装
                while (!(context instanceof Activity) && context instanceof ContextThemeWrapper) {
                    context = ((ContextThemeWrapper) context).getBaseContext();
                }
                if (!(context instanceof Activity))
                    return;
                fromClass = context.getClass();

                findTrack(fromClass, v, new IFindTrack() {

                    @Override
                    public boolean equalsNode(Node node) {
                        if (!clazz.isInstance(node)) return false;
                        int nodeId = ((ViewNode) node).getId();
                        return nodeId == ANY_VIEW || nodeId == id;
                    }
                });
            }
        });
    }


    void dialogShow(final Dialog dialog) {
        executeDialogNodeEvent(dialog, ShowDialogNode.class);
    }

    void dialogDismiss(final Dialog dialog) {
        executeDialogNodeEvent(dialog, DismissDialogNode.class);
    }

    /**
     * dialog 的统一处理,逻辑一样
     */
    private void executeDialogNodeEvent(final Dialog dialog, final Class<? extends DialogNode> nodeClazz) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                if (dialog == null)
                    return;
                //检查是否有注册
                final Class dialogClass = dialog.getClass();
                if (!isRegisterObject(DIALOG_CLASS, dialogClass))
                    return;

                Context context = dialog.getContext();
                Class fromClass;
                //在AlertDialog中，会对activity 进行多层包装
                while (!(context instanceof Activity) && context instanceof ContextThemeWrapper) {
                    context = ((ContextThemeWrapper) context).getBaseContext();
                }
                if (!(context instanceof Activity))
                    return;
                fromClass = context.getClass();

                findTrack(fromClass, dialog, new IFindTrack() {
                    @Override
                    public boolean equalsNode(Node node) {
                        return nodeClazz.isInstance(node)
                                && ((DialogNode) node).getDialogClass().isInstance(dialog);
                    }
                });
            }
        });
    }

    /**
     * dialog button click
     *
     * @param dialog
     */
    void dialogButtonClick(final Dialog dialog, final int buttonId) {

        execute(new TrackRunnable() {
            @Override
            public void execute() {
                if (dialog == null)
                    return;
                //不用检查，因为有可能不show
//                final Class dialogClass = dialog.getClass();
//                if (!mAllDialogClass.contains(dialogClass))
//                    return;

                Context context = dialog.getContext();
                Class fromClass;
                //在AlertDialog中，会对activity 进行多层包装
                while (!(context instanceof Activity) && context instanceof ContextThemeWrapper) {
                    context = ((ContextThemeWrapper) context).getBaseContext();
                }
                if (!(context instanceof Activity))
                    return;
                fromClass = context.getClass();

                findTrack(fromClass, dialog, new IFindTrack() {
                    @Override
                    public boolean equalsNode(Node node) {
                        return node instanceof DialogButtonClickNode
                                && ((DialogButtonClickNode) node).getButtonId() == buttonId;
                    }
                });

            }
        });
    }

    /**
     * 方法被调用
     */
    void onMethodCall(final Class returnClass, final Object target,
                      final String methodName, final Class[] argsClasses,
                      final Object[] args) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                IFindTrack ifind = new IFindTrack() {
                    @Override
                    public boolean equalsNode(Node node) {
                        if (!(node instanceof OnMethodCallNode))
                            return false;
                        OnMethodCallNode methodNode = (OnMethodCallNode) node;
                        if (methodNode.getReturnClass() != AnyClass.class //返回值匹配
                                && methodNode.getReturnClass() != returnClass)
                            return false;
                        if (!TextUtils.equals(methodNode.getMethodName(), ANY_METHOD)//方法名匹配
                                && !TextUtils.equals(methodNode.getMethodName(), methodName))
                            return false;
                        return Arrays.equals(methodNode.getArgs(), argsClasses);//参数匹配
                    }
                };
                Object data = args;
                setTargetWeakRef(target);

                findTrack(null, data, ifind, target, true);
            }
        });
    }


    void popupShow(final PopupWindow popup) {
        executePopupNodeEvent(popup, ShowPopupNode.class);
    }

    void popupDismiss(PopupWindow popup) {
        executePopupNodeEvent(popup, DismissPopupNode.class);
    }

    private void executePopupNodeEvent(final PopupWindow popup, final Class<? extends DialogNode> popClass) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                if (popup == null)
                    return;
                //检查是否有注册
                final Class dialogClass = popup.getClass();
                if (!isRegisterObject(POPUP_WINDOW_CLASS, dialogClass))
                    return;

                View contentView = popup.getContentView();
                if (contentView == null)
                    return;
                Context context = contentView.getContext();
                if (!(context instanceof Activity))
                    return;
                Class fromClass = context.getClass();

                findTrack(fromClass, popup, new IFindTrack() {
                    @Override
                    public boolean equalsNode(Node node) {
                        return popClass.isInstance(node)
                                && ((DialogNode) node).getDialogClass().isInstance(popup);
                    }
                });
            }
        });
    }

    private boolean isInstanceObjectClass(Object target) {
        Set<Class> set = (Set<Class>) mAllRegisterSet.get(OBJECT_CLASS);
        for (Class clazz : set) {//为定义的子类即可
            if (clazz.isInstance(target))
                return true;
        }
        return false;
    }

    private void execute(final TrackRunnable runnable) {
        mDispatcher.execute(runnable);
    }

    /**
     * 是否为已注册class
     * @param clazz
     * @return
     */
    public boolean isRegisterActivityClass(Class<Activity> clazz) {
        return isRegisterObject(ACTIVITY_CLASS, clazz);
    }

    /**
     * 是否为已注册ViewId
     *
     * @return
     */
    public boolean isRegisterViewId(int id) {
        if (id == View.NO_ID) return false;
        return isRegisterObject(VIEW_ID, id);
    }

    public boolean isRegisterObject(int type, Object obj) {
        Set set = (Set) mAllRegisterSet.get(type);
        if (set == null) return false;
        if (obj instanceof Class) {//只要注册的为目标class 或者 目标的superClass,都算匹配成功
            Class clazz = (Class) obj;
            while (clazz != null && clazz != Object.class) {
                if (set.contains(clazz)) return true;
                clazz = clazz.getSuperclass();
            }
            return false;
        } else {
            return set.contains(obj);
        }
    }

    //从所有路径中找到符合的新路径
    private <T> void findTrack(Class fromClass, T data, IFindTrack iFind) {
        findInAllTracks(fromClass, data, iFind, null, false);
    }

    //兼容 onMethodCall
    private <T> void findTrack(Class fromClass, T data, IFindTrack iFind, Object target, boolean isOnMethodCall) {

        //从所有的track寻找到新的track，并点亮
        findInAllTracks(fromClass, data, iFind, target, isOnMethodCall);
    }

    private <T> void findInAllTracks(Class fromClass, T data, IFindTrack iFind, Object target, boolean isOnMethodCall) {

        lightTrack.clear();
        for (Node node : mAllTrack) {

            if (node instanceof FromObjectNode) {//如果是fromObj,则要检查target是否为from实例
                if (!node.getFromClass().isInstance(target)) {
                    continue;
                }
            }
            Class nodeFromClass = Track.getFromClass(node);

            if (isOnMethodCall || isSupportClass(nodeFromClass, fromClass)) {//上下文是否正确

                for (Node child : node.children()) {
                    if (iFind.equalsNode(child)) {
                        addFilterNode(lightTrack, data, child);
                    }
                }
            }

        }

        for (Node lightNode : lightTrack) {
            Node parent;
            if ((parent = getFromNode(lightNode)) != null) {
                removeChildLightTrack(lightNode);

                removeGrayTrack(parent);
            }
            if (mGrayTrack.containsKey(lightNode))//已置灰的路径无法点亮
                continue;

            for (Node child : lightNode.children()) {
                if (child instanceof SubscribeNode) {//终点
                    callSubscribe(data, (SubscribeNode) child);
                } else {
                    mAllTrack.add(lightNode);
                }
            }
        }
    }

    private <T> void callSubscribe(T data, SubscribeNode child) {
        SubscribeNode subscribeNode = child;
        if(subscribeNode.getOnSubscribe() instanceof OnMainThreadSubscribe){
            postMainRunnable(new TrackRunnable() {
                @Override
                public void execute() {
                    subscribeNode.getOnSubscribe().call(data);
                }
            });
        }else{
            subscribeNode.getOnSubscribe().call(data);
        }
    }

    /**
     * 如果父结点 是from ,返回父结点。如果filter ，则继续向上找
     * 否则返回空
     *
     * @param node
     * @return
     */
    private Node getFromNode(Node node) {
        if (node == null)
            return null;
        Node parent = node.parent();
        if (parent instanceof FromNode)
            return parent;
        else if (node instanceof FilterNode)
            return getFromNode(parent);//递归向上
        return null;

    }

    /**
     * 把此节点 中所有置灰的节点，释放出来
     *
     * @param parent
     */
    private void removeGrayTrack(Node parent) {
        Iterator<Map.Entry<Node, Node>> iterator = mGrayTrack.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Node, Node> entry = iterator.next();
            if (entry.getValue() == parent) {
                iterator.remove();
            }
        }
    }

    //从所以已点亮中，找到所有此node下的子node，熄灭
    private void removeChildLightTrack(Node lightNode) {
        Iterator<Node> iterator = mAllTrack.iterator();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (!(node instanceof FromNode) && isChildNode(lightNode, node)) {
                iterator.remove();
            }
        }
    }

    /**
     * filter Node 过滤，
     *
     * @param data
     * @param node
     * @param <T>
     * @return
     */
    private <T> void addFilterNode(Set<Node> lightTrack, T data, Node node) {
        if (node == null || node.children() == null)
            return;
        for (Node child : node.children()) {//filter Node 检查
            if (child instanceof FilterNode) {
                if (((FilterNode) child).getFilter().filter(data)) {
                    addFilterNode(lightTrack, data, child);//递归查找，因为filter后面还可以接filter
                }//else 条件不满足，忽略
            } else if (child instanceof JoinNode) {//兼容 join
                lightTrack.add(((JoinNode) child).getJoinNode());
            } else {//普通
                lightTrack.add(node);
            }
        }
    }

    /**
     * @param rootNode 父node
     * @param node     要检查的node
     * @return rootNode 是否为 node 的父辈 node
     */
    private boolean isChildNode(Node rootNode, Node node) {
        if (rootNode == node)
            return true;
        else if (rootNode == null || node == null)
            return false;
        while ((node = node.parent()) != null) {
            if (node == rootNode)
                return true;
        }
        return false;
    }

    void fragmentOnLifeCycle(final int lifeType, final Fragment target) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                if (target == null)
                    return;
                //检查是否注册
                final Class fragClass = target.getClass();

                if (!isRegisterObject(FRAGMENT_CLASS, fragClass))
                    return;

                Class from = null;
                if (target.getActivity() != null)
                    from = target.getActivity().getClass();
                else if (target.getContext() instanceof Activity)
                    from = target.getContext().getClass();

                if (from == null)
                    return;

                findTrack(from, target, new IFindTrack() {
                    @Override
                    public boolean equalsNode(Node node) {
                        return node instanceof FragmentLifeCycleNode
                                && ((FragmentLifeCycleNode) node).getLifeCycleType() == lifeType
                                && ((FragmentLifeCycleNode) node).getFragmentClass().isInstance(target);
                    }
                });

            }
        });

    }

    /**
     * 熄灭路径,从已点亮路径集中删除
     *
     * @param node
     */
    void lightOff(final Node node) {
        execute(new TrackRunnable() {
            @Override
            public void execute() {
                if (node != null) {
                    mGrayTrack.put(node, Track.getRootNode(node));
                    removeNode(node);
                }
            }
        });
    }

    /**
     * 从路径中删除 '此结点' 及其 '子结点'
     *
     * @param node
     */
    private void removeNode(Node node) {
        if (node == null) return;
        mAllTrack.remove(node);
        if (node.children() != null) {
            for (Node child : node.children()) {
                removeNode(child);
            }
        }
    }

}
