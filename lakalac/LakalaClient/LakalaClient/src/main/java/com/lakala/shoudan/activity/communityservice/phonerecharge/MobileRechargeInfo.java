package com.lakala.shoudan.activity.communityservice.phonerecharge;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.bean.MobileChargeCard;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/3/15.
 */
public class MobileRechargeInfo extends BaseTransInfo implements Serializable{
    private String phoneNumber;
    private MobileChargeCard chargeCard;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MobileChargeCard getChargeCard() {
        return chargeCard;
    }

    public void setChargeCard(MobileChargeCard chargeCard) {
        this.chargeCard = chargeCard;
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
        return "手机充值";
    }

    @Override
    public String getRepayName() {
        return "充值";
    }

    @Override
    public String getTransTypeName() {
        return "手机充值";
    }

    @Override
    public String getSwipeAmount() {
        return chargeCard.getCardName();
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        //transferInfoEntities.add(new TransferInfoEntity("付款银行卡", getCardNo().replace("X","*")));
        transferInfoEntities.add(new TransferInfoEntity("交易流水号:", getSysRef() == null? "": getSysRef()));
        transferInfoEntities.add(new TransferInfoEntity("充值手机号:", getPhoneNumber()));
        transferInfoEntities.add(new TransferInfoEntity("交易时间:",  DateUtil.formateDateTransTime(getSyTm())));
        transferInfoEntities.add(new TransferInfoEntity("充值金额:",
                                                        StringUtil.formatDisplayAmount(chargeCard.getCardName()),
                                                        true));
        transferInfoEntities.add(new TransferInfoEntity("刷卡金额:",  StringUtil.formatDisplayAmount(chargeCard.getCardName()),true));

        return transferInfoEntities;
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        String amount = chargeCard.getCardName();
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("充值手机号:", getPhoneNumber()));
        transferInfoEntities.add(new TransferInfoEntity("充值金额:", StringUtil.formatDisplayAmount(amount),
                                                        true));

        return transferInfoEntities;
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
        String event="";
        if(PublicEnum.Business.isHome()){
        }else if(PublicEnum.Business.isDirection()){
            event=ShoudanStatisticManager.Phone_Recharge_Success_De;
        }else if(PublicEnum.Business.isAd()){
            event=ShoudanStatisticManager.Phone_Recharge_Success_Ad;
        }else if(PublicEnum.Business.isPublic()){
            event=ShoudanStatisticManager.Phone_Recharge_Success_Public;
        }else {
            event=ShoudanStatisticManager.Phone_Recharge_Success_Succes;
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
        return TransactionType.MobileRecharge;
    }

    @Override
    public String getAdditionalMsg() {
        return "充值手机号:"+ phoneNumber;
    }
}
