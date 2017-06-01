package com.lakala.shoudan.activity.shoudan.rentcollection;

import android.util.Log;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 14-6-26.
 */
public class RentCollectionInfo extends BaseTransInfo {

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
        return "特约商户缴费";
    }

    @Override
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
        return ShoudanStatisticManager.Rent_Collection_Amount;
    }

    @Override
    public String getStatisticSignPage() {
        return null;
    }

    @Override
    public String getStatisticIsSend() {
        return null;
    }

    private String tips;

    private String fee;
    private String price;
    private String srcSid;

    private String inpan;

    private String feeFlag;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getInpan() {
        return inpan;
    }

    public void setInpan(String inpan) {
        this.inpan = inpan;
    }

    public String getFeeFlag() {
        return feeFlag;
    }

    public void setFeeFlag(String feeFlag) {
        this.feeFlag = feeFlag;
    }

    public String getSrcSid() {
        return srcSid;
    }

    public void setSrcSid(String srcSid) {
        this.srcSid = srcSid;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String getRepayName() {
        return "收银";
    }

    @Override
    public String getTransTypeName() {
        return "收银宝";
    }

    @Override
    public String getSwipeAmount() {
        return price;
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {
        return getBillInfo();
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {

        List<TransferInfoEntity> entities = new ArrayList<TransferInfoEntity>();
        entities.add(new TransferInfoEntity("收款金额:", Util.formatDisplayAmount(getSwipeAmount()), true));
        //entities.add(new TransferInfoEntity("手续费:", Util.formatDisplayAmount(fee), true));
        //entities.add(new TransferInfoEntity("刷卡金额:", Util.formatDisplayAmount(price), true));
        entities.add(new TransferInfoEntity("结算账户:", StringUtil.formatCardNumberN6S4N4(inpan)));;
        //entities.add(new TransferInfoEntity("账户姓名", Parameters.merchantInfo.getAccountName()));

        return entities;
    }

    @Override
    public List<TransferInfoEntity> getBillInfo() {

        List<TransferInfoEntity> infoEntities = new ArrayList<TransferInfoEntity>();
        infoEntities.add(new TransferInfoEntity("付款卡:", StringUtil.formatCardNumberN6S4N4(payCardNo)));
        //infoEntities.add(new TransferInfoEntity("手续费:", Util.formatDisplayAmount(fee)));
        infoEntities.add(new TransferInfoEntity("交易金额:", Util.formatDisplayAmount(price),true));
        //infoEntities.add(new TransferInfoEntity("交易状态:", getResultCode() == Constants.ResultCode.SUCCESS ? "收款成功" : "结果未知"));
        infoEntities.add(new TransferInfoEntity("交易时间:", Util.getNowTime()));
        if(!"".equals(getTips())){
            infoEntities.add(new TransferInfoEntity("备注:", getTips()));
        }

        return infoEntities;
    }


    public void unpackQueryResult(JSONObject jsonObject){
        try {

            feeFlag = jsonObject.getString("feeflag");

            srcSid = jsonObject.getString("sid");
            price = Util.fen2Yuan(jsonObject.getString("price"));
            fee = Util.fen2Yuan(jsonObject.getString("fee"));
            //inpan = jsonObject.getString("inpan");

        }catch (Exception e ){
            if(Parameters.debug){
                LogUtil.e(getClass().getName(), "Unpack Query Rent Result error", e);
            }
        }


    }

    @Override
    public TransactionType getType() {
        return TransactionType.RentCollection;
    }

    @Override
    public String getAdditionalMsg() {
        return ("结算账户:" + (inpan));
    }
}
