package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by fengx on 2015/10/19.
 */
public class WithdrawInfo extends BaseTransInfo{
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
        return null;
    }

    @Override
    public String getSwipeAmount() {
        return null;
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        return null;
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
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
        return ShoudanStatisticManager.Finance_Withdraw_Success;
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
        return TransactionType.WITHDRAW;
    }

    //TODO wait for lixiangyu
    @Override
    public String getAdditionalMsg() {
        return null;
    }
}
