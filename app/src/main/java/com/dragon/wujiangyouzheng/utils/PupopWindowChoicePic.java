package com.dragon.wujiangyouzheng.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.dragon.wujiangyouzheng.R;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/20.
 */

public class PupopWindowChoicePic implements View.OnClickListener {
    private Fragment fragment;
    private Activity activity;
    private Activity mContext;
    private PopupWindow mPopWindow;

    public PupopWindowChoicePic(Activity activity) {
        mContext = activity;
        this.activity = activity;
    }

    public PupopWindowChoicePic(Fragment fragment) {
        mContext = fragment.getActivity();
        this.fragment = fragment;
    }

    public void showPupopWindow() {
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.popuplayout_selector_pic, null);
        mPopWindow = new PopupWindow(contentView, WindowManager.LayoutParams
                .MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        ImageView callCamera = (ImageView) contentView
                .findViewById(R.id.callCamera);
        View view = contentView.findViewById(R.id.view);
        RelativeLayout mpop_rela = (RelativeLayout) contentView
                .findViewById(R.id.mpop_rela);
        ImageView callPhoto = (ImageView) contentView
                .findViewById(R.id.callPhoto);
        view.setOnClickListener(this);
        callCamera.setOnClickListener(this);
        callPhoto.setOnClickListener(this);
        mpop_rela.setOnClickListener(this);
        mPopWindow.setContentView(contentView);
        mPopWindow.setFocusable(true);
        mPopWindow.setOutsideTouchable(true);
        mPopWindow.showAtLocation(contentView, Gravity.CENTER, 0,
                0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.callCamera) {
            mPopWindow.dismiss();
            String name = DateFormat.format("yyyyMMdd_hhmmss",
                    Calendar.getInstance(Locale.CHINA))
                    + ".jpg";
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            File file = new File(Environment.getExternalStorageDirectory()
                    .toString()
                    + File.separator
                    + mContext.getPackageName()
                    + File.separator + name); // 定义File类对象
            if (!file.getParentFile().exists()) { // 父文件夹不存在
                file.getParentFile().mkdirs(); // 创建文件夹
            }
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(mContext, mContext
                        .getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            // 获取拍照后未压缩的原图片，并保存在uri路径中
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            if (activity != null) {
                mContext.startActivityForResult(intent, 112);
            } else {
                fragment.startActivityForResult(intent, 112);
            }
            if (mChoiceCameraListener != null) {
                mChoiceCameraListener.onchoiceCamera(file.getAbsolutePath());
            }
        } else if (v.getId() == R.id.callPhoto) {
            mPopWindow.dismiss();
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (activity != null) {
                mContext.startActivityForResult(i, 123);
            } else {
                fragment.startActivityForResult(i, 123);
            }
        } else if (v.getId() == R.id.view || v.getId() == R.id.mpop_rela) {
            mPopWindow.dismiss();
        }
    }

    public void setmChoiceCameraListener(ChoiceCameraListener
                                                 mChoiceCameraListener) {
        this.mChoiceCameraListener = mChoiceCameraListener;
    }

    ChoiceCameraListener mChoiceCameraListener;

    public interface ChoiceCameraListener {
        void onchoiceCamera(String cameraPath);
    }
}
