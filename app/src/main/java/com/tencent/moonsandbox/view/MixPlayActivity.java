package com.tencent.moonsandbox.view;

import android.app.Dialog;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tencent on 2016/8/16.
 */

public class MixPlayActivity extends MoonActivity implements ITXLivePlayListener, View.OnClickListener, ViewPush {
    private final static String TAG = "MixPlayActivity";
    private static final int TIME_INTERVAL = 20;
    private static final int MSG_EVENT_TIMER = 0x100;
    private static final int MSG_RESUME_PLAY = 0x101;

    private static final int USERVIEW_WIDTH = 360;
    private static final int USERVIEW_HEIGHT = 480;

    private TXCloudVideoView txvvPlayerView;
    private AVRootView mRootView;
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
            mHandler.sendEmptyMessage(MSG_EVENT_TIMER);
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
            case MSG_EVENT_TIMER:
                spanTime ++;
                if (CurVideoInfo.isHost() && 0 == (spanTime%TIME_INTERVAL)){ // 发送心跳
                    pushHelper.heartBeat(new ReqHeartBeat(CurVideoInfo.getPushId(),
                            UserInfo.getInstance().getAccount(),
                            spanTime));
                }
                updateAVRootView();
                break;
            case MSG_RESUME_PLAY:
                if (null != mTxlpPlayer){
                    if (!bFirstPlay && TXLivePlayer.PLAY_TYPE_LIVE_FLV == mPlayType){
                        mTxlpPlayer.resume();
                    }else{
                        mTxlpPlayer.setPlayListener(MixPlayActivity.this);
                        mTxlpPlayer.startPlay(CurVideoInfo.getPlayUrl(), mPlayType);
                        bFirstPlay = false;
                    }
                }
                break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int result = ILiveConstants.NO_ERR;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_mixplay);
        initView();

        mTxlpPlayer = new TXLivePlayer(this);

        mTxlpPlayer.setPlayerView(txvvPlayerView);
        mTxlpPlayer.setConfig(new TXLivePlayConfig());

        if (TextUtils.isEmpty(CurVideoInfo.getPlayUrl())){
            MLog.e(TAG, "onCreate->play url is empty");
            finish();
            return;
        }
        MLog.v(TAG, "avroom: "+CurVideoInfo.getPushId()+"/"+CurVideoInfo.isHost());
        MLog.v(TAG, "play url:"+CurVideoInfo.getPlayUrl());
        if (CurVideoInfo.getPlayUrl().endsWith("flv")){
            mPlayType = TXLivePlayer.PLAY_TYPE_LIVE_FLV;
        }

        ILiveRoomManager.getInstance().initAvRootView(mRootView);
        pushHelper = new PushHelper(this);

        mPushTimer = new ShareTimerTask();
        mTimer.schedule(mPushTimer, 1000, 1000);
        if (CurVideoInfo.isHost()){
            result = ILiveRoomManager.getInstance().createRoom(CurVideoInfo.getPushId(),
                    new ILiveRoomOption(ILiveSDK.getInstance().getMyUserId())
                            .gbMode(true)
                            .imsupport(false),
                    new ILiveCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            MLog.d(TAG, "createRoom->success");
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {
                            MLog.d(TAG, "createRoom->failed:"+module+"|"+errCode+"|"+errMsg);
                            Toast.makeText(MixPlayActivity.this, getString(R.string.tip_stop_push)+":"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }else{
            result = ILiveRoomManager.getInstance().joinRoom(CurVideoInfo.getPushId(),
                    new ILiveRoomOption("")         // 房主填空，以便无视频时自动关闭
                            .imsupport(false)
                            .autoMic(false)
                            .gbMode(true)
                            .autoCamera(false),
                    new ILiveCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            MLog.d(TAG, "joinRoom->success");
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {
                            MLog.d(TAG, "joinRoom->failed:"+module+"|"+errCode+"|"+errMsg);
                        }
                    });
        }

        if (ILiveConstants.NO_ERR != result){
            Toast.makeText(MixPlayActivity.this, getString(R.string.tip_cs_failed), Toast.LENGTH_SHORT).show();
        }

        mRootView.setZOrderOnTop(true);
        mRootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
            @Override
            public void onSubViewCreated() {
                mRootView.getViewByIndex(0).setRotationMode(ILiveConstants.ROTATION_FULL_SCREEN);
            }
        });
        //mRootView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ILiveRoomManager.getInstance().onPause();
        txvvPlayerView.onPause();
        if (null != mTxlpPlayer){
            if (TXLivePlayer.PLAY_TYPE_LIVE_FLV == mPlayType){
                mTxlpPlayer.pause();
            }else{
                mTxlpPlayer.setPlayListener(null);
                mTxlpPlayer.stopPlay(true);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        ILiveRoomManager.getInstance().onResume();
        txvvPlayerView.onResume();
        mHandler.sendEmptyMessage(MSG_RESUME_PLAY);
    }

    @Override
    protected void onDestroy() {
        if (CurVideoInfo.isHost()){
            pushHelper.stopPush(CurVideoInfo.getPushId());
        }
        if (null != mTimer){
            mTimer.cancel();
            mTimer = null;
        }
        mTxlpPlayer.setPlayListener(null);
        mTxlpPlayer.stopPlay(false);
        txvvPlayerView.onDestroy();
        ILiveRoomManager.getInstance().onDestory();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        slowQuit(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.tv_horizontal:
            mTxlpPlayer.setRenderRotation(bHorizontal ? TXLiveConstants.RENDER_ROTATION_PORTRAIT : TXLiveConstants.RENDER_ROTATION_LANDSCAPE);
            bHorizontal = !bHorizontal;
            break;
        case R.id.tv_exit:
            slowQuit(true);
            break;
        }
    }

    private void initView(){
        txvvPlayerView = (TXCloudVideoView)findViewById(R.id.txcvv_player);
        mRootView = (AVRootView)findViewById(R.id.avrv_screen);
        tvLog = (TextView)findViewById(R.id.tv_log);
    }

    /**
     * 采用较温合方式退出(退出前释放资源)
     */
    private void slowQuit(final boolean bReadyToFinish){
        ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                if (bReadyToFinish){
                    finish();
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(MixPlayActivity.this, "Quit AV Room Failed:"+module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
                if (bReadyToFinish){
                    finish();
                }
            }
        });

        mTxlpPlayer.setPlayListener(null);
        mTxlpPlayer.stopPlay(false);
    }

    private void addLog(String strInfo){
        mStrLog += strInfo + "\r\n";
        tvLog.setText(mStrLog);
    }

    private void showEndDialog(){
        if (isFinishing()){     // 应用结束后忽略
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
        slowQuit(false);
        mEndDialog.show();
    }

    // 更新AVRootView尺寸
    private void updateAVRootView(){
        if (null != mRootView){
            int imgWidth, imgHeight;
            ViewGroup.LayoutParams lp = mRootView.getLayoutParams();
            lp.height = 0;
            lp.width = 0;
            //MLog.d("XDBG", "view height: "+lp.height);
            for (int i=0; i< ILiveConstants.MAX_AV_VIDEO_NUM; i++){
                AVVideoView avVideoView = mRootView.getViewByIndex(i);
                if (avVideoView.isFirstFrameRecved() && avVideoView.isRendering()){  // 拥有画面
                    MLog.d("XDBG", "view:"+i+"|"+avVideoView.getImageWidth()+", "+avVideoView.getImageHeight()+"|"+avVideoView.getImageAngle()+"/"+avVideoView.getIdentifier());

                    avVideoView.setPosTop(lp.height);
                    avVideoView.setPosLeft(0);

                    if (true || ILiveSDK.getInstance().getMyUserId().equals(avVideoView.getIdentifier())){     // 主播不切换
                        imgHeight = avVideoView.getImageWidth();
                        imgWidth = avVideoView.getImageHeight();
                    }else {
                        if (0x1 == (avVideoView.getImageAngle() & 0x1)) {
                            imgHeight = avVideoView.getImageWidth();
                            imgWidth = avVideoView.getImageHeight();
                        } else {
                            imgWidth = avVideoView.getImageWidth();
                            imgHeight = avVideoView.getImageHeight();
                        }
                    }

                    if (imgHeight >= imgWidth){
                        imgWidth = (imgWidth * USERVIEW_HEIGHT / imgHeight);
                        imgHeight = USERVIEW_HEIGHT;
                    }else{
                        imgHeight = (imgHeight * USERVIEW_WIDTH  / imgWidth);
                        imgWidth = USERVIEW_WIDTH;
                    }

                    avVideoView.setPosWidth(imgWidth);
                    avVideoView.setPosHeight(imgHeight);
                    MLog.d("XDBG", "final size:"+imgWidth+", "+imgHeight);

                    lp.height += imgHeight;
                    if (imgWidth > lp.width){
                        lp.width = imgWidth;
                    }
                    //MLog.d("XDBG", "view size:"+avVideoView.getPosWidth()+","+avVideoView.getImageHeight());
                    avVideoView.autoLayout();
                }
            }

            if (0 == lp.width && 0 == lp.height){   // 无视频时隐藏
                if (View.INVISIBLE != mRootView.getVisibility()) {
                    mRootView.setVisibility(View.INVISIBLE);
                }
            }else{
                if (View.VISIBLE != mRootView.getVisibility()) {
                    mRootView.setVisibility(View.VISIBLE);
                }
                mRootView.setLayoutParams(lp);
            }
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (TXLiveConstants.PLAY_EVT_PLAY_PROGRESS == event){       // 忽略process事件
            return;
        }
        MLog.v(TAG, "onPlayEvent->event:"+event+"|"+param.getString(TXLiveConstants.EVT_DESCRIPTION));
        addLog("event:"+event+"|"+param.getString(TXLiveConstants.EVT_DESCRIPTION));
        //错误还是要明确的报一下
        if (event < 0) {
            Toast.makeText(getApplicationContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
            tvLog.setVisibility(View.VISIBLE);
        }

        if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            showEndDialog();
        }else if (TXLiveConstants.PLAY_EVT_PLAY_BEGIN == event){
            tvLog.setVisibility(View.GONE);
        }else if (TXLiveConstants.PLAY_EVT_PLAY_LOADING == event){
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
        Toast.makeText(this, module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
    }
}
