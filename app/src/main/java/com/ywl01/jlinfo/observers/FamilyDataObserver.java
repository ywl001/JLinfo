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

public class FamilyDataObserver extends BaseObserver<List<FamilyNode>,List<FamilyNode>> {
    @Override
    protected List<FamilyNode> convert(List<FamilyNode> data) {
        return data;
    }
}
