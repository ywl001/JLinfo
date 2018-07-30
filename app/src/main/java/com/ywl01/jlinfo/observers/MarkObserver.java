package com.ywl01.jlinfo.observers;

import android.graphics.Color;

import com.esri.arcgisruntime.symbology.CompositeSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ywl01.jlinfo.beans.GraphicBean;
import com.ywl01.jlinfo.beans.MarkBean;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.map.SymbolManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywl01 on 2017/1/21.
 */

public class MarkObserver extends GraphicObserver {
    @Override
    protected int getFlag() {
        return GraphicFlag.MARK;
    }

    @Override
    protected List<GraphicBean> getBeanList(String json) {
        return new Gson().fromJson(json, new TypeToken<List<MarkBean>>(){}.getType());
    }

    @Override
    protected Symbol getSymbol(GraphicBean bean) {
        MarkBean mark = (MarkBean) bean;
        PictureMarkerSymbol pms = SymbolManager.getPmsByName(mark.symbol);
        TextSymbol ts = new TextSymbol(12, mark.name, Color.BLACK, TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.MIDDLE);
        ts.setOffsetX(7);
        List<Symbol> symbols = new ArrayList<>();
        symbols.add(pms);
        symbols.add(ts);
        CompositeSymbol cs = new CompositeSymbol(symbols);
        return cs;
    }
}
