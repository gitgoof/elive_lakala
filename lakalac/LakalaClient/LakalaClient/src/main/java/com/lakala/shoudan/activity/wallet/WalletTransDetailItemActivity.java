package com.lakala.shoudan.activity.wallet;

import android.os.Bundle;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.wallet.bean.TransDetail;
import com.lakala.ui.component.NavigationBar;

/**
 * Created by fengx on 2015/12/7.
 */
public class WalletTransDetailItemActivity extends AppBaseActivity{

    private TextView type;
    private TextView amount;
    private TextView balance;
    private TextView status;
    private TextView time;
    private TextView token;
    private TransDetail transDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_detail_item);
        transDetail = (TransDetail) getIntent().getSerializableExtra("detail");
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("交易详情");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        finish();
                        break;
                }
            }
        });

        type = (TextView) findViewById(R.id.wallet_trans_type);
        amount = (TextView) findViewById(R.id.wallet_trans_amount);
        balance = (TextView) findViewById(R.id.wallet_balance);
        status = (TextView) findViewById(R.id.wallet_trans_status);
        time = (TextView) findViewById(R.id.wallet_trans_time);
        token = (TextView) findViewById(R.id.wallet_trans_token);

        type.setText(transDetail.getTransName());
        amount.setText(transDetail.getTranAmount());
        balance.setText(transDetail.getWalletBalance());
        status.setText(transDetail.getTransRetdesc());
        time.setText(transDetail.getTransDate() + " " + transDetail.getTransTime());
        token.setText(transDetail.getTransRes());
    }
}
