package com.lakala.shoudan.activity.shoudan.largeamountcollection;

import android.text.TextUtils;

import com.lakala.library.util.CardUtil;
import com.lakala.library.util.DateUtil;
import com.lakala.platform.statistic.LargeAmountEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/6/11.
 */
public class LargeAmountTransInfo extends BaseTransInfo {

    private String amount;

    public String getAmount() {
        return amount;
    }

    public LargeAmountTransInfo setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public boolean isSignatureNeeded() {
        return true;
    }

    @Override
    public boolean ifSupportIC() {
        return true;
    }

    @Override
    public String getTransTitle() {
        return "大额收款";
    }

    @Override
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
        String event="";
        if(!TextUtils.isEmpty(LargeAmountEnum.LargeAmount.getAdvertId())){//广告id不为null，从广告进入
            event = String.format(ShoudanStatisticManager.Advert_LargeAmount_Collection_Success,LargeAmountEnum.LargeAmount.getAdvertId());
        }else{//收款一级菜单进入并成功
            event=ShoudanStatisticManager.LargeAmount_Collection_Success;
        }
        return event;
    }

    @Override
    public String getStatisticSignPage() {
        return ShoudanStatisticManager.LargeAmount_Collection_Page;
    }

    @Override
    public String getStatisticIsSend() {
        return ShoudanStatisticManager.LargeAmount_Collection_Send;
    }

    private String infoCollectSid;

    private String mobileNo;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getInfoCollectSid() {
        return infoCollectSid;
    }

    public void setInfoCollectSid(String infoCollectSid) {
        this.infoCollectSid = infoCollectSid;
    }

    @Override
    public String getRepayName() {
        return "收款";
    }

    @Override
    public String getTransTypeName() {
        return "大额收款";
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

        List<TransferInfoEntity> transferInfoEntityList = new ArrayList<TransferInfoEntity>();
        transferInfoEntityList.add(new TransferInfoEntity("收款金额", Util.formatDisplayAmount(getAmount()), true));
        transferInfoEntityList.add(new TransferInfoEntity("结算账户", ApplicationEx.getInstance().getUser().getMerchantInfo().getBankName() + "\n" + Util.formatCardNumberWithStar(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo())));

        return transferInfoEntityList;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        infoEntities.add(new TransferInfoEntity("交易状态", transResult == TransResult.SUCCESS ? "大额收款成功" : "结果未知"));
        infoEntities.add(new TransferInfoEntity("卡号", CardUtil.formatCardNumberWithStar(payCardNo)));
        infoEntities.add(new TransferInfoEntity("日期/时间", DateUtil.formateDateTransTime(syTm)));
        infoEntities.add(new TransferInfoEntity("交易金额", com.lakala.library.util.StringUtil.formatDisplayAmount(amount),true));
        return infoEntities;
    }


    @Override
    public TransactionType getType() {
        return TransactionType.LARGE_AMOUNT_COLLECTION;
    }

    @Override
    public String getAdditionalMsg() {
        return "用户号:" + ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo();
    }
}
