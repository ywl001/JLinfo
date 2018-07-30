package com.ywl01.jlinfo.activities;

import android.content.DialogInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.beans.MarkBean;
import com.ywl01.jlinfo.beans.MarkImageBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.ShowMarkInfoEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.DialogUtils;
import com.ywl01.jlinfo.views.holds.ImagePageViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;


/**
 * Created by ywl01 on 2017/3/14.
 */

public class ImageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private List<MarkImageBean> images;
    private int position;
    private boolean isShowDelBtn;
    private MarkBean markBean;

    private String delImageUrl;
    private String delThumbUrl;

    @BindView(R.id.image_pager)
    ViewPager imagePager;

    @BindView(R.id.btn_del)
    Button btnDel;

    @BindView(R.id.tv_count_page)
    TextView tvCountPage;


    private ImagePageAdapter pagerAdapter;
    private boolean isImageChange = false;


    @Override
    protected void initData() {
        super.initData();
        images = (List<MarkImageBean>) CommVar.getInstance().get("images");
        position = (int) CommVar.getInstance().get("position");
        isShowDelBtn = (boolean) CommVar.getInstance().get("isShowDelBtn");
        markBean = (MarkBean) CommVar.getInstance().get("markBean");
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);

        tvCountPage.setText((position + 1) + "/" + images.size());
        pagerAdapter = new ImagePageAdapter(images);

        imagePager.setAdapter(pagerAdapter);
        imagePager.setCurrentItem(position);
        imagePager.addOnPageChangeListener(this);
    }

    @OnClick(R.id.btn_del)
    public void del() {
        DialogUtils.showAlert(BaseActivity.currentActivity, "删除提示", "确定要删除这张图片吗？", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmDel();
                    }
                }, "取消", null);

    }

    private void confirmDel() {
        IntObserver delImageObserver = new IntObserver();
        MarkImageBean imageBean = images.get(position);
        delImageUrl = imageBean.imageUrl;
        delThumbUrl = imageBean.thumbUrl;
        long id = imageBean.id;
        String sql = SqlFactory.delete(TableName.MARK_IMAGE, id);
        HttpMethods.getInstance().getSqlResult(delImageObserver, SqlAction.DELETE, sql);

        delImageObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int rows = (int) data;
                if (rows > 0) {
                    AppUtils.showToast("删除图片成功");
                    isImageChange = true;
                    images.remove(position);

                    if (images.size() == 0) {
                        finish();
                    } else
                        pagerAdapter.notifyDataSetChanged();

                    //数据库删除后，删除服务器端文件
                    IntObserver delFileObserver = new IntObserver();

                    String filePaths = delImageUrl + " " + delThumbUrl;
                    HttpMethods.getInstance().delFile(delFileObserver, filePaths);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //派发事件，从mainActivity中重新加载markInfoView
        if (isImageChange) {
            System.out.println("图像页面派发了事件");
            ShowMarkInfoEvent e = new ShowMarkInfoEvent();
            e.markBean = markBean;
            e.dispatch();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 实现viewpager的OnPageChangeListener，用以从外部获取position
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        tvCountPage.setText((position + 1) + "/" + images.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    ///////////////////////////////////////////////////////////////////////////
    // adapter的实现
    ///////////////////////////////////////////////////////////////////////////
    class ImagePageAdapter extends PagerAdapter {

        private List<MarkImageBean> imageUrls;

        public ImagePageAdapter(List<MarkImageBean> imageUrls) {
            this.imageUrls = imageUrls;
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImagePageViewHolder imagePageViewHolder = new ImagePageViewHolder();
            View view = imagePageViewHolder.getRootView();
            imagePageViewHolder.setData(imageUrls.get(position));
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }


}
