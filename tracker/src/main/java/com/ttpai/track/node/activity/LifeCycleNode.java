package com.ttpai.track.node.activity;

import com.ttpai.track.node.BaseDataNode;

/**
 * FileName: LifeCycleNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public class LifeCycleNode extends BaseDataNode {

    private int lifeCycleType;

    public LifeCycleNode(Class fromClass, int lifeCycleType) {
        super(fromClass);
        this.lifeCycleType = lifeCycleType;
    }

    public int getLifeCycleType() {
        return lifeCycleType;
    }
}
