package com.dragon.wujiangyouzheng.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.dragon.wujiangyouzheng.R;
import com.dragon.wujiangyouzheng.config.Configs;
import com.dragon.wujiangyouzheng.config.Urls;
import com.dragon.wujiangyouzheng.entity.UserEntity;
import com.dragon.wujiangyouzheng.utils.OkHttpUtils;
import com.dragon.wujiangyouzheng.utils.RequestParams;
import com.dragon.wujiangyouzheng.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements
        OnClickListener {
    UserEntity userEntity;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Editor editor = sp.edit();
                    if (remember_password.isChecked()) {
                        editor.putString(Configs.USER_NAME, username.getText
                                ().toString().trim());
                        editor.putString(Configs.PASSWORD, password.getText
                                ().toString().trim());
                    }
                    editor.commit();
                    Intent intent = new Intent();
                    if (userEntity.getRole_name().equals("法院用户")) {
                        intent.setClass(LoginActivity.this, QueryActivity
                                .class);
                    } else {
                        intent.setClass(LoginActivity.this, MainActivity.class);
                    }
                    LoginActivity.this.startActivity(intent);
                    finish();
                    break;
            }
        }
    };

    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.remember_password)
    CheckBox remember_password;
    @BindView(R.id.remember_atuo_login)
    CheckBox remember_atuo_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        boolean isFirstOpen = sp.getBoolean(Configs.IS_FIRST_OPEN, true);
        if (isFirstOpen) {
            Editor edit = sp.edit();
            edit.putBoolean(Configs.IS_FIRST_OPEN, false);
            edit.commit();
            permissionUtils.startRequestPermission(11, Manifest
                    .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                    .CAMERA);
        }
        initView();
        if (sp.getBoolean(Configs.IS_AUTOLOGIN,
                false) && !TextUtils.isEmpty(sp.getString(Configs.USER_NAME,
                "")) && !TextUtils.isEmpty(sp.getString(Configs.PASSWORD, "")
        )) {
            findViewById(R.id.submit).performClick();
        }
    }


    private void initView() {
        remember_atuo_login.setChecked(sp.getBoolean(Configs.IS_AUTOLOGIN,
                false));
        remember_atuo_login.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Editor edit = sp.edit();
                edit.putBoolean(Configs.IS_AUTOLOGIN, isChecked);
                edit.commit();
            }
        });
        remember_password.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Editor edit = sp.edit();
                edit.putBoolean(Configs.IS_REMEMBER_PASSWORD, isChecked);
                edit.commit();
            }
        });
        if (sp.getBoolean(Configs.IS_REMEMBER_PASSWORD, true)) {
            remember_password.setChecked(true);
            username.setText(sp.getString(Configs.USER_NAME, ""));
            password.setText(sp.getString(Configs.PASSWORD, ""));
        } else {
            remember_password.setChecked(false);
        }
    }

    @Override
    @OnClick({R.id.submit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                if (!sp.getBoolean(Configs.IS_CAN_LOGIN, true)) {
                    return;
                }
                String userName = username.getText().toString().trim();
                if (TextUtils.isEmpty(userName)) {
                    ToastUtil.showToast(this, "用户名为空,请输入用户名!");
                    return;
                }
                String passWard = password.getText().toString().trim();
                if (TextUtils.isEmpty(passWard)) {
                    ToastUtil.showToast(this, "用户密码为空,请输入用户密码!");
                    return;
                }
                mProgressDialog.showDialog();
                final RequestParams params = new RequestParams();
                params.put("user_login", userName);
                params.put("user_password", passWard);
                okHttpUtils.get(Urls.BASE_URL + "upload.do?method=userLogin&"
                        + params.toString(), new OkHttpUtils
                        .OnRequestListener() {
                    @Override
                    public void onSuccesss(String response) {
                        parseData(response);
                        mProgressDialog.cancelDialog();
                    }

                    @Override
                    public void onfail() {
                        mProgressDialog.cancelDialog();
                        ToastUtil.showToast(LoginActivity.this, "服务器连接失败!");
                    }
                });
                break;
            default:
                break;
        }
    }

    private void parseData(String response) {
        try {
            userEntity = JSON.parseObject(response,
                    UserEntity.class);
            sp.edit().putString(Configs.USER_INFOMATION, response).commit();
            mHandler.sendEmptyMessage(0);
        } catch (Exception e) {
            ToastUtil.showToast(this, response);
        }
    }

    public String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context
                .TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String DEVICE_ID = tm.getDeviceId();
        return DEVICE_ID;
    }
}
