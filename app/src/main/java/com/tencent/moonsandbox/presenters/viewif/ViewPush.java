package com.tencent.moonsandbox.presenters.viewif;

import com.tencent.moonsandbox.model.json.Rsp;
import com.tencent.moonsandbox.model.json.RspPush;

/**
 * Created by tencent on 2016/8/23.
 */

public interface ViewPush {
    void onPushSuccess(RspPush push);

    void onStopPushSuccess(Rsp rsp);

    void onRequestFailed(String module, int errCode, String errMsg);
}
