package com.lakala.shoudan.activity.wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.coupon.activity.CouponHomeActivity;
import com.lakala.shoudan.activity.integral.IntegralController;
import com.lakala.shoudan.activity.integral.callback.FirstEnterCallback;
import com.lakala.shoudan.activity.integral.callback.VoucherListCallback;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.BankCardAdditionActivity;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.BankCardEntranceActivity;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.BankCardBean;
import com.lakala.shoudan.activity.wallet.bean.WalletInfo;
import com.lakala.shoudan.activity.wallet.request.GetAccountInfoRequest;
import com.lakala.shoudan.activity.wallet.request.GetComPrensiveInfoRequest;
import com.lakala.shoudan.activity.wallet.request.MyBankCardRequest;
import com.lakala.shoudan.activity.wallet.request.RedPackageRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTransDetailRequest;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.Voucher;
import com.lakala.shoudan.datadefine.VoucherList;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.BaseDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengx on 2015/11/19.
 * 零钱首页
 */
public class WalletHomeActivity extends AppBaseActivity implements OnClickListener {

    private TextView changeBalance;
    private RelativeLayout changeRecharge;
    private RelativeLayout changeWithdrawal;
    private RelativeLayout changeRedPackage;
    private RelativeLayout changeBankCard;
    private RelativeLayout changeHappyBean;
    private RelativeLayout changeCoupon;

    private Intent intent;
    private List<BankCardBean> bankCardBeanList;
    private BankCardBean bankCardBean;
    public static final int PAGE = 1;
    public static final int SIZE = 20;
    public static final String RED_PACKAGE_BUSID = "130002";
    public static final String WALLET_RECHARGE = "800003";
    public static final String WALLET_TRANSFER = "800005";
    public static final String WALLET_TRANSFER_TO_BILL = "300007";
    private WalletInfo walletInfo = new WalletInfo();
    private boolean isQuery = true;
    private String balance = "";
    private BaseDialog dialog;
    private BaseDialog AhtuDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_home);
        initUI();
        TransactionType.WALLET_RECHARGE.setMainClazz(getClass());
        TransactionType.WALLET_TRANSFER.setMainClazz(getClass());
    }

    @Override
    protected void initUI() {
        super.initUI();

        navigationBar.setTitle("零钱");
        navigationBar.setActionBtnText("收支明细");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem) {
                    case back:
                        finish();
                        break;
                    case action:
                        if (isQuery) {
                            toast("处理中，请稍后");
                        } else {
                            getTransDetail();
                        }
                        break;
                }
            }
        });

        changeBalance = (TextView) findViewById(R.id.change_balance);
        changeRecharge = (RelativeLayout) findViewById(R.id.change_recharge);
        changeWithdrawal = (RelativeLayout) findViewById(R.id.change_withdrawal);
        changeRedPackage = (RelativeLayout) findViewById(R.id.change_red_package);
        changeBankCard = (RelativeLayout) findViewById(R.id.change_bank_card);
        changeHappyBean = (RelativeLayout) findViewById(R.id.change_happy_bean);
        changeCoupon = (RelativeLayout) findViewById(R.id.change_coupon);
        changeRecharge.setOnClickListener(this);
        changeWithdrawal.setOnClickListener(this);
        changeRedPackage.setOnClickListener(this);
        changeBankCard.setOnClickListener(this);
        changeHappyBean.setOnClickListener(this);
        changeCoupon.setOnClickListener(this);

        bankCardBeanList = new ArrayList<BankCardBean>();
        initQuery();
    }

    //根据用户等级，显示隐藏引导绑定银行卡的提示
     public void showintroducer(String introducer){
         LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) changeBankCard.getLayoutParams();
         if (introducer==null||introducer.equals("EWALLET_USERTYPE_NOT_REALNAME")||introducer.equals("EWALLET_USERTYPE_FIRST")||introducer.equals("EWALLET_USERTYPE_SECOND")){
             params.height=getResources().getDimensionPixelSize(R.dimen.order_item_height_hint);
             findViewById(R.id.add_bank_card_hint).setVisibility(View.VISIBLE);
         }else {
             findViewById(R.id.add_bank_card_hint).setVisibility(View.GONE);
             params.height=getResources().getDimensionPixelSize(R.dimen.order_item_height);
         }
         changeBankCard.setLayoutParams(params);
     }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isQuery) {
                int length = changeBalance.length();
                if (length >= 5) {
                    changeBalance.setText("");
                }
                changeBalance.append(".");
                changeBalance.postDelayed(this, 300);
            } else {
                changeBalance.removeCallbacks(runnable);
            }
        }
    };

    private void initQuery() {
        changeBalance.post(runnable);
        checkWalletTerminal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryUserType();
    }

    @Override
    public void onClick(View v) {
        if (isQuery) {
            toast("处理中，请稍后");
        } else {
            switch (v.getId()) {
                case R.id.change_recharge:
                    //showAhtuDialog();

                    if (walletInfo.getAuthFlag() == 0) {
                        showAhtuDialog();
                    } else {
                        queryRechargeLimit();
                    }

                    break;
                case R.id.change_withdrawal:
                    if (walletInfo.getAuthFlag() == 0) {
                        showAhtuDialog();
                    } else {
                        getAccountInfo();
                    }
                    break;
                case R.id.change_red_package:
                    initGift();
                    break;
                case R.id.change_bank_card:
                    initBank();
                    break;
//                case R.id.change_happy_bean:
//                    Intent intent=new Intent(WalletHomeActivity.this,HappyBeanDetailsActivity.class);
//                    startActivity(intent);
//                    break;
                case R.id.change_coupon:
                    showProgressWithNoMsg();
                    IntegralController.getInstance(context).isFirstEnter(firstEnterCallback);
                    break;
            }
        }
    }

    private void showAhtuDialog() {
        AhtuDialog = DialogCreator.createFullContentDialog(this, "提示", "关闭", "马上认证", "根据中国人民银行的规定，进行钱包交易您需要先完成实名认证", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               /* intent = new Intent(WalletHomeActivity.this, WalletProcessAuthActivity.class);
                startActivity(intent);*/
                startActivityForResult(new Intent(WalletHomeActivity.this, BankCardAdditionActivity.class), ConstKey.REQUEST_ADD);
            }
        });
        if (!AhtuDialog.isShowing()) {
            AhtuDialog.show();
        }
    }

    FirstEnterCallback firstEnterCallback = new FirstEnterCallback(context) {
        @Override
        public void firstEnterCallback(@Nullable Boolean firstEnter) {
            if (firstEnter == null) {
                context.hideProgressDialog();
                return;
            }
            if (firstEnter) {
                context.hideProgressDialog();
                CouponHomeActivity.start(context, null, CouponHomeActivity.IsFirst.ZERO);
            } else {
                getCouponList();
            }

        }

        @Override
        public void onEvent(HttpConnectEvent connectEvent) {
            context.hideProgressDialog();
            ToastUtil.toast(context, context.getString(R.string.socket_fail));
        }
    };

    public void checkWalletTerminal() {

        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {
                LogUtil.print("------>", "onStart");
            }

            @Override
            public void onError(String msg) {
                LogUtil.print("------>", "onError");
                showDialog();
            }

            @Override
            public void onSuccess() {
                LogUtil.print("------>", "onSuccess");
                getComprensiveInfo();
            }
        });

    }

    /**
     * 获取用户综合安全信息
     */
    private void getComprensiveInfo() {

        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.COMPREHENSIVE_INFORMATION);
        GetComPrensiveInfoRequest params = new GetComPrensiveInfoRequest(context);
        LogUtil.print("------>",params.getTelecode());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    isQuery = false;
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);

                        walletInfo.setWalletBalance(jsonObject.optString("walletBalance"));
                        walletInfo.setCustomerName(jsonObject.optString("customerName"));
                        walletInfo.setWalletWithdrawBalance(jsonObject.optString("walletWithdrawBalance"));
                        walletInfo.setAuthFlag(jsonObject.optInt("authFlag"));
                        walletInfo.setWalletNo(jsonObject.optString("walletNo"));
                        walletInfo.setNoPwdAmount(jsonObject.optString("noPwdAmount"));
                        walletInfo.setNoPwdFlag(jsonObject.optInt("noPwdFlag"));
                        walletInfo.setTrsPasswordFlag(jsonObject.optInt("trsPasswordFlag"));
                        walletInfo.setQuestionFlag(jsonObject.optInt("questionFlag"));

                        changeBalance.setText(walletInfo.getWalletBalance());

                    } catch (Exception e) {
                        LogUtil.print("wallet json解析异常");
                        e.printStackTrace();
                    }

                } else {
                    showDialog();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
                showDialog();

            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }

    private void showDialog() {
        dialog = DialogCreator.createFullContentDialog(this, "取消", "重试", "您的网络状况不佳，请稍后重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                finish();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                changeBalance.removeCallbacks(runnable);
                initQuery();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    /**
     * 转出先查询收款人信息判断有没绑卡及到账时间
     * 1、有绑卡，则默认选择第一位，并且根据卡号去查卡Bin
     * 2、没绑卡，则需主动跳转到绑卡界面然后判断有无开通商户来决定是否带出卡信息
     */

    private void getAccountInfo() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_TOBANK_ACCOUNT_INFO);
        GetAccountInfoRequest params = new GetAccountInfoRequest(this);
        LogUtil.print("------>",params.getTelecode());
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    Intent intent = new Intent(WalletHomeActivity.this, WalletTransferActivity.class);
                    intent.putExtra(Constants.IntentKey.WALLET_INFO, walletInfo);
                    intent.putExtra(Constants.IntentKey.WALLET_ACCOUNT_INFO, resultServices.retData);
//                    intent.putExtra(Constants.IntentKey.Wallet_Withdraw, "walletWithdraw");
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Withdraw, context);
                    startActivity(intent);
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

    /**
     * 钱包交易明细
     */
    private void getTransDetail() {
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_DETAIL_QRY);
        WalletTransDetailRequest params = new WalletTransDetailRequest(this);
        LogUtil.print("------>",params.getTelecode());
        params.setPage(WalletTransDetailActivity.page);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {

                    WalletTransDetailActivity.page++;
                    String data = resultServices.retData;
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                            .Balance_of_Payment_Details, context);
                    Intent intent = new Intent(WalletHomeActivity.this, WalletTransDetailActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                } else {
                    ToastUtil.toast(WalletHomeActivity.this, resultServices.retMsg);
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

    /**
     * 查询红包信息
     */
    private void initGift() {

        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.RED_PACKAGE_QRY);
        RedPackageRequest params = new RedPackageRequest(context);
        LogUtil.print("------>",params.getTelecode());
        params.setPage("1");//红包分页编号
        params.setBusid(RED_PACKAGE_BUSID);
        params.setGiftStat("0");
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    String data = resultServices.retData;
                    intent = new Intent(WalletHomeActivity.this, RedPackageActivity.class);
                    intent.putExtra("data", data);
                    startActivity(intent);
                    ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Check_Red_Envelope, context);
                } else {
                    ToastUtil.toast(WalletHomeActivity.this, resultServices.retMsg);
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
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Recharge, context);
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
//                                intent = new Intent(WalletHomeActivity.this, WalletRechargeActivity.class);
//                                intent.putExtra(Constants.IntentKey.WALLET_RECHARGE_LIMIT, limitInfo);
//                                balance = walletInfo.getWalletBalance();
//                                intent.putExtra("balance",balance);
////                                intent.putExtra(Constants.IntentKey.Wallet_Recharge,"walletRecharge");
//                                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Wallet_Recharge, context);
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

    /**
     * 查询我的银行卡
     */
    private void initBank() {
        showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(this, RequestFactory.Type.MY_WALLET);
        MyBankCardRequest params = new MyBankCardRequest(context);
        LogUtil.print("------>",params.getTelecode());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        JSONArray array = jsonObject.optJSONArray("cardList");
                        bankCardBeanList.clear();
                        if (array.length() != 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = array.getJSONObject(i);
                                bankCardBean = new BankCardBean();
                                bankCardBean.setAccountNo(json.optString("accountNo"));
                                bankCardBean.setAccountType(json.optString("accountType"));
                                bankCardBean.setBankName(json.optString("bankName"));
                                bankCardBean.setCardId(json.optString("cardId"));
                                bankCardBean.setBkMobile(json.optString("bkMobile"));
                                bankCardBean.setSignFlag(json.optString("signFlag"));
                                bankCardBean.setBankCode(json.optString("bankCode"));
                                bankCardBeanList.add(bankCardBean);
                            }
                        }
                        Intent intent = new Intent(WalletHomeActivity.this, BankCardEntranceActivity.class);
                        intent.putExtra(ConstKey.BANK_CARD_LIST, (Serializable) bankCardBeanList);
                        startActivity(intent);
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Check_Bankcard, context);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }
    /**
     * 查询商户等级，显示或者隐藏引导语
     */
    private void queryUserType() {
        showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_GET_USER_TYPE);
        MyBankCardRequest params = new MyBankCardRequest(context);
        LogUtil.print("------>",params.getTelecode());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        String userTypeInfo=jsonObject.optString("userTypeInfo");
                        WalletHomeActivity.this.showintroducer(userTypeInfo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        isQuery = true;
        initQuery();
    }

    /**
     * 获取代金券列表
     */
    private void getCouponList() {
        VoucherListCallback callback = new VoucherListCallback() {
            @Override
            public void onResult(ResultServices resultServices, VoucherList voucherList) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    List<Voucher> data = new ArrayList<Voucher>();
                    if (voucherList != null && voucherList.getVouchers() != null) {
                        data.addAll(voucherList.getVouchers());
                    }
                    CouponHomeActivity.start(context, data, CouponHomeActivity.IsFirst.ONE);
                } else {
                    LogUtil.print(resultServices.retMsg);
                    toast(resultServices.retMsg);
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        IntegralController.getInstance(context).getVoucherList(callback);
    }
}
