package com.tencent.moonsandbox.model.json;

import java.util.List;

/**
 * Created by tencent on 2016/8/16.
 */

public class RspQuery {
    private int total;
    private List<RspPushInfo> pushList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<RspPushInfo> getPushList() {
        return pushList;
    }

    public void setPushList(List<RspPushInfo> pushList) {
        this.pushList = pushList;
    }
}
