package com.lakala.shoudan.activity.wallet.happybean;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

/**
 * Created by huangjp on 2016/5/17.
 */
public class HappyBeanDetailsActivity extends AppBaseActivity {
    private ListView lvHappyBeanDetails;
    private TextView tvAmount;
    private Button btnCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happybean_details);
        init();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("欢乐豆明细");
    }

    private void init(){
        initUI();
        lvHappyBeanDetails=(ListView)findViewById(R.id.lv_happy_bean_details);
        tvAmount=(TextView) findViewById(R.id.tv_amount);
        btnCharge=(Button) findViewById(R.id.btn_charge);
        btnCharge.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_charge:
                //TODO huangjp
                break;
        }
    }
}
