package com.tencent.moonsandbox.presenters;

import android.content.Context;

import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.TIMUser;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.moonsandbox.model.MoonConstants;
import com.tencent.moonsandbox.presenters.viewif.ViewLogin;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by tencent on 2016/8/18.
 */

public class LoginHelper {
    private ViewLogin viewLogin;

    public LoginHelper(Context context, ViewLogin view){
        viewLogin = view;
    }

    public void loginSig(final String account, final String userSig){
        ILiveLoginManager.getInstance().tilvbLogin(account, userSig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                viewLogin.onLoginSuccess(account, userSig);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                viewLogin.onLoginFailed(module, errCode, errMsg);
            }
        });
    }

    public int login(final String account, String password){
        ILiveLoginManager.getInstance().tlsLogin(account, password, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String userSig) {
                loginSig(account, userSig);
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                viewLogin.onLoginFailed(module, errCode, errMsg);
            }
        });
        return ILiveConstants.NO_ERR;
    }
}
