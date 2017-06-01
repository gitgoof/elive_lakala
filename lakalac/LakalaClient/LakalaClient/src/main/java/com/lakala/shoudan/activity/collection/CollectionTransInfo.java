package com.lakala.shoudan.activity.collection;

import android.text.TextUtils;

import com.lakala.library.util.CardUtil;
import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.bean.AccountType;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.CollectionEnum;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.StatisticManager;
import com.lakala.platform.statistic.StatisticType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/2/10.
 */
public class CollectionTransInfo extends BaseTransInfo implements Serializable{

    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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
        return "收款";
    }

    @Override
    public String getRepayName() {
        return "收款";
    }

    @Override
    public String getTransTypeName() {
        return "收款";
    }

    @Override
    public String getSwipeAmount() {
        return amount;
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        infoEntities.add(new TransferInfoEntity("交易状态", transResult == TransResult.SUCCESS ? "收款成功" : "结果未知"));
        infoEntities.add(new TransferInfoEntity("卡号", CardUtil.formatCardNumberWithStar(payCardNo)));
        infoEntities.add(new TransferInfoEntity("日期/时间", DateUtil.formateDateTransTime(syTm)));
        infoEntities.add(new TransferInfoEntity("交易金额", StringUtil.formatDisplayAmount(amount),true));
        return infoEntities;
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {

        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        User user = ApplicationEx.getInstance().getUser();
        infoEntities.add(new TransferInfoEntity("收款金额", StringUtil.formatDisplayAmount(amount), true));
        if(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountType() == AccountType.PRIVATE){//对私
            infoEntities.add(new TransferInfoEntity("结算账户", user.getMerchantInfo().getBankName() + "\n" + CardUtil.formatCardNumberWithStar(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo())).setNeedDevider(false));
        }else {
            infoEntities.add(new TransferInfoEntity("结算账户", user.getMerchantInfo().getBankName() + "\n" + CardUtil.formateCardNumberOfCompany(ApplicationEx.getInstance().getUser().getMerchantInfo().getAccountNo())).setNeedDevider(false));
        }

        return infoEntities;
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
        return "";
    }

    @Override
    public String getStatisticSignPage() {
        String event="";
        if(PublicEnum.Business.isHome()){
            event= ShoudanStatisticManager.Collection_Home_Sign;
        }else if(PublicEnum.Business.isDirection()){
        }else if(PublicEnum.Business.isAd()){
        }else if(PublicEnum.Business.isPublic()){
            event= ShoudanStatisticManager.Collection_Public_Sign;
        }else {
            event=ShoudanStatisticManager.Collection_Succes_Sign;
        }
        return event;
    }

    @Override
    public String getStatisticIsSend() {
        String event="";
        if(PublicEnum.Business.isHome()){
            event= ShoudanStatisticManager.Collection_Home_Send;
        }else if(PublicEnum.Business.isDirection()){
        }else if(PublicEnum.Business.isAd()){
        }else if(PublicEnum.Business.isPublic()){
            event= ShoudanStatisticManager.Collection_Public_Send;
        }else {
            event=ShoudanStatisticManager.Collection_Succes_Send;
        }
        return event;
    }


    @Override
    public TransactionType getType() {
        return TransactionType.Collection;
    }

    @Override
    public String getAdditionalMsg() {
        return "用户号:" + ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo();
    }


}
