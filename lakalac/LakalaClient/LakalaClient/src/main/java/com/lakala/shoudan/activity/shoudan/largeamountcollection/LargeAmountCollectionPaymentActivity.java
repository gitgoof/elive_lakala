package com.lakala.shoudan.activity.shoudan.largeamountcollection;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.CommonPaymentActivity;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.ui.common.Dimension;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONObject;

/**
 * Created by More on 15/6/11.
 * 大额收款 支付页
 */
public class LargeAmountCollectionPaymentActivity extends NewCommandProtocolPaymentActivity {

    private LargeAmountTransInfo largeAmountTransInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        largeAmountTransInfo = (LargeAmountTransInfo)getSerializableTransInfo();

        View seperator = new View(this);
        LinearLayout.LayoutParams seperatorParams = new LinearLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, Dimension.dip2px(1, this));
        seperator.setBackgroundResource(R.drawable.lakala_divide_line);
        seperator.setLayoutParams(seperatorParams);
        confirmInfo.addView(seperator);

        TextView textView = new TextView(this);
        textView.setText("您所收款项将结算至您的收款账户");
        textView.setPadding(20, 10, 10, 0);
        textView.setTextColor(getResources().getColor(R.color.orange2));
        confirmInfo.addView(textView);


    }

    @Override
    protected void addTransParams(RequestParams requestParams) {
        requestParams.put("busid","X00001");
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("fee", Utils.yuan2Fen("0"));
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("mobile", ApplicationEx.getInstance().getSession().getUser().getLoginName());
        requestParams.put("issms", "1");
        requestParams.put("srcsid", largeAmountTransInfo.getInfoCollectSid());//照片上送sid
        requestParams.put("mobileno",  largeAmountTransInfo.getMobileNo());
        requestParams.put("Tips", "");
        requestParams.put("amount", Utils.yuan2Fen(largeAmountTransInfo.getAmount()));
    }

//    @Override
//    protected void startPayment(SwiperInfo swiperInfo) {
//
//        BusinessRequest businessRequest = BusinessRequest.obtainRequest(LargeAmountCollectionPaymentActivity.this, "v1.0/trade", HttpRequest.RequestMethod.POST, true);
//        RequestParams requestParams = businessRequest.obtainTransRequestParams(swiperInfo);
//        requestParams.put("busid","X00001");
//        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
//        requestParams.put("fee", Utils.yuan2Fen("0"));
//        requestParams.put("tdtm", Utils.createTdtm());
//        requestParams.put("mobile", ApplicationEx.getInstance().getSession().getUser().getLoginName());
//        requestParams.put("issms", "1");
//        requestParams.put("srcsid", largeAmountTransInfo.getInfoCollectSid());//照片上送sid
//        requestParams.put("mobileno",  largeAmountTransInfo.getMobileNo());
//        requestParams.put("Tips", "");
//        requestParams.put("amount", Utils.yuan2Fen(largeAmountTransInfo.getAmount()));
//
//        businessRequest.setResponseHandler(this);
//
//        businessRequest.execute();
//
//
//    }


    @Override
    protected void reStartSwipe() {
        finish();
    }
}
