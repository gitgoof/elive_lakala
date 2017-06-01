package com.lakala.shoudan.activity.integral.callback;

import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.datadefine.Voucher;
import com.lakala.shoudan.datadefine.VoucherList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linmq on 2016/6/13.
 */
public abstract class VoucherListCallback implements ServiceResultCallback {
    @Override
    public void onSuccess(ResultServices resultServices) {
        VoucherList voucherList = new VoucherList();
        List<Voucher> vouchers = new ArrayList<Voucher>();
        voucherList.setVouchers(vouchers);
        if(resultServices.isRetCodeSuccess()){
            try {
                JSONObject jsonObject = new JSONObject(resultServices.retData);
                String sumPrice = jsonObject.optString("sum_price");
                voucherList.setSum_price(sumPrice);
                String sumCount = jsonObject.optString("sum_count");
                voucherList.setSum_count(sumCount);
                String name = "vouchers";
                if(jsonObject.has(name)){
                    JSONArray voucherArray = jsonObject.optJSONArray(name);
                    if(voucherArray == null){
                        return;
                    }
                    int length = voucherArray.length();
                    for(int i = 0;i<length;i++){
                        JSONObject voucherJson = voucherArray.optJSONObject(i);
                        if(voucherJson == null){
                            continue;
                        }
                        Voucher voucher = Voucher.obtain(voucherJson);
                        vouchers.add(voucher);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        onResult(resultServices,voucherList);
    }
    public abstract void onResult(ResultServices resultServices, VoucherList voucherList);
}
