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
import com.ywl01.jlinfo.beans.BuildingBean;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.SelectValueEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.DialogUtils;
import com.ywl01.jlinfo.utils.PeopleNumbleUtils;
import com.ywl01.jlinfo.views.QueryPeopleView;
import com.ywl01.jlinfo.views.SelectBuildingRoomNumberDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
            addPeopleFlag = (int) graphic.getAttributes().get("graphicFlag");
        } else
            addPeopleFlag = GraphicFlag.NONE;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
        queryPeopleView.setLayoutParams(params);
        setContentView(queryPeopleView);

        setTitle("请查询出要添加的人员");
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
        Map<String, Object> tableData = new HashMap<>();
        tableData.put("peopleID", peopleBean.id);
        tableData.put("graphicID", graphic.getAttributes().get("id"));

        if (addPeopleFlag == GraphicFlag.MARK) {
            tableData.put("tableName",TableName.PEOPLE_MARK);
            tableData.put("graphicIDName", "markID");
        } else if (addPeopleFlag == GraphicFlag.BUILDING) {
            tableData.put("tableName",TableName.PEOPLE_BUILDING);
            tableData.put("graphicIDName", "buildingID");
        } else if (addPeopleFlag == GraphicFlag.HOUSE) {
            tableData.put("tableName",TableName.PEOPLE_HOUSE);
            tableData.put("graphicIDName", "houseID");
        }
        IntObserver checkPeopleIsAddObserver = new IntObserver();
        HttpMethods.getInstance().getSqlResult(checkPeopleIsAddObserver,PhpFunction.CHECK_PEOPLE_IS_IN_GRAPHIC,tableData);
        checkPeopleIsAddObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                if ((int)data > 0) {
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
        setTitle("请完善人员的相应信息：");
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
            btnRandomPeopleNumber.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.btn_submit)
    public void onSubmint(View v) {
        if (vilidateData()) {
            //人员不在库中，从界面获取人员信息，然后添加到库
            if (people.isExists == 0) {
                insertPeople(people);
            } else {
                if (addPeopleFlag == GraphicFlag.MARK) {
                    insertPeopleMark(people);
                } else if (addPeopleFlag == GraphicFlag.BUILDING) {

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
    private void insertPeople(PeopleBean p) {
        p.peopleNumber = etPeopleNumber.getText().toString();
        p.name = etPeopleName.getText().toString();
        p.sex = rgSex.getCheckedRadioButtonId() == R.id.rb_man ? "男" : "女";
        p.telephone = etTelephone.getText().toString();
        p.nation = etNation.getText().toString();

        Map<String, Object> peopleData = new HashMap<>();
        peopleData.put("peopleNumber", p.peopleNumber);
        peopleData.put("name", p.name);
        peopleData.put("sex", p.sex);
        peopleData.put("telephone", p.telephone);
        peopleData.put("nation", p.nation);
        peopleData.put("community", p.community);
        peopleData.put("liveType", p.liveType);
        peopleData.put("insertUser", CommVar.loginUser.id);
        peopleData.put("isDead", p.isDead);
        peopleData.put("insertTime", "now()");

        IntObserver insertPeopleObserver = new IntObserver();
        PhpFunction.insert(insertPeopleObserver,TableName.PEOPLE,peopleData);
        insertPeopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int pid = (int) data;
                if (pid > 0) {
                    //插入户信息
                    people.id = pid;
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
        homeData.put("insertUser", CommVar.loginUser.id + "");
        homeData.put("updateUser", CommVar.loginUser.id + "");
        homeData.put("insertTime", "now()");

        IntObserver insertHomeObserver = new IntObserver();
        PhpFunction.insert(insertHomeObserver,TableName.PEOPLE_HOME,homeData);
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
    private void insertPeopleMark(PeopleBean p) {
        p.isManager = isManager.isChecked() ? 1 : 0;
        p.department = etDepartment.getText().toString();
        p.job = etJob.getText().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("peopleID", p.id);
        data.put("markID", graphic.getAttributes().get("id"));
        data.put("isManager", p.isManager);
        data.put("department", p.department);
        data.put("job", p.job);
        data.put("insertUser", CommVar.loginUser.id);
        data.put("updateUser", CommVar.loginUser.id);
        data.put("insertTime", "now()");

        IntObserver insertPMarkObserver = new IntObserver();
        PhpFunction.insert(insertPMarkObserver,TableName.PEOPLE_MARK,data);
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
        p.roomNumber = etRoomNumber.getText().toString();

        Map<String, Object> data = new HashMap<>();
        data.put("peopleID", p.id);
        data.put("buildingID", graphic.getAttributes().get("id"));
        data.put("roomNumber", p.roomNumber);
        data.put("insertUser", CommVar.loginUser.id);
        data.put("updateUser", CommVar.loginUser.id);
        data.put("insertTime", "now()");

        IntObserver insertPBuildingObserver = new IntObserver();
        PhpFunction.insert(insertPBuildingObserver,TableName.PEOPLE_BUILDING,data);
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
        Map<String, Object> data = new HashMap<>();
        data.put("peopleID", p.id);
        data.put("houseID", graphic.getAttributes().get("id"));
        data.put("insertUser", CommVar.loginUser.id);
        data.put("insertTime", "now()");

        IntObserver insertPHouseObserver = new IntObserver();
        PhpFunction.insert(insertPHouseObserver,TableName.PEOPLE_HOUSE,data);
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

    //检索同户人员,根據需求添加人員
    private void selectPeopleInHome(final PeopleBean people) {
        Map<String, Object> tableData = new HashMap<>();
        tableData.put("id", people.id);
        PeopleObserver selectHomePeopleObserver = new PeopleObserver(PeopleFlag.FROM_HOME);
        HttpMethods.getInstance().getSqlResult(selectHomePeopleObserver, PhpFunction.SELECT_HOME_PEOPLES_BY_PEOPLE_ID, tableData);
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


    @OnClick(R.id.et_room_number)
    public void showSelectBudingRoomNumber() {
        if (addPeopleFlag == GraphicFlag.BUILDING) {
            Map<String, Object> attributes = graphic.getAttributes();
            BuildingBean building = new BuildingBean();
            AppUtils.mapToBean(attributes,building);

            createBuildingRoomNumbers(building);
        }
    }

    private void createBuildingRoomNumbers(BuildingBean building) {
        if (building.countFloor < 1 || building.countUnit < 1 || building.countHomesInUnit < 1 || AppUtils.isEmptyString(building.sortType)) {
            AppUtils.showToast("楼房的参数不正确，请先设置楼房的参数");
            return;
        }
        SelectBuildingRoomNumberDialog dialog = new SelectBuildingRoomNumberDialog(this,building);
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onGetRoomNumber(SelectValueEvent event) {
        if (event.type == SelectValueEvent.SELECT_ROOM_NUMBER) {
            etRoomNumber.setText(event.selectValue.toString());
        }
    }
}
