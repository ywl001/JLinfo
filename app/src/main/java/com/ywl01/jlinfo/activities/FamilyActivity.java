package com.ywl01.jlinfo.activities;


import android.view.Menu;
import android.view.MenuItem;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.FamilyNode;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.FamilyView;

import java.util.List;

import pl.polidea.view.ZoomView;

/**
 * Created by ywl01 on 2017/2/23.
 */

public class FamilyActivity extends BaseActivity {
    public static List<FamilyNode> familyNodes;
    public static PeopleBean       basePeople;
    private FamilyView familyView;

    @Override
    protected void initView() {
        familyView = new FamilyView(AppUtils.getContext());
        familyView.setData(familyNodes);

        setContentView(familyView);

        setTitle(basePeople.name + "亲戚关系图");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.family,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.zoom_reset:
                familyView.resetZoom();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
