package com.ywl01.jlinfo.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.SetHomeActivity;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.beans.SetHomeBean;
import com.ywl01.jlinfo.utils.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ywl01 on 2017/2/15.
 */

public class SetHomeFragment2 extends Fragment implements QueryPeopleView.OnItemSelectListener{
    @BindView(R.id.query_people)
    QueryPeopleView queryPeopleView;

    @BindView(R.id.result_group)
    LinearLayout resultGroup;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.et_homePeople)
    EditText etHomePeople;

    @BindView(R.id.et_relation)
    EditText etRelation;

    @BindView(R.id.root_view)
    LinearLayout rootView;

    @BindView(R.id.tv_desc)
    TextView tvDesc;

    private PeopleBean people;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("page2 on create");
        rootView = (LinearLayout) inflater.inflate(R.layout.set_home2, null);
        ButterKnife.bind(this, rootView);
        queryPeopleView.setOnItemSelectListener(this);
        refresh();
        return rootView;
    }

    public void refresh() {
        SetHomeActivity activity = (SetHomeActivity) getActivity();
        String info1 = "请查询出想要加入的户中的任意一名人员，然后点击选择该成员。例如要加入到张三的户中，将张三或张三户中的任意任意一人查询出来，然后点击选择。";
        String info2 = "请查询出要加入本户的人员，然后点击选择该人员。例如张三要加入到本户中，将张三查询出来，并点击选择";
        if (activity.setHomeBean.oprationType == SetHomeBean.OWN_JOIN_OTHER) {
            tvTitle.setText(info1);
        } else if (activity.setHomeBean.oprationType == SetHomeBean.OTHER_JOIN_OWN) {
            tvTitle.setText(info2);
        }
    }

    @Override
    public void onItemSelect(PeopleBean peopleBean) {
        queryPeopleView.setVisibility(View.GONE);
        resultGroup.setVisibility(View.VISIBLE);
        people = peopleBean;

        SetHomeActivity activity = (SetHomeActivity) getActivity();
        String info1 = "你已经选择了要添加的户，请填写在该户中和户主的关系";
        String info2 = "你已经选择类要加入该户主的人员，请填写人员和户主的关系";
        if (activity.setHomeBean.oprationType == SetHomeBean.OWN_JOIN_OTHER) {
            tvTitle.setText(info1);
            etHomePeople.setText(peopleBean.name + "的户中");
            tvDesc.setText("待加入的户：");
        } else if (activity.setHomeBean.oprationType == SetHomeBean.OTHER_JOIN_OWN) {
            etHomePeople.setText(peopleBean.name);
            tvTitle.setText(info2);
            tvDesc.setText("待加入的人：");
        }
    }

    @OnClick(R.id.btn_next)
    public void onBtnNext() {
        if(!validate())
            return;

        SetHomeActivity activity = (SetHomeActivity) getActivity();
        if (activity.setHomeBean.oprationType == SetHomeBean.OWN_JOIN_OTHER) {
            activity.setHomeBean.newHomeNumber = people.homeNumber;
            activity.setHomeBean.relation = etRelation.getText().toString().trim();
        } else if (activity.setHomeBean.oprationType == SetHomeBean.OTHER_JOIN_OWN) {
            activity.setHomeBean.peopleID = people.id;
            activity.setHomeBean.homeIsExists = !TextUtils.isEmpty(people.homeNumber);
            activity.setHomeBean.relation = etRelation.getText().toString().trim();
            activity.setHomeBean.oldHomeID = people.phmID;
        }

        SetHomeFragment3 fragment3 = new SetHomeFragment3();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.root_view,fragment3)
                .addToBackStack("")
                .commit();
        //activity.addFragment(this,fragment3,R.id.root_view);
    }

    private boolean validate(){
        if("".equals(etRelation.getText())){
            AppUtils.showToast("与户主关系必须填写");
            return false;
        }
        return true;
    }
}
