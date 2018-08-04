package com.ywl01.jlinfo.observers;

public class IntObserver extends BaseObserver<String,Integer> {
    @Override
    protected Integer convert(String data) {
        return Integer.parseInt(data);
    }
}
