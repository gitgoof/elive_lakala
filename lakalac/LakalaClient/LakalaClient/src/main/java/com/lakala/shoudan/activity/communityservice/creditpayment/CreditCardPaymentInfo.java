package com.lakala.shoudan.activity.communityservice.creditpayment;

import android.text.TextUtils;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.Utils;
import com.lakala.platform.statistic.CreditCardEnum;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 15/2/9.
 */
public class CreditCardPaymentInfo extends BaseTransInfo {

    private String amount;//还款金额
    private String number;//信用卡号
    private String mobileno;//短信通知号码
    private String issms;//是否短信通知  短信通知:"1";不用短信通知"0";
    private String price = "0";//支付金额
    private String fee = "0";//还款手续费
    private String userName;
    private String srcSid;

    public String getSrcSid() {
        return srcSid;
    }

    public void setSrcSid(String srcSid) {
        this.srcSid = srcSid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getIssms() {
        return issms;
    }

    public void setIssms(String issms) {
        this.issms = issms;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
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
        return "信用卡还款";
    }

    @Override
    public String getRepayName() {
        return "还款";
    }

    @Override
    public String getTransTypeName() {
        return "信用卡还款";
    }

    @Override
    public String getSwipeAmount() {
        //获取刷卡金额 还款金额+手续费
        String totalAmount;
        if (!TextUtils.isEmpty(getFee())) {//有手续费，需要加上手续费
            totalAmount = String.valueOf(new BigDecimal(getAmount()).add(new BigDecimal(getFee())));
        } else {
            totalAmount = getAmount();
        }
        return totalAmount;
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        infoEntities.add(new TransferInfoEntity("交易流水号", getSysRef() == null ? "" : getSysRef()));
        infoEntities.add(new TransferInfoEntity("还款信用卡", StringUtil.formatCardNumberN6S4N4(getNumber())));
        infoEntities.add(new TransferInfoEntity("付款银行卡", getPayCardNo()));
        infoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formateDateTransTime(getSyTm())));
        infoEntities.add(new TransferInfoEntity("还款金额", StringUtil.formatDisplayAmount(getAmount()),
                true));
        infoEntities.add(new TransferInfoEntity("手续费", StringUtil.formatDisplayAmount(getFee()), true));
        infoEntities.add(new TransferInfoEntity("刷卡金额", StringUtil.formatDisplayAmount(getPrice()), true));
        return infoEntities;

    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        infoEntities.add(new TransferInfoEntity("信用卡号", StringUtil.formatCardNumberN6S4N4(number)));
        if (!TextUtils.isEmpty(getUserName())) {//有姓名，显示姓名
            infoEntities.add(new TransferInfoEntity("持卡人姓名:", Utils.formateUserNameKeepLast(getUserName())));
        }
        infoEntities.add(new TransferInfoEntity("还款金额", StringUtil.formatDisplayAmount(amount)));
        infoEntities.add(new TransferInfoEntity("手续费", StringUtil.formatDisplayAmount(getFee())));
        infoEntities.add(new TransferInfoEntity("刷卡金额", StringUtil.formatDisplayAmount(getPrice())));

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
        String event = "";
        if (PublicEnum.Business.isDirection()) {
            event = ShoudanStatisticManager.Credit_Card_paySuccess_direction;
        } else if (PublicEnum.Business.isHome()) {
            event = ShoudanStatisticManager.Credit_Card_paySuccess_home;
        } else if (PublicEnum.Business.isAd()) {
            event = ShoudanStatisticManager.Credit_Card_paySuccess_advert;
        } else if (PublicEnum.Business.isPublic()) {
            event = ShoudanStatisticManager.Credit_Card_paySuccess_public;
        }
        String smsStatus = CreditCardEnum.PayBack.isSmsOpen() ? "是" : "否";
        String reachStatus = CreditCardEnum.PayBack.isPayOpen() ? "是" : "否";
        event = String.format(event, smsStatus, reachStatus);
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.CreditCardPayment;
    }

    @Override
    public String getAdditionalMsg() {
        return "卡号:" + number;
    }
}
