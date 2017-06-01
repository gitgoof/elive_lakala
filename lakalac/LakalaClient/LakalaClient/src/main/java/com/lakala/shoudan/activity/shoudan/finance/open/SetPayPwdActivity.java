package com.lakala.shoudan.activity.shoudan.finance.open;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.activity.BaseSafeManageActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundSignUpRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.SetPayPwdRequest;
import com.lakala.platform.statistic.ShoudanStatisticManager;

/**
 * Created by LMQ on 2015/10/9.
 */
public class SetPayPwdActivity extends BaseSafeManageActivity {

    private SetPayPwdRequest request;
    private FundSignUpRequest signUpRequest = null;
    public static void open(Context context,FundSignUpRequest signUpRequest){
        Intent intent = new Intent(context,SetPayPwdActivity.class);
        if(signUpRequest != null){
            intent.putExtra(Constants.IntentKey.TRANS_INFO, JSON.toJSONString(signUpRequest));
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String json = getIntent().getStringExtra(Constants.IntentKey.TRANS_INFO);
        signUpRequest = JSON.parseObject(json,FundSignUpRequest.class);
        request = new SetPayPwdRequest();
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("设置支付密码");
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = inputStack.size();
                if(size < 6){
                    ToastUtil.toast(context, "请输入6位数字作为支付密码");
                    return;
                }
                request.setTrsPassword(getString(inputStack));
                ConfirmPayPwdActivity.start(context,request, signUpRequest);
                finish();
            }
        });
    }
}
