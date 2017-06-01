package com.lakala.shoudan.activity.communityservice.balanceinquery;

import com.lakala.platform.statistic.StatisticType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/2/7.
 */
public class BalanceTransInfo extends BaseTransInfo implements Serializable {


    private String balance;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
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
        return "余额查询";
    }

    @Override
    public String getRepayName() {
        return "查询";
    }

    @Override
    public String getTransTypeName() {
        return "余额查询";
    }

    @Override
    public String getSwipeAmount() {
        return "0";
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {

        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();

        return transferInfoEntities;

    }


    @Override
    public List<TransferInfoEntity> getBillInfo() {
        return  getResultInfo();
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("银行卡卡号", "","刷卡或插卡后自动显示卡号"));
        transferInfoEntities.add(new TransferInfoEntity("银行卡余额", "","等待查询交易"));
        return transferInfoEntities;
    }

    @Override
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
//        return StatisticType.Balance_2;
        return null;
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
        return TransactionType.Query;
    }

    @Override
    public String getAdditionalMsg() {
        return "";
    }
}
