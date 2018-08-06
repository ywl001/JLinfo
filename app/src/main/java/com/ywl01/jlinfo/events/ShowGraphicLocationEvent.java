package com.ywl01.jlinfo.events;

import com.esri.arcgisruntime.mapping.view.Graphic;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ywl01 on 2017/2/5.
 */

public class ShowGraphicLocationEvent extends Event{
    public List<Graphic> positions;

}
