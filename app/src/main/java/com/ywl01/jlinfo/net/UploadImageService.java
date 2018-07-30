package com.ywl01.jlinfo.net;


import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by ywl01 on 2017/1/30.
 */

public interface UploadImageService {
    @Multipart
    @POST()
    Observable<String> upload(@Url String url,@Part("fileDir") RequestBody fileDir, @Part MultipartBody.Part file);
}
