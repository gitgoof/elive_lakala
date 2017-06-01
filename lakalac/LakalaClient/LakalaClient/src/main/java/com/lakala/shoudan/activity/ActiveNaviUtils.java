package com.lakala.shoudan.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.AppConfig;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.CollectionEnum;
import com.lakala.platform.statistic.DoEnum;
import com.lakala.platform.statistic.FinancePurchanceEnum;
import com.lakala.platform.statistic.LargeAmountEnum;
import com.lakala.platform.statistic.PayForYouEnum;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.UndoEnum;
import com.lakala.platform.statistic.WalletRechargeEnum;
import com.lakala.platform.statistic.WalletWithdrawEnum;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.communityservice.balanceinquery.BalanceInqueryActivity;
import com.lakala.shoudan.activity.communityservice.balanceinquery.BalanceTransInfo;
import com.lakala.shoudan.activity.communityservice.creditpayment.CreditFormInputActivity;
import com.lakala.shoudan.activity.communityservice.phonerecharge.RechargeSelectAmountActivity;
import com.lakala.shoudan.activity.communityservice.transferremittance.TransferRemittanceActivity;
import com.lakala.shoudan.activity.coupon.activity.CouponInputActivity;
import com.lakala.shoudan.activity.integral.IntegralController;
import com.lakala.shoudan.activity.merchant.upgrade.UpgradeStatus;
import com.lakala.shoudan.activity.merchantmanagement.MerchantManagementAcivity;
import com.lakala.shoudan.activity.myaccount.FollowActivity;
import com.lakala.shoudan.activity.payment.DeviceManagementActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.quickArrive.OnDayLoanClickListener;
import com.lakala.shoudan.activity.safetymanagement.SafetyManagementActivity;
import com.lakala.shoudan.activity.shoudan.AdShareActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.chargebusiness.ChargeBusinessSelection;
import com.lakala.shoudan.activity.shoudan.chargebusiness.ContributePaymentAmountInputActivity;
import com.lakala.shoudan.activity.shoudan.chargebusiness.RentCollectionAmountActivity;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.loan.LoanTrailActivity;
import com.lakala.shoudan.activity.shoudan.loan.bankinfo.CreditBankInfoActivity;
import com.lakala.shoudan.activity.shoudan.loan.kepler.KeplerUtil;
import com.lakala.shoudan.activity.shoudan.loan.loanlist.CreditListActivity;
import com.lakala.shoudan.activity.shoudan.loan.loanlist.CreditListModel;
import com.lakala.shoudan.activity.shoudan.loan.location.U51LocationHelper;
import com.lakala.shoudan.activity.shoudan.loan.personinfo.CreditPersonInfoActivity;
import com.lakala.shoudan.activity.shoudan.promotionarea.PromotionAreaActivity;
import com.lakala.shoudan.activity.shoudan.records.RecordsQuerySelectionActivity;
import com.lakala.shoudan.activity.shoudan.records.TradeManageActivity;
import com.lakala.shoudan.activity.wallet.WalletHomeActivity;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.webbusiness.WebMallContainerActivity;
import com.lakala.shoudan.bll.AdDownloadManager;
import com.lakala.shoudan.bll.BarcodeAccessManager;
import com.lakala.shoudan.bll.LargeAmountAccessManager;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.common.util.ServiceManagerUtil;
import com.lakala.shoudan.component.DrawButtonClickListener;
import com.lakala.shoudan.component.DrawButtonClickListener2;
import com.lakala.shoudan.util.CommonUtil;
import com.lakala.shoudan.util.XAtyTask;
import com.treefinance.sdk.GFDAgent;
import com.zhangdan.app.loansdklib.U51WebViewActivity;
import com.zhangdan.app.loansdklib.api.sdkhelp.api.InitializaU51LoanSDK;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by LMQ on 2015/12/23.
 */
//
//[   { "id": "sksk", "text": "刷卡收款"},
//        { "id": "smsk", "text": "扫码收款"},
//        { "id": "desk", "text": "大额收款"},
//        { "id": "d0", "text": "立即提款"},
//        { "id": "xykhk", "text": "信用卡还款"},
//        { "id": "zzhk", "text": "转账汇款"},
//        { "id": "sjcz", "text": "手机充值"},
//        { "id": "yecx", "text": "余额查询"},
//        { "id": "licai", "text": "理财"},
//        { "id": "zxgm", "text": "专享购买"},
//        { "id": "1kuai", "text": "一块夺宝"},
//        { "id": "shgl", "text": "用户信息"},
//        { "id": "hdzq", "text": "活动专区"},
//        { "id": "goumai", "text": "购买收款宝"},
//        { "id": "deviceManager", "text": "设备管理"}
//        ]
public class ActiveNaviUtils {
    public static void start(final AppBaseActivity context, Type key, @Nullable AdDownloadManager.Advertise advertise) {
        if (advertise == null) {
            advertise = new AdDownloadManager.Advertise();
        }
        switch (key) {
            case ONE_YUAN:
                startOneYuan(context);
                break;
            case TREASURE:
                startTreasure(context);
                break;
            case MARKET_ZONE:
//                WebMallContainerActivity.open(context, title, url,type);
                break;
            case WALLET_HOME:
                Intent intent = new Intent(context, WalletHomeActivity.class);
                context.startActivity(intent);
                WalletWithdrawEnum.WalletWithdraw.setAdvertId(advertise.getId());
                WalletRechargeEnum.WalletRecharge.setAdvertId(advertise.getId());
                break;
            case COLLECTION:
                swipeCollection(context, advertise);
                break;
            case BARCODE_COLLECTION:
                scanCollection(context, advertise);
                break;
            case LARGE_COLLECTION:
                largeAmountCollection(context, advertise);
                break;
            case REVOCATION:
                revocation(context, advertise);
                break;
            case D0:
                new DrawButtonClickListener2(context).onClick(null);
                DoEnum.Do.setAdvertId(advertise.getId());
                break;
            case D01:
                new DrawButtonClickListener(context).onClick(null);
//                DoEnum.Do.setAdvertId(advertise.getId());
                break;
            case CREDIT:
                intent = new Intent(context, CreditFormInputActivity.class);
                context.startActivity(intent);
                break;
            case TRANSFER:
                intent = new Intent(context, TransferRemittanceActivity.class);
                context.startActivity(intent);
                break;
            case COLLLECTION_RECORD:
                TradeManageActivity.queryDealType(context, RecordsQuerySelectionActivity.Type.COLLECTION_RECORD, false);
                break;
            case PHONE_RECHARGE:
                intent = new Intent(context, RechargeSelectAmountActivity.class);
                context.startActivity(intent);
                break;
            case BALANCE_QUERY:
                intent = new Intent(context, BalanceInqueryActivity.class);
                intent.putExtra(ConstKey.TRANS_INFO, new BalanceTransInfo());
                context.startActivity(intent);
                break;
            case FINANCE:
                FinanceRequestManager.getInstance().startFinance(context);
                FinancePurchanceEnum.FinancePurchance.setAdvertId(advertise.getId());
                break;
            case LOAN_PAY:
                LoanTrailActivity.open(context);
                PayForYouEnum.PayForYou.setAdvertId(advertise.getId());
                break;
            case TEQUAN:
                startQequan(context);
                break;
            case MERCHANT_MANAGER:
//                intent = new Intent(context, MerchantManagementAcivity.class);
//                context.startActivity(intent);
                ActiveNaviUtils.start((AppBaseActivity) context, ActiveNaviUtils.Type.UPDATE_STATUS);
                break;
//            case SERVICE_UPGRADE:
//                //升级服务
//                break;
            case TE_YUE:
                //特约商户缴费
                TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
                    @Override
                    public void onStart() {
                        if (!TerminalKey.hadVirtualSigned()) {
                            context.showProgressWithNoMsg();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        context.hideProgressDialog();
                        ToastUtil.toast(context, context.getString(R.string.socket_fail));
                        LogUtil.print(msg);
                    }

                    @Override
                    public void onSuccess() {
                        context.hideProgressDialog();
                        jumpMerchant(context);
                    }
                });
                break;
            case AN_QUAN:
                getSecurityInfo(context);
                break;
            case LOAN_BUSINESS:
                getLoanListAndDetail(context);
                break;
            case HDZQ://活动专区
                intent = new Intent(context, PromotionAreaActivity.class);
                context.startActivity(intent);
                break;
            case GUAN_ZHU:
                intent = new Intent(context, FollowActivity.class);
                context.startActivity(intent);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Follow_LaKaLa, context);
                break;
            case JIA_MENG:
                ProtocalActivity.open(context, ProtocalType.JOIN_LAKALA);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Join_ShouKuanBao, context);
                break;
            case GOU_MAI://goumai
                ProtocalActivity.open(context, ProtocalType.BUY_LAKALA_SWIPE);
                ShoudanStatisticManager.getInstance()
                        .onEvent(ShoudanStatisticManager.Buy_ShouKuanBao, context);
                break;
            case UPDATE_STATUS:
                PublicToEvent.MerchantlEvent(ShoudanStatisticManager.MENU_MERCHANT, context);
                context.showProgressWithNoMsg();
                LoginRequestFactory.createBusinessInfoRequest().setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
                        //商户信息请求成功
                        if (resultServices.isRetCodeSuccess()) {
                            try {
                                User user = ApplicationEx.getInstance().getSession().getUser();
                                JSONObject data = new JSONObject(resultServices.retData);
                                user.initMerchantAttrWithJson(data);
                                ApplicationEx.getInstance().getUser().save();

                                MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
                                MerchantStatus status = merchantInfoData.getMerchantStatus();
                                switch (status) {
                                    case FAILURE:
                                    case FROZEN:
                                    case PROCESSING:

                                    case COMPLETED:
                                        //FinanceRequestManager financeRequestManager = FinanceRequestManager.getInstance();
                                        // financeRequestManager.setStatistic(ShoudanStatisticManager.Finance_HomePage);
                                        //financeRequestManager.startFinanceYHGL((AppBaseActivity) context);
                                        queryMerLevelInfo(context);
                                        break;
                                    case NONE:
                                        context.startActivity(new Intent(context, MerchantManagementAcivity.class));
                                        context.hideProgressDialog();
                                        context.startActivity(new Intent(context, MerchantManagementAcivity.class));
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                ToastUtil.toast(context, "数据解析异常");
                                context.hideProgressDialog();
                            }
                        } else {
                            ToastUtil.toast(context, resultServices.retMsg);
                            context.hideProgressDialog();
                        }
                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        ToastUtil.toast(context, R.string.socket_fail);
                        context.hideProgressDialog();
                    }
                })).execute();

//                TokenRefreshService.getInstance().getMerchantInfo2(new TokenRefreshService.getMerchantInfoLis() {
//                    @Override
//                    public void onSuccess() {
//                        context.showProgressWithNoMsg();
//                        LogUtil.print("------>", "刷新成功");
//                        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
//                        MerchantStatus status = merchantInfoData.getMerchantStatus();
//                        switch (status) {
//                            case FAILURE:
//                            case FROZEN:
//                            case PROCESSING:
//                            case COMPLETED:
//                                LogUtil.print("<><><>", "query");
//                                queryMerLevelInfo(context);
//                                break;
//                            case NONE:
//                                LogUtil.print("<><><>", "start");
//                                context.startActivity(new Intent(context, MerchantManagementAcivity.class));
//                        }
//                        context.hideProgressDialog();
//
//                    }
//
//                    @Override
//                    public void onEvent() {
//                        LogUtil.print("------>", "失败");
//                        context.hideProgressDialog();
//                    }
//                });

                break;
            case COUPON_STATUS:
                getCouponStatus(context);
                break;
            case DEVICEMANAGER:
                context.startActivity(new Intent(context, DeviceManagementActivity.class));
                break;
            case INTEGRAL: {
                IntegralController.getInstance(context)
                        .check2Enter();
                break;
            }
            case ONE_DAY_LOAN:
                new OnDayLoanClickListener(context).onClick(null);
                break;
            case APPlY_CREDITA_CARD:
                context.startActivity(
                        new Intent(context, AdShareActivity.class)
                                .putExtra("url", "http://m.rong360.com/credit/card/landing/4?code=3&utm_source=lkl&utm_medium=skb_xyk&fcode=2")
                                .putExtra("title", "信用卡申办"));
                break;
            case REPAYMENT_CREDITA_CARD:
                BusinessLauncher.getInstance().start("creditcard_payment");
                break;
            case LOAN_YFQ:
                new CreditListModel().getYFQurl(context);
                break;
            case LOAN_PAPH:
                new KeplerUtil().enterKepler((AppBaseActivity)context,"");
                break;
            case LOAN_GFD:
                ActiveNaviUtils.toGFDSDK((AppBaseActivity) context);
                break;
        }
    }

    public static void start(AppBaseActivity context, Type type) {
        start(context, type, null);
    }

    public static void start(AppBaseActivity context, String key) {
        start(context, key, null);
    }

    public static void start(AppBaseActivity context, String key, @Nullable AdDownloadManager.Advertise advertise) {
        Type type = null;
        try {
            type = Type.keyValueOf(key);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (type == null) {
            return;
        }
        start(context, type, advertise);
    }

    /**
     * 撤销
     */
    private static void revocation(FragmentActivity context, AdDownloadManager.Advertise advertise) {
        if (CommonUtil.isMerchantValid(context)) {
            BusinessLauncher.getInstance().start("revocation");
            UndoEnum.Undo.setAdvertId(advertise.getId());
        }
    }

    /**
     * 大额收款
     */
    private static void largeAmountCollection(AppBaseActivity context, AdDownloadManager.Advertise advertise) {
        if (CommonUtil.isMerchantValid(context)) {
            new LargeAmountAccessManager(context).check();
            LargeAmountEnum.LargeAmount.setAdvertId(advertise.getId());
        }
    }

    /**
     * 扫码收款
     */
    private static void scanCollection(AppBaseActivity context, AdDownloadManager.Advertise advertise) {
        if (CommonUtil.isMerchantValid(context)) {
            BarcodeAccessManager barcodeAccessManager = new BarcodeAccessManager(context);
            barcodeAccessManager.check(true, true);
            ScanCodeCollectionEnum.ScanCodeCollection.setData(advertise.getId(), false);
        }
    }

    /**
     * 刷卡收款
     */
    private static void swipeCollection(FragmentActivity context, AdDownloadManager.Advertise advertise) {

        if (CommonUtil.isMerchantValid(context)) {
            BusinessLauncher.getInstance().start("collection_transaction");
            CollectionEnum.Colletcion.setData(advertise.getId(), false);
        }

    }

    /**
     * 特权购买
     */
    private static void startQequan(final AppBaseActivity context) {
        if (context == null) {
            return;
        }
        startWebActiveTEQUAN(context, "TEQUAN", Type.TEQUAN);
    }

    /**
     * 一元购
     */
    private static void startOneYuan(AppBaseActivity context) {
        if (context == null) {
            return;
        }

    }

    /**
     * 一块夺宝
     */
    private static void startTreasure(final AppBaseActivity context) {
        if (context == null) {
            return;
        }
        startWebActive(context, "YI_KUAI", Type.TREASURE);
    }

    public static void jumpMerchant(AppBaseActivity context) {
        AppConfig appConfig = ApplicationEx.getInstance().getUser().getAppConfig();

        if (appConfig.isRentCollectionEnabled() && appConfig.isContributePaymentEnabled()) {
            ShoudanStatisticManager.getInstance()
                    .onEvent(ShoudanStatisticManager.Charge_Business_Selection, context);
            context.startActivity(new Intent(context, ChargeBusinessSelection.class));

        } else if (appConfig.isContributePaymentEnabled()) {
            context.startActivity(new Intent(context, ContributePaymentAmountInputActivity.class));

        } else if (appConfig.isRentCollectionEnabled()) {
            context.startActivity(new Intent(context, RentCollectionAmountActivity.class));
        }
    }

    private static void startWebActive(final AppBaseActivity context, String activeType, final Type type) {
        context.showProgressWithNoMsg();
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.MARKET_EFFORTS);
        HttpRequestParams params = request.getRequestParams();
        params.put("effortCode", activeType);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                context.hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONObject(resultServices.retData).optJSONArray("marketefforts");
                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                        String title = jsonObject.optString("webviewTitle");
                        String url = jsonObject.optString("url");
                        WebMallContainerActivity.open(context, title, url, type);
                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                context.toastInternetError();
            }
        });
        request.execute();
    }

    private static void startWebActiveTEQUAN(final AppBaseActivity context, String activeType, final Type type) {
        context.showProgressWithNoMsg();
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.MARKET_EFFORTS);
        HttpRequestParams params = request.getRequestParams();
        params.put("effortCode", activeType);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONObject(resultServices.retData).optJSONArray("marketefforts");
                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                        final String title = jsonObject.optString("webviewTitle");
                        final String url = jsonObject.optString("url");


                        AdDownloadManager.getInstance().check(new AdDownloadManager.AdDownloadListener() {
                            @Override
                            public void onSuccess(List<AdDownloadManager.Advertise> advertises) {
                                context.hideProgressDialog();
                                if (advertises != null && advertises.size() > 0) {
                                    WebMallContainerActivity.open(context, title, url, advertises.get(0).getRemark(), type);
                                }
                            }

                            @Override
                            public void onFailed() {
                                context.hideProgressDialog();
                            }
                        }, AdDownloadManager.Type.EVENT);
                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                context.toastInternetError();
            }
        });
        request.execute();
    }

    /**
     * 获取用户支付安全信息
     */
    private static void getSecurityInfo(final AppBaseActivity context) {
        context.showProgressWithNoMsg();
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.SECURITY_fLAG);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                context.hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        WalletServiceManager.getInstance().setNoPwdFlag(jsonObject.optString("noPwdFlag"));//免密标识
                        WalletServiceManager.getInstance().setTrsPasswordFlag(jsonObject.optString("trsPasswordFlag"));//支付密码标识
                        WalletServiceManager.getInstance().setQuestionFlag(jsonObject.optString("questionFlag"));//密保标识0未设置，1已设置
                        WalletServiceManager.getInstance().setNoPwdAmount(jsonObject.optString("noPwdAmount"));//免密金额
                        Intent intent = new Intent(context, SafetyManagementActivity.class);
                        ShoudanStatisticManager.getInstance()
                                .onEvent(ShoudanStatisticManager.Security_Management_Homepage, context);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(context, businessRequest);
    }

    /**
     * 贷款列表及详情
     */
    private static void getLoanListAndDetail(final AppBaseActivity context) {
        context.showProgressWithNoMsg();
        //定位

        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.Loan_List_Homepage);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        String data = resultServices.retData;
                        Intent intent = new Intent(context, CreditListActivity.class);
                        intent.putExtra("dataList", data);
                        getAdver(intent, context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(context, businessRequest);
    }

    /**
     * 贷款列表及详情
     */
    private static void getAdver(final Intent intent, final AppBaseActivity context) {
        AdDownloadManager.getInstance().check1(intent, AdDownloadManager.Type.DAIKUAN, context);
    }

    /**
     * 获取升级状态
     */
    private static void getUpdateStatus(final AppBaseActivity context, final String data) {
        LogUtil.print("<><><>", "into");
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_STATUS);
        businessRequest.setAutoShowToast(true);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                LogUtil.print("<><><>", "into1");
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        LogUtil.print("<><><>", "into2:");
                        jsonObject = new JSONObject(resultServices.retData);
                        LogUtil.print("<><><>", "into2:" + jsonObject.toString());
                        boolean thirdUpgrade = jsonObject.optBoolean("thirdUpgrade", false);
                        String secondUpgradeStatus = jsonObject.optString("secondUpgradeStatus");
                        String firstUpgradeStatus = jsonObject.optString("firstUpgradeStatus");
                        String openBankBranchName = jsonObject.optString("openBankBranchName");
                        String busLicenceCode = jsonObject.optString("busLicenceCode");
                        Intent intent = new Intent(context, MerchantManagementAcivity.class);
                        boolean isShowUpgrade = true;
                        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
                        MerchantStatus status = merchantInfoData.getMerchantStatus();
                        if (TextUtils.isEmpty(secondUpgradeStatus)) {
                            return;
                        }
                        boolean showHint = false;

                        /**
                         * 若商户已开通，且审核通过，显示商户升级按钮；
                         若商户仅提交基础进件资料、持身份证照、持银行卡照，且一类审核已通过，显示商户升级按钮。
                         若商户所有资料均已提交，且二类审核已通过，则根据后台交易行为分的数据跑分判断商户是否可升级：若可升级，提供商户升级按钮；
                         */
                        if (status != MerchantStatus.COMPLETED) {
                            isShowUpgrade = false;
                            // dealResults(null,intent,context);
                        } else {
                            if (TextUtils.equals(secondUpgradeStatus, UpgradeStatus.COMPLETED.name()) &&
                                    (thirdUpgrade == false)) {
                                isShowUpgrade = false;
                            }
                            if (TextUtils.equals(secondUpgradeStatus, UpgradeStatus.FAILURE.name()) ||
                                    TextUtils.equals(firstUpgradeStatus, UpgradeStatus.FAILURE.name())) {
                                showHint = true;
                            }
                            // getD0Status(context,intent);
                        }
                        intent.putExtra("thirdUpgrade", thirdUpgrade);
                        intent.putExtra("isShowUpgrade", isShowUpgrade);
                        intent.putExtra("firstUpgradeStatus", firstUpgradeStatus);
                        intent.putExtra("secondUpgradeStatus", secondUpgradeStatus);
                        intent.putExtra("openBankBranchName", openBankBranchName);
                        intent.putExtra("busLicenceCode", busLicenceCode);
                        intent.putExtra("showHint", showHint);
                        intent.putExtra(UniqueKey.MER_LEVEL_INFO, data);
                        getMenrchattuStas(context, intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                    LogUtil.print(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                ToastUtil.toast(context, context.getString(R.string.socket_fail));
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        ServiceManagerUtil.getInstance().start(context, businessRequest);
    }

    /**
     * 获取业务开通状态
     *
     * @param context
     * @param intent
     */
    private static void getDredgeStatus(final AppBaseActivity context, final Intent intent) {
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.GET_DREDGE_STATUS);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                context.hideProgressDialog();
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        LogUtil.print("<><><>", "into2:" + jsonObject.toString());
                        JSONObject bLimitStatus = jsonObject.optJSONObject("bLimitStatus");//大额收款
                        String bStatus = bLimitStatus.optString("status");

                        JSONObject speedArrivalStatus = jsonObject.optJSONObject("speedArrivalStatus");//急速到账
                        String speedStatus = speedArrivalStatus.optString("status");
                        JSONObject imagePayStatus = jsonObject.optJSONObject("imagePayStatus");
                        JSONObject imageStatus = imagePayStatus.optJSONObject("status");
                        String c2bStatus = imageStatus.optString("c2b");
                        String b2cStatus = imageStatus.optString("b2c");

                        intent.putExtra("bStatus", bStatus);//大额收款
                        intent.putExtra("speedStatus", speedStatus);//急速到账
                        intent.putExtra("c2bStatus", c2bStatus);//扫码收款
                        // intent.putExtra("b2cStatus", b2cStatus);//急速到账
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    LogUtil.print(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
                //context.startActivity(intent);
            }
        });
        ServiceManagerUtil.getInstance().start(context, businessRequest);
    }

    private static void getMenrchattuStas(final AppBaseActivity context, final Intent intent) {
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_MERCHAT_BUSINESSNAME);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        LogUtil.print("<><><>", "into2:" + jsonObject.toString());

                        JSONObject ACCOUNTNO = jsonObject.optJSONObject("ACCOUNTNO");
                        JSONObject accountNoLimt = ACCOUNTNO.optJSONObject("accountNoLimt");
                        JSONObject accountNoStatus = ACCOUNTNO.optJSONObject("accountNoStatus");
                        String accountStatus = accountNoStatus.optString("status");
                        String newValue = accountNoStatus.optString("newValue");
                        String bankName = accountNoStatus.optString("bankName");
                        String bankNo = accountNoStatus.optString("bankNo");
                        String accountType = accountNoStatus.optString("accountType");
                        intent.putExtra("bankName", bankName);
                        intent.putExtra("bankNo", bankNo);
                        intent.putExtra("accountType", accountType);
                        intent.putExtra("newValue", newValue);
                        intent.putExtra("accountStatus", accountStatus);
                        String accountStatusName = accountNoStatus.optString("statusName");
                        intent.putExtra("accountStatusName", accountStatusName);
                        if (accountNoLimt != null && accountNoLimt.has("msg")) {
                            String accountLimit = accountNoLimt.optString("msg");
                            intent.putExtra("accountLimit", accountLimit);
                        }

                        JSONObject BUSINESSNAME = jsonObject.optJSONObject("BUSINESSNAME");
                        JSONObject businessNameLimt = BUSINESSNAME.optJSONObject("businessNameLimt");
                        if (businessNameLimt != null && businessNameLimt.has("msg")) {
                            String businessNameLimit = businessNameLimt.optString("msg");
                            intent.putExtra("businessNameLimit", businessNameLimit);
                        }

                        String cardAppType = jsonObject.optString("cardAppType");
                        intent.putExtra("cardAppType", cardAppType);

                        JSONObject lv1Status = jsonObject.optJSONObject("lv1Status");
                        String lv1StatusMsg = lv1Status.optString("msg");
                        intent.putExtra("lv1StatusMsg", lv1StatusMsg);

                        JSONObject lv2Status = jsonObject.optJSONObject("lv2Status");
                        String lv2StatusMsg = lv2Status.optString("msg");
                        intent.putExtra("lv2StatusMsg", lv2StatusMsg);
                        getDredgeStatus(context, intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    LogUtil.print(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        ServiceManagerUtil.getInstance().start(context, businessRequest);
    }

    public static void getMerchantStatus(final AppBaseActivity context, final Intent intent) {
        BusinessRequest businessRequest = RequestFactory.getRequest(context, RequestFactory.Type.UPDATE_MERCHAT_BUSINESSNAME);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                JSONObject jsonObject = null;
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        LogUtil.print("<><><>", "into2:" + jsonObject.toString());

                        JSONObject ACCOUNTNO = jsonObject.optJSONObject("ACCOUNTNO");
                        JSONObject accountNoLimt = ACCOUNTNO.optJSONObject("accountNoLimt");
                        JSONObject accountNoStatus = ACCOUNTNO.optJSONObject("accountNoStatus");
                        String accountStatus = accountNoStatus.optString("status");
                        String newValue = accountNoStatus.optString("newValue");
                        String bankName = accountNoStatus.optString("bankName");
                        String bankNo = accountNoStatus.optString("bankNo");
                        String accountType = accountNoStatus.optString("accountType");
                        intent.putExtra("bankName", bankName);
                        intent.putExtra("bankNo", bankNo);
                        intent.putExtra("accountType", accountType);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    LogUtil.print(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        ServiceManagerUtil.getInstance().start(context, businessRequest);
    }

    /**
     * 跳转功夫贷SDK
     *
     * @param context
     */
    public static void toGFDSDK(AppBaseActivity context) {
        if (context.getProgressDialog() != null && context.isProgressisShowing()) {
            context.hideProgressDialog();
        }
        String phone = ApplicationEx.getInstance().getUser().getLoginName();
        GFDAgent.getInstance().show(context, phone);
    }

    /**
     * 跳转功夫贷SDK
     *
     * @param context
     */
    public static void toGFDSDK1(AppBaseActivity context) {
        if (context.getProgressDialog() != null && context.isProgressisShowing()) {
            context.hideProgressDialog();
        }
        String phone = ApplicationEx.getInstance().getUser().getLoginName();
        GFDAgent.getInstance().show(context, phone);
        stopBankInfo();
    }

    /**
     * 跳转51人品贷SDK
     *
     * @param context
     */
    public static void to51DSDK(final AppBaseActivity context, final String tag) {
        context.showProgressWithNoMsg();
        final Handler mHander = new Handler(context.getMainLooper());
        //调用初始化;
        //InitializaU51LoanSDK.initLoanSDK(context, null);
        U51LocationHelper.getInstance().initialize(context);
        String usrId = ApplicationEx.getInstance().getUser().getUserId();
        if (TextUtils.isEmpty(usrId) || "0".equals(usrId)) {
            usrId = String.valueOf(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getId());
        }
        String appid = Constants.APP_ID_51DAI;
        String appkey = Constants.APP_KEY_51DAI;
        //请求授权,成功后使用U51LoanWebViewActivity.class打开贷款页面;
        InitializaU51LoanSDK.requestAccessToken(appid,
                appkey,
                usrId,
                new InitializaU51LoanSDK.SDKCallback() {
                    @Override
                    public void success(final String access_token, final String entry_url) {
                        mHander.post(new Runnable() {
                            @Override
                            public void run() {
                                context.hideProgressDialog();
                                Intent intent = new Intent(context, U51WebViewActivity.class);
                                intent.putExtra(U51WebViewActivity.EXTRA_URL, entry_url);
                                intent.putExtra(U51WebViewActivity.EXTRA_APPEND_COMMOM_PARAMS, true);
                                context.startActivity(intent);
                                if (tag.equals("CreditBankInfoModel")) {
                                    stopBankInfo();
                                }
                            }
                        });
                    }

                    @Override
                    public void error(final int code, final String msg) {
                        mHander.post(new Runnable() {
                            @Override
                            public void run() {
                                context.hideProgressDialog();
                                ToastUtil.toast(context, msg);
                            }
                        });
                    }
                });
    }

    public static void stopBankInfo() {
        XAtyTask.getInstance().killAty(CreditBankInfoActivity.class);
        XAtyTask.getInstance().killAty(CreditPersonInfoActivity.class);
/*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                XAtyTask.getInstance().killAty(CreditBankInfoActivity.class);
                XAtyTask.getInstance().killAty(CreditPersonInfoActivity.class);
            }
        },1000);
*/
    }


    /**
     * 跳转51人品贷SDK
     *
     * @param context
     */
    public static void to51DSDK1(final AppBaseActivity context) {
        final Handler mHander = new Handler(context.getMainLooper());
        //调用初始化;
        //InitializaU51LoanSDK.initLoanSDK(context, null);
        U51LocationHelper.getInstance().initialize(context);
        String usrId = ApplicationEx.getInstance().getUser().getUserId();
        if (TextUtils.isEmpty(usrId) || "0".equals(usrId)) {
            usrId = String.valueOf(ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getId());
        }
        String appid = Constants.APP_ID_51DAI;
        String appkey = Constants.APP_KEY_51DAI;
        //请求授权,成功后使用U51LoanWebViewActivity.class打开贷款页面;
        InitializaU51LoanSDK.requestAccessToken(appid,
                appkey,
                usrId,
                new InitializaU51LoanSDK.SDKCallback() {
                    @Override
                    public void success(final String access_token, final String entry_url) {
                        mHander.post(new Runnable() {
                            @Override
                            public void run() {
                                context.hideProgressDialog();
                                Intent intent = new Intent(context, U51WebViewActivity.class);
                                intent.putExtra(U51WebViewActivity.EXTRA_URL, entry_url);
                                intent.putExtra(U51WebViewActivity.EXTRA_APPEND_COMMOM_PARAMS, true);
                                context.startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void error(final int code, final String msg) {
                        mHander.post(new Runnable() {
                            @Override
                            public void run() {
                                context.hideProgressDialog();
                                ToastUtil.toast(context, msg);
                            }
                        });
                    }
                });
    }

    /**
     * @param context
     */
    public static void queryMerLevelInfo(final AppBaseActivity context) {
        BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type.QUERY_MerLevelAndLimit);
        request.setAutoShowToast(true);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    String data = resultServices.retData;
                    getUpdateStatus(context, data);
                } else {
                    context.hideProgressDialog();
                    context.toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                context.toast(context.getString(R.string.socket_fail));
            }
        });
        request.execute();
    }


    private static void getCouponStatus(final AppBaseActivity context) {
        CommonUtil.isMerchantValid(context);
        MerchantInfo merchantInfoData = ApplicationEx.getInstance().getUser().getMerchantInfo();
        ;
        switch (merchantInfoData.getMerchantStatus()) {
            case PROCESSING:
            case FAILURE:
            case COMPLETED:
                context.forwardActivity(CouponInputActivity.class);
                break;

        }
    }

    public enum Type {
        /**
         * 一元购
         */
        ONE_YUAN("yyg"),
        /**
         * 零钱首页
         */
        WALLET_HOME("lqsy"),
        /**
         * 刷卡收款
         */
        COLLECTION("sksk"),
        /**
         * 扫码收款
         */
        BARCODE_COLLECTION("smsk"),
        /**
         * 大额收款
         */
        LARGE_COLLECTION("desk"),
        /**
         * 撤销交易
         */
        REVOCATION("cxjy"),
        /**
         * 立即提款
         */
        D0("d0"),
        /**
         * 首页D0提款
         */
        D01("d01"),
        /**
         * 设备管理
         */
        DEVICEMANAGER("deviceManager"),
        /**
         * 信用卡还款
         */
        CREDIT("xykhk"),
        /**
         * 转账汇款
         */
        TRANSFER("zzhk"),
        /**
         * 手机充值
         */
        PHONE_RECHARGE("sjcz"),
        /**
         * 余额查询
         */
        BALANCE_QUERY("yecx"),
        /**
         * 理财
         */
        FINANCE("licai"),
        /**
         * 替你还
         */
        LOAN_PAY("tnh"),
        /**
         * 特权购买
         */
        TEQUAN("zxgm"),
        /**
         * 一块夺宝BankCardEntrance
         */
        TREASURE("1kuai"),
        /**
         * 用户信息
         */
        MERCHANT_MANAGER("shgl"),
//        /**升级服务*/
//        SERVICE_UPGRADE("shengji"),
        /**
         * 特约商户缴费
         */
        TE_YUE("teyue"),
        /**
         * 贷款列表及详情
         */
        LOAN_BUSINESS("loan_business"),
        /**
         * 密码管理
         */

        /**
         * 收款记录
         */
        COLLLECTION_RECORD("skjl"),

        AN_QUAN("anquan"),

        /**
         * 活动专区
         */
        HDZQ("hdzq"),

        /**
         * 关注拉卡拉
         */
        GUAN_ZHU("guanzhu"),

        /**
         * 招商加盟
         */
        JIA_MENG("jiameng"),

        /**
         * 购买收款宝
         */
        GOU_MAI("goumai"),

        /**
         * 升级状态
         */
        UPDATE_STATUS("shengji"),

        /**
         * 代金券
         */
        COUPON_STATUS("djqpay"),

        /**
         * 积分购
         */
        INTEGRAL("jfbuy"),

        /**
         * 一日贷
         */
        ONE_DAY_LOAN("yrd"),
        /**
         * 信用卡申办
         */
        APPlY_CREDITA_CARD("xyksb"),
        /**
         * 信用卡还款
         */
        REPAYMENT_CREDITA_CARD("xykhk"),
        /**
         * i贷款/平安普惠
         */
        LOAN_PAPH("paph"),
        /**
         * 功夫贷款
         */
        LOAN_GFD("gfd"),
        /**
         * 易分期
         */
        LOAN_YFQ("yfq"),
        /**
         * 贷款一日贷
         */
        DK_ONE_DAY_LOAN("DK_YRD"),

        /**
         * 活动
         */
        MARKET_ZONE("market_zone");

        private String keyValue;

        Type(String keyValue) {
            this.keyValue = keyValue;
        }

        public String getKeyValue() {
            return keyValue;
        }

        public static Type keyValueOf(String keyValue) {
            Type[] types = values();
            for (Type type : types) {
                if (TextUtils.equals(type.getKeyValue(), keyValue)) {
                    return type;
                }
            }
            return null;
        }
    }

}
