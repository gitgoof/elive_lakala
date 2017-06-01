package com.lakala.shoudan.activity.shoudan.webmall.privilege;

import android.os.Bundle;
import android.text.TextUtils;

import com.lakala.library.encryption.Mac;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPayment2Activity;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.webbusiness.WebTransInfo;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 特权购买收款
 * @author ZhangMY
 */
public class PrivilegePurchasePay2mentActivity extends NewCommandProtocolPayment2Activity {

    PrivilegePurchaseTransInfo2 privilegePurchaseTransInfo2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        privilegePurchaseTransInfo2 = (PrivilegePurchaseTransInfo2)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);

    }


    @Override
    protected void addTransParams(RequestParams requestParams) {
        requestParams.put("busid",privilegePurchaseTransInfo2.getBusid());//默认1CN
        requestParams.put("amount",privilegePurchaseTransInfo2.getAmount());//000000000001
        float amo=Float.valueOf(privilegePurchaseTransInfo2.getAmount());
        float amoun=amo*100;
        int amou=(int)amoun;
        String pattern="000000000000";
        java.text.DecimalFormat df = new java.text.DecimalFormat(pattern);
        requestParams.put("amount",df.format(amou));
        requestParams.put("fee","");
//        requestParams.put("series",Utils.createSeries());
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("mobile", ApplicationEx.getInstance().getSession().getUser().getLoginName());
        requestParams.put("userToken",ApplicationEx.getInstance().getSession().getUser().getAccessToken());
//        requestParams.put("mercid","");
        requestParams.put("ordno",privilegePurchaseTransInfo2.getOrderNo());
//        requestParams.put("ordno","T20160728105307921");
        requestParams.put("tips","MPOS活动");
        requestParams.put("type","PR");
        requestParams.put("acctname","");
        requestParams.put("idnumber","");
        requestParams.put("voutype","");
        requestParams.put("curcode","156");
        requestParams.put("famount","");
        requestParams.put("orderRnd", Mac.getRnd());
//        requestParams.put("chntype", BusinessRequest.CHN_TYPE);
//        requestParams.put("chncode", BusinessRequest.CHN_CODE);

//        requestParams.put("mobileno", ApplicationEx.getInstance().getSession().getUser().getLoginName());
    }
}
