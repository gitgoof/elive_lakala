package com.lakala.shoudan.activity.shoudan.chargebusiness;

import android.content.Intent;
import android.os.Bundle;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.base.AmountInputActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.rentcollection.RentCollectionInfo;
import com.lakala.shoudan.activity.shoudan.rentcollection.RentCollectionPaymentActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by LMQ on 2015/12/4.
 * 平安收银宝
 */
public class RentCollectionAmountActivity extends AmountInputActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationBar.setTitle("特约商户缴费");
    }

    @Override
    protected void onInputComplete(BigDecimal bgAmount) {
        if(bgAmount.compareTo(new BigDecimal("5.01")) <0){
            toast("收款金额小于下限");
            return;
        }

        if(bgAmount.compareTo(new BigDecimal("100000")) >0){

            toast("收款金额大于上限");
            return;
        }

        queryRentFee(new DecimalFormat("#0.00").format(bgAmount));
    }

    private void queryRentFee(final String amount){

        ShoudanService instance = ShoudanService.getInstance();

        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                boolean toHideProgress = true;
                if(resultServices.isRetCodeSuccess()){
                    RentCollectionInfo rentCollectionInfo = new RentCollectionInfo();
                    try {
                        JSONArray jb = new JSONArray(resultServices.retData);
                        int length = jb == null ? 0 : jb.length();
                        for (int i = 0; i < length; i++) {
                            if ("1F2".equals(jb.getJSONObject(i).getString("busid"))) {
                                rentCollectionInfo.setInpan(jb.getJSONObject(i).getString("inpan"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if ("".equals(rentCollectionInfo.getInpan())) {
                        toast("未查询到开户账户");
                        return;
                    }
                    toHideProgress = false;
                    queryRentCollectionFee(rentCollectionInfo, amount);
                }else {
                    toast(resultServices.retMsg);
                }
                if(toHideProgress){
                    hideProgressDialog();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        showProgressWithNoMsg();
        instance.rentInpanQuery(callback);
    }

    private void queryRentCollectionFee(final RentCollectionInfo rentCollectionInfo, final String amount) {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    rentCollectionInfo.setAmount(amount);
                    try {
                        Intent intent = new Intent();
                        JSONObject feeInfo = new JSONObject(resultServices.retData);
                        rentCollectionInfo.unpackQueryResult(feeInfo);
                        intent.putExtra(Constants.IntentKey.TRANS_INFO, rentCollectionInfo);
                        intent.setClass(context, RentCollectionPaymentActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        toastInternetError();
                    }
                }else{
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance().queryRentCollectionFee(amount, rentCollectionInfo.getInpan(), callback);
    }
}
