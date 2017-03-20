package com.tencent.moonsandbox.model.json;

/**
 * Created by tencent on 2016/8/16.
 */

public class ReqPush {
    private String id;
    private String title;
    private String avsupport = "false";
    private String description;
    private String playurl;

    public ReqPush(String userId){
        id = userId;
    }

    public ReqPush setTitle(String strTitle){
        title = strTitle;
        return this;
    }

    public ReqPush setAvSupport(boolean enable){
        avsupport = enable ? "true" : "false";
        return this;
    }

    public ReqPush setDesciption(String strDescription){
        description = strDescription;
        return this;
    }

    public ReqPush setPlayUrl(String strPlayUrl){
        playurl = strPlayUrl;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPlayurl() {
        return playurl;
    }

    public boolean isAvSupport(){
        return avsupport.equalsIgnoreCase("true");
    }
}
