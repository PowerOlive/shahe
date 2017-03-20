package com.tencent.moonsandbox.model.json;

/**
 * Created by tencent on 2016/8/16.
 */

public class ReqHeartBeat {
    private int pushid;
    private String id;
    private int timeSpan;

    public ReqHeartBeat(int iPushId, String strId, int iTime){
        pushid = iPushId;
        id = strId;
        timeSpan = iTime;
    }

    public int getPushid() {
        return pushid;
    }

    public void setPushid(int pushid) {
        this.pushid = pushid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(int timeSpan) {
        this.timeSpan = timeSpan;
    }
}
