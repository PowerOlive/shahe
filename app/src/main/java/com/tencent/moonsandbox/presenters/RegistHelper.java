package com.tencent.moonsandbox.presenters;


import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.moonsandbox.presenters.viewif.ViewRegist;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;


/**
 * Created by tencent on 2016/8/18.
 */

public class RegistHelper {
    private final static String TAG = "RegistHelper";
    private ViewRegist viewRegist;

    public RegistHelper(ViewRegist view){
        viewRegist = view;
    }


    public int regsit(final String account, final String password){
        ILiveLoginManager.getInstance().tlsRegister(account, password, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                viewRegist.onRegistSuccess(account, password);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                viewRegist.onRegistFailed(module, errCode, errMsg);
            }
        });
        return ILiveConstants.NO_ERR;
    }
}
