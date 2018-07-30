package com.ywl01.jlinfo.events;

/**
 * Created by ywl01 on 2017/2/3.
 */

public class SelectValueEvent extends Event{

    public static final int SELECT_SYMBOL = 0;
    public static final int SELECT_MAP_LEVEL = 1;
    public static final int SELECT_ANGLE = 2;
    public int type;
    public Object selectValue;

    public SelectValueEvent(int type) {
        this.type = type;
    }
}
