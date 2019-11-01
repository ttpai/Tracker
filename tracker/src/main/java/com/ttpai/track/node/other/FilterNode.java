package com.ttpai.track.node.other;

import com.ttpai.track.callback.IFilter;
import com.ttpai.track.node.BaseDataNode;

/**
 * FileName: FilterNode
 * Author: zhihao.wu@ttpai.cn
 * Date: 2019-09-12
 * Description:
 */
public class FilterNode extends BaseDataNode {
    private IFilter filter;
    public FilterNode(Class fromClass,IFilter filter) {
        super(fromClass);
        this.filter=filter;
    }

    public IFilter getFilter() {
        return filter;
    }
}
