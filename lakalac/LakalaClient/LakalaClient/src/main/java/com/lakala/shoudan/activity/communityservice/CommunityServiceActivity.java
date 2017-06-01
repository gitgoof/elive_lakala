package com.lakala.shoudan.activity.communityservice;

import android.content.Intent;
import android.os.Bundle;

import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.communityservice.balanceinquery.BalanceTransInfo;
import com.lakala.shoudan.activity.shoudan.records.RecordsQuerySelectionActivity;
import com.lakala.shoudan.activity.shoudan.records.TradeManageActivity;
import com.lakala.ui.component.NavigationBar;

import butterknife.OnClick;

/**
 * Created by HUASHO on 2015/1/16.
 * 便民服务
 */
public class CommunityServiceActivity extends AppBaseActivity{

    BusinessLauncher businessLauncher = BusinessLauncher.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_service);
        initUI();
    }

    protected void initUI(){
        navigationBar.setTitle(R.string.community_service);
        navigationBar.setActionBtnText("交易查询");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if(navBarItem == NavigationBar.NavigationBarItem.action){
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                            .Life_Transaction, context);
                    TradeManageActivity.queryDealType(context, RecordsQuerySelectionActivity.Type.LIFE_RECORD);
                }else if(navBarItem == NavigationBar.NavigationBarItem.back){
                    finish();
                }
            }
        });
    }

    /**
     * 信用卡还款
     */
    @OnClick(R.id.stv_credit_payment) void skipCreditPaymentActivity(){
//        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
//                .Credit_Card_Repayment, context);
        businessLauncher.start("creditcard_payment");
    }

    /**
     * 转账汇款
     */
    @OnClick(R.id.stv_transfer_remittance) void skipTransferRemittanceActivity(){
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                .Transfer_Remittance, context);
//        StatisticManager.getInstance().onEvent(StatisticType.Transfer_1,CommunityServiceActivity.this);
        businessLauncher.start("remittance");
    }

    /**
     * 手机充值
     */
    @OnClick(R.id.stv_telphone_payment) void skipTelephonePaymentActivity(){
//        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
//                .Phone_Recharge, context);
        businessLauncher.start("mobile_recharge");
    }

    /**
     * 余额查询
     */
    @OnClick(R.id.stv_balance_inquery) void skipBalanceInqueryActivity(){

        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                .Balance_Inquiry, context);
        Intent intent = new Intent();
        intent.putExtra(ConstKey.TRANS_INFO, new BalanceTransInfo());

        businessLauncher.start("balance_query", intent);
    }
}
