package com.tencent.moonsandbox.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.tencent.moonsandbox.R;
import com.tencent.moonsandbox.model.MoonConstants;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends MoonFragmentActivity {
    private final int REQUEST_PHONE_PERMISSIONS = 0x100;

    private LayoutInflater mLayoutInflater;
    private FragmentTabHost mTabHost;
    private Class mFragmentArray[] = {ListFragment.class, PushFragment.class, ProfileFragment.class};
    private int mImageViewArray[] = {R.drawable.md_video, R.drawable.play, R.drawable.md_profile};
    private String mTagArray[] = {"list", "push", "profile"};
    private String mPermissions[] = {
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    };

    // tab标题
    private View getTabItemView(int index){
        View view = mLayoutInflater.inflate(R.layout.tab_home, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(mImageViewArray[index]);
        return view;
    }

    private void initView(){
        mLayoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost)findViewById(R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabcontent);
        int fragmentCount = mFragmentArray.length;
        for (int i = 0; i < fragmentCount; ++i) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTagArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);
        }

        mTabHost.getTabWidget().getChildTabViewAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CreateActivity.class));
            }
        });
    }

    void checkPermission() {
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : mPermissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission)) {
                    permissionsList.add(permission);
                }
            }
            if (permissionsList.size() != 0) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initView();
        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (REQUEST_PHONE_PERMISSIONS == requestCode){
            if (PackageManager.PERMISSION_GRANTED != grantResults[0]){
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                sendBroadcast(new Intent(MoonConstants.BD_EXIT_APP));
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
