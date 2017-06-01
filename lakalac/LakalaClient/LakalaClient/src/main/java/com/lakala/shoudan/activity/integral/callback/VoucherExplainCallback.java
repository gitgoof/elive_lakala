package com.lakala.shoudan.activity.integral.callback;

import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.datadefine.VoucherExplain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linmq on 2016/6/13.
 */
public abstract class VoucherExplainCallback implements ServiceResultCallback {
    @Override
    public void onSuccess(ResultServices resultServices) {
        List<VoucherExplain> explainList = new ArrayList<VoucherExplain>();
        if(resultServices.isRetCodeSuccess()){
            try {
                JSONObject jsonObject = new JSONObject(resultServices.retData);
                String name = "voucherExplain";
                if(jsonObject.has(name)){
                    JSONArray explainArray = jsonObject.optJSONArray(name);
                    if(explainArray != null){
                        int length = explainArray.length();
                        for(int i = 0;i<length;i++){
                            JSONObject explainJson = explainArray.optJSONObject(i);
                            if(explainJson == null){
                                continue;
                            }
                            explainList.add(VoucherExplain.obtain(explainJson));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        onResult(resultServices,explainList);
    }

    public abstract void onResult(ResultServices resultServices, List<VoucherExplain> explainList);
}
