package com.ywl01.jlinfo.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import com.ywl01.jlinfo.events.LocationEvent;
import com.ywl01.jlinfo.utils.MPermissionUtils;

public class LocationService {
    private Activity activity;
    private String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private MyLocationListener listener;
    private LocationManager locationManager;
    public LocationService(Activity activity) {
        this.activity = activity;
        checkPremission();
    }

    private void checkPremission() {
        if (MPermissionUtils.checkPermissions(activity, perms)) {
            System.out.println("有定位权限");
            initLocationManager();
        } else {
            MPermissionUtils.requestPermissionsResult(activity, 100, perms, new MPermissionUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted() {
                    initLocationManager();
                }

                @Override
                public void onPermissionDenied() {
                    MPermissionUtils.showTipsDialog(activity);
                }
            });
        }
    }

    private void initLocationManager() {
        listener = new MyLocationListener();
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void requestLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,listener);
    }

    public void closeLocation() {
        locationManager.removeUpdates(listener);
    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            System.out.println("x:" + location.getLongitude() + "-----y:" + location.getLatitude());
            LocationEvent event = new LocationEvent(location.getLongitude(), location.getLatitude());
            event.dispatch();
        }

        @Override
        public void onStatusChanged(String s, int status, Bundle bundle) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    System.out.println("当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    System.out.println("当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    System.out.println("当前GPS状态为暂停服务状态");
                    break;
            }

        }

       // GPS开启时触发
        @Override
        public void onProviderEnabled(String s) {

        }

        //GPS禁用时触发
        @Override
        public void onProviderDisabled(String s) {

        }
    }
}






