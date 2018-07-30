package com.ywl01.jlinfo.observers;

/**
 * Created by ywl01 on 2017/2/1.
 * 上传成功后返回图片在服务器端的相对路径
 */

public class UploadObserver extends BaseObserver<String> {
    @Override
    protected String convert(String data) {
//        String imgUrl = objectMap.substring(12);
//        String[] temp = imgUrl.split(".");
//        String thumbUrl = temp[0] + "_thumb.jpg";
//        UploadPathBean pathBean = new UploadPathBean();
//        pathBean.imageUrl = imgUrl;
//        pathBean.thumbUrl
//        UploadPathBean path = new Gson().fromJson(objectMap, UploadPathBean.class);
//        return path;
        return data;
    }
}
