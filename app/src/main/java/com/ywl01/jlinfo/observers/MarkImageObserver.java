package com.ywl01.jlinfo.observers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl01.jlinfo.beans.MarkImageBean;

import java.util.List;

/**
 * Created by ywl01 on 2017/1/24.
 */

public class MarkImageObserver extends BaseObserver {

    @Override
    protected List<MarkImageBean> convert(String data) {
        List<MarkImageBean> photos = new Gson().fromJson(data, new TypeToken<List<MarkImageBean>>() {}.getType());
        return photos;
    }
}
