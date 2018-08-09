package com.ywl01.jlinfo.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedEvent;
import com.esri.arcgisruntime.mapping.view.MapScaleChangedListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.mapping.view.NavigationChangedEvent;
import com.esri.arcgisruntime.mapping.view.NavigationChangedListener;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedEvent;
import com.esri.arcgisruntime.mapping.view.ViewpointChangedListener;
import com.esri.arcgisruntime.symbology.CompositeSymbol;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.Symbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.util.ListenableList;
import com.ywl01.jlinfo.R;
import com.ywl01.jlinfo.activities.BaseActivity;
import com.ywl01.jlinfo.activities.BuildingPlanActivity;
import com.ywl01.jlinfo.activities.PeoplesActivity;
import com.ywl01.jlinfo.beans.BuildingBean;
import com.ywl01.jlinfo.beans.MarkBean;
import com.ywl01.jlinfo.beans.PeopleBean;
import com.ywl01.jlinfo.consts.CommVar;
import com.ywl01.jlinfo.consts.GraphicFlag;
import com.ywl01.jlinfo.consts.PeopleFlag;
import com.ywl01.jlinfo.consts.SqlAction;
import com.ywl01.jlinfo.consts.TableName;
import com.ywl01.jlinfo.events.LocationEvent;
import com.ywl01.jlinfo.events.ShowGraphicMenuEvent;
import com.ywl01.jlinfo.events.ShowMarkInfoEvent;
import com.ywl01.jlinfo.events.ShowGraphicLocationEvent;
import com.ywl01.jlinfo.events.TypeEvent;
import com.ywl01.jlinfo.net.HttpMethods;
import com.ywl01.jlinfo.net.SqlFactory;
import com.ywl01.jlinfo.observers.BaseObserver;
import com.ywl01.jlinfo.observers.BuildingObserver;
import com.ywl01.jlinfo.observers.GraphicObserver;
import com.ywl01.jlinfo.observers.HouseObserver;
import com.ywl01.jlinfo.observers.IntObserver;
import com.ywl01.jlinfo.observers.MarkObserver;
import com.ywl01.jlinfo.observers.PeopleObserver;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.BeanMapUtils;
import com.ywl01.jlinfo.utils.DialogUtils;
import com.ywl01.jlinfo.utils.StringUtils;
import com.ywl01.jlinfo.views.AddGraphicMenuDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;

public class MapListener extends DefaultMapViewOnTouchListener
        implements
        MapScaleChangedListener,
        ViewpointChangedListener,
        NavigationChangedListener {

    private final MapView mapView;


    private Graphic prevGraphic;
    private Graphic nowGraphic;

    //观察者
    private MarkObserver markObserver;
    private BuildingObserver buildingObserver;
    private HouseObserver houseObserver;

    //定义保存地图数据的overlay
    private GraphicsOverlay markOverlay;
    private GraphicsOverlay buildingOverlay;
    private GraphicsOverlay houseOverlay;
    private GraphicsOverlay locationOverlay;

    private Envelope prevExtent;

    private boolean isFirstLoad = true;
    private boolean isUpdateAllMarks = false;
    private boolean isUpdateAllBuildings;
    private boolean isUpdateAllHouses;

    //地图是否进行了缩放
    private Envelope nowExtent;
    private double mapScale;
    private final double displayScale_building;
    private final double displayScale_house;
    private boolean isMoveGraphic;
    private PeopleObserver peopleObserver;

    public MapListener(Context context, final MapView mapView) {
        super(context, mapView);
        this.mapView = mapView;

        this.mapView.addViewpointChangedListener(this);
        this.mapView.addNavigationChangedListener(this);
        this.mapView.addMapScaleChangedListener(this);


        markObserver = new MarkObserver();

        markOverlay = new GraphicsOverlay();
        buildingOverlay = new GraphicsOverlay();
        houseOverlay = new GraphicsOverlay();
        locationOverlay = new GraphicsOverlay();

        this.mapView.getGraphicsOverlays().add(markOverlay);
        this.mapView.getGraphicsOverlays().add(buildingOverlay);
        this.mapView.getGraphicsOverlays().add(houseOverlay);
        this.mapView.getGraphicsOverlays().add(locationOverlay);

        EventBus.getDefault().register(this);
        displayScale_building = CommVar.getInstance().getScaleBylevel(CommVar.buildingDisplayLevel);
        displayScale_house = CommVar.getInstance().getScaleBylevel(CommVar.houseDisplayLevel);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 地图元素的载入
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void navigationChanged(NavigationChangedEvent navigationChangedEvent) {
        //navigation 开始时 isNavigating（）is true；结束时为false；
        if (navigationChangedEvent.isNavigating()) {
            TypeEvent.dispatch(TypeEvent.CLEAR_BOTTOM_CONTAINER);
            return;
        } else {
            nowExtent = mapView.getVisibleArea().getExtent();
            boolean isChange = true;

            if (prevExtent != null) {
                isChange = isExtentChange(prevExtent, nowExtent);
            }
            prevExtent = nowExtent;
            mapScale = mapView.getMapScale();
            if (isChange) {
                loadMarks();
                System.out.println("加载。。。。");
                if (mapScale < displayScale_building) {
                    loadBuildings();
                } else {
                    clearGraphic(buildingOverlay);
                }

                if (mapScale < displayScale_house) {
                    loadHouses();
                } else {
                    clearGraphic(houseOverlay);
                }
            }
        }
    }

    private void clearGraphic(GraphicsOverlay graphicsOverlay) {
        graphicsOverlay.getGraphics().clear();
    }

    private void loadHouses() {
        houseObserver = new HouseObserver(mapScale);
        loadGraphics(houseObserver, nowExtent, mapScale, TableName.HOUSE);
    }

    private void loadMarks() {
        loadGraphics(markObserver, nowExtent, mapScale, TableName.MARK);
    }

    private void loadBuildings() {
        buildingObserver = new BuildingObserver(mapScale);
        loadGraphics(buildingObserver, nowExtent, mapScale, TableName.BUILDING);
    }

    private void updateGraphic(List<Graphic> loadGraphics, GraphicsOverlay graphicsOverlay, Boolean isUpdateAll) {
        System.out.println("加载完毕，开始更新graphic，是否更新全部" + isUpdateAll);
        if (isUpdateAll) {
            updateAllGraphic(loadGraphics, graphicsOverlay);
        } else {
            updatePartGraphic(loadGraphics, graphicsOverlay);
        }
    }

    //更新全部graphic
    private void updateAllGraphic(List<Graphic> loadGraphics, GraphicsOverlay graphicsOverlay) {
        ListenableList<Graphic> mapGraphics = graphicsOverlay.getGraphics();
        mapGraphics.clear();
        mapGraphics.addAll(loadGraphics);
    }

    //更新变化的graphic
    private void updatePartGraphic(List<Graphic> loadGraphics, GraphicsOverlay graphicsOverlay) {
        ListenableList<Graphic> mapGraphics = graphicsOverlay.getGraphics();
        //添加新的Graphic
        for (int i = 0; i < loadGraphics.size(); i++) {
            Graphic g = loadGraphics.get(i);
            if (!isGraphicInMap(mapGraphics, g)) {
                mapGraphics.add(g);
            }
        }
        //去除范围外的
        Envelope mapExtent = mapView.getVisibleArea().getExtent();
        for (int i = mapGraphics.size() - 1; i >= 0; i--) {
            Graphic g = mapGraphics.get(i);
            if (isGraphicOutExtent(mapExtent, g)) {
                mapGraphics.remove(g);
            }
        }
    }

    //graphic 是否已经在地图中
    private boolean isGraphicInMap(List<Graphic> mapGraphics, Graphic g) {
        for (int i = 0; i < mapGraphics.size(); i++) {
            Graphic graphic = mapGraphics.get(i);
            Geometry geo = graphic.getGeometry();
            if (geo.equals(g.getGeometry())) {
                return true;
            }
        }
        return false;
    }

    //graphic 是否出地图范围
    private boolean isGraphicOutExtent(Envelope mapExtent, Graphic g) {
        double currentMapScale = mapView.getMapScale();
        int displayLevel = (int) g.getAttributes().get("displayLevel");
        double graphicScale = CommVar.getInstance().getScaleBylevel(displayLevel);
        if (!GeometryEngine.contains(mapExtent, g.getGeometry()) || graphicScale < currentMapScale)
            return true;
        return false;
    }

    //服务器端载入数据
    private void loadGraphics(GraphicObserver observer, Envelope extent, double mapScale, String tableName) {
        String sql = SqlFactory.selectGraphicData(extent, mapScale, tableName);
        observer.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                if (observer == markObserver) {
                    updateGraphic((List<Graphic>) data, markOverlay, isUpdateAllMarks);
                    isUpdateAllMarks = false;
                } else if (observer == buildingObserver) {
                    updateGraphic((List<Graphic>) data, buildingOverlay, isUpdateAllBuildings);
                    isUpdateAllBuildings = false;
                } else if (observer == houseObserver) {
                    updateGraphic((List<Graphic>) data, houseOverlay, isUpdateAllHouses);
                    isUpdateAllHouses = false;
                }
            }
        });
        HttpMethods.getInstance().getSqlResult(observer, SqlAction.SELECT, sql);
    }

    //判断当前视图是否改变
    private boolean isExtentChange(Envelope beforeExtent, Envelope nowExtent) {
        return !beforeExtent.equals(nowExtent);
    }

    //地图初始化，第一次获取数据
    @Override
    public void viewpointChanged(ViewpointChangedEvent viewpointChangedEvent) {
        if (isFirstLoad) {
            Envelope extent = mapView.getVisibleArea().getExtent();
            CommVar.mapSpatialReference = mapView.getSpatialReference();
            loadGraphics(markObserver, extent, mapView.getMapScale(), TableName.MARK);
            isFirstLoad = false;
            prevExtent = extent;
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // 单击操作：
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        nowGraphic = getSelectGraphic(screenPoint);

        //单击空白区域
        if (nowGraphic == null) {
            if (isMoveGraphic) {
                moveGraphic(screenPoint);
            }
            return super.onSingleTapConfirmed(e);
        }

        changeGraphicSymbol();
        int graphicFlag = (int) nowGraphic.getAttributes().get("graphicFlag");

        switch (graphicFlag) {
            case GraphicFlag.MARK:
                showMarkInfo();
//                Viewpoint viewpoint = new Viewpoint(mp, mapView.getMapScale());
//                mapView.setViewpointAsync(viewpoint, 0.5f);
                break;

            case GraphicFlag.BUILDING:
                showBuildingPlan(nowGraphic);
                break;

            case GraphicFlag.POSITION:
                showPositionInfo();
                break;
        }
        return super.onSingleTapConfirmed(e);
    }

    private void showPositionInfo() {
        double positionMapScale = (double) nowGraphic.getAttributes().get("mapScale");
        String displayText = (String) nowGraphic.getAttributes().get("displayText");
        nowGraphic.getGraphicsOverlay().getGraphics().remove(nowGraphic);

        showPositionCallout(mapView, (Point) nowGraphic.getGeometry(), displayText, positionMapScale);
    }

    //显示markinfo
    private void showMarkInfo() {
        MarkBean markBean = new MarkBean();
        BeanMapUtils.mapToBean(nowGraphic.getAttributes(), markBean);

        ShowMarkInfoEvent event = new ShowMarkInfoEvent();
        event.markBean = markBean;
        event.dispatch();
    }

    private void showBuildingPlan(final Graphic g) {
        String sql = SqlFactory.selectPeopleByBuilding((int) g.getAttributes().get("id"));
        PeopleObserver peopleObserver_buildingPlan = new PeopleObserver(PeopleFlag.FROM_BUILDING);
        HttpMethods.getInstance().getSqlResult(peopleObserver_buildingPlan, SqlAction.SELECT, sql);
        peopleObserver_buildingPlan.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                List<PeopleBean> peoples = (List<PeopleBean>) data;
                System.out.println("show building plan");
                //获取buildingBean
                BuildingBean buildingBean = new BuildingBean();
                BeanMapUtils.mapToBean(nowGraphic.getAttributes(), buildingBean);
                if (buildingBean.countFloor == 0 || buildingBean.countUnit == 0 || buildingBean.countHomesInUnit == 0 || StringUtils.isEmpty(buildingBean.sortType)) {
                    DialogUtils.showAlert(BaseActivity.currentActivity, "楼房参数不正确，请先修改楼房参数", "确定", null);
                } else {
                    CommVar.getInstance().clear();
                    CommVar.getInstance().put("building", buildingBean);
                    CommVar.getInstance().put("peoples", peoples);

                    AppUtils.startActivity(BuildingPlanActivity.class);
                }
            }
        });
    }

    private void moveGraphic(android.graphics.Point screenPoint) {
        isMoveGraphic = false;
        Point mapPoint = mapView.screenToLocation(screenPoint);
        prevGraphic.setGeometry(mapPoint);

        String tableName = getTableNameByGraphic(prevGraphic);
        int id = (int) prevGraphic.getAttributes().get("id");
        Map<String, String> data = new HashMap<>();
        data.put("x", mapPoint.getX() + "");
        data.put("y", mapPoint.getY() + "");
        data.put("updateUser", CommVar.UserID + "");
        String sql = SqlFactory.update(tableName, data, id);

        IntObserver moveObserver = new IntObserver();
        HttpMethods.getInstance().getSqlResult(moveObserver, SqlAction.UPDATE, sql);
        moveObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                int rows = (int) data;
                if (rows > 0) {
                    AppUtils.showToast("移动位置成功");
                }
            }
        });
    }


    //设置点击的Graphic符号的变化
    private void changeGraphicSymbol() {
        if (nowGraphic == null) {
            return;
        }
        setFocusSymbol(nowGraphic.getSymbol());
        if (prevGraphic != null)
            recoverSymbol(prevGraphic.getSymbol());
        prevGraphic = nowGraphic;
    }

    //设置点击时的符号
    private void setFocusSymbol(Symbol symbol) {
        if (symbol instanceof CompositeSymbol) {
            CompositeSymbol cs = (CompositeSymbol) symbol;
            List<Symbol> symbols = cs.getSymbols();
            BitmapDrawable drawable = (BitmapDrawable) ContextCompat.getDrawable(AppUtils.getContext(), R.drawable.smb_glow);
            PictureMarkerSymbol pms = new PictureMarkerSymbol(drawable);
            pms.setHeight(25);
            pms.setWidth(25);
            symbols.add(1, pms);
        } else if (symbol instanceof TextSymbol) {
            TextSymbol ts = (TextSymbol) symbol;
            ts.setColor(Color.RED);
        }
    }

    //恢复符号
    private void recoverSymbol(Symbol symbol) {
        if (symbol instanceof CompositeSymbol) {
            CompositeSymbol cs = (CompositeSymbol) symbol;
            List<Symbol> symbols = cs.getSymbols();
            symbols.remove(1);
        } else if (symbol instanceof TextSymbol) {
            TextSymbol ts = (TextSymbol) symbol;
            ts.setColor(Color.BLACK);
        }
    }


    //获取当前点击的graphic
    private Graphic getSelectGraphic(android.graphics.Point screenPoint) {
        ListenableFuture<List<IdentifyGraphicsOverlayResult>> results = mapView.identifyGraphicsOverlaysAsync(screenPoint, 5, false, 1);
        try {
            List<IdentifyGraphicsOverlayResult> identifyGraphicsOverlayResults = results.get();
            if (identifyGraphicsOverlayResults.size() > 0) {
                IdentifyGraphicsOverlayResult identifyResult = identifyGraphicsOverlayResults.get(0);
                List<Graphic> graphics = identifyResult.getGraphics();
                if (graphics.size() > 0) {
                    return graphics.get(0);
                } else
                    return null;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return null;
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

    ///////////////////////////////////////////////////////////////////////////
    // 长按操作：
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onLongPress(MotionEvent e) {
        android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        nowGraphic = getSelectGraphic(screenPoint);

        //长按空白区域
        if (nowGraphic == null) {
            showAddGraphicDialog(mapView.screenToLocation(screenPoint));
        }
        //符号上面长按
        else{
            showGraphicMenu();
        }

        super.onLongPress(e);
    }

    //显示graphic菜单
    private void showGraphicMenu() {
        changeGraphicSymbol();
        ShowGraphicMenuEvent event = new ShowGraphicMenuEvent();
        event.graphic = nowGraphic;
        event.dispatch();
    }

    //显示添加graphic菜单
    private void showAddGraphicDialog(Point point) {
        Map<String, Object> data = new HashMap<>();
        data.put("mapScale", mapView.getMapScale());
        data.put("mapPoint", point);

        AddGraphicMenuDialog dialog = new AddGraphicMenuDialog(BaseActivity.currentActivity, R.style.DialogBackgroundNull);
        dialog.data = data;
        dialog.show();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 双击操作：
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        nowGraphic = getSelectGraphic(screenPoint);
        if (nowGraphic == null)
            return super.onDoubleTap(e);

        changeGraphicSymbol();
        showPeoples(nowGraphic);

        return false;//双击graphic,不进行默认操作，不放大地图
    }

    private void showPeoples(Graphic g) {
        //显示忙碌图标
        TypeEvent.dispatch(TypeEvent.SHOW_PROGRESS_BAR);
        int graphicFlag = (int) g.getAttributes().get("graphicFlag");
        int id = (int) g.getAttributes().get("id");
        String sql = "";
        int peopleFlag = -1;
        if (graphicFlag == GraphicFlag.MARK) {
            sql = SqlFactory.selectPeopleByMark(id);
            peopleFlag = PeopleFlag.FROM_MARK;
        } else if (graphicFlag == GraphicFlag.BUILDING) {
            sql = SqlFactory.selectPeopleByBuilding(id);
            peopleFlag = PeopleFlag.FROM_BUILDING;
        } else if (graphicFlag == GraphicFlag.HOUSE) {
            sql = SqlFactory.selectPeopleByHouse(id);
            peopleFlag = PeopleFlag.FROM_HOUSE;
        }
        peopleObserver = new PeopleObserver(peopleFlag);
        HttpMethods.getInstance().getSqlResult(peopleObserver, SqlAction.SELECT, sql);
        peopleObserver.setOnNextListener(new BaseObserver.OnNextListener() {
            @Override
            public void onNext(Observer observer, Object data) {
                TypeEvent.dispatch(TypeEvent.HIDE_PROGRESS_BAR);
                ArrayList<PeopleBean> peoples = (ArrayList<PeopleBean>) data;
                if (peoples.size() > 0) {
                    CommVar.getInstance().clear();
                    CommVar.getInstance().put("peoples", peoples);
                    AppUtils.startActivity(PeoplesActivity.class);
                } else {
                    Toast.makeText(AppUtils.getContext(), "无相关人员", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ///////////////////////////////////////////////////////////////////////////
    // eventBus 事件的接收
    ///////////////////////////////////////////////////////////////////////////
    @Subscribe
    public void refreshGraphic(TypeEvent event) {
        switch (event.type) {
            case TypeEvent.REFRESH_MARKERS:
                isUpdateAllMarks = true;
                loadMarks();
                break;
            case TypeEvent.REFRESH_BUILDINGS:
                isUpdateAllBuildings = true;
                loadBuildings();
                break;
            case TypeEvent.REFRESH_HOUSE:
                isUpdateAllHouses = true;
                loadHouses();
                break;
            case TypeEvent.MOVE_GRAPHIC:
                isMoveGraphic = true;
                AppUtils.showToast("请在要移动的位置上单击");
                break;
            case TypeEvent.CLEAR_LOCATION:
                locationOverlay.getGraphics().clear();
                break;
        }
    }

    @Subscribe
    public void showGraphicLocation(ShowGraphicLocationEvent event) {
        locationOverlay.getGraphics().clear();
        List<Graphic> positions = event.positions;
        if (mapView.getCallout().isShowing()) {
            mapView.getCallout().dismiss();
        }
        if (positions.size() == 1) {
            Graphic g = positions.get(0);
            double mapscale = (double) g.getAttributes().get("mapScale");
            String displayText = (String) g.getAttributes().get("displayText");
            showPositionCallout(mapView, (Point) g.getGeometry(), displayText, mapscale);
        } else {
            locationOverlay.getGraphics().addAll(positions);
            mapView.setViewpointGeometryAsync(locationOverlay.getExtent(), 20);
        }
    }

    private void showPositionCallout(final MapView mapView, Point mapPoint, String displayText, double mapScale) {
        mapView.setViewpointCenterAsync(mapPoint, mapScale);
        final Callout callout = mapView.getCallout();
        TextView calloutContent = new TextView(AppUtils.getContext());
        calloutContent.setTextColor(Color.BLACK);
        calloutContent.setSingleLine();
        calloutContent.setText(displayText);
        callout.setContent(calloutContent);
        callout.setLocation(mapPoint);
        callout.show();
        calloutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callout.dismiss();
                mapView.setViewpointScaleAsync(mapView.getMapScale() / 3);
            }
        });
    }

    @Subscribe
    public void showLocation(LocationEvent event) {
        Point p = new Point(event.x, event.y, CommVar.mapSpatialReference);
        SimpleMarkerSymbol sms = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 12);
        Graphic g = new Graphic(p,sms);
        locationOverlay.getGraphics().clear();
        locationOverlay.getGraphics().add(g);
    }
    //禁止地图旋转
    @Override
    public boolean onRotate(MotionEvent event, double rotationAngle) {
        return false;
    }

    @Override
    public void mapScaleChanged(MapScaleChangedEvent mapScaleChangedEvent) {
        System.out.println("map scale change,current map scale:" + mapView.getMapScale());
        isUpdateAllBuildings = true;
        isUpdateAllHouses = true;
    }
}
