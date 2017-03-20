package com.tencent.moonsandbox.presenters;

import android.os.AsyncTask;

import com.tencent.moonsandbox.model.json.ReqHeartBeat;
import com.tencent.moonsandbox.model.json.ReqPush;
import com.tencent.moonsandbox.model.json.Rsp;
import com.tencent.moonsandbox.model.json.RspPush;
import com.tencent.moonsandbox.presenters.viewif.ViewPush;
import com.tencent.moonsandbox.utils.MLog;

/**
 * Created by tencent on 2016/8/23.
 */

public class PushHelper {
    private static final String TAG = "PushHelper";
    ViewPush viewPush;
    private PushTask pushTask;
    private StopPushTask stopPushTask;
    private HeartBeatTask heartBeatTask;

    public PushHelper(ViewPush view){
        viewPush = view;
    }

    public void startPush(ReqPush pushReq){
        pushTask = new PushTask();
        pushTask.execute(pushReq);
    }

    public void stopPush(int pushId){
        stopPushTask = new StopPushTask();
        stopPushTask.execute(pushId);
    }

    public void heartBeat(ReqHeartBeat hb){
        heartBeatTask = new HeartBeatTask();
        heartBeatTask.execute(hb);
    }

    /**
     * 开始推流
     */
    class PushTask extends AsyncTask<ReqPush, Integer, Rsp<RspPush>> {

        @Override
        protected Rsp<RspPush> doInBackground(ReqPush... params) {
            return OKhttpHelper.getInstance().notifyServerStartPush(params[0]);
        }

        @Override
        protected void onPostExecute(Rsp<RspPush> result) {
            if(null != result) {
                MLog.v(TAG, "do push result:"+result.toString());
                if (0 == result.getErrerCode()){
                    viewPush.onPushSuccess(result.getData());
                }else{
                    viewPush.onRequestFailed("SERVER", result.getErrerCode(), result.getErrorInfo());
                }
            }
        }
    }

    /**
     * 结束推流
     */
    class StopPushTask extends AsyncTask<Integer, Integer, Rsp> {

        @Override
        protected Rsp doInBackground(Integer... params) {
            return OKhttpHelper.getInstance().notifyServerStopPush(params[0]);
        }

        @Override
        protected void onPostExecute(Rsp result) {
            if(null != result) {
                MLog.v(TAG, "do stop push result:"+result.toString());
                if (0 == result.getErrerCode()){
                    viewPush.onStopPushSuccess(result);
                }else{
                    viewPush.onRequestFailed("SERVER", result.getErrerCode(), result.getErrorInfo());
                }
            }
        }
    }

    /**
     * 发送心跳
     */
    class HeartBeatTask extends AsyncTask<ReqHeartBeat, Integer, Rsp> {

        @Override
        protected Rsp doInBackground(ReqHeartBeat... params) {
            return OKhttpHelper.getInstance().notifyServerHeartBeat(params[0]);
        }

        @Override
        protected void onPostExecute(Rsp result) {
            if(null != result) {
                MLog.v(TAG, "heatbeat result:"+result.toString());
            }
        }
    }
}
