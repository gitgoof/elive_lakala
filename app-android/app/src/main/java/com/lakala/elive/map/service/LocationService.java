package com.lakala.elive.map.service;

import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MyLocationConfiguration;

/**
 * Created by xg on 2016/8/11.
 * 定位方法在这里
 */
public class LocationService implements ILocationService {
    // 定位相关
    LocationClient mLocClient;
    BaiduMap baiduMap;
    public MyLocationListenner myListener;

    /**
     * 初始化定位参数
     */
    public LocationService(BaiduMap baiduMap, Context context) {
        this.baiduMap = baiduMap;
        //初始化定位
        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;//NORMAL普通态： 更新定位数据时不对地图做任何操作
        myListener = new MyLocationListenner(this.baiduMap, true, context);//第一次定位true
        this.baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));//定位图标为默认图标
        // 开启定位图层
        this.baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
    }

    /**
     * 设置定位时间
     *
     * @param time
     */
    @Override
    public void setLocateTime(int time) {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(time * 1000);
        mLocClient.setLocOption(option);
    }

    /**
     * 开始定位
     */
    @Override
    public void startLocate() {
        if (baiduMap != null) {
            baiduMap.setMyLocationEnabled(true);
        }
        if (myListener != null) {
            myListener.isFirstLoc = true;
        }
        mLocClient.start();
        mLocClient.requestLocation();//请求定位，异步返回，结果在locationListener中获取.
    }

    /**
     * 停止定位
     */
    @Override
    public void stopLocate() {
        if (baiduMap != null) {
            baiduMap.setMyLocationEnabled(false);
        }
        if (mLocClient.isStarted()) {
            mLocClient.stop();  // 退出时销毁定位
        }
        if(myListener!=null){
            myListener.setmReceiverLoca(null);
        }
    }
    public void destoryLocation(){
        if(myListener!=null){
            myListener.setmReceiverLoca(null);
        }
        baiduMap = null;
        myListener = null;
        mLocClient = null;
    }
}
