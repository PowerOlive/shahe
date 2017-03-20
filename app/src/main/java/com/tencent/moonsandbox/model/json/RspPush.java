package com.tencent.moonsandbox.model.json;

/**
 * Created by tencent on 2016/8/16.
 */

public class RspPush {
    private int pushid;
    private String rtmpurl;

    public int getPushid() {
        return pushid;
    }

    public void setPushid(int pushid) {
        this.pushid = pushid;
    }

    public String getRtmpurl() {
        return rtmpurl;
    }

    public void setRtmpurl(String rtmpurl) {
        this.rtmpurl = rtmpurl;
    }

    @Override
    public String toString() {
        return "JsonRspPush{" +
                "pushid=" + pushid +
                ", rtmpurl='" + rtmpurl + '\'' +
                '}';
    }
}
