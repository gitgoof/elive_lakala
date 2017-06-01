package com.lakala.shoudan.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.wallet.bean.WalletLimitInfo;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.NavigationBar;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fengx on 2015/11/19.
 */
public class WalletRechargeActivity extends AppBaseActivity{

    private EditText rechargeAmount;
    private TextView btnRecharge;
    private TextView hint;
//    private WalletLimitInfo limitInfo = new WalletLimitInfo();
    String msg;
    private boolean isAdJumpTo=false;
    private String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_recharge);
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
        rechargeAmount = (EditText) findViewById(R.id.recharge_amount);
        rechargeAmount.setOnFocusChangeListener(new HintFocusChangeListener());
        btnRecharge = (TextView) findViewById(R.id.btn_recharge);
        hint = (TextView) findViewById(R.id.hint);

        findViewById(R.id.ll_parent).requestFocus();
        msg =  getIntent().getStringExtra(Constants.IntentKey.WALLET_RECHARGE_LIMIT);
//        String walletRecharge=getIntent().getStringExtra(Constants.IntentKey.Wallet_Recharge);
//        if(TextUtils.isEmpty(walletRecharge)){
//            isAdJumpTo=true;
//        }
//        hint.setText(StringUtil.getWalletRechargeHint(limitInfo.getPerLimit(),limitInfo.getDailyLimitAmt()
//                ,limitInfo.getMonthlyLimitAmt()));
        msg=msg.replace("<br/>","\n");
        hint.setText(msg);
        addTextWatch();

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(rechargeAmount.getText().toString())) {
                    ToastUtil.toast(WalletRechargeActivity.this, "充值金额必须大于等于1元！");
                } else if (Double.valueOf(rechargeAmount.getText().toString()) < 1) {
                    ToastUtil.toast(WalletRechargeActivity.this, "充值金额必须大于等于1元！");
                } else {
                    Intent intent = new Intent(WalletRechargeActivity.this, WalletRechargeConfirmActivity.class);
                    intent.putExtra("amount", rechargeAmount.getText().toString());
                    intent.putExtra("balance",balance);
//                    if(!isAdJumpTo){
//                        intent.putExtra(Constants.IntentKey.Wallet_Recharge,"walletRecharge");
//                    }
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 只能输入两位小数
     */
    private void addTextWatch(){
        rechargeAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        rechargeAmount.setText(s);
                        rechargeAmount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    rechargeAmount.setText(s);
                    rechargeAmount.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        rechargeAmount.setText(s.subSequence(0, 1));
                        rechargeAmount.setSelection(1);
                        return;
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
