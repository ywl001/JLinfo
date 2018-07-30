package com.ywl01.jlinfo.observers;

/**
 * Created by ywl01 on 2017/1/29.
 * 插入监控返回id
 */

public class InsertObserver extends BaseObserver{
    @Override
    protected Long convert(String data) {
        return Long.parseLong(data);
    }
}
