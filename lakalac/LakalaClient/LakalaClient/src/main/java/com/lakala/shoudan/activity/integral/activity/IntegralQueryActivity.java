package com.lakala.shoudan.activity.integral.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;

/**
 * Created by fengxuan on 2016/5/17.
 */
public class IntegralQueryActivity extends AppBaseActivity implements View.OnClickListener{

    private TextView query;
    private EditText balance;
    private TextView exchangeVoucher;
    private TextView voucherDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_query);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        query = (TextView) findViewById(R.id.query_integral);
        balance = (EditText) findViewById(R.id.et_integral_balance);
        exchangeVoucher = (TextView) findViewById(R.id.can_exchange_voucher);
        voucherDetails = (TextView) findViewById(R.id.voucher_details);

        query.setOnClickListener(this);
        exchangeVoucher.setOnClickListener(this);
        voucherDetails.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.query_integral:
                Uri uri = Uri.parse("smsto://10658999");
                Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
                intent.putExtra("sms_body","JF");
                startActivity(intent);
                break;
            case R.id.can_exchange_voucher:
                break;
            case R.id.voucher_details:
                //TODO
                String title = "转账服务协议";
                String url = "https://download.lakala.com/lklmbl/html/sht/sht_trans.html";
                ProtocalActivity.open(context, title, url);
                break;
        }
    }
}
