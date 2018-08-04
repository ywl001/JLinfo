package com.ywl01.jlinfo.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.holds.QueryPeopleHolder;

import java.util.List;

/**
 * Created by ywl01 on 2017/1/26.
 */

public class QueryPeopleListAdapter extends BaseAdapter{

    public QueryPeopleListAdapter(List datas) {
        super(datas);
    }

    @Override
    protected RecyclerView.ViewHolder getHolder() {
        View view = View.inflate(AppUtils.getContext(), R.layout.item_query_people, null);
        return new QueryPeopleHolder(view);
    }

    @Override
    protected int getClickBackground() {
        return AppUtils.getResColor(R.color.light_blue);
    }

}
