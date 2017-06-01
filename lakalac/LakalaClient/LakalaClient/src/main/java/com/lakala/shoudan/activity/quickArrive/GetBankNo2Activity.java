package com.lakala.shoudan.activity.quickArrive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPayment3Activity;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.revocation.RevocationRecordSelectionActivity;
import com.lakala.shoudan.activity.revocation.RevocationTransinfo;
import com.lakala.shoudan.activity.shoudan.records.DealDetails;
import com.lakala.shoudan.activity.shoudan.records.TradeQueryInfo;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;
import com.loopj.lakala.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 刷卡读取卡号
 * Created by More on 15/3/15.
 */
public class GetBankNo2Activity extends NewCommandProtocolPayment3Activity {

    private RevocationTransinfo2 revocationTransinfo;
    public static List<DealDetails> dealDetails = new ArrayList<DealDetails>();
    private SwiperInfo swiperInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        revocationTransinfo = (RevocationTransinfo2)getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swiperManagerHandler.setPinInputSeparate(true);
    }

//    @Override
//    protected boolean divideNeed() {
//        return false;
//    }

    @Override
    public void onProcessEvent(SwiperProcessState swiperProcessState, SwiperInfo swiperInfo) {
        super.onProcessEvent(swiperProcessState, swiperInfo);
        if(swiperProcessState == SwiperProcessState.SWIPE_END){
//            LogUtil.print("<S>",swiperInfo.toString());
////            queryRecords(swiperInfo);
//            Intent intent=new Intent();
//            intent.putExtra("swiperInfo",swiperInfo);
//            setResult(Activity.RESULT_OK,intent);
            startPayment(swiperInfo);
//            startActivity(new Intent(GetBankNo2Activity.this,InCardNoActivity.class)
//                    .putExtra("cardType",swiperInfo.getCardType()).putExtra("cardNo",swiperInfo.getMaskedPan()));
//            finish();

        }else if(swiperProcessState == SwiperProcessState.WAITING_FOR_PIN_INPUT){
            swiperManagerHandler.setResetable(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        swiperManagerHandler.setResetable(true);
        if(requestCode == 0 && resultCode == RESULT_OK){

            dealDetails.clear();
            record = RevocationRecordSelectionActivity.selectedDetais;

            List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
            transferInfoEntities.add(new TransferInfoEntity("收款金额", StringUtil.formatDisplayAmount(String.valueOf(record.getDealAmount())),true));
            transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.stringToDate(record.getDealStartDateTime())));
            confirmInfo.removeAllViews();
            confirmInfo.addList(this, transferInfoEntities,true,getResources().getColor(R.color.white));
            revocationTransinfo.setSrcAmount(String.valueOf(record.getDealAmount()));

            swiperManagerHandler.startInputPin();


        }else{
            dealDetails.clear();
            swiperManagerHandler.reset();
            finish();
        }
    }

    private DealDetails record;
//
//
//    protected void reStartSwipe(){
//        confirmInfo.removeAllViews();
//        confirmInfo.addList(this, baseTransInfo.getConfirmInfo(), divideNeed(),getResources().getColor(R.color.white));
//
//        startSwipe();
//    }

    @Override
    protected void addTransParams(RequestParams requestParams) {
        LogUtil.print("<S>","addTransParams");
        User user = ApplicationEx.getInstance().getUser();
        requestParams.put("busid", "X10000");
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
//        requestParams.put("srcsid",record.getSid());
        requestParams.put("mobile", user.getLoginName());
        requestParams.put("srcseries", Util.createSeries());
        requestParams.put("srcauth", "");
//        requestParams.put("pan", record.getPaymentAccount());
        requestParams.put("mobileno", ApplicationEx.getInstance().getUser().getLoginName());
//        requestParams.put("amount",  Utils.yuan2Fen(String.valueOf(record.getDealAmount())));
        requestParams.put("tdtm", Utils.createTdtm());
    }

    @Override
    public void finish() {
        if(BusinessLauncher.getInstance().getTopActivity() instanceof RevocationRecordSelectionActivity){
            BusinessLauncher.getInstance().getTopActivity().finish();
        }
        super.finish();
    }
}
