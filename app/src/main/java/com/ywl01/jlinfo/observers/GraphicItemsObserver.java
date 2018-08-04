package com.ywl01.jlinfo.observers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl01.jlinfo.beans.GraphicItemBean;
import com.ywl01.jlinfo.beans.PeopleBean;

import java.util.List;

/**
 * Created by ywl01 on 2017/1/22.
 */

public class GraphicItemsObserver extends BaseObserver<String,List<GraphicItemBean>> {

    @Override
    protected List<GraphicItemBean> convert(String data) {

        List<GraphicItemBean> graphicItemBeans = new Gson().fromJson(data, new TypeToken<List<GraphicItemBean>>() {}.getType());
        return graphicItemBeans;
    }
}
