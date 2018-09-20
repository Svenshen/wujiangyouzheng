package com.dragon.wujiangyouzheng.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/18.
 */

public class OkHttpUtils {
    static OkHttpUtils okHttpUtils;
    static OkHttpClient client;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Entity entity = (Entity) msg.obj;
            switch (msg.what) {
                case 0:
                    if (entity.getOnRequestListener() != null) {
                        entity.getOnRequestListener().onSuccesss(entity
                                .getResponce());
                    }
                    break;
                case 1:
                    if (entity.getOnRequestListener() != null) {
                        entity.getOnRequestListener().onfail();
                    }
                    break;
                case 2:
                    if (entity.getOnDownloadListener() != null) {
                        entity.getOnDownloadListener().onDownloadFailed();
                    }
                    break;
                case 3:
                    if (entity.getOnDownloadListener() != null) {
                        entity.getOnDownloadListener().onDownloadSuccess(
                                entity.getResponce());
                    }
                    break;
                case 4:
                    if (entity.getOnDownloadListener() != null) {
                        long[] longs = entity.getDownNum();
                        entity.getOnDownloadListener().onDownloading
                                (longs[0], longs[1]);
                    }
                    break;
            }
        }
    };


    public static OkHttpUtils getInstance() {
        if (okHttpUtils == null) {
            okHttpUtils = new OkHttpUtils();
            client = new OkHttpClient();
        }
        return okHttpUtils;
    }


    public void getems(final String mailno, final OnRequestListener
            onRequestSuccess) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url("http://211.156.193.140:8000/cotrackapi/api/track/mail/"+mailno)
                        .addHeader("version","ems_track_cn_1.0")
                        .addHeader("authenticate","szxhbtx_12kledu90sau")
                        .build();
                Response response;
                Entity entity = new Entity(onRequestSuccess, null, null);
                try {
                    response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        entity.setResponce(response
                                .body()
                                .string());
                        Message message = mHandler.obtainMessage(0, entity);
                        mHandler.sendMessage(message);
                    } else {
                        Message message = mHandler.obtainMessage(1, entity);
                        mHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    //  e.printStackTrace();
                    Message message = mHandler.obtainMessage(1, entity);
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    public void get(final String url, final OnRequestListener
            onRequestSuccess) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response;
                Entity entity = new Entity(onRequestSuccess, null, null);
                try {
                    response = client.newCall(request).execute();
                    if (response.code() == 200) {
                        entity.setResponce(response
                                .body()
                                .string());
                        Message message = mHandler.obtainMessage(0, entity);
                        mHandler.sendMessage(message);
                    } else {
                        Message message = mHandler.obtainMessage(1, entity);
                        mHandler.sendMessage(message);
                    }
                } catch (IOException e) {
                    //  e.printStackTrace();
                    Message message = mHandler.obtainMessage(1, entity);
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }


    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final
    OnDownloadListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder().url(url).build();
                final Entity entity = new Entity(null, null, listener);
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        // 下载失败
                        Message message = mHandler.obtainMessage(2, entity);
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws
                            IOException {
                        InputStream is = null;
                        byte[] buf = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        // 储存下载文件的目录
                        String savePath = isExistDir(saveDir);
                        try {
                            is = response.body().byteStream();
                            long total = response.body().contentLength();
                            File file = new File(savePath, url.substring(url
                                    .lastIndexOf("/") + 1));
                            fos = new FileOutputStream(file);
                            long sum = 0;
                            while ((len = is.read(buf)) != -1) {
                                fos.write(buf, 0, len);
                                sum += len;
                                int progress = (int) (sum * 1.0f / total * 100);
                                // 下载中
                                entity.setDownNum(new long[]{sum, total});
                                Message message = mHandler.obtainMessage(4,
                                        entity);
                                mHandler.sendMessage(message);

                            }
                            fos.flush();
                            // 下载完成
                            entity.setResponce(file
                                    .getAbsolutePath());
                            Message message = mHandler.obtainMessage(3, entity);
                            mHandler.sendMessage(message);
                        } catch (Exception e) {
                            Message message = mHandler.obtainMessage(2, entity);
                            mHandler.sendMessage(message);
                        } finally {
                            try {
                                if (is != null)
                                    is.close();
                            } catch (IOException e) {
                            }
                            try {
                                if (fos != null)
                                    fos.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                });
            }
        }).start();
    }


    public interface OnRequestListener {
        void onSuccesss(String response);

        void onfail();
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess(String path);

        /**
         * @param progress 下载进度
         */
        void onDownloading(long progress, long total);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory
                (), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    class Entity {
        OnRequestListener onRequestListener;
        String responce;
        long[] downNum;
        OnDownloadListener onDownloadListener;

        public long[] getDownNum() {
            return downNum;
        }

        public void setDownNum(long[] downNum) {
            this.downNum = downNum;
        }

        public Entity(OnRequestListener onRequestListener, String responce,
                      OnDownloadListener onDownloadListener) {
            this.onRequestListener = onRequestListener;
            this.responce = responce;
            this.onDownloadListener = onDownloadListener;
        }

        public OnRequestListener getOnRequestListener() {
            return onRequestListener;
        }

        public void setOnRequestListener(OnRequestListener onRequestListener) {
            this.onRequestListener = onRequestListener;
        }

        public String getResponce() {
            return responce;
        }

        public void setResponce(String responce) {
            this.responce = responce;
        }

        public OnDownloadListener getOnDownloadListener() {
            return onDownloadListener;
        }

        public void setOnDownloadListener(OnDownloadListener
                                                  onDownloadListener) {
            this.onDownloadListener = onDownloadListener;
        }
    }

}
