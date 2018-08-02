package com.ywl01.jlinfo.observers;

import android.graphics.Color;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.ywl01.jlinfo.consts.CommVar;


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
public class PositionObserver extends BaseObserver {
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
                    String tableName = jsonObject.getString("tableName");
                    String roomNumber = jsonObject.getString("roomNumber");
                    String community = jsonObject.getString("community");
                    String name = jsonObject.getString("name");
                    if ("house".equals(tableName)) {
                        displayText = community + roomNumber + name;
                    } else if ("building".equals(tableName)) {
                        displayText = name + roomNumber;
                    }else {
                        displayText = name;
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("mapScale", mapScale);
                    map.put("displayText", displayText);

                    TextSymbol ts = new TextSymbol(16,displayText, Color.BLUE, TextSymbol.HorizontalAlignment.CENTER, TextSymbol.VerticalAlignment.MIDDLE);

                    Graphic g = new Graphic(new Point(x, y,CommVar.mapSpatialReference),map,ts);
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
