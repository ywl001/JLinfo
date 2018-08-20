package com.ywl01.jlinfo.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;


import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.BaseActivity;
import com.ywl01.jlinfo.activities.SetHomeActivity;
import com.ywl01.jlinfo.beans.SetHomeBean;
import com.ywl01.jlinfo.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ywl01 on 2017/2/15.
 */

public class SetHomeFragment1 extends Fragment implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.rg_set_home)
    RadioGroup rgSetHome;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_home1,null);
        ButterKnife.bind(this,view);
        getActivity().setTitle("设置户关系：");
        rgSetHome.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SetHomeActivity activity = (SetHomeActivity) getActivity();
        if (checkedId == R.id.rb_self) {
            //跳转到第三页
            activity.setHomeBean.oprationType = SetHomeBean.OWN_CREATE;

            activity.setHomeBean.peopleID = activity.people.id;
            activity.setHomeBean.newHomeNumber = System.currentTimeMillis() + "";
            activity.setHomeBean.homeIsExists = (activity.people.phmID > 0);
            activity.setHomeBean.relation = "户主";
            activity.setHomeBean.oldHomeID = activity.people.phmID;

            SetHomeFragment3 fragment3 = new SetHomeFragment3();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_view,fragment3)
                    .addToBackStack("")
                    .commit();

            //activity.addFragment(this,fragment3,R.id.root_view);

        }else if(checkedId == R.id.rb_join_other){
            activity.setHomeBean.oprationType = SetHomeBean.OWN_JOIN_OTHER;

            activity.setHomeBean.peopleID = activity.people.id;
            activity.setHomeBean.homeIsExists = (activity.people.phmID > 0);
            activity.setHomeBean.oldHomeID = activity.people.phmID;

            SetHomeFragment2 fragment2 = new SetHomeFragment2();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_view,fragment2)
                    .addToBackStack("")
                    .commit();
//            activity.addFragment(this,fragment2,R.id.root_view);
        } else if (checkedId == R.id.rb_other_join) {
            activity.setHomeBean.oprationType = SetHomeBean.OTHER_JOIN_OWN;
            activity.setHomeBean.newHomeNumber = activity.people.homeNumber;

            SetHomeFragment2 fragment2 = new SetHomeFragment2();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_view,fragment2)
                    .addToBackStack("")
                    .commit();
            //activity.addFragment(this,fragment2,R.id.root_view);
        }
    }
}
