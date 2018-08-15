package com.ywl01.jlinfo.views;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.BuildingBean;
import com.ywl01.jlinfo.beans.SymbolBean;
import com.ywl01.jlinfo.events.SelectValueEvent;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.adapters.BaseAdapter;
import com.ywl01.jlinfo.views.adapters.DividerGridItemDecoration;
import com.ywl01.jlinfo.views.holds.SymbolHolder;

import java.util.ArrayList;
import java.util.List;

public class SelectBuildingRoomNumberDialog extends Dialog{

    private Context context;
    private BuildingBean building;

    public SelectBuildingRoomNumberDialog(@NonNull Context context, BuildingBean building) {
        super(context, R.style.fullScreenDialog);
        this.context = context;
        this.building = building;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BuildingRoomNumberView view = new BuildingRoomNumberView(context, building);
        view.setOnClickRoomNumberListener(new BuildingRoomNumberView.OnClickRoomNumberListener() {
            @Override
            public void getRoomNumber(String roomNumber) {
                SelectValueEvent event = new SelectValueEvent(SelectValueEvent.SELECT_ROOM_NUMBER);
                event.selectValue = roomNumber;
                event.dispatch();
                dismiss();
            }
        });
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity= Gravity.CENTER;
        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height= WindowManager.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);
    }

}
