package com.ywl01.jlinfo.activities;

import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.consts.KeyName;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.InsertObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.DialogUtils;
import com.ywl01.jlinfo.utils.PeopleNumbleUtils;
import com.ywl01.jlinfo.views.QueryPeopleView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

public class AddPeopleActivity extends BaseActivity implements QueryPeopleView.OnItemSelectListener, BaseObserver.OnNextListener, DialogInterface.OnClickListener {

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

    @BindView(R.id.workplace_group)
    LinearLayout workplaceGroup;

    @BindView(R.id.et_department)
    EditText etDepartment;

    @BindView(R.id.et_job)
    EditText etJob;

    @BindView(R.id.is_manager)
    CheckBox isManager;

    @BindView(R.id.building_group)
    LinearLayout buildingGroup;

    @BindView(R.id.et_room_number)
    EditText etRoomNumber;

    private int peopleFlag;
    private PeopleBean people;
    private int peopleExists;
    private int homeExists;
    private long peopleID;
    private List<PeopleBean> homePeoples;
    private Graphic graphic;
    private int graphicFlag;

    private InsertObserver insertPeopleObserver;
    private InsertObserver insertHomeObserver;
    private InsertObserver insertPMarkObserver;
    private InsertObserver insertPBuildingObserver;
    private InsertObserver insertPHouseObserver;
    private PeopleObserver selectHomePeopleObserver;
    private IntObserver checkPeopleIsAddObserver;

    @Override
    protected void initView() {
        QueryPeopleView queryPeopleView = new QueryPeopleView(this);
        queryPeopleView.SetIsAddNewPeople(true);

        graphic = (Graphic) CommVar.getInstance().get("graphic");
        graphicFlag = (int) graphic.getAttributes().get(KeyName.GRAPHIC_FLAG);
        int peopleFlag = -1;
        if (graphicFlag == GraphicFlag.MARK) {
            peopleFlag = PeopleFlag.FROM_MARK;
        } else if (graphicFlag == GraphicFlag.BUILDING) {
            peopleFlag = PeopleFlag.FROM_BUILDING;
        }else if (graphicFlag == GraphicFlag.HOUSE) {
            peopleFlag = PeopleFlag.FROM_HOUSE;
        }
        queryPeopleView.setPeopleFlag(peopleFlag);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
        queryPeopleView.setLayoutParams(params);
        setContentView(queryPeopleView);

        queryPeopleView.setOnClickBtnCancelListener(new QueryPeopleView.onClickBtnCancelListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        queryPeopleView.setOnItemSelectListener(this);
    }

    @Override
    public void onItemSelect(PeopleBean peopleBean) {
        System.out.println("on item select");
        checkPeopleIsAdded(peopleBean);
    }

    private void checkPeopleIsAdded(PeopleBean peopleBean) {
        String tableName = "";
        String sql = "";
        long graphicID = (long) graphic.getAttributes().get("id");
        long peopleID = peopleBean.id;
        if (graphicFlag == GraphicFlag.MARK) {
            tableName = TableName.PEOPLE_MARK;
            sql = "select peopleId from " + tableName + " where peopleID =  "+ peopleID + " and markID = "+ graphicID;
        } else if (graphicFlag == GraphicFlag.BUILDING) {
            tableName = TableName.PEOPLE_BUILDING;
            sql = "select peopleId from " + tableName + " where peopleID =  "+ peopleID + " and buildingID = "+ graphicID;
        }else if (graphicFlag == GraphicFlag.HOUSE) {
            tableName = TableName.PEOPLE_HOUSE;
            sql = "select peopleId from " + tableName + " where peopleID =  "+ peopleID + " and houseID = "+ graphicID;
        }
        checkPeopleIsAddObserver = new IntObserver();
        HttpMethods.getInstance().getSqlResult(checkPeopleIsAddObserver, SqlAction.SELECT, sql);
        checkPeopleIsAddObserver.setOnNextListener(this);
    }

    private void initAddPeopleView(PeopleBean peopleBean) {
        setContentView(R.layout.activity_add_people);
        ButterKnife.bind(AddPeopleActivity.this);

        people = peopleBean;
        peopleFlag = people.peopleFlag;

        if (peopleFlag == PeopleFlag.FROM_MARK) {
            workplaceGroup.setVisibility(View.VISIBLE);
        } else if (peopleFlag == PeopleFlag.FROM_BUILDING) {
            buildingGroup.setVisibility(View.VISIBLE);
        } else if (peopleFlag == PeopleFlag.FROM_SEARCH) {
            isDeadGroup.setVisibility(View.VISIBLE);
        }

        if (people.id > 0) {
            setUIValue(people);
            peopleExists = 1;
            peopleID = people.id;
        } else {
            peopleExists = 0;
            etPeopleName.setText(people.name);
            etPeopleNumber.setText(people.peopleNumber);
        }
    }


    @OnClick(R.id.btn_submit)
    public void onSubmint(View v) {
        if (vilidateData()) {
            people.isExists = peopleExists;
            people.homeExists = homeExists;

            if (peopleExists == 0) {
                people.peopleNumber = etPeopleNumber.getText().toString();
                people.name = etPeopleName.getText().toString();
                people.sex = rgSex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女";
                people.telephone = etTelephone.getText().toString();
                people.nation = etNation.getText().toString();
            }

            if (peopleFlag == PeopleFlag.FROM_SEARCH)
                people.isDead = isDead.isChecked() ? 1 : 0;
            else if (peopleFlag == PeopleFlag.FROM_MARK) {
                people.isManager = isManager.isChecked() ? 1 : 0;
                people.department = etDepartment.getText().toString();
                people.job = etJob.getText().toString();
            } else if (peopleFlag == PeopleFlag.FROM_BUILDING)
                people.roomNumber = etRoomNumber.getText().toString();

            if (peopleExists == 0) {
                //插入人员到人员库中，并返回人员id；
                insertPeople();
            } else {
                if (peopleFlag == PeopleFlag.FROM_MARK) {
                    //插入people_mark表
                    insertPeopleMark();
                } else if (peopleFlag == PeopleFlag.FROM_BUILDING || peopleFlag == PeopleFlag.FROM_HOUSE) {
                    //查询同户人员
                    selectPeopleInHome();
                }
            }
        }
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel() {
        finish();
    }

    private void insertPeople() {
        Map<String, String> peopleData = new HashMap<>();
        peopleData.put("peopleNumber", people.peopleNumber);
        peopleData.put("name", people.name);
        peopleData.put("sex", people.sex);
        peopleData.put("telephone", people.telephone);
        peopleData.put("nation", people.nation);
        peopleData.put("community", people.community);
        peopleData.put("liveType", people.liveType);
        peopleData.put("insertUser", CommVar.UserID + "");
        peopleData.put("isDead", people.isDead + "");
        peopleData.put("insertTime", "now()");

        String sql = SqlFactory.insert(TableName.PEOPLE, peopleData);
        insertPeopleObserver = new InsertObserver();
        HttpMethods.getInstance().getSqlResult(insertPeopleObserver, SqlAction.INSERT, sql);
        insertPeopleObserver.setOnNextListener(this);
    }

    //给新添加人员分配个户号
    private void insertPeopleHome() {
        Map<String, String> homeData = new HashMap<String, String>();
        homeData.put("peopleID", peopleID + "");
        homeData.put("homeNumber", System.currentTimeMillis() + "");
        homeData.put("relation", "户主");
        homeData.put("insertUser", CommVar.UserID + "");
        homeData.put("insertTime", "now()");

        insertHomeObserver = new InsertObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_HOME, homeData);
        HttpMethods.getInstance().getSqlResult(insertHomeObserver, SqlAction.INSERT, sql);
        insertHomeObserver.setOnNextListener(this);
    }

    private void insertPeopleMark() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("peopleID", peopleID + "");
        data.put("markID", graphic.getAttributes().get(KeyName.ID) + "");
        data.put("isManager", people.isManager + "");
        data.put("department", people.department);
        data.put("job", people.job);
        data.put("insertUser", CommVar.UserID + "");
        data.put("insertTime", "now()");

        insertPMarkObserver = new InsertObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_MARK, data);
        HttpMethods.getInstance().getSqlResult(insertPMarkObserver, SqlAction.INSERT, sql);
        insertPMarkObserver.setOnNextListener(this);
    }

    private void insertPeopleBuilding(PeopleBean p) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("peopleID", p.id + "");
        data.put("buildingID", graphic.getAttributes().get(KeyName.ID) + "");
        data.put("roomNumber", p.roomNumber);
        data.put("insertUser", CommVar.UserID + "");
        data.put("insertTime", "now()");

        insertPBuildingObserver = new InsertObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_BUILDING, data);
        HttpMethods.getInstance().getSqlResult(insertPBuildingObserver, SqlAction.INSERT, sql);
        insertPBuildingObserver.setOnNextListener(this);
    }

    private void insertPeopleHouse(PeopleBean p) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("peopleID", p.id + "");
        data.put("houseID", graphic.getAttributes().get(KeyName.ID) + "");
        data.put("insertUser", CommVar.UserID + "");
        data.put("insertTime", "now()");

        insertPHouseObserver = new InsertObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_HOUSE, data);
        HttpMethods.getInstance().getSqlResult(insertPHouseObserver, SqlAction.INSERT, sql);
        insertPHouseObserver.setOnNextListener(this);
    }

    private void selectPeopleInHome() {
        String sql = SqlFactory.selectPeoplesByHome(peopleID);
        selectHomePeopleObserver = new PeopleObserver(PeopleFlag.FROM_HOME);
        HttpMethods.getInstance().getSqlResult(selectHomePeopleObserver, SqlAction.SELECT, sql);
        selectHomePeopleObserver.setOnNextListener(this);
    }

    //验证填写的信息
    private boolean vilidateData() {
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

    //为人员控件赋值
    private void setUIValue(PeopleBean people) {
        etPeopleName.setText(people.name);
        rgSex.check(people.sex.equals("男") ? R.id.rb_man : R.id.rb_woman);
        etPeopleNumber.setText(people.peopleNumber);
        etNation.setText(people.nation);
        etTelephone.setText(people.telephone);
        setUIEnabled(peopleGroup, false);
    }

    private void setUIEnabled(View view, boolean enabled) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View v = vg.getChildAt(i);
                if (v instanceof ViewGroup) {
                    setUIEnabled(v, enabled);
                } else {
                    v.setEnabled(enabled);
                }
            }
        } else {
            view.setEnabled(enabled);
        }
    }

    @Override
    public void onNext(Observer observer,Object data) {
        if (observer == checkPeopleIsAddObserver) {
            List<Integer> peopleIDs = (List<Integer>) data;
            if (peopleIDs.size() > 0) {
                DialogUtils.showPrompt(this,"人员已经添加过了");
            }
        } else if (observer == insertPeopleObserver) {
            Long pid = (Long) data;
            if (pid > 0) {
                //插入户信息
                peopleID = pid;
                insertPeopleHome();
            }
        } else if (observer == insertHomeObserver) {
            if (peopleFlag == PeopleFlag.FROM_MARK) {
                insertPeopleMark();
            } else if (peopleFlag == PeopleFlag.FROM_HOUSE) {
                insertPeopleHouse(people);
            } else if (peopleFlag == PeopleFlag.FROM_BUILDING) {
                insertPeopleBuilding(people);
            }
        } else if (observer == insertPMarkObserver) {
            Long returnID = (Long) data;
            if (returnID > 0) {
                AppUtils.showToast("插入人员工作场所成功");
                finish();
            }
        } else if (observer == insertPBuildingObserver) {
            Long returnID = (Long) data;
            if (returnID > 0) {
                AppUtils.showToast("插入人员住所成功");
                finish();
            }
        } else if (observer == insertPHouseObserver) {
            Long returnID = (Long) data;
            if (returnID > 0) {
                AppUtils.showToast("插入人员住所成功");
                finish();
            }
        } else if (observer == selectHomePeopleObserver) {
            homePeoples = (List<PeopleBean>) data;
            DialogUtils.showAlert(this, "提示信息：", "和" + people.name + "同户的还有" + homePeoples.size() + "人,是否一起添加？", "确定", this, "取消", this);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            for (int i = 0; i < homePeoples.size(); i++) {
                if (peopleFlag == PeopleFlag.FROM_BUILDING) {
                    insertPeopleBuilding(homePeoples.get(i));
                } else {
                    insertPeopleHouse(homePeoples.get(i));
                }
            }
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            if (peopleFlag == PeopleFlag.FROM_BUILDING) {
                insertPeopleBuilding(people);
            } else {
                insertPeopleHouse(people);
            }
        }
    }

}
