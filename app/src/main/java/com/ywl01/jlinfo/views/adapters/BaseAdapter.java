package com.ywl01.jlinfo.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ywl01.jlinfo.events.ListEvent;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.views.holds.BaseRecyclerHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by ywl01 on 2017/2/3.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter implements View.OnClickListener {

    public static final int add = 1;
    public static final int remove = 2;
    public static final int update = 3;

    protected List<T> datas;

    private OnItemClickListener onItemClickListener;
    private RecyclerView recyclerView;
    private View prevClickView;

    private int backgroundColor = -1;
    private int clickPosition = -1;

    public BaseAdapter(List<T> datas) {
        this.datas = datas;
        EventBus.getDefault().register(this);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerHolder holder = (BaseRecyclerHolder) getHolder();
        View view = holder.getRootView();
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        T data = datas.get(position);
        BaseRecyclerHolder holder2 = (BaseRecyclerHolder) holder;
        holder2.position = position;
        holder2.setData(data);

        if (position != clickPosition) {
            holder2.getRootView().setBackgroundColor(backgroundColor);
        } else {
            holder2.getRootView().setBackgroundColor(getClickBackground());
        }
    }

    @Override
    public int getItemCount() {
        if (datas != null)
            return datas.size();
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    @Override
    public void onClick(View v) {
        //设置点击后焦点背景
        if (prevClickView != null) {
            prevClickView.setBackgroundColor(backgroundColor);
        }
        v.setBackgroundColor(getClickBackground());
        prevClickView = v;

        int position = recyclerView.getChildAdapterPosition(v);
        clickPosition = position;
        //设置监听回调
        if (recyclerView != null && onItemClickListener != null) {
            onItemClickListener.onItemClick(recyclerView, v, position);
        }
    }

    @Subscribe
    public void refresh(ListEvent event) {
        int action = event.action;
        int position = event.position;
        switch (action) {
            case add:
                notifyItemInserted(position);
                break;
            case remove:
                datas.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,datas.size());
                TypeEvent.dispatch(TypeEvent.RESET_SWIPEITEM_STATE);
                break;
            case update:
                notifyItemChanged(position);
                //清除状态
                TypeEvent.dispatch(TypeEvent.RESET_SWIPEITEM_STATE);
                break;
        }
    }

    protected abstract RecyclerView.ViewHolder getHolder();

    protected abstract int getClickBackground();

    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View itemView, int position);
    }
}
