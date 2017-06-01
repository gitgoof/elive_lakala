package com.lakala.shoudan.activity.shoudan.webmall;

import android.os.Bundle;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.payment.CommonPaymentActivity;
import com.lakala.shoudan.common.util.Util;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by More on 14-7-21.
 */
public class WebMallPaymentActivity extends CommonPaymentActivity {

    private WebMallTransInfo webMallTransInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webMallTransInfo = (WebMallTransInfo)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
    }

    @Override
    protected void startPayment(SwiperInfo swiperInfo) {
        queryBillInfo(swiperInfo);
    }

    /**
     * 查询订单信息
     */
    private void queryBillInfo(final SwiperInfo swiperInfo){

        BusinessRequest businessRequest = BusinessRequest.obtainRequest(WebMallPaymentActivity.this, "v1.0/trade", HttpRequest.RequestMethod.POST, true);

        RequestParams requestParams = businessRequest.getRequestParams();
        requestParams.put("busid","M50005");
        requestParams.put("mobile", ApplicationEx.getInstance().getSession().getUser().getLoginName());
        requestParams.put("mobileno", "");
        requestParams.put("amount", Utils.yuan2Fen(webMallTransInfo.getAmount()));
        requestParams.put("billno", webMallTransInfo.getfBillNo());

        businessRequest.setResponseHandler(new ServiceResultCallback(){
            @Override
            public void onSuccess(ResultServices resultServices) {
                if(resultServices.isRetCodeSuccess()){

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        webMallTransInfo.setQuerySid(jsonObject.getString("sid"));
                        webMallTransInfo.setPrice(Util.formatString(Util.fen2Yuan(jsonObject.getString("price"))));
                        pay(swiperInfo);
                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }


                }else {
                    if (null != resultServices.retMsg) {
                        ToastUtil.toast(WebMallPaymentActivity.this,resultServices.retMsg);
                    } else {
                        ToastUtil.toast(WebMallPaymentActivity.this,"订单验证失败");
                    }
                    finish();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

            }
        });

        businessRequest.execute();


    }

    private void pay(SwiperInfo swiperInfo){

        BusinessRequest businessRequest = BusinessRequest.obtainRequest(WebMallPaymentActivity.this, "v1.0/trade", HttpRequest.RequestMethod.POST, true);

        RequestParams requestParams = businessRequest.obtainTransRequestParams(swiperInfo);
        requestParams.put("busid","M20013");
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("fee", Utils.yuan2Fen("0"));
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("mobile", ApplicationEx.getInstance().getSession().getUser().getLoginName());
        requestParams.put("amount", Utils.yuan2Fen(webMallTransInfo.getPrice()));

        requestParams.put("srcsid", webMallTransInfo.getQuerySid());
        requestParams.put("billno", webMallTransInfo.getfBillNo());

        businessRequest.setResponseHandler(this);

        businessRequest.execute();


    }



}
