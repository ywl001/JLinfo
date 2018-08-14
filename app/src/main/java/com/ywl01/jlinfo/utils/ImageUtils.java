package com.ywl01.jlinfo.utils;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.ywl01.jlinfo.activities.BaseActivity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ywl01 on 2017/3/20.
 * 图像缩放、压缩
 */

public class ImageUtils {
    public static final int NEW_WIDTH = 1280;
    public static final int NEW_HEIGHT = 960;
    public static final int IMAGE_MAX_SIZE = 200;//200k

    public final static String SDCARD_MNT = "/mnt/sdcard";
    public final static String SDCARD = "/sdcard";

    public static Bitmap getScaleBitmap(String path) {
        // 获取option
        BitmapFactory.Options options = new BitmapFactory.Options();
        // inJustDecodeBounds设置为true,这样使用该option decode出来的Bitmap是null，
        // 只是把长宽存放到option中
        options.inJustDecodeBounds = true;
        // 此时bitmap为null
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int inSampleSize = 1; // 1是不缩放
        // 计算宽高缩放比例
        int inSampleSizeW = options.outWidth / NEW_WIDTH;
        int inSampleSizeH = options.outHeight / NEW_HEIGHT;
        // 最终取大的那个为缩放比例，这样才能适配，例如宽缩放3倍才能适配屏幕，而
        // 高不缩放就可以，那样的话如果按高缩放，宽在屏幕内就显示不下了
        if (inSampleSizeW > inSampleSizeH) {
            inSampleSize = inSampleSizeW;
        }else {
            inSampleSize = inSampleSizeH;
        }
        // 设置缩放比例
        options.inSampleSize = inSampleSize;
        // 一定要记得将inJustDecodeBounds设为false，否则Bitmap为null
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
//        return bitmap;
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    //压缩
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > IMAGE_MAX_SIZE) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            System.out.println(baos.toByteArray().length);
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private static String[] externalStroagePermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public static void saveBitmap(final Context context,final Bitmap bitmap, final String fileName, final String fileDir)  {
        if (bitmap != null) {
            if (isHasExternalStoragePermission(context)) {
                saveBitmapToFile(context, bitmap, fileName, fileDir);
            } else {
                MPermissionUtils.requestPermissionsResult(BaseActivity.currentActivity, 10, externalStroagePermissions, new MPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        System.out.println("保存图片权限通过了。。。。。。。");
                       saveBitmapToFile(context, bitmap, fileName, fileDir);
                    }

                    @Override
                    public void onPermissionDenied() {
                        MPermissionUtils.showTipsDialog(BaseActivity.currentActivity);
                    }
                });
            }
        }
    }

    private static boolean isHasExternalStoragePermission(Context context) {
        return MPermissionUtils.checkPermissions(context, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static File saveBitmapToFile(Context context,Bitmap mBitmap,String fileName,String fileDir) {
        File dir = new File( Environment.getExternalStorageDirectory(),fileDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, fileName + ".jpg");
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
            fOut.flush();
            fOut.close();
            String saveInfo = "图片保存在" + fileDir + "文件夹下面的" + fileName;
            Toast.makeText(context,saveInfo,Toast.LENGTH_LONG).show();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap loadBitmapFromView(View view) {
        // width measure spec
        int widthSpec = View.MeasureSpec.makeMeasureSpec(
                view.getMeasuredWidth(), View.MeasureSpec.AT_MOST);
        // height measure spec
        int heightSpec = View.MeasureSpec.makeMeasureSpec(
                view.getMeasuredHeight(), View.MeasureSpec.AT_MOST);
        // measure the view
        view.measure(widthSpec, heightSpec);
        // set the layout sizes
        view.layout(view.getLeft(), view.getTop(), view.getMeasuredWidth() + view.getLeft(), view.getMeasuredHeight() + view.getTop());
        // create the bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        // create a canvas used to get the view's image and draw it on the bitmap
        Canvas c = new Canvas(bitmap);
        // position the image inside the canvas
        c.translate(-view.getScrollX(), -view.getScrollY());
        // get the canvas
        view.draw(c);

        return bitmap;
    }

}
