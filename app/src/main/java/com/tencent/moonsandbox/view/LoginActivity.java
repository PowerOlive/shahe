package com.tencent.moonsandbox.view;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tencent.moonsandbox.R;
import com.tencent.moonsandbox.model.UserInfo;
import com.tencent.moonsandbox.presenters.LoginHelper;
import com.tencent.moonsandbox.presenters.viewif.ViewLogin;
import com.tencent.moonsandbox.utils.MLog;
import com.tencent.moonsandbox.utils.MoonFunc;

/**
 * Created by tencent on 2016/8/18.
 */

public class LoginActivity extends MoonActivity implements ViewLogin, View.OnClickListener{
    private final static String TAG = "LoginActivity";
    private LinearLayout llLogin;
    private EditText etAccount, etPassword;
    private LoginHelper loginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        llLogin = (LinearLayout)findViewById(R.id.ll_login);
        etAccount = (EditText)findViewById(R.id.et_account);
        etPassword = (EditText)findViewById(R.id.et_password);

        UserInfo.getInstance().getCache(getApplicationContext());

        loginHelper = new LoginHelper(this, this);
        if (!TextUtils.isEmpty(UserInfo.getInstance().getAccount())
                && !TextUtils.isEmpty(UserInfo.getInstance().getUserSig())){
            // 使用sig登录
            loginHelper.loginSig(UserInfo.getInstance().getAccount(),
                    UserInfo.getInstance().getUserSig());
        }else{
            llLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        if (TextUtils.isEmpty(etAccount.getText().toString()) && TextUtils.isEmpty(etPassword.getText().toString())){
            etAccount.setText(UserInfo.getInstance().getAccount());
            etPassword.setText(UserInfo.getInstance().getPassword());
        }
        super.onResume();
    }

    @Override
    public void onLoginSuccess(String account, String userSig) {
        MLog.d(TAG, "onLoginSuccess->enter with account: "+account);
        UserInfo.getInstance().setAccount(account);
        UserInfo.getInstance().setUserSig(userSig);
        UserInfo.getInstance().writeToCache(getApplicationContext());
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onLoginFailed(String module, int errCode, String errMsg) {
        if (View.VISIBLE != llLogin.getVisibility()){
            llLogin.setVisibility(View.VISIBLE);
        }
        Toast.makeText(this, module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.btn_login:
            if (TextUtils.isEmpty(etAccount.getText().toString())
                    || TextUtils.isEmpty(etPassword.getText().toString())){
                Toast.makeText(this, R.string.tip_input_empty, Toast.LENGTH_SHORT).show();
            }else if (MoonFunc.compileExChar(etAccount.getText().toString())){
                Toast.makeText(this, R.string.hit_special_chat, Toast.LENGTH_SHORT).show();
            }else{
                loginHelper.login(etAccount.getText().toString(),
                        etPassword.getText().toString());
            }
            break;
        case R.id.tv_regist:
            startActivity(new Intent(this, RegistActivity.class));
            break;
        }
    }
}
