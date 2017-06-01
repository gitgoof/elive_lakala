package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lakala.library.encryption.Mac;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.BaseFinanceActivity;
import com.lakala.shoudan.activity.shoudan.finance.BindingCardActivity;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.ApplyInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.PaymentTypesEntity;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.DoPayRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundStateQryRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.wallet.request.MyBankCardRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.DateUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.PayType;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.BaseDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LMQ on 2015/10/16.
 * 理财买入页面
 */
public class FundTranserInActivity extends BaseFinanceActivity {

    private static final int INPUT_PWD_REQUEST = 0x1231;
    private static final int BIND_CARD_REQUEST = 0x3821;
    private TextView tvProdName;
    private TextView tvTranserinAmount;
    private TextView tvTips1;
    private TextView tvTranserinType;
    private TextView tvAgreement1;
    private CheckBox cboxAgreement2;
    private TextView tvAgreement2;
    private CheckBox cboxAgreement3;
    private TextView tvAgreement3;
    private TextView transerIn;
    private ProductInfo productInfo;
    private String BillId;
    private String Amount;
    private ProdType prodType;
    private List<PayType> types = new ArrayList<>();
    private PayType selectedType;
    private ApplyInfo applyInfo = new ApplyInfo();
    private CompoundButton.OnCheckedChangeListener cboxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            transerIn.setEnabled(isChecked);
        }
    };
    private int outMode = 2;
    private BaseDialog dialog;
    public static void open(Bundle bundle, Context context) {
        Intent intent = new Intent(context, FundTranserInActivity.class);
        if (bundle != null) {
            intent.putExtra(Constants.IntentKey.TRANS_INFO, bundle);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_transerin);
        Bundle bundle = getIntent().getBundleExtra(Constants.IntentKey.TRANS_INFO);
        if (bundle != null) {
            String productStr = bundle.getString("productInfo");
            productInfo = ProductInfo.parser(productStr);
            String queryPay = bundle.getString("queryPay");
            initTypes(queryPay);
            BillId = bundle.getString("BillId");
            Amount = bundle.getString("amount");
        }
        if (productInfo == null) {
            return;
        }
        prodType = ProdType.values()[productInfo.getProdType()];
        initUI();
        initView();
        initApplyInfo();
        //先获取签名及用户级别
        checkWalletTerminal();
    }

    private void initApplyInfo() {
        applyInfo.setProductName(productInfo.getProdName());
        applyInfo.setProductId(productInfo.getProductId());
        applyInfo.setPeriod(productInfo.getPeriod());
        applyInfo.setAmount(Amount);
    }

    private void setSelectedType(int position) {
        PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance_Product_purchaseStyle, context);
        if (position < 0 || position >= types.size()) {
            return;
        }
        PayType type = types.get(position);
        if (!TextUtils.isEmpty(type.PaymentType)) {
            selectedType = type;
            tvTranserinType.setText(selectedType.getShowText());
        } else {//使用新卡支付
            fundStateQry();
        }
    }

    public void fundStateQry() {
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (returnHeader.isSuccess()) {
                    if (responseData == null) {
                        return;
                    }
                    String name = responseData.optString("CustomerName", "");
                    String idcard = responseData.optString("Identifier", "");
                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(idcard)) {
                        return;
                    }
                    BindingCardActivity.OpenParams params = new BindingCardActivity.OpenParams();
                    params.setName(name).setIdcard(idcard);
                    BindingCardActivity.open(context, BIND_CARD_REQUEST, params);
                } else {
                    toast(returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
            }
        };
        FinanceRequestManager.getInstance().fundStateQry(new FundStateQryRequest(), listener);
    }

    /**
     * 获取用户级别前置
     */
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
                //获取用户级别
                getUserType();
            }
        });

    }
    /**
     * 获取用户级别
     */
    private void getUserType() {
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
                        String type=jsonObject.optString("userTypeInfo");
                        if(!TextUtils.isEmpty(type)){
                            if("EWALLET_USERTYPE_THIRD".equals(type)){
                                //如果为3类用户，则隐藏话术
                                ApplicationEx.getInstance().getUser().setUserType(0);

                            }else{

                                ApplicationEx.getInstance().getUser().setUserType(1);
                            }
                        }else{
                            ApplicationEx.getInstance().getUser().setUserType(1);
                        }

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
                checkWalletTerminal();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    private void initTypes(String queryPay) {
        types.clear();
        try {
            JSONObject jsonObject = new JSONObject(queryPay);
            JSONArray jsonArray = jsonObject.getJSONArray("PaymentTypes");
            if (jsonArray == null) {
                return;
            }
            HashMap<String, JSONObject> typeMaps = new HashMap<String, JSONObject>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String type = obj.optString("PaymentType", "");
                typeMaps.put(type, obj);
            }
            if (typeMaps.containsKey("D")) {
                jsonObject = typeMaps.get("D");
                jsonArray = jsonObject.getJSONArray("List");
                if (jsonArray != null && jsonArray.length() != 0) {
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if (!obj.isNull("ShortCards") && obj.getJSONArray("ShortCards").length() != 0) {
                        JSONArray array = obj.getJSONArray("ShortCards");
                        for (int i = 0; i < array.length(); i++) {
                            PayType type = new PayType();
                            types.add(type);
                            type.PaymentTypeName = jsonObject.optString("PaymentTypeName", "");
                            type.PaymentType = jsonObject.optString("PaymentType", "");
                            JSONObject jobj = array.getJSONObject(i);
                            if (jobj != null) {
                                type.shortCard = PayType.ShortCard.parseFinance(jobj);
                            }
                        }
                    }
                }
            }
            if (typeMaps.containsKey("C")) {
                jsonObject = typeMaps.get("C");
                PayType type = new PayType();
                types.add(type);
                type.PaymentType = jsonObject.optString("PaymentType", "C");
                type.PaymentTypeFlag = jsonObject.optString("PaymentTypeFlag", "1");
                type.PaymentTypeName = jsonObject.optString("PaymentTypeName", "刷卡支付");
            }
            PayType type = new PayType();
            types.add(type);
            type.PaymentTypeName = "使用新卡支付";
            type.PaymentTypeFlag = "-1";
            type.PaymentType = "";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    enum ProdType {
        活期, 定期
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("购买理财");
        navigationBar.setActionBtnText("规则");
        PublicToEvent.FinalEvent( ShoudanStatisticManager.Finance_Product_takeOutSuccess, context);
        navigationBar.setOnNavBarClickListener(
                new NavigationBar.OnNavBarClickListener() {
                    @Override
                    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                        switch (navBarItem) {
                            case back:
                                onBackPressed();
                                break;
                            case action:
                                if (productInfo.getProductId().equals("KL1H00001")) {//理财1号
                                    ProtocalActivity.open(context, ProtocalType.LC_LC1NOTE);
                                } else if (productInfo.getProductId().equals("000686")) {//理财2号
                                    ProtocalActivity.open(context, ProtocalType.LC_LC2NOTE);
                                } else if (productInfo.getProductId().equals("KL1H00002")) {//理财3号
                                    ProtocalActivity.open(context, ProtocalType.LC_LC3NOTE);
                                }
                                break;
                        }
                    }
                }
        );
    }

    private void initView() {
        tvProdName = (TextView) findViewById(R.id.tv_prod_name);
        tvTranserinAmount = (TextView) findViewById(R.id.et_transerin_amount);
        tvTips1 = (TextView) findViewById(R.id.tv_tips1);
        tvTranserinType = (TextView) findViewById(R.id.tv_transerin_type);
        tvAgreement1 = (TextView) findViewById(R.id.tv_agreement1);
        cboxAgreement2 = (CheckBox) findViewById(R.id.cbox_agreement2);
        tvAgreement2 = (TextView) findViewById(R.id.tv_agreement2);
        cboxAgreement3 = (CheckBox) findViewById(R.id.cbox_agreement3);
        tvAgreement3 = (TextView) findViewById(R.id.tv_agreement3);
        transerIn = (TextView) findViewById(R.id.transerIn);
        tvTranserinAmount.setText(Amount);
        TextView tvPredictIncome = (TextView) findViewById(R.id.tv_predict_income);
        View llIncome = findViewById(R.id.ll_income);
        View llAgreement2 = findViewById(R.id.ll_agreement2);
        View llAgreement3 = findViewById(R.id.ll_agreement3);
        switch (prodType) {
            case 活期:
                countEarnDate();
                llIncome.setVisibility(View.GONE);
                llAgreement2.setVisibility(View.VISIBLE);
                llAgreement3.setVisibility(View.GONE);
                cboxAgreement2.setOnCheckedChangeListener(cboxListener);
                break;
            case 定期:
                llAgreement2.setVisibility(View.GONE);
                llAgreement3.setVisibility(View.VISIBLE);
                cboxAgreement3.setOnCheckedChangeListener(cboxListener);
                tvTips1.setText(
                        "该产品为不可变现产品，封闭期间不可提前取出，产品到期后的两个工作日一次性还本付息，" + "本金和收益自动取出至赎回卡内"
                );
                double YearOrofit = productInfo.getGrowthRate();
                String day = productInfo.getLimitTime();
                if (!TextUtils.isEmpty(day)) {
                    //每百元预期收益
                    double data = Double.parseDouble(Amount) * (YearOrofit / 100) * Integer
                            .parseInt(day) / 365;
                    tvPredictIncome.setText(Util.formatTwo(data));//保留两位小数
                    llIncome.setVisibility(View.VISIBLE);
                }
                break;
        }

        tvTranserinType.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (types == null) {
                            return;
                        }
                        DialogCreator.showPayChooseTypeDialog(
                                context, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        setSelectedType(which);
                                    }
                                }, types
                        );
                    }
                }
        );
        transerIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedType == null) {
                            toast("请选择支付方式");
                            return;
                        }
//                        TransactionManager.getInstance()
//                                          .setTransType(TransactionManager.TransType.APPLY);
                        if ("D".equals(selectedType.PaymentType)) {
                            startInputPwd();
                        } else if ("C".equals(selectedType.PaymentType)) {
                            doPaySwipe();
                        }
                    }
                }
        );

        if (productInfo == null) {
            return;
        }
        tvProdName.setText("买入" + productInfo.getProdName());
        initProtocal();
    }

    private void initProtocal() {
        tvAgreement1.setText(ProtocalType.LC_QYKZFXE.getTitle());
        tvAgreement1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProtocalActivity.open(context, ProtocalType.LC_QYKZFXE);
                    }
                }
        );
        tvAgreement2.setText(ProtocalType.LC2_ALL_SG.getTitle());
        tvAgreement2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //建信基金管理公司网上交易协议
                        ProtocalActivity.open(context, ProtocalType.LC2_ALL_SG);
                    }
                }
        );

        ProtocalType protocalType = ProtocalType.LC1_ALL_SG;
        View.OnClickListener protocalListener = null;
        if (TextUtils.equals(productInfo.getProductId(), "KL1H00001")) {
            //理财1号
            protocalType = ProtocalType.LC1_ALL_SG;
            protocalListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProtocalActivity.open(context, ProtocalType.LC1_ALL_SG);
                }
            };
        } else if (TextUtils.equals(productInfo.getProductId(), "KL1H00002")) {
            //理财3号
            protocalType = ProtocalType.LC3_ALL_SG;
            protocalListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProtocalActivity.open(context, ProtocalType.LC3_ALL_SG);
                }
            };
        }
        tvAgreement3.setText(protocalType.getTitle());
        tvAgreement3.setOnClickListener(protocalListener);
    }

    private void doPaySwipe() {
        applyInfo.setBillid(BillId);
        applyInfo.setLastPaymentType(selectedType.PaymentType);
        applyInfo.setPeriod(productInfo.getPeriod());
        applyInfo.setOutMode(outMode);
        PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance_Product_Swipe, context);
        Intent intent = new Intent(context, FinanceSwipeBuyActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, applyInfo);
        startActivity(intent);
    }

    private void doPay(String pwd) {
        final DoPayRequest request = new DoPayRequest();
        request.setBillId(BillId);
        request.setAmount(Amount);
        request.setBusid(DoPayRequest.BusIdType.申购);
        request.setProductId(productInfo.getProductId());
        request.setPan(selectedType.shortCard.getAccountNo());
        request.setTrsPassword(pwd);
        request.setLastPaymentType(selectedType.PaymentType);
        List<PaymentTypesEntity> types = new ArrayList<PaymentTypesEntity>();
        PaymentTypesEntity type = new PaymentTypesEntity();
        types.add(type);
        type.setAccountType(Integer.parseInt(selectedType.shortCard.getAccountType()));
        type.setCardId(Long.parseLong(selectedType.shortCard.getCardId()));
        type.setPaymentAmount(Amount);
        type.setPaymentType(selectedType.PaymentType);
        type.setProductId(productInfo.getProductId());
        type.setRandom(Mac.getRnd());
        request.setPaymentTypes(types);
        request.setFee(0);
        request.setPeriod(productInfo.getPeriod());
        request.setOutMode(outMode);
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
                applyInfo.setTime(DateUtil.formatSystemDate("yyyy/MM/dd HH:mm:ss"));
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                hideProgressDialog();
                if (!FinanceRequestManager.isReturnValid(context, returnHeader)) {
                    return;
                }
                if (FinanceRequestManager.TRAN_TIMEOUT.equals(returnHeader.getRetCode())) {
                    startTranTimeout(returnHeader.getErrMsg(), applyInfo);
                    return;
                }
                if (returnHeader.isSuccess()) {
                    applyInfo.setAmount(responseData.optString("Amount", "0"));
                    applyInfo.setJnlId(responseData.optString("JnlId"));
                    String date = responseData.optString("JnlDate");
                    applyInfo.setTime(
                            DateUtil.formatDateStrToPattern(
                                    date, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"
                            )
                    );
                    PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance_Product_Swipe, context);
                    applyInfo.setTransResult(TransResult.SUCCESS);
                } else {
                    PublicToEvent.FinalEvent(ShoudanStatisticManager.Finance_Product_Swipe_Error, context);
                    applyInfo.setTime(DateUtil.formatSystemDate("yyyy/MM/dd HH:mm:ss"));
                    applyInfo.setTransResult(TransResult.FAILED);
                    applyInfo.setMsg(returnHeader.getErrMsg());
                }

                Intent intent = new Intent(context, ConfirmResultActivity.class);
                intent.putExtra(Constants.IntentKey.TRANS_INFO, applyInfo);
                startActivity(intent);
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();

                startTranTimeout(getString(R.string.socket_error), applyInfo);
            }
        };
        FinanceRequestManager.getInstance().doPay(request, listener);
    }

    private void countEarnDate() {
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                if (returnHeader.isSuccess()) {
                    String countDate = responseData.optString("CountDate", "");
                    String earnDate = responseData.optString("EarnDate", "");
                    SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
                    try {
                        Date date1 = parse.parse(countDate);
                        countDate = format.format(date1);
                        Date date2 = parse.parse(earnDate);
                        earnDate = format.format(date2);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("预计").append(countDate).append("产生收益，")
                                .append(earnDate).append("可查看收益");
                        tvTips1.setText(stringBuilder.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    toast(returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {

            }
        };
        FinanceRequestManager.getInstance().countEarnDate(listener);
    }

    private void startInputPwd() {
        Intent intent = new Intent(context, InputPayPwdDialogActivity.class);
        startActivityForResult(intent, INPUT_PWD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INPUT_PWD_REQUEST && resultCode == RESULT_OK) {
            String pwd = data.getStringExtra(Constants.IntentKey.PASSWORD);
            doPay(pwd);
        } else if (requestCode == BIND_CARD_REQUEST && resultCode == RESULT_OK) {
            HttpResponseListener listener = new HttpResponseListener() {
                @Override
                public void onStart() {
                    showProgressWithNoMsg();
                }

                @Override
                public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                    hideProgressDialog();
                    if (returnHeader.isSuccess()) {

                        //类型设定
                        initTypes(responseData.toString());
                        //再查看下级别
                        checkWalletTerminal();

//                        tvTranserinType.performClick();

                    } else {
                        toast(returnHeader.getErrMsg());
                    }
                }

                @Override
                public void onErrorResponse() {
                    hideProgressDialog();
                    ToastUtil.toast(FundTranserInActivity.this,R.string.socket_fail);
                }
            };
            FinanceRequestManager.getInstance().queryPay(BillId, listener);
        }
    }
}
