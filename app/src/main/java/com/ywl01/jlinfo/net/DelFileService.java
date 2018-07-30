package com.ywl01.jlinfo.net;


import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;


/**
 * Created by ywl01 on 2017/1/30.
 */

public interface DelFileService {
    @FormUrlEncoded
    @POST()
    Observable<String> delFile(@Url String url,@Field("files") String filePaths);
}
