package com.ttpai.track.node.fragment;

import android.support.v4.app.Fragment;

/**
 * FileName: FragmentArgsNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-30
 * Description:
 */
public class FragmentArgsNode<T> extends FragmentLifeCycleNode {

    private T args;
    public FragmentArgsNode(Class fromClass, Class<? extends Fragment> fragmentClass, int lifeCycleType, T args) {
        super(fromClass,fragmentClass, lifeCycleType);
        this.args=args;
    }

    public T getArgs() {
        return args;
    }

}
