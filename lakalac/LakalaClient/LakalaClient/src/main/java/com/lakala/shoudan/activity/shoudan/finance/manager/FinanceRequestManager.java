package com.lakala.shoudan.activity.shoudan.finance.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.MerchantInfo;
import com.lakala.platform.bean.MerchantStatus;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.FrontPageActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.Bank;
import com.lakala.shoudan.activity.shoudan.finance.bean.BankCard;
import com.lakala.shoudan.activity.shoudan.finance.bean.BusinessState;
import com.lakala.shoudan.activity.shoudan.finance.bean.FundStateQryReturnData;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.QryBusiBankReturnData;
import com.lakala.shoudan.activity.shoudan.finance.bean.RequestUrlEnum;
import com.lakala.shoudan.activity.shoudan.finance.bean.TransDetailProInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.AddUserQuestionRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.CardQryRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.CheckPayPwdRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.DoPayRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.DoPaySwipeRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.DoPayWithdrawRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundContListRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundProdDetailQryRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundSignUpRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundStateQryRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundTransferInRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundValidateProdListRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundWeekIncomeQryRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.GenerateBillIdRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.GetSMSCodeRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.ProdListRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QryBusiBanksRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryCurrentCardInfoRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryLineNoRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryOTPPasswordRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryPayRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryRegularCardInfoRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.QueryWalletNoRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.SetPayPwdRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.ShortcutSignRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TcRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TradeListRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TransDetailRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.VerifyQuestionRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.VerifySMSCodeRequest;
import com.lakala.shoudan.activity.shoudan.finance.open.FinanceOpenActivity;
import com.lakala.shoudan.activity.shoudan.finance.trade.FinanceTransDetailActivity;
import com.lakala.shoudan.activity.shoudan.finance.trade.FundTranserInActivity;
import com.lakala.shoudan.activity.shoudan.finance.trade.ProductDetailsActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.net.volley.BaseRequest;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.net.volley.ServiceManager;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/9/2.
 */
public class FinanceRequestManager {
    private String statistic;
    /**
     * 理财token失效重新登陆
     */
    public static final String TOKEN_OUT_OF_DATE = "401";
    public static final String TOKEN_OUT_TO_REFRESH = "403";
    /**
     * 理财支付密码错误
     */
    public static final String PAYPWD_ERROR = "55";
    /**
     * 理财交易超时
     */
    public static final String TRAN_TIMEOUT = "FS1000";
    /**
     * 理财业务开通状态
     */
    private BusinessState businessState;
    private String telecode;
    private String incomeString;
    private String totalAssetsString;
    private HttpResponseListener queryAssetsEndListener;
    public static boolean isFinanceChange = false;

    public void clear() {
        businessState = null;
        telecode = null;
        incomeString = null;
        totalAssetsString = null;
        queryAssetsEndListener = null;
    }

    public String getIncomeString() {
        return incomeString;
    }

    public String getTotalAssetsString() {
        return totalAssetsString;
    }

    public String getTelecode() {
        return telecode;
    }

    public FinanceRequestManager setTelecode(String telecode) {
        this.telecode = telecode;
        return this;
    }

    public String getStatistic() {
        return statistic;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }

    private static FinanceRequestManager financeRequestManager = new FinanceRequestManager();

    public static FinanceRequestManager getInstance() {
        return financeRequestManager;
    }

    private FinanceRequestManager() {

    }

    public void realStartFinance(final AppBaseActivity context) {
        sendRequest(
                new BaseRequest(), RequestUrlEnum.STATE_QUERY, new HttpResponseListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {

                        if (returnHeader.isSuccess() && responseData != null) {

                            businessState = BusinessState.values()[responseData.optInt("State")];
                            switch (businessState) {

                                case NONE:
                                case DISABLED:
                                case FAILED:
                                    context.hideProgressDialog();
                                    User user = ApplicationEx.getInstance().getUser();
                                    if (user.getMerchantInfo().getMerchantStatus() == MerchantStatus.NONE) {
                                        DialogCreator.showMerchantNotOpenDialog(context);
                                    } else if (user.getMerchantInfo().getMerchantStatus() == MerchantStatus.COMPLETED) {
                                        toFrontPageActivity(context);
                                    } else {
                                        DialogCreator
                                                .createConfirmDialog(context, "确定", "您的商户认证审核未通过")
                                                .show();
                                    }

                                    break;
                                case ENABLED:
                                    incomeString = responseData.toString();
                                    queryAssets(context);
                                    break;
                            }
                        } else {
                            context.hideProgressDialog();
                            ToastUtil.toast(context, returnHeader.getErrMsg());
                        }
                    }

                    @Override
                    public void onErrorResponse() {

                        toastHttpError(context);
                        context.hideProgressDialog();
                    }
                }
        );
    }

    public void startFinance(final AppBaseActivity context) {
        startFinance(context, null);
    }

    public void startFinance(final AppBaseActivity context, HttpResponseListener
            queryAssetsEndListener) {
        context.showProgressWithNoMsg();
        this.queryAssetsEndListener = queryAssetsEndListener;
        TerminalKey.virtualTerminalSignUp(new TerminalKey.VirtualTerminalSignUpListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onError(String msg) {
                context.hideProgressDialog();
                ToastUtil.toast(context, msg);
            }

            @Override
            public void onSuccess() {
                realStartFinance(context);
            }
        });
    }

    private void setFinanceTelecode(final AppBaseActivity context) {
        queryLineNo(ApplicationEx.getInstance().getUser().getTerminalId(), new HttpResponseListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinished(ReturnHeader returnHeader,
                                   JSONObject responseData) {
                if (returnHeader.isSuccess()) {
                    String lineNo = responseData
                            .optString("LineNo", "");
                    setTelecode(lineNo);

                } else {
                    context.hideProgressDialog();
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                context.hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
            }
        });
    }

    /**
     * 购买理财
     *
     * @param context
     * @param productInfo
     * @param amount
     */
    public void buyFinance(final AppBaseActivity context, ProductInfo productInfo, String amount) {
        BusinessState state = getBusinessState();
        switch (state) {
            case NONE:
            case DISABLED:
            case FAILED://未开户
                fundStateQry(context);
                break;
            case ENABLED://已开户
                String startAmount = productInfo.getStartAmout();
                if (TextUtils.isEmpty(amount)) {
                    ToastUtil.toast(context, "请输入金额");
                    return;
                }
                double startAmountDouble = Double.parseDouble(startAmount);
                double amountDouble = Double.parseDouble(amount);
                if (amountDouble < startAmountDouble) {
                    ToastUtil.toast(context, "买入金额必须不小于起购金额！");
                    return;
                }
                if (amountDouble > 1000000) {
                    ToastUtil.toast(context, "您输入的金额超限，请重新输入");
                    return;
                }
                String doubleAmount = Util.formatTwo(amountDouble);
                fundTransferInMmt(context, productInfo, doubleAmount);
                break;
        }
    }


    /**
     * 跳转到理财首页
     *
     * @param context
     */
    private void toFrontPageActivity(Context context) {
        Intent intent = new Intent(context, FrontPageActivity.class);
        context.startActivity(intent);
    }

    private void queryAssets(final AppBaseActivity context) {
        sendRequest(
                new BaseRequest(), RequestUrlEnum.FUND_TOTAL, new HttpResponseListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {

                        context.hideProgressDialog();
                        if (returnHeader.isSuccess() && responseData != null) {
                            totalAssetsString = responseData.toString();
                            if (queryAssetsEndListener == null) {
                                toFrontPageActivity(context);//跳转到理财页面，
                                if (!TextUtils.isEmpty(statistic)) {
                                    PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance_HomePage, context);
                                }
                            }
                        } else {
                            ToastUtil.toast(context, returnHeader.getErrMsg());
                        }

                        if (queryAssetsEndListener != null) {
                            queryAssetsEndListener.onFinished(returnHeader, responseData);
                        }
                    }

                    @Override
                    public void onErrorResponse() {

                        toastHttpError(context);
                        context.hideProgressDialog();
                        if (queryAssetsEndListener != null) {
                            queryAssetsEndListener.onErrorResponse();
                        }
                    }
                }
        );
    }

    /**
     * 返回fundTotalQry接口的数据
     *
     * @param httpResponseListener
     */
    public void getTotalInfo(HttpResponseListener httpResponseListener) {
        sendRequest(new BaseRequest(), RequestUrlEnum.FUND_TOTAL, httpResponseListener);
    }

    /**
     * 签约验证
     */
    public void fundStateQry(final AppBaseActivity context) {
        FundStateQryRequest request = new FundStateQryRequest();
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                context.showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, final JSONObject responseData) {
                if (returnHeader.isSuccess()) {
                    final FundStateQryReturnData data = FundStateQryReturnData.parse(responseData);
                    List<BankCard> banks = data.getList();
                    boolean isBankInfoEquals = false;
                    MerchantInfo merchantInfo = ApplicationEx.getInstance().getUser().getMerchantInfo();
                    String accountNo = merchantInfo.getAccountNo();
                    if (banks != null) {
                        for (BankCard bankCard : banks) {
                            if (!TextUtils.equals(accountNo, bankCard.getAccountNo())) {
                                continue;
                            }
                            isBankInfoEquals = true;
                        }
                    }
                    if (isBankInfoEquals) {//如果信息一致，进入已签约页面
                        context.hideProgressDialog();
                        FinanceOpenActivity.start(context, false, responseData);
                        return;
                    }
                    queryBeijingBankCode(
                            new AccountSupportListener() {
                                @Override
                                public void isAccountSupport(boolean isSupport) {
                                    context.hideProgressDialog();
                                    if (isSupport) {//进入未签约页面
                                        FinanceOpenActivity.start(context, true, responseData);
                                    } else {
                                        DialogCreator.createConfirmDialog(
                                                context, "确定", "您的结算账号不支持理财开户"
                                        ).show();
                                    }
                                }
                            }, context);
                } else {
                    context.hideProgressDialog();
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                context.hideProgressDialog();
                toastHttpError(context);

            }
        };
        FinanceRequestManager.getInstance().fundStateQry(request, listener);
    }

    /**
     * 理财开通
     */
    public void startSignUp(final AppBaseActivity context, FundSignUpRequest request) {
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                context.showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                if (!isReturnValid(context, returnHeader)) {
                    return;
                }
                context.hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    DialogCreator.createOneConfirmButtonDialog(
                            context, "去购买", "恭喜！您已成功开通拉卡拉理财账户！赶快购买属于您的第一份理财产品吧！",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //跳转到产品详情
                                    FinanceRequestManager.getInstance().setBusinessState(BusinessState.ENABLED);
                                    BusinessLauncher.getInstance().clearTop
                                            (ProductDetailsActivity.class);
                                }
                            }
                    ).show();
                } else {
                    DialogCreator.createOneConfirmButtonDialog(
                            context, "确定", returnHeader.getErrMsg(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //返回理财首页
                                    BusinessLauncher.getInstance().clearTop(FrontPageActivity.class);
                                }
                            }
                    ).show();
                }
            }

            @Override
            public void onErrorResponse() {
                context.hideProgressDialog();
                toastHttpError(context);

            }
        };
        FinanceRequestManager.getInstance().fundSignUp(request, listener);
    }


    /**
     * 理财产品列表
     *
     * @param httpResponseListener
     */
    public void getProductList(HttpResponseListener httpResponseListener) {

        sendRequest(new ProdListRequest(), RequestUrlEnum.PRODUCT_LIST_QUERY, httpResponseListener);

    }

    public void getProductList(String productId, HttpResponseListener httpResponseListener) {
//        ProdListRequest  request = new ProdListRequest();
//        request.setProductId(productId);
//        sendRequest(request, RequestUrlEnum.PRODUCT_LIST_QUERY, httpResponseListener);
        sendRequest(
                new FundProdDetailQryRequest(productId), RequestUrlEnum.FUND_PROD_DETAIL_QRY,
                httpResponseListener
        );
    }

    public void getUnValidProductList(FundContListRequest request, HttpResponseListener listener) {

        sendRequest(request, RequestUrlEnum.FUND_CONTLIST_QRY, listener);
    }

    public void getTradeList(HttpResponseListener httpResponseListener) {
        sendRequest(new TradeListRequest(), RequestUrlEnum.FUND_TRADELIST_QRY, httpResponseListener);

    }

    /**
     * 收益率数据返回结果
     */
    public void getProfitInfo(String TradeType, String PageSize, HttpResponseListener httpResponseListener) {
        sendRequest(new FundWeekIncomeQryRequest(TradeType, PageSize), RequestUrlEnum.FUND_WEEK_INCOMEQRY, httpResponseListener);
    }

    /**
     * 签约验证
     *
     * @param request
     * @param listener
     */
    public void fundStateQry(FundStateQryRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.FUND_STATE_QRY, listener);
    }

    /**
     * 理财业务开通
     *
     * @param listener
     */
    private void fundSignUp(FundSignUpRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.FUND_SIGN_UP, listener);
    }

    /**
     * 获取代收签约验证码
     *
     * @param request
     * @param listener
     */
    public void queryOTPPassword(QueryOTPPasswordRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.QUERY_OTP_PASSWORD, listener);
    }

    /**
     * 代收签约
     *
     * @param request
     * @param listener
     */
    public void shortCutSign(ShortcutSignRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.SHORT_CUT_SIGN, listener);
    }

    /**
     * 查询活期签约卡信息
     */
    public void queryCardMsg(QueryCurrentCardInfoRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.INIT_FUND_TRANSFER_CARD, listener);
    }

    /**
     * 查询定期签约卡信息
     */
    public void queryCardMsg(QueryRegularCardInfoRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.INIT_FUND_TRANSFER_CARD, listener);
    }

    /**
     * 赎回生成订单id
     *
     * @param request
     * @param listener
     */
    public void generateBillId(GenerateBillIdRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.FUND_TRANSFER_CARD_BILL, listener);
    }

    /**
     * 查询钱包账号
     *
     * @param request
     * @param listener
     */
    public void queryWalletNo(QueryWalletNoRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.QUERY_WALLET_NO, listener);
    }

    /**
     * 赎回
     *
     * @param request
     * @param listener
     */
    public void doPay(DoPayWithdrawRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.DO_PQY, listener);
    }

    /**
     * 刷卡、插卡买入
     *
     * @param request
     * @param listener
     */
    public void doPaySwipe(DoPaySwipeRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.DO_PQY, listener);
    }

    /**
     * 买入
     *
     * @param request
     * @param listener
     */
    public void doPay(DoPayRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.DO_PQY, listener);
    }

    /**
     * 获取理财产品交易记录
     *
     * @param request
     * @param listener
     */

    public void getFinancialRecord(TransDetailRequest request, HttpResponseListener listener) {

        sendRequest(request, RequestUrlEnum.QUERY_FUND_RECORD, listener);
    }

    /**
     * 获取理财全部产品列表
     *
     * @param request
     * @param listener
     */
    public void getProdList(FundValidateProdListRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.FUND_PROD_LIST, listener);
    }

    public void setPayPwd(SetPayPwdRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.SET_PAY_PWD, listener);
    }

    /**
     * 查询支持理财银行列表
     *
     * @param listener
     */
    public void qryBusiBanks(HttpResponseListener listener) {
        qryBusiBanks(null, listener);
    }

    public void qryBusiBanks(String busid, HttpResponseListener listener) {
        QryBusiBanksRequest request = new QryBusiBanksRequest();
        if (busid != null) {
            request.setBusId(busid);
        }
        sendRequest(request, RequestUrlEnum.QRY_BUSI_BANKS, listener);
    }

    private void queryBeijingBankCode(final AccountSupportListener listener, final AppBaseActivity context) {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                String bankCode = ApplicationEx.getInstance().getUser().getMerchantInfo().getBankNo();
                if (resultServices == null) {
                    ToastUtil.toast(context, R.string.socket_fail);
                    context.hideProgressDialog();
                    return;
                }
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonObject == null) {
                        return;
                    }
                    jsonObject = jsonObject
                            .optJSONObject("OLD_BANK_CODE_MAP");
                    if (jsonObject != null) {
                        bankCode = jsonObject.optString(
                                bankCode, bankCode
                        );
                    }
                    ApplicationEx.getInstance().getUser().setBankNoBeijing(bankCode);
                    isAccountSupport(context, listener);
                } else {
                    context.hideProgressDialog();
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
                context.hideProgressDialog();

            }
        };
        ShoudanService.getInstance().getOLD_BANK_CODE_MAP(callback);
    }


    private void isAccountSupport(final AppBaseActivity context, final AccountSupportListener listener) {
        qryBusiBanks(
                new HttpResponseListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                        if (returnHeader.isSuccess()) {
                            QryBusiBankReturnData data = QryBusiBankReturnData.parse(responseData);
                            List<Bank> list = data.getList();
                            boolean isSupport = false;
                            String bankNo = ApplicationEx.getInstance().getUser().getBankNoBeijing();
                            if (list != null) {
                                for (Bank bank : list) {
                                    if (TextUtils.equals(bankNo, bank.getBankCode())) {
                                        isSupport = true;
                                        break;
                                    }
                                }
                            }
                            listener.isAccountSupport(isSupport);
                        } else {
                            ToastUtil.toast(context, returnHeader.getErrMsg());
                        }
                    }

                    @Override
                    public void onErrorResponse() {
                        toastHttpError(context);

                    }
                }
        );
    }

    /**
     * 查询密保问题列表
     *
     * @param listener
     */
    public void questionListQry(HttpResponseListener listener) {
        sendRequest(new BaseRequest(), RequestUrlEnum.QUESTION_LIST_QRY, listener);
    }

    /**
     * 查询用哦户密保问题
     *
     * @param listener
     */
    public void userQuestionQry(HttpResponseListener listener) {
        sendRequest(new BaseRequest(), RequestUrlEnum.USER_QUESTION_QRY, listener);
    }

    /**
     * 新增用户密保问题
     *
     * @param request
     * @param listener
     */
    public void addUserQuestion(AddUserQuestionRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.ADD_USER_QUESTION, listener);
    }

    /**
     * 校验用户密保问题
     *
     * @param request
     * @param listener
     */
    public void verifyQuestion(VerifyQuestionRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.VERIFY_QUESTION, listener);
    }

    /**
     * 校验支付密码
     *
     * @param request
     * @param listener
     */
    public void checkPayPwd(CheckPayPwdRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.CHECK_PAY_PWD, listener);
    }

    /**
     * 查询账单信息--买入方式
     */
    public void queryPay(String BillId, HttpResponseListener listener) {
        QueryPayRequest request = new QueryPayRequest();
        request.setBillId(BillId);
        sendRequest(request, RequestUrlEnum.QUERY_PAY, listener);
    }

    /**
     * 查询账单信息--买入方式
     *
     * @param context
     */
    public void queryPay(final AppBaseActivity context, final Bundle bundle) {
        QueryPayRequest request = new QueryPayRequest();
        request.setBillId(bundle.getString("BillId"));
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                context.hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    bundle.putString("queryPay", responseData.toString());
                    FundTranserInActivity.open(bundle, context);
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                context.hideProgressDialog();

                ToastUtil.toast(context, R.string.socket_fail);
            }
        };
        sendRequest(request, RequestUrlEnum.QUERY_PAY, listener);
    }

    public void fundTransferInMmt(final AppBaseActivity context, ProductInfo productInfo, String amount) {
        FundTransferInRequest request = new FundTransferInRequest();
        request.setProductId(productInfo.getProductId());
        request.setAmount(amount);
        final Bundle bundle = new Bundle();
        bundle.putString("productInfo", JSON.toJSONString(productInfo));
        bundle.putString("amount", amount);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                context.showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                if (returnHeader.isSuccess()) {
                    String BillId = responseData.optString("BillId", "");
                    bundle.putString("BillId", BillId);
                    queryPay(context, bundle);
                } else {
                    context.hideProgressDialog();
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                context.hideProgressDialog();

                ToastUtil.toast(context, R.string.socket_fail);
            }
        };
        sendRequest(request, RequestUrlEnum.FUND_TRANSFERIN_MMT, listener);
    }

    /**
     * 异步TC
     *
     * @param request
     * @param listener
     */
    public void asyTransTc(TcRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.ASY_TRANS_TC, listener);
    }

    /**
     * 同步TC
     *
     * @param request
     * @param listener
     */
    public void sncTransTc(TcRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.SNC_TRANS_TC, listener);
    }

    /**
     * 生成订单
     *
     * @param listener
     */
    public void fundTransferInMmt(FundTransferInRequest request, HttpResponseListener listener) {
        sendRequest(request, RequestUrlEnum.FUND_TRANSFERIN_MMT, listener);
    }

    /**
     * 获取线路号
     *
     * @param listener
     */
    public void queryLineNo(HttpResponseListener listener) {
        QueryLineNoRequest request = new QueryLineNoRequest();
        request.setTerminalId(ApplicationEx.getInstance().getUser().getTerminalId());
        sendRequest(request, RequestUrlEnum.QUERY_LINE_NO, listener);
    }

    public void queryLineNo(String termid, HttpResponseListener listener) {
        QueryLineNoRequest request = new QueryLineNoRequest();
        request.setTerminalId(termid);
        sendRequest(request, RequestUrlEnum.QUERY_LINE_NO, listener);
    }

    /**
     * 获取短信验证码--找回支付密码
     *
     * @param listener
     */
    public void getSMSCode(HttpResponseListener listener) {
        sendRequest(new GetSMSCodeRequest(), RequestUrlEnum.GET_SMS_CODE, listener);
    }

    /**
     * 获取短信验证码--快捷签约
     *
     * @param listener
     */
    public void getSMSCodeBindingCard(HttpResponseListener listener) {
        GetSMSCodeRequest request = new GetSMSCodeRequest();
        request.setBusinessCode(228104);
        sendRequest(request, RequestUrlEnum.GET_SMS_CODE, listener);
    }

    /**
     * 验证短信验证码--找回支付密码
     *
     * @param code     收到的短信验证码
     * @param listener
     */
    public void verifySMSCode(String code, HttpResponseListener listener) {
        VerifySMSCodeRequest request = new VerifySMSCodeRequest();
        request.setSMSCode(code);
        sendRequest(request, RequestUrlEnum.VERIFY_SMS_CODE, listener);
    }

    /**
     * 根据卡bin获取卡信息
     *
     * @param cardNo
     * @param listener
     */
    public void cardQry(String cardNo, HttpResponseListener listener) {
        CardQryRequest request = new CardQryRequest();
        request.setCreditcardNo(cardNo);
        sendRequest(request, RequestUrlEnum.CARD_QRY, listener);
    }

    public void countEarnDate(HttpResponseListener listener) {
        sendRequest(new BaseRequest(), RequestUrlEnum.COUNT_EARN_DATE, listener);
    }

    public BusinessState getBusinessState() {
        return businessState;
    }

    public void setBusinessState(BusinessState businessState) {
        this.businessState = businessState;
    }

    private <T extends BaseRequest> void sendRequest(T baseRequest, RequestUrlEnum requestUrlEnum, HttpResponseListener listener) {
        ServiceManager.getInstance().sendRequest(baseRequest, requestUrlEnum, listener);
    }


    private void toastHttpError(AppBaseActivity context) {

        context.hideProgressDialog();
        ToastUtil.toast(context, "您的网络不太好，请稍候重试");

    }

    public interface AccountSupportListener {
        void isAccountSupport(boolean isSupport);
    }

    public static boolean isReturnValid(AppBaseActivity activity, ReturnHeader returnHeader) {
        activity.hideProgressDialog();
        boolean isRight = true;
        if (FinanceRequestManager.PAYPWD_ERROR.equals(returnHeader.getRetCode())) {
            DialogCreator.createConfirmDialog(activity, "确定", returnHeader.getErrMsg()).show();
            isRight = false;
        }
        return isRight;
    }

    public void toTransDetail(AppBaseActivity context) {
        toTransDetail(context, false);
    }

    /**
     * @param context
     * @param detailBackToFront 交易明细页面，返回时是否直接返回到理财首页   true:直接返回到理财首页
     */
    public void toTransDetail(final AppBaseActivity context, final boolean detailBackToFront) {
        FundValidateProdListRequest request = new FundValidateProdListRequest();
        request.setMobile(ApplicationEx.getInstance().getUser().getLoginName());
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                context.showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                List<TransDetailProInfo> proList = new ArrayList<TransDetailProInfo>();
                if (returnHeader.isSuccess()) {
                    try {
                        JSONArray jsonArray = responseData.getJSONArray("ProdList");
                        int isDefaultId = 0;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            TransDetailProInfo info = new TransDetailProInfo();
                            info.setProName(jsonObject.optString("ProdName"));
                            info.setIsDefault(jsonObject.optInt("IsDefault"));
                            if (jsonObject.optInt("IsDefault") == 1) {
                                isDefaultId = i;
                            }
                            info.setProductId(jsonObject.optString("ProductId"));
                            proList.add(info);
                        }
                        getRecord(context, proList, isDefaultId, detailBackToFront);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                    context.hideProgressDialog();
                }
            }

            @Override
            public void onErrorResponse() {
                context.hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().getProdList(request, listener);
    }

    private void getRecord(final AppBaseActivity context, final List<TransDetailProInfo> proList,
                           final int position, final boolean detailBackToFront) {
        String productId = proList.get(position).getProductId();
        TransDetailRequest request = new TransDetailRequest();
        request.setPageCount(String.valueOf(FinanceTransDetailActivity.PAGECOUNT));
        request.setPageSize(String.valueOf(FinanceTransDetailActivity.PAGESIZE));
        request.setProductId(productId);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                context.hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    Intent intent = new Intent(context, FinanceTransDetailActivity.class);
                    String data = responseData.toString();
                    intent.putExtra("data", data);
                    intent.putExtra("position", position);
                    intent.putExtra("proList", (Serializable) proList);
                    intent.putExtra("backToFront", detailBackToFront);
                    context.startActivity(intent);
                } else {
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                context.hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().getFinancialRecord(request, listener);
    }
}
