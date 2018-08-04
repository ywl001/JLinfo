package com.ywl01.jlinfo.views;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.SetHomeActivity;
import com.ywl01.jlinfo.beans.PeopleHomeBean;
import com.ywl01.jlinfo.beans.SetHomeBean;
import com.ywl01.jlinfo.beans.User;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

/**
 * Created by ywl01 on 2017/2/15.
 */

public class SetHomeFragment3 extends Fragment implements BaseObserver.OnNextListener,DialogInterface.OnClickListener {

    @BindView(R.id.tv_text)
    TextView tv;

    SetHomeBean setHomeBean;
    private PeopleHomeObserver peopleHomeObserver;
    private IntObserver updateOldObserver;
    private IntObserver insertNewObserver;
    private IntObserver delOldObserver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_home3, null);
        ButterKnife.bind(this, view);
        String str = "　　被操作人员<font color='#FF0000'>是否从亲身父母户中分离出来？</font>如果是，系统会维护这种关系，会在查询亲戚列表中显示出来，请根据实际情况选择是或者否。";
        tv.setText(Html.fromHtml(str));
        return view;
    }

    @OnClick(R.id.btn_yes)
    public void onClickYes() {
        SetHomeActivity activity = (SetHomeActivity) getActivity();
        activity.setHomeBean.isRealLeave = true;
        setHomeBean = activity.setHomeBean;
        setHome();
    }

    @OnClick(R.id.btn_no)
    public void onClickNo() {
        SetHomeActivity activity = (SetHomeActivity) getActivity();
        activity.setHomeBean.isRealLeave = false;
        setHomeBean = activity.setHomeBean;
        setHome();
    }

    private void setHome() {
        if (setHomeBean.homeIsExists) {
            if (setHomeBean.isRealLeave) {
                //查询父级户号是否设置
                String sql = "select * from " + TableName.PEOPLE_HOME + " where peopleID = " + setHomeBean.peopleID + " and isDelete = 1";
                peopleHomeObserver = new PeopleHomeObserver();
                HttpMethods.getInstance().getSqlResult(peopleHomeObserver, SqlAction.SELECT, sql);
                peopleHomeObserver.setOnNextListener(this);
            }
            else{
                DialogUtils.showAlert(getActivity(), "提示：", "此次操作会将改变人员的户信息，是否继续？", "确定", this, "取消", this);
            }
        } else {
            insertNew();
        }
    }

    private void delOld() {
        System.out.println("删除老户号");
        String sql = SqlFactory.delete(TableName.PEOPLE_HOME, setHomeBean.oldHomeID);
        delOldObserver = new IntObserver();
        HttpMethods.getInstance().getSqlResult(delOldObserver,SqlAction.DELETE,sql);
        delOldObserver.setOnNextListener(this);
    }

    @Override
    public void onNext(Observer observer,Object data) {
        if (observer == peopleHomeObserver) {
            List<PeopleHomeBean> homes = (List<PeopleHomeBean>) data;
            if (homes.size() > 0) {
                System.out.println("上级户号已经设置。。。");
                PeopleHomeBean homeBean = homes.get(0);
                if(homeBean.id == setHomeBean.oldHomeID){
                    insertNew();
                }else{
                    System.out.println("显示对话框");
                    DialogUtils.showAlert(getActivity(), "提示：", "该人员已经从亲生父母户中分离，此次操作会将该人员从当前户中分离，建立新户，是否继续？", "确定", this, "取消", this);
                }
            } else {
                updateOld();
            }
        } else if (observer == updateOldObserver) {
            int rows = (int) data;
            if (rows > 0) {
                //更新老户号成功
                insertNew();
            }
        } else if (observer == insertNewObserver) {
            int id = (int) data;
            if (id > 0) {
                AppUtils.showToast("设置户号成功,可点击户信息按钮查看。");
                getActivity().finish();
            }
        } else if (observer == delOldObserver) {
            int rows = (int) data;
            if (rows > 0) {
                //删除老户号成功
                insertNew();
            }
        }
    }

    private void insertNew() {
        Map<String, String> map = new HashMap<>();
        map.put("peopleID", setHomeBean.peopleID + "");
        map.put("homeNumber", setHomeBean.newHomeNumber);
        map.put("relation", setHomeBean.relation);
        map.put("updateUser", CommVar.UserID + "");
        map.put("insertTime", "now()");

        String sql = SqlFactory.insert(TableName.PEOPLE_HOME, map);
        insertNewObserver = new IntObserver();
        HttpMethods.getInstance().getSqlResult(insertNewObserver, SqlAction.INSERT, sql);
        insertNewObserver.setOnNextListener(this);
    }

    private void updateOld() {
        Map<String, String> map = new HashMap<>();
        map.put("isDelete", "1");
        String sql = SqlFactory.update(TableName.PEOPLE_HOME,map,setHomeBean.oldHomeID);
        updateOldObserver = new IntObserver();
        HttpMethods.getInstance().getSqlResult(updateOldObserver,SqlAction.UPDATE,sql);
        updateOldObserver.setOnNextListener(this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            System.out.println("确定");
            delOld();
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            getActivity().finish();
        }
    }
}
