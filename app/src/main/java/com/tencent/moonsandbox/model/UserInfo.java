package com.tencent.moonsandbox.model;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tencent on 2016/8/18.
 */

public class UserInfo {
    private String account;
    private String password;    // 临时
    private String userSig;

    private static UserInfo instance;
    public static UserInfo getInstance(){
        if (null == instance){
            synchronized (UserInfo.class){
                if (null == instance){
                    instance = new UserInfo();
                }
            }
        }
        return instance;
    }

    private UserInfo(){};

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

    public void writeToCache(Context context){
        SharedPreferences shareInfo = context.getSharedPreferences(MoonConstants.MOON_USERINFO, 0);
        SharedPreferences.Editor editor = shareInfo.edit();
        editor.putString(MoonConstants.MOON_ACCOUNT, account);
        editor.putString(MoonConstants.MOON_USERSIG, userSig);
        password = "";
        editor.commit();
    }

    public void clearCache(Context context){
        SharedPreferences shareInfo = context.getSharedPreferences(MoonConstants.MOON_USERINFO, 0);
        SharedPreferences.Editor editor = shareInfo.edit();
        editor.clear();
        editor.commit();
    }

    public void getCache(Context context){
        SharedPreferences shareInfo = context.getSharedPreferences(MoonConstants.MOON_USERINFO, 0);
        account = shareInfo.getString(MoonConstants.MOON_ACCOUNT, null);
        userSig = shareInfo.getString(MoonConstants.MOON_USERSIG, null);
    }
}
