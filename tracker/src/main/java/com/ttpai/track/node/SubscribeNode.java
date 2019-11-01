package com.ttpai.track.node;

import com.ttpai.track.callback.OnSubscribe;

import java.util.List;

/**
 * FileName: SubscribeNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public class SubscribeNode implements Node{
    private OnSubscribe onSubscribe;

    public SubscribeNode(OnSubscribe onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    public OnSubscribe getOnSubscribe() {
        return onSubscribe;
    }

    public void setOnSubscribe(OnSubscribe onSubscribe) {
        this.onSubscribe = onSubscribe;
    }

    @Override
    public Class getFromClass() {
        return null;
    }

    @Override
    public List<Node> children() {
        return null;
    }

    @Override
    public void setChildren(List<Node> children) {

    }

    @Override
    public Node parent() {
        return null;
    }

    @Override
    public void setParent(Node parent) {

    }

}
