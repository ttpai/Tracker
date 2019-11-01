package com.ttpai.track.node.activity;

import com.ttpai.track.node.BaseDataNode;

/**
 * FileName: StartActivityNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-20
 * Description: activity.startActivity or activity.startActivityForResult Node
 */
public class StartActivityNode extends BaseDataNode {


    private Class toClass;
    public StartActivityNode(Class clazz, Class toClazz) {
        super(clazz);
        this.toClass = toClazz;
    }

    public Class getToClass() {
        return toClass;
    }
}
