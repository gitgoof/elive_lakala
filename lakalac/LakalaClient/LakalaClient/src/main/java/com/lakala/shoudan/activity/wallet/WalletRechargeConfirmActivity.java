package com.lakala.shoudan.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.wallet.bean.RechargeBill;
import com.lakala.shoudan.activity.wallet.bean.RechargeTransInfo;
import com.lakala.shoudan.activity.wallet.request.WalletRechargeToBillRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fengx on 2015/11/19.
 */
public class WalletRechargeConfirmActivity extends AppBaseActivity {

    private TextView rechargeAmount;
    private TextView btnRecharge;
    //    private boolean isAdJumpTo=false;
    private String balance;
    private double amount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_recharge_confirm);
        balance = getIntent().getStringExtra("balance");
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();

        navigationBar.setTitle("零钱充值");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                }
            }
        });
        rechargeAmount = (TextView) findViewById(R.id.recharge_amount);
        btnRecharge = (TextView) findViewById(R.id.change_pay_confirm);

        String str = getIntent().getStringExtra("amount");
//        String walletRecharge=getIntent().getStringExtra(Constants.IntentKey.Wallet_Recharge);
//        if(TextUtils.isEmpty(walletRecharge)){
//            isAdJumpTo=true;
//        }
        if (str == null) {
            return;
        }
        amount = Double.valueOf(str);
        rechargeAmount.setText(Util.formatTwo(amount) + "元");

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toBill();
            }
        });
    }


    /**
     * 钱包充值生成账单
     */
    private void toBill() {

        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_RECHARGE_BILL);
        WalletRechargeToBillRequest params = new WalletRechargeToBillRequest(context);
        params.setAmount(amount);
        params.setBusid(WalletHomeActivity.WALLET_RECHARGE);
        params.setMobile(ApplicationEx.getInstance().getUser().getLoginName());
        params.setTransType("N");
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jobj = new JSONObject(resultServices.retData);
                        RechargeBill billInfo = new RechargeBill();
                        billInfo.setBillid(jobj.optString("billId"));
                        billInfo.setFee(jobj.optString("fee"));

                        Intent intent = new Intent(context, WalletPaymentActivity.class);

                        RechargeTransInfo transInfo = new RechargeTransInfo();
                        transInfo.setAmount(Util.formatTwo(amount));
                        intent.putExtra(Constants.IntentKey.TRANS_INFO, transInfo);
//                        if(!isAdJumpTo){
//                            intent.putExtra(Constants.IntentKey.Wallet_Recharge,"walletRecharge");
//                        }
                        intent.putExtra("balance", balance);
                        intent.putExtra("billinfo", billInfo);
                        startActivity(intent);
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_SwipingToCharge, context);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
        WalletServiceManager.getInstance().start(params, request);
    }
}
