package com.tencent.moonsandbox;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomConfig;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.moonsandbox.model.MoonConstants;
import com.tencent.moonsandbox.model.UserInfo;
import com.tencent.moonsandbox.utils.MLog;

/**
 * Created by xkazerzhang on 2016/8/24.
 */

public class MoonApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ILiveSDK.getInstance().initSdk(getApplicationContext(), MoonConstants.SDK_APPID, MoonConstants.ACCOUNT_TYPE);
        ILiveRoomManager.getInstance().init(new ILiveRoomConfig());
        MLog.setLogLevel(MLog.SxbLogLevel.DEBUG);

        ILiveLoginManager.getInstance().setUserStatusListener(new ILiveLoginManager.TILVBStatusListener() {
            @Override
            public void onForceOffline(int error, String message) {
                Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.tip_force_offline), Toast.LENGTH_SHORT).show();
                // 清除用户信息
                UserInfo.getInstance().clearCache(getApplicationContext());
                getApplicationContext().sendBroadcast(new Intent(MoonConstants.BD_EXIT_APP));
            }
        });
    }
}
