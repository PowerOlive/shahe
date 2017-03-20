package com.tencent.moonsandbox.presenters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.moonsandbox.model.json.ReqHeartBeat;
import com.tencent.moonsandbox.model.json.ReqPush;
import com.tencent.moonsandbox.model.json.ReqQuery;
import com.tencent.moonsandbox.model.json.ReqStop;
import com.tencent.moonsandbox.model.json.Rsp;
import com.tencent.moonsandbox.model.json.RspPush;
import com.tencent.moonsandbox.model.json.RspQuery;
import com.tencent.moonsandbox.utils.MLog;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 网络请求类
 */
public class OKhttpHelper {
    private static final String TAG = "OKhttpHelper";

    private static final String SERVER = "182.254.234.225";

    private static final String PATH_STARTPUSH = "/moon/index.php?svc=push&cmd=start";
    private static final String PATH_STOPPUSH = "/moon/index.php?svc=push&cmd=stop";
    private static final String PATH_HEARTBEAT = "/moon/index.php?svc=push&cmd=heartbeat";
    private static final String PATH_QUERY = "/moon/index.php?svc=push&cmd=query";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static OKhttpHelper instance = null;

    private OkHttpClient mClient;
    private Gson mGson;

    public static OKhttpHelper getInstance() {
        if (instance == null) {
            synchronized (OKhttpHelper.class){
                if (null == instance){
                    instance = new OKhttpHelper();
                }
            }
        }
        return instance;
    }

    private OKhttpHelper(){
        mClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        mGson = new Gson();
    }


    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build();

    private String post(String path, String json) {
        MLog.i(TAG, "post:"+json);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("http://"+SERVER+path)
                .post(body)
                .build();

        try {
            Response response = mClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        }catch (IOException e){
            MLog.e(TAG, "post failed:"+e.toString());
        }
        return null;
    }

    public Rsp<RspPush> notifyServerStartPush(ReqPush pushInfo){
        String rsp = post(PATH_STARTPUSH, mGson.toJson(pushInfo, ReqPush.class));
        MLog.d(TAG, "notify server start push rsp:"+rsp);
        return mGson.fromJson(rsp, new TypeToken<Rsp<RspPush>>(){}.getType());
    }

    public Rsp notifyServerStopPush(int pushId){
        String rsp = post(PATH_STOPPUSH, mGson.toJson(new ReqStop(pushId), ReqStop.class));
        MLog.d(TAG, "notify server stop push rsp:"+rsp);
        return mGson.fromJson(rsp, Rsp.class);
    }

    public Rsp notifyServerHeartBeat(ReqHeartBeat hb){
        String rsp = post(PATH_HEARTBEAT, mGson.toJson(hb, ReqHeartBeat.class));
        MLog.d(TAG, "notify server heartbeat:"+rsp);
        return mGson.fromJson(rsp, Rsp.class);
    }

    public Rsp<RspQuery> requestPushList(ReqQuery query){
        String rsp = post(PATH_QUERY, mGson.toJson(query, ReqQuery.class));
        MLog.d(TAG, "reqeust server pushlist:"+rsp);
        return mGson.fromJson(rsp, new TypeToken<Rsp<RspQuery>>(){}.getType());
    }
}
