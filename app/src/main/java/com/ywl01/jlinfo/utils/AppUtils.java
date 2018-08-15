package com.ywl01.jlinfo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ywl01.jlinfo.BaseApplication;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ywl01 on 2018/3/8.
 */

public class AppUtils {

    ///////////////////////////////////////////////////////////////////////////
    // 返回context
    ///////////////////////////////////////////////////////////////////////////
    public static Context getContext() {
        return BaseApplication.getAppContext();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 检测网络连接
    ///////////////////////////////////////////////////////////////////////////
    public static boolean isNetConnect(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.v("error", e.toString());
        }
        return false;
    }

    public static boolean isWifiConnect(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            // 获取网络连接管理的对象
            NetworkInfo networkInfo = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
            return false;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 单位转换dp px
    ///////////////////////////////////////////////////////////////////////////
    public static int dip2px(int dip) {
        // 公式： dp = px / (dpi / 160) px = dp * (dpi / 160)
        // dp = px / denisity
        // px = dp * denisity;
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        return (int) (dip * density + 0.5f);
    }

    public static int px2dip(int px) {
        // dp = px / denisity
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        return (int) (px / density + 0.5f);
    }

    //获取版本名称
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取版本号
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //获取应用名称
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Toast toast;

    public static void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(BaseActivity.currentActivity, message, Toast.LENGTH_LONG);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    public static void startActivity(Class<?> cls) {
        Intent intent = new Intent(BaseActivity.currentActivity, cls);
        BaseActivity.currentActivity.startActivity(intent);
        BaseActivity.currentActivity.overridePendingTransition(R.anim.acticity_in,R.anim.activity_out);
    }

    public static void startActivity(Class<?> cls, Bundle args) {
        Intent intent = new Intent(BaseActivity.currentActivity, cls);
        intent.putExtras(args);
        BaseActivity.currentActivity.startActivity(intent);
    }

    public static void moveActivityToFront(Class<?> cls) {
        Intent intent = new Intent(BaseActivity.currentActivity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        BaseActivity.currentActivity.startActivity(intent);
    }

    public static void playSound(int resID) {
        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), resID);
        mediaPlayer.setVolume(0.50f, 0.50f);
        mediaPlayer.start();
    }

    public static String[] getResArray(int resArrayID) {
        Resources res = getContext().getResources();
        return res.getStringArray(resArrayID);
    }

    public static String getResString(int resStringID) {
        Resources res = getContext().getResources();
        return res.getString(resStringID);
    }

    public static int getResColor(int resID) {
        return ContextCompat.getColor(getContext(), resID);
    }

    public static Drawable getResDrawable(int resDrawableID) {
        return ContextCompat.getDrawable(getContext(), resDrawableID);
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getPathByUri(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        cursor.moveToNext();
        return cursor.getString(0);
    }

    //字符是否都是数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    //字符是否是汉子
    public static boolean isChinese(String str) {
        String regEx = "[\\u4e00-\\u9fa5]+";
        return str.matches(regEx);
    }

    //是否是手机号
    public static boolean isMobile(String cellphone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,1,2,5-9])|(177))\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cellphone);
        return matcher.matches();
    }

    //是否是电话号码
    public static boolean isPhone(String str) {
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        boolean b = false;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }

    public static boolean isDate(String str) {
        boolean convertSuccess=true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
        // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (Exception e) {
            // e.printStackTrace();
           // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess=false;
        }
        return convertSuccess;
    }

    public static boolean isEmptyString(String str) {
        if (TextUtils.isEmpty(str))
            return true;
        else if ("null".equals(str.toLowerCase()))
            return true;
        else
            return false;
    }

    public static String checkString(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        } else if (str.toLowerCase().equals("null")) {
            return "";
        } else
            return str;
    }

    public static void mapToBean(Map<String, Object> map, Object bean) {
        Class cls = bean.getClass();
        Field[] fields = cls.getFields();

        for (int i = 0; i < fields.length; i++) {
            try {
                Field f = fields[i];
                String key = f.getName();
                //System.out.println("markInfoBean key:" + key);
                if(map.containsKey(key)){
                    f.set(bean,map.get(key));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Object> beanToMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        Class cls = bean.getClass();
        Field[] fields = cls.getFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                Field f = fields[i];
                int modifer = f.getModifiers();
                if (modifer == Modifier.PUBLIC) {
                    String key = f.getName();
                    Object value = f.get(bean);
                    map.put(key, value);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
