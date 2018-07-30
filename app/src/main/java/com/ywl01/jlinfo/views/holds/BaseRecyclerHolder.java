package com.ywl01.jlinfo.views.holds;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ywl01 on 2017/2/3.
 */

public abstract class BaseRecyclerHolder<T> extends RecyclerView.ViewHolder {
    protected View rootView;
    protected T data;

    public int position;

    public BaseRecyclerHolder(View itemView) {
        super(itemView);
        rootView = itemView;
    }

    public View getRootView(){
        return rootView;
    }

    public void setData(T data) {
        this.data = data;
        refreshUI(data);
    }

    protected abstract void refreshUI(T data);
}
