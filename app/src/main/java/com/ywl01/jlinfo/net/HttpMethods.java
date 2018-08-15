package com.ywl01.jlinfo.net;


import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.observers.UploadObserver;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 单例模式
 * Created by ywl01 on 2017/12/10.
 */

public class HttpMethods {
    private static final int DEFAULT_TIMEOUT = 10;
    private Retrofit retrofit;

    private ConfigService configService;
    private SqlService sqlService;
    private DelFileService delFileService;
    private UploadImageService uploadImageService;

    private HttpMethods() {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
        okBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);

        OkHttpClient client = okBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(CommVar.baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        configService = retrofit.create(ConfigService.class);
        sqlService = retrofit.create(SqlService.class);
        delFileService = retrofit.create(DelFileService.class);
        uploadImageService = retrofit.create(UploadImageService.class);
    }

//    public void getAppConfig(Observer observer) {
//        Observable<String> observable = configService.getConfig(CommVar.configUrl);
//        execute(observable,observer);
//    }

    public void getSqlResult(Observer observer, String sqlAction, String sql) {
        Observable<String> observable = sqlService.getResult(CommVar.sqlUrl,sqlAction,sql);
        execute(observable,observer);
    }

    public void delFile(Observer<String> observer, String filePath) {
        Observable<String> observable = delFileService.delFile(CommVar.delFileUrl,filePath);
        execute(observable,observer);
    }

    public void uploadImage(UploadObserver observer, RequestBody fileDir, MultipartBody.Part file){
        Observable<String> observable = uploadImageService.upload(CommVar.uploadUrl,fileDir, file);
        execute(observable,observer);
    }

    private void execute(Observable observable, Observer observer){
        //网络请求的入口处显示忙碌图标
        TypeEvent.dispatch(TypeEvent.SHOW_PROGRESS_BAR);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }
}
