package com.ywl01.jlinfo.events;

import com.esri.arcgisruntime.geometry.Point;

public class ShowLocationEvent extends Event{
   public double x;
   public double y;

    public ShowLocationEvent(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
