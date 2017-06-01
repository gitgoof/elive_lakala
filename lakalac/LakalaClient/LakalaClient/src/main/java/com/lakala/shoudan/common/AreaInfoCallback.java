package com.lakala.shoudan.common;

import android.util.Log;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.myaccount.Region;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/11/26.
 */
public abstract class AreaInfoCallback implements ServiceResultCallback {

    private final String type = "G_MT_AREA";

    public AreaInfoCallback() {

    }

    @Override
    public void onEvent(HttpConnectEvent connectEvent) {

    }

    @Override
    public void onSuccess(ResultServices resultServices) {
        List<Region> china = new ArrayList<Region>();
        try {
            JSONObject retData = new JSONObject(resultServices.retData);
            if (retData.has(type)) {
                JSONArray areaArray = retData.getJSONArray(type);
                for (int i = 0; i < areaArray.length(); i++) {
                    // province
                    JSONObject province = areaArray.getJSONObject(i);
                    String provinceName = province.getString("name");
                    String provinceCode = province.getString("code");

                    Region provinceChild = new Region();
                    if (province.has("children")) {
                        JSONArray cities = province.getJSONArray("children");
                        for (int j = 0; j < cities.length(); j++) {
                            JSONObject city = cities.getJSONObject(j);
                            String cityName = city.getString("name");
                            String cityCode = city.getString("code");
                            Region cityChild = new Region();
                            if (city.has("children")) {
                                JSONArray areas = city.getJSONArray("children");

                                for (int k = 0; k < areas.length(); k++) {
                                    JSONObject area = areas.getJSONObject(k);
                                    String areaName = area.getString("name");
                                    String areaCode = area.getString("code");
                                    Region areaRegion = new Region();
                                    areaRegion.setCode(areaCode);
                                    areaRegion.setName(areaName);
                                    cityChild.getChildren().add(areaRegion);
                                }
                            }
                            cityChild.setCode(cityCode);
                            cityChild.setName(cityName);
                            provinceChild.getChildren().add(cityChild);
                        }
                    }

                    provinceChild.setCode(provinceCode);
                    provinceChild.setName(provinceName);
                    china.add(provinceChild);
                }
            }
        } catch (Exception e) {
            if (Parameters.debug) {
                LogUtil.e(getClass().getName(), "Get Dict Failed", e);
            }
        }
        onSuccess(china);
    }
    public abstract void onSuccess(List<Region> area);
}
