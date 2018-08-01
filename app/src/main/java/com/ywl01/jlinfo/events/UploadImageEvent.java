package com.ywl01.jlinfo.events;

/**
 * Created by ywl01 on 2017/2/1.
 */

public class UploadImageEvent extends Event {
    public static final int SELECT_IMAGE_FOR_MARK = 1;
    public static final int SELECT_IMAGE_FOR_PEOPLE = 2;
    public static final int TAKE_IMAGE_FOR_MARK = 3;
    public static final int TAKE_IMAGE_FOR_PEOPLE = 4;

    public int type;

    public String IMAGE_DIR;//服务器图片文件夹相对目录
    public int id;//操作对象的id

    public UploadImageEvent(int type) {
        this.type = type;
    }

}
