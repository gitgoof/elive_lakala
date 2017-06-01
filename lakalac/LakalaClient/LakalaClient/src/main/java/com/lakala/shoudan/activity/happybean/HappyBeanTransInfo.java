package com.lakala.shoudan.activity.happybean;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by huangjp on 2016/5/18.
 */
public class HappyBeanTransInfo extends BaseTransInfo implements Serializable{
    private String amount;//交易金额
    private String time;//交易时间
    private String dealType;//交易类型
    private String rechargeType;//充值方式：刷卡支付、快捷支付、钱包支付


    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.HAPPYBEAN;
    }

    @Override
    public String getAdditionalMsg() {
        return null;
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
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("交易类型号:",getDealType()));
        transferInfoEntities.add(new TransferInfoEntity("交易时间:",  DateUtil.formateDateTransTime(getSyTm())));
        transferInfoEntities.add(new TransferInfoEntity("充值金额:",
                StringUtil.formatDisplayAmount(getAmount()),
                true));
        transferInfoEntities.add(new TransferInfoEntity("充值方式",getRechargeType()));
        return transferInfoEntities;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        return null;
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
}
