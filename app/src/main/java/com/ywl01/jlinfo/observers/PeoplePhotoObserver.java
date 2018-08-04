package com.ywl01.jlinfo.observers;

import com.ywl01.jlinfo.beans.ImageBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ywl01 on 2017/1/24.
 */

public class PeoplePhotoObserver extends BaseObserver<String,List<ImageBean>> {
    @Override
    protected List<ImageBean> convert(String data) {
        List<ImageBean> images = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ImageBean imageBean = new ImageBean();
                if (jsonObject != null) {
                    imageBean.id = jsonObject.optInt("id");
                    imageBean.hostID = jsonObject.optInt("peopleID");
                    imageBean.imageUrl = jsonObject.optString("photoUrl");
                    imageBean.thumbUrl = jsonObject.optString("thumbUrl");
                    imageBean.insertTime = jsonObject.optString("insertTime");
                    imageBean.insertUser = jsonObject.optInt("insertUser");
                }
                images.add(imageBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return images;
    }
}
