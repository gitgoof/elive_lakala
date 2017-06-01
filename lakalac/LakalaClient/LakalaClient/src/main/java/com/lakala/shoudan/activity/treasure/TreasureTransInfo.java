package com.lakala.shoudan.activity.treasure;

import android.text.TextUtils;

import com.lakala.library.util.DateUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/12/28.
 */
public class TreasureTransInfo extends BaseTransInfo {
    private String amount;
    private String productName;
    private String billId;
    private String lakalaOrderId;
    private String params;
    private String notifyURL;
    private String callbackUrl;
    private String jnlId;

    public String getJnlId() {
        return jnlId;
    }

    public TreasureTransInfo setJnlId(String jnlId) {
        this.jnlId = jnlId;
        return this;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public TreasureTransInfo setCallbackUrl(String url,String params) {
        this.callbackUrl = new StringBuilder().append(url)
                .append("/").append(params).toString();
        return this;
    }

    public String getNotifyURL() {
        return notifyURL;
    }

    public TreasureTransInfo setNotifyURL(String notifyURL) {
        this.notifyURL = notifyURL;
        return this;
    }

    public String getParams() {
        return params;
    }

    public TreasureTransInfo setParams(String params) {
        this.params = params;
        return this;
    }

    public String getLakalaOrderId() {
        return lakalaOrderId;
    }

    public TreasureTransInfo setLakalaOrderId(String lakalaOrderId) {
        this.lakalaOrderId = lakalaOrderId;
        return this;
    }

    public String getBillId() {
        return billId;
    }

    public TreasureTransInfo setBillId(String billId) {
        this.billId = billId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public TreasureTransInfo setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public TreasureTransInfo setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    @Override
    public TransactionType getType() {
        return TransactionType.TREASURE;
    }

    @Override
    public String getAdditionalMsg() {
        //TODO 待确认
//        return "收款商户号:" + ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo();
        return "";
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
        return "一块夺宝";
    }

    @Override
    public String getRepayName() {
        return "一块夺宝";
    }

    @Override
    public String getTransTypeName() {
        return "一块夺宝";
    }

    @Override
    public String getSwipeAmount() {
        return amount;
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        List<TransferInfoEntity> list = new ArrayList<>();
        list.add(new TransferInfoEntity("交易状态",transResult == TransResult.SUCCESS ? "付款成功" :
                "结果未知"));
        list.add(new TransferInfoEntity("交易时间", DateUtil.formateDateTransTime(syTm)));
        if(!TextUtils.isEmpty(jnlId)){
            list.add(new TransferInfoEntity("交易流水号",String.valueOf(jnlId)));
        }
        list.add(new TransferInfoEntity("交易金额", StringUtil.formatDisplayAmount(amount),true));
        list.add(new TransferInfoEntity("购买产品",productName));
        return list;
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        List<TransferInfoEntity> list = new ArrayList<>();
        list.add(new TransferInfoEntity("产品名称",productName));
        list.add(new TransferInfoEntity("支付金额", StringUtil.formatDisplayAmount(amount), true));
        return list;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {
        return getResultInfo();
    }

    @Override
    public String getStatisticTransResult() {
        return ShoudanStatisticManager.Purchase_Product_Success_Treasure_Buy;
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
