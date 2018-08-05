package com.ywl01.jlinfo.map;

import android.Manifest;
import android.app.Activity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.ywl01.jlinfo.utils.AppUtils;
import com.ywl01.jlinfo.utils.MPermissionUtils;
import com.ywl01.jlinfo.utils.StringUtils;


/**
 * Created by ywl01 on 2017/10/12.
 * 利用百度定位
 */

public class Location implements MPermissionUtils.OnPermissionListener {
    private LocationClient locationClient;
    private Activity activity;

    private String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private OnGetLocationListener onGetLocationListener;
    private boolean isFirst = true;

    public void setOnGetLocationListener(OnGetLocationListener onGetLocationListener) {
        this.onGetLocationListener = onGetLocationListener;
    }

    public Location(Activity activity) {
        this.activity = activity;
        checkPremission();
    }

    private void checkPremission() {
        if (MPermissionUtils.checkPermissions(activity, perms)) {
            System.out.println("有定位权限");
            initLocation();
        } else {
            MPermissionUtils.requestPermissionsResult(activity, 100, perms, this);
        }
    }

    private void initLocation() {
        locationClient = new LocationClient(activity);
        BDAbstractLocationListener locationListener = new LocationListener();
        locationClient.registerLocationListener(locationListener);

        LocationClientOption option = new LocationClientOption();

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(true);

        locationClient.setLocOption(option);

        if (!locationClient.isStarted()) {
            locationClient.start();
        }
    }

    //请求定位（对外方法）
    public void requestLocation() {
        if (locationClient == null) {
            return;
        }
        if (!locationClient.isStarted()) {
            locationClient.start();
        }
        locationClient.requestLocation();
    }

    private String getLocationAddrInfo(BDLocation bdLocation) {
        String addstr = bdLocation.getAddrStr();
        StringUtils.checkStr(addstr);
        if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
            return "GPS定位：" + addstr;
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
            return "网络定位：" + addstr;
        } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
            return "网络定位失败。";
        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
            return "无网络";
        } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
            return "当前缺少定位依据，可能是用户没有授权。";
        }
        return "定位失败。";
    }

    @Override
    public void onPermissionGranted() {
        System.out.println("有权限定位");
        initLocation();
    }

    @Override
    public void onPermissionDenied() {
        System.out.println("权限被拒绝");
        MPermissionUtils.showTipsDialog(activity);
    }

    private class LocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            System.out.println("获取定位信息");
            String locationInfo = getLocationAddrInfo(bdLocation);
            AppUtils.showToast(locationInfo);

            if (onGetLocationListener != null)
                onGetLocationListener.getLocationInfo(bdLocation);
        }
    }

    public interface OnGetLocationListener {
        void getLocationInfo(BDLocation locationInfo);
    }
}
