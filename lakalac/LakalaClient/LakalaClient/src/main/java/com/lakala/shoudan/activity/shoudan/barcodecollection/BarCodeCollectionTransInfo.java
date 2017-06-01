package com.lakala.shoudan.activity.shoudan.barcodecollection;

import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.ScanCodeCollectionEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.component.VerticalListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/9/22.
 */
public class BarCodeCollectionTransInfo extends BaseTransInfo {

    public static final String WECHAT = "WECHAT";
    public static final String ALIPAY = "ALIPAY";
    public static final String BAIDUPAY = "BAIDUPAY";
    public static final String UNIONPAY = "UNIONPAY";


    private String amount;

    private VerticalListView.IconType type;

    public void setType(VerticalListView.IconType type) {
        this.type = type;
    }
    public VerticalListView.IconType getIconType(){
        return this.type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public boolean isSignatureNeeded() {
        return false;
    }

    @Override
    public boolean ifSupportIC() {
        return false;
    }

    @Override
    public String getTransTitle() {
        return "扫码收款";
    }

    @Override
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
        String event="";
        if(ScanCodeCollectionEnum.ScanCodeCollection.isHomePage()){
            //首页进入并成功
            event=ShoudanStatisticManager.Scan_Code_Collection_Success_Homepage;
        }else if(!TextUtils.isEmpty(ScanCodeCollectionEnum.ScanCodeCollection.getAdvertId())){
            event = String.format(ShoudanStatisticManager.Advert_Scan_Code_Collection_Success,ScanCodeCollectionEnum.ScanCodeCollection.getAdvertId());
        }else{
            event=ShoudanStatisticManager.Scan_Code_Collection_Success;
        }
        return event;
    }

    @Override
    public String getStatisticSignPage() {
        return null;
    }

    @Override
    public String getStatisticIsSend() {
        return null;
    }

    @Override
    public String getRepayName() {
        return "支付";
    }

    @Override
    public String getTransTypeName() {
        return "扫码收款";

    }

    @Override
    public String getSwipeAmount() {
        return getAmount();
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {

        return getBillInfo();
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        return null;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
//        infoEntities.add(new TransferInfoEntity("用户名", ApplicationEx.getInstance().getUser().getMerchantInfo().getBusinessName()));
        infoEntities.add(new TransferInfoEntity("交易状态", transResult == TransResult.SUCCESS ? "交易成功" : (transResult == TransResult.FAILED ? "交易失败" : "交易超时")));
        infoEntities.add(new TransferInfoEntity("付款方式", type.getType(), type));
        infoEntities.add(new TransferInfoEntity("日期/时间", Util.getNowTime()));
        infoEntities.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(getAmount()), true));
        return infoEntities;

    }


    @Override
    public TransactionType getType() {
        return TransactionType.BARCODE_COLLECTION;
    }

    @Override
    public String getAdditionalMsg() {
        return "";//扫码收款，未使用到刷卡器
    }
}
