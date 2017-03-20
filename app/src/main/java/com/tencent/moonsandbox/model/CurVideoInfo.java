package com.tencent.moonsandbox.model;

import com.tencent.moonsandbox.model.json.RspPushInfo;

/**
 * Created by tencent on 2016/8/17.
 */

public class CurVideoInfo {
    static private boolean bHost;      // 是否发布者

    static private String id;       // 发布者id
    static private int pushId;      // 当前推流id
    static private String playUrl;    // 当前播放地址

    public static boolean isHost() {
        return bHost;
    }


    public static void setHost(boolean bHost) {
        CurVideoInfo.bHost = bHost;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        CurVideoInfo.id = id;
    }

    public static int getPushId() {
        return pushId;
    }

    public static void setPushId(int pushId) {
        CurVideoInfo.pushId = pushId;
    }

    public static String getPlayUrl() {
        return playUrl;
    }

    public static void setPlayUrl(String playUrl) {
        CurVideoInfo.playUrl = playUrl;
    }
}
