package com.map.uipresenter;

import android.content.Context;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.map.api.Api;
import com.map.api.ApiService;
import com.map.bean.MyMarker;
import com.map.common.Params;
import com.map.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by xiaogu on 2017/3/13.
 */

public class BasePress {
    ApiService apiService = Api.createApiService();//所有的网络请求
    CompositeSubscription mCompositeSubscription;

/*    但是，如果有很多个数据源，那岂不是要取消很多次？当然不是的，可以利用 CompositeSubscription， 相当于一个 Subscription 集合。
    CompositeSubscription list = new CompositeSubscription();
    list.add(subscription1);
    list.add(subscription2);
    list.add(subscription3);
    统一调用一次unsubscribe，就可以把所有的订阅都取消
    list.unsubscribe();*/

    //RXjava取消注册，以避免内存泄露
    public void onUnsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }


    public void addSubscription(Observable observable, Observer observer) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer));
    }

    public void addSubscription(Observable observable, Action1 action1) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1));
    }

    /**
     * 往地图上添加点
     *
     * @param baiduMap
     * @param myMarkers
     */
    public List<Overlay> addMarkerToMap(Context context, BaiduMap baiduMap, ArrayList<MyMarker> myMarkers) {
        // TODO 点的列表集合
        ArrayList<OverlayOptions> markeroptions = new ArrayList<>();
        for (MyMarker myMarker : myMarkers) {
            // TODO 循环自定义的 Marker
            Bundle bundle = new Bundle();
            bundle.putSerializable(Params.MYMARKER, myMarker);
            // TODO 根据marker 获得View ，该View被封装到BitmapDescriptor中
            BitmapDescriptor bitmapDes = ImageUtil.getTextBitmapDes(context, myMarker);
            // TODO marker 标记，options 选项
            MarkerOptions option = new MarkerOptions()
                    .position(new LatLng(myMarker.getLat(), myMarker.getLng()))// TODO 设置每个点的经纬度
                    .icon(bitmapDes)// TODO 每个点显示的信息,或者每个点显示的图像
                    .extraInfo(bundle);// TODO 把这个信息存储到 MarkerOptions 中
            markeroptions.add(option);
        }
        // overlay ： 意思是：覆盖图
        // 地图支持使用ArrayList<OverlayoutOptions> 来存储地图上的点
        return baiduMap.addOverlays(markeroptions);//添加大量点时最好用这种方法
        // 如果单个的加图可以用: addOverlay(*);
    }
}
