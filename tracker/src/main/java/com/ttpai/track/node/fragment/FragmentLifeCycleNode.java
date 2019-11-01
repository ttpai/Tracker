package com.ttpai.track.node.fragment;

import android.support.v4.app.Fragment;

import com.ttpai.track.node.activity.LifeCycleNode;

/**
 * FileName: FragmentLifeCycleNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-30
 * Description:
 */
public class FragmentLifeCycleNode extends LifeCycleNode {

    private Class<? extends Fragment> fragmentClass;
    public FragmentLifeCycleNode(Class fromClass,Class<? extends Fragment> fragmentClass, int lifeCycleType) {
        super(fromClass, lifeCycleType);
        this.fragmentClass=fragmentClass;
    }

    public Class getFragmentClass() {
        return fragmentClass;
    }

    public void setFragmentClass(Class fragmentClass) {
        this.fragmentClass = fragmentClass;
    }
}
