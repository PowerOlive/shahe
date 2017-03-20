package com.tencent.moonsandbox.model.json;

/**
 * Created by tencent on 2016/8/16.
 */

public class ReqQuery {
    private int pageIndex = 0;
    private int pageSize = 10;

    public ReqQuery setIndex(int index){
        pageIndex = index;
        return this;
    }

    public ReqQuery setSize(int size){
        pageSize = size;
        return this;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }
}
