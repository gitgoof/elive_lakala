package com.map.service;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

/**
 * 百度地图定位监听
 * Created by xiaogu on 2015/6/10. *
 */

public class MyLocationListenner implements BDLocationListener, OrientationUtil.OnOrientationListener {

    BaiduMap mBaiduMap;
    public boolean isFirstLoc;
    OrientationUtil orientationUtil;
    Context context;
    int myDirection;//方向

    public LatLng ll;

    public MyLocationListenner(BaiduMap mBaiduMap, boolean isFirstLoc, Context context) {
        this.mBaiduMap = mBaiduMap;
        this.isFirstLoc = isFirstLoc;
        this.context = context;
        orientationUtil = new OrientationUtil(context);
        orientationUtil.start();
        orientationUtil.setOnOrientationListener(this);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        // map view 销毁后不在处理新接收的位置
        if (location == null || mBaiduMap == null) return;
        MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(myDirection).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        if (isFirstLoc) {
            isFirstLoc = false;
            ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(u);
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {

    }

    @Override
    public void onOrientationChanged(float x) {
        myDirection = (int) x;
    }
}
