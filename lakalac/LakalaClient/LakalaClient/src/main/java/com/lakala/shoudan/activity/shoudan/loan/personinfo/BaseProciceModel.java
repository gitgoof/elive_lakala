package com.lakala.shoudan.activity.shoudan.loan.personinfo;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.BaseView;
import com.lakala.shoudan.activity.shoudan.loan.RegionInfoAdapter;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RegionInfo;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/28 0028.
 */

public abstract class BaseProciceModel {
    private List<RegionInfo> proviceList = new ArrayList<RegionInfo>();//省

    private RegionInfo currentProvice;
    private RegionInfo currentCity;
    private RegionInfo currentDistic;

    private List<RegionInfo> cityList = new ArrayList<RegionInfo>();//市

    private List<RegionInfo> disticList = new ArrayList<RegionInfo>();//地区
    private boolean isDialogOpen;
    private RegionInfoAdapter cityAdapter;
    private RegionInfoAdapter disticAdapter;

    public boolean isDialogOpen() {
        return isDialogOpen;
    }

    public void setDialogOpen(boolean dialogOpen) {
        isDialogOpen = dialogOpen;
    }

    public RegionInfoAdapter getCityAdapter() {
        return cityAdapter;
    }

    public void setCityAdapter(RegionInfoAdapter cityAdapter) {
        this.cityAdapter = cityAdapter;
    }

    public RegionInfoAdapter getDisticAdapter() {
        return disticAdapter;
    }

    public void setDisticAdapter(RegionInfoAdapter disticAdapter) {
        this.disticAdapter = disticAdapter;
    }

    public RegionInfo getCurrentProvice() {
        return currentProvice;
    }

    public void setCurrentProvice(RegionInfo currentProvice) {
        this.currentProvice = currentProvice;
    }

    public RegionInfo getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(RegionInfo currentCity) {
        this.currentCity = currentCity;
    }

    public RegionInfo getCurrentDistic() {
        return currentDistic;
    }

    public void setCurrentDistic(RegionInfo currentDistic) {
        this.currentDistic = currentDistic;
    }

    public List<RegionInfo> getProviceList() {

        return proviceList;
    }

    public void setProviceList(List<RegionInfo> proviceList) {
        this.proviceList = proviceList;
    }

    public List<RegionInfo> getCityList() {
        return cityList;
    }

    public void setCityList(List<RegionInfo> cityList) {
        this.cityList = cityList;
    }

    public List<RegionInfo> getDisticList() {
        return disticList;
    }

    public void setDisticList(List<RegionInfo> disticList) {
        this.disticList = disticList;
    }


    public void getProvice(final BaseView view) {
        view.showLoading();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    String emptyMsg = "暂无省信息";
                    try {
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        List<RegionInfo> list = unpackRegions(jsonArray);
                        proviceList.clear();
                        if(list != null && list.size() != 0){
                            proviceList.addAll(list);
                            currentProvice = proviceList.get(0);
                            getCityListOfProvince(currentProvice,view);
                        }else{
                            view.showToast(emptyMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        view.showToast(emptyMsg);
                    }
                }else{
                    view.hideLoading();
                    view.showToast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                view.hideLoading();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getProvinceList(callback);
    }
    protected void getCityListOfProvince(RegionInfo provice, final BaseView view){
        if(provice == null){
            return;
        }
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONArray cityArray = new JSONArray(resultServices.retData);
                        List<RegionInfo> list = unpackRegions(cityArray);
                        cityList.clear();
                        if(list != null && list.size() != 0){
                            cityList.addAll(list);
                            currentCity = cityList.get(0);
                            getDistrictListOfCity(currentCity,view);
                            if(isDialogOpen && cityAdapter != null){
                                cityAdapter.notifyDataSetChanged();
                            }
                        }else {
                            view.hideLoading();
                            view.showToast("暂无市级信息");
                        }
                    } catch (Exception e) {
                        view.hideLoading();
                        e.printStackTrace();
                    }
                }else{
                    view.hideLoading();
                    view.showToast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                view.hideLoading();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getCityListOfProvince(provice.getpId(), callback);
    }
    public abstract void initDialog(BaseView view);
    protected void getDistrictListOfCity(RegionInfo city, final BaseView view){
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                view.hideLoading();
                if(resultServices.isRetCodeSuccess()){
                    List<RegionInfo> list = null;
                    try {
                        JSONArray disticsArray = new JSONArray(resultServices.retData);
                        list = unpackRegions(disticsArray);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    disticList.clear();
                    if(list != null && list.size() != 0){
                        disticList.addAll(list);
                        if(isDialogOpen && disticAdapter != null){
                            disticAdapter.notifyDataSetChanged();
                        }else {
                            initDialog(view);
                        }
                    }else{
                        view.showToast("暂无县、地区信息");
                    }
                }else{
                    view.showToast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                view.hideLoading();
                view.showToast( "网络请求失败");
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().getDistrictList(city.getcId(), callback);
    }
    private List<RegionInfo> unpackRegions(JSONArray jsonArray)throws Exception{
        List<RegionInfo> regions = new ArrayList<RegionInfo>();
        for(int i=0;i<jsonArray.length();i++){
            JSONObject jsonData = (JSONObject) jsonArray.get(i);
            RegionInfo region = new RegionInfo();
            region.setpNm(jsonData.optString("pNm"));
            region.setpId(jsonData.optString("pId"));
            region.setcId(jsonData.optString("cId"));
            region.setcNm(jsonData.optString("cNm"));
            region.setaId(jsonData.optString("aId"));
            region.setaNm(jsonData.optString("aNm"));
            regions.add(region);
        }
        return regions;
    }
}
