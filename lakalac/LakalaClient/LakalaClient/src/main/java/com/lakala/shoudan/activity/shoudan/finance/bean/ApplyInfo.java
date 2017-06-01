package com.lakala.shoudan.activity.shoudan.finance.bean;

import android.text.TextUtils;

import com.lakala.platform.statistic.FinancePurchanceEnum;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;
import com.lakala.shoudan.activity.payment.base.TransResult;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.common.util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMQ on 2015/10/22.
 */
public class ApplyInfo extends BaseTransInfo {
    private String JnlId;
    private String productName;
    private String productId;
    private String Billid;
    private String lastPaymentType;
    private String period;
    private int outMode;
    private String amount;//交易金额
    private String time;//交易时间

    public String getAmount() {
        return amount;
    }

    public String getTime() {
        return time;
    }

    public ApplyInfo setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public ApplyInfo setTime(String time) {
        this.time = time;
        return this;
    }

    public int getOutMode() {
        return outMode;
    }

    public ApplyInfo setOutMode(int outMode) {
        this.outMode = outMode;
        return this;
    }

    public String getJnlId() {
        return JnlId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductId() {
        return productId;
    }

    public ApplyInfo setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getBillid() {
        return Billid;
    }

    public ApplyInfo setBillid(String billid) {
        Billid = billid;
        return this;
    }

    public String getLastPaymentType() {
        return lastPaymentType;
    }

    public ApplyInfo setLastPaymentType(String lastPaymentType) {
        this.lastPaymentType = lastPaymentType;
        return this;
    }

    public String getPeriod() {
        return period;
    }

    public ApplyInfo setPeriod(String period) {
        this.period = period;
        return this;
    }

    public ApplyInfo setJnlId(String jnlId) {
        JnlId = jnlId;
        return this;
    }

    public ApplyInfo setProductName(String productName) {
        this.productName = productName;
        return this;
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
        return null;
    }

    @Override
    public String getRepayName() {
        return "申购";
    }

    @Override
    public String getTransTypeName() {
        return "购买";
    }

    @Override
    public String getSwipeAmount() {
        return getAmount();
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        List<TransferInfoEntity> resultInfo = new ArrayList<TransferInfoEntity>();
        if(!TextUtils.isEmpty(JnlId)){
            resultInfo.add(new TransferInfoEntity("交易流水号", JnlId));
        }
        resultInfo.add(new TransferInfoEntity("交易时间", getTime()));
        resultInfo.add(
                new TransferInfoEntity(
                        "交易状态", getTransResult() == TransResult.SUCCESS ? "购买成功" : "购买失败"
                )
        );
        resultInfo.add(new TransferInfoEntity("交易金额", Util.formatDisplayAmount(getAmount()), true));
        resultInfo.add(new TransferInfoEntity("购买产品", productName));
        return resultInfo;
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {
        List<TransferInfoEntity> confirmInfo = new ArrayList<TransferInfoEntity>();
        confirmInfo.add(new TransferInfoEntity("理财产品",productName));
        confirmInfo.add(new TransferInfoEntity("支付金额",Util.formatDisplayAmount(getAmount()),true));
        return confirmInfo;
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
        String event="";
        if (!TextUtils.isEmpty(FinancePurchanceEnum.FinancePurchance.getAdvertId())) {

            event = String.format(ShoudanStatisticManager.Advert_Finance_Purchance, FinancePurchanceEnum.FinancePurchance.getAdvertId());
        }else{
            String[] loan_Top = {"首页公共业务","定向业务","广告"};
            if (PublicEnum.Business.isPublic()){
                event=loan_Top[0];
            }else  if (PublicEnum.Business.isDirection()){
                event=loan_Top[1];
            }else  if (PublicEnum.Business.isAd()){
                event=loan_Top[2];
            }
            event=event+ShoudanStatisticManager.Finance_Purchance_Success;
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
        return TransactionType.APPLY;
    }

    //TODO wait for lixiangyu
    @Override
    public String getAdditionalMsg() {
        return "";
    }
}
