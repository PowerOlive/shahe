package com.tencent.moonsandbox.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.moonsandbox.R;
import com.tencent.moonsandbox.model.UserInfo;
import com.tencent.moonsandbox.presenters.LogoutHelper;
import com.tencent.moonsandbox.presenters.viewif.ViewLogout;
import com.tencent.moonsandbox.utils.MLog;
import com.tencent.moonsandbox.utils.MoonFunc;
import com.tencent.rtmp.TXRtmpApi;

/**
 * Created by tencent on 2016/8/16.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener, ViewLogout{
    private final static String TAG = "ProfileFragment";
    private TextView tvId, tvVersion, tvLogout;
    private LogoutHelper logoutHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvId = (TextView)view.findViewById(R.id.tv_user);
        tvVersion = (TextView)view.findViewById(R.id.tv_version);
        tvLogout = (TextView)view.findViewById(R.id.btn_exit);

        logoutHelper = new LogoutHelper(this);
        tvLogout.setOnClickListener(this);

        tvVersion.setText(getActivity().getString(R.string.app_name)
                + " "
                + MoonFunc.getAppVersion(getContext()));
        tvId.setText(UserInfo.getInstance().getAccount());
        return view;
    }

    @Override
    public void onClick(View v) {
        logoutHelper.logout();
    }

    @Override
    public void onLogout() {
        MLog.d(TAG, "onLogout->enter");
        UserInfo.getInstance().clearCache(getContext());
        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}
