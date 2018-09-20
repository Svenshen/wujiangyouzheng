package com.dragon.wujiangyouzheng.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dragon.wujiangyouzheng.R;
import com.dragon.wujiangyouzheng.config.Configs;
import com.dragon.wujiangyouzheng.config.Urls;
import com.dragon.wujiangyouzheng.entity.EmsEntity;
import com.dragon.wujiangyouzheng.utils.OkHttpUtils;
import com.dragon.wujiangyouzheng.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MailqueryActivity extends BaseActivity implements View.OnClickListener{


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getmailquery);

        Intent i = getIntent();
        String mailno = i.getStringExtra("mailno");
        mProgressDialog.showDialog();
        okHttpUtils.getems(mailno, new OkHttpUtils
                .OnRequestListener() {
            @Override
            public void onSuccesss(String response) {
                parseData(response);
                mProgressDialog.cancelDialog();
            }

            @Override
            public void onfail() {
                mProgressDialog.cancelDialog();
                ToastUtil.showToast(MailqueryActivity.this, "服务器连接失败!");
                finish();
            }
        });


        findViewById(R.id.getmailquery_go_back).setOnClickListener(this);


    }

//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 0:
//                    textView.setText();
//                    break;
//            }
//        }
//    };


    private void parseData(String response) {
        try {


            ListView listView = findViewById(R.id.getmailquery_listview);

            List<EmsEntity> list = JSON.parseArray(JSON.parseObject(response).get("traces").toString(),EmsEntity.class);


            List<Map<String, String>> data = new ArrayList<Map<String,String>>();
            for(EmsEntity emsEntity:list){
                Map<String, String> map1 = new HashMap<String, String>();
                map1.put("remark",emsEntity.getRemark());
                map1.put("timeaddress",emsEntity.getAcceptTime()+","+emsEntity.getAcceptAddress());
                data.add(map1);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this,data,android.R.layout.simple_list_item_2,new String[]{"remark","timeaddress"},new int[]{android.R.id.text1,android.R.id.text2});

            listView.setAdapter(simpleAdapter);
//            textView.setText(response.toString());
//            mHandler.sendEmptyMessage(0);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showToast(this, response);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.getmailquery_go_back:
                finish();
                break;
        }
    }
}
