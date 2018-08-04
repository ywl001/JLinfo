package com.ywl01.jlinfo.observers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl01.jlinfo.beans.PeopleHomeBean;

import java.util.List;

/**
 * Created by ywl01 on 2017/1/22.
 */

public class PeopleHomeObserver extends BaseObserver<String,List<PeopleHomeBean>>  {

    @Override
    protected List<PeopleHomeBean> convert(String data) {
        List<PeopleHomeBean> homes = new Gson().fromJson(data, new TypeToken<List<PeopleHomeBean>>() {}.getType());
        return homes;
    }
}
