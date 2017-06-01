package com.lakala.shoudan.activity.shoudan.finance;

import android.content.Intent;

import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;

/**
 * Created by LMQ on 2015/11/23.
 */
public class BaseFinanceActivity extends AppBaseActivity {
    protected void startTranTimeout(String errMsg,BaseTransInfo transInfo){
        hideProgressDialog();
        transInfo.setMsg(errMsg);
        transInfo.setTransResult(TransResult.TIMEOUT);
        Intent intent = new Intent(context, ConfirmResultActivity.class);
        startActivity(intent);
    }
}
