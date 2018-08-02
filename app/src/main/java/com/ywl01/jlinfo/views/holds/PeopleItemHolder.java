package com.ywl01.jlinfo.views.holds;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.mapping.view.Graphic;
import com.squareup.picasso.Picasso;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.BaseActivity;
import com.ywl01.jlinfo.activities.EditPeopleActivity;
import com.ywl01.jlinfo.activities.ImageActivity;
import com.ywl01.jlinfo.activities.MainActivity;
import com.ywl01.jlinfo.activities.PeoplesActivity;
import com.ywl01.jlinfo.beans.ImageBean;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.ImageType;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.ListEvent;
import com.ywl01.jlinfo.events.ShowPositionEvent;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.observers.PeoplePhotoObserver;
import com.ywl01.jlinfo.observers.PositionObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.DialogUtils;
import com.ywl01.jlinfo.utils.StringUtils;
import com.ywl01.jlinfo.views.SwipeItem;
import com.ywl01.jlinfo.views.UploadImageMenuDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;

/**
 * Created by ywl01 on 2017/1/23.
 */

public class PeopleItemHolder extends BaseRecyclerHolder<PeopleBean> {

    @BindView(R.id.image_group)
    LinearLayout imageGroup;//存放照片的容器，继承自linearLayout

    @BindView(R.id.tv_name)
    TextView tvName;//人员姓名

    @BindView(R.id.tv_sex)
    TextView tvSex;//人员性别

    @BindView(R.id.tv_telephone)
    TextView tvTelephone;//人员电话号码

    @BindView(R.id.tv_people_number)
    TextView tvPeopleNumber;//人员身份证号

    @BindView(R.id.tv_address)
    TextView tvAddress;//人员住址

    @BindView(R.id.tv_workplace)
    TextView tvWorkPlace;//人员工作单位

    @BindView(R.id.tv_update_info)
    TextView tvInsertTime;//插入时间

    @BindView(R.id.tv_home)
    TextView tvHome;//与户主关系

    @BindView(R.id.btn_edit_people)
    Button btnEditPeople;

    @BindView(R.id.btn_upload_photo)
    Button btnUploadPhoto;

    @BindView(R.id.btn_create_relation)
    Button btnCreateRelation;

    @BindView(R.id.btn_break_realtion)
    Button btnBreakRelation;

    @BindView(R.id.btn_del_people)
    Button btnDelPeople;

    @BindView(R.id.btn_set_home)
    Button btnSetPeopleHome;


    //操作的各种观察者
    //private PositionObserver positionObserver;//显示位置观察者
    // private FamilyDataObserver familyDataObserver;//亲戚数据观察者
    private PeopleObserver familyObserver;//显示亲戚信息观察者
    private IntObserver delPhotoOberver;//删除照片观察者
    private IntObserver delParentObserver;

    private int peopleFlag;
    private ImageBean photo;

    private Bundle bundleArgs;
    private SwipeItem rootView;
    private Dialog delPhotoDialog;
    private Dialog delParentDialog;
    private Dialog delPeopleDialog;
    private IntObserver setPeopleLeaveObserver;
    private IntObserver delPeopleObserver;

    public PeopleItemHolder(View itemView) {
        super(itemView);
        rootView = (SwipeItem) itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("root view click");
            }
        });
        ButterKnife.bind(this, itemView);
        EventBus.getDefault().register(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 根据数据显示ui
    ///////////////////////////////////////////////////////////////////////////
    @Override
    protected void refreshUI(PeopleBean data) {
        peopleFlag = data.peopleFlag;

        setMenuButton();
        setPeopleUIValue();
        setBackground();

        getPeoplePhoto(data);
    }

    private void setMenuButton() {
        if (CommVar.isEdit) {
            if (peopleFlag == PeopleFlag.FROM_BUILDING || peopleFlag == PeopleFlag.FROM_MARK || peopleFlag == PeopleFlag.FROM_HOUSE) {
                btnDelPeople.setVisibility(View.VISIBLE);
            } else {
                btnDelPeople.setVisibility(View.GONE);
            }
        } else {
            btnEditPeople.setVisibility(View.GONE);
            btnSetPeopleHome.setVisibility(View.GONE);
            btnBreakRelation.setVisibility(View.GONE);
            btnCreateRelation.setVisibility(View.GONE);
            btnUploadPhoto.setVisibility(View.GONE);
            btnDelPeople.setVisibility(View.GONE);
        }
    }

    private void setPeopleUIValue() {
        setTextViewValue(tvName, data.name);
        setTextViewValue(tvSex, data.sex);
        setTextViewValue(tvTelephone, data.telephone);
        setTextViewValue(tvPeopleNumber, data.peopleNumber);
        setTextViewValue(tvWorkPlace, getString(data.workPlace) + getString(data.department) + getString(data.job));
        setTextViewValue(tvHome, data.relation);

        String address = "";

        if (peopleFlag == PeopleFlag.FROM_BUILDING) {
            address = getString(data.buildingName) + getString(data.roomNumber);
        } else if (peopleFlag == PeopleFlag.FROM_HOUSE) {
            address = getString(data.community) + getString(data.roomNumber);
        }

        setTextViewValue(tvAddress, address);
    }

    private void setTextViewValue(TextView tv, String txt) {
        if (!StringUtils.isEmpty(txt)) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(txt);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    private void setBackground() {
        if (data.isLeave == 1) {
            setUIAlpha(rootView, 0.3f);
        } else {
            setUIAlpha(rootView, 1);
        }
    }

    private String getString(String string) {
        if (string == null || string == "null") {
            return "";
        }
        return string;
    }

    private void setUIAlpha(View view, float alpha) {
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View v = vg.getChildAt(i);
                if (v instanceof ViewGroup) {
                    setUIAlpha(v, alpha);
                } else {
                    v.setAlpha(alpha);
                }
            }
        } else {
            view.setAlpha(alpha);
        }
    }

    //获取人员照片
    private void getPeoplePhoto(@NonNull PeopleBean people) {
        imageGroup.removeAllViews();
        PeoplePhotoObserver photoObserver = new PeoplePhotoObserver();
        HttpMethods.getInstance().getSqlResult(photoObserver, SqlAction.SELECT, SqlFactory.selectPhotosByPeopleID(people.id));
        photoObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                List<ImageBean> photos = (List<ImageBean>) data;
                setPeoplePhoto(photos);
            }
        });
    }

    private void setPeoplePhoto(final List<ImageBean> photos) {
        if (photos != null && photos.size() > 0) {
            for (int i = 0; i < photos.size(); i++) {
                ImageView img = new ImageView(AppUtils.getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, AppUtils.dip2px(118));
                img.setLayoutParams(params);
                if (i != photos.size() - 1)
                    params.setMargins(0, 1, 10, 1);
                img.setAdjustViewBounds(true);
                img.setScaleType(ImageView.ScaleType.FIT_XY);
                img.setTag(photos.get(i));
                if (data.isLeave == 1) {
                    img.setAlpha(0.3f);
                } else {
                    img.setAlpha(1.0f);
                }
                String thumburl = CommVar.serverImageRootUrl + photos.get(i).thumbUrl;
                Picasso.with(AppUtils.getContext()).load(thumburl).into(img);
                imageGroup.addView(img);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("show photo");
                        ImageBean imageBean = (ImageBean) view.getTag();
                        int position = photos.indexOf(imageBean);
                        boolean isShowDelBtn = false;

                        CommVar.getInstance().clear();
                        CommVar.getInstance().put("isShowDelBtn", isShowDelBtn);
                        CommVar.getInstance().put("images", photos);
                        CommVar.getInstance().put("position", position);
                        CommVar.getInstance().put("imageType",ImageType.phpto);

                        AppUtils.startActivity(ImageActivity.class);
                    }
                });
            }
        } else {
            ImageView img = new ImageView(AppUtils.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AppUtils.dip2px(90), AppUtils.dip2px(120));
            img.setLayoutParams(params);
            Picasso.with(AppUtils.getContext()).load(R.drawable.img_no_photo).into(img);
            imageGroup.addView(img);
        }
    }

    private void getUpdateInfo() {

    }

    ///////////////////////////////////////////////////////////////////////////
    // 点击人员按钮的操作
    ///////////////////////////////////////////////////////////////////////////
    //显示人员住址
    @OnClick(R.id.btn_show_address)
    public void onShowAddress() {
        String sql = SqlFactory.selectAddressByPeopleID(data.id);
        PositionObserver positionObserver = new PositionObserver();
        HttpMethods.getInstance().getSqlResult(positionObserver, SqlAction.SELECT, sql);
        positionObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                List<Graphic> positions = (List<Graphic>) data;
                if (positions != null && positions.size() > 0) {
                    AppUtils.moveActivityToFront(MainActivity.class);
                    ShowPositionEvent event = new ShowPositionEvent(ShowPositionEvent.SHOW_ADDRESS);
                    event.positions = positions;
                    event.dispatch();
                } else {
                    AppUtils.showToast("人员无住址信息");
                }
            }
        });
    }

    //显示人员工作单位
    @OnClick(R.id.btn_show_work_place)
    public void onShowWorkPlace() {

    }

    //显示人员同户人员
    @OnClick(R.id.btn_show_home)
    public void onShowHomePeoples() {
        rootView.close2();

        String sql = SqlFactory.selectHomePeopleByPid(data.id);
        PeopleObserver homePeopleObserver = new PeopleObserver(PeopleFlag.FROM_HOME);
        HttpMethods.getInstance().getSqlResult(homePeopleObserver, SqlAction.SELECT, sql);
        homePeopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                ArrayList<PeopleBean> homePeoples = (ArrayList<PeopleBean>) data;
                CommVar.getInstance().clear();
                CommVar.getInstance().put("peoples", homePeoples);
                AppUtils.startActivity(PeoplesActivity.class);
            }
        });
    }

    //显示人员亲戚信息
    @OnClick(R.id.btn_show_family)
    public void onShowFamilies() {
//        String sql = SqlFactory.selectAllHomePeopleByPid(data.id);
//        familyObserver = new PeopleObserver(PeopleFlag.FROM_HOME);
//        HttpMethods.getInstance().getResult(familyObserver, SqlAction.SELECT, sql);
//        familyObserver.setOnNextListener(this);
    }

    //编辑人员信息
    @Nullable
    @OnClick(R.id.btn_edit_people)
    public void onEditPeople() {
        CommVar.getInstance().clear();
        CommVar.getInstance().put("people",data);
        AppUtils.startActivity(EditPeopleActivity.class);
    }

    //上传人员照片
    @Nullable
    @OnClick(R.id.btn_upload_photo)
    public void onUploadPhoto() {
        Map<String, Object> menuData = new HashMap<>();
        menuData.put("imageDir", CommVar.serverPhotoDir);
        menuData.put("id", data.id );
        menuData.put("imageType", ImageType.phpto);

        UploadImageMenuDialog dialog = new UploadImageMenuDialog(BaseActivity.currentActivity);
        dialog.data = menuData;
        dialog.show();

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onUploadComplete(TypeEvent e) {
//        if (EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
//        ListEvent event = new ListEvent();
//        event.action = ListEvent.update;
//        event.position = position;
//        event.dispatch();
    }

    @Nullable
    @OnClick(R.id.btn_set_home)
    public void onSetHome() {
//        IntentUtils.startActivity(SetHomeActivity.class, bundleArgs);
    }

    @Nullable
    @OnClick(R.id.btn_create_relation)
    public void addRelationHome() {
//        IntentUtils.startActivity(RelationHomeActivity.class, bundleArgs);
    }

    @Nullable
    @OnClick(R.id.btn_break_realtion)
    public void breakRelationHome() {
//        delParentDialog = DialogUtils.showAlert(BaseActivity.currentActivity, "删除警告：", "你确定要断开和父级的联系吗？", "确定", this, "取消", this);
    }

//    private void breakParentHomeRelation() {
//        String sql = "delete from people_home where peopleID = '" + data.id + "' and isDelete = 1";
//        delParentObserver = new IntObserver();
//        HttpMethods.getInstance().getResult(delParentObserver, SqlAction.DELETE, sql);
//        delParentObserver.setOnNextListener(this);
//    }

    //删除人员的关联位置
    @Nullable
    @OnClick(R.id.btn_del_people)
    public void onDelpeople() {
        String txtInfo = "确定要从这里删除人员吗？";
        switch (data.peopleFlag) {
            case PeopleFlag.FROM_BUILDING:
                txtInfo = "确认要从" + data.buildingName + data.roomNumber + "室删除" + data.name + "吗？";
                break;
            case PeopleFlag.FROM_HOUSE:
                txtInfo = "确认要从" + data.name + "家删除" + data.name + "吗？";
                break;
            case PeopleFlag.FROM_MARK:
                txtInfo = "确认要从" + data.workPlace + "删除" + data.name + "吗？";
                break;
        }
        delPeopleDialog = DialogUtils.showAlert(BaseActivity.currentActivity,
                "删除警告：", txtInfo,
                "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delPeople();
                    }
                },
                "取消",
                null);
    }

    private void delPeople() {
        String tableName = null;
        int tableID = 0;
        if (data.peopleFlag == PeopleFlag.FROM_BUILDING) {
            tableName = TableName.PEOPLE_BUILDING;
            tableID = data.pbID;
        } else if (data.peopleFlag == PeopleFlag.FROM_HOUSE) {
            tableName = TableName.PEOPLE_HOUSE;
            tableID = data.phuID;
        } else if (data.peopleFlag == PeopleFlag.FROM_MARK) {
            tableName = TableName.PEOPLE_MARK;
            tableID = data.pmID;
        }
        if (data.isLeave == 0) {
            Map<String, String> map = new HashMap<>();
            map.put("isDelete", "1");

            String sql = SqlFactory.update(tableName, map, tableID);
            setPeopleLeaveObserver = new IntObserver();
            setPeopleLeaveObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                @Override
                public void onNext(Observer observer, Object data) {
                    int rows = (int) data;
                    if (rows > 0) {
                        AppUtils.showToast("删除人员成功。");
                        ListEvent event = new ListEvent(ListEvent.remove,position);
                        event.dispatch();
                    }
                }
            });
            HttpMethods.getInstance().getSqlResult(setPeopleLeaveObserver, SqlAction.UPDATE, sql);
        } else if (data.isLeave == 1) {
            delPeopleObserver = new IntObserver();
            String sql = SqlFactory.delete(tableName, tableID);
            delPeopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                @Override
                public void onNext(Observer observer, Object data) {
                    int rows = (int) data;
                    if (rows > 0) {
                        AppUtils.showToast("删除人员成功。");
                        ListEvent event = new ListEvent(ListEvent.remove,position);
                        event.dispatch();
                    }
                }
            });
            HttpMethods.getInstance().getSqlResult(delPeopleObserver, SqlAction.DELETE, sql);
        }
    }


// else if (observer == positionObserver) {
//            List<Graphic> positions = (List<Graphic>) data;
//            if (positions != null && positions.size() > 0) {
//                IntentUtils.moveActivityToFront(MapActivity.class);
//                ShowPositionEvent event = new ShowPositionEvent(ShowPositionEvent.SHOW_ADDRESS);
//                event.positions = positions;
//                event.dispatch();
//            } else {
//                ToastUtils.show("人员无住址信息");
//            }
//        } else if (observer == homePeopleObserver) {
//            ArrayList<PeopleBean> homePeoples = (ArrayList<PeopleBean>) data;
//            Bundle args = new Bundle();
//            args.putParcelableArrayList(KeyName.DATA, homePeoples);
//            IntentUtils.startActivity(PeoplesActivity.class, args);
//
//        } else if (observer == familyObserver) {
//            ArrayList<PeopleBean> peoples = (ArrayList<PeopleBean>) data;
//            if (peoples.size() > 0) {
//                FamilyNode node = new FamilyNode();
//                node.level = 0;
//                node.homeNumber = peoples.get(0).homeNumber;
//                node.peoples = peoples;
//                node.sign = QueryFamilyServices.BASE;
//
//                QueryFamilyServices services = new QueryFamilyServices();
//                familyDataObserver = new FamilyDataObserver();
//                services.setData(node, familyDataObserver);
//                familyDataObserver.setOnNextListener(this);
//            } else {
//                ToastUtils.show("该人员无亲戚信息");
//            }
//        } else if (observer == familyDataObserver) {
//            ArrayList<FamilyNode> familyNodes = (ArrayList<FamilyNode>) data;
//            //此处用类的静态变量传值，用parceable死循环。
//            FamilyActivity.familyNodes = familyNodes;
//            IntentUtils.startActivity(FamilyActivity.class);
//        } else if (observer == delParentObserver) {
//            int rows = (int) data;
//            if (rows > 0) {
//                ToastUtils.show("解除父子关系成功。");
//            }
//        }
//    }

    ///////////////////////////////////////////////////////////////////////////
    // 拖动人员照片的回调,下载或删除
    ///////////////////////////////////////////////////////////////////////////
//    @Override
//    public void onDrag(Object data, int action) {
////        photo = (PeoplePhotoBean) data;
////        if (action == ImageGroup.DOWNLOAD_IMAGE) {
////            ToastUtils.show("下载图像");
////        } else if (action == ImageGroup.DELETE_IMAGE) {
////            delPhotoDialog = DialogUtils.showAlert(BaseActivity.currentActivity,
////                    UIUtils.getResString(R.string.del_title),
////                    UIUtils.getResString(R.string.del_message),
////                    UIUtils.getResString(R.string.btn_ok),
////                    this,
////                    UIUtils.getResString(R.string.btn_cancel),
////                    this);
////        }
//    }

    ///////////////////////////////////////////////////////////////////////////
    // 点击确认对话框的回调
    ///////////////////////////////////////////////////////////////////////////

//    public void onClick(DialogInterface dialog, int which) {
//        if (dialog == delPhotoDialog) {
//            if (which == DialogInterface.BUTTON_POSITIVE) {
//                long id = photo.id;
//                delPhotoOberver = new IntObserver();
//                String sql = SqlFactory.delete(TableName.PEOPLE_PHOTO, id);
//                System.out.println(sql);
//                HttpMethods.getInstance().getSqlResult(delPhotoOberver, SqlAction.DELETE, sql);
//                delPhotoOberver.setOnNextListener(this);
//            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
//                getPeoplePhoto(data);
//            }
//        } else if (dialog == delParentDialog) {
//            if (which == DialogInterface.BUTTON_POSITIVE) {
//                //breakParentHomeRelation();
//            }
//        } else if (dialog == delPeopleDialog) {
//            if (which == DialogInterface.BUTTON_POSITIVE) {
//                delPeople();
//            }
//        }
//    }


    @Subscribe
    public void refreshImages(TypeEvent event) {
        if(event.type == TypeEvent.REFRESH_IMAGE){
            getPeoplePhoto(data);
            System.out.println("刷新了人员照片");
        }

    }
}
