package com.ywl01.jlinfo.events;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ywl01 on 2017/3/13.
 */

public class Event {
    public void dispatch(){
        EventBus.getDefault().post(this);
    }
}
