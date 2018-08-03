package com.ywl01.jlinfo.activities;


import com.ywl01.jlinfo.beans.FamilyNode;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.FamilyView;

import java.util.List;

/**
 * Created by ywl01 on 2017/2/23.
 */

public class FamilyActivity extends BaseActivity {
    public static List<FamilyNode> familyNodes;
    @Override
    protected void initView() {
        FamilyView familyView = new FamilyView(AppUtils.getContext());
        familyView.setData(familyNodes);
        setContentView(familyView);
    }
}
