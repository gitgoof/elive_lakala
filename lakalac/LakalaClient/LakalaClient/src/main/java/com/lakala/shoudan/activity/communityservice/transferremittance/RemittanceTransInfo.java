package com.lakala.shoudan.activity.communityservice.transferremittance;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.statistic.TransferEnum;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/3/10.
 */
public class RemittanceTransInfo extends BaseTransInfo {
    private String openBank;
    private String bankCode;
    private String cardNumber;
    private String name;
    private String amount;
    private String transFee;
    private String phone;
    private int transType;
    private String transTypeText;
    private String remark;
    private String srcsid;
    private String billno;
    private String orderId;
    private String price;

    public String getOpenBank() {
        return openBank;
    }

    public void setOpenBank(String openBank) {
        this.openBank = openBank;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransFee() {
        return transFee;
    }

    public void setTransFee(String transFee) {
        this.transFee = transFee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public String getTransTypeText() {
        return transTypeText;
    }

    public void setTransTypeText(String transTypeText) {
        this.transTypeText = transTypeText;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSrcsid() {
        return srcsid;
    }

    public void setSrcsid(String srcsid) {
        this.srcsid = srcsid;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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
        return "转账汇款";
    }

    @Override
    public String getRepayName() {
        return "转账";
    }

    @Override
    public String getTransTypeName() {
        return "转账汇款";
    }

    @Override
    public String getSwipeAmount() {
        double swipeAmount = 0;
        try {
            swipeAmount = Double.parseDouble(getAmount()) + Double.parseDouble(getTransFee());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return String.valueOf(swipeAmount);
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("交易流水号", getSysRef()));
        transferInfoEntities.add(new TransferInfoEntity("转账借记卡", StringUtil.formatCardNumberN6S4N4(getCardNumber())));
        transferInfoEntities.add(new TransferInfoEntity("付款借记卡", getPayCardNo()));
        transferInfoEntities.add(new TransferInfoEntity("到账时间", getTransTypeText()));
        transferInfoEntities.add(new TransferInfoEntity("交易时间", DateUtil.formateDateTransTime(getSyTm())));
        transferInfoEntities.add(new TransferInfoEntity("转账金额", StringUtil.formatDisplayAmount(getAmount()), true));
        transferInfoEntities.add(new TransferInfoEntity("手续费", StringUtil.formatDisplayAmount(getTransFee()), true));
        transferInfoEntities.add(new TransferInfoEntity("刷卡金额", StringUtil.formatDisplayAmount(getSwipeAmount()), true));
        return transferInfoEntities;
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("收款银行", getOpenBank()));
        transferInfoEntities.add(new TransferInfoEntity("收款卡号", StringUtil.formatCardNumberN6S4N4(getCardNumber())));
        transferInfoEntities.add(new TransferInfoEntity("收款人", name));
        transferInfoEntities.add(new TransferInfoEntity("到账时间", getTransTypeText()));
        transferInfoEntities.add(new TransferInfoEntity("刷卡金额",
                StringUtil.formatDisplayAmount(getSwipeAmount()),
                true));
        return transferInfoEntities;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        return getConfirmInfo();
    }

    @Override
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
        String event = "";
        if (PublicEnum.Business.isDirection()) {
            event = ShoudanStatisticManager.Transfer_Remittance_Success_direction;
        } else if (PublicEnum.Business.isHome()) {
            event = ShoudanStatisticManager.Transfer_Remittance_Success_home;
        } else if (PublicEnum.Business.isAd()) {
            event = ShoudanStatisticManager.Transfer_Remittance_Success_advert;
        } else if (PublicEnum.Business.isPublic()) {
            event = ShoudanStatisticManager.Transfer_Remittance_Success_public;
        }
        String contactStatus = TransferEnum.transfer.isContanct() ? "选" : "填";
        String smsStatus = TransferEnum.transfer.isSmsOpen() ? "是" : "否";
        String reachStatus = TransferEnum.transfer.isReachTime() ? "1" : "2";
        String phoneStatus = TransferEnum.transfer.isPhone() ? "填" : "选";
        event = String.format(event, contactStatus, reachStatus, smsStatus, phoneStatus);
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
        return TransactionType.Remittance;
    }

    @Override
    public String getAdditionalMsg() {
        return "卡号:" + cardNumber;
    }
}
