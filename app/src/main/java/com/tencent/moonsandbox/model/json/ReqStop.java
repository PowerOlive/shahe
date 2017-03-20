package com.tencent.moonsandbox.model.json;

/**
 * Created by tencent on 2016/8/16.
 */

public class ReqStop {
    private int pushid;

    public ReqStop(int id){
        pushid = id;
    }

    public int getPushid() {
        return pushid;
    }

    public void setPushid(int pushid) {
        this.pushid = pushid;
    }
}
