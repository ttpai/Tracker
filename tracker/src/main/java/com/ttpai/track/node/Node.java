package com.ttpai.track.node;

import java.util.List;

/**
 * FileName: Node
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-19
 * Description:
 */
public interface Node {

    /**
     * @return 当前node所属类的class
     */
    Class getFromClass();

    /**
     * @return 当前node所属类的HashCode,用于对象的唯一标识
     */
    List<Node> children();

    void setChildren(List<Node> children);

    Node parent();

    void setParent(Node parent);

}
