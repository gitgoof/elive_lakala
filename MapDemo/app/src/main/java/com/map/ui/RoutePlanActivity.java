package com.map.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.map.R;
import com.map.adapter.RoutePlanAdapter;
import com.map.bean.MyMarker;
import com.map.common.Params;
import com.map.uipresenter.RoutePlanPress;
import com.map.uiview.RoutePlanView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RoutePlanActivity extends AppCompatActivity implements RoutePlanView {
    @BindView(R.id.lin_content)
    LinearLayout lin_content;
    @BindView(R.id.mapView)
    MapView mapView;
    BaiduMap baiduMap;
    @BindView(R.id.rc_tasks)
    RecyclerView rc_tasks;
    RoutePlanAdapter routePlanAdapter;

    RoutePlanPress routePlanPress;

    ArrayList<MyMarker> myMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        ButterKnife.bind(this);
        routePlanPress = new RoutePlanPress(this);
        init();
    }

    protected void init() {
        baiduMap = mapView.getMap();
        rc_tasks.setLayoutManager(new LinearLayoutManager(this));
        routePlanAdapter = new RoutePlanAdapter();
        rc_tasks.setAdapter(routePlanAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myMarkers = (ArrayList<MyMarker>) bundle.getSerializable(Params.MYMARKERS);

        routePlanAdapter.setNewData(myMarkers);

        routePlanPress.getRoute(baiduMap, myMarkers.get(0), myMarkers.get(myMarkers.size() - 1), myMarkers.subList(1, myMarkers.size() - 1));
        routePlanPress.addMarkerToMap(this, baiduMap, myMarkers);
    }

    @OnClick({R.id.btn_navigation})
    void click(View view) {
        switch (view.getId()) {
            case R.id.btn_navigation:
                routePlanPress.showChooseWay(lin_content);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
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
