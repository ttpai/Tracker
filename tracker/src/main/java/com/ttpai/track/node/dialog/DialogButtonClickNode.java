package com.ttpai.track.node.dialog;

import com.ttpai.track.node.BaseDataNode;

/**
 * FileName: DialogButtonClickNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-23
 * Description:
 */
public class DialogButtonClickNode extends BaseDataNode {
    private int buttonId;
    public DialogButtonClickNode(Class fromClass, int buttonId) {
        super(fromClass);
        this.buttonId=buttonId;
    }

    public int getButtonId() {
        return buttonId;
    }

}
