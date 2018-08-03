package com.ywl01.jlinfo.observers;


import com.ywl01.jlinfo.activities.FamilyActivity;
import com.ywl01.jlinfo.beans.FamilyNode;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by ywl01 on 2017/2/7.
 */

public class FamilyDataObserver implements Observer {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Object o) {
        System.out.println(o);
        ArrayList<FamilyNode> familyNodes = (ArrayList<FamilyNode>) o;
        FamilyActivity.familyNodes = familyNodes;
        AppUtils.startActivity(FamilyActivity.class);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
