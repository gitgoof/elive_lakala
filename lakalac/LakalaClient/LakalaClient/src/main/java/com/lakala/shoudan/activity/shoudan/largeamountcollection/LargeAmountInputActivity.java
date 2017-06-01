package com.lakala.shoudan.activity.shoudan.largeamountcollection;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.activity.base.AmountInputActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by LMQ on 2015/12/4.
 */
public class LargeAmountInputActivity extends AmountInputActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationBar.setTitle("大额收款");
        showForceQpbocSwitch();
    }

    @Override
    protected void onInputComplete(BigDecimal amount) {
        getLimitAmount(amount);
    }

    private void getLimitAmount(final BigDecimal bgAmount) {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                try {
                    JSONObject jb = new JSONObject(resultServices.retData);
                    JSONObject jsonObject = jb.optJSONObject("BLIMIT");
                    if(jsonObject != null){
                        String limit = jsonObject.optString("LIMIT");
                        ApplicationEx.getInstance().getUser().setLargeAmountLimit(limit);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                toInfoCollectActivity(bgAmount);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toInfoCollectActivity(bgAmount);
            }
        };
        showProgressWithNoMsg();
        ShoudanService.getInstance().getLargeAmountLimit(callback);
    }

    private void toInfoCollectActivity(BigDecimal bgAmount) {
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.LargeAmount_Collection_PicRaise, context);
        if (!TextUtils.isEmpty(ApplicationEx.getInstance().getUser().getLargeAmountLimit())
                && (bgAmount.compareTo(new BigDecimal(
                ApplicationEx.getInstance().getUser().getLargeAmountLimit())) < 0)) {
            toast("单笔最小金额必须大于等于" + (ApplicationEx.getInstance().getUser().getLargeAmountLimit()) + "元");
            return;
        }
        LargeAmountTransInfo largeAmountTransInfo = new LargeAmountTransInfo();
        Intent intent = new Intent();
        largeAmountTransInfo
                .setAmount(new DecimalFormat("#0.00").format(bgAmount));
        intent.setClass(
                context,
                InformationCollectActivity.class
        );
        intent.putExtra(Constants.IntentKey.TRANS_INFO, largeAmountTransInfo);

        startActivity(intent);
    }
}
