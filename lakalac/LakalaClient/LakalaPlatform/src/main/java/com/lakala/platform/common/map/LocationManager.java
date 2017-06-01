package com.lakala.platform.common.map;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;


/**
 * Created by LMQ on 2015/1/27.
 */
public class LocationManager {

    private static LocationManager instance = new LocationManager();
    private double longitude = 0.0;
    private double latitude = 0.0;
    private boolean isFirst=true;

    public boolean isFirst2() {
        return isFirst2;
    }

    public LocationManager setFirst2(boolean first2) {
        isFirst2 = first2;
        return instance;
    }

    private boolean isFirst2=true;
    private String city;
    private String address;

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    private LocationListener locationListener;

    private LocationManager() {
        initSdk();
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public synchronized static LocationManager getInstance() {

        if(instance == null){
            instance = new LocationManager();
        }
        return instance;
    }

    private LocationClient mLocationClient;

    public void initSdk(){
        mLocationClient = new LocationClient(ApplicationEx.getInstance());     //声明LocationClient类
        mLocationClient.registerLocationListener(bdLocationListener);    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(20000);//设置发起定位请求的间隔时间为900ms,会处理成一次性
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(false);//返回的定位结果包含手机机头的方向
        option.setProdName("shoudan");
        mLocationClient.setLocOption(option);
    }

    private BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            LogUtil.print("c:" + bdLocation.getLocType() +"纬度:" + bdLocation.getLatitude() + "经度:" + bdLocation.getLongitude());
             boolean isLocationSuccess = false;
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                //GPS定位成功
                isLocationSuccess = true;
                LogUtil.print("test3","GPS定位成功");
            }
            if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                //网络定位成功
                isLocationSuccess = true;
                LogUtil.print("test3","网络定位成功");
            }
            if(bdLocation.getLocType() == BDLocation.TypeOffLineLocation){
                //离线定位成功
                isLocationSuccess = true;
                LogUtil.print("test3","离线定位成功");
            }
            if(isLocationSuccess){
                longitude = bdLocation.getLongitude();
                latitude = bdLocation.getLatitude();
                city=bdLocation.getCity();
                address=bdLocation.getAddress().address;
                LogUtil.print("test3","\nlongitude:"+longitude+"\n"+"latitude:"+latitude+"\n"+"city:"+city+"\n"+"address:"+address+"\n");
                LogUtil.print("test3","address:"+bdLocation.getAddress());
                if (isFirst==false||isFirst2==false){
                    if(locationListener != null){
                        locationListener.onLocate();
                        LogUtil.print("test3","onLocate");
                    }
                }
                mLocationClient.stop();
            }else{
                LogUtil.print("iiii",isFirst+"<>"+isFirst2);
                if (isFirst==false||isFirst2==false){
                    locationListener.onFailed();
                    LogUtil.print("test3","onFailed");
                }
                mLocationClient.stop();
            }

        }
    };
    public void startLocation(LocationListener locationListener){
        this.locationListener = locationListener;
        if(mLocationClient == null){
            initSdk();
        }
        mLocationClient.start();
    }

    public interface LocationListener{
        void onLocate();
        void onFailed();
    }

    public void statLocating(){

        if (mLocationClient == null ){
            initSdk();
        }
        if(!mLocationClient.isStarted())
        {
            mLocationClient.start();
            //mLocationClient.requestLocation();

        }
        else
            LogUtil.print("LocSDK5", "locClient is null or not started");


    }

    private BDNotifyListener bdNotifyListener = new BDNotifyListener() {
        @Override
        public void SetNotifyLocation(double v, double v2, float v3, String s) {
            super.SetNotifyLocation(v, v2, v3, s);
        }

        @Override
        public void onNotify(BDLocation bdLocation, float v) {
            super.onNotify(bdLocation, v);
        }
    };

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
