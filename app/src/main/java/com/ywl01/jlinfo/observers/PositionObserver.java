package com.ywl01.jlinfo.observers;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.StringUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ywl01 on 2017/2/5.
 */
public class PositionObserver extends BaseObserver<String,List<Graphic>> {
    @Override
    protected List<Graphic> convert(String data) {
        List<Graphic> graphics = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject != null) {
                    double x = jsonObject.optDouble("x");
                    double y = jsonObject.optDouble("y");
                    int level = jsonObject.optInt("displayLevel");
                    double mapScale = getScaleBylevel(level);
                    String displayText = "";
                    String tableName = jsonObject.optString("tableName");
                    String roomNumber = jsonObject.optString("roomNumber");
                    String community = jsonObject.optString("community");
                    String name = jsonObject.optString("name");
                    if ("house".equals(tableName)) {
                        displayText = StringUtils.checkStr(community) + StringUtils.checkStr(roomNumber) + StringUtils.checkStr(name);
                    } else if ("building".equals(tableName)) {
                        displayText = StringUtils.checkStr(name) + StringUtils.checkStr(roomNumber);
                    }else {
                        displayText = name;
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("mapScale", mapScale);
                    map.put("displayText", displayText);
                    map.put("graphicFlag", GraphicFlag.POSITION);

                    BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(AppUtils.getContext(), R.drawable.position);
                    PictureMarkerSymbol pms = new PictureMarkerSymbol(drawable);
                    pms.setHeight(40);
                    pms.setWidth(40);
                    pms.setOffsetY(20);

                    Graphic g = new Graphic(new Point(x, y,CommVar.mapSpatialReference),map,pms);
                    graphics.add(g);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return graphics;
    }

    private static double getScaleBylevel(int level) {
        Map<Integer,Double> levelScale = CommVar.getInstance().level_scale;
        for (int key : levelScale.keySet()) {
            if (key == level) {
                return levelScale.get(level);
            }
        }
        return 128000;
    }
}
