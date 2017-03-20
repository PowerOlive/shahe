package com.tencent.moonsandbox.presenters;

import android.os.AsyncTask;

import com.tencent.moonsandbox.model.json.ReqQuery;
import com.tencent.moonsandbox.model.json.Rsp;
import com.tencent.moonsandbox.model.json.RspQuery;
import com.tencent.moonsandbox.presenters.viewif.ViewList;

/**
 * Created by tencent on 2016/8/16.
 */

public class ListHelper {
    private ViewList mView;
    private GetLiveListTask mGetLiveListTask;

    public ListHelper(ViewList view){
        mView = view;
    }


    /**
     * 获取后台数据接口
     */
    class GetLiveListTask extends AsyncTask<Integer, Integer, Rsp<RspQuery>> {

        @Override
        protected Rsp<RspQuery> doInBackground(Integer... params) {
            return OKhttpHelper.getInstance().requestPushList(new ReqQuery()
                    .setIndex(params[0])
                    .setSize(params[1]));
        }

        @Override
        protected void onPostExecute(Rsp<RspQuery> result) {
            if(null != result) {
                mView.onUpdate(result.getData());
            }else{
                mView.onUpdate(null);
            }
        }
    }




    public void update(){
        mGetLiveListTask = new GetLiveListTask();
        mGetLiveListTask.execute(0, 20);
    }
}
