package com.ywl01.jlinfo.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.beans.PeopleHomeBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
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


public class RelationHomeActivity extends BaseActivity implements BaseObserver.OnNextListener, QueryPeopleView.OnItemSelectListener, QueryPeopleView.onClickBtnCancelListener,DialogInterface.OnClickListener {
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
    private PeopleHomeObserver checkHasParentObserver;

    private PeopleBean childPeople;
    private PeopleBean parentPeople;
    private PeopleHomeObserver getParentHomeNumberObserver;
    private PeopleHomeObserver checkHasParentObserver_child;
    private IntObserver insertObserver;

    private Dialog confirmSubmitDialog;
    private Dialog hasParentDialog;
    private Dialog noHomeNumberDialog;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_relation_home);
        ButterKnife.bind(this);
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
        String sql = "select id from people_home where peopleID = '" + people.id + "' and isDelete = 1";
        checkHasParentObserver = new PeopleHomeObserver();
        HttpMethods.getInstance().getSqlResult(checkHasParentObserver, SqlAction.SELECT, sql);
        checkHasParentObserver.setOnNextListener(this);
    }

    //当添加子级时，如果该人员的户号不存在，获取户号
    private void getParentHomeNumber() {
        String sql = "select id,homeNumber,relation from people_home where peopleID = '" + parentPeople.id + "' and isDelete = 1";
        getParentHomeNumberObserver = new PeopleHomeObserver();
        getParentHomeNumberObserver.setOnNextListener(this);
        HttpMethods.getInstance().getSqlResult(getParentHomeNumberObserver, SqlAction.SELECT, sql);
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
        confirmSubmitDialog = DialogUtils.showAlert(this, "提示：", "确定要建立连接吗，是否继续？", "确定", new DialogInterface.OnClickListener() {
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
        map.put("insertUser", CommVar.UserID + "");
        map.put("updateUser", CommVar.UserID + "");
        map.put("insertTime", "now()");

        insertObserver = new IntObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_HOME, map);
        HttpMethods.getInstance().getSqlResult(insertObserver,SqlAction.INSERT,sql);
        insertObserver.setOnNextListener(this);
    }

    private void checkChildIsHaveParent() {
        String sql = "select id from people_home where peopleID = '" + childPeople.id + "' and isDelete = 1";
        checkHasParentObserver_child = new PeopleHomeObserver();
        HttpMethods.getInstance().getSqlResult(checkHasParentObserver_child, SqlAction.SELECT, sql);
        checkHasParentObserver_child.setOnNextListener(this);
    }

    @Override
    public void onClick() {
        finish();
    }

    @Override
    public void onNext( Observer observer,Object data) {
        if (observer == checkHasParentObserver) {
            List<PeopleHomeBean> homes = (List<PeopleHomeBean>) data;
            if (homes != null && homes.size() > 0){
                //显示提示框，已经添加过了
                hasParentDialog = DialogUtils.showAlert(this, "该人员已经和爹建立联系过了。", "确定", this);
            } else {
                addFlag = PARENT;
                childPeople = people;
                showAddParentUI();
            }
        } else if (observer == getParentHomeNumberObserver) {
            List<PeopleHomeBean> homes = (List<PeopleHomeBean>) data;
            if (homes != null && homes.size() > 0) {
                PeopleHomeBean home = homes.get(0);
                parentPeople.homeNumber = home.homeNumber;
                showAddChildUI();
            } else {
                noHomeNumberDialog = DialogUtils.showAlert(this, "该人员没有户信息，请先设置该人员的户信息。", "确定", this);
            }
        } else if (observer == checkHasParentObserver_child) {
            List<PeopleHomeBean> homes = (List<PeopleHomeBean>) data;
            if (homes != null && homes.size() > 0) {
                //显示提示框，已经添加过了
               hasParentDialog =  DialogUtils.showAlert(this, "你添加的这个子级已经有上级了。", "确定",this);
            } else {
                showConfirmDialog();
            }
        } else if (observer == insertObserver) {
            int returnID = (int) data;
            if (returnID > 0) {
                finish();
                AppUtils.showToast("添加链接成功");
            }
        }
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
        addFlag = CHILD;
    }

    private void HideAll() {
        for (int i = 0; i < rootView.getChildCount(); i++) {
            View view = rootView.getChildAt(i);
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog == confirmSubmitDialog) {
            if(which == Dialog.BUTTON_POSITIVE){
                submit();
            }else {
                finish();
            }

        } else if (dialog == hasParentDialog) {
            if(which == Dialog.BUTTON_POSITIVE){
                finish();
            }
        } else if (dialog == noHomeNumberDialog) {
            if(which == Dialog.BUTTON_POSITIVE){
                finish();
            }
        }
    }
}
