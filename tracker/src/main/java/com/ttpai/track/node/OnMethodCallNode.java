package com.ttpai.track.node;

/**
 * FileName: OnMethodCallNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-08-27
 * Description:
 */
public class OnMethodCallNode extends BaseDataNode{


    private Class<?> returnClass;
    private String methodName;
    private Class[] args;
    public OnMethodCallNode(Class fromClass, Class<?> returnClass, String methodName, Class[] args) {
        super(fromClass);
        this.returnClass=returnClass;
        this.methodName=methodName;
        this.args=args;
    }

    public Class<?> getReturnClass() {
        return returnClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getArgs() {
        return args;
    }
}
