package com.tencent.moonsandbox.presenters.viewif;

/**
 * Created by tencent on 2016/8/18.
 */

public interface ViewRegist {
    void onRegistSuccess(String account, String password);
    void onRegistFailed(String module, int errCode, String errMsg);
}
