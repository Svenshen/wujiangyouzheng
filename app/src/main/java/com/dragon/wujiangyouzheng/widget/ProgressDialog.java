package com.dragon.wujiangyouzheng.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.dragon.wujiangyouzheng.R;

public class ProgressDialog {
    private Context context;
    private Dialog dialog;
    private String message = null;

    public ProgressDialog(Context Context) {
        this(Context, null, null);
    }

    public ProgressDialog(Context Context, String message,
                          View.OnClickListener listenner) {
        this.context = Context;
        this.message = message;
        initView();
    }

    public void setMessage(String message) {
        this.message = message;
        if (message == null) {
            dialog.findViewById(R.id.tv).setVisibility(View.GONE);
        } else {
            TextView tv = dialog.findViewById(R.id.tv);
            tv.setText(message);
            tv.setVisibility(View.VISIBLE);
        }
    }

    public void initView() {
        View myView = LayoutInflater.from(context).inflate(R.layout
                        .progress_dialog,
                null);
        if (message != null) {
            TextView tv = myView.findViewById(R.id.tv);
            tv.setVisibility(View.VISIBLE);
            tv.setText(message);
        }
        dialog = new Dialog(context, R.style.progress_dialog);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        dialog.addContentView(myView, params);
        dialog.setCanceledOnTouchOutside(false);// 触摸Dialog不消失
        dialog.setCancelable(true);// 返回键Dialog不消失
    }

    public void showDialog() {
        if (dialog == null)
            return;
        if (dialog.isShowing())
            dialog.dismiss();
        dialog.show();
    }

    public void cancelDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

}
