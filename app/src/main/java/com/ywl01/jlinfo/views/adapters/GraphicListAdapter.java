package com.ywl01.jlinfo.views.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.events.ShowPositionEvent;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.holds.GraphicItemHolder;
import com.ywl01.jlinfo.views.holds.PeopleItemHolder;

import java.util.List;

public class GraphicListAdapter extends BaseAdapter {

    public GraphicListAdapter(List datas) {
        super(datas);
    }

    @Override
    protected RecyclerView.ViewHolder getHolder() {
        View view = View.inflate(AppUtils.getContext(), R.layout.item_graphic_list, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new GraphicItemHolder(view);
    }

    @Override
    protected int getClickBackground() {
        return AppUtils.getResColor(R.color.light_blue);
    }

}
