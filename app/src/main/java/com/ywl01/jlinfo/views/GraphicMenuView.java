package com.ywl01.jlinfo.views;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.AddPeopleActivity;
import com.ywl01.jlinfo.activities.BaseActivity;
import com.ywl01.jlinfo.activities.EditGraphicActivity;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.consts.ImageType;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        data.put("id", graphic.getAttributes().get("id") + "");
        data.put("imageType", ImageType.images + "");

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

    }
}
