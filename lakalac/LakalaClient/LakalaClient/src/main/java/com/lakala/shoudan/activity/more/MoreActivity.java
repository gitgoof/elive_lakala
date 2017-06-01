package com.lakala.shoudan.activity.more;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.AppUpgradeController;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.ActiveNaviUtils;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.myaccount.AccountAboutLakalaActivity;
import com.lakala.shoudan.activity.payment.DeviceManagementActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.merchantpayment.MerchantPayQueryInstBillActivity;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.component.SingleLineTextView;

/**
 * 更多
 * Created by HJP on 2015/12/1.
 */
public class MoreActivity extends AppBaseActivity {
    /**
     * 设备管理
     */
    private SingleLineTextView deviceManagement;
    /**
     * 交易规则
     */
    private SingleLineTextView tradeRules;
    /**
     * 使用帮助
     */
    private SingleLineTextView idHelp;
    /**
     * 关注拉卡拉
     */
    private SingleLineTextView idWeibo;
    /**
     *手机收款宝招商加盟
     */
//    private SingleLineTextView joinShoukuanbao;
    /**
     * 购买手机收款宝
     */
    private SingleLineTextView buyShoukuanbao;
    /**
     * 检查更新
     */
    private SingleLineTextView idUpdate;
    /**
     * 关于我们
     */
    private SingleLineTextView idAboutUs;
    /**
     * 企业商户签约
     */
    private SingleLineTextView merchantSignatrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        init();
    }


    private void assignViews() {
        deviceManagement = (SingleLineTextView) findViewById(R.id.device_management);
        tradeRules = (SingleLineTextView) findViewById(R.id.trade_rules);
        idHelp = (SingleLineTextView) findViewById(R.id.id_help);
        idWeibo = (SingleLineTextView) findViewById(R.id.id_weibo);
//        joinShoukuanbao = (SingleLineTextView) findViewById(R.id.join_shoukuanbao);
//        buyShoukuanbao = (SingleLineTextView) findViewById(R.id.buy_shoukuanbao);
        idUpdate = (SingleLineTextView) findViewById(R.id.id_update);
        idAboutUs = (SingleLineTextView) findViewById(R.id.id_about_us);
        TextView set_exit_app= (TextView) findViewById(R.id.set_exit_app);
        SingleLineTextView passwordManagement=(SingleLineTextView) findViewById(R.id.password_management);
        merchantSignatrue = (SingleLineTextView) findViewById(R.id.open_merchant_signature_payment);
        deviceManagement.setOnClickListener(this);
        tradeRules.setOnClickListener(this);
        idHelp.setOnClickListener(this);
        idWeibo.setOnClickListener(this);
//        joinShoukuanbao.setOnClickListener(this);
//        buyShoukuanbao.setOnClickListener(this);
        idUpdate.setOnClickListener(this);
        idAboutUs.setOnClickListener(this);
        merchantSignatrue.setOnClickListener(this);
        passwordManagement.setOnClickListener(this);
        set_exit_app.setOnClickListener(this);
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle(R.string.more);
    }

    public void init() {
        initUI();
        assignViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trade_rules:
                ProtocalActivity.open(context, ProtocalType.TRANSACTION_RULES);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Transaction_Rules, context);
                break;
            case R.id.id_help:
                ProtocalActivity.open(context, ProtocalType.NIGHT_HELP);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Use_Help, context);
                break;
//            case R.id.buy_shoukuanbao://购买拉卡拉
//                ShoudanStatisticManager.getInstance()
//                        .onEvent(ShoudanStatisticManager.Buy_ShouKuanBao, context);
//                ActiveNaviUtils.start(context, ActiveNaviUtils.Type.GOU_MAI);
//                break;
            case R.id.device_management://设备管理
                startActivity(DeviceManagementActivity.class);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Device_Management, context);
                break;
            case R.id.id_weibo:// 关注拉卡拉微博
                ActiveNaviUtils.start(context, ActiveNaviUtils.Type.GUAN_ZHU);
                break;
            case R.id.id_update:// 检查更新
                checkAppUpdate();
                break;
            case R.id.id_about_us:// 关于我们
                startActivity(AccountAboutLakalaActivity.class);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Aboout_Us, context);
                break;
//            case R.id.join_shoukuanbao: {//招商加盟
//                ActiveNaviUtils.start(context, ActiveNaviUtils.Type.JIA_MENG);
//                break;
//            }
            case R.id.open_merchant_signature_payment:
                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.More_MerchantSign, this);
                showProgressWithNoMsg();
                TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onError(String msg) {
                        hideProgressDialog();
                        toastInternetError();
                        LogUtil.print(msg);
                    }

                    @Override
                    public void onSuccess() {
                        hideProgressDialog();
                        Intent intent = new Intent(MoreActivity.this, MerchantPayQueryInstBillActivity.class);
                        startActivity(intent);
                    }
                });
                break;

            case R.id.password_management://密码管理
                ActiveNaviUtils.start( context, ActiveNaviUtils.Type.AN_QUAN);
                break;
            case R.id.set_exit_app://退出
                logout();
                break;
        }


    }
    //退出
    private void logout() {

        DialogCreator.createFullContentDialog(context, "取消", "确定", getString(R.string.sure_cancellation_current_login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                LoginUtil.clearSession2Login(context);

            }
        }).show(getSupportFragmentManager());

    }

    /**
     * Activity 跳转
     *
     * @param targetActivity
     */
    protected void startActivity(Class targetActivity) {
        Intent intent = getIntent();
        intent.setClass(this, targetActivity);
        intent.putExtra(Constants.IntentKey.MODIFY_ACCOUNTINFO, true);
        startActivity(intent);
    }

    private AppUpgradeController appUpgradeController = AppUpgradeController.getInstance();

    /**
     * 检查更新
     */
    private void checkAppUpdate() {

        appUpgradeController.setCtx(this);
        appUpgradeController.check(true, true);

    }

}
