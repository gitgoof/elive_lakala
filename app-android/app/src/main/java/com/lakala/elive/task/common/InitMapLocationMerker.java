package com.lakala.elive.task.common;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.lakala.elive.Session;
import com.lakala.elive.common.net.ApiRequestListener;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.map.activity.MyShop;
import com.lakala.elive.map.bean.ShopLatLonAloneReque;
import com.lakala.elive.map.bean.ShopLatLonAloneRespon;
import com.lakala.elive.map.bean.ShopLatLonReque;
import com.lakala.elive.map.bean.ShopLatLonRespon;
import com.lakala.elive.map.common.ImageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gaofeng on 2017/5/26.
 */

public class InitMapLocationMerker implements OnGetGeoCoderResultListener,ApiRequestListener {
    private static final InitMapLocationMerker ourInstance = new InitMapLocationMerker();

    public static InitMapLocationMerker getInstance() {
        return ourInstance;
    }

    private InitMapLocationMerker() {
        initGeoCoder();
    }

    private void initGeoCoder(){
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
    }
    //百度地图位置编码
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private GeoCodeOption option;
    private BaiduMap mBaiduMap;

    /**
     * 发起搜索
     */
    public void searchButtonProcess(String city, String address) {
        option = new GeoCodeOption().city(city).address(address);
        // Geo搜索
        mSearch.geocode(option);
    }
    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {//地址获取经纬度
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
//        latitude = result.getLocation().latitude;
//        longitude = result.getLocation().longitude;
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {//经纬度获取地址
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
    }

    private final HashMap<String,String> mArrayMap = new HashMap<String,String>();
    private void toShowMarker(List<String> list){
        if(list == null || list.size()==0)return;
        List<String> needRequest = new ArrayList<String>();
        List<String> needShow = new ArrayList<String>();
        for(String id:list){
            String location = mArrayMap.get(id);
            if(location == null){
                needRequest.add(id);
            } else {
                // TODO 根据已有的经纬度去显示
                needShow.add(id);
            }
        }
        // TODO 更新地图显示
        if(needShow.size() != 0){
            toShowMerker(needShow);
        }
        if(needRequest.size()==0){
            if(mEndCallback!=null)mEndCallback.callback();
            return;
        }
        if(needRequest.size() == 1){
            getShopLatLonAloneRequest(needRequest.get(0));
        } else {
            toGetLocationById(needRequest);
        }
    }
    private void toGetLocationById(List<String> list){
        if(mActivity==null || mActivity.isFinishing())return;
        if(list == null || list.size() == 0){
            if(mEndCallback!=null)mEndCallback.callback();
            return;
        }
        ShopLatLonReque shopLatLonReque = new ShopLatLonReque();
        shopLatLonReque.setAuthToken(Session.get(mActivity).getUserLoginInfo().getAuthToken());
        shopLatLonReque.setShopIds(list);
        NetAPI.getShopLatLonRequest(mActivity,this,shopLatLonReque);
    }
    private void getShopLatLonAloneRequest(String id){
        if(mActivity==null || mActivity.isFinishing())return;
        if(TextUtils.isEmpty(id)){
            if(mEndCallback!=null)mEndCallback.callback();
            return;
        }
        ShopLatLonAloneReque shopLatLonReque = new ShopLatLonAloneReque();
        shopLatLonReque.setAuthToken(Session.get(mActivity).getUserLoginInfo().getAuthToken());
        shopLatLonReque.shopId=id;
        NetAPI.getShopLatLonAloneRequest(mActivity,this,shopLatLonReque);
    }

    @Override
    public void onSuccess(int method, Object obj) {
        if(obj == null)return;
        switch (method){
            case NetAPI.getShopLocationList:
                ShopLatLonRespon location = (ShopLatLonRespon) obj;
                if(location.getContent()==null || location.getContent().size() == 0)break;
                List<String> showIds = new ArrayList<String>();
                for(ShopLatLonRespon.ContentBean contentBean:location.getContent()){
                    String shopId = contentBean.getShopId();
                    if(TextUtils.isEmpty(shopId))continue;
                    ShopLatLonRespon.Geo geo = contentBean.getGeo();
                    if(geo == null)continue;
                    String type = geo.type;
                    if(TextUtils.isEmpty(type))continue;
                    if(geo.getCoordinates()==null || geo.getCoordinates().size()!=2)continue;
                    String longitude = geo.getCoordinates().get(0);
                    String latitude = geo.getCoordinates().get(1);
                    if(TextUtils.isEmpty(longitude)||TextUtils.isEmpty(latitude))continue;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(type);// 类型
                    stringBuilder.append(",");
                    stringBuilder.append(longitude);// 经度
                    stringBuilder.append(",");
                    stringBuilder.append(latitude);// 纬度
                    mArrayMap.put(shopId,stringBuilder.toString());
                    showIds.add(shopId);
                }
                // TODO 更新地图显示
                toShowMerker(showIds);
                if(mEndCallback!=null)mEndCallback.callback();
                break;
            case NetAPI.getShopLocation:
                ShopLatLonAloneRespon loca = (ShopLatLonAloneRespon) obj;
                if(loca==null || loca.getContent() == null)break;
                List<String> showIdsO = new ArrayList<String>();
                ShopLatLonAloneRespon.ContentBean contentBean = loca.getContent();
                String shopId = contentBean.getShopId();
                if(TextUtils.isEmpty(shopId))break;
                ShopLatLonAloneRespon.Geo geo = contentBean.getGeo();
                if(geo == null)break;
                String type = geo.type;
                if(TextUtils.isEmpty(type))break;
                if(geo.getCoordinates()==null || geo.getCoordinates().size()!=2)break;
                String longitude = geo.getCoordinates().get(0);
                String latitude = geo.getCoordinates().get(1);
                if(TextUtils.isEmpty(longitude)||TextUtils.isEmpty(latitude))break;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(type);// 类型
                stringBuilder.append(",");
                stringBuilder.append(longitude);// 经度
                stringBuilder.append(",");
                stringBuilder.append(latitude);// 纬度
                mArrayMap.put(shopId,stringBuilder.toString());
                showIdsO.add(shopId);
                // TODO 更新地图显示
                toShowMerker(showIdsO);
                if(mEndCallback!=null)mEndCallback.callback();
                break;
        }
    }
    public String getLatLontById(String shopNo){
        if(TextUtils.isEmpty(shopNo))return null;
        return mArrayMap.get(shopNo);
    }
    @Override
    public void onError(int method, String statusCode) {
        // TODO 不作处理
    }
    private Activity mActivity;
    public void setBaiduMap(BaiduMap baiduMap, Activity activity){
        mShopListMap.clear();
        mBaiduMap = baiduMap;
        mActivity= activity;
        mEndCallback = null;
    }
    public void desBaiduMap(){
        mShopListMap.clear();
        mBaiduMap=null;
        mActivity = null;
        mEndCallback = null;
    }
    private final HashMap<String,MyShop> mShopListMap = new HashMap<String,MyShop>();
    public boolean setToShowMerker(List<MyShop> list){
        if(mBaiduMap == null || mActivity == null)return false;
        // TODO 处理需要显示的店铺
        mShopListMap.clear();
        if(list==null||list.size()==0)return false;
        List<String> shopids = new ArrayList<String>();
        for(MyShop shop:list){
            if(shop == null)continue;
            String shopId = shop.shopNo;
            if(TextUtils.isEmpty(shopId))continue;
            String shopName = shop.name;
            if(TextUtils.isEmpty(shopName))continue;
            mShopListMap.put(shopId+"",shop);
            if(shopids.contains(shopId+""))continue;
            shopids.add(shopId+"");
        }
        toShowMarker(shopids);
        return true;
    }
    private void toShowMerker(List<String> showIds){
        if(mBaiduMap == null)return;
        if(mActivity == null)return;
        for(String id:showIds){
            MyShop myShop = mShopListMap.get(id);
            if(myShop == null)continue;
            String lat = mArrayMap.get(id);
            if(TextUtils.isEmpty(lat))continue;
            String[] types = lat.split(",");
            if(types.length!=3)continue;
            LatLng ll = new LatLng(Double.valueOf(types[2]), Double.valueOf(types[1]));

            BitmapDescriptor markerView = ImageUtil.getPointBitmapDes(mActivity,myShop);
            Bundle bundle = new Bundle();
            bundle.putSerializable("HOTEL", myShop);
            MarkerOptions markerOptions = new MarkerOptions().position(ll)
                    .icon(markerView).zIndex(myShop.index).draggable(true).extraInfo(bundle);
            mBaiduMap.addOverlay(markerOptions);
        }
    }
    public interface EndCallback{
        public void callback();
    }
    private EndCallback mEndCallback;

    public void setmEndCallback(EndCallback mEndCallback) {
        this.mEndCallback = mEndCallback;
    }
}
