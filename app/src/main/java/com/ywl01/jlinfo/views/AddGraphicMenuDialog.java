package com.ywl01.jlinfo.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.esri.arcgisruntime.geometry.Point;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.AddGraphicActivity;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ywl01 on 2017/2/14.
 */

public class AddGraphicMenuDialog extends Dialog {
    private Context context;
    public Map<String,Object> data;

    @BindView(R.id.btn_add_building)
    Button btnAddBuilding;

    @BindView(R.id.btn_add_house)
    Button btnAddHouse;

    private Point mapPoint;
    public AddGraphicMenuDialog(Context context) {
        super(context);
        this.context = context;
    }

    public AddGraphicMenuDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_add_graphic, null);
        ButterKnife.bind(this,view);
        setContentView(view);
        double currentMapScale = (double) data.get("mapScale");
        mapPoint = (Point) data.get("mapPoint");
        double buildingMapScale = CommVar.getInstance().getScaleBylevel(CommVar.buildingDisplayLevel);
        double houseMapScale = CommVar.getInstance().getScaleBylevel(CommVar.houseDisplayLevel);

        System.out.println(currentMapScale + "----" + buildingMapScale);
        if (currentMapScale >= buildingMapScale) {
            btnAddBuilding.setVisibility(View.GONE);
        }else{
            btnAddBuilding.setVisibility(View.VISIBLE);
        }
        if (currentMapScale >= houseMapScale) {
            btnAddHouse.setVisibility(View.GONE);
        }else{
            btnAddHouse.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_add_mark)
    public void onAddMark() {
        CommVar.getInstance().put("graphicFlag", GraphicFlag.MARK);
        CommVar.getInstance().put("mapPoint",mapPoint);
        AppUtils.startActivity(AddGraphicActivity.class);
        dismiss();
    }

    @OnClick(R.id.btn_add_building)
    public void onAddBuilding() {
        CommVar.getInstance().put("graphicFlag", GraphicFlag.BUILDING);
        CommVar.getInstance().put("mapPoint",mapPoint);
        AppUtils.startActivity(AddGraphicActivity.class);
        dismiss();
    }

    @OnClick(R.id.btn_add_house)
    public void onAddHouse() {
        CommVar.getInstance().put("graphicFlag", GraphicFlag.HOUSE);
        CommVar.getInstance().put("mapPoint",mapPoint);
        AppUtils.startActivity(AddGraphicActivity.class);
        dismiss();
    }

}
