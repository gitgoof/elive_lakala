package com.lakala.shoudan.activity.communityservice.balanceinquery;

import android.os.Bundle;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.CardUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.MutexThreadManager;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.CommonPaymentActivity;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HUASHO on 2015/1/16.
 * 余额查询
 */
public class BalanceInqueryActivity extends NewCommandProtocolPaymentActivity {

    private BalanceTransInfo balanceTransInfo;

    @Override
    protected void addTransParams(RequestParams requestParams) {
        requestParams.put("mobile", ApplicationEx.getInstance().getUser().getLoginName());
        requestParams.put("busid", "M30000");
        requestParams.put("tdtm", Utils.createTdtm());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPoint();
    }



    protected void initUI(){
        super.initUI();
        navigationBar.setTitle(R.string.balance_inquery);
        balanceTransInfo = (BalanceTransInfo)getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
    }

    @Override
    public void onProcessEvent(SwiperProcessState swiperProcessState, SwiperInfo swiperInfo) {
        super.onProcessEvent(swiperProcessState, swiperInfo);

        if(swiperProcessState == SwiperProcessState.WAITING_FOR_CARD_SWIPE){
            balanceTransInfo.setBalance("");
            balanceTransInfo.setMsg("");
            balanceTransInfo.setPayCardNo("");
            List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
            transferInfoEntities.add(new TransferInfoEntity("银行卡卡号", "","刷卡或插卡后自动显示卡号"));
            transferInfoEntities.add(new TransferInfoEntity("银行卡余额", "","等待查询"));
            confirmInfo.removeAllViews();
            confirmInfo.addList(this, transferInfoEntities,true,getResources().getColor(R.color.white));

        }

        if(swiperProcessState == SwiperProcessState.SWIPE_END){
            List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
            transferInfoEntities.add(new TransferInfoEntity("银行卡卡号",  CardUtil.formatCardNumberWithStar(balanceTransInfo.getPayCardNo())));
            transferInfoEntities.add(new TransferInfoEntity("银行卡余额", "", "等待查询"));
            confirmInfo.removeAllViews();
            confirmInfo.addList(this, transferInfoEntities, true, getResources().getColor(R.color.white));
        }
    }

    private SwiperInfo swiperInfo;

    @Override
    protected void startPayment(SwiperInfo swiperInfo) {
        super.startPayment(swiperInfo);
        this.swiperInfo = swiperInfo;
    }

    @Override
    public void onSuccess(ResultServices resultServices) {
        if(BusinessRequest.SUCCESS_CODE.equals(resultServices.retCode)){
            try{

                JSONObject jsonObject = new JSONObject(resultServices.retData);

                balanceTransInfo.setBalance(StringUtil.formatDisplayAmount(Utils.fen2Yuan(jsonObject.optString("amount"))));

                List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
                transferInfoEntities.add(new TransferInfoEntity("银行卡卡号", CardUtil.formatCardNumberWithStar(balanceTransInfo.getPayCardNo())));
                transferInfoEntities.add(new TransferInfoEntity("银行卡余额", balanceTransInfo.getBalance(),true));
                confirmInfo.removeAllViews();
                confirmInfo.addList(BalanceInqueryActivity.this, transferInfoEntities, true, getResources().getColor(R.color.white));
                showErrorView();
                updatePrompt("查询成功");
                initPoint2();
                if(swiperInfo.getCardType() == SwiperInfo.CardType.ICCard){
                    swiperManagerHandler.cancelEmv(true);
                }

            }catch (JSONException e){
                updatePrompt("查询失败");
                balanceTransInfo.setMsg(getString(R.string.plat_http_004));
                showErrorView();
                if(swiperInfo.getCardType() == SwiperInfo.CardType.ICCard){
                    swiperManagerHandler.cancelEmv(false);
                }
            }
        }else{
            updatePrompt("查询失败");
            balanceTransInfo.setMsg(resultServices.retMsg);
            showErrorView();
            if(swiperInfo.getCardType() == SwiperInfo.CardType.ICCard){
                swiperManagerHandler.cancelEmv(false);
            }
        }
    }

    @Override
    public void onEvent(HttpConnectEvent connectEvent) {
        balanceTransInfo.setMsg("网络连接异常");
        showErrorView();
        if(swiperInfo.getCardType() == SwiperInfo.CardType.ICCard){
            swiperManagerHandler.cancelEmv(false);
        }
        updatePrompt("查询失败");
    }


    @Override
    public void requestUploadTc(SwiperInfo swiperInfo) {

    }

    public void initPoint(){
        String event="";
        if(PublicEnum.Business.isHome()){
            event= ShoudanStatisticManager.Balance_Inquiry_Home;
        }else if(PublicEnum.Business.isDirection()){
            event= ShoudanStatisticManager.Balance_Inquiry_De;
        }else if(PublicEnum.Business.isAd()){
            event= ShoudanStatisticManager.Balance_Inquiry_Ad;
        }else if(PublicEnum.Business.isPublic()){
            event= ShoudanStatisticManager.Balance_Inquiry_Public;
        }else {
            event= ShoudanStatisticManager.Balance_Inquiry_Succes;
        }
        ShoudanStatisticManager.getInstance().onEvent(event,this);
    }

    public void initPoint2(){
        String event="";
        if(PublicEnum.Business.isHome()){
            event= ShoudanStatisticManager.Balance_Inquiry_Home_Succes;
        }else if(PublicEnum.Business.isDirection()){
            event= ShoudanStatisticManager.Balance_Inquiry_De_Succes;
        }else if(PublicEnum.Business.isAd()){
            event= ShoudanStatisticManager.Balance_Inquiry_Ad_Succes;
        }else if(PublicEnum.Business.isPublic()){
            event= ShoudanStatisticManager.Balance_Inquiry_Public_Succes;
        }else {
            event= ShoudanStatisticManager.Balance_Inquiry_Succes_Succes;
        }
        ShoudanStatisticManager.getInstance().onEvent(event,this);
    }
}
