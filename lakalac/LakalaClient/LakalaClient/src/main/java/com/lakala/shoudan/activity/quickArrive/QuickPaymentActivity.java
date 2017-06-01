package com.lakala.shoudan.activity.quickArrive;

import android.os.Bundle;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

/**
 * 快捷支付服务协议
 * Created by WangCheng on 2016/8/26.
 */
public class QuickPaymentActivity extends AppBaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_payment);
        navigationBar.setTitle("快捷支付服务协议");
    }
}
