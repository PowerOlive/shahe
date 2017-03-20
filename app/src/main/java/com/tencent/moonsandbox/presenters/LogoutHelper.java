package com.tencent.moonsandbox.presenters;

import com.tencent.TIMCallBack;
import com.tencent.TIMManager;
import com.tencent.moonsandbox.presenters.viewif.ViewLogout;

/**
 * Created by tencent on 2016/8/23.
 */

public class LogoutHelper {
    private ViewLogout viewLogout;

    public LogoutHelper(ViewLogout view){
        viewLogout = view;
    }

    public void logout(){
        TIMManager.getInstance().logout(new TIMCallBack() {
            @Override
            public void onError(int errCode, String errMsg) {
                viewLogout.onLogout();
            }

            @Override
            public void onSuccess() {
                viewLogout.onLogout();
            }
        });
    }
}
