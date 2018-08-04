package com.ywl01.jlinfo.views;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.views.adapters.BaseAdapter;
import com.ywl01.jlinfo.views.adapters.DividerGridItemDecoration;
import com.ywl01.jlinfo.events.SelectValueEvent;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.holds.LevelHolder;

import java.util.ArrayList;
import java.util.List;

public class SelectLevelDialog extends Dialog implements BaseAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<Integer> levels;

    public SelectLevelDialog(@NonNull Context context) {
        super(context, R.style.dialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        levels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            levels.add(i);
        }

        recyclerView = new RecyclerView(context);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(-1, -1);
        recyclerView.setLayoutParams(params);
        GridLayoutManager manager = new GridLayoutManager(context, 2);

        recyclerView.addItemDecoration(new DividerGridItemDecoration(context));
        LevelListAdapter adapter = new LevelListAdapter(levels);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        setContentView(recyclerView);
    }

    @Override
    public void onItemClick(RecyclerView parent, View itemView, int position) {
        Integer level = levels.get(position);
        dismiss();
        SelectValueEvent event = new SelectValueEvent(SelectValueEvent.SELECT_MAP_LEVEL);
        event.selectValue = level;
        event.dispatch();
    }


    class LevelListAdapter extends BaseAdapter<Integer> {

        public LevelListAdapter(List<Integer> datas) {
            super(datas);
        }

        @Override
        protected RecyclerView.ViewHolder getHolder() {
            Button button = new Button(AppUtils.getContext());

            LevelHolder holder = new LevelHolder(button);
            return holder;
        }

        @Override
        protected int getClickBackground() {
            return AppUtils.getResColor(R.color.light_blue);
        }
    }

}
