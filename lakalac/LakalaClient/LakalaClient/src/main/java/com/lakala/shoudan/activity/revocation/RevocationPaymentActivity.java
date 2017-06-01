package com.lakala.shoudan.activity.revocation;

import android.content.Intent;
import android.os.Bundle;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.consts.ConstKey;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.payment.NewCommandProtocolPaymentActivity;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
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
 * Created by More on 15/3/15.
 */
public class RevocationPaymentActivity extends NewCommandProtocolPaymentActivity {

    private RevocationTransinfo revocationTransinfo;
    private int prepage = 1;
    private int totalpage = 1;
    public static List<DealDetails> dealDetails = new ArrayList<DealDetails>();
    private String bankName = "";
    private String cardName = "";
    private String series = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        revocationTransinfo = (RevocationTransinfo)getIntent().getSerializableExtra(ConstKey.TRANS_INFO);
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Undo_Pwd_SwipeFailure,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        swiperManagerHandler.setPinInputSeparate(true);
    }

    @Override
    protected boolean divideNeed() {
        return false;
    }

    @Override
    public void onProcessEvent(SwiperProcessState swiperProcessState, SwiperInfo swiperInfo) {
        super.onProcessEvent(swiperProcessState, swiperInfo);
        if(swiperProcessState == SwiperProcessState.SWIPE_END){

            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Undo_Pwd_SwipeSuccess,this);
            queryRecords(swiperInfo);

        }else if(swiperProcessState == SwiperProcessState.WAITING_FOR_PIN_INPUT){
            swiperManagerHandler.setResetable(true);
        }
    }


    private void queryCardInfo(final String cardNo){

        ShoudanService.getInstance().queryCardInfo(cardNo, new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        bankName = jsonObject.optString("bankName");
                        cardName = jsonObject.optString("cardName");
                        hideProgress();
                        swiperManagerHandler.setResetable(false);
                        Intent intent = new Intent(RevocationPaymentActivity.this, RevocationRecordSelectionActivity.class);
                        intent.putExtra("bankName",bankName);
                        intent.putExtra("cardName",cardName);
                        startActivityForResult(intent, 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    hideProgress();
                    toast(resultServices.retMsg);
                    finish();
                    return;
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                toastInternetError();
                hideProgress();
                finish();
                return;
            }
        });
    }
    private void queryRecords(final SwiperInfo swiperInfo){


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String startTime = sdf.format(new Date());
        sdf = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String endTime = sdf.format(new Date());

        showProgress();
        ShoudanService.getInstance().queryRevocationRecords(true, prepage, startTime, endTime, "P01", swiperInfo.getTransTypePan(), "18X", "", "",
                ApplicationEx.getInstance().getSession().getCurrentKSN(), new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                String msg="";
                if (resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        TradeQueryInfo tradeQueryInfo = new TradeQueryInfo();
                        tradeQueryInfo = tradeQueryInfo.parseObject(jsonObject);
                        totalpage = tradeQueryInfo.getTotalPage();
                        if (totalpage == 0){
                            toast("暂时没有可撤销的交易");
                            msg="暂时没有可撤销的交易";
                            finish();
                            return;
                        }
                        try {
                            series = tradeQueryInfo.getDealDetails().get(0).getSeries();
                        } catch (Exception e) {
                            e.printStackTrace();
                            toast("返回数据异常");
                            msg="返回数据异常";
                            finish();
                            return;
                        }
                        dealDetails.addAll(tradeQueryInfo.getDealDetails());
                        if (prepage >= totalpage){
                            queryCardInfo(swiperInfo.getTransTypePan());
                        }else {
                            prepage++;
                            queryRecords(swiperInfo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    hideProgress();
                    toast(resultServices.retMsg);
                    msg=resultServices.retMsg;
                    finish();
                }
                String event=String.format(ShoudanStatisticManager.Undo_Pwd_Failure,msg);
                ShoudanStatisticManager.getInstance().onEvent(event,RevocationPaymentActivity.this);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgress();
                toastInternetError();
                String event=String.format(ShoudanStatisticManager.Undo_Pwd_Failure,"网络连接失败");
                ShoudanStatisticManager.getInstance().onEvent(event,RevocationPaymentActivity.this);
                finish();
            }
        });

    }

    public static final String TRANS_RECORDS = "trans_records";
    public static final String  REVOCATION_RECORD = "revocation_record";

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


    protected void reStartSwipe(){
        confirmInfo.removeAllViews();
        confirmInfo.addList(this, baseTransInfo.getConfirmInfo(), divideNeed(),getResources().getColor(R.color.white));

        startSwipe();
    }

    @Override
    protected void addTransParams(RequestParams requestParams) {
        User user = ApplicationEx.getInstance().getUser();
        requestParams.put("busid", "18Y");
        requestParams.put("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo());
        requestParams.put("srcsid",record.getSid());
        requestParams.put("mobile", user.getLoginName());
        requestParams.put("srcseries", Util.createSeries());
        requestParams.put("srcauth", "");
        requestParams.put("pan", record.getPaymentAccount());
        requestParams.put("mobileno", ApplicationEx.getInstance().getUser().getLoginName());
        requestParams.put("amount",  Utils.yuan2Fen(String.valueOf(record.getDealAmount())));
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
