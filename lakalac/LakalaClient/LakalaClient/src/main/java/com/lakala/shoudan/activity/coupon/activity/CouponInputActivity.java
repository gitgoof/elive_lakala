package com.lakala.shoudan.activity.coupon.activity;

import android.content.Intent;

import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.base.AmountInputActivity;
import com.lakala.shoudan.activity.coupon.params.CouponFeeRequest;
import com.lakala.shoudan.common.util.ServiceManagerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

/**
 * 代金券金额收款页
 * Created by huangjp on 2016/6/1.
 */
public class CouponInputActivity extends AmountInputActivity {
    @Override
    protected void onInputComplete(BigDecimal amount) {
        getFee(amount);
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.coupon_title);
    }

    /**
     * 代金券收款手续费
     */
    private void getFee(final BigDecimal amount){
        showProgressWithNoMsg();
        BusinessRequest request= RequestFactory.getRequest(context,RequestFactory.Type.COUPON_FEE);
        CouponFeeRequest params=new CouponFeeRequest(context);
        params.setAmount(amount.doubleValue());
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                JSONObject jsonObject;
                double fee=0.00;
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()){
                    try {
                        jsonObject=new JSONObject(resultServices.retData);
                        fee=jsonObject.optDouble("fee");
                        Intent intent=new Intent(CouponInputActivity.this,CouponCollectionsActivity.class);
                        intent.putExtra("amount",amount.doubleValue());
                        intent.putExtra("fee",fee);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toast(getString(R.string.socket_fail));
            }
        });
        ServiceManagerUtil.getInstance().start(params,request);
    }
}
