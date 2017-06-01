package com.lakala.shoudan.activity.wallet.bean;

import android.text.TextUtils;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.WalletWithdrawEnum;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengxuan on 2015/12/22.
 */
public class WithdrawTransInfo extends BaseTransInfo implements Serializable{

    private String amount;
    private String card;
    private String date;
    private String fee;
    private String balance;
    private String errorMsg;
//    private boolean isAdJumpTo;

//    public boolean isAdJumpTo() {
//        return isAdJumpTo;
//    }

//    public void setIsAdJumpTo(boolean isAdJumpTo) {
//        this.isAdJumpTo = isAdJumpTo;
//    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
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
        return null;
    }

    @Override
    public String getRepayName() {
        return null;
    }

    @Override
    public String getTransTypeName() {
        return "零钱转出";
    }

    @Override
    public String getSwipeAmount() {
        return null;
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {

        List<TransferInfoEntity> entityList = new ArrayList<TransferInfoEntity>();
        entityList.add(new TransferInfoEntity("转款卡:",getCard()));
        entityList.add(new TransferInfoEntity("转出金额:",getAmount()));
        entityList.add(new TransferInfoEntity("交易时间:",getDate()));
        entityList.add(new TransferInfoEntity("手续费:",getFee()));
        if (getErrorMsg() != null){
            entityList.add(new TransferInfoEntity("错误信息:",getErrorMsg()));
        }
        return entityList;
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        return null;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        return null;
    }

    @Override
    public String getStatisticTransResult() {
        String event="";
        if (!TextUtils.isEmpty(WalletWithdrawEnum.WalletWithdraw.getAdvertId())) {//广告id不为null，从广告进入
            event = String.format(ShoudanStatisticManager.Advert_Wallet_Withdraw_Success, WalletWithdrawEnum.WalletWithdraw.getAdvertId());
        }else{
            event = ShoudanStatisticManager.Wallet_Withdraw_Success;
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
    public TransactionType getType() {
        return TransactionType.WALLET_TRANSFER;
    }

    @Override
    public String getAdditionalMsg() {
        return "充值账户:" + ApplicationEx.getInstance().getUser().getLoginName();
    }
}
