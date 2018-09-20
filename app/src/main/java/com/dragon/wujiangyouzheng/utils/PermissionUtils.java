package com.dragon.wujiangyouzheng.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/27.
 */

public class PermissionUtils {
    private static Activity activity;
   public PermissionUtils(Activity activity){
       this.activity=activity;
   }
    // 检验请求权限
    public boolean checkRequestPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> checkPermissions = new ArrayList<>();
            // 检查该权限是否已经获取
            for (String permission : permissions) {
                int i = ContextCompat.checkSelfPermission(activity, permission);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    checkPermissions.add(permission);
                }
            }
            if (checkPermissions.size() > 0)
                return false;
        }
        return true;
    }

    // 开始提交请求权限
    public void startRequestPermission(int requestCode,String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> checkPermissions = new ArrayList<>();
            // 检查该权限是否已经获取
            for (String permission : permissions) {
                int i = ContextCompat.checkSelfPermission(activity, permission);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    checkPermissions.add(permission);
                }
            }
            if (checkPermissions.size() > 0) {
                String[] array1 = checkPermissions.toArray(new
                        String[checkPermissions.size()]);
                ActivityCompat.requestPermissions(activity, array1, requestCode);
            }
        }
    }

    // 用户权限 申请 的回调方法
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                        boolean b = activity
                                .shouldShowRequestPermissionRationale
                                        (permissions[i]);
                        if (b) {
                            // 用户还是想用我的 APP 的
                            // 提示用户去应用设置界面手动开启权限
                            showDialogTipUserGoToAppSettting();
                            break;
                        }
                    }
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("权限申请")
                .setMessage("为保证APP正常运行,请在-应用设置-权限-中，允许常规需要的权限!")
                .setPositiveButton("立即开启", new DialogInterface
                        .OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }
}
