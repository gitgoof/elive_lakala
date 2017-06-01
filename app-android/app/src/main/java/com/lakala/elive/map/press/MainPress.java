package com.lakala.elive.map.press;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lakala.elive.map.bean.MyMarker;
import com.lakala.elive.map.common.FileUtil;
import com.lakala.elive.map.common.Params;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaogu on 2017/3/13.
 */

public class MainPress extends BasePress {
    Context context;
//    MainView mainView;

    ArrayMap<String, Marker> mapMarker = new ArrayMap<>();//地图上全部显示的点，用来控制显示和隐藏

    public MainPress(Context context) {
        this.context = context;
//        this.mainView = (MainView) context;
    }

    public void addMarker(BaiduMap baiduMap) {
        String json = FileUtil.getJson(context, "demo.json");

        Gson gson = new Gson();
        ArrayList<MyMarker> myMarkers = gson.fromJson(json, new TypeToken<ArrayList<MyMarker>>() {}.getType());

        // 返回标签的列表,Overlay 类型为 Marker
        List<Overlay> overlays = addMarkerToMap(context, baiduMap, myMarkers);

        for (Overlay overlay : overlays) {
            Bundle bundleMarker = overlay.getExtraInfo();
            MyMarker myMarker = (MyMarker) bundleMarker.get(Params.MYMARKER);
            mapMarker.put(myMarker.getId(), (Marker) overlay);
        }
    }

    /**
     * 坐标点，距离
     * 在坐标点内的图标进行显示
     * @param latLng
     * @param distance
     */
    public void serach(LatLng latLng, int distance) {
        for (Marker marker : mapMarker.values()) {
            double getDistance = DistanceUtil.getDistance(latLng, marker.getPosition());
            if (getDistance > distance) {
                marker.setVisible(false);
            } else {
                if (!marker.isVisible()) marker.setVisible(true);
            }
        }
    }
}
