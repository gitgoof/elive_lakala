package com.map.service;

/**
 * Created by xg on 2016/8/11.
 * 定位接口
 */
public interface ILocationService {
    void setLocateTime(int time);//设置定位时间

    void startLocate();//开始定位

    void stopLocate();//停止定位
}
