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
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.net.HttpMethods;
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

public class SetHomeFragment3 extends Fragment {

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
                Map<String, Object> data = new HashMap<>();
                data.put("id", setHomeBean.peopleID);
                peopleHomeObserver = new PeopleHomeObserver();
                HttpMethods.getInstance().getSqlResult(peopleHomeObserver, PhpFunction.SELECT_PEOPLE_IN_PARENT_HOME_INFO, data);
                peopleHomeObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                    @Override
                    public void onNext(Observer observer, Object data) {
                        List<PeopleHomeBean> homes = (List<PeopleHomeBean>) data;
                        if (homes.size() > 0) {
                            System.out.println("上级户号已经设置。。。");
                            PeopleHomeBean homeBean = homes.get(0);
                            if(homeBean.id == setHomeBean.oldHomeID){
                                insertNew();
                            }else{
                                System.out.println("显示对话框");
                                DialogUtils.showAlert(getActivity(),
                                        "提示：",
                                        "该人员已经从亲生父母户中分离，此次操作会将该人员从当前户中分离，建立新户，是否继续？",
                                        "确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                delOld();
                                            }
                                        }, "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                getActivity().finish();
                                            }
                                        });
                            }
                        } else {
                            updateOld();
                        }
                    }
                });
            }
            else{
                DialogUtils.showAlert(getActivity(),
                        "提示：",
                        "此次操作会将改变人员的户信息，是否继续？",
                        "确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delOld();
                            }
                        }, "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                            }
                        });
            }
        } else {
            insertNew();
        }
    }

    private void delOld() {
        System.out.println("删除老户号");
        delOldObserver = new IntObserver();
        PhpFunction.delete(delOldObserver,TableName.PEOPLE_HOME,setHomeBean.oldHomeID);
        delOldObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int rows = (int) data;
                if (rows > 0) {
                    //删除老户号成功
                    insertNew();
                }
            }
        });
    }

    private void insertNew() {
        Map<String, String> map = new HashMap<>();
        map.put("peopleID", setHomeBean.peopleID + "");
        map.put("homeNumber", setHomeBean.newHomeNumber);
        map.put("relation", setHomeBean.relation);
        map.put("updateUser", CommVar.loginUser.id + "");
        map.put("insertTime", "now()");

        insertNewObserver = new IntObserver();
        PhpFunction.insert(insertNewObserver, TableName.PEOPLE_HOME,map);
        insertNewObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int id = (int) data;
                if (id > 0) {
                    AppUtils.showToast("设置户号成功,可点击户信息按钮查看。");
                    getActivity().finish();
                }
            }
        });
    }

    private void updateOld() {
        Map<String, String> map = new HashMap<>();
        map.put("isDelete", "1");
        updateOldObserver = new IntObserver();
        PhpFunction.update(updateOldObserver,TableName.PEOPLE_HOME,map,setHomeBean.oldHomeID);
        updateOldObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int rows = (int) data;
                if (rows > 0) {
                    //更新老户号成功
                    insertNew();
                }
            }
        });
    }
}
