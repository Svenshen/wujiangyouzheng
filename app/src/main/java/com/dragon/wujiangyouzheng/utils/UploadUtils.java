package com.dragon.wujiangyouzheng.utils;

import android.app.Activity;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dragon.wujiangyouzheng.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;


public class UploadUtils {
    private long totalLength;
    private long uploadLength;
    private PopupWindow mPopupWindow;
    private View contentView;
    private ProgressBar progresssbar;
    private TextView numberText;
    private TextView maxText;

    public void uploadFiles(final String url, final List<String> paths,
                            final HashMap<String, String>
                                    map, final Activity
                                    activity, UploadListerer uploadListerer) {
        this.uploadListerer = uploadListerer;
        showUploadPopwindow(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                File[] files = new File[paths.size()];
                for (int i = 0; i < paths.size(); i++) {
                    if (paths.get(i).endsWith(".png") || paths.get(i)
                            .endsWith(".jpg")
                            || paths.get(i).endsWith(".bmp")
                            || paths.get(i).endsWith(".pnf")
                            || paths.get(i).endsWith(".jpeg")) {
                        String name = paths.get(i).substring(paths.get(i)
                                .lastIndexOf("/") + 1, paths.get(i).lastIndexOf
                                ("" + ".")) + ".jpg";
                        File file = new File(Environment
                                .getExternalStorageDirectory()
                                .toString()
                                + File.separator
                                + activity.getPackageName() + File.separator
                                + "compression"
                                + File.separator
                                + name);
                        if (!file.getParentFile().exists())
                            file.getParentFile().mkdirs();
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                                ByteArrayOutputStream baos = ImageFactory
                                        .zoomImage(paths.get(i), 1080);
                                FileOutputStream fos = new
                                        FileOutputStream(file);
                                baos.writeTo(fos);
                                baos.flush();
                                baos.close();
                            } catch (Exception e) {
                                // e.printStackTrace();
                            }
                        }
                        files[i] = file;
                        totalLength += file.length();
                    } else {
                        files[i] = new File(paths.get(i));
                    }
                }
                StringBuffer sbUrl = new StringBuffer();
                sbUrl.append(url);
                Iterator<Map.Entry<String, String>> it = map.entrySet()
                        .iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = it.next();
                    sbUrl.append("&");
                    sbUrl.append(encode(entry.getKey()));
                    sbUrl.append("=");
                    sbUrl.append(encode(entry.getValue()));
                }
                upLoadMultiFiles(sbUrl.toString(),
                        files, activity, map);
            }
        }).start();
    }

    private String encode(String value) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return encode;
    }

    /**
     * 上传多个文件和参数
     */
    private void upLoadMultiFiles(final String url, final File[] files, final
    Activity activity, final HashMap<String, String> map) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                maxText.setText("共计:" + Formatter.formatFileSize(activity,
                        totalLength));
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                MultipartBody.Builder builder = new MultipartBody.Builder()
                        .setType
                                (MultipartBody.FORM);
                //添加文件
                if (files.length != 0) {
                    for (int i = 0; i < files.length; i++) {
                        builder.addFormDataPart("uploadfile", files[i]
                                        .getName(),
                                createCustomRequestBody(MediaType.parse
                                        (MediaTypeUtils.getMIMEType(files[i]
                                                .getName())), files[i], new
                                        ProgressListener() {
                                            @Override
                                            public void onProgress(Long totalBytes,
                                                                   Long readCount,
                                                                   Boolean done) {
                                                uploadLength += readCount;
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progresssbar.setProgress
                                                                ((int) uploadLength);
                                                        numberText.setText
                                                                ("已上传:" +
                                                                        Formatter
                                                                                .formatFileSize(
                                                                                        activity,
                                                                                        uploadLength));
                                                    }
                                                });
                                            }
                                        }));
                    }
                }
                //添加参数
                if (map != null) {
                    for (String key : map.keySet()) {
                        builder.addFormDataPart(key, map.get(key));
                    }
                }
                Request request = new Request.Builder().url(url).post(builder
                        .build()
                ).build();
                client.newCall(request).
                        enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, final
                            IOException e) {
                                for (File file : files) {
                                    file.delete();
                                }
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPopupWindow.dismiss();
                                        totalLength = 0;
                                        uploadLength = 0;
                                        ToastUtil.showToast(activity,
                                                "上传失败," +
                                                        "请重新上传!");
                                        if (uploadListerer != null) {
                                            uploadListerer.failed(e
                                                    .getMessage());
                                        }
                                    }
                                });
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, final Response
                                    response) {
                                for (File file : files) {
                                    file.delete();
                                }
                                final String string;
                                try {
                                    string = response.body()
                                            .string();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPopupWindow.dismiss();
                                            totalLength = 0;
                                            uploadLength = 0;
                                            if (uploadListerer != null) {
                                                uploadListerer.success(string);
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPopupWindow.dismiss();
                                            totalLength = 0;
                                            uploadLength = 0;
                                            ToastUtil.showToast(activity,
                                                    "上传失败!");

                                        }
                                    });
                                }
                            }
                        });
            }
        }).start();
    }

    private static RequestBody createCustomRequestBody(final MediaType
                                                               contentType,
                                                       final File file, final
                                                       ProgressListener
                                                               listener) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    //sink.writeAll(source);
                    Buffer buf = new Buffer();
                    Long remaining = contentLength();
                    for (long readCount; (readCount = source.read(buf, 2048))
                            != -1; ) {
                        sink.write(buf, readCount);
                        listener.onProgress(contentLength(), readCount,
                                remaining == 0);

                    }
                } catch (Exception e) {
                    //  e.printStackTrace();
                }
            }
        };
    }

    interface ProgressListener {
        void onProgress(Long totalBytes, Long readCount, Boolean done);

    }

    private void showUploadPopwindow(Activity activity) {
        if (mPopupWindow == null) {
            contentView = LayoutInflater.from(activity).inflate(
                    R.layout.progress_bar_horizontal, null, false);
            // 声明一个弹出框
            mPopupWindow = new PopupWindow(contentView,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, true);
            mPopupWindow.setContentView(contentView);
            progresssbar = contentView
                    .findViewById(R.id.horizontalProgressBar);
            numberText = contentView
                    .findViewById(R.id.numberText);
            maxText = contentView
                    .findViewById(R.id.maxText);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setClippingEnabled(false);
        }
        progresssbar.setProgress(0);
        numberText.setText("");
        maxText.setText("");
        mPopupWindow.showAtLocation(contentView, Gravity
                .CENTER, 0, 0);
    }

    UploadListerer uploadListerer;

    public interface UploadListerer {
        void success(String responce);

        void failed(String responce);
    }
}
