package com.lakala.shoudan.activity.shoudan.finance.trade;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.CommonPaymentActivity;
import com.lakala.shoudan.activity.payment.ConfirmResultActivity;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.bean.ApplyInfo;
import com.lakala.shoudan.activity.shoudan.finance.bean.SwipePaymentType;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.DoPaySwipeRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.TcRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.activity.shoudan.finance.manager.TcSyncManager;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.DateUtil;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.dialog.ProgressDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/10/23.
 */
public class FinanceSwipeBuyActivity extends CommonPaymentActivity {

    private ApplyInfo applyInfo;
    private ProgressDialog progressDialog;
    private TcRequest tcRequest;
    private FinanceSwipeBuyActivity context;
    private HttpResponseListener tcListener = new HttpResponseListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
            dismissProgress();
            if(tcRequest != null && "0".equals(tcRequest.getTcAsyFlag())){//异步TC
                if(!returnHeader.isSuccess()){
                    tcManager.saveTcInfo(tcRequest,tcRequest.getTelecode());
                }
            }else if(tcRequest != null){//同步TC
                if(returnHeader.isSuccess()){
                    startConfirmResult(applyInfo);
                }else{
                    applyInfo.setTransResult(TransResult.FAILED);
                    applyInfo.setMsg(returnHeader.getErrMsg());
                    startConfirmResult(applyInfo);
                }
            }
        }

        @Override
        public void onErrorResponse() {
            dismissProgress();

            if("0".equals(tcRequest.getTcAsyFlag())){
                tcManager.saveTcInfo(tcRequest,tcRequest.getTelecode());
                return;
            }
            startTranTimeout(getString(R.string.socket_error), applyInfo);
        }
    };
    private TcSyncManager tcManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        tcManager = new TcSyncManager();
        applyInfo = (ApplyInfo)getIntent().getSerializableExtra(Constants.IntentKey.TRANS_INFO);
    }

    @Override
    protected void startPayment(final SwiperInfo swiperInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doPay(swiperInfo);
            }
        });
    }
    protected void showProgress(){
        progressDialog = DialogCreator.createProgressDialog(FinanceSwipeBuyActivity.this,
                                                            "");
        progressDialog.show();
    }
    private void dismissProgress(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
    private void doPay(final SwiperInfo swiperInfo){

        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgress();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                if(returnHeader.isSuccess()){
                    String lineNo = responseData.optString("LineNo", "");
                    doPay(swiperInfo,lineNo);
                }else{
                    dismissProgress();
                    toast(returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                dismissProgress();

                toastInternetError();
            }
        };
        FinanceRequestManager.getInstance().queryLineNo(ApplicationEx.getInstance().getSession()
                                                                .getCurrentKSN(),
                                                        listener);
    }

    private void doPay(final SwiperInfo swiperInfo, final String telecode){
        DoPaySwipeRequest request = new DoPaySwipeRequest(){
            @Override
            protected String getMacRnd() {
                return swiperInfo.getRandomNumber();
            }
        };
        request.setOutMode(applyInfo.getOutMode());
        request.setAmount(applyInfo.getAmount());
        request.setBillId(applyInfo.getBillid());
        request.setLastPaymentType(applyInfo.getLastPaymentType());
        request.set_TerminalId(ApplicationEx.getInstance().getSession().getCurrentKSN());
        request.setProductId(applyInfo.getProductId());
        request.setPeriod(applyInfo.getPeriod());
        request.setTelecode(telecode);

        List<SwipePaymentType> paymentTypes = new ArrayList<SwipePaymentType>();
        SwipePaymentType type = new SwipePaymentType();
        paymentTypes.add(type);
        type.setPaymentAmount(applyInfo.getAmount());
        type.setPaymentType(applyInfo.getLastPaymentType());
        type.setOtrack(swiperInfo.getEncTracks());
        type.setPinkey(swiperInfo.getPin());
        type.setRandom(swiperInfo.getRandomNumber());
        type.setProductId(applyInfo.getProductId());
        String pan = swiperInfo.getMaskedPan();
        switch(swiperInfo.getCardType()){
            case ICCard:
                applyInfo.setCardType(SwiperInfo.CardType.ICCard);
                type.setCardType("2");
                type.setPosemc("1");
                type.setTrack2(swiperInfo.getTrack2());
                type.setIcc55(swiperInfo.getIcc55());
                type.setCardsn(swiperInfo.getCardsn());
                request.setPan(pan);
                type.setPayerAcNo(pan);
                type.setPan(pan);
                break;
            case MSC:
                applyInfo.setCardType(SwiperInfo.CardType.MSC);
                type.setCardType("1");
                type.setPosemc("0");
                type.setPayerAcNo(swiperInfo.getTransTypePan());
                break;
        }
        request.setPaymentTypes(paymentTypes);

        tcRequest = new TcRequest(){
            @Override
            public String getTelecode() {
                return telecode;
            }

            @Override
            protected String getTermId() {
                return ApplicationEx.getInstance().getSession().getCurrentKSN();
            }
        };
        tcRequest.setBillId(applyInfo.getBillid());
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinished(ReturnHeader returnHeader, JSONObject responseData) {
                applyInfo.setTime(DateUtil.formatSystemDate("yyyy/MM/dd HH:mm:ss"));
                if(returnHeader.isSuccess()){
                    applyInfo.setAmount(responseData.optString("Amount", "0"));
                    applyInfo.setJnlId(responseData.optString("JnlId"));
                    String date = responseData.optString("JnlDate");
                    applyInfo.setTime(
                            DateUtil.formatDateStrToPattern(
                                    date, "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss"
                            )
                    );
                    tcRequest.setBillId(applyInfo.getBillid());
                    tcRequest.setTelecode(telecode);
                    applyInfo.setTransResult(TransResult.SUCCESS);
                }else{
                    applyInfo.setTransResult(TransResult.FAILED);
                    applyInfo.setMsg(returnHeader.getErrMsg());
                }
                if(responseData != null){
                    String sid = responseData.optString("sid", "");
                    String tcAsyFlag = responseData.optString("TcAsyFlag", "");//0 为异步TC上送
                    tcRequest.setSrcSid(sid);
                    tcRequest.setTcAsyFlag(tcAsyFlag);
                    String icc55 = responseData.optString("icc55", "");
                    applyInfo.setIcc55(icc55);
                }
                if(applyInfo.getCardType() == SwiperInfo.CardType.ICCard && !TextUtils.isEmpty(tcRequest.getSrcSid())){
                    doSecIss(applyInfo.getTransResult() == TransResult.SUCCESS ? true :
                                     false, applyInfo.getIcc55(), "");
                }else{
                    dismissProgress();
                    startConfirmResult(applyInfo);
                }
            }

            @Override
            public void onErrorResponse() {
                dismissProgress();
                startTranTimeout(getString(R.string.socket_error), applyInfo);
            }
        };
        FinanceRequestManager.getInstance().doPaySwipe(request, listener);
    }


    private void startConfirmResult(ApplyInfo applyInfo){
        Intent intent = new Intent(context,ConfirmResultActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO,applyInfo);
        startActivity(intent);
    }


    @Override
    public void requestUploadTc(final SwiperInfo swiperInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if(applyInfo.getTransResult() == TransResult.SUCCESS && applyInfo.getCardType()
//                        == SwiperInfo.CardType.MSC){
//                    dismissProgress();
//                    startConfirmResult(applyInfo);
//                    return;
//                }
                tcRequest.setTcicc55(swiperInfo.getIcc55());
                tcRequest.setScpicc55(swiperInfo.getScpicc55());
                tcRequest.setTcvalue(swiperInfo.getTCValue());
                tcRequest.setMacRnd(swiperInfo.getRandomNumber());
                if("0".equals(tcRequest.getTcAsyFlag())){//TC异步上送
                    dismissProgress();
                    startConfirmResult(applyInfo);//不等待TC结果，直接跳转到结果页
                    FinanceRequestManager.getInstance().asyTransTc(tcRequest,tcListener);
                }else{
                    FinanceRequestManager.getInstance().sncTransTc(tcRequest,tcListener);
                }
                tcManager.tcSyncService();
            }
        });
    }

    protected void startTranTimeout(String errMsg, ApplyInfo applyInfo) {
        applyInfo.setMsg(errMsg);
        applyInfo.setTransResult(TransResult.TIMEOUT);
        applyInfo.setTime(DateUtil.formatSystemDate("yyyy/MM/dd HH:mm:ss"));
        Intent intent = new Intent(context, ConfirmResultActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, applyInfo);
        startActivity(intent);
    }
}
