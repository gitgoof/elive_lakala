package com.lakala.shoudan.activity.payment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.TimeCounter;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.collection.CollectionsAmountInputActivity;
import com.lakala.shoudan.activity.communityservice.balanceinquery.BalanceInqueryActivity;
import com.lakala.shoudan.activity.communityservice.creditpayment.CreditFormInputActivity;
import com.lakala.shoudan.activity.communityservice.phonerecharge.RechargeSelectAmountActivity;
import com.lakala.shoudan.activity.communityservice.transferremittance.TransferRemittanceActivity;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.chargebusiness.ContributePaymentAmountInputActivity;
import com.lakala.shoudan.activity.shoudan.finance.FrontPageActivity;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.finance.trade.FundTranserInActivity;
import com.lakala.shoudan.activity.shoudan.finance.trade.WithdrawalActivity;
import com.lakala.shoudan.activity.shoudan.loan.LoanHistoryActivity;
import com.lakala.shoudan.activity.shoudan.records.RecordsQuerySelectionActivity;
import com.lakala.shoudan.activity.shoudan.records.TradeManageActivity;
import com.lakala.shoudan.activity.treasure.TreasureTransInfo;
import com.lakala.shoudan.activity.wallet.WalletHomeActivity;
import com.lakala.shoudan.activity.wallet.WalletRechargeConfirmActivity;
import com.lakala.shoudan.activity.wallet.WalletTransDetailActivity;
import com.lakala.shoudan.activity.wallet.WalletTransferActivity;
import com.lakala.shoudan.activity.wallet.bean.RechargeTransInfo;
import com.lakala.shoudan.activity.wallet.bean.WithdrawTransInfo;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTransDetailRequest;
import com.lakala.shoudan.activity.webbusiness.WebMallContainerActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.component.VerticalListView;

import org.json.JSONObject;

/**
 * Created by More on 15/1/22.
 */
public class ConfirmResultActivity extends AppBaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_result_activity);
        initUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
        TimeCounter.getInstance().may2Gesture(this);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        TimeCounter.getInstance().save2BackgroundTime(this);
    }

    private BaseTransInfo transInfo;

    protected void initUI() {

        transInfo = (BaseTransInfo)getIntent().getSerializableExtra(ConstKey.TRANS_INFO);

        //统计交易结果
        String statisicResult = transInfo.getStatisticTransResult();
        try {
            PublicEnum.Business.Close();
        }catch (Exception x){}

        VerticalListView resultList = (VerticalListView)findViewById(R.id.vertical_result_list);
        resultList.addList(this, transInfo.getResultInfo(), false, getResources().getColor(R.color.white));
        TextView resultTitle = (TextView)findViewById(R.id.result_title);
        navigationBar.setBackBtnVisibility(View.GONE);

        navigationBar.setTitle(transInfo.getTransTypeName());
        initResultButtons(transInfo.getTransResult());
        //initTips()
        ImageView resultIcon = (ImageView)findViewById(R.id.result_icon);
        if(transInfo.getTransResult() == TransResult.SUCCESS){

            //TODO full fill all kinds transaction
            if(!TextUtils.isEmpty(statisicResult)){
                ShoudanStatisticManager.getInstance().onEvent(statisicResult, context);
            }

            //设置交易图标为成功,加载签名
            resultIcon.setImageDrawable(getResources().getDrawable(R.drawable.success));
            resultTitle.setText(transInfo.getTransTypeName() + getString(R.string.success));
            resultTitle.setTextColor(getResources().getColor(R.color.gray_666666));
            switch (transInfo.getType()){
                case WebMall:
                case PaymentForCall:
                    resultTitle.setText("付款成功");
                    break;
                case PRIVILEGEPURCHASE:
                    resultTitle.setText("交易成功");
                    break;
                case WITHDRAW://理财取出
                    resultTitle.setText("取出成功");
                    break;
                case APPLY:
                    resultTitle.setText("购买成功");
                    break;
                case WALLET_TRANSFER://钱包转出
                    resultTitle.setText("转出成功");
                    break;
//                case HAPPYBEAN:
                case WALLET_RECHARGE://钱包零钱充值
                    resultTitle.setText("交易成功");
                    break;
            }
//            //钱包零钱充值
//            if(transInfo instanceof RechargeTransInfo){
//                RechargeTransInfo transInfo = (RechargeTransInfo) this.transInfo;
//                if(transInfo.isAdJumpTo()){
//                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Advert_Recharge_Success, context);
//                }else{
//                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Recharge_Success, context);
//
//                }
//            }
//            if(transInfo instanceof WithdrawTransInfo){
//                WithdrawTransInfo transInfo = (WithdrawTransInfo) this.transInfo;
//                if(transInfo.isAdJumpTo()){
//                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Advert_Wallet_Withdraw_Success, context);
//                }else{
//                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Withdraw_Success, context);
//
//                }
//            }
        }else if(transInfo.getTransResult() == TransResult.FAILED) {
            resultTitle.setText(getString(R.string.transacton_fail));
            resultIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_fail));
            findViewById(R.id.error).setVisibility(View.VISIBLE);
            TextView error = (TextView)findViewById(R.id.err_ditail);
            error.setText(transInfo.getMsg());
        }else{
            //设置交易图标为未知, 隐藏签名,展示错误提示
            resultIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_query));
            findViewById(R.id.error).setVisibility(View.VISIBLE);
            resultTitle.setText(getString(R.string.transaction_exception));
        }
//        signatureLayout = findViewById(R.id.signature_layout);
//        if(transInfo instanceof RevocationTransinfo && transInfo.getTransResult()==TransResult.SUCCESS){
//            signatureLayout.setVisibility(View.VISIBLE);
//        }
//        if (transInfo instanceof CollectionTransInfo && transInfo.getTransResult()==TransResult.SUCCESS){
//            signatureLayout.setVisibility(View.VISIBLE);
//        }else{
//            signatureLayout.setVisibility(View.GONE);
//        }
    }

    private void backToMain(){
        //返回首页
        startActivity(new Intent(this, MainActivity.class));

    }

    /**
     * 返回理财首页
     * @param refresh 是否刷新理财首页数据
     */
    private void backToFinanceMain(boolean refresh){
        BusinessLauncher.getInstance().clearTop(FrontPageActivity.class);
        FinanceRequestManager
                .getInstance().isFinanceChange = refresh;
        finish();
    }

    /**
     *
     * 1. 便民交易成功页面，顶部没有返回键，底部一个通栏按钮，文字为“返回首页”；便民交易失败页面，顶部有返回键，点击回到刷卡页的上一个页面，底部一个通栏按钮，文字为“重新还款”/"重新转账"/“重新充值”/“重新查询”，点击后准备重新插卡或刷卡；便民交易异常页面，顶部有返回键，点击回到首页，底部两个半通栏按钮，左侧文字为“在线客服”，右侧文字为“交易查询”；

     2. 收款交易成功，顶部没有返回键，底部一个通栏按钮，文字为“返回首页”；收款交易失败，顶部有返回键，底部一个通栏按钮，文字为“重新收款”，点击后准备重新插卡或刷卡；收款交易异常页面，顶部有返回键，点击回到首页，底部两个半通栏按钮，左侧文字为“在线客服”，右侧文字为“交易查询”；

     3. 撤销交易成功 , 顶部没有返回键，底部一个通栏按钮，文字为“返回首页”；撤销交易失败，顶部有返回键，底部一个通栏按钮，文字为“重新撤销”，点击后准备重新插卡或刷卡；收款交易异常页面，顶部有返回键，点击回到首页，底部两个半通栏按钮，左侧文字为“在线客服”，右侧文字为“交易查询”；
     */
    private void initResultButtons(TransResult result){
        TextView leftButton = (TextView) findViewById(R.id.button_left);
        leftButton.setOnClickListener(resultButtonsClick);
        TextView rightButton = (TextView) findViewById(R.id.button_right);
        rightButton.setOnClickListener(resultButtonsClick);
        TextView bottomButton= (TextView) findViewById(R.id.button_bottom);
        bottomButton.setOnClickListener(resultButtonsClick);
        TransactionType transactionType = transInfo.getType();
        if(transactionType == TransactionType.PaymentForCall){
            rightButton.setTag("call_back");
            rightButton.setText("结束");
            leftButton.setVisibility(View.GONE);
            return;
        }
        if(result == TransResult.SUCCESS){
            leftButton.setTag("back_to_main");
            leftButton.setText("返回首页");
            switch (transactionType){
                case Remittance:
                    rightButton.setText("继续转账");
                    rightButton.setTag("remittance");
                    rightButton.setVisibility(View.VISIBLE);
                    break;
                case Collection:
                    rightButton.setText("继续收款");
                    rightButton.setTag("collection");
                    rightButton.setVisibility(View.VISIBLE);
                    break;
                case ContributePayment:
                    rightButton.setText("继续收款");
                    rightButton.setTag("contribute");
                    rightButton.setVisibility(View.VISIBLE);
                    break;
                case MobileRecharge:
                    rightButton.setText("继续充值");
                    rightButton.setTag("mobile_recharge");
                    rightButton.setVisibility(View.VISIBLE);
                    break;
                case CreditCardPayment:
                    rightButton.setText("继续还款");
                    rightButton.setTag("credit_pay");
                    rightButton.setVisibility(View.VISIBLE);
                    break;
                case Query:
                    rightButton.setText("继续查询");
                    rightButton.setTag("balance_query");
                    rightButton.setVisibility(View.VISIBLE);
                    break;
                case APPLY:
                    leftButton.setVisibility(View.GONE);
                    rightButton.setTag("back_to_finance");
                    rightButton.setText("确定");
                    break;
                case WITHDRAW:
                    leftButton.setVisibility(View.GONE);
                    rightButton.setTag("back_to_finance");
                    rightButton.setText("返回首页");
                    break;
                case WALLET_RECHARGE:
                case WALLET_TRANSFER:
                    leftButton.setVisibility(View.GONE);
                    rightButton.setTag("back_to_wallet");
                    rightButton.setText("返回首页");
                    break;
                case TREASURE:
                    leftButton.setVisibility(View.GONE);
                    rightButton.setTag("treasure_result");
                    rightButton.setText("查看订单详情");
                    break;
                default:
                    leftButton.setVisibility(View.GONE);
                    rightButton.setTag("back_to_main");
                    rightButton.setText("返回首页");
                    break;
            }
        }else if(result == TransResult.FAILED){
            leftButton.setVisibility(View.GONE);
            rightButton.setTag("back_to_main");
            rightButton.setText("返回首页");
            switch (transactionType){
                case TREASURE:
                    rightButton.setTag("back_to_treasure");
                    break;
                case APPLY:
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                    leftButton.setText("返回首页");
                    leftButton.setTag("back_to_finance");
                    rightButton.setText("重新交易");
                    rightButton.setTag("retry_buy_product");
                    break;
                case WITHDRAW:
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                    leftButton.setText("返回首页");
                    leftButton.setTag("back_to_finance");
                    rightButton.setText("重新交易");
                    rightButton.setTag("retry_withdraw");
                    break;
                case WALLET_RECHARGE:
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                    leftButton.setText("返回首页");
                    leftButton.setTag("back_to_wallet");
                    rightButton.setText("重新交易");
                    rightButton.setTag("retry_recharge");
                    break;
                case WALLET_TRANSFER:
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                    leftButton.setText("返回首页");
                    leftButton.setTag("back_to_wallet");
                    rightButton.setText("重新交易");
                    rightButton.setTag("retry_transfer");
                    break;
                default:
                    break;
            }
        }else{
            leftButton.setText("电话咨询");
            leftButton.setTag("call_lakala");
            switch (transactionType){
                case Query:
                case Revocation:
                case RevocationAgain:
                    rightButton.setText("返回首页");
                    rightButton.setTag("back_to_main");
                    break;
                case BARCODE_COLLECTION:
                case Collection:
                case LARGE_AMOUNT_COLLECTION:
                    rightButton.setVisibility(View.GONE);
                    TextView centerButton = (TextView) findViewById(R.id.button_center);
                    centerButton.setText("交易查询");
                    centerButton.setTag("query_collection");
                    centerButton.setVisibility(View.VISIBLE);
                    centerButton.setOnClickListener(resultButtonsClick);

                    bottomButton.setText("返回首页");
                    bottomButton.setTag("back_to_main");
                    bottomButton.setVisibility(View.VISIBLE);
                    break;
                case CreditCardPayment:
                case MobileRecharge:
                case Remittance:
                case ContributePayment:
                case PRIVILEGEPURCHASE:
                    rightButton.setText("交易查询");
                    rightButton.setTag("query_life");

                    bottomButton.setText("返回首页");
                    bottomButton.setTag("back_to_main");
                    bottomButton.setVisibility(View.VISIBLE);
                    break;
                case RepayForYou:
                    rightButton.setText("交易查询");
                    rightButton.setTag("query_payforyou_history");

                    bottomButton.setText("返回首页");
                    bottomButton.setTag("back_to_main");
                    bottomButton.setVisibility(View.VISIBLE);
                    break;

                case WALLET_RECHARGE:
                case WALLET_TRANSFER:
                    rightButton.setText("交易查询");
                    rightButton.setTag("query_wallet");

                    bottomButton.setText("返回首页");
                    bottomButton.setTag("back_to_wallet");
                    bottomButton.setVisibility(View.VISIBLE);
                    break;
                case APPLY:
                case WITHDRAW:
                    rightButton.setText("交易查询");
                    rightButton.setTag("query_finance_detail");
                    break;
                default:
                    rightButton.setText("交易查询");
                    rightButton.setTag("query_transfer");
                    break;
            }
        }
    }

    private View.OnClickListener resultButtonsClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String tag = (String) view.getTag();
            if (tag.equals("back_to_main")) {
                backToMain();
            } else if(TextUtils.equals("query_payforyou_history",tag)){
                //替你还历史记录
                LoanHistoryActivity.open(context);
            }else if(TextUtils.equals("query_life",tag)){
                TradeManageActivity.queryDealType(context, RecordsQuerySelectionActivity.Type
                        .LIFE_RECORD);
            }else if (tag.equals
                    ("retry_buy_product")) {
                //退回到买入页面,重新买入
                BusinessLauncher.getInstance().clearTop(FundTranserInActivity.class);
            } else if (tag.equals("retry_withdraw")) {
                //退回到取出页面，重新取出
                BusinessLauncher.getInstance().clearTop(WithdrawalActivity.class);
            }else if (tag.equals("back_to_wallet")){
                //退回到钱包首页
                BusinessLauncher.getInstance().clearTop(transInfo.getType().getMainClazz());
                if (transInfo.getType().getMainClazz() == null){
                    BusinessLauncher.getInstance().clearTop(WalletHomeActivity.class);
                }
            }else if (tag.equals("retry_recharge")){
                //重新充值
                BusinessLauncher.getInstance().clearTop(WalletRechargeConfirmActivity.class);
            }else if (tag.equals("retry_transfer")){
                //重新提现
                BusinessLauncher.getInstance().clearTop(WalletTransferActivity.class);
            }else if (tag.equals("query_wallet")){
                //查询钱包明细
                BusinessRequest request = RequestFactory.getRequest(ConfirmResultActivity.this, RequestFactory.Type.WALLET_DETAIL_QRY);
                WalletTransDetailRequest params = new WalletTransDetailRequest(ConfirmResultActivity.this);
                params.setPage(WalletTransDetailActivity.page);
                request.setResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
                        hideProgressDialog();
                        if (resultServices.isRetCodeSuccess()) {

                            WalletTransDetailActivity.page++;
                            String data = resultServices.retData;
                            String balance = "";
                            if (transInfo.getType() == TransactionType.WALLET_RECHARGE){
                                balance = ((RechargeTransInfo)transInfo).getWalletBalance();
                            }else {
                                balance = ((WithdrawTransInfo)transInfo).getBalance();
                            }
                            Intent intent = new Intent(ConfirmResultActivity.this, WalletTransDetailActivity.class);
                            intent.putExtra("data", data);
                            intent.putExtra("balance",balance);
                            startActivity(intent);
                        } else {
                            ToastUtil.toast(ConfirmResultActivity.this, resultServices.retMsg);
                        }
                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        hideProgressDialog();
                        toastInternetError();
                    }
                });
                WalletServiceManager.getInstance().start(params,request);
            }else if (tag.equals("back_to_finance")) {
                TransResult result = transInfo.getTransResult();
                if(transInfo.getType()== TransactionType.APPLY){
                }else if(transInfo.getType()== TransactionType.WITHDRAW){
                }
                if (result == TransResult.SUCCESS) {
                    FinanceRequestManager.getInstance().startFinance(ConfirmResultActivity.this,
                                                                     new HttpResponseListener() {
                                                                         @Override
                                                                         public void onStart() {
                                                                         }

                                                                         @Override
                                                                         public void onFinished(
                                                                                 ReturnHeader returnHeader,
                                                                                 JSONObject responseData) {
                                                                             backToFinanceMain(true);
                                                                         }

                                                                         @Override
                                                                         public void onErrorResponse() {
                                                                             backToFinanceMain(false);
                                                                         }
                                                                     }
                    );
                } else {
                    backToFinanceMain(false);
                }
            } else if (tag.equals("query_transfer")) {
                BusinessLauncher.getInstance().clearTop(TradeManageActivity.class);
            }else if(TextUtils.equals("query_collection",tag)){
                TradeManageActivity.queryDealType(context, RecordsQuerySelectionActivity.Type
                        .COLLECTION_RECORD);
            } else if (tag.equals
                    ("collection")) {
                CollectionsAmountInputActivity.ifClean = true;
                BusinessLauncher.getInstance().clearTop(CollectionsAmountInputActivity.class);
            } else if (tag.equals("call_lakala")) {
                getPhone();
            } else if(TextUtils.equals(tag,"remittance")){
                BusinessLauncher.getInstance().clearTop(TransferRemittanceActivity.class);
            }else if (tag.equals("contribute")) {//继续收款
                ContributePaymentAmountInputActivity.ifClean = true;
                BusinessLauncher.getInstance().clearTop(ContributePaymentAmountInputActivity.class);
            } else if (tag.equals("query_finance_detail")) {
                FinanceRequestManager.getInstance().toTransDetail(ConfirmResultActivity.this, true);
            } else if (TextUtils.equals(tag, "mobile_recharge")) {
                BusinessLauncher.getInstance().clearTop(RechargeSelectAmountActivity.class);
            } else if (TextUtils.equals(tag, "credit_pay")) {
                BusinessLauncher.getInstance().clearTop(CreditFormInputActivity.class);
            } else if (TextUtils.equals(tag, "balance_query")) {
                BusinessLauncher.getInstance().clearTop(BalanceInqueryActivity.class);
            } else if(TextUtils.equals(tag,"treasure_result")
                    || TextUtils.equals(tag,"back_to_treasure")){
                WebMallContainerActivity.onResumeUrl = ((TreasureTransInfo)transInfo)
                        .getCallbackUrl();
                BusinessLauncher.getInstance().clearTop(WebMallContainerActivity.class);
            }else {
                if (Parameters.debug) {
                    throw new RuntimeException("Unknown Button Tag");
                }
            }
        }
    };


    @Override
    public void onBackPressed() {
        return;
    }


    private void getPhone(){
        showProgressWithNoMsg();
        ShoudanService.getInstance().queryLakalaService(new ShoudanService.PhoneQueryListener() {
            @Override
            public void onSuccess(String phoneStr) {
                hideProgressDialog();
                if(!TextUtils.isEmpty(phoneStr)){
                    PhoneNumberUtil.callPhone(ConfirmResultActivity.this, phoneStr);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        backToMain();
    }
}
