package com.lakala.shoudan.activity.wallet.bean;

import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.WalletRechargeEnum;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxuan on 2015/12/17.
 */
public class RechargeTransInfo extends BaseTransInfo implements Serializable{

    private String amount;
    private String errorMsg;
    private String walletBalance;
//    private boolean isAdJumpTo;

//    public boolean isAdJumpTo() {
//        return isAdJumpTo;
//    }

//    public void setIsAdJumpTo(boolean isAdJumpTo) {
//        this.isAdJumpTo = isAdJumpTo;
//    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.WALLET_RECHARGE;
    }

    @Override
    public boolean isSignatureNeeded() {
        return false;
    }

    @Override
    public boolean ifSupportIC() {
        return true;
    }

    @Override
    public String getTransTitle() {
        return "零钱充值";
    }

    @Override
    public String getRepayName() {
        return "零钱充值";
    }

    @Override
    public String getTransTypeName() {
        return "零钱充值";
    }

    @Override
    public String getSwipeAmount() {
        return amount;
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        List<TransferInfoEntity> entityList = new ArrayList<TransferInfoEntity>();
        entityList.add(new TransferInfoEntity("交易类型:","零钱充值"));
        entityList.add(new TransferInfoEntity("充值钱包账户:", ApplicationEx.getInstance().getUser().getLoginName()));
        entityList.add(new TransferInfoEntity("充值金额:",amount));
        entityList.add(new TransferInfoEntity("钱包余额:",walletBalance));
        if (errorMsg != null){
            entityList.add(new TransferInfoEntity("错误信息",errorMsg));
        }
        return entityList;
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        List<TransferInfoEntity> entityList = new ArrayList<TransferInfoEntity>();
        entityList.add(new TransferInfoEntity("充值方式：","刷卡充值"));
        entityList.add(new TransferInfoEntity("充值金额：",amount));
        return entityList;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        return getResultInfo();
    }

    @Override
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
        String event="";
        if (!TextUtils.isEmpty(WalletRechargeEnum.WalletRecharge.getAdvertId())) {//广告id不为null，从广告进入
            event = String.format(ShoudanStatisticManager.Advert_Recharge_Success,WalletRechargeEnum.WalletRecharge.getAdvertId());
        }else{
            event = ShoudanStatisticManager.Wallet_Recharge_Success;
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
    public String getAdditionalMsg() {
        return "充值账户:"+ApplicationEx.getInstance().getUser().getLoginName();
    }
}
