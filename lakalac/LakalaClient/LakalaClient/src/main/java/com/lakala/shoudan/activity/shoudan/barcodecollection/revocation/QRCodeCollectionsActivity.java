package com.lakala.shoudan.activity.shoudan.barcodecollection.revocation;

import android.os.Bundle;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

/**
 * Created by huangjp on 2016/5/31.
 */
public class QRCodeCollectionsActivity extends AppBaseActivity{
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("二维码收款");
        navigationBar.setActionBtnText("帮助");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr_code);
    }
}
