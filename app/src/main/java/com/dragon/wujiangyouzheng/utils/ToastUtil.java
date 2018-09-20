package com.dragon.wujiangyouzheng.utils;

import android.content.res.Resources;
import android.widget.Toast;

import android.content.Context;
import android.view.Gravity;

public class ToastUtil {
    private static Toast mToast;


    public static void showToast(Context context, String info) {
        if (mToast == null) {
            mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        }
        mToast.setText(info);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}

