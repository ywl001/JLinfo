package com.ywl01.jlinfo.events;

/**
 * Created by ywl01 on 2018/3/7.
 * 对于不需要携带参数的event的合集
 */

public class TypeEvent extends Event {

    public static final int CHANGE_PANORAMA_ICON   = 1;
    public static final int DEL_MONITOR            = 2;
    public static final int LOGIN                  = 3;
    public static final int MOVE_GRAPHIC           = 4;
    public static final int SHOW_PANORRMA          = 6;
    public static final int REFRESH_IMAGE          = 7;
    public static final int SHOW_BTN_CONTAINER     = 8;
    public static final int REFRESH_MARKERS        = 5;
    public static final int REFRESH_BUILDINGS      = 9;
    public static final int REFRESH_HOUSE          = 10;
    public static final int CLEAR_BOTTOM_CONTAINER = 11;
    public static final int RESET_SWIPEITEM_STATE  = 12;
    public static final int CLEAR_LOCATION         = 13;
    public static final int SHOW_PROGRESS_BAR = 14;
    public static final int HIDE_PROGRESS_BAR = 15;


    public int type;

    public TypeEvent(int type) {
        this.type = type;
    }

    public static void dispatch(int type) {
        TypeEvent event = new TypeEvent(type);
        event.dispatch();
    }

}
