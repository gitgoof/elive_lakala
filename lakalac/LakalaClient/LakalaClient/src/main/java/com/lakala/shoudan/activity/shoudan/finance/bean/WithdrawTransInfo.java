package com.lakala.shoudan.activity.shoudan.finance.bean;

import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengx on 2015/10/22.
 */
public class WithdrawTransInfo extends BaseTransInfo{

    private String sid;
    private String prod;
    private String time;
    private String amount;

    public String getTime() {
        return time;
    }

    public WithdrawTransInfo setTime(String time) {
        this.time = time;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public WithdrawTransInfo setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    @Override
    public String getSid() {
        return sid;
    }

    public String getProd() {
        return prod;
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
        return "取出";
    }

    @Override
    public String getSwipeAmount() {
        return null;
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
        if (getTransResult() == TransResult.SUCCESS){
            infoEntities.add(new TransferInfoEntity("交易流水号",getSid()));
            infoEntities.add(new TransferInfoEntity("交易时间",getTime()));
            infoEntities.add(new TransferInfoEntity("交易状态","取出成功"));
            infoEntities.add(new TransferInfoEntity("交易金额",getAmount()));
            infoEntities.add(new TransferInfoEntity("取出/卖出产品",getProd()));
        }else {
            infoEntities.add(new TransferInfoEntity("交易时间",getTime()));
            infoEntities.add(new TransferInfoEntity("交易状态","取出失败"));
            infoEntities.add(new TransferInfoEntity("交易金额",getAmount()));
            infoEntities.add(new TransferInfoEntity("取出/卖出产品",getProd()));
        }

        return infoEntities;
    }

    @Override
    public void unpackValidateResult(JSONObject jsonObject) {

    }


    @Override
    public String getStatisticTransResult() {
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
        return TransactionType.WITHDRAW;
    }

    @Override
    public String getAdditionalMsg() {
        return null;
    }
}
