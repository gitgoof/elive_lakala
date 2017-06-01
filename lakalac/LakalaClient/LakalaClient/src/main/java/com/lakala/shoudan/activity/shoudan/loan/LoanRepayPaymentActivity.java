package com.lakala.shoudan.activity.shoudan.loan;

import android.content.Intent;
import android.os.Bundle;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.payment.CommonPaymentActivity;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.platform.common.MutexThreadManager;
import com.lakala.shoudan.common.util.Util;

import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanRepayTransInfo;
import com.loopj.lakala.http.RequestParams;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * 替你还, 还借款
 *
 * Created by More on 14/11/26.
 */
public class LoanRepayPaymentActivity extends NewCommandProtocolPaymentActivity {


    private LoanRepayTransInfo loanRepayTransInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loanRepayTransInfo = (LoanRepayTransInfo)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
    }

    @Override
    protected void addTransParams(RequestParams requestParams) {
        requestParams.put("busid","1EH");
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("amount", Utils.yuan2Fen(loanRepayTransInfo.getSwipeAmount()));
        requestParams.put("fee", "000000000000");
        requestParams.put("tdtm",Util.dateForWallet());
        requestParams.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        requestParams.put("issms", "1");
        requestParams.put("mobileno", "");
        requestParams.put("tips","");
        requestParams.put("curcode", "156");
        requestParams.put("creditno", loanRepayTransInfo.getContractNo());
        requestParams.put("type", "PR");
    }
}



