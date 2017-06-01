package com.lakala.shoudan.activity.oneyuan.activity;

import android.os.Bundle;

import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.loopj.lakala.http.RequestParams;

/**
 * Created by fengxuan on 2016/5/18.
 */
public class OneYuanPaymentActivity extends NewCommandProtocolPaymentActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void startPayment(SwiperInfo swiperInfo) {
        super.startPayment(swiperInfo);
    }

    @Override
    protected void addTransParams(RequestParams requestParams) {

    }
}
