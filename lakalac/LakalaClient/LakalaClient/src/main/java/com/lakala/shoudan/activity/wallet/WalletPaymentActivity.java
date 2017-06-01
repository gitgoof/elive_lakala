package com.lakala.shoudan.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TcRequest;
import com.lakala.shoudan.activity.wallet.bean.WalletPaymentTypes;
import com.lakala.shoudan.activity.wallet.bean.RechargeBill;
import com.lakala.shoudan.activity.wallet.bean.RechargeTransInfo;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTradeRequest;
import com.lakala.shoudan.activity.wallet.tc.WalletTcManage;
import com.lakala.shoudan.activity.wallet.request.WalletTcRequest;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fengx on 2015/11/30.
 */
public class WalletPaymentActivity extends NewCommandProtocolPaymentActivity{

    private RechargeTransInfo transInfo;
    private RechargeBill billInfo;
    private String amount;
//    private String walletRecharge;
    private TcRequest tcRequest;
    private WalletTcManage walletTcManage = new WalletTcManage(this);
    private String balance;
    private String icc55 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    private void init() {
        transInfo = (RechargeTransInfo) getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
        amount  = transInfo.getAmount();
//        walletRecharge=getIntent().getStringExtra(Constants.IntentKey.Wallet_Recharge);
        billInfo = (RechargeBill) getIntent().getSerializableExtra("billinfo");
        balance = getIntent().getStringExtra("balance");
        navigationBar.setTitle(transInfo.getTransTitle());
    }

    @Override
    protected void addTransParams(RequestParams requestParams) {

    }

    @Override
    protected void startPayment(SwiperInfo swiperInfo) {
        toRecharge(swiperInfo);
    }



    private void toResult(){
        hideProgress();
        Intent intent = new Intent(WalletPaymentActivity.this,ConfirmResultActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO,transInfo);
        startActivity(intent);
    }


    /**
     * 2、钱包交易
     */
    private void toRecharge(final SwiperInfo swiperInfo) {

        tcRequest = new TcRequest();

        final SwiperInfo.CardType cardType = swiperInfo.getCardType();

        WalletPaymentTypes types = new WalletPaymentTypes();
        types.setCardsn(swiperInfo.getCardsn());
        types.setCardtype(swiperInfo.getCardType() == SwiperInfo.CardType.ICCard ? "2" : "1");
        types.setIcc55(swiperInfo.getIcc55());
        types.setPan(swiperInfo.getTransTypePan());
        types.setPayerAcNo(swiperInfo.getTransTypePan());
        types.setPaymentAmount(Double.valueOf(amount));
        types.setPinkey(swiperInfo.getPin());
        if (cardType != SwiperInfo.CardType.MSC){
            types.setTrack2(swiperInfo.getTrack2());
        }
        types.setRandom(swiperInfo.getRandomNumber());
        types.setPosemc(swiperInfo.getPosemc());
        types.setPaymentType("C");
        if (cardType == SwiperInfo.CardType.MSC){
            types.setOtrack(swiperInfo.getEncTracks());
        }

        types.setPaymentTypes(types.getJsonArray());
        BusinessRequest request = RequestFactory.getRequest(this, RequestFactory.Type.WALLET_TRADE);
        WalletTradeRequest params = new WalletTradeRequest(this){
            @Override
            protected String getMacRnd() {
                return swiperInfo.getRandomNumber();
            }

            @Override
            protected String getChntype() {
                return "02101";
            }
        };
        params.setBusid(WalletHomeActivity.WALLET_RECHARGE);
        params.setBillId(billInfo.getBillid());
        params.setLastPaymentType("C");
        params.setAmount(amount);
        if (cardType != SwiperInfo.CardType.MSC){
            params.setPan(swiperInfo.getMaskedPan());
        }
        params.setPaymentTypes(types.getPaymentTypes().toString());
        params.setTerminalId(ApplicationEx.getInstance().getSession().getCurrentKSN());
        params.setTelecode(ApplicationEx.getInstance().getSession().getCurrentLineNo());
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                try {
                    JSONObject jsonObject = new JSONObject(resultServices.retData);
                    if (resultServices.isRetCodeSuccess()) {
                        String amount = jsonObject.optString("billAmount");
                        String walletBalance = jsonObject.optString("walletBalance");
                        transInfo.setAmount(amount + "元");
                        transInfo.setWalletBalance(walletBalance + "元");
                        transInfo.setTransResult(TransResult.SUCCESS);
                        icc55 = jsonObject.optString("icc55");
                        transInfo.setIcc55(icc55);
                    } else {
                        transInfo.setAmount(amount + "元");
                        transInfo.setWalletBalance(balance + "元");
                        transInfo.setTransResult(TransResult.FAILED);
                        transInfo.setMsg(resultServices.retMsg);
                    }

                    String srcSid = jsonObject.optString("sid");
                    tcRequest.setSrcSid(srcSid);
                } catch (JSONException e) {
                    e.printStackTrace();
                    LogUtil.print(e);
                }

//                IC卡不管成功或者失败都要异步上送TC
                if (swiperInfo.getCardType() == SwiperInfo.CardType.ICCard
                        && !TextUtils.isEmpty(tcRequest.getSrcSid())) {
                    boolean b = transInfo.getTransResult() == TransResult.SUCCESS ? true : false;
                    doSecIss(b, transInfo.getIcc55(), "");   //回调requestUploadTc
                }else {
                    toResult();
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                transInfo.setTransResult(TransResult.TIMEOUT);
                transInfo.setAmount(amount + "元");
                transInfo.setWalletBalance(balance + "元");
                toResult();
            }
        });
        showProgress();
        WalletServiceManager.getInstance().start(params,request);
    }

    @Override
    public void requestUploadTc(final SwiperInfo swiperInfo) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toResult();
                tcRequest.setTcicc55(swiperInfo.getIcc55());
                tcRequest.setScpicc55(swiperInfo.getScpicc55());
                tcRequest.setTcvalue(swiperInfo.getTcValue());

                BusinessRequest request = RequestFactory.getRequest(WalletPaymentActivity.this, RequestFactory.Type.UPLOAD_TC);
                WalletTcRequest params = new WalletTcRequest(WalletPaymentActivity.this);

                params.setTcicc55(tcRequest.getTcicc55());
                params.setScpicc55(tcRequest.getScpicc55());
                params.setSrcSid(tcRequest.getSrcSid());
                params.setTcvalue(tcRequest.getTcvalue());

                request.setResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
//                        if (resultServices.isRetCodeSuccess()) {
//
//                        } else {
//                            //失败保存tc信息下次发送
//                            walletTcManage.saveTcInfo(tcRequest, TerminalKey.getTelecode());
//                        }
                        //检查上次TC是否失败，失败的重新发送
                        walletTcManage.tcSyncService();

                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        //失败保存tc信息下次发送
                        walletTcManage.saveTcInfo(tcRequest, TerminalKey.getTelecode());
                    }
                });
                WalletServiceManager.getInstance().start(params, request);
            }
        });
    }
}
