package com.lakala.shoudan.activity.happybean;

import android.content.Context;
import android.content.Intent;

import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.happybean.request.RechargeBankTypeRequest;
import com.lakala.shoudan.common.util.ServiceManagerUtil;

/**
 * Created by huangjp on 2016/5/18.
 */
public class HappyBeanSMSCheckActivity extends BaseSMSCheckActivity {

    private  RechargeBankTypeRequest parmas;
    public static void open(Context context, String data, String pwd){
        Intent intent = new Intent(context,HappyBeanSMSCheckActivity.class);
        intent.putExtra("data",data);
        intent.putExtra("pwd",pwd);
        context.startActivity(intent);
    }

    @Override
    void initPreData() {
        parmas=new RechargeBankTypeRequest(context);
    }

    @Override
    void getCaptcha() {
        BusinessRequest request = HappyBeanRequestFactory
                .getRequest(context, HappyBeanRequestFactory.HappyBeanType.CHECK_PAY_PWD);
        showProgressWithNoMsg();
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()){
                    tvGetCaptcha.startCaptchaDown(60);
                    //TODO huangjp
                }else{
                    tvGetCaptcha.resetCaptchaDown();
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

            }
        });
        ServiceManagerUtil.getInstance().start(parmas,request);
    }

    @Override
    void trade() {

    }


}
