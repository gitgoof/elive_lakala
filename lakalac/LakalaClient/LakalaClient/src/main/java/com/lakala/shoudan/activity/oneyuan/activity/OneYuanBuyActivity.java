package com.lakala.shoudan.activity.oneyuan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.wallet.WalletRechargeActivity;
import com.lakala.shoudan.activity.wallet.bean.WalletInfo;
import com.lakala.shoudan.activity.wallet.bean.WalletLimitInfo;
import com.lakala.shoudan.activity.wallet.request.GetComPrensiveInfoRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.PayType;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxuan on 2016/5/18.
 */
public class OneYuanBuyActivity extends AppBaseActivity implements View.OnClickListener{

    private TextView beansNum;
    private TextView payType;
    private TextView walletRecharge;
    private TextView nextStep;
    private TextView chooseRedPack;

    private List<PayType> payTypes = new ArrayList<PayType>();
    private String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_yuan_buy);
        initUI();
        initData();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("欢乐豆支付");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    finish();
                }
            }
        });

        beansNum = (TextView) findViewById(R.id.beans_num);
        payType = (TextView) findViewById(R.id.tv_pay_type);
        nextStep = (TextView) findViewById(R.id.next_step);
        walletRecharge = (TextView) findViewById(R.id.wallet_recharge);
        chooseRedPack = (TextView) findViewById(R.id.tv_red_package);

        payType.setOnClickListener(this);
        walletRecharge.setOnClickListener(this);
        chooseRedPack.setOnClickListener(this);
        nextStep.setOnClickListener(this);

    }
    private void initData(){

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_pay_type:

                break;

            case R.id.next_step:

                walletBuy();
                swiperBuy();
                newCardBuy();
                break;
            case R.id.wallet_recharge:
                getComprensiveInfo();
                break;

            case R.id.tv_red_package:

                break;
        }
    }

    /**
     *
     * 获取用户综合安全信息
     */
    private void getComprensiveInfo(){

        BusinessRequest businessRequest= RequestFactory.getRequest(context, RequestFactory.Type.COMPREHENSIVE_INFORMATION);
        GetComPrensiveInfoRequest params = new GetComPrensiveInfoRequest(context);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);

                        balance = jsonObject.optString("walletBalance");
                        queryRechargeLimit();

                    } catch (Exception e) {
                        LogUtil.print("wallet json解析异常");
                        e.printStackTrace();
                    }

                } else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
                toastInternetError();
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }

    /**
     * 查询充值限额
     */
    private void queryRechargeLimit() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_RECHARGE_LIMIT_QRY);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jobj = new JSONObject(resultServices.retData);
                        Intent intent = new Intent(context, WalletRechargeActivity.class);
                        intent.putExtra(Constants.IntentKey.WALLET_RECHARGE_LIMIT, jobj.optString("appMsg"));
                        intent.putExtra("balance", balance);
                        startActivity(intent);
//                        JSONArray jarr = jobj.getJSONArray("list");
//                        for (int i = 0; i < jarr.length(); i++) {
//                            JSONObject json = jarr.getJSONObject(i);
//                            String type = json.optString("type");
//                            //如果是储蓄卡
//                            if (type.equalsIgnoreCase("CX")) {
//                                WalletLimitInfo limitInfo = new WalletLimitInfo();
//                                limitInfo.setType(type);
//                                limitInfo.setPerLimit(json.optDouble("perLimit", 0));
//                                limitInfo.setDailyLimitAmt(json.optDouble("dailyLimitAmt", 0));
//                                limitInfo.setMonthlyLimitAmt(json.optDouble("monthlyLimitAmt", 0));
//                                Intent intent = new Intent(context, WalletRechargeActivity.class);
//                                intent.putExtra(Constants.IntentKey.WALLET_RECHARGE_LIMIT, limitInfo);
//                                intent.putExtra("balance", balance);
//                                startActivity(intent);
//                            }
//                        }
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
        WalletServiceManager.getInstance().start(context, request);
    }

    private void showChooseDialog(){

        DialogCreator.showPayTypeChooseDialog(this, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //TODO 选择支付方式
            }
        },payTypes);
    }

    /**
     * 零钱购买
     */
    private void walletBuy(){

    }

    /**
     * 刷卡支付
     */
    private void swiperBuy(){

    }

    /**
     * 新卡支付
     */
    private void newCardBuy(){


    }
}
