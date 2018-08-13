package com.ywl01.jlinfo.activities;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.esri.arcgisruntime.geometry.Point;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.SelectValueEvent;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.views.CompassDialog;
import com.ywl01.jlinfo.views.SelectLevelDialog;
import com.ywl01.jlinfo.views.SelectSymbolDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

public class AddGraphicActivity extends BaseActivity {

    @BindView(R.id.et_name)
    EditText etName;
    //---------mark----------------
    @BindView(R.id.mark_container)
    LinearLayout markContainer;

    @BindView(R.id.et_registe_name)
    EditText etRegistName;

    @BindView(R.id.et_telephone)
    EditText etTelephone;

    @BindView(R.id.et_type)
    EditText etType;

    @BindView(R.id.et_symbol)
    EditText etSymbol;

    @BindView(R.id.et_display_level)
    EditText etDisplayLevel;
    //----------building--------------
    @BindView(R.id.building_container)
    LinearLayout buildingContainer;

    @BindView(R.id.et_building_name)
    EditText etBuildingName;

    @BindView(R.id.et_building_community)
    EditText etBuildingCommunity;

    @BindView(R.id.et_count_floor)
    EditText etCountFloor;

    @BindView(R.id.rg_sort_type)
    RadioGroup rgSortType;

    @BindView(R.id.et_count_unit)
    EditText etCountUnit;

    @BindView(R.id.et_count_unit_homes)
    EditText etCountUnitHomes;

    @BindView(R.id.et_building_angle)
    EditText etBuildingAngle;
    //----------house------------------
    @BindView(R.id.house_container)
    LinearLayout houseContainer;

    @BindView(R.id.et_community)
    EditText etCommunity;

    @BindView(R.id.et_room_number)
    EditText etRoomNumber;

    @BindView(R.id.et_house_angle)
    EditText etHouseAngle;

    //----------公共变量-------------------
    private int graphicFlag;
    private double x;
    private double y;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_graphic);
        ButterKnife.bind(this);

        graphicFlag = (int) CommVar.getInstance().get("graphicFlag");
        Point mapPoint= (Point) CommVar.getInstance().get("mapPoint");
        x = mapPoint.getX();
        y = mapPoint.getY();

        switch (graphicFlag) {
            case GraphicFlag.MARK:
                markContainer.setVisibility(View.VISIBLE);
                break;
            case GraphicFlag.BUILDING:
                buildingContainer.setVisibility(View.VISIBLE);
                break;
            case GraphicFlag.HOUSE:
                houseContainer.setVisibility(View.VISIBLE);
                break;
        }

    }

    private String getTableNameByGraphicFlag(int graphicFlag) {
        if (graphicFlag == GraphicFlag.MARK) {
            return TableName.MARK;
        } else if (graphicFlag == GraphicFlag.HOUSE) {
            return TableName.HOUSE;
        } else if (graphicFlag == GraphicFlag.BUILDING) {
            return TableName.BUILDING;
        }
        return null;
    }

    @OnClick(R.id.et_symbol)
    public void onSelectSymbol() {
        System.out.println("select symbol");
        SelectSymbolDialog dialog = new SelectSymbolDialog(this);
        dialog.show();
    }

    @OnClick(R.id.et_display_level)
    public void onSelectLevel() {
        SelectLevelDialog dialog = new SelectLevelDialog(this);
        dialog.show();
    }

    @OnClick({R.id.et_building_angle, R.id.et_house_angle})
    public void onSelectAngle() {
        CompassDialog dialog = new CompassDialog(this);
        float initAngle;
        if (graphicFlag == GraphicFlag.BUILDING)
            initAngle = AppUtils.isEmptyString(etBuildingAngle.getText().toString()) ? 0 : Float.parseFloat(etBuildingAngle.getText().toString());
        else
            initAngle = AppUtils.isEmptyString(etHouseAngle.getText().toString()) ? 0 : Float.parseFloat(etHouseAngle.getText().toString());
        dialog.setInitAngle(initAngle);
        dialog.show();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmit() {
        Map<String, String> data = new HashMap<>();
        data.put("name", etName.getText().toString().trim());
        data.put("updateUser", CommVar.UserID + "");
        data.put("x", x+"");
        data.put("y",y+"");
        int id = 0;
        if (graphicFlag == GraphicFlag.MARK) {
            data.put("registerName", etRegistName.getText().toString().trim());
            data.put("telephone", etTelephone.getText().toString().trim());
            data.put("category", etType.getText().toString().trim());
            data.put("displayLevel", etDisplayLevel.getText().toString().trim());
            data.put("symbol", etSymbol.getText().toString().trim());
        } else if (graphicFlag == GraphicFlag.BUILDING) {
            data.put("buildingName", etBuildingName.getText().toString().trim());
            data.put("community", etBuildingCommunity.getText().toString().trim());
            data.put("countFloor", etCountFloor.getText().toString().trim());
            data.put("sortType", rgSortType.getCheckedRadioButtonId()==R.id.rb_lianxu ? "a" : "b");
            data.put("countUnit", etCountUnit.getText().toString().trim());
            data.put("countHomesInUnit", etCountUnitHomes.getText().toString().trim());
            data.put("angle", etBuildingAngle.getText().toString().trim());
        } else if (graphicFlag == GraphicFlag.HOUSE) {
            data.put("community", etCommunity.getText().toString().trim());
            data.put("roomNumber", etRoomNumber.getText().toString().trim());
            data.put("angle", etHouseAngle.getText().toString().trim());
        }

        IntObserver observer = new IntObserver();
        String sql = SqlFactory.insert(getTableNameByGraphicFlag(graphicFlag), data);
        HttpMethods.getInstance().getSqlResult(observer, SqlAction.INSERT,sql);
        observer.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int rows = (int) data;
                if (rows > 0) {
                    AppUtils.showToast("添加成功");
                    finish();
                    TypeEvent event = null;
                    if (graphicFlag == GraphicFlag.MARK) {
                        event = new TypeEvent(TypeEvent.REFRESH_MARKERS);
                    } else if (graphicFlag == GraphicFlag.BUILDING) {
                        event = new TypeEvent(TypeEvent.REFRESH_BUILDINGS);
                    } else if (graphicFlag == GraphicFlag.HOUSE) {
                        event = new TypeEvent(TypeEvent.REFRESH_HOUSE);
                    }
                    event.dispatch();
                }
            }
        });
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel() {
        finish();
    }

    @Subscribe
    public void onSelectComplete(SelectValueEvent event) {
        if (event.type == SelectValueEvent.SELECT_SYMBOL) {
            etSymbol.setText((String) event.selectValue);
        } else if (event.type == SelectValueEvent.SELECT_MAP_LEVEL) {
            etDisplayLevel.setText(event.selectValue.toString());
        } else if (event.type == SelectValueEvent.SELECT_ANGLE) {
            if (graphicFlag == GraphicFlag.BUILDING) {
                etBuildingAngle.setText(event.selectValue.toString());
            } else {
                etHouseAngle.setText(event.selectValue.toString());
            }
        }
    }

    @Override
    protected void onStart() {
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
}
