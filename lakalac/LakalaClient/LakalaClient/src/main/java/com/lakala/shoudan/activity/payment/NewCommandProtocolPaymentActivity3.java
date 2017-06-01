package com.lakala.shoudan.activity.payment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LKlPreferencesKey;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.platform.swiper.devicemanager.controller.TransFactor;
import com.lakala.shoudan.BuildConfig;
import com.lakala.shoudan.R;
import com.lakala.shoudan.component.DialogCreator;
import com.loopj.lakala.http.RequestParams;
import com.newland.mtype.module.common.security.GetDeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by More on 15/12/21.
 */
public abstract class NewCommandProtocolPaymentActivity3 extends CommonPaymentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swiperManagerHandler.setInputPinNeeded(!BuildConfig.FLAVOR.contains("无密"));
        swiperManagerHandler.setForceQpboc(LklPreferences.getInstance().getBoolean(LKlPreferencesKey.FORCE_QPBOC, false));
    }

    @Override
    protected void startSwipe() {

        //发起刷卡指令前需要获取服务端随机数
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                requestForSRandom();
            }
        });


    }

    @Override
    public void onProcessEvent(SwiperProcessState swiperProcessState, SwiperInfo swiperInfo) {
        if(swiperProcessState == SwiperProcessState.WAITING_FOR_CARD_SWIPE){
            showSwipeView();
            setProgressVisibility(false);
            prompt.setTextColor(getResources().getColor(R.color.gray_666666));
            String prompt;
            int tipPic;
            if(swiperManagerHandler.isPbocSupported()){
                if(swiperManagerHandler.isQPbocSupported() && baseTransInfo.getType().isSupportQPBOC()){

                    if(swiperManagerHandler.isForceQpboc()){


                        prompt = (getString(R.string.use_card_reader_pass));
                        tipPic = R.drawable.lakala_swipe_force_qpboc;

                    }else{

                        prompt = (getString(R.string.use_card_reader_swip_or_insert_or_pass));
                        tipPic = R.drawable.lakala_swipe_triple;
                    }

                }else{
                    prompt = (getString(R.string.use_card_reader_swip_or_insert));
                    tipPic  = (R.drawable.lakala_swipe_both);
                }

            }else{
                prompt = (getString(R.string.use_card_reader_swipe));
                tipPic  = (R.drawable.lakala_swipe_only);
            }
            updatePrompt(prompt);
            updateTipPic(tipPic);
        }else{
            super.onProcessEvent(swiperProcessState, swiperInfo);
        }

    }

    private TransFactor newCommandPro;

    protected void sendSwipeCommand(String srnd){

        String amount = baseTransInfo.getSwipeAmount();

        String additionalMsg = baseTransInfo.getAdditionalMsg();

        newCommandPro = new TransFactor(baseTransInfo.getType());
        newCommandPro.setAdditionalMsg(additionalMsg);
        newCommandPro.setAmount(amount);
        newCommandPro.setServiceCode(srnd);
        swiperManagerHandler.startCommonTrans(newCommandPro);

    }

    protected String btoken;

    private ServiceResultCallback serviceResultCallback = new ServiceResultCallback() {
        @Override
        public void onSuccess(ResultServices resultServices) {

            if(resultServices.isRetCodeSuccess()){

                JSONObject responseData = null;
                try {
                    responseData = new JSONObject(resultServices.retData);

                    btoken = responseData.optString("btoken");

                    String srnd = responseData.optString("srnd", "rand");

                    sendSwipeCommand(srnd);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else{
                if(TextUtils.isEmpty(btoken)){
                    showBtokenGetFailedDialog();
                }else{
                    toast(resultServices.retMsg);
                }
            }

        }

        @Override
        public void onEvent(HttpConnectEvent connectEvent) {


            if(TextUtils.isEmpty(btoken)){
                showBtokenGetFailedDialog();
            }else{
                toastInternetError();
            }


        }
    };

    private void showBtokenGetFailedDialog(){

        DialogCreator.createFullContentDialog(this, "确定", "取消", "您的手机收款宝验证未成功，请重新验证。", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestForSRandom();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();

            }
        }).show(getSupportFragmentManager());

    }

    private void requestForSRandom(){


        BusinessRequest sRndRequest = BusinessRequest.obtainRequest(NewCommandProtocolPaymentActivity3.this, "v1.0/token/btoken", HttpRequest.RequestMethod.POST, true);
        sRndRequest.removeHeader(BusinessRequest.X_B_TOKEN);
        RequestParams requestParams = sRndRequest.getRequestParams();
        requestParams.put("bType", "TRADE");

        JSONObject extInfo = new JSONObject();
//        属性	说明	必选	类型	参数位置	备注
//        busid	业务代码	M	an..100	B
//        termid	终端号，PSAM	M	o	B
//        amount	交易金额	M	n12	B	单位分
//        fee	手续费	M	n12	B	单位分
//        inAccount	转入账号/手机号/商户号

        try {

            extInfo.put("busid", baseTransInfo.getType().getBusId());
            extInfo.put("termid", ApplicationEx.getInstance().getSession().getCurrentKSN());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestParams.put("extInfo", extInfo);

        sRndRequest.setResponseHandler(serviceResultCallback);
        sRndRequest.execute();

    }





    //Main Trans logic
    protected BusinessRequest createDefaultTradeRequest(){
        BusinessRequest businessRequest = BusinessRequest.obtainRequest(NewCommandProtocolPaymentActivity3.this, "v1.0/business/marketzone/trade", HttpRequest.RequestMethod.POST, true);

        return businessRequest;
    }

    protected abstract void addTransParams(RequestParams requestParams);

    protected void startPayment(SwiperInfo swiperInfo){
        //默认trade接口
        BusinessRequest businessRequest = createDefaultTradeRequest();
        //添加刷卡信息相关请求参数
        addDefaultRequestParams(swiperInfo, businessRequest.getRequestParams());
        //添加自定义交易请求参数
        addTransParams(businessRequest.getRequestParams());
        //添加默认请求参数
        addHeader(businessRequest);
        //主交易监听默认走this, 封装集成了tc上送的逻辑
        businessRequest.setResponseHandler(this);
        //发起交易
        businessRequest.execute();
    }


    private void addHeader(BusinessRequest businessRequest){

        businessRequest.setHeader(BusinessRequest.X_B_TOKEN, btoken);

    }



    /**
     * 生成交易需要的RequestParams
     * @param swiperInfo
     * @return
     */
    private void addDefaultRequestParams(SwiperInfo swiperInfo, RequestParams requestParams){

        requestParams.put("otrack", swiperInfo.getEncTracks());
        requestParams.put("rnd", swiperInfo.getRandomNumber());
        requestParams.put("pinkey", swiperInfo.getPin());
        requestParams.put("posemc", swiperInfo.getPosemc());
        requestParams.put("icc55", swiperInfo.getIcc55());
        requestParams.put("cardsn", swiperInfo.getCardsn());
        requestParams.put("track2", swiperInfo.getTrack2());
        if(swiperInfo.getCardType() != SwiperInfo.CardType.MSC)
            requestParams.put("pan", swiperInfo.getMaskedPan());
        requestParams.put("series", Utils.createSeries());
        requestParams.put("track1", swiperInfo.getTrack1());
        requestParams.put("termid", ApplicationEx.getInstance().getSession().getCurrentKSN());
        requestParams.put("chntype", BusinessRequest.CHN_TYPE);
        requestParams.put("chncode", BusinessRequest.CHN_CODE);

        if(!TextUtils.isEmpty(swiperInfo.getMac())){

            requestParams.put("tmac", swiperInfo.getMac());
            if(swiperInfo.getCardType() != SwiperInfo.CardType.MSC)
                requestParams.put("icinfo", swiperInfo.getIcinfo());

            if(swiperInfo.getCardType() == SwiperInfo.CardType.QPBOC){
                requestParams.put("freePWD", swiperManagerHandler.isInputPinNeeded());
                requestParams.put("unContact", "true");
            }else{
                requestParams.put("unContact", "false");
                requestParams.put("freePWD", "false");
            }
        }

        if(swiperInfo.getDeviceInfo() != null){

            GetDeviceInfo temp = swiperInfo.getDeviceInfo();
            requestParams.put("mver", temp.getFirmwareVersion());
            requestParams.put("aver", temp.getAppVersion());
            requestParams.put("pver", temp.getCommandVersion());

        }
    }

    @Override
    protected void downGradeSwipe() {
        newCommandPro.setDownGrade(true);
        swiperManagerHandler.startCommonTrans(newCommandPro);
    }
}
