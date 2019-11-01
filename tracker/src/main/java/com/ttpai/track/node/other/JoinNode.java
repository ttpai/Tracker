package com.ttpai.track.node.other;

import com.ttpai.track.node.BaseDataNode;
import com.ttpai.track.node.Node;

/**
 * FileName: JoinNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-10-16
 * Description:
 */
public class JoinNode extends BaseDataNode {

    private Node mJoinNode;
    public JoinNode(Node joinNode) {
        super(joinNode.getFromClass());
        this.mJoinNode=joinNode;
    }

    public Node getJoinNode() {
        return mJoinNode;
    }

    public void setJoinNode(Node joinNode) {
        this.mJoinNode = joinNode;
    }
}
