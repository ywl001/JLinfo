package com.ywl01.jlinfo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

/**
 * Created by ywl01 on 2018/5/29.
 */

public class SearchActivity extends BaseActivity implements SearchItemView.OnItemChangeListener, BaseObserver.OnNextListener {

    @BindView(R.id.root_view)
    LinearLayout rootView;

    @BindView(R.id.search_item)
    SearchItemView firstItem;

    List<SearchItemView> items;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        items = new ArrayList<>();
        firstItem.setOnItemChangeListener(this);
        firstItem.setBtnRemoveEnable(false);
        items.add(firstItem);
        firstItem.setBtnRemoveVisible(false);
    }

    @Override
    protected void initActionBar() {
        setTitle("综合查询：");
    }

    @Override
    public void onAdd(SearchItemView view) {
        int position = items.indexOf(view);
        if ((position == items.size() - 1) && view.checkAdd()) {
            SearchItemView itemView = new SearchItemView(this);
            itemView.setOnItemChangeListener(this);
            rootView.addView(itemView);
            items.add(itemView);
            setRemoveButtonEnable();
        }
    }

    private void setRemoveButtonEnable() {
        for (int i = 0; i < items.size(); i++) {
            SearchItemView v = items.get(i);
            if (i < items.size() - 1 || i == 0) {
                v.setBtnRemoveVisible(false);
            } else {
                v.setBtnRemoveVisible(true);
            }
        }
    }

    @Override
    public void onRemove(SearchItemView view) {
        items.remove(view);
        setRemoveButtonEnable();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        String sql = getSql();
        if (TextUtils.isEmpty(sql)) {
            AppUtils.showToast("请创建查询条件");
            return;
        }

        sql = "select m.*,u.realName insertUser from monitor m left join user u on m.userID = u.id where " + sql;

        System.out.println(sql);

        CamerasObserver camerasObserver = new CamerasObserver();
        camerasObserver.setOnNextListener(this);
        HttpMethods.getInstance().getSqlResult(camerasObserver, SqlAction.SELECT, sql);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel() {
        finish();
    }

    private String getSql() {
        String sql = "";
        for (int i = 0; i < items.size(); i++) {
            Map<String, String> data = items.get(i).getData();
            if (data != null) {
                sql += mapToStr(data);
            } else {
                continue;
            }
        }
        //如果没有值，直接返回
        if (TextUtils.isEmpty(sql)) {
            return sql;
        }

        if (sql.length() > 4 && " or ".equals(sql.substring(sql.length() - 4))) {
            sql = sql.substring(0, sql.length() - 4);
        } else if (sql.length() > 5 && " and ".equals(sql.substring(sql.length() - 5))) {
            sql = sql.substring(0, sql.length() - 5);
        }
        return sql;
    }

    private String mapToStr(Map<String, String> data) {
        String operator = data.get("operator");
        String field = data.get("field");
        String keyword = data.get("keyword");
        String logic = "";
        if (data.get("logic") != null) {
            logic = data.get("logic");
        }

        if (operator.equals("contain")) {
            return getStr(field, " like '%", keyword, "%' ", logic);
        } else if (operator.equals("noContain")) {
            return getStr(field, " not like '%", keyword, "%' ", logic);
        } else if (operator.equals("start")) {
            return getStr(field, " like '", keyword, "%' ", logic);
        } else if (operator.equals("end")) {
            return getStr(field, " like '%", keyword, "' ", logic);
        } else {
            return getStr(field, operator + " '", keyword, "' ", logic);
        }
    }

    //针对 包含、不包含、开始于等的获取字符串
    private String getStr(String field, String str1, String keyword, String str2, String logic) {
        String str = "";
        str = field + str1 + keyword + str2 + logic + " ";
        return str;
    }

    @Override
    public void onNext(Object data, Observer observer) {
        ArrayList<CameraBean> cameras = (ArrayList<CameraBean>) data;
        if (cameras.size() > 0) {
            Bundle args = new Bundle();
            args.putParcelableArrayList("cameras",cameras);
            Intent intent = new Intent();
            intent.putExtras(args);
            setResult(RESULT_OK,intent);
            finish();
        }else{
            AppUtils.showToast("没有符号条件的人员");
        }
    }
}
