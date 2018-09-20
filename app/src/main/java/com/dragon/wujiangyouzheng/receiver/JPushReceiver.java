package com.dragon.wujiangyouzheng.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.dragon.wujiangyouzheng.config.Configs;


import cn.jpush.android.api.JPushInterface;

/**
 */
public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle
                    .getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.i(TAG, "[JPushReceiver] 接收到REGISTRATION_ID : " + regId);
            Log.i(TAG, "JPush注册成功");
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
                .getAction())) {
            Log.i(TAG,
                    "[JPushReceiver] 接收到自定义消息: "
                            + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            receiverCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
                .getAction())) {
            int notifactionId = bundle
                    .getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.i(TAG, "[JPushReceiver] 接收通知的NOTIFICATION_ID: " +
                    notifactionId);
            receiverNotification(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
                .getAction())) {
            Log.i(TAG, "[JPushReceiver] 点击了通知");
            openNotification(context, bundle);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
                .getAction())) {
            Log.i(TAG,
                    "[JPushReceiver] 收到RICH PUSH CALLBACK: "
                            + bundle.getString(JPushInterface.EXTRA_EXTRA));
        }
    }

    /**
     * 用户接收到通知
     *
     * @param context
     * @param bundle
     */
    private void receiverNotification(Context context, Bundle bundle) {

    }

    /**
     * 点击打开了通知
     *
     * @param context
     * @param bundle
     */
    private void openNotification(Context context, Bundle bundle) {

    }

    /**
     * 接收到自定义消息
     *
     * @param context
     * @param bundle
     */
    private void receiverCustomMessage(Context context, Bundle bundle) {
        String title = bundle
                .getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        SharedPreferences sp = context.getSharedPreferences
                (Configs.SP_USER_INFO, Context.MODE_PRIVATE);
        Log.i("message", message);
        if (message.equals("禁止登陆")) {
            sp.edit().putBoolean(Configs.IS_CAN_LOGIN, false).commit();
        } else {
            sp.edit().putBoolean(Configs.IS_CAN_LOGIN, true).commit();
        }
    }

}
