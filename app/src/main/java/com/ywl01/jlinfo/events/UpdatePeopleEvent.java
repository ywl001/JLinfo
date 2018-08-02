package com.ywl01.jlinfo.events;

import com.ywl01.jlinfo.beans.PeopleBean;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ywl01 on 2017/2/12.
 */

public class UpdatePeopleEvent extends Event{
    public PeopleBean people;

}
