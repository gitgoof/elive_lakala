package com.lakala.shoudan.activity.wallet.happybean;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.ui.module.holographlibrary.Line;

/**
 * Created by huangjp on 2016/5/17.
 */
public class HappyBeanRechargeActivity extends AppBaseActivity {
    private Button btnPay;
    private LinearLayout llReceiver;
    private LinearLayout llRechargeType;
    private TextView tvAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happybean_recharge);
        init();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("欢乐豆充值");
    }
    private void init(){
        initUI();
        btnPay=(Button)findViewById(R.id.btn_pay);
        llReceiver=(LinearLayout)findViewById(R.id.ll_receiver);
        llRechargeType=(LinearLayout)findViewById(R.id.ll_recharge_type);
        tvAmount=(TextView)findViewById(R.id.tv_amount);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }
}
