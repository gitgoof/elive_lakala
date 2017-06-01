package com.lakala.shoudan.activity.shoudan.chargebusiness;

import android.content.Intent;
import android.os.Bundle;

import com.lakala.shoudan.activity.base.AmountInputActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.merchantpayment.MerchantPayTransInfo;
import com.lakala.shoudan.activity.shoudan.merchantpayment.MerchantPaymentConfirmActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by LMQ on 2015/12/4.
 */
public class ContributePaymentAmountInputActivity extends AmountInputActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigationBar.setTitle("特约商户缴费");
        tvTips.setText("缴费金额");
    }

    @Override
    protected void onInputComplete(BigDecimal bgAmount) {
        MerchantPayTransInfo merchantPayTransInfo = new MerchantPayTransInfo();
        merchantPayTransInfo.setAmount(new DecimalFormat("#0.00").format(bgAmount));
        Intent intent = new Intent();
        intent.putExtra(Constants.IntentKey.TRANS_INFO, merchantPayTransInfo);
        intent.setClass(context, MerchantPaymentConfirmActivity.class);
        startActivity(intent);
    }
}
