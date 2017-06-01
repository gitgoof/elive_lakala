package com.lakala.elive.map.common;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.lakala.elive.map.bean.MyMarker;

import java.util.List;

/**
 * Created by xiaogu on 2017/3/14.
 */

public class MapUtil {
    /**
     * 设置地图的显示范围(所有点都包裹进去)
     *
     * @param baiduMap
     * @param markers
     */
    public static void setMapBunds(BaiduMap baiduMap, List<MyMarker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MyMarker marker : markers) {
            LatLng latLng = new LatLng(marker.getLat(), marker.getLng());
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds);
        baiduMap.setMapStatus(u);//设置显示范围
    }

    //定义一些常量
    static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @param bd_lon_o
     * @param bd_lat_o
     * @returns {*[]}
     */
    public static String bd09togcj02(double bd_lat_o, double bd_lon_o) {
        double bd_lon = +bd_lon_o;
        double bd_lat = +bd_lat_o;
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return gg_lat + "," + gg_lng;
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @param lng_o
     * @param lat_o
     * @returns {*[]}
     */
    public static LatLng gcj02tobd09(double lat_o, double lng_o) {
        double lat = +lat_o;
        double lng = +lng_o;
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new LatLng(bd_lat, bd_lng);
    }
}
