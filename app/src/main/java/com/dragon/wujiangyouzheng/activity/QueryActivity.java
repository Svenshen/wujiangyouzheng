package com.dragon.wujiangyouzheng.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dragon.wujiangyouzheng.R;
import com.dragon.wujiangyouzheng.adapter.QueryAdapter;
import com.dragon.wujiangyouzheng.config.Configs;
import com.dragon.wujiangyouzheng.config.Urls;
import com.dragon.wujiangyouzheng.entity.QueryEntity;
import com.dragon.wujiangyouzheng.entity.UserEntity;
import com.dragon.wujiangyouzheng.utils.OkHttpUtils;
import com.dragon.wujiangyouzheng.utils.PupopWindowChoicePic;
import com.dragon.wujiangyouzheng.utils.RequestParams;
import com.dragon.wujiangyouzheng.utils.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.xudaojie.qrcodelib.CaptureActivity;

public class QueryActivity extends BaseActivity implements View
        .OnClickListener {
    @BindView(R.id.recy)
    RecyclerView recy;
    @BindView(R.id.go_back)
    ImageView go_back;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.et_search2)
    EditText et_search2;
    @BindView(R.id.et_search3)
    EditText et_search3;
    @BindView(R.id.fram_date)
    FrameLayout fram_date;
    @BindView(R.id.tv_timebtn0)
    TextView tv_timebtn0;
    @BindView(R.id.tv_timebtn1)
    TextView tv_timebtn1;
    QueryAdapter queryAdapter;
    public List<QueryEntity> queryEntities;
    int year1;
    int moth1;
    int day1;
    int year2;
    int moth2;
    int day2;
    private int currentDatePicker;

    public static Context context;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.bind(this);
        initView();
        initData();
        context = getApplicationContext();
    }


    private void initView() {
        Calendar c = Calendar.getInstance();
        year2 = c.get(Calendar.YEAR);
        moth2 = c.get(Calendar.MONTH) + 1;
        day2 = c.get(Calendar.DAY_OF_MONTH);
        tv_timebtn1.setText(formatTime(c.get(Calendar.YEAR)) + "-"
                + formatTime(c.get(Calendar.MONTH) + 1) + "-" + formatTime(c
                .get(Calendar
                        .DATE)));
        c.add(Calendar.MONTH, -1);
        year1 = c.get(Calendar.YEAR);
        moth1 = c.get(Calendar.MONTH) + 1;
        day1 = c.get(Calendar.DAY_OF_MONTH);
        tv_timebtn0.setText(formatTime(c.get(Calendar.YEAR)) + "-"
                + formatTime(c.get(Calendar.MONTH) + 1) + "-" + formatTime(c
                .get(Calendar
                        .DATE)));
        if (userEntity.getRole_name().equals("法院用户")) {
            go_back.setVisibility(View.GONE);
        } else {
            go_back.setVisibility(View.VISIBLE);
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recy.setLayoutManager(linearLayoutManager);
        recy.addItemDecoration(new SpaceItemDecoration(30));
        queryAdapter = new QueryAdapter(queryEntities);
        queryAdapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout
                .empty_view, null));
        et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_search2.setText("");
                    et_search3.setText("");
                }
            }
        });
        et_search2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_search.setText("");
                    et_search3.setText("");
                }
            }
        });
        et_search3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et_search.setText("");
                    et_search2.setText("");
                }
            }
        });
    }

    private void initData() {
        RequestParams requestParams = new RequestParams();
        requestParams.put("user_id", userEntity
                .getUser_id() + "");
        requestParams.put("startDate", formatTime(year1) + "-" +
                formatTime(moth1) + "-" +
                formatTime(day1));
        requestParams.put("endDate", formatTime(year2) + "-" +
                formatTime(moth2) + "-" +
                formatTime(day2));
        requestParams.put("mail_code", et_search.getText().toString().trim());
        requestParams.put("user_name", et_search2.getText().toString().trim());
        requestParams.put("user_code", et_search3.getText().toString().trim());
        okHttpUtils.get(Urls.BASE_URL + "upload" +
                ".do?method=downloadPhotoAndMailList&" + requestParams
                .toString(), new OkHttpUtils
                .OnRequestListener() {
            @Override
            public void onSuccesss(String response) {
                try {
                    queryEntities = JSONArray.parseArray(response,
                            QueryEntity.class);
                    queryAdapter.setNewData(queryEntities);
                    recy.setAdapter(queryAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                    queryAdapter.setNewData(null);
                }
            }

            @Override
            public void onfail() {

            }
        });
    }

    @Override
    @OnClick({R.id.go_back, R.id.date_icon, R.id.sys, R.id.search, R.id
            .search2, R.id.search3, R.id.lin_timebtn0, R.id.lin_timebtn1, R.id
            .tv_date_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go_back:
                finish();
                break;
            case R.id.date_icon:
                fram_date.setVisibility(View.VISIBLE);
                break;
            case R.id.sys:
                if (!permissionUtils.checkRequestPermission(Manifest
                        .permission.CAMERA, Manifest.permission
                        .READ_EXTERNAL_STORAGE)) {
                    permissionUtils.startRequestPermission(111, Manifest
                            .permission.CAMERA, Manifest.permission
                            .READ_EXTERNAL_STORAGE);
                } else {
                    Intent i = new Intent(this, CaptureActivity.class);
                    startActivityForResult(i, 114);
                }
                break;
            case R.id.search:
                initData();
                break;
            case R.id.search2:
                initData();
                break;
            case R.id.search3:
                initData();
                break;
            case R.id.lin_timebtn0:
                currentDatePicker = 1;
                showdialog(1);
                break;
            case R.id.lin_timebtn1:
                currentDatePicker = 2;
                showdialog(2);
                break;
            case R.id.tv_date_confirm:
                fram_date.setVisibility(View.GONE);
                initData();
                break;

        }
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new
            DatePickerDialog
                    .OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int
                        monthOfYear,
                                      int dayOfMonth) {
                    if (currentDatePicker == 1) {
                        year1 = year;
                        moth1 = monthOfYear + 1;
                        day1 = dayOfMonth;
                        tv_timebtn0.setText(year + "-" + moth1 + "-" +
                                dayOfMonth);
                    } else if (currentDatePicker == 2) {
                        year2 = year;
                        moth2 = monthOfYear + 1;
                        day2 = dayOfMonth;
                        tv_timebtn1.setText(year + "-" + moth2 + "-" +
                                dayOfMonth);
                    }
                }
            };

    public void showdialog(int id) {
        if (id == 1) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    mdateListener, year1, moth1 - 1, day1);
            Window window = datePickerDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            /* 实例化Window操作者 */
            lp.x = 0; // 新位置X坐标
            lp.y = 380; // 新位置Y坐标
            window.setAttributes(lp);
            /* 放置属性 */
            datePickerDialog.show();
        } else if (id == 2) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    mdateListener, year2, moth2 - 1, day2);
            Window window = datePickerDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            /* 实例化Window操作者 */
            lp.x = 0; // 新位置X坐标
            lp.y = 380; // 新位置Y坐标
            window.setAttributes(lp);
            datePickerDialog.show();
        }
    }

    public String formatTime(int time) {
        if (time < 10)
            return "0" + time;
        else return "" + time;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[]
                                                   grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode == 111) {
            if (permissionUtils.checkRequestPermission(Manifest
                    .permission.CAMERA, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
                Intent i = new Intent(this, CaptureActivity.class);
                startActivityForResult(i, 114);
            }
        }
        permissionUtils.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 114 && resultCode == RESULT_OK
                && intent != null) {
            String result = intent.getStringExtra("result");
            et_search.setText(result);
            findViewById(R.id.search).performClick();
        }
    }

}
