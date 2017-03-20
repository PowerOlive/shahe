package com.tencent.moonsandbox.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.moonsandbox.R;
import com.tencent.moonsandbox.model.CurVideoInfo;
import com.tencent.moonsandbox.model.UserInfo;
import com.tencent.moonsandbox.model.json.ReqPush;
import com.tencent.moonsandbox.model.json.Rsp;
import com.tencent.moonsandbox.model.json.RspPush;
import com.tencent.moonsandbox.presenters.PushHelper;
import com.tencent.moonsandbox.presenters.viewif.ViewPush;
import com.tencent.moonsandbox.view.customview.CustomSwitch;

/**
 * Created by tencent on 2016/8/16.
 */

public class CreateActivity extends MoonActivity implements View.OnClickListener, ViewPush{
    private PushHelper pushHelper;
    private EditText etTitle, etDesc, etPlayUrl;
    private CustomSwitch cs_rtmp, cs_interact;
    private boolean bRTMP = true;
    private boolean bInteract = false;

    private boolean bCreating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        etTitle = (EditText)findViewById(R.id.et_title);
        etDesc = (EditText)findViewById(R.id.et_desc);
        etPlayUrl = (EditText)findViewById(R.id.et_play_url);

        cs_rtmp = (CustomSwitch)findViewById(R.id.cs_rtmp);
        cs_interact = (CustomSwitch)findViewById(R.id.cs_interact);

        cs_rtmp.setChecked(bRTMP, false);
        cs_interact.setChecked(bInteract, false);
        cs_rtmp.setEnabled(false);

        pushHelper = new PushHelper(this);
    }

    @Override
    protected void onResume() {
        if (null != etPlayUrl && !TextUtils.isEmpty(CurVideoInfo.getPlayUrl())){
            etPlayUrl.setText(CurVideoInfo.getPlayUrl());
        }
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.cs_rtmp:
            bRTMP = !bRTMP;
            cs_rtmp.setChecked(bRTMP, true);
            break;
        case R.id.cs_interact:
            bInteract = !bInteract;
            cs_interact.setChecked(bInteract, true);
            break;
        case R.id.iv_play_qr:
            startActivity(new Intent(CreateActivity.this, QRScanActivity.class));
            break;
        case R.id.btn_create:
            if (TextUtils.isEmpty(etPlayUrl.getText().toString())) {
                Toast.makeText(getApplicationContext(), R.string.hit_create_empty, Toast.LENGTH_SHORT).show();
            }else if (!etPlayUrl.getText().toString().startsWith("rtmp://")
                    && !(etPlayUrl.getText().toString().startsWith("http://") && etPlayUrl.getText().toString().contains(".flv"))){
                Toast.makeText(getApplicationContext(), R.string.hit_create_invalid_url, Toast.LENGTH_SHORT).show();
            }else if (bCreating){
                Toast.makeText(getApplicationContext(), R.string.hit_creating, Toast.LENGTH_SHORT).show();
            } else{
                bCreating = true;
                CurVideoInfo.setId(ILiveSDK.getInstance().getMyUserId());
//                CurVideoInfo.setPlayUrl("rtmp://3700.liveplay.myqcloud.com/live/3700_47ac8a61ba3211e69776e435c87f075e");
                CurVideoInfo.setPlayUrl(etPlayUrl.getText().toString());
                pushHelper.startPush(new ReqPush(UserInfo.getInstance().getAccount())
                        .setTitle(etTitle.getText().toString())
                        .setAvSupport(cs_interact.getChecked())
                        .setDesciption(etDesc.getText().toString())
                        .setPlayUrl(etPlayUrl.getText().toString()));
            }
            break;
        }
    }



    @Override
    public void onPushSuccess(RspPush push) {
        CurVideoInfo.setPushId(push.getPushid());
        CurVideoInfo.setHost(true);    // 视频发布者
        if (cs_interact.getChecked()){
            startActivity(new Intent(CreateActivity.this, MixPlayActivity.class));
        }else {
            startActivity(new Intent(CreateActivity.this, PlayActivity.class));
        }
        finish();
    }

    @Override
    public void onRequestFailed(String module, int errCode, String errMsg) {
        Toast.makeText(this, module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
        bCreating = false;
    }

    @Override
    public void onStopPushSuccess(Rsp rsp) {

    }
}
