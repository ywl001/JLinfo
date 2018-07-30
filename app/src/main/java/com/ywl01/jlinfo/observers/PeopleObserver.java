package com.ywl01.jlinfo.observers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl01.jlinfo.beans.PeopleBean;

import java.util.List;

/**
 * Created by ywl01 on 2017/1/22.
 */

public class PeopleObserver extends BaseObserver {

    private int peopleFlag;
    public PeopleObserver(int peopleFlag) {
        this.peopleFlag = peopleFlag;
    }

    @Override
    protected List<PeopleBean> convert(String data) {

        List<PeopleBean> peoples = new Gson().fromJson(data, new TypeToken<List<PeopleBean>>() {}.getType());

        int countPeople = peoples.size();
        for (int i = 0; i < countPeople; i++) {
            PeopleBean p = peoples.get(i);
            p.peopleFlag = peopleFlag;
        }

        return peoples;
    }
}
