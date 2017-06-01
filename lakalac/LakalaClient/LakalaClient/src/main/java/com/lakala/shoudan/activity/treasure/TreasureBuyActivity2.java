package com.lakala.shoudan.activity.treasure;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lakala.library.encryption.Mac;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.password.SetPaymentPasswordActivity;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.BankCardAdditionActivity;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.shoudan.webmall.privilege.PrivilegePurchasePay2mentActivity;
import com.lakala.shoudan.activity.shoudan.webmall.privilege.PrivilegePurchaseTransInfo;
import com.lakala.shoudan.activity.shoudan.webmall.privilege.PrivilegePurchaseTransInfo2;
import com.lakala.shoudan.activity.wallet.WalletRechargeActivity;
import com.lakala.shoudan.activity.wallet.bean.RedPackageInfo;
import com.lakala.shoudan.activity.wallet.bean.WalletLimitInfo;
import com.lakala.shoudan.activity.wallet.bean.WalletPaymentTypes;
import com.lakala.shoudan.activity.wallet.request.GetComPrensiveInfoRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTradeRequest;
import com.lakala.shoudan.activity.webbusiness.WebMallContainerActivity;
import com.lakala.shoudan.activity.webbusiness.WebTransInfo;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.PayType;
import com.lakala.ui.component.LabelTextView;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LMQ on 2015/11/19.
 */
public class TreasureBuyActivity2 extends AppBaseActivity implements View.OnClickListener{
    LabelTextView tv_info,tv_num;
    TextView tv_content;
    TextView tv_ok;
    PrivilegePurchaseTransInfo2 privilegePurchaseTransInfo2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_buy2);
        privilegePurchaseTransInfo2 = (PrivilegePurchaseTransInfo2)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
        initUI();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("产品购买");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem){
                    case back:
                        onBackPressed();
                        break;
                }
            }
        });
        tv_info= (LabelTextView) findViewById(R.id.prodInfo);
        tv_num= (LabelTextView) findViewById(R.id.amountInfo);
        tv_ok= (TextView) findViewById(R.id.id_common_guide_button);
        tv_content= (TextView) findViewById(R.id.tv_content);
        tv_ok.setOnClickListener(this);
    }

    private void initData() {
        tv_info.setText(privilegePurchaseTransInfo2.getOrderInfo());
        DecimalFormat df=new DecimalFormat("######0.00");
        double dAmount=Double.valueOf(privilegePurchaseTransInfo2.getSwipeAmount());
        double ad=dAmount;
        tv_num.setText(privilegePurchaseTransInfo2.getAmount()+"元");
        tv_content.setText("刷卡付款");
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.id_common_guide_button:
                startActivity(new Intent(this, PrivilegePurchasePay2mentActivity.class)
                        .putExtra(Constants.IntentKey.TRANS_INFO,privilegePurchaseTransInfo2));
                break;
        }
    }
}
