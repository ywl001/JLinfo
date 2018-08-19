package com.ywl01.jlinfo.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.ImageActivity;
import com.ywl01.jlinfo.beans.ImageBean;
import com.ywl01.jlinfo.beans.MarkBean;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.beans.User;
import com.ywl01.jlinfo.CommVar;
import com.ywl01.jlinfo.consts.ImageType;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.PhpFunction;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.MarkImageObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.observers.UserObserver;
import com.ywl01.jlinfo.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;

/**
 * Created by ywl01 on 2017/3/12.
 * 监控信息显示的view
 */

public class MarkInfoView extends FrameLayout implements View.OnClickListener {
    private Context context;
    private MarkBean markBean;


    private String delImageUrl;
    private String delThumbUrl;


    private ArrayList<ImageBean> markImages;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_type)
    TextView tvType;

    @BindView(R.id.tv_update_info)
    TextView TvUpdateInfo;

    @BindView(R.id.tv_manager)
    TextView tvManager;

    @BindView(R.id.tv_telephone)
    TextView tvTelephone;

    @BindView(R.id.scroller_view)
    HorizontalScrollView scrollView;

    @BindView(R.id.image_group)
    LinearLayout imageGroup;


    public MarkInfoView(Context context) {
        super(context);
        this.context = context;
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        View view = View.inflate(AppUtils.getContext(), R.layout.view_mark_info, this);
        ButterKnife.bind(this, view);
    }

    public void setData(MarkBean data) {
        markBean = data;
        refreshUI(markBean);
    }

    private void refreshUI(MarkBean markBean) {
        tvName.setText(markBean.name);
        setText(tvType, markBean.category);
        setText(tvTelephone, markBean.telephone);
        imageGroup.removeAllViews();
        getManager();
        getMarkImage();
        getUpdateInfo();
    }

    private void setText(TextView tv, String text) {
        if (!AppUtils.isEmptyString(text)) {
            tv.setVisibility(VISIBLE);
            tv.setText(text);
        } else {
            tv.setVisibility(GONE);
        }
    }

    private void getUpdateInfo() {
        UserObserver userObserver = new UserObserver();
        Map<String, Object> tableData = new HashMap<>();
        tableData.put("id",markBean.updateUser);
        HttpMethods.getInstance().getSqlResult(userObserver, PhpFunction.SELECT_USER_BY_ID, tableData);
        userObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                User updateUser = (User) data;
                if (updateUser != null) {
                    setText(TvUpdateInfo, updateUser.realName + markBean.updateTime.substring(0, 10) + "更新");
                } else {
                    TvUpdateInfo.setVisibility(GONE);
                }
            }
        });
    }

    private void getManager() {
        PeopleObserver managerObersver = new PeopleObserver(PeopleFlag.FROM_MARK);
        Map<String, Object> tableData = new HashMap<>();
        tableData.put("id", markBean.id);
        HttpMethods.getInstance().getSqlResult(managerObersver, PhpFunction.SELECT_MARK_MANAGER, tableData);
        managerObersver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                List<PeopleBean> peoples = (List<PeopleBean>) data;
                if (peoples.size() > 0) {
                    PeopleBean people = peoples.get(0);
                    setText(tvManager, people.name);
                    if (!AppUtils.isEmptyString(people.telephone)) {
                        setText(tvTelephone, people.telephone);
                    }
                } else {
                    tvManager.setVisibility(GONE);
                }
            }
        });
    }

    //获取图像
    private void getMarkImage() {
        MarkImageObserver imageObserver = new MarkImageObserver();
        Map<String, Object> tableData = new HashMap<>();
        tableData.put("id", markBean.id);
        HttpMethods.getInstance().getSqlResult(imageObserver, PhpFunction.SELECT_MARK_IMAGES, tableData);
        imageObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                markImages = (ArrayList<ImageBean>) data;
                setMarkImages(markImages);
            }
        });
    }

    private void setMarkImages(List<ImageBean> markImages) {
        if (markImages != null && markImages.size() > 0) {
            scrollView.setVisibility(View.VISIBLE);
            imageGroup.removeAllViews();
            for (int i = 0; i < markImages.size(); i++) {
                String thumbUrl = markImages.get(i).thumbUrl;
                ImageView imageView = new ImageView(AppUtils.getContext());
                imageView.setOnClickListener(this);
                imageView.setTag(markImages.get(i));
                ImageLoader.getInstance().displayImage(CommVar.serverImageRootUrl + thumbUrl, imageView);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(AppUtils.dip2px(160), AppUtils.dip2px(120));
                imageView.setLayoutParams(params);
                if (i != markImages.size() - 1)
                    params.setMargins(0, 0, 10, 0);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);

                imageGroup.addView(imageView);
            }
        } else {
            scrollView.setVisibility(View.GONE);
        }
    }

    //点击图片后显示放大图像
    @Override
    public void onClick(View view) {
        if (view instanceof ImageView) {

            System.out.println("show image");
            ImageBean MarkImageBean = (ImageBean) view.getTag();
            int position = markImages.indexOf(MarkImageBean);
            boolean isShowDelBtn = false;

            CommVar.getInstance().clear();
            CommVar.getInstance().put("isShowDelBtn", isShowDelBtn);
            CommVar.getInstance().put("images", markImages);
            CommVar.getInstance().put("position", position);
            CommVar.getInstance().put("imageType", ImageType.images);

            AppUtils.startActivity(ImageActivity.class);
        }
    }

    @Subscribe
    public void refreshImages(TypeEvent event) {
        if (event.type == TypeEvent.REFRESH_IMAGE) {
            imageGroup.removeAllViews();
            getMarkImage();
        }
    }
}
