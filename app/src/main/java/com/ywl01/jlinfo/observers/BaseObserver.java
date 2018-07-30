package com.ywl01.jlinfo.observers;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Observer的基类，实现onNext接口的回调
 * 子类实现数据的转换，把从服务器端的string 转换成需要的类型
 * Created by ywl01 on 2017/12/10.
 */

public abstract class BaseObserver<T> implements Observer<String> {

    protected Disposable disposable;

    private OnNextListener onNextListener;

    public void setOnNextListener(OnNextListener onNextListener) {
        this.onNextListener = onNextListener;
    }
    @Override
    public void onSubscribe(@NonNull Disposable d) {
        this.disposable = d;
    }

    /**
     *
     * @param data 服务器端给的数据
     * 在onNext方法中将服务器端数据转换后给回调接口
     */
    @Override
    public void onNext(@NonNull String data) {
        System.out.println("on next");
        T newData = convert(data);
        if (onNextListener != null) {
            onNextListener.onNext(this,newData);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        System.out.println("observer on Error: " + e.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("on complete");
    }

    /**
     * 子类实现的方法，用于数据转换
     * @param data 服务器端数据
     * @return 新数据
     */
    protected abstract T convert(String data);

    public interface OnNextListener<T>{
       void onNext(Observer observer, T data);
    }
}
