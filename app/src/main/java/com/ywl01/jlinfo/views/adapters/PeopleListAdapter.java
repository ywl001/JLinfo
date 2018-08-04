package com.ywl01.jlinfo.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.holds.PeopleItemHolder;

import java.util.List;

/**
 * Created by ywl01 on 2017/1/24.
 */

public class PeopleListAdapter extends BaseAdapter {

    public PeopleListAdapter(List<PeopleBean> datas) {
        super(datas);
    }

    @Override
    protected RecyclerView.ViewHolder getHolder() {
        View view = View.inflate(AppUtils.getContext(), R.layout.item_people_swip, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new PeopleItemHolder(view);
    }

    @Override
    protected int getClickBackground() {
        System.out.println("点击的背景");
        return AppUtils.getResColor(R.color.light_blue);
    }
}
