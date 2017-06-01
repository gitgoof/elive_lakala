package com.map.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.map.R;
import com.map.adapter.TaskAtapter;
import com.map.bean.MyMarker;
import com.map.common.Params;
import com.map.uipresenter.TaskPress;
import com.map.uiview.TaskView;
import com.map.util.MapUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 任务列表
 */
public class TaskActivity extends AppCompatActivity implements TaskView {
    @BindView(R.id.mapView)
    MapView mapView;
    BaiduMap baiduMap;
    @BindView(R.id.rc_tasks)
    RecyclerView rc_tasks;
    TaskAtapter taskAtapter;

    TaskPress taskPress;
    ArrayList<MyMarker> myMarkers;
    ArrayMap<String, Overlay> mapMarker = new ArrayMap<>();//用来保存地图上存在的点

    ArrayList<MyMarker> mMarkers = new ArrayList<>();//保存的是起点放到了第一个位置，终点为最后一个位置进行路线规划

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);
        taskPress = new TaskPress(this);
        init();
    }

    protected void init() {
        baiduMap = mapView.getMap();
        rc_tasks.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                switch (view.getId()) {
                    case R.id.btn_start://设为起点(将设为起点的点放到第一个)
                        mMarkers.clear();
                        mMarkers.addAll(myMarkers);
                        mMarkers.remove(i);
                        mMarkers.add(0, (MyMarker) baseQuickAdapter.getData().get(i));
                        break;
                    case R.id.btn_remove://移除任务
                        mMarkers.clear();
                        mMarkers.addAll(myMarkers);

                        String markerId = ((MyMarker) baseQuickAdapter.getData().get(i)).getId();
                        mapMarker.get(markerId).remove();//移除地图上显示的
                        mapMarker.remove(markerId);//移除列表中
                        taskAtapter.remove(i);
                        break;
                }
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        myMarkers = (ArrayList<MyMarker>) bundle.getSerializable(Params.MYMARKERS);

        MapUtil.setMapBunds(baiduMap, myMarkers);

        rc_tasks.setLayoutManager(new LinearLayoutManager(this));
        taskAtapter = new TaskAtapter();
        taskAtapter.setNewData(myMarkers);
        rc_tasks.setAdapter(taskAtapter);

        List<Overlay> overlays = taskPress.addMarkerToMap(this, baiduMap, myMarkers);
        for (Overlay overlay : overlays) {
            Bundle bundleMarker = overlay.getExtraInfo();
            MyMarker myMarker = (MyMarker) bundleMarker.get(Params.MYMARKER);
            mapMarker.put(myMarker.getId(), overlay);
        }
    }

    @OnClick({R.id.btn_route_plan})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_route_plan:
                if (mMarkers == null || mMarkers.size() == 0) {
                    Toast.makeText(TaskActivity.this, "先设置起点", Toast.LENGTH_SHORT).show();
                    return;
                } else if (mMarkers.size() < 2) {
                    Toast.makeText(TaskActivity.this, "路线规划至少需要两个点", Toast.LENGTH_SHORT).show();
                    return;
                }
                double distance = 0;
                int j = 0;//用来存储最远的点
                MyMarker start = mMarkers.get(0);
                for (int i = 1; i < mMarkers.size(); i++) {//找出距离起点的最远的点
                    MyMarker myMarker = myMarkers.get(i);
                    double getDistance = DistanceUtil.getDistance(new LatLng(start.getLat(), start.getLng()), new LatLng(myMarker.getLat(), myMarker.getLng()));
                    if (distance < getDistance) {
                        distance = getDistance;
                        j = i;
                    }
                }
                MyMarker end = mMarkers.get(j);
                mMarkers.remove(j);
                mMarkers.add(end);//最后的到的mMarkers第一个点为起点，最后一个点为终点


                Intent intent = new Intent();
                intent.setClass(TaskActivity.this, RoutePlanActivity.class);

                Bundle bundle = new Bundle();
                bundle.putSerializable(Params.MYMARKERS, mMarkers);
                intent.putExtras(bundle);

                startActivity(intent);
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
