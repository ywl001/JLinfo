package com.ywl01.jlinfo.activities;

import android.view.MenuItem;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.beans.PeopleHomeBean;
import com.ywl01.jlinfo.beans.SetHomeBean;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.PeopleHomeObserver;
import com.ywl01.jlinfo.views.SetHomeFragment1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;


/**
 * Created by ywl01 on 2017/2/23.
 */

public class SetHomeActivity extends BaseActivity {

    public PeopleBean people;
    public SetHomeBean setHomeBean;

    @Override
    protected void initView() {
        people = (PeopleBean) CommVar.getInstance().get("people");
        setHomeBean = new SetHomeBean();

        if (people.phmID > 0) {
            setContentView(R.layout.fragment_set_home);
            SetHomeFragment1 fragment = new SetHomeFragment1();
            addFragment(fragment, R.id.root_view);
        } else {
            getPeopleHomeInfo();
        }
    }

    private void getPeopleHomeInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", people.id);
        PeopleHomeObserver getHomeInfoObserver = new PeopleHomeObserver();
        HttpMethods.getInstance().getSqlResult(getHomeInfoObserver, PhpFunction.SELECT_PEOPLE_HOME_INFO, data);
        getHomeInfoObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                List<PeopleHomeBean> temp = (List<PeopleHomeBean>) data;
                if (temp != null && temp.size() > 0) {
                    PeopleHomeBean h = temp.get(0);
                    people.phmID = h.id;
                    people.homeNumber = h.homeNumber;
                    people.relation = h.relation;
                } else {
                    people.homeExists = 0;
                }
                System.out.println("重新设置户号");
                setContentView(R.layout.fragment_set_home);
                SetHomeFragment1 fragment = new SetHomeFragment1();
                addFragment(fragment, R.id.root_view);
            }
        });
    }
}
