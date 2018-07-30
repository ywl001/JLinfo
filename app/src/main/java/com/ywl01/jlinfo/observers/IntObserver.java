package com.ywl01.jlinfo.observers;

public class IntObserver extends BaseObserver {
    @Override
    protected Object convert(String data) {
        return Integer.parseInt(data);
    }
}
