package com.ywl01.jlinfo.observers;

import android.graphics.Color;

import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl01.jlinfo.beans.BuildingBean;
import com.ywl01.jlinfo.beans.GraphicBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;

import java.util.List;

/**
 * Created by ywl01 on 2017/1/21.
 */

public class BuildingObserver extends GraphicObserver {
    private double mapScale;

    public BuildingObserver(double mapScale) {
        this.mapScale = mapScale;
    }

    @Override
    protected int getFlag() {
        return GraphicFlag.BUILDING;
    }

    @Override
    protected List<GraphicBean> getBeanList(String json) {
        return new Gson().fromJson(json, new TypeToken<List<BuildingBean>>() {
        }.getType());
    }

    @Override
    protected Symbol getSymbol(GraphicBean bean) {
        TextSymbol ts = new TextSymbol(getSize(), bean.name, Color.BLUE, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);
        ts.setAngle(360 -bean.angle);
        return ts;
    }

    public float getSize() {
        return(float) (4 * CommVar.getInstance().getScaleBylevel(CommVar.buildingDisplayLevel) / mapScale);
    }
}
