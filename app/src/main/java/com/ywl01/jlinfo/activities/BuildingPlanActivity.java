package com.ywl01.jlinfo.activities;


import com.ywl01.jlinfo.beans.BuildingBean;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.views.BuildingPlan;

import java.util.List;

/**
 * Created by ywl01 on 2017/2/22.
 */

public class BuildingPlanActivity extends BaseActivity {
    @Override
    protected void initView() {

        BuildingPlan buildingPlan = new BuildingPlan(this);
        List<PeopleBean> peoples = (List<PeopleBean>) CommVar.getInstance().get("peoples");
        BuildingBean building = (BuildingBean) CommVar.getInstance().get("building");

        buildingPlan.setData(building,peoples);
        setContentView(buildingPlan);
        getSupportActionBar().hide();
    }
}
