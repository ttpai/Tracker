package com.ttpai.sample;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * FileName: TrackTools
 * Author: zhihao.wu@ttpai.cn
 * Date: 2020/11/10
 * Description:
 */
public class TrackTools {


    static final Map<Integer, Object> pageParams = new HashMap<>();//存放页面 参数的map

    /**
     * setTag 无参数
     */
    public static View setTag(View v, String viewTag) {
        v.setTag(R.integer.viewDesc, viewTag);
        return v;
    }

    /**
     * setTag 并且携带参数
     */
    public static View setTag(View v, Object tagObj) {
        ViewTag tag = tagObj.getClass().getAnnotation(ViewTag.class);
        if (tag == null || TextUtils.isEmpty(tag.value())) {
            throw new RuntimeException(tagObj.getClass() + " don't has ViewTag Annotation");
        }
        v.setTag(R.integer.viewDesc, tagObj);
        return v;
    }


    /**
     * setPageId 并且携带参数
     */
    public static void setPageId(Activity activity, Object pageObj) {
        setPageIdInner(activity, pageObj);
    }

    public static void setPageId(Fragment fragment, Object pageObj) {
        setPageIdInner(fragment, pageObj);
    }

    private static void setPageIdInner(Object page, Object pageObj) {
        if (page == null) return;
        PageId id = pageObj.getClass().getAnnotation(PageId.class);
        if (id == null || TextUtils.isEmpty(id.value())) {
            throw new RuntimeException(pageObj.getClass() + " don't has PageId Annotation");
        }
        pageParams.put(page.hashCode(), pageObj);
    }

    /**
     * 获取 page 的 参数
     */
    public static Object getPageParams(Object page) {
        Object tag = pageParams.get(getKey(page));
        if (tag == null) return null;
        if (tag.getClass().getAnnotation(PageId.class) != null) {
            return tag;
        }
        return null;
    }


    public static String getTag(View v) {
        Object tag = v.getTag(R.integer.viewDesc);
        if (tag == null) return null;
        ViewTag viewTag;
        if (tag instanceof String) {
            return (String) tag;
        } else if ((viewTag = tag.getClass().getAnnotation(ViewTag.class)) != null) {
            return viewTag.value();
        }
        return null;
    }

    public static String getFragmentTag(View v) {
        return (String) v.getTag(R.integer.viewFragment);
    }

    public static View setViewFragmentTag(View v, Fragment fragment) {
        if (isFragmentPage(fragment)) {
            String pageId = getPageId(fragment);
            v.setTag(R.integer.viewFragment, pageId);
        }
        return v;
    }


    public static String getViewFragmentTag(View v) {
        if (v == null) return null;
        String tag = getFragmentTag(v);
        while (TextUtils.isEmpty(tag) && v.getId() != android.R.id.content) {
            if (v.getParent() instanceof ViewGroup) {
                v= (View) v.getParent();
                tag = getFragmentTag(v);
            } else {
                return null;
            }
        }
        return tag;
    }

    /**
     * 获取view 的唯一 事件id
     *
     * @param v
     * @return
     */
    public static String getViewActionId(View v) {
        String tag = getTag(v);
        if (tag != null) {
            return tag;
        } else {
            return viewTreePath(v);
        }

    }

    /**
     * 获取view的 附加参数
     *
     * @param v
     * @return
     */
    public static Object getViewParams(View v) {
        Object tag = v.getTag(R.integer.viewDesc);
        if (tag == null) return null;
        if (tag.getClass().getAnnotation(ViewTag.class) != null) {
            return tag;
        }
        return null;
    }


    /**
     * 判断fragment 是否 是page ,只有 PageId 注解，才认为是页面
     */
    public static boolean isFragmentPage(Fragment fragment) {
        if (fragment == null) return false;

        PageId tag = fragment.getClass().getAnnotation(PageId.class);
        if (tag != null) {
            return !TextUtils.isEmpty(tag.value());
        } else {
            Object pageObj = pageParams.get(getKey(fragment));
            if (pageObj != null) {
                PageId id = pageObj.getClass().getAnnotation(PageId.class);
                return id != null && !TextUtils.isEmpty(id.value());
            }
        }
        return false;
    }


    /**
     * 通过view 获取pageId，fragment/activity 二选一
     */
    public static String getPageId(View v) {
        if (v == null) return null;

        String pageId = getViewFragmentTag(v);
        if (pageId == null) {
            //在DialogFragment中，会对activity 进行多层包装
            Context context = v.getContext();
            while (!(context instanceof Activity) && context instanceof ContextThemeWrapper) {
                context = ((ContextThemeWrapper) context).getBaseContext();
            }
            pageId = getPageIdInner(context);
        }
        return pageId;
    }

    /**
     * 获取 页面 的别名 id
     */
    public static String getPageId(Fragment fragment) {
        return getPageIdInner(fragment);
    }

    /**
     * 获取 页面 的别名 id
     */
    public static String getPageId(Activity activity) {
        return getPageIdInner(activity);
    }

    static String getPageIdInner(Object page) {
        Object pageObj = pageParams.get(getKey(page));
        if (pageObj != null) {
            PageId id = pageObj.getClass().getAnnotation(PageId.class);
            return id.value();
        }
        PageId tag = page.getClass().getAnnotation(PageId.class);
        if (tag != null) {
            return tag.value();
        }
        return page.getClass().getSimpleName();
    }

    static void cleanPageParams(Object page) {
        pageParams.remove(getKey(page));
    }

    private static int getKey(Object page) {
        return page.hashCode();
    }

    public static String getPageName(Object activityOrFragment) {
        return activityOrFragment.getClass().getSimpleName();
    }

    //viewTree
    public static String viewTreePath(View v) {
        if (v == null || v.getId() == android.R.id.content) return "";

        String frgTag = getFragmentTag(v);
        if (frgTag != null) {//fragment root view
            return getViewName(v);
        }
        ViewParent parent = v.getParent();
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            int index = getViewIndex(v, viewGroup);
            String groupPath = viewTreePath(viewGroup);
            if (!TextUtils.isEmpty(groupPath)) {
                return String.format(Locale.CHINA, "%s/%s[%d]", groupPath, getViewName(v), index);
            } else {
                return String.format(Locale.CHINA, "%s[%d]", getViewName(v), index);
            }
        }
        return getViewName(v);
    }

    private static int getViewIndex(View v, ViewGroup viewGroup) {
        int index;
        if (viewGroup instanceof RecyclerView) {
            index = getIndexForRecyclerView(v, (RecyclerView) viewGroup);
        } else if (viewGroup instanceof ViewPager) {
            index = getIndexForViewPager(v, (ViewPager) viewGroup);
        } else if (viewGroup instanceof AdapterView) {
            index = getIndexForAdapterView(v, (AdapterView) viewGroup);
        } else {
            index = getIndexForViewGroup(v, viewGroup);
        }
        return index;
    }

    //viewName
    private static String getViewName(View v) {
        return getLongViewName(v);
//        return getShortViewName(v);
    }

    private static String getLongViewName(View v) {
        return v.getClass().getSimpleName();
    }

    //缩短viewName
    private static String getShortViewName(View v) {
        String simpleName = v.getClass().getSimpleName();
        char[] name = simpleName.toCharArray();
        String newName = "";
        int lastUp = 0;
        for (int i = 0; i < name.length; i++) {
            if (name[i] > 'A' && name[i] < 'Z') {
                newName += name[i];
                lastUp = i;
            }
        }
        return newName + simpleName.substring(lastUp + 1);
    }

    private static int getIndexForRecyclerView(View v, RecyclerView recyclerView) {
        return recyclerView.getChildAdapterPosition(v);
    }

    private static int getIndexForAdapterView(View v, AdapterView adapterView) {
        return adapterView.getPositionForView(v);
    }

    private static int getIndexForViewPager(View v, ViewPager viewPager) {
        return viewPager.getCurrentItem();
    }

    private static int getIndexForViewGroup(View v, ViewGroup viewGroup) {
        int index = 0;
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            if (viewGroup.getChildAt(i) == v) {
                index = i;
                break;
            }
        }
        return index;
    }
}
