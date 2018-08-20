package com.ywl01.jlinfo.events;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ywl01 on 2017/2/14.
 */

public class ListEvent extends Event{

    public static final int add = 1;
    public static final int remove = 2;
    public static final int update = 3;

    public int action;
    public int position;

    public ListEvent(int action,int position) {;
        this.action = action;
        this.position = position;
    }

}
