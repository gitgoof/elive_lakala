package com.lakala.shoudan.activity.shoudan.records;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.AccountType;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.NavigationBar;

/**
 * 我要提款-提款确认页面
 */
public class DrawMoneySubmitActivity extends AppBaseActivity implements OnClickListener {

    private boolean isOne;
    private Button nextBtn;
    private TextView tv_tag1;
    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
        @Override
        public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
            if (navBarItem == NavigationBar.NavigationBarItem.back) {
                finish();
            } else if (navBarItem == NavigationBar.NavigationBarItem.action) {

                if (isOne) {
                    //设置埋点：
                    if(PublicEnum.Business.isDirection()){
                        //定向业务-一日贷
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.OneDayLoan_direction_inputmoney_submitfee_serviceDesc, DrawMoneySubmitActivity.this);
                   LogUtil.d(ShoudanStatisticManager.OneDayLoan_direction_inputmoney_submitfee_serviceDesc);
                    }
                    ProtocalActivity.open(context, ProtocalType.ONE_DAY_LOAN_NOTE);
                } else {
                    initPoint2();
                    ProtocalActivity.open(context, ProtocalType.D0_DESCRIPTION);
                }
            }
        }
    };
    private String mAmount;
    private String mFee;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_trade_draw_money_submit);
        Intent intent = getIntent();
        mAmount = intent.getStringExtra("amount");
        mFee = intent.getStringExtra("fee");
        isOne = intent.getBooleanExtra("isOne", false);
        initUI();

    }

    public void initPoint() {
        if (DoEnum.Do.isHomepage()) {//首页进入，成功提款
            LogUtil.print("ishome", "isHomepage");
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Public_Input_Free, context);
        } else if (DoEnum.Do.isCollectionPage()) {//收款页进入
            LogUtil.print("ishome", "isCollectionPage");
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Public_Input_Public_Free, context);
        } else if (!TextUtils.isEmpty(DoEnum.Do.getAdvertId())) {//广告进入
            LogUtil.print("ishome", "getAdvertId");
            String event = String.format(ShoudanStatisticManager.Do_Public_Input_Ad_Free, DoEnum.Do.getAdvertId());
            ShoudanStatisticManager.getInstance().onEvent(event, context);
        } else {//交易管理页进入
            LogUtil.print("ishome", "交易管理页进入");
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Public_Input_Record_Free, context);
        }
    }

    public void initPoint2() {
        if (DoEnum.Do.isHomepage()) {//首页进入，成功提款
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Public_Input_Free_Explain, context);
        } else if (DoEnum.Do.isCollectionPage()) {//收款页进入
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Public_Input_Public_Free_Explain, context);
        } else if (!TextUtils.isEmpty(DoEnum.Do.getAdvertId())) {//广告进入
            String event = String.format(ShoudanStatisticManager.Do_Public_Input_Ad_Free_Explain, DoEnum.Do.getAdvertId());
            ShoudanStatisticManager.getInstance().onEvent(event, context);
        } else {//交易管理页进入
            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Public_Input_Record_Free_Explain, context);
        }
    }

    protected void initUI() {
        nextBtn = (Button) findViewById(R.id.next_draw_money);
        tv_tag1 = (TextView) findViewById(R.id.tv_tag1);
        navigationBar.setTitle("立即提款");
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
        navigationBar.setActionBtnText("业务说明");
        nextBtn.setOnClickListener(this);
        TextView accNo = (TextView) findViewById(R.id.accNo);
        String accountNo = ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo();
        if (ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountType() == AccountType.PRIVATE)
            accNo.setText(Util.formatCardNumberWithStar(accountNo));
        else
            accNo.setText(Util.formatCompanyAccount(accountNo));
        TextView tvAmount = (TextView) findViewById(R.id.amount);
        TextView tvFee = (TextView) findViewById(R.id.fee);
        tvAmount.setText(mAmount);
        tvFee.setText(mFee);
        TextView tvAccName = (TextView) findViewById(R.id.accName);
        tvAccName.setText(ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName());
        if (isOne) {
            navigationBar.setTitle("一日贷");
            tv_tag1.setText("贷款金额");
            nextBtn.setText("确定");
        }else{
            initPoint();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_draw_money:
                getCash(mAmount);
                break;
            default:
                break;
        }
    }

    /**
     * 提款结束提示
     */
    private void getCashOverTip() {
        DialogCreator.createOneConfirmButtonDialog(
                context, "确定", "系统正在为您划款，请及时关注您的账户资金变化", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        callMainActivity();
                    }
                }
        ).show();
    }

    /**
     * 确认提款
     *
     * @param amount 提款金额
     */
    private void getCash(final String amount) {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                analyzeGetCashResult(resultServices);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        CommonServiceManager
                .getInstance().getT0Cash(amount, callback);
    }

    private void analyzeGetCashResult(final ResultServices result) {
        if (result.isRetCodeSuccess()) {
            getCashOverTip();
//            if(DoEnum.Do.isHomepage()){//首页进入，成功提款
//                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Success_Homepage,context);
//            }
//            else if(DoEnum.Do.isCollectionPage()){//收款页进入
//                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Success,context);
//            }
//            else if(!TextUtils.isEmpty(DoEnum.Do.getAdvertId())){//广告进入
//                String event = String.format(ShoudanStatisticManager.Advert_Do_Success, DoEnum.Do.getAdvertId());
//                ShoudanStatisticManager.getInstance().onEvent(event,context);
//            }else{//交易管理页进入
//                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Success_Transaction_Management,context);
//            }
        } else {
            LogUtil.e(DrawMoneySubmitActivity.this.getLocalClassName(), result.retMsg);
            ToastUtil.toast(context, result.retMsg);
        }
    }
}
