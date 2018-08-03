package com.ywl01.jlinfo.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.BackgroundGrid;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.ShowGraphicMenuEvent;
import com.ywl01.jlinfo.events.ShowMarkInfoEvent;
import com.ywl01.jlinfo.events.ShowPositionEvent;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.events.UploadImageEvent;
import com.ywl01.jlinfo.map.MapListener;
import com.ywl01.jlinfo.map.SymbolManager;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.ProgressRequestBody;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.InsertObserver;
import com.ywl01.jlinfo.observers.UploadObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.ImageUtils;
import com.ywl01.jlinfo.utils.PhotoUtils;
import com.ywl01.jlinfo.views.GraphicMenuView;
import com.ywl01.jlinfo.views.MarkInfoView;
import com.ywl01.jlinfo.views.SearchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends BaseActivity {

    @BindView(R.id.map_view)
    MapView mapView;

    @BindView(R.id.btn_search)
    Button btnSearch;

    @BindView(R.id.btn_container)
    LinearLayout btnContainer;

    @BindView(R.id.searchView)
    SearchView searchView;

    @BindView(R.id.bottom_container)
    FrameLayout bottomContainer;

    private MarkInfoView markInfoView;
    private GraphicMenuView graphicMenuView;
    private UploadImageEvent uploadImageEvent;

    private ArcGISTiledLayer tiledLayer;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        getMetrics();
        initMap();
    }

    //获取手机各种尺寸
    private void getMetrics() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = 0;
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        CommVar.screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        TypedValue tv = new TypedValue();
        int titleBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            titleBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        CommVar.appHeight = screenHeight - statusBarHeight - titleBarHeight;
    }

    /**
     * 初始化地图
     */
    private void initMap() {
        //设置license
        ArcGISRuntimeEnvironment.setLicense(CommVar.license);
        //设置隐藏logo
        mapView.setAttributionTextVisible(false);
        //隐藏背景网格
        mapView.setBackgroundGrid(new BackgroundGrid(Color.WHITE, Color.WHITE, 0, 10));

        //添加切片层
        final ArcGISMap map = new ArcGISMap();
        map.setMaxScale(250);
        map.setMinScale(128000);
        tiledLayer = new ArcGISTiledLayer(CommVar.vecMapUrl);
        map.getBasemap().getBaseLayers().add(tiledLayer);

        mapView.setMap(map);
        mapView.setOnTouchListener(new MapListener(MainActivity.this, mapView));
    }

    ///////////////////////////////////////////////////////////////////////////
    // 点击事件的处理
    ///////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.btn_search)
    public void onSearch() {
        btnContainer.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_zoom_in)
    public void onZoomIn() {
        double scale = mapView.getMapScale();
        mapView.setViewpointScaleAsync(scale / 2);
    }

    @OnClick(R.id.btn_zoom_out)
    public void onZoomOut() {
        double scale = mapView.getMapScale();
        mapView.setViewpointScaleAsync(scale * 2);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Eventbus事件处理
    ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void showBtnContainer(TypeEvent event) {
        if (event.type == TypeEvent.SHOW_BTN_CONTAINER)
            btnContainer.setVisibility(View.VISIBLE);
        else if (event.type == TypeEvent.CLEAR_BOTTOM_CONTAINER) {
            clearBottomContainer();
        }
    }

    @Subscribe
    public void showMarkInfo(ShowMarkInfoEvent event) {
        if (markInfoView == null)
            markInfoView = new MarkInfoView(this);
        markInfoView.setData(event.markBean);

        bottomContainer.removeAllViews();
        bottomContainer.addView(markInfoView);

        setAnmation(bottomContainer,markInfoView.getHeight(),0);
    }

    @Subscribe
    public void showGraphicMenu(ShowGraphicMenuEvent event) {
        if(graphicMenuView == null)
            graphicMenuView = new GraphicMenuView(this);
        graphicMenuView.setData(event.graphic);
        bottomContainer.removeAllViews();
        bottomContainer.addView(graphicMenuView);

        setAnmation(bottomContainer, graphicMenuView.getHeight(),0);
    }


    private void clearBottomContainer() {
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, bottomContainer.getHeight());
        animation.setDuration(500);
        bottomContainer.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bottomContainer.removeAllViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void setAnmation(View target,float fromY,float toY) {
        TranslateAnimation animation = new TranslateAnimation(0, 0, fromY, toY);
        animation.setDuration(500);
        target.startAnimation(animation);
    }

    @Subscribe
    public void uploadImage(UploadImageEvent event) {
        uploadImageEvent = event;
        if (event.type == UploadImageEvent.SELECT_IMAGE_FOR_MARK)
            PhotoUtils.selectImage(this);
        else if (event.type == UploadImageEvent.TAKE_IMAGE_FOR_MARK)
            PhotoUtils.takeImage(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 系统方法复写
    ///////////////////////////////////////////////////////////////////////////
    //系统返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PhotoUtils.SELECT_PHOTO) {
                // 被选中文件的Uri
                Uri uri = data.getData();
                System.out.println(uri.getPath());
                File file = new File(AppUtils.getPathByUri(uri));

                Bitmap bitmap = ImageUtils.getScaleBitmap(file.getPath());
                File tempFile = ImageUtils.saveBitmap(bitmap, "temp");
                uploadFile(tempFile);
            } else if (requestCode == PhotoUtils.TAKE_PHOTO) {
                Bitmap bm = ImageUtils.getScaleBitmap(PhotoUtils.tempFile.getPath());
                File tempFile = ImageUtils.saveBitmap(bm, "temp");
                uploadFile(tempFile);
            }
        }
    }

    private void uploadFile(File file) {
        System.out.println(file.length());
        //传递服务器端存储图片文件的目录
        String server_image_dir = uploadImageEvent.IMAGE_DIR;
        RequestBody fileDir = RequestBody.create(MultipartBody.FORM, server_image_dir);
        //上传文件的包装Filedata为php服务器端_FILE[fileData],这个名字和服务器的要一致，区分大小写！
        ProgressRequestBody requestFile = new ProgressRequestBody(file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("fileData", file.getName(), requestFile);

        UploadObserver uploadObserver = new UploadObserver();

        HttpMethods.getInstance().uploadImage(uploadObserver, fileDir, body);
        uploadObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                String returnData = (String) data;
                String imgUrl = returnData.substring(12);
                String[] temp = imgUrl.split("\\.");
                String thumbUrl = temp[0] + "_thumb.jpg";

                InsertObserver insertObserver = new InsertObserver();
                Map<String, String> tableData = new HashMap<String, String>();
                int id = uploadImageEvent.id;

                tableData.put("markID", id+"");
                tableData.put("imageUrl", imgUrl);
                tableData.put("thumbUrl", thumbUrl);
                tableData.put("insertUser", CommVar.UserID + "");
                tableData.put("insertTime", "now()");
                String sql = SqlFactory.insert(TableName.MARK_IMAGE, tableData);
                HttpMethods.getInstance().getSqlResult(insertObserver, SqlAction.INSERT, sql);

                insertObserver.setOnNextListener(new BaseObserver.OnNextListener() {
                    @Override
                    public void onNext(Observer observer, Object data) {
                        Long returnData = (Long) data;
                        if (returnData > 0) {
                            AppUtils.showToast("上传图片成功");
                            //派发事件，让cameraInfoView刷新图片
                            TypeEvent.dispatch(TypeEvent.REFRESH_IMAGE);
                        }
                    }
                });
            }
        });
    }

    private boolean isPressBack;
    private int clickBackTimes;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.out.println("back");
            isPressBack = true;
            clickBackTimes++;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        if (isPressBack) {
            if (activities.size() > 1) {
                for (int i = 0; i < activities.size(); i++) {
                    BaseActivity activity = (BaseActivity) activities.get(i);
                    if (activity instanceof PeoplesActivity) {
                        AppUtils.moveActivityToFront(activity.getClass());
                        System.out.println("activitys size:" + activities.size());
                    }
                }
                clickBackTimes = 0;
            } else if (clickBackTimes == 1) {
                AppUtils.showToast("再按一次退出");
            }else{
                super.finish();
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
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.dispose();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
