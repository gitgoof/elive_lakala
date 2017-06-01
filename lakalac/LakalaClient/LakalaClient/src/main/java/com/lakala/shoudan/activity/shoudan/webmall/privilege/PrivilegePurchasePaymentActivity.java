package com.lakala.shoudan.activity.shoudan.webmall.privilege;

import android.os.Bundle;
import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.webbusiness.WebTransInfo;
import com.loopj.lakala.http.RequestParams;

/**
 * 特权购买收款
 * @author ZhangMY
 */
public class PrivilegePurchasePaymentActivity extends NewCommandProtocolPaymentActivity {

    private WebTransInfo mPrivilegePurchaseTransInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrivilegePurchaseTransInfo = (WebTransInfo)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
    }


    @Override
    protected void addTransParams(RequestParams requestParams) {
        requestParams.put("busid",mPrivilegePurchaseTransInfo.getBusid());
        if(!TextUtils.isEmpty(mPrivilegePurchaseTransInfo.getBuynum())){
            requestParams.put("buynum", mPrivilegePurchaseTransInfo.getBuynum());
        }

        requestParams.put("msgdigest",mPrivilegePurchaseTransInfo.getMsgdigest());
        requestParams.put("ordtime", mPrivilegePurchaseTransInfo.getOrdtime());
        requestParams.put("ordexpdat",mPrivilegePurchaseTransInfo.getOrdexpdat());
        requestParams.put("type", "PR");
        requestParams.put("mercid", mPrivilegePurchaseTransInfo.getMercid());
        requestParams.put("ordno", mPrivilegePurchaseTransInfo.getOrderNo());
        requestParams.put("tdtm", Utils.createTdtm());
        requestParams.put("mobile", ApplicationEx.getInstance().getSession().getUser().getLoginName());
        requestParams.put("mobileno", ApplicationEx.getInstance().getSession().getUser().getLoginName());
        requestParams.put("Tips", "");
        requestParams.put("amount", Utils.yuan2Fen(mPrivilegePurchaseTransInfo.getAmount()));
    }
}
