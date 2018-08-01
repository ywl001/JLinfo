package com.ywl01.jlinfo.activities;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;


import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.beans.User;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.KeyName;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.UpdatePeopleEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.PeopleNumbleUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

/**
 * Created by ywl01 on 2017/2/23.
 */

public class EditPeopleActivity extends BaseActivity{

    /////人员基本信息
    @BindView(R.id.et_people_name)
    EditText etPeopleName;

    @BindView(R.id.rg_sex)
    RadioGroup rgSex;

    @BindView(R.id.et_nation)
    EditText etNation;

    @BindView(R.id.et_telephone)
    EditText etTelephone;

    @BindView(R.id.et_people_number)
    EditText etPeopleNumber;

    @BindView(R.id.is_dead)
    CheckBox isDead;

    @BindView(R.id.is_dead_group)
    LinearLayout isDeadGroup;

    @BindView(R.id.people_group)
    LinearLayout peopleGroup;

    ///人员工作信息
    @BindView(R.id.workplace_group)
    LinearLayout workplaceGroup;

    @BindView(R.id.et_department)
    EditText etDepartment;

    @BindView(R.id.et_job)
    EditText etJob;

    @BindView(R.id.is_manager)
    CheckBox isManager;

    //人员住所信息
    @BindView(R.id.building_group)
    LinearLayout buildingGroup;

    @BindView(R.id.et_room_number)
    EditText etRoomNumber;

    @BindView(R.id.et_relation)
    EditText etRelation;

    private int flag;
    private PeopleBean people;


    @Override
    protected void initView() {
        setContentView(R.layout.activity_add_people);
        ButterKnife.bind(this);

        people = (PeopleBean) CommVar.getInstance().get("people");
        flag = people.peopleFlag;

        etPeopleName.setText(people.name);
        rgSex.check(people.sex.equals("男") ? R.id.rb_man : R.id.rb_woman);
        etPeopleNumber.setText(people.peopleNumber);
        etNation.setText(people.nation);
        etTelephone.setText(people.telephone);
        isDead.setChecked(people.isDead == 1 ? true : false);

        if (flag == PeopleFlag.FROM_MARK) {
            workplaceGroup.setVisibility(View.VISIBLE);
            etDepartment.setText(people.department);
            etJob.setText(people.job);
            isManager.setChecked(people.isManager == 1 ? true : false);
        } else if (flag == PeopleFlag.FROM_BUILDING) {
            buildingGroup.setVisibility(View.VISIBLE);
            etRoomNumber.setText(people.roomNumber);
        }else if (flag == PeopleFlag.FROM_HOME) {

            etRelation.setText(people.relation);
        }
        isDeadGroup.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel() {
        finish();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        if (peopleInfoIsChange() && validateData()) {
            Map<String, String> peopleData = new HashMap<>();
            peopleData.put("name", etPeopleName.getText().toString().trim());
            peopleData.put("sex", rgSex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女");
            peopleData.put("nation", etNation.getText().toString().trim());
            peopleData.put("peopleNumber", etPeopleNumber.getText().toString().trim());
            peopleData.put("telephone", etTelephone.getText().toString().trim());
            peopleData.put("isDead", isDead.isChecked() ? "1" : "0");
            peopleData.put("updateUser", CommVar.UserID + "");

            people.name = peopleData.get("name");
            people.sex = peopleData.get("sex");
            people.nation = peopleData.get("nation");
            people.peopleNumber = peopleData.get("peopleNumber");
            people.telephone = peopleData.get("telephone");
            people.community = peopleData.get("community");
            people.liveType = peopleData.get("liveType");
            people.isDead = isDead.isChecked() ? 1 : 0;

            IntObserver updatePeopleObserver = new IntObserver();
            String sql = SqlFactory.update(TableName.PEOPLE, peopleData, people.id);
            HttpMethods.getInstance().getSqlResult(updatePeopleObserver, SqlAction.UPDATE, sql);
            updatePeopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                @Override
                public void onNext(Observer observer, Object data) {
                    int rows = (int) data;
                    if (rows > 0) {
                        AppUtils.showToast("更新人员信息成功。。。");
                        finish();
                        UpdatePeopleEvent event = new UpdatePeopleEvent();
                        event.people = people;
                        event.dispatch();
                    }
                }
            });
        }

        Map<String, String> otherData = null;
        if (flag == PeopleFlag.FROM_MARK && markInfoIsChange()) {
            otherData = new HashMap<>();
            otherData.put("department", etDepartment.getText().toString().trim());
            otherData.put("job", etJob.getText().toString().trim());
            otherData.put("isManager", isManager.isChecked() ? "1" : "0");
        } else if (flag == PeopleFlag.FROM_BUILDING && buildingInfoIsChange()) {
            otherData = new HashMap<>();
            otherData.put("roomNumber", etRoomNumber.getText().toString().trim());
        } else if (flag == PeopleFlag.FROM_HOME && relationIsChange()) {
            otherData = new HashMap<>();
            otherData.put("relation", etRelation.getText().toString().trim());
        }

        if (otherData != null) {
            IntObserver updateOtherObserver = new IntObserver();
            String sql = "";
            if (flag == PeopleFlag.FROM_MARK) {
                sql = SqlFactory.update(TableName.PEOPLE_MARK, otherData, people.pmID);
            } else if (flag == PeopleFlag.FROM_BUILDING) {
                sql = SqlFactory.update(TableName.PEOPLE_BUILDING, otherData, people.pbID);
            } else if (flag == PeopleFlag.FROM_HOME) {
                sql = SqlFactory.update(TableName.PEOPLE_HOME, otherData, people.phmID);
            }

            HttpMethods.getInstance().getSqlResult(updateOtherObserver, SqlAction.UPDATE, sql);
            updateOtherObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                @Override
                public void onNext(Observer observer, Object data) {
                    int rows = (int) data;
                    if (rows > 0) {
                        AppUtils.showToast("更新人员信息成功。。。");
                        finish();
                        UpdatePeopleEvent event = new UpdatePeopleEvent();
                        event.people = people;
                        event.dispatch();
                    }
                }
            });
        }
    }

    private boolean peopleInfoIsChange() {
        if (etPeopleName.getText().toString().trim().equals(people.name)
                && (rgSex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女").equals(people.sex)
                && etNation.getText().toString().trim().equals(people.nation)
                && etPeopleNumber.getText().toString().trim().equals(people.peopleNumber)
                && etTelephone.getText().toString().trim().equals(people.telephone)
                && (isDead.isChecked() ? 1 : 0) == people.isDead
                ) {
            return false;
        }
        return true;
    }

    private boolean markInfoIsChange() {
        if (etDepartment.getText().toString().trim().equals(people.department) &&
                etJob.getText().toString().trim().equals(people.job) &&
                (isManager.isChecked() ? 1 : 0) == people.isManager) {
            return false;
        }
        return true;
    }

    private boolean buildingInfoIsChange() {
        if (etRoomNumber.getText().toString().trim().equals(people.roomNumber)) {
            return false;
        }
        return true;
    }

    private boolean relationIsChange() {
        if (etRelation.getText().toString().trim().equals(people.relation)) {
            return false;
        }
        return true;
    }

    //验证填写的信息
    private boolean validateData() {
        if (etPeopleName.getText().equals("")) {
            AppUtils.showToast("人员姓名没有填写");
            return false;
        }

        if (rgSex.getCheckedRadioButtonId() == -1) {
            AppUtils.showToast("性别没有选择");
            return false;
        }

        boolean idValidateResult = PeopleNumbleUtils.validate(etPeopleNumber.getText().toString());
        if (!idValidateResult) {
            AppUtils.showToast(PeopleNumbleUtils.errorMessage);
        }
        return idValidateResult;
    }

}
