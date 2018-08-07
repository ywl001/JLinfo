package com.ywl01.jlinfo.activities;

import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
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

public class AddPeopleActivity extends BaseActivity {

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

    @BindView(R.id.btn_random)
    Button btnRandomPeopleNumber;


    //人员添加的标识，用于初始化界面、提交数据库
    private int addPeopleFlag;

    //需要添加的人员
    private PeopleBean people;

    //人员需要关联的graphic
    private Graphic graphic;

    @Override
    protected void initView() {
        QueryPeopleView queryPeopleView = new QueryPeopleView(this);
        queryPeopleView.SetIsAddNewPeople(true);

        graphic = (Graphic) CommVar.getInstance().get("graphic");

        addPeopleFlag = -1;
        if (graphic != null) {
            addPeopleFlag = (int) graphic.getAttributes().get("grahpicFlag");
        } else
            addPeopleFlag = GraphicFlag.NONE;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
        queryPeopleView.setLayoutParams(params);
        setContentView(queryPeopleView);

        //点击取消，退出界面
        queryPeopleView.setOnClickBtnCancelListener(new QueryPeopleView.onClickBtnCancelListener() {
            @Override
            public void onClick() {
                finish();
            }
        });

        //选择人员后，检查人员是否添加过了
        queryPeopleView.setOnItemSelectListener(new QueryPeopleView.OnItemSelectListener() {
            @Override
            public void onItemSelect(PeopleBean peopleBean) {
                people = peopleBean;
                if (people.id > 0 && graphic != null) {
                    checkPeopleIsAdded(peopleBean);
                } else {
                    //不在库中人员和不需要关联graphic的人员，不用检查
                    initAddPeopleView();
                }
            }
        });
    }

    //检查人员是否已经添加到graphic内了
    private void checkPeopleIsAdded(PeopleBean peopleBean) {
        String tableName = "";
        String sql = "";
        int graphicID = (int) graphic.getAttributes().get("id");
        int peopleID = peopleBean.id;
        if (addPeopleFlag == GraphicFlag.MARK) {
            tableName = TableName.PEOPLE_MARK;
            sql = "select * from " + tableName + " where peopleID =  " + peopleID + " and markID = " + graphicID;
        } else if (addPeopleFlag == GraphicFlag.BUILDING) {
            tableName = TableName.PEOPLE_BUILDING;
            sql = "select * from " + tableName + " where peopleID =  " + peopleID + " and buildingID = " + graphicID;
        } else if (addPeopleFlag == GraphicFlag.HOUSE) {
            tableName = TableName.PEOPLE_HOUSE;
            sql = "select * from " + tableName + " where peopleID =  " + peopleID + " and houseID = " + graphicID;
        }
        PeopleObserver checkPeopleIsAddObserver = new PeopleObserver(addPeopleFlag);
        HttpMethods.getInstance().getSqlResult(checkPeopleIsAddObserver, SqlAction.SELECT, sql);
        checkPeopleIsAddObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                List<PeopleBean> peoples = (List<PeopleBean>) data;
                if (peoples.size() > 0) {
                    DialogUtils.showPrompt(AddPeopleActivity.this, "人员已经添加过了");
                } else {
                    initAddPeopleView();
                }
            }
        });
    }

    //初始化添加人员的界面
    private void initAddPeopleView() {
        setContentView(R.layout.activity_add_people);
        ButterKnife.bind(AddPeopleActivity.this);

        if (addPeopleFlag == GraphicFlag.MARK) {
            workplaceGroup.setVisibility(View.VISIBLE);
        } else if (addPeopleFlag == GraphicFlag.BUILDING) {
            buildingGroup.setVisibility(View.VISIBLE);
        } else if (addPeopleFlag == GraphicFlag.NONE) {
            isDeadGroup.setVisibility(View.VISIBLE);
            btnRandomPeopleNumber.setVisibility(View.VISIBLE);
        }

        //人员在库中的情况，设置人员信息不可编辑
        if (people.id > 0) {
            setUIValue(people);
            people.isExists = 1;
        }
        //人员不在库中
        else {
            people.isExists = 0;
            etPeopleName.setText(people.name);
            etPeopleNumber.setText(people.peopleNumber);
        }
    }

    @OnClick(R.id.btn_submit)
    public void onSubmint(View v) {
        if (vilidateData()) {
            //人员不在库中，从界面获取人员信息，然后添加到库
            if (people.isExists == 0) {
                people.peopleNumber = etPeopleNumber.getText().toString();
                people.name = etPeopleName.getText().toString();
                people.sex = rgSex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女";
                people.telephone = etTelephone.getText().toString();
                people.nation = etNation.getText().toString();

                insertPeople(people);
            } else {
                if (addPeopleFlag == GraphicFlag.MARK) {
                    people.isManager = isManager.isChecked() ? 1 : 0;
                    people.department = etDepartment.getText().toString();
                    people.job = etJob.getText().toString();
                    insertPeopleMark(people);
                } else if (addPeopleFlag == GraphicFlag.BUILDING) {
                    people.roomNumber = etRoomNumber.getText().toString();
                    selectPeopleInHome(people);
                } else if (addPeopleFlag == GraphicFlag.HOUSE) {
                    selectPeopleInHome(people);
                }
            }
        }
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel() {
        finish();
    }

    @OnClick(R.id.btn_random)
    public void onGetRandomPeopleNumber() {
        if (rgSex.getCheckedRadioButtonId() == -1) {
            AppUtils.showToast("请先选择性别");
            return;
        }
        String sex = rgSex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女";
        String randomPeopleNumber = PeopleNumbleUtils.CreateRandomPeopleNumber(sex);
        etPeopleNumber.setText(randomPeopleNumber);
    }

    //新人员进人员库
    private void insertPeople(PeopleBean people) {
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
        IntObserver insertPeopleObserver = new IntObserver();
        HttpMethods.getInstance().getSqlResult(insertPeopleObserver, SqlAction.INSERT, sql);
        insertPeopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int pid = (int) data;
                if (pid > 0) {
                    //插入户信息
                    insertPeopleHome(pid);
                }
            }
        });
    }

    //给新添加人员分配个户号
    private void insertPeopleHome(int peopleID) {
        Map<String, String> homeData = new HashMap<String, String>();
        homeData.put("peopleID", peopleID + "");
        homeData.put("homeNumber", System.currentTimeMillis() + "");
        homeData.put("relation", "户主");
        homeData.put("insertUser", CommVar.UserID + "");
        homeData.put("updateUser", CommVar.UserID + "");
        homeData.put("insertTime", "now()");

        IntObserver insertHomeObserver = new IntObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_HOME, homeData);
        HttpMethods.getInstance().getSqlResult(insertHomeObserver, SqlAction.INSERT, sql);
        insertHomeObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                if (addPeopleFlag == GraphicFlag.MARK) {
                    insertPeopleMark(people);
                } else if (addPeopleFlag == GraphicFlag.HOUSE) {
                    insertPeopleHouse(people);
                } else if (addPeopleFlag == GraphicFlag.BUILDING) {
                    insertPeopleBuilding(people);
                } else if (addPeopleFlag == GraphicFlag.NONE) {
                    finish();
                    AppUtils.showToast("插入成功");
                }
            }
        });
    }

    //插入人员到场所单位
    private void insertPeopleMark(PeopleBean people) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("peopleID", people.id + "");
        data.put("markID", graphic.getAttributes().get("id") + "");
        data.put("isManager", people.isManager + "");
        data.put("department", people.department);
        data.put("job", people.job);
        data.put("insertUser", CommVar.UserID + "");
        data.put("updateUser", CommVar.UserID + "");
        data.put("insertTime", "now()");

        IntObserver insertPMarkObserver = new IntObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_MARK, data);
        HttpMethods.getInstance().getSqlResult(insertPMarkObserver, SqlAction.INSERT, sql);
        insertPMarkObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int returnID = (int) data;
                if (returnID > 0) {
                    AppUtils.showToast("插入人员工作场所成功");
                    finish();
                }
            }
        });
    }

    //插入人员到住宅楼
    private void insertPeopleBuilding(PeopleBean p) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("peopleID", p.id + "");
        data.put("buildingID", graphic.getAttributes().get("id") + "");
        data.put("roomNumber", p.roomNumber);
        data.put("insertUser", CommVar.UserID + "");
        data.put("updateUser", CommVar.UserID + "");
        data.put("insertTime", "now()");

        IntObserver insertPBuildingObserver = new IntObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_BUILDING, data);
        HttpMethods.getInstance().getSqlResult(insertPBuildingObserver, SqlAction.INSERT, sql);
        insertPBuildingObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int returnID = (int) data;
                if (returnID > 0) {
                    AppUtils.showToast("插入人员住所成功");
                    finish();
                }
            }
        });
    }

    //插入人员到民房
    private void insertPeopleHouse(PeopleBean p) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("peopleID", p.id + "");
        data.put("houseID", graphic.getAttributes().get("id") + "");
        data.put("insertUser", CommVar.UserID + "");
        data.put("insertTime", "now()");

        IntObserver insertPHouseObserver = new IntObserver();
        String sql = SqlFactory.insert(TableName.PEOPLE_HOUSE, data);
        HttpMethods.getInstance().getSqlResult(insertPHouseObserver, SqlAction.INSERT, sql);
        insertPHouseObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int returnID = (int) data;
                if (returnID > 0) {
                    AppUtils.showToast("插入人员住所成功");
                    finish();
                }
            }
        });
    }

    //检索同户人员
    private void selectPeopleInHome(final PeopleBean people) {
        String sql = SqlFactory.selectPeoplesByHome(people.id);
        PeopleObserver selectHomePeopleObserver = new PeopleObserver(PeopleFlag.FROM_HOME);
        HttpMethods.getInstance().getSqlResult(selectHomePeopleObserver, SqlAction.SELECT, sql);
        selectHomePeopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                final List<PeopleBean> homePeoples = (List<PeopleBean>) data;
                //有同户人员
                if (homePeoples.size() > 1)
                    DialogUtils.showAlert(AddPeopleActivity.this,
                            "提示信息：",
                            "和" + people.name + "同户的还有" + (homePeoples.size() - 1) + "人,是否一起添加？",
                            "确定",
                            new DialogInterface.OnClickListener() {
                                //点击确定，一起添加同户所有人员和graphic关联
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    for (int i = 0; i < homePeoples.size(); i++) {
                                        if (addPeopleFlag == GraphicFlag.BUILDING) {
                                            insertPeopleBuilding(homePeoples.get(i));
                                        } else {
                                            insertPeopleHouse(homePeoples.get(i));
                                        }
                                    }
                                    finish();
                                }
                            }, "取消",
                            //点击取消，只添加人员自己
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (addPeopleFlag == GraphicFlag.BUILDING) {
                                        insertPeopleBuilding(people);
                                    } else {
                                        insertPeopleHouse(people);
                                    }
                                    finish();
                                }
                            });
                else {
                    //没有同户人员
                    if (addPeopleFlag == GraphicFlag.BUILDING) {
                        insertPeopleBuilding(people);
                    } else {
                        insertPeopleHouse(people);
                    }
                    finish();
                }
            }
        });
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

    //设置ui是否可交互
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
}
