package com.dragon.wujiangyouzheng.activity;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dragon.wujiangyouzheng.R;
import com.dragon.wujiangyouzheng.adapter.MainGridAdapter;
import com.dragon.wujiangyouzheng.config.Urls;
import com.dragon.wujiangyouzheng.utils.PermissionUtils;
import com.dragon.wujiangyouzheng.utils.PupopWindowChoicePic;
import com.dragon.wujiangyouzheng.utils.SpaceItemDecoration;
import com.dragon.wujiangyouzheng.utils.ToastUtil;
import com.dragon.wujiangyouzheng.utils.UploadUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.xudaojie.qrcodelib.CaptureActivity;
import ru.truba.touchgallery.GallerActivity;

public class MainActivity extends BaseActivity implements MainGridAdapter
        .OnItemClickListener, View.OnClickListener {

    @BindView(R.id.confirm)
    TextView confirm;
    @BindView(R.id.odd_numbers)
    EditText odd_numbers;
    @BindView(R.id.recy)
    RecyclerView recy;
    ArrayList<String> paths = new ArrayList<>();
    MainGridAdapter adapter;
    String cameraPath;

    public String getMac() {
        WifiManager wifi = (WifiManager) getApplicationContext()
                .getSystemService(Context
                        .WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recy.setLayoutManager(gridLayoutManager);
        recy.addItemDecoration(new SpaceItemDecoration(30));
        paths.add(null);
        adapter = new MainGridAdapter(paths, this);
        adapter.setOnItemClickListener(this);
        recy.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {
        if (position == paths.size() - 1 && paths.get(paths.size() - 1) ==
                null) {
            if (!permissionUtils.checkRequestPermission(Manifest
                    .permission.CAMERA, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
                permissionUtils.startRequestPermission(112, Manifest
                        .permission.CAMERA, Manifest.permission
                        .READ_EXTERNAL_STORAGE);
            } else {
                PupopWindowChoicePic pupopWindowChoicePic = new
                        PupopWindowChoicePic(this);
                pupopWindowChoicePic.showPupopWindow();
                pupopWindowChoicePic.setmChoiceCameraListener(new PupopWindowChoicePic.ChoiceCameraListener() {
                    @Override
                    public void onchoiceCamera(String cameraPath) {
                        MainActivity.this.cameraPath = cameraPath;
                    }
                });
            }
        } else {
            Intent intent = new Intent(this, GallerActivity.class);
            intent.putExtra("index", position);
            ArrayList showPaths = new ArrayList<String>();
            for (String path : paths) {
                if (path != null) showPaths.add(path);
            }
            intent.putExtra("paths", showPaths);
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(final int position) {
        if (position == paths.size() - 1 && paths.get(paths.size() - 1) ==
                null) {
        } else {
            // 创建构建器
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // 设置参数
            builder
                    .setMessage("您确定删除这张图片吗?").setPositiveButton("确定", new
                    DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            paths.remove(position);
                            if (paths.size() == 3 && paths.get(paths.size() -
                                    1) !=
                                    null) {
                                paths.add(null);
                            }
                            adapter.refresh(paths);
                            dialog.dismiss();
                        }
                    }).setNegativeButton("取消", new DialogInterface
                    .OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 112 && resultCode == Activity.RESULT_OK) {
            paths.add(paths.size() - 1, cameraPath);
            adapter.refresh(paths);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + cameraPath)));
        } else if (requestCode == 123 && resultCode == RESULT_OK
                && intent != null) {
            Uri selectedImage = intent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            paths.add(paths.size() - 1, picturePath);
            adapter.refresh(paths);
        } else if (requestCode == 114 && resultCode == RESULT_OK
                && intent != null) {
            String result = intent.getStringExtra("result");
            odd_numbers.setText(result);
        }
    }

    @Override
    @OnClick({R.id.confirm, R.id.capture, R.id.scan})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                if (TextUtils.isEmpty(odd_numbers.getText().toString().trim()
                )) {
                    ToastUtil.showToast(this, "请输入单号!");
                    return;
                }
                if (paths.size() < 2) {
                    ToastUtil.showToast(this, "请至少上传一张照片!");
                    return;
                }
                confirm.setClickable(false);
                uploadFiles();
                break;
            case R.id.capture:
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
            case R.id.scan:
                Intent i = new Intent(this, QueryActivity.class);
                startActivity(i);
                break;
        }
    }

    public void uploadFiles() {
        confirm.setClickable(false);
        List<String> list = new ArrayList<>();
        for (String path : paths) {
            if (path != null) list.add(path);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("mail_code", odd_numbers.getText().toString().trim());
        map.put("user_id", userEntity.getUser_id() + "");
        new UploadUtils().uploadFiles(Urls.BASE_URL + "upload" +
                ".do?method=uploadPhotoAndMail", list, map, this, new
                UploadUtils.UploadListerer() {
                    @Override
                    public void success(String responce) {
                        confirm.setClickable(true);
                        if (responce.equals("1")) {
                            paths.clear();
                            paths.add(null);
                            adapter.refresh(paths);
                            odd_numbers.setText("");
                            ToastUtil.showToast(MainActivity.this, "上传成功!");
                        } else {
                            ToastUtil.showToast(MainActivity.this, "上传失败," +
                                    "请重新上传!");
                        }
                    }

                    @Override
                    public void failed(String responce) {
                        confirm.setClickable(true);
                        ToastUtil.showToast(MainActivity.this, "上传失败," +
                                "请重新上传!");
                    }
                });
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
        } else if (requestCode == 112) {
            if (permissionUtils.checkRequestPermission(Manifest
                    .permission.CAMERA, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
                PupopWindowChoicePic pupopWindowChoicePic = new
                        PupopWindowChoicePic(this);
                pupopWindowChoicePic.showPupopWindow();
                pupopWindowChoicePic.setmChoiceCameraListener(new PupopWindowChoicePic.ChoiceCameraListener() {
                    @Override
                    public void onchoiceCamera(String cameraPath) {
                        MainActivity.this.cameraPath = cameraPath;
                    }
                });
            }
        }
        permissionUtils.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }
}
