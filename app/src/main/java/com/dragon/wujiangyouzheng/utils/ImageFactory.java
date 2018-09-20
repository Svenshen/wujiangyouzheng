package com.dragon.wujiangyouzheng.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.R.attr.scaleHeight;
import static android.R.attr.scaleWidth;
import static android.R.attr.width;

public class ImageFactory {

    public static ByteArrayOutputStream zoomImage(String filePath,
                                                  int newWidth) {
        File file = new File(filePath);
        Bitmap bgimage;
        int bitmapRotateAngle = getBitmapRotateAngle(filePath);
        if (bitmapRotateAngle != 0) {
            bgimage = creatBitmap(filePath, bitmapRotateAngle);
        } else {
            bgimage = BitmapFactory.decodeFile(filePath);
        }
        int width = bgimage.getWidth();
        int height = bgimage.getHeight();
        float scale = width / newWidth > 1 ? newWidth * 1.0f / width : 1;
        Bitmap bitmap = Bitmap.createScaledBitmap(bgimage, (int) (width *
                        scale),
                (int) (height * scale), true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length > file.length()
                || baos.toByteArray().length / 1024 > 500) {
            baos.reset();
            options -= 3;
            if (options < 5)
                break;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        bgimage.recycle();
        bitmap.recycle();
        return baos;
    }

    /**
     * 获取图片的旋转角度
     *
     * @param imgPath 图片路径
     * @return 返回旋转角度
     */
    public static int getBitmapRotateAngle(String imgPath) {
        // 判断图片方向
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }

        }
        return digree;
    }

    /**
     * @param path   图片路径
     * @param digree 旋转角度
     * @return
     */
    public static Bitmap creatBitmap(String path, int digree) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Bitmap bitmapRe;
        // 旋转图片
        Matrix m = new Matrix();
        m.postRotate(digree);
        bitmapRe = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), m, true);
        if (bitmap != bitmapRe) {
            bitmap.recycle();
        }
        return bitmapRe;

    }
}
