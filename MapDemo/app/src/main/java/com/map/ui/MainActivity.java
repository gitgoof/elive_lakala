package com.map.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.map.R;
import com.map.bean.MyMarker;
import com.map.common.Params;
import com.map.service.LocationService;
import com.map.uipresenter.MainPress;
import com.map.uiview.MainView;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements MainView, BaiduMap.OnMarkerClickListener, BaiduMap.OnMapClickListener {
    @BindView(R.id.mapView)
    MapView mapView;
    BaiduMap baiduMap;

    @BindView(R.id.line_detail)
    LinearLayout line_detail;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_loacte)
    CheckBox iv_loacte;

    MainPress mainPress;
    MyMarker myMarker;//当前选中的点
    ArrayMap<String, MyMarker> mapTask = new ArrayMap<>();//用来存储加入拜访的点
    private LocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(
                        Manifest.permission.ACCESS_COARSE_LOCATION,//定位(网络)
                        Manifest.permission.ACCESS_FINE_LOCATION,//定位（gps）

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,//存储（写）
                        Manifest.permission.READ_EXTERNAL_STORAGE//存储(读)
                ).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {//有权限

                } else {//没有权限
                    Toast.makeText(MainActivity.this, "进行定位需要获取权限", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mainPress = new MainPress(MainActivity.this);
        init();


    }

    protected void init() {
        baiduMap = mapView.getMap();
        baiduMap.setOnMarkerClickListener(this);//marker的点击事件
        baiduMap.setOnMapClickListener(this);//地图点击事件
        locationService = new LocationService(baiduMap, this);

        mainPress.addMarker(baiduMap);
    }

    @OnClick({R.id.btn_visit, R.id.btn_task, R.id.iv_loacte, R.id.btn_serach})
    void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_visit:
                if (myMarker != null) mapTask.put(myMarker.getId(), myMarker);
                Toast.makeText(MainActivity.this, "添加任务成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_task:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TaskActivity.class);

                List<MyMarker> myMarkers = new ArrayList<>();
                myMarkers.addAll(mapTask.values());

                Bundle bundle = new Bundle();
                bundle.putSerializable(Params.MYMARKERS, (Serializable) myMarkers);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.iv_loacte:
                if (iv_loacte.isChecked()) {//定位
                    locationService.startLocate();
                } else {//关闭
                    locationService.stopLocate();
                }
                break;
            case R.id.btn_serach:
                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, SerachActivity.class);
                startActivityForResult(intent1, 100);
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        setMarkerUnChecked();//点击新的点前先关闭之前的点
        Bundle bundle = marker.getExtraInfo();
        if (bundle != null && bundle.containsKey(Params.MYMARKER)) {
            MyMarker myMarker = (MyMarker) bundle.get(Params.MYMARKER);
            this.myMarker = myMarker;
            setMarkerChecked(marker);
            setMarkerInfo(myMarker);
        }
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (line_detail.getVisibility() != View.GONE) line_detail.setVisibility(View.GONE);
        setMarkerUnChecked();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    @Override
    public void setMarkerInfo(MyMarker markerInfo) {
        if (line_detail.getVisibility() != View.VISIBLE) line_detail.setVisibility(View.VISIBLE);
        tv_title.setText(markerInfo.getTitle());
    }

    ArrayMap<Marker, BitmapDescriptor> mapMarker = new ArrayMap<>();

    @Override
    public void setMarkerChecked(Marker marker) {
        // 保存原有图片
        mapMarker.put(marker, marker.getIcon());
        // BitmapDescriptor
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_dw_pointer));
    }

    @Override
    public void setMarkerUnChecked() {
        if (mapMarker != null && mapMarker.size() != 0) {
            mapMarker.keyAt(0).setIcon(mapMarker.valueAt(0));
            mapMarker.clear();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {//搜索回调
            int distance = data.getIntExtra(Params.DISTANCE, 0);
            if (locationService.myListener.ll == null) {
                Toast.makeText(MainActivity.this, "定位之后进行搜索", Toast.LENGTH_SHORT).show();
            } else {
                mainPress.serach(locationService.myListener.ll, distance);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        locationService.stopLocate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
}
