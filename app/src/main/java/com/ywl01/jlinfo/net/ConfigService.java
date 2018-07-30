package com.ywl01.jlinfo.net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by ywl01 on 2017/12/10.
 */

public interface ConfigService {
    @GET
    Observable<String> getConfig(@Url String url);
}
