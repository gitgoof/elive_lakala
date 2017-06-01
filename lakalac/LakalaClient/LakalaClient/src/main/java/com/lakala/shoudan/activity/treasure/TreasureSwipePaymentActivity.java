package com.lakala.shoudan.activity.treasure;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TcRequest;
import com.lakala.shoudan.activity.wallet.bean.WalletPaymentTypes;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.activity.wallet.request.WalletTradeRequest;
import com.lakala.shoudan.activity.wallet.tc.WalletTcManage;
import com.lakala.shoudan.activity.wallet.request.WalletTcRequest;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LMQ on 2015/12/28.
 */
public class TreasureSwipePaymentActivity extends NewCommandProtocolPaymentActivity {
    private TreasureSwipePaymentActivity context;
    private TreasureTransInfo transInfo = null;
    private TcRequest tcRequest;
    private WalletTcManage walletTcManage = new WalletTcManage(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        transInfo = (TreasureTransInfo)getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
    }

    @Override
    protected void startPayment(final SwiperInfo swiperInfo) {
        tcRequest = new TcRequest();
        BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.WALLET_TRADE);
        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                try {
                    JSONObject jsonObject = new JSONObject(resultServices.retData);
                    if (resultServices.isRetCodeSuccess()) {
                        transInfo.setTransResult(TransResult.SUCCESS);
                    } else {
                        transInfo.setTransResult(TransResult.FAILED);
                        transInfo.setMsg(resultServices.retMsg);
                    }
                    String srcSid = jsonObject.optString("sid");
                    tcRequest.setSrcSid(srcSid);
                    String icc55 = jsonObject.optString("icc55");
                    transInfo.setIcc55(icc55);
                    String url = jsonObject.optString("callBackURL", "");
                    String params = jsonObject.optString("orderid");
                    String jnlId = jsonObject.optString("jnlId","");
                    transInfo.setJnlId(jnlId);
                    transInfo.setCallbackUrl(url, params);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //IC卡不管成功或者失败都要异步上送TC
                if (swiperInfo.getCardType() == SwiperInfo.CardType.ICCard && !TextUtils.isEmpty
                        (tcRequest.getSrcSid())) {
                    boolean b = transInfo.getTransResult() == TransResult.SUCCESS ? true : false;
                    doSecIss(b, transInfo.getIcc55(), "");   //回调requestUploadTc
                }else {
                    toResult();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                transInfo.setTransResult(TransResult.TIMEOUT);
                transInfo.setMsg(getString(R.string.socket_error));
                toResult();
            }
        });

        final SwiperInfo.CardType cardType = swiperInfo.getCardType();

        WalletPaymentTypes types = new WalletPaymentTypes();
        types.setCardsn(swiperInfo.getCardsn());
        types.setCardtype(swiperInfo.getCardType() == SwiperInfo.CardType.ICCard ? "2" : "1");
        types.setIcc55(swiperInfo.getIcc55());
        types.setPan(swiperInfo.getTransTypePan());
        types.setPayerAcNo(swiperInfo.getTransTypePan());
        types.setPaymentAmount(Double.valueOf(transInfo.getAmount()));
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

        WalletTradeRequest params = new WalletTradeRequest(context){
            @Override
            protected String getMacRnd() {
                return swiperInfo.getRandomNumber();
            }
            @Override
            protected String getChntype() {
                return "02101";
            }
        };
        params.setLastPaymentType("C");
        params.setBusid("1CN");//刷卡 钱包 走1CN;;快捷走的19U
        params.setBillId(transInfo.getBillId());
        if (cardType != SwiperInfo.CardType.MSC){
            params.setPan(swiperInfo.getMaskedPan());
        }
        params.setPaymentTypes(types.getPaymentTypes().toString());
        params.setTerminalId(ApplicationEx.getInstance().getSession().getCurrentKSN());
        params.setTrsPassword(swiperInfo.getPin());
        params.setTelecode(ApplicationEx.getInstance().getSession().getCurrentLineNo());
        params.setFee(Utils.yuan2Fen("0"));
        params.setOrderToPayFlag("1");
        params.setLakalaOrderId(transInfo.getLakalaOrderId());
        params.setParams(transInfo.getParams());
        params.setNotifyURL(transInfo.getNotifyURL());
        params.setAmount(transInfo.getAmount());
        WalletServiceManager.getInstance().start(params, request);
    }

    @Override
    protected void addTransParams(RequestParams requestParams) {
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

                BusinessRequest request = RequestFactory.getRequest(context, RequestFactory.Type
                        .UPLOAD_TC);
                WalletTcRequest params = new WalletTcRequest(context);

                params.setTcicc55(tcRequest.getTcicc55());
                params.setScpicc55(tcRequest.getScpicc55());
                params.setSrcSid(tcRequest.getSrcSid());
                params.setTcvalue(tcRequest.getTcvalue());

                request.setResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {
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

    private void toResult(){
        hideProgress();
        Intent intent = new Intent(context, ConfirmResultActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, transInfo);
        startActivity(intent);
    }

}
