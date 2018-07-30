package com.ywl01.jlinfo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by ywl01 on 2016/12/31.
 */

public class BaseApplication extends Application {

    private static Context appContext;
    private static int mainThreadID;
    private static Handler mainThreadHandler;
    private static Looper mainThreadLooper;
    private static Thread mainThread;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        mainThreadHandler = new Handler();
        mainThreadID = Process.myTid();
        mainThread = Thread.currentThread();
        mainThreadLooper = getMainLooper();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder() //
                .showImageForEmptyUri(R.drawable.ic_launcher_foreground) //
                .showImageOnFail(R.drawable.ic_launcher_foreground) //
                .cacheInMemory(true) //
                .cacheOnDisk(true) //
                .build();//
        ImageLoaderConfiguration config = new ImageLoaderConfiguration//
                .Builder(getApplicationContext())//
                .defaultDisplayImageOptions(defaultOptions)//
                .discCacheSize(50 * 1024 * 1024)//
                .discCacheFileCount(100)// 缓存一百张图片
                .writeDebugLogs()//
                .build();//
        ImageLoader.getInstance().init(config);
    }

    public static int getMainThreadID() {
        return mainThreadID;
    }

    public static Handler getMainHandler() {
        return mainThreadHandler;
    }

    public static Looper getMainThreadLooper() {
        return mainThreadLooper;
    }

    public static Thread getMainThread() {
        return mainThread;
    }

    public static Context getAppContext() {
        return appContext;
    }


}
