package com.lakala.shoudan.activity.shoudan.loan.location;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.zhangdan.app.loansdklib.api.sdkhelp.api.InitializaU51LoanSDK;
import com.zhangdan.app.loansdklib.location.U51LocationClient;
import com.zhangdan.app.loansdklib.location.U51LocationClientManager;
import com.zhangdan.app.loansdklib.location.U51LocationData;
import com.zhangdan.app.loansdklib.location.U51LocationResultListener;

import java.lang.ref.WeakReference;

/**
 * Created by intbird on 16/4/1.
 */
public class U51LocationHelper {

    private static U51LocationHelper instance;

    public static U51LocationHelper getInstance() {
        if (null == instance) {
            synchronized (U51LocationHelper.class) {
                if (null == instance) {
                    instance = new U51LocationHelper();
                }
            }
        }
        return instance;
    }

    /**
     * InitializaU51LoanSDK.initLoanSDK添加u51LocationClient后,
     * 可以直接调用U51LocationClientManager.getInstance().start()等定位方法;
     *
     * @param context
     */
    public void initialize(Context context) {
        mContext = context.getApplicationContext();
        InitializaU51LoanSDK.initLoanSDK(context, u51LocationClient);
    }

    public void registerListener(U51LocationResultListener u51LocationResultListener) {
        U51LocationClientManager.getInstance().registerListener(u51LocationResultListener);
    }

    public void start() {
        U51LocationClientManager.getInstance().start();
    }

    public void requestLocation() {
        U51LocationClientManager.getInstance().requestLocation();
    }

    public void stop() {
        U51LocationClientManager.getInstance().stop();
    }

    private Context mContext;
    private LocationClient mBDLocationClient;

    /**
     * 51LocationClient;
     */
    private U51LocationClient u51LocationClient = new U51LocationClient() {

        @Override
        public void start() {
            myBDLocationClient().start();
        }

        @Override
        public void stop() {
            myBDLocationClient().stop();
        }

        @Override
        public void requestLocation() {
            myBDLocationClient().requestLocation();
        }

        @Override
        public void registerLocationListener(U51LocationResultListener u51LocationResultListener) {
            myBDLocationClient().registerLocationListener(new MyBDLocationListener(u51LocationResultListener));
        }
    };

    /**
     * 百度LocationClient;
     *
     * @return
     */
    private LocationClient myBDLocationClient() {
        if (null == mBDLocationClient) {
            mBDLocationClient = new LocationClient(mContext);     //声明LocationClient类
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
            );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
            option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            option.setOpenGps(false);//可选，默认false,设置是否使用gps
            option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
            mBDLocationClient.setLocOption(option);
        }
        return mBDLocationClient;
    }

    /**
     * 百度定位回调;
     */
    private static class MyBDLocationListener implements BDLocationListener {
        WeakReference<U51LocationResultListener> listenerWeakReference;

        public MyBDLocationListener(U51LocationResultListener listener) {
            this.listenerWeakReference = new WeakReference<>(listener);
        }

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation &&
                    null != listenerWeakReference &&
                    null != listenerWeakReference.get()) {
                U51LocationData data = new U51LocationData();
                data.setProvince(bdLocation.getProvince());
                data.setCity(bdLocation.getCity());
                data.setLongitude(bdLocation.getLongitude());
                data.setLatitude(bdLocation.getLatitude());
                listenerWeakReference.get().onReceiveLocation(data);
            }
        }

    }
}
