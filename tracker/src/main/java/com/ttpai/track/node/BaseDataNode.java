package com.ttpai.track.node;

import java.util.List;

/**
 * FileName: BaseDataNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-20
 * Description:
 */
public abstract class BaseDataNode implements Node {
    private Class fromClass;
    private List<Node> children;
    private Node parent;

    public BaseDataNode(Class fromClass) {
        this.fromClass = fromClass;
    }

    @Override
    public Class getFromClass() {
        return fromClass;
    }

    @Override
    public void setChildren(List<Node> children) {
        this.children=children;
    }

    @Override
    public List<Node> children() {
        return children;
    }

    @Override
    public Node parent() {
        return parent;
    }

    @Override
    public void setParent(Node parent) {
        this.parent=parent;
    }
}
