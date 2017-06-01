package com.lakala.shoudan.activity.treasure;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.library.encryption.Mac;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
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
import com.lakala.shoudan.activity.password.SetPaymentPasswordActivity;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.BankCardAdditionActivity;
import com.lakala.shoudan.activity.shoudan.bankitcard.activity.BankCardEntranceActivity;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.BankCardBean;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.wallet.WalletHomeActivity;
import com.lakala.shoudan.activity.wallet.WalletRechargeActivity;
import com.lakala.shoudan.activity.wallet.bean.RedPackageInfo;
import com.lakala.shoudan.activity.wallet.bean.WalletPaymentTypes;
import com.lakala.shoudan.activity.wallet.request.GetComPrensiveInfoRequest;
import com.lakala.shoudan.activity.wallet.request.MyBankCardRequest;
import com.lakala.shoudan.activity.wallet.request.PayTypeParams;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTradeRequest;
import com.lakala.shoudan.activity.webbusiness.WebMallContainerActivity;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.datadefine.PayType;
import com.lakala.ui.component.LabelTextView;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.dialog.BaseDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LMQ on 2015/11/19.
 */
public class TreasureBuyActivity extends AppBaseActivity{
    private static final int PAY_PWD_REQUEST = 0x2341;
    private static final int DEFAULT_TYPE_POSITION = 0;
    private static final double WALLET_NEED_PAY = 0;//零钱最少需要支付的金额
    private int typePosition = DEFAULT_TYPE_POSITION;
    private TextView tvBuyTypeName;
    private ImageView ivBtnArrow1;
    private TextView tvRedPackage;
    private ImageView ivBtnArrow2;
    private TextView idCommonGuideButton;
    private TextView tvToRechange;
    private PartnerPayResponse mData;
    private String dataStr = null;
    private LabelTextView prodInfo;
    private LabelTextView amountInfo;
    private List<PayType> buyTypes = new ArrayList<>();
    private PayType selectedBuyType;
    private View layoutRedPkg;
    private List<RedPackageInfo.GiftListEntity> giftList = new ArrayList<>();
    private HashMap<String, PartnerPayResponse.PaymentType> typeMap = new HashMap<>();;
    private String pwdTmp;
    private RedPackageInfo.GiftListEntity selectedGift;
    private Double walletBalance = 0d;
    private boolean updateOnResume = false;
    private TreasureTransInfo transInfo;
    private boolean needWalletRechange = false;
    private TextView tvHint;
    private String giftNo;
    private BaseDialog dialog;
    public static void open(Context context,String data){
        Intent intent = new Intent(context,TreasureBuyActivity.class);
        intent.putExtra("data",data);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_buy);
        String data = getIntent().getStringExtra("data");
        try {
            JSONObject jsonObject = new JSONObject(data);
            mData = PartnerPayResponse.parse(jsonObject);
            dataStr = data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initUI();
        initData();
        initListener();
        TransactionType.WALLET_RECHARGE.setMainClazz(getClass());

        transInfo = new TreasureTransInfo();
        transInfo.setAmount(mData.getOrderAmount());
        transInfo.setBillId(mData.getBillId());
        transInfo.setLakalaOrderId(mData.getLakalaOrderId());
        transInfo.setProductName(mData.getSubject());
        transInfo.setParams(mData.getParams());
        transInfo.setNotifyURL(mData.getNotifyURL());
        transInfo.setCallbackUrl(mData.getCallBackURL(),mData.getOutOrderNo());

        //先获取签名及用户级别
        checkWalletTerminal();


    }
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
    @Override
    protected void onResume() {
        super.onResume();
        if(updateOnResume){
            //检查级别
            checkWalletTerminal();
            //查询银行卡类型
            queryPayTypes();
            updateOnResume = false;
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("产品购买");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                switch (navBarItem){
                    case back:
                        onBackPressed();
                        break;
                }
            }
        });
        prodInfo = (LabelTextView)findViewById(R.id.prodInfo);
        amountInfo = (LabelTextView)findViewById(R.id.amountInfo);
        tvBuyTypeName = (TextView) findViewById(R.id.tv_buy_type_name);
        ivBtnArrow1 = (ImageView) findViewById(R.id.iv_btn_arrow1);
        tvRedPackage = (TextView) findViewById(R.id.tv_red_package);
        ivBtnArrow2 = (ImageView) findViewById(R.id.iv_btn_arrow2);
        idCommonGuideButton = (TextView) findViewById(R.id.id_common_guide_button);
        tvToRechange = (TextView) findViewById(R.id.tv_to_rechange);
        layoutRedPkg = findViewById(R.id.layout_red_pkg);
        tvHint = (TextView)findViewById(R.id.tvHint);
    }

    private void initData() {
        prodInfo.setText(String.valueOf(mData.getSubject()));
        buyTypes.clear();
        typeMap.clear();
        giftList.clear();
        amountInfo.setText(String.valueOf(mData.getOrderAmount())+"元");
        PartnerPayResponse.PaymentTypes paymentTypes = mData.getPaymentTypes();
        if(paymentTypes == null){
            return;
        }
        List<PartnerPayResponse.PaymentType> types = paymentTypes.getPaymentTypes();
        if(types != null){
            int length = types.size();
            for(int i = 0;i<length;i++){
                PartnerPayResponse.PaymentType paymentType = types.get(i);
                String key = paymentType.getPaymentType();
                if(!TextUtils.isEmpty(key)){
                    typeMap.put(key,paymentType);
                }
            }
            if(typeMap.containsKey("W")){//钱包
                PartnerPayResponse.PaymentType t = typeMap.get("W");
                walletBalance = Double.valueOf(t.getWalletWithdrawBalance());
                PayType type = new PayType();
                type.PaymentType = "W";
                type.PaymentTypeFlag = t.getPaymentTypeFlag();
//                type.PaymentTypeName = t.getPaymentTypeName();
                type.PaymentTypeName = "零钱支付";
                buyTypes.add(type);

                initGift(t);
            }
            if(typeMap.containsKey("C")){//刷卡
                PartnerPayResponse.PaymentType t = typeMap.get("C");
                PayType type = new PayType();
                type.PaymentType = "C";
                type.PaymentTypeName = t.getPaymentTypeName();
                type.PaymentTypeFlag = t.getPaymentTypeFlag();
                buyTypes.add(type);
            }
            if(typeMap.containsKey("S")){//快捷支付
                PartnerPayResponse.PaymentType t = typeMap.get("S");
                JSONArray jsonArray = t.getList();
                if(jsonArray != null && jsonArray.length() != 0){
                    JSONObject obj = jsonArray.optJSONObject(0);
                    if(!obj.isNull("shortCards") && obj.optJSONArray("shortCards").length() != 0){
                        JSONArray array = obj.optJSONArray("shortCards");
                        for(int i = 0;i<array.length();i++){
                            PayType type = new PayType();
                            buyTypes.add(type);
                            type.PaymentTypeFlag = t.getPaymentTypeFlag();
                            type.PaymentType = "S";
                            type.PaymentTypeName = t.getPaymentTypeName();
                            JSONObject jobj = array.optJSONObject(i);
                            if(jobj == null){
                                continue;
                            }
                            type.shortCard = PayType.ShortCard.parseTreasure(jobj);
                        }
                    }
                }
            }
            PayType type = new PayType();
            buyTypes.add(type);
            type.PaymentTypeName = "使用新卡支付";
            type.PaymentTypeFlag = "-1";
            type.PaymentType = "";
        }
        selectTypePostion(typePosition);
        if(typePosition == 0){
            initGiftBtnStatus();
        }
    }

    private void initGift(PartnerPayResponse.PaymentType type) {
        JSONArray list = type.getList();
        int length = list.length();
        Map<String,JSONObject> map = new HashMap<>();
        for(int i = 0;i < length;i++){
            JSONObject obj = list.optJSONObject(i);
            if(obj != null && obj.has("paymentType")){
                String key = obj.optString("paymentType");
                map.put(key,obj);
            }
        }
        if(!map.containsKey("G")){
            return;
        }
        JSONArray giftArray = map.get("G").optJSONArray("giftList");
        if(giftArray == null || giftArray.length() == 0){
            return;
        }
        int i = 0;
        for(;i<giftArray.length();i++){
            JSONObject obj = giftArray.optJSONObject(i);
            if(obj == null){
                continue;
            }
            RedPackageInfo.GiftListEntity gift = RedPackageInfo.GiftListEntity.parseObject(obj);
            giftList.add(gift);
        }
        if(i != 0){
            giftList.add(null);
        }
    }

    private void initGiftBtnStatus() {
        if(giftList.size() == 0){
            tvRedPackage.setEnabled(false);
            tvRedPackage.setClickable(false);
            tvRedPackage.setText("");
            tvRedPackage.setHint("可用红包个数0");
            ivBtnArrow2.setVisibility(View.GONE);
        }else {
            tvRedPackage.setEnabled(true);
            tvRedPackage.setClickable(true);
            tvRedPackage.setText("请选择");
            ivBtnArrow2.setVisibility(View.VISIBLE);
        }
    }

    private void showBuyTypeDialog() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                selectTypePostion(which);
            }
        };
        DialogCreator.showPayChooseTypeDialog(context, listener, buyTypes);
    }

    private void selectTypePostion(int which) {
        PayType tmpType = buyTypes.get(which);
        String lastAppendStr = "";
        if(TextUtils.equals("W", tmpType.PaymentType)){//钱包
            lastAppendStr = new StringBuilder().append("  ")
                    .append(Util.formatTwo(walletBalance)).append("元")
                    .toString();
            layoutRedPkg.setVisibility(View.VISIBLE);
            tvToRechange.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.INVISIBLE);
            updateRechangeStatus();
        }else{
            needWalletRechange = false;
            tvHint.setVisibility(View.GONE);
            layoutRedPkg.setVisibility(View.GONE);
            tvToRechange.setVisibility(View.INVISIBLE);
        }
        if(TextUtils.isEmpty(tmpType.PaymentType)){
            //使用新卡支付
            Intent intent = new Intent(context, BankCardAdditionActivity.class);
            startActivity(intent);
            updateOnResume = true;
            return;
        }
        StringBuilder text = new StringBuilder();
        text.append(tmpType.getShowText());
        if(!TextUtils.isEmpty(lastAppendStr)){
            text.append(lastAppendStr);
        }
        tvBuyTypeName.setText(text.toString());
        selectedBuyType = tmpType;
        typePosition = which;
    }

    private void updateRechangeStatus() {
        PartnerPayResponse.PaymentType type = typeMap.get("W");
        //钱包余额
        Double userPay = Double.valueOf(type.getWalletWithdrawBalance());
        if (userPay < WALLET_NEED_PAY){
            //如果零钱余额小于1，提示零钱余额不足
            needWalletRechange = true;
            return;
        }
        if(selectedGift != null){
            //红包余额
            Double giftBalance = Double.valueOf(selectedGift.getGiftBalance());
            userPay+=giftBalance;
        }
        //零钱+红包的总额小于订单总额
        if(Double.valueOf(mData.getOrderAmount()) > userPay){
            needWalletRechange = true;
        }else{
            needWalletRechange = false;
        }
    }

    private void initListener(){
        View.OnClickListener buyTypeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuyTypeDialog();
            }
        };
        View.OnClickListener redListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {//红包
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        selectedGiftPosition(which);
                    }
                };
                DialogCreator.showRedPkgChooseDialog(context, listener, giftList);
            }
        };
        View.OnClickListener toRechangeListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOnResume = true;
                queryRechargeLimit();
            }
        };
        View.OnClickListener guideListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下一步
                if(TextUtils.equals("C",selectedBuyType.PaymentType)){
                    //刷卡交易
                    Intent intent = new Intent(context,TreasureSwipePaymentActivity.class);
                    intent.putExtra(ConstKey.TRANS_INFO,transInfo);
                    startActivity(intent);
                }else if(TextUtils.equals("2",mData.getPaymentTypes().getSafetyToolFlag())){//未设置支付密码

                    DialogCreator.createFullContentDialog(
                            context, "取消", "设置", "为了保证您的交易安全，需要先设置支付密码和密保问题",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(context, SetPaymentPasswordActivity.class);
                                    intent.putExtra("fromOneTreasure",true);
                                    startActivity(intent);
                                    updateOnResume = true;
                                }
                            }
                    ).show();
                }else{
                    if(needWalletRechange){
                        toast("零钱余额不足");
                        return;
                    }
                    Intent intent = new Intent(context,InputPayPwdDialogActivity.class);
                    startActivityForResult(intent,PAY_PWD_REQUEST);
                }
//                else if(TextUtils.equals("0",mData.getSafetyToolFlag())) {//不需要支付密码
//                    if (TextUtils.equals("S", selectedBuyType.PaymentType)) {
//                        //获取短信验证码
//                        TreasureSMSCheckActivity
//                                .open(context, selectedBuyType.shortCard, dataStr, null);
//                    } else {
//                        queryWallet(pwdTmp);
//                    }
//                }
            }
        };
        tvBuyTypeName.setOnClickListener(buyTypeListener);
        ivBtnArrow1.setOnClickListener(buyTypeListener);
        tvRedPackage.setOnClickListener(redListener);
        ivBtnArrow2.setOnClickListener(redListener);
        tvToRechange.setOnClickListener(toRechangeListener);
        idCommonGuideButton.setOnClickListener(guideListener);
    }

    private void selectedGiftPosition(int which) {
        if(which < 0){
            selectedGift = null;
            tvRedPackage.setText("请选择");
        }else{
            RedPackageInfo.GiftListEntity gift = giftList.get(which);
            selectedGift = gift;
            if(gift == null){
                tvRedPackage.setText("不使用红包");
            }else {
                tvRedPackage.setText(gift.getShowSpanned());
            }
        }
        updateRechangeStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PAY_PWD_REQUEST && resultCode == RESULT_OK){
            pwdTmp = data.getStringExtra(Constants.IntentKey.PASSWORD);
            if(TextUtils.equals("W",selectedBuyType.PaymentType)){
                queryWallet(pwdTmp);
            }else if(TextUtils.equals("S",selectedBuyType.PaymentType)){
                //获取短信验证码
                TreasureSMSCheckActivity.open(context,selectedBuyType.shortCard,dataStr,pwdTmp);
            }
        }
    }

    private void queryWallet(final String pwd){
        showProgressWithNoMsg();
        BusinessRequest businessRequest= RequestFactory.getRequest(context, RequestFactory.Type.COMPREHENSIVE_INFORMATION);
        GetComPrensiveInfoRequest params = new GetComPrensiveInfoRequest(context);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String walletNo = jsonObject.optString("walletNo");
                        giftNo = jsonObject.optString("giftNo");
                        walletTrade(walletNo, pwd);
                    } catch (Exception e) {
                        hideProgressDialog();
                        LogUtil.print("wallet json解析异常");
                        e.printStackTrace();
                    }

                } else {
                    hideProgressDialog();
                    toast(resultServices.retMsg);
                    transInfo.setMsg(resultServices.retMsg);
                    transInfo.setTransResult(TransResult.FAILED);
                    tradeOver2Result();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                transInfo.setTransResult(TransResult.TIMEOUT);
                tradeOver2Result();
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }

    private void tradeOver2Result(){
        Intent intent = new Intent(context, ConfirmResultActivity.class);
        intent.putExtra(ConstKey.TRANS_INFO,transInfo);
        startActivity(intent);
    }

    private void walletTrade(String walletNo, String pwd){
        trade("1CN",walletNo,pwd,selectedGift);
    }
    private void trade(String busid, String pan, String pwd, RedPackageInfo.GiftListEntity gift){
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.WALLET_TRADE);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if("55".equals(resultServices.retCode)){//密码错误，当前页面直接显示
                    toast(resultServices.retMsg);
                    return;
                }
                try {
                    hideProgressDialog();
                    JSONObject jsonObject = new JSONObject(resultServices.retData);
                    if (resultServices.isRetCodeSuccess()) {
                        transInfo.setTransResult(TransResult.SUCCESS);
                    } else {
//                        toast(resultServices.retMsg);
                        transInfo.setMsg(resultServices.retMsg);
                        transInfo.setTransResult(TransResult.FAILED);
                    }
                    String url = jsonObject.optString("callBackURL", "");
                    String params = jsonObject.optString("orderid","");
                    String jnlId = jsonObject.optString("jnlId","");
                    transInfo.setJnlId(jnlId);
                    transInfo.setCallbackUrl(url, params);
                    tradeOver2Result();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
                transInfo.setTransResult(TransResult.TIMEOUT);
                tradeOver2Result();
            }
        });

        List<WalletPaymentTypes> types = new ArrayList<>();
        WalletPaymentTypes type = new WalletPaymentTypes();
        type.setPaymentType(selectedBuyType.PaymentType);
        type.setRandom(Mac.getRnd());
        Double orderAmount = Double.valueOf(mData.getOrderAmount());
        double walletPayAmount = orderAmount;
        JSONObject jsonObject = null;
        if(gift != null){
            jsonObject = new JSONObject();
            try {
                jsonObject.put("paymentType","G");
                jsonObject.put("giftIds",gift.getJsonObject());
                Double gBalance = Double.valueOf(gift.getGiftBalance());//红包余额
                if (orderAmount - gBalance <=WALLET_NEED_PAY){//不管红包多少，零钱支付最少WALLET_NEED_PAY元
                    walletPayAmount = WALLET_NEED_PAY;
                    gBalance = orderAmount - walletPayAmount;
                }else {
                    walletPayAmount = orderAmount - gBalance;
                }
                if (gBalance != 0){
                    jsonObject.put("paymentAmount",gBalance);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        WalletTradeRequest params = new WalletTradeRequest(context);

        if(walletPayAmount != 0){//如果零钱支付金额不为0
            params.setLastPaymentType(selectedBuyType.PaymentType);
            type.setPaymentAmount(walletPayAmount);
            types.add(type);
            params.setPan(pan);
        }else {//零钱支付金额为0时，设置支付方式为红包支付(G)
            params.setLastPaymentType("G");
            params.setPan(giftNo);
        }

        JSONArray typesArray = WalletPaymentTypes.getJsonArray(types);
        if(jsonObject != null && jsonObject.has("paymentAmount")){
            typesArray.put(jsonObject);
        }
        params.setBusid(busid);//刷卡 钱包 走1CN;;快捷走的19U
        params.setBillId(mData.getBillId());
        params.setAmount(mData.getOrderAmount());
        params.setPaymentTypes(typesArray.toString());
        params.setTerminalId(ApplicationEx.getInstance().getUser().getTerminalId());
        params.setOrderToPayFlag("1");
        params.setLakalaOrderId(mData.getLakalaOrderId());
        params.setParams(mData.getParams());
        params.setNotifyURL(mData.getNotifyURL());
        if(!TextUtils.isEmpty(pwd)){
            params.setTrsPassword(pwd);
        }
        params.setFee(Utils.yuan2Fen("0"));
        WalletServiceManager.getInstance().start(params, request);
    }

    private void queryPayTypes(){
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.EWALLET_PAYTYPE);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        mData.updatePayType(jsonObject);
                        initData();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });
        PayTypeParams params = new PayTypeParams(context);
        params.setBillId(mData.getBillId());
        showProgressWithNoMsg();
        WalletServiceManager.getInstance().start(params, request);
    }
    /**
     * 查询充值限额
     */
    private void queryRechargeLimit(){
        showProgressWithNoMsg();
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_RECHARGE_LIMIT_QRY);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jobj = new JSONObject(resultServices.retData);
                        Intent intent = new Intent(context, WalletRechargeActivity.class);
                        intent.putExtra(Constants.IntentKey.WALLET_RECHARGE_LIMIT, jobj.optString("appMsg"));
                        intent.putExtra("balance", walletBalance);
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
//                                intent.putExtra("balance",walletBalance);
////                                intent.putExtra(Constants.IntentKey.Wallet_Recharge,"walletRecharge");
//                                startActivity(intent);
//                            }
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
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

    @Override
    public void onBackPressed() {
        WebMallContainerActivity.onResumeUrl = transInfo.getCallbackUrl();
        super.onBackPressed();
    }
}
