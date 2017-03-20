package com.tencent.moonsandbox.view;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.moonsandbox.R;
import com.tencent.moonsandbox.model.CurVideoInfo;
import com.tencent.moonsandbox.model.UserInfo;
import com.tencent.moonsandbox.model.json.ReqHeartBeat;
import com.tencent.moonsandbox.model.json.Rsp;
import com.tencent.moonsandbox.model.json.RspPush;
import com.tencent.moonsandbox.presenters.PushHelper;
import com.tencent.moonsandbox.presenters.viewif.ViewPush;
import com.tencent.moonsandbox.utils.MLog;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tencent on 2016/8/16.
 */

public class PlayActivity extends MoonActivity implements ITXLivePlayListener, View.OnClickListener, ViewPush {
    private final static String TAG = "PlayActivity";
    private static final int TIME_INTERVAL = 20;
    private static final int MSG_EVENT_TIMER = 0x100;
    private static final int MSG_RESUME_PLAY = 0x101;

    private TXCloudVideoView txvvPlayerView;
    private TextView tvLog;

    private int mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;
    private TXLivePlayer mTxlpPlayer;

    private PushHelper pushHelper;

    private Dialog mEndDialog;
    private String mStrLog = "";
    private boolean bHorizontal = false;
    private boolean bFirstPlay = true;

    private Timer mTimer = new Timer();
    private ShareTimerTask mPushTimer;
    private int spanTime;   /** 推流时长 */

    /**
     * 心跳记时器
     */
    private class ShareTimerTask extends TimerTask {
        @Override
        public void run() {
            spanTime += TIME_INTERVAL;
            mHandler.sendEmptyMessage(MSG_EVENT_TIMER);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EVENT_TIMER:
                    pushHelper.heartBeat(new ReqHeartBeat(CurVideoInfo.getPushId(),
                            UserInfo.getInstance().getAccount(),
                            spanTime));
                    break;
                case MSG_RESUME_PLAY:
                    if (null != mTxlpPlayer) {
                        if (!bFirstPlay && TXLivePlayer.PLAY_TYPE_LIVE_FLV == mPlayType) {
                            mTxlpPlayer.resume();
                        } else {
                            mTxlpPlayer.setPlayListener(PlayActivity.this);
                            mTxlpPlayer.stopPlay(true);
                            mTxlpPlayer.enableHardwareDecode(isHardCode());
                            mTxlpPlayer.startPlay(CurVideoInfo.getPlayUrl(), mPlayType);
                            bFirstPlay = false;
                        }
                    }
                    break;
            }
            return false;
        }
    });

    private boolean isHardCode() {
        //读取系统配置文件/system/etc/media_codecc.xml
        File file = new File("/system/etc/media_codecs.xml");
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (in == null) {
            android.util.Log.i("xp", "in == null");
        } else {
            android.util.Log.i("xp", "in != null");
        }
        boolean isHardcode = false;
        XmlPullParserFactory pullFactory;
        try {
            pullFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = pullFactory.newPullParser();
            xmlPullParser.setInput(in, "UTF-8");
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("MediaCodec".equals(tagName)) {
                            String componentName = xmlPullParser.getAttributeValue(0);
                            android.util.Log.i("xp", componentName);
                            if (componentName.startsWith("OMX.")) {
                                if (!componentName.startsWith("OMX.google.")) {
                                    isHardcode = true;
                                }
                            }
                        }
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        android.util.Log.i("xp", "" + isHardcode);
        return isHardcode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_play);
        initView();

        mTxlpPlayer = new TXLivePlayer(this);

        mTxlpPlayer.setPlayerView(txvvPlayerView);
        mTxlpPlayer.setConfig(new TXLivePlayConfig());
        //mTxlpPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

        if (TextUtils.isEmpty(CurVideoInfo.getPlayUrl())) {
            MLog.e(TAG, "onCreate->play url is empty");
            finish();
            return;
        }
        MLog.v(TAG, "play url:" + CurVideoInfo.getPlayUrl());
        if (CurVideoInfo.getPlayUrl().endsWith("flv")) {
            mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        }

        //mTxlpPlayer.setPlayListener(this);
        //mTxlpPlayer.startPlay(CurVideoInfo.getPlayUrl(), mPlayType);

        if (CurVideoInfo.isHost()) {
            pushHelper = new PushHelper(this);

            mPushTimer = new ShareTimerTask();
            mTimer.schedule(mPushTimer, TIME_INTERVAL * 1000, TIME_INTERVAL * 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        txvvPlayerView.onPause();
        if (null != mTxlpPlayer) {
            if (TXLivePlayer.PLAY_TYPE_LIVE_FLV == mPlayType) {
                mTxlpPlayer.pause();
            } else {
                mTxlpPlayer.setPlayListener(null);
                mTxlpPlayer.stopPlay(true);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        txvvPlayerView.onResume();
        mHandler.sendEmptyMessage(MSG_RESUME_PLAY);
    }

    @Override
    protected void onDestroy() {
        if (CurVideoInfo.isHost()) {
            pushHelper.stopPush(CurVideoInfo.getPushId());
        }
        mTxlpPlayer.stopPlay(false);
        txvvPlayerView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_horizontal:
                mTxlpPlayer.setRenderRotation(bHorizontal ? TXLiveConstants.RENDER_ROTATION_PORTRAIT : TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
                bHorizontal = !bHorizontal;
                break;
            case R.id.tv_exit:
                finish();
                break;
        }
    }

    private void initView() {
        txvvPlayerView = (TXCloudVideoView) findViewById(R.id.txcvv_player);
        tvLog = (TextView) findViewById(R.id.tv_log);
    }

    private void addLog(String strInfo) {
        mStrLog += strInfo + "\r\n";
        tvLog.setText(mStrLog);
    }

    private void showEndDialog() {
        if (isFinishing()) {     // 应用结束后忽略
            return;
        }
        if (null == mEndDialog) {
            mEndDialog = new Dialog(this, R.style.moon_dialog);
            mEndDialog.setContentView(R.layout.dialog_end);
            TextView tvEnd = (TextView) mEndDialog.findViewById(R.id.tv_end);
            tvEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        mEndDialog.show();
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (TXLiveConstants.PLAY_EVT_PLAY_PROGRESS == event) {       // 忽略process事件
            return;
        }
        MLog.v(TAG, "onPlayEvent->event:" + event + "|" + param.getString(TXLiveConstants.EVT_DESCRIPTION));
        addLog("event:" + event + "|" + param.getString(TXLiveConstants.EVT_DESCRIPTION));
        //错误还是要明确的报一下
        if (event < 0) {
            Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            tvLog.setVisibility(View.VISIBLE);
        }

        if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            showEndDialog();
        } else if (TXLiveConstants.PLAY_EVT_PLAY_BEGIN == event) {
            tvLog.setVisibility(View.GONE);
        } else if (TXLiveConstants.PLAY_EVT_PLAY_LOADING == event) {
            tvLog.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Override
    public void onPushSuccess(RspPush push) {

    }

    @Override
    public void onStopPushSuccess(Rsp rsp) {

    }

    @Override
    public void onRequestFailed(String module, int errCode, String errMsg) {
        Toast.makeText(this, module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
    }
}
