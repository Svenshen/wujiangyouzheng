package com.dragon.wujiangyouzheng.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.dragon.wujiangyouzheng.config.Configs;
import com.dragon.wujiangyouzheng.entity.UserEntity;
import com.dragon.wujiangyouzheng.utils.OkHttpUtils;
import com.dragon.wujiangyouzheng.utils.PermissionUtils;
import com.dragon.wujiangyouzheng.widget.ProgressDialog;

public class BaseActivity extends AppCompatActivity {

    public PermissionUtils permissionUtils;
    public ProgressDialog mProgressDialog;
    public OkHttpUtils okHttpUtils;
    public SharedPreferences sp;
    public UserEntity userEntity;

    static BaseActivity baseActivity;

    public static BaseActivity getbaseactivity(){
        return baseActivity;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionUtils = new PermissionUtils(this);
        mProgressDialog = new ProgressDialog(this);
        okHttpUtils = OkHttpUtils.getInstance();
        sp = getSharedPreferences(Configs.SP_USER_INFO, Context.MODE_PRIVATE);
        try {
            userEntity = JSON.parseObject(sp.getString(Configs
                            .USER_INFOMATION, ""),
                    UserEntity.class);
        } catch (Exception e) {
        }
        baseActivity = this;
    }
}
