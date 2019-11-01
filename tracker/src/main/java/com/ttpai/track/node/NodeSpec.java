package com.ttpai.track.node;

/**
 * FileName: NodeSpec
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-20
 * Description: like View.MeasureSpec
 */
public class NodeSpec {

    private static final int MODE_SHIFT = 29;
    private static final int MODE_MASK = 0x7 << MODE_SHIFT;

    //大类Mode
    public static final int MODE_VIEW = 0 << MODE_SHIFT;//view 事件

    public static final int MODE_ACTIVITY = 1 << MODE_SHIFT;//activity 事件

    public static final int MODE_FRAGMENT = 2 << MODE_SHIFT;//fragment 事件
    public static final int MODE_DIALOG = 3 << MODE_SHIFT;//dialog 事件

    //…… 7

    public static final int TYPE_CLICK=1;// click
    public static final int TYPE_VISIBILITY=2;//visibility

    public static final int TYPE_START_ACTIVITY=1;//startActivity
    public static final int TYPE_ONCREATE =2;//onCreate
    public static final int TYPE_ONSTART =3;
    public static final int TYPE_ONRESUMED =4;
    public static final int TYPE_ONPAUSE =5;
    public static final int TYPE_ONSTOP =6;
    public static final int TYPE_ONDESTROY =7;

    public static final int TYPE_ONSAVEINSTANCE =8;
    public static final int TYPE_ONFINISH =9;

    public static final int TYPE_ONHIDDENCHANGED =10;
    public static final int TYPE_SET_USER_VISIBLE =11;

    public static final int TYPE_SHOW_DIALOG=1;//show dialog
    public static final int TYPE_DISMISS_DIALOG=2;//dismiss dialog



    /**
     * @param type 子类型
     * @param mode 父类型，大类
     * @return
     */
    public static int makeNodeSpec(int type, int mode) {
        return (type & ~MODE_MASK) | (mode & MODE_MASK);
    }

    public static int getMode(int nodeSpec) {
        return (nodeSpec & MODE_MASK);
    }

    public static int getType(int nodeSpec) {
        return (nodeSpec & ~MODE_MASK);
    }
}
