package com.lakala.elive.map.common;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.lakala.elive.R;
import com.lakala.elive.common.utils.Utils;

/**
 * Created by hongzhiliang on 2016/9/17.
 */
public abstract class BaseActivity extends Activity {



    private static final String TAG = "BaseActivity";

    /** 121.396101,31.23853 公司坐标（北京市海淀区东北旺南路45号）*/
    protected LatLng companyPos = new LatLng(31.23853, 121.396101);

    /** 天安门坐标 */
    protected LatLng tamPos = new LatLng(39.915112,116.403963);


    protected MapView mMapView = null;
    protected BaiduMap mBaiduMap  = null;

//    1.隐藏缩放按钮、比例尺
//    2.获取获取最小（3）、最大缩放级别（20）
//    3.设置地图中心点
//    4.设置地图缩放为15
//    5.更新地图状态
//    1)缩小
//    2)放大
//    3)旋转（0 ~ 360），每次在原来的基础上再旋转30度
//    4)俯仰（0 ~ -45），每次在原来的基础上再俯仰-5度
//    5)移动
//    6.获取地图Ui控制器：隐藏指南针

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);

        //1.隐藏缩放按钮、比例尺
        mMapView.showScaleControl(false);//比例尺
        mMapView.showZoomControls(false);//缩放按钮

        //获取地图控制器MapController
        mBaiduMap = mMapView.getMap();

//       2.获取获取最小（3）、最大缩放级别（20）
        float maxZoomLever = mBaiduMap.getMaxZoomLevel();
        float minZoomLever = mBaiduMap.getMinZoomLevel();
        Log.i(TAG, "minZoomLever=" + minZoomLever + "maxZoomLever=" + maxZoomLever);


//       3.设置地图中心点(纬度，经度)
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(companyPos);
        mBaiduMap.setMapStatus(mapStatusUpdate);

//      4.设置地图缩放为15
        mapStatusUpdate = MapStatusUpdateFactory.zoomTo(15);
        mBaiduMap.setMapStatus(mapStatusUpdate);

//      6.	获取地图Ui控制器：隐藏指南针
         UiSettings uiSettings = mBaiduMap.getUiSettings();
         uiSettings.setCompassEnabled(false);	//  不显示指南针

        init();
    }

    /** 这个方法让子类实现 */
    public abstract void init();


    /**
     * 在屏幕中央显示一个Toast
     * @param text
     */
    public void showToast(CharSequence text) {
        Utils.showToast(this, text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//        if(mMapView!=null){
//            mMapView.onDestroy();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
//        if(mMapView!=null){
//            mMapView.onResume();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
//        if(mMapView!=null){
//            mMapView.onPause();
//        }
    }

}
