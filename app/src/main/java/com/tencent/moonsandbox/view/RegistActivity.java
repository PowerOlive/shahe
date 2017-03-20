package com.tencent.moonsandbox.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.moonsandbox.R;
import com.tencent.moonsandbox.model.UserInfo;
import com.tencent.moonsandbox.presenters.RegistHelper;
import com.tencent.moonsandbox.presenters.viewif.ViewRegist;
import com.tencent.moonsandbox.utils.MoonFunc;

/**
 * Created by tencent on 2016/8/18.
 */

public class RegistActivity extends MoonActivity implements View.OnClickListener, ViewRegist{
    private RegistHelper registHelper;
    private EditText etAccount, etPassword, etConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        etAccount = (EditText)findViewById(R.id.et_account);
        etPassword = (EditText)findViewById(R.id.et_password);
        etConfirm = (EditText)findViewById(R.id.et_password_confirm);

        registHelper = new RegistHelper(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.btn_regist:
            registAccount();
            break;
        }
    }

    @Override
    public void onRegistSuccess(String account, String password) {
        UserInfo.getInstance().setAccount(account);
        UserInfo.getInstance().setPassword(password);
        Toast.makeText(getApplicationContext(), R.string.tip_regist_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onRegistFailed(String module, int errCode, String errMsg) {
        Toast.makeText(this, module+"|"+errCode+"|"+errMsg, Toast.LENGTH_SHORT).show();
    }

    private void registAccount(){
        if (TextUtils.isEmpty(etAccount.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            Toast.makeText(this, R.string.tip_input_empty, Toast.LENGTH_SHORT).show();
        }else if (!etPassword.getText().toString().equals(etConfirm.getText().toString())) {
            Toast.makeText(this, R.string.tip_not_match, Toast.LENGTH_SHORT).show();
        }else if (etAccount.getText().toString().length() < 4 || etAccount.getText().toString().length() > 24){
            Toast.makeText(this, R.string.hit_regist_account, Toast.LENGTH_SHORT).show();
        }else if (etPassword.getText().toString().length() < 8 || etPassword.getText().toString().length() > 16) {
            Toast.makeText(this, R.string.hit_regist_password, Toast.LENGTH_SHORT).show();
        }else if (MoonFunc.compileExChar(etAccount.getText().toString())){
            Toast.makeText(this, R.string.hit_special_chat, Toast.LENGTH_SHORT).show();
        }else{
            registHelper.regsit(etAccount.getText().toString(), etPassword.getText().toString());
        }
    }
}
