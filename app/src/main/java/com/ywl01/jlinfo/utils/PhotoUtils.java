package com.ywl01.jlinfo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by ywl01 on 2017/10/6.
 */

public class PhotoUtils {
    public static File tempFile;

    public static final int SELECT_PHOTO = 0;// 拍照
    public static final int TAKE_PHOTO = 1;// 拍照
    public static final int CAPTURE_VIDEO = 2;// 拍摄视频

    private static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    /**
     * 打开相机拍照上传
     */
    public static void takePhoto(Activity activity) {
        if (isHasPermission(activity)) {
            System.out.println("有权限");
            //獲取系統版本
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            // 激活相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 判断存储卡是否可以用，可用进行存储
            if (hasSdcard()) {
                File file = new File(Environment.getExternalStorageDirectory(), "拍照");
                if (!file.exists()) {
                    file.mkdir();
                }
                tempFile = new File(file, "uploadFile.jpg");
                try {
                    if (tempFile.exists()) {
                        tempFile.delete();
                    }
                    tempFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (currentapiVersion < 24) {
                    // 从文件中创建uri
                    Uri uri = Uri.fromFile(tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                } else {
                    //兼容android7.0 使用共享文件的形式
//                    ContentValues contentValues = new ContentValues(1);
//                    contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
//                    Uri uri = activity.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    Uri uri = FileProvider.getUriForFile(activity,"com.ywl01.jlinfo.fileprovider", tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                }
            }
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
            activity.startActivityForResult(intent, TAKE_PHOTO);
        } else {
            requestPermission(activity, TAKE_PHOTO);
        }
    }

    //选择图片上传
    public static void selectPhoto(Activity activity) {
        if (isHasPermission(activity)) {
            System.out.println("有权限");
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            activity.startActivityForResult(intent, SELECT_PHOTO);
        } else {
            requestPermission(activity, SELECT_PHOTO);
        }
    }


    /**
     * 打开相机录像
     */
    public static void startToVideo(Activity activity) {
        //獲取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Uri fileUri = null;
        File file = null;
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        try {
            file = createMediaFile();
            if (file.exists()) {
                fileUri = Uri.fromFile(file); // create a file to save the click
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currentapiVersion < 24) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the click image quality to high
        } else {
            //兼容android7.0
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            Uri uri = activity.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        // start the Video Capture Intent
        activity.startActivityForResult(intent, CAPTURE_VIDEO);
    }

    /*
   * 判断sdcard是否被挂载
   */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 创建保存录制得到的视频文件
     *
     * @return
     * @throws IOException
     */
    public static File createMediaFile() throws IOException {
        if (hasSdcard()) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES), "CameraVideos");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "VID_" + timeStamp;
            String suffix = ".mp4";
            File mediaFile = new File(mediaStorageDir + File.separator + imageFileName + suffix);
            return mediaFile;
        }
        return null;
    }


    private static boolean isHasPermission(Activity activity) {
        return MPermissionUtils.checkPermissions(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private static void requestPermission(final Activity activity, final int requestCode) {

        MPermissionUtils.requestPermissionsResult(activity, requestCode, permissions, new MPermissionUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (requestCode == TAKE_PHOTO)
                    takePhoto(activity);
                else if (requestCode == SELECT_PHOTO) {
                    selectPhoto(activity);
                }
            }

            @Override
            public void onPermissionDenied() {
                MPermissionUtils.showTipsDialog(activity);
            }
        });
    }
}
