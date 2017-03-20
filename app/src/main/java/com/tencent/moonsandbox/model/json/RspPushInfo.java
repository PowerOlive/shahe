package com.tencent.moonsandbox.model.json;

/**
 * Created by tencent on 2016/8/16.
 */

public class RspPushInfo {
    private String title;
    private String description;
    private String id;
    private int pushid;
    private String avsupport = "false";
    private String create_time;
    private String playurl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getPlayurl() {
        return playurl;
    }

    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }

    public int getPushid() {
        return pushid;
    }

    public void setPushid(int pushid) {
        this.pushid = pushid;
    }

    public boolean isAvsupport() {
        return avsupport.equalsIgnoreCase("true");
    }

    public void setAvsupport(boolean avsupport) {
        this.avsupport = avsupport ? "true" : "false";
    }

    @Override
    public String toString() {
        return "RspPushInfo{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", pushid=" + pushid +
                ", avsupport=" + avsupport +
                ", create_time='" + create_time + '\'' +
                ", playurl='" + playurl + '\'' +
                '}';
    }
}
