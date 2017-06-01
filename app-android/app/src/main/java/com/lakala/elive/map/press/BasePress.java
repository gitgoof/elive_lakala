package com.lakala.elive.map.press;

import android.content.Context;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.lakala.elive.map.api.Api;
import com.lakala.elive.map.api.ApiService;
import com.lakala.elive.map.bean.MyMarker;
import com.lakala.elive.map.common.ImageUtil;
import com.lakala.elive.map.common.Params;


import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by xiaogu on 2017/3/13.
 */

public class BasePress {

    CompositeSubscription mCompositeSubscription;
    ApiService apiService = Api.createApiService();//所有的网络请求

    public void addSubscription(Observable observable, Observer observer) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer));
    }

    /**
     * 往地图上添加点
     *
     * @param baiduMap
     * @param myMarkers
     */
    public List<Overlay> addMarkerToMap(Context context, BaiduMap baiduMap, ArrayList<MyMarker> myMarkers) {
        ArrayList<OverlayOptions> markeroptions = new ArrayList<>();
        for (MyMarker myMarker : myMarkers) {

            Bundle bundle = new Bundle();
            bundle.putSerializable(Params.MYMARKER, myMarker);
            // 根据marker 获得View ，该View被封装到BitmapDescriptor中
            BitmapDescriptor bitmapDes = ImageUtil.getTextBitmapDes(context, myMarker);
            // marker 标记，options 选项
            MarkerOptions option = new MarkerOptions()
                    .position(new LatLng(myMarker.getLat(), myMarker.getLng()))// 设置每个点的经纬度
                    .icon(bitmapDes)// 每个点显示的信息
                    .extraInfo(bundle);
            markeroptions.add(option);
        }
        // 地图支持使用ArrayList<OverlayoutOptions> 来存储地图上的点
        return baiduMap.addOverlays(markeroptions);//添加大量点时最好用这种方法
    }
}
