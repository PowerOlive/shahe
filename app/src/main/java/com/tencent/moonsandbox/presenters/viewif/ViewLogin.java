package com.tencent.moonsandbox.presenters.viewif;

/**
 * Created by tencent on 2016/8/18.
 */

public interface ViewLogin {
    void onLoginSuccess(String account, String userSig);
    void onLoginFailed(String module, int errCode, String errMsg);
}
