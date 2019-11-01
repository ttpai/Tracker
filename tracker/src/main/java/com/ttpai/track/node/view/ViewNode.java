package com.ttpai.track.node.view;

import android.view.View;

import com.ttpai.track.node.BaseDataNode;

/**
 * FileName: ViewNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-20
 * Description:
 */
public abstract class ViewNode extends BaseDataNode {

    private int id;

    public ViewNode(Class fromClass, int id) {
        super(fromClass);
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
