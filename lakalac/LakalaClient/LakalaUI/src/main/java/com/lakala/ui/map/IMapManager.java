package com.lakala.ui.map;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Blues on 13-12-30.
 */
public interface IMapManager {

    /**
     *地图管理初始化操作
     * @param  context
     * @param  mapView          地图view
     * @param  isAutoGps       是否自动开启gps
     * */
    void init(Context context, View mapView, boolean isAutoGps);

    /**
     *显示当前位置
     */
    void enableMyLocation();

    /**
     *禁用当前位置显示
     */
    void disableMyLocation();

    /**
     *标注网点
     * @param    branchInfos      网点数据
     * */
    void showMapMakers(ArrayList<MapabcOverItem.BranchInfo> branchInfos);

    /**
     *清除标注的网点
     */
    void clearMapMarkers();
    
    /**
     * 搜索网点
     * @param query 搜索关键字
     */
    void searBranch(String query);

    /**
     *显示路线规划
     * @param  startPointLat        起点维度
     * @param  startPointLon        起点经度
     * @param  endPointLat          终点经度
     * @param  endPointLon          终点经度
     * */
    void findPIOLine(int startPointLat, int startPointLon, int endPointLat, int endPointLon);


}
