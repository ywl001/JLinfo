package com.ywl01.jlinfo.activities;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.beans.PeopleHomeBean;
import com.ywl01.jlinfo.beans.SetHomeBean;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.PeopleHomeObserver;
import com.ywl01.jlinfo.views.SetHomeFragment1;

import java.util.List;

import io.reactivex.Observer;


/**
 * Created by ywl01 on 2017/2/23.
 */

public class SetHomeActivity extends BaseActivity {

    public PeopleBean people;
    public SetHomeBean setHomeBean;
    private PeopleHomeObserver getHomeInfoObserver;

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
        String sql = "select id,homeNumber,relation from people_home where peopleID = " + people.id + " and isDelete = 0";
        System.out.println(sql);
        getHomeInfoObserver = new PeopleHomeObserver();
        HttpMethods.getInstance().getSqlResult(getHomeInfoObserver, SqlAction.SELECT, sql);
        getHomeInfoObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                List<PeopleHomeBean> temp = (List<PeopleHomeBean>) data;
                if (temp.size() > 0) {
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
