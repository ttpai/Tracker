package com.ttpai.track.node.dialog;

import com.ttpai.track.node.BaseDataNode;

/**
 * FileName: DialogNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-23
 * Description:
 */
public class DialogNode extends BaseDataNode {

    private Class dialogClass;
    public DialogNode(Class fromClass, Class dialogClass) {
        super(fromClass);
        this.dialogClass=dialogClass;
    }

    public Class getDialogClass() {
        return dialogClass;
    }

}
