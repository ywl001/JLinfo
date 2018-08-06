package com.ywl01.jlinfo.events;

public class LocationEvent extends Event {
    public double x;
    public double y;

    public LocationEvent(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
