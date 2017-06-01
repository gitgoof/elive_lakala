package com.lakala.elive.common.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.lakala.elive.R;

import java.util.List;

public class BaiduMapUtils {

    public String gpsTime = ""; // 定位时间

    public double latitude; // 纬度

    public double longitude; // 经度

    public double radius; // 半径

    public String province = ""; // 省份

    public String city = ""; // 城市

    public String  district = ""; //区域

    public String locationAddr = "";// 详细地址

    public String allText = "";// 以上所有信息

    public double speed; // 速度

    public int satelliteNumber; // 卫星数

    public int locType; //定位类型

    //百度地图
    public BDLocation mLocation = null;
    public LocationClient mLocationClient = null;

    //定位处理类
    public BDLocationListener myListener = new MyLocationListener();
    public List<Poi> mPoiList;
    private Context mContext;

    //外部调用异步处理接口
    public LocationGpsListener mLocationGpsListener;

    public BaiduMapUtils() {

    }

    /**
     * @方法说明:开启定位
     * @方法名称:startGps
     * @返回值:void
     */
    public void startGps(Context mContext) {
        this.mContext = mContext;
        initLocationClient();
        mLocationClient.start();
    }

    public void initLocationClient() {
        // 初始化
        mLocationClient = new LocationClient(mContext);
        //设置定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span= 5000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //注册监听函数
        mLocationClient.registerLocationListener(new MyLocationListener());
    }

    /**
     * @方法说明:注销定位
     * @方法名称:stopGps
     * @返回值:void
     */
    public void stopGps() {
        if(mLocationClient==null){
            return;
        }
        mLocationClient.unRegisterLocationListener(myListener);
        mLocationClient.stop();
        mLocationClient = null;
    }

    /** 设置定位图层的配置 */
    public void setMyLocationConfigeration(MyLocationConfiguration.LocationMode mode,BaiduMap mBaiduMap) {
        boolean enableDirection = true;	// 设置允许显示方向
        BitmapDescriptor customMarker = BitmapDescriptorFactory.fromResource(R.mipmap.icon_geo);	// 自定义定位的图标
        MyLocationConfiguration config = new MyLocationConfiguration(mode, enableDirection, customMarker);
        mBaiduMap.setMyLocationConfigeration(config);
    }

    public void setLocationListener(LocationGpsListener mLocationGpsListener) {
        this.mLocationGpsListener = mLocationGpsListener;
    }

    /**
     *
     * 当前位置定位监听处理
     *
     */
    public class MyLocationListener  implements BDLocationListener {
        // 在这个方法里面接收定位结果
        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location != null){
                handleReceiveLocation(location);
                mLocationGpsListener.locationSuccess();
            }else{
                mLocationGpsListener.locationSuccess();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

    }

    /**
     * 处理定位的地址
     * @param location
     */
    public void handleReceiveLocation(BDLocation location) {
        //Receive Location
        StringBuffer sb = new StringBuffer(256);

        gpsTime = location.getTime(); // 定位时间
        sb.append("time : ");
        sb.append(gpsTime);

        locType = location.getLocType(); //定位类型
        sb.append("\nerror code : ");
        sb.append(locType);

        latitude = location.getLatitude(); //纬度
        sb.append("\nlatitude : ");
        sb.append(latitude);

        longitude = location.getLongitude(); // 经度
        sb.append("\nlontitude : ");
        sb.append(longitude);

        radius = location.getRadius(); // 半径
        sb.append("\nradius : ");
        sb.append(radius);

        this.mLocation = location;

        if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 单位：公里每小时
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 单位：米
            sb.append("\ndirection : ");
            sb.append(location.getDirection());// 单位度
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());// 位置语义化信息

        city = location.getCity();
        province = location.getProvince();
        district = location.getDistrict();
        locationAddr = location.getAddrStr();

        mPoiList = location.getPoiList();// POI数据
        if (mPoiList != null) {
            sb.append("\npoilist size = : ");
            sb.append(mPoiList.size());
            for (Poi p : mPoiList) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }

    }

    public static interface LocationGpsListener{

        public void locationSuccess();

        public void locationErr();
    }
}
