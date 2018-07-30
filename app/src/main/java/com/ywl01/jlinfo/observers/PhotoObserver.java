package com.ywl01.jlinfo.observers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl01.jlinfo.beans.PeoplePhotoBean;

import java.util.List;

/**
 * Created by ywl01 on 2017/1/24.
 */

public class PhotoObserver extends BaseObserver {

    @Override
    protected List<PeoplePhotoBean> convert(String data) {
        List<PeoplePhotoBean> photos = new Gson().fromJson(data, new TypeToken<List<PeoplePhotoBean>>() {}.getType());
        return photos;
    }
}
