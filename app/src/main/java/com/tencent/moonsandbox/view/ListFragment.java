package com.tencent.moonsandbox.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tencent.moonsandbox.R;
import com.tencent.moonsandbox.model.json.RspQuery;
import com.tencent.moonsandbox.presenters.ListHelper;
import com.tencent.moonsandbox.presenters.viewif.ViewList;
import com.tencent.moonsandbox.view.adapter.AdapterPushInfo;

/**
 * Created by tencent on 2016/8/16.
 */

public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ViewList{
    private static String TAG = "ListFragment";
    private ListHelper mListHelper;

    private SwipeRefreshLayout mSrlList;
    private ListView mLvList;

    private AdapterPushInfo mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        mSrlList = (SwipeRefreshLayout)view.findViewById(R.id.srl_list);
        mLvList = (ListView)view.findViewById(R.id.lv_list);

        mSrlList.setOnRefreshListener(this);

        mListHelper = new ListHelper(this);

        mListHelper.update();

        return view;
    }

    @Override
    public void onUpdate(RspQuery query) {
        mSrlList.setRefreshing(false);

        if (null != query) {
            mAdapter = new AdapterPushInfo(getContext(), R.layout.item_pushlist, query.getPushList());
            mLvList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        mListHelper.update();
    }
}
