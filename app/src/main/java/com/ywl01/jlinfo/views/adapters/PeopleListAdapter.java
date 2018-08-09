package com.ywl01.jlinfo.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.holds.PeopleItemHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywl01 on 2017/1/24.
 */

public class PeopleListAdapter extends BaseAdapter implements Filterable{

    private PeopleFilter peopleFilter;
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

    @Override
    public Filter getFilter() {
        if (peopleFilter == null) {
            peopleFilter = new PeopleFilter(datas);
        }
        return peopleFilter;
    }

    private class PeopleFilter extends Filter {

        private List<PeopleBean> filterSouce;

        public PeopleFilter(List fitersouce) {
            this.filterSouce = fitersouce;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            FilterResults filterResults = new FilterResults();
            List<PeopleBean> temp = new ArrayList<>();

            //关键字为空的时候，搜索结果为复制的结果
            if (charSequence == null || charSequence.length() == 0) {
                filterResults.values = filterSouce;
                filterResults.count = filterSouce.size();
            } else {
                PeopleBean people = filterSouce.get(0);
                if (people.peopleFlag == PeopleFlag.FROM_BUILDING || people.peopleFlag == PeopleFlag.FROM_MARK || people.peopleFlag == PeopleFlag.FROM_HOUSE) {
                    for (int i = 0; i < filterSouce.size(); i++) {
                        PeopleBean p = filterSouce.get(i);
                        String isLeave = p.isLeave + "";
                        if (isLeave.equals(charSequence)) {
                            temp.add(p);
                        }
                    }
                } else {
                    for (int i = 0; i < filterSouce.size(); i++) {
                        PeopleBean p = filterSouce.get(i);
                        String isDead = p.isDead + "";
                        if (isDead.equals(charSequence)) {
                            temp.add(p);
                        }
                    }
                }
                filterResults.values = temp;
                filterResults.count = temp.size();
            }
            System.out.println("--------" + filterResults.count);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            datas = (List<PeopleBean>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            }
        }
    }
}