package com.ywl01.jlinfo.views;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.AddPeopleActivity;
import com.ywl01.jlinfo.activities.BaseActivity;
import com.ywl01.jlinfo.activities.EditGraphicActivity;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.consts.ImageType;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.DialogUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

public class GraphicMenuView extends LinearLayout {

    private Context context;
    private Graphic graphic;

    @BindView(R.id.btn_add_people)
    LinearLayout btnAddPeople;

    @BindView(R.id.btn_upload_image)
    LinearLayout btnUploadImage;

    @BindView(R.id.btn_edit)
    LinearLayout btnEdit;

    @BindView(R.id.btn_move)
    LinearLayout btnMove;

    @BindView(R.id.btn_del)
    LinearLayout btnDelete;


    public GraphicMenuView(Context context) {
        super(context);
        initView();
    }


    private void initView() {
        View view = View.inflate(AppUtils.getContext(), R.layout.view_graphic_menu, this);
        ButterKnife.bind(this, view);
        setBackground(AppUtils.getResDrawable(R.color.light_gray));
        setPadding(0, AppUtils.dip2px(5), 0, AppUtils.dip2px(5));
    }

    public void setData(Graphic graphic) {
        this.graphic = graphic;
        Map<String, Object> attributes = graphic.getAttributes();
        int graphicFlag = (int) attributes.get("graphicFlag");
        if (graphicFlag == GraphicFlag.BUILDING || graphicFlag == GraphicFlag.HOUSE) {
            btnDelete.setVisibility(GONE);
            btnUploadImage.setVisibility(GONE);
        } else {
            btnDelete.setVisibility(VISIBLE);
            btnUploadImage.setVisibility(VISIBLE);
        }
    }

    @OnClick(R.id.btn_add_people)
    public void onAddPeople() {
        CommVar.getInstance().clear();
        CommVar.getInstance().put("graphic", graphic);
        AppUtils.startActivity(AddPeopleActivity.class);
    }

    @OnClick(R.id.btn_edit)
    public void onEdit() {
        CommVar.getInstance().clear();
        CommVar.getInstance().put("graphic", graphic);
        AppUtils.startActivity(EditGraphicActivity.class);
    }

    @OnClick(R.id.btn_upload_image)
    public void onUploadImage() {
        Map<String, Object> data = new HashMap<>();
        data.put("imageDir", CommVar.serverImageDir);
        data.put("id", graphic.getAttributes().get("id"));
        data.put("imageType", ImageType.images);

        UploadImageMenuDialog dialog = new UploadImageMenuDialog(BaseActivity.currentActivity, R.style.dialog);
        dialog.data = data;
        dialog.show();
    }

    @OnClick(R.id.btn_move)
    public void onMove() {
        TypeEvent.dispatch(TypeEvent.MOVE_GRAPHIC);
    }

    @OnClick(R.id.btn_del)
    public void onDelete() {
        DialogUtils.showAlert(BaseActivity.currentActivity, "删除提示", "确定要删除吗？", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmDel();
                    }
                }, "取消", null);
    }

    private void confirmDel() {
        IntObserver delObserver = new IntObserver();
        final String tableName = getTableNameByGraphic(graphic);
        Map<String, String> data = new HashMap<>();
        data.put("isDelete", "1");
        PhpFunction.update(delObserver,tableName, data, (Integer) graphic.getAttributes().get("id"));
        delObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int rows = (int) data;
                if (rows > 0) {
                    AppUtils.showToast("删除成功");
                    if (TableName.MARK.equals(tableName)) {
                        TypeEvent.dispatch(TypeEvent.REFRESH_MARKERS);
                    } else if (TableName.BUILDING.equals(tableName)) {
                        TypeEvent.dispatch(TypeEvent.REFRESH_BUILDINGS);
                    } else if (TableName.HOUSE.equals(tableName)) {
                        TypeEvent.dispatch(TypeEvent.REFRESH_HOUSE);
                    }
                }
            }
        });
    }

    private String getTableNameByGraphic(Graphic graphic) {
        int graphicFlag = (int) graphic.getAttributes().get("graphicFlag");
        if (graphicFlag == GraphicFlag.MARK) {
            return TableName.MARK;
        } else if (graphicFlag == GraphicFlag.HOUSE) {
            return TableName.HOUSE;
        } else if (graphicFlag == GraphicFlag.BUILDING) {
            return TableName.BUILDING;
        }
        return null;
    }
}
