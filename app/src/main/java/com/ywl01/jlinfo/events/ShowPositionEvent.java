package com.ywl01.jlinfo.events;

import com.esri.arcgisruntime.mapping.view.Graphic;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by ywl01 on 2017/2/5.
 */

public class ShowPositionEvent extends Event{
    public static final String SHOW_ADDRESS = "showAddress";
    public static final String SHOW_WORKPLACE = "showWorkplace";

    public String type;
    public List<Graphic> positions;

    public ShowPositionEvent(String type) {
        this.type = type;
    }

}
