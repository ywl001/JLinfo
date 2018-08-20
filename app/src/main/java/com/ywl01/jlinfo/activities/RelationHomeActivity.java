package com.ywl01.jlinfo.activities;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.beans.PeopleHomeBean;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.observers.PeopleHomeObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.DialogUtils;
import com.ywl01.jlinfo.views.QueryPeopleView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;


public class RelationHomeActivity extends BaseActivity implements QueryPeopleView.OnItemSelectListener, QueryPeopleView.onClickBtnCancelListener {
    private static final int PARENT = 1;
    private static final int CHILD = 2;

    @BindView(R.id.root_view)
    LinearLayout rootView;

    @BindView(R.id.title_parent)
    TextView tvTitleParent;

    @BindView(R.id.title_child)
    TextView tvTitleChild;

    @BindView(R.id.query_people)
    QueryPeopleView queryPeopleView;

    @BindView(R.id.btn_add_parent)
    Button btnAddParent;

    @BindView(R.id.btn_add_child)
    Button btnAddChild;

    private int addFlag;
    private PeopleBean people;

    private PeopleBean childPeople;
    private PeopleBean parentPeople;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_relation_home);
        ButterKnife.bind(this);
        setTitle("建立直系亲属关系：");
        people = (PeopleBean) CommVar.getInstance().get("people");
        queryPeopleView.setOnItemSelectListener(this);
    }

    @OnClick(R.id.btn_add_parent)
    public void onClickAddParent() {
        checkIsHasParent();
    }

    @OnClick(R.id.btn_add_child)
    public void onClickAddChild() {
        addFlag = CHILD;
        parentPeople = people;

        if (TextUtils.isEmpty(parentPeople.homeNumber)) {
            getParentHomeNumber();
        } else {
            showAddChildUI();
        }
    }

    //检查父级是否存在
    private void checkIsHasParent() {
        IntObserver checkHasParentObserver = new IntObserver();
        Map<String, Object> data = new HashMap<>();
        data.put("id", people.id);
        HttpMethods.getInstance().getSqlResult(checkHasParentObserver, PhpFunction.CHECK_PEOPLE_IS_HAS_PARENT, data);
        checkHasParentObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                if ((int) data > 0) {
                    DialogUtils.showAlert(RelationHomeActivity.this, "该人员已经和爹建立联系过了。", "确定", null);
                }else {
                    addFlag = PARENT;
                    childPeople = people;
                    showAddParentUI();
                }
            }
        });
    }

    //当添加子级时，如果该人员的户号不存在，获取户号
    private void getParentHomeNumber() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", parentPeople.id);
        PeopleHomeObserver peopleHomeObserver = new PeopleHomeObserver();
        peopleHomeObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                List<PeopleHomeBean> homes = (List<PeopleHomeBean>) data;
                if (homes != null && homes.size() > 0) {
                    PeopleHomeBean home = homes.get(0);
                    parentPeople.homeNumber = home.homeNumber;
                    showAddChildUI();
                } else {
                    DialogUtils.showAlert(RelationHomeActivity.this, "该人员没有户信息，请先设置该人员的户信息。", "确定", null);
                }
            }
        });
        HttpMethods.getInstance().getSqlResult(peopleHomeObserver, PhpFunction.SELECT_PEOPLE_HOME_INFO,data);
    }

    @Override
    public void onItemSelect(PeopleBean peopleBean) {
        System.out.println("选择了人员");
        if (addFlag == CHILD) {
            childPeople = peopleBean;
            checkChildIsHaveParent();
        } else if (addFlag == PARENT) {
            parentPeople = peopleBean;
            showConfirmDialog();
        }
    }

    private void showConfirmDialog() {
        DialogUtils.showAlert(this, "提示：", "确定要建立连接吗，是否继续？", "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submit();
            }
        }, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    private void submit() {
        String relation = (childPeople.sex == "男") ? "儿子" : "女儿";
        Map<String, String> map = new HashMap<>();
        map.put("peopleID", childPeople.id + "");
        map.put("homeNumber", parentPeople.homeNumber);
        map.put("relation", relation);
        map.put("isDelete", "1");
        map.put("insertUser", CommVar.loginUser.id + "");
        map.put("updateUser", CommVar.loginUser.id + "");
        map.put("insertTime", "now()");

        IntObserver insertObserver = new IntObserver();
        PhpFunction.insert(insertObserver,TableName.PEOPLE_HOME,map);
        insertObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int returnID = (int) data;
                if (returnID > 0) {
                    finish();
                    AppUtils.showToast("添加链接成功");
                }
            }
        });
    }

    private void checkChildIsHaveParent() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", childPeople.id);
        IntObserver intObserver = new IntObserver();
        intObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                if ((int)data > 0) {
                    DialogUtils.showAlert(RelationHomeActivity.this, "你添加的这个子级已经有上级了。", "确定",null);
                }else{
                    showConfirmDialog();
                }
            }
        });
        HttpMethods.getInstance().getSqlResult(intObserver,PhpFunction.CHECK_PEOPLE_IS_HAS_PARENT,data);
    }

    @Override
    public void onClidkCancel() {
        finish();
    }

    private void showAddParentUI() {
        HideAll();
        tvTitleParent.setVisibility(View.VISIBLE);
        queryPeopleView.setVisibility(View.VISIBLE);
    }

    private void showAddChildUI() {
        HideAll();
        tvTitleChild.setVisibility(View.VISIBLE);
        queryPeopleView.setVisibility(View.VISIBLE);
        queryPeopleView.setOnClickBtnCancelListener(this);
        addFlag = CHILD;
    }

    private void HideAll() {
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View view = rootView.getChildAt(i);
            view.setVisibility(View.GONE);
        }
    }
}
