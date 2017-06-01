package com.lakala.shoudan.activity.shoudan.loan.datafine;

import android.text.TextUtils;

import com.lakala.platform.statistic.LargeAmountEnum;
import com.lakala.platform.statistic.PayForYouEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 *
 * 还借款 bean
 * Created by More on 14/11/26.
 */
public class LoanRepayTransInfo extends BaseTransInfo {

    private String amountStill;//待还款金额

    private String contractNo;//合同号

    private String amount;

    private String time;

    private String cardNo;

    public String getTime() {
        return time;
    }

    public LoanRepayTransInfo setTime(String time) {
        this.time = time;
        return this;
    }

    public String getCardNo() {
        return cardNo;
    }

    public LoanRepayTransInfo setCardNo(String cardNo) {
        this.cardNo = cardNo;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public LoanRepayTransInfo setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getAmountStill() {
        return amountStill;
    }

    public void setAmountStill(String amountStill) {
        this.amountStill = amountStill;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
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
        return "还款";
    }

    @Override
    public String getTransTypeName() {
        return "替你还";
    }

    @Override
    public String getSwipeAmount() {
        return getAmount();
    }

    @Override
    public List<TransferInfoEntity> getResultInfo() {


        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("交易流水号:", getSysRef()== null ? "" : getSysRef()));
        transferInfoEntities.add(new TransferInfoEntity("合同号:", contractNo));
        transferInfoEntities.add(new TransferInfoEntity("付款借记卡",StringUtil.formatCardNumberN6S4N4(getCardNo())));
        transferInfoEntities.add(new TransferInfoEntity("交易时间:", Util.formateDateTransTime(getTime())));
        transferInfoEntities.add(new TransferInfoEntity("还款金额:", Util.formatDisplayAmount(getAmount()),true));
//        transferInfoEntities.add(new TransferInfoEntity("手续费:", Util.formatDisplayAmount("0"),true));
        transferInfoEntities.add(new TransferInfoEntity("刷卡金额:", Util.formatDisplayAmount(getAmount()),true));
        return transferInfoEntities;
    }

    @Override
    public List<TransferInfoEntity> getConfirmInfo() {

        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
        transferInfoEntities.add(new TransferInfoEntity("合同编号:", contractNo));
        transferInfoEntities.add(new TransferInfoEntity("待还金额:", Util.formatDisplayAmount(amountStill)));
        transferInfoEntities.add(new TransferInfoEntity("还款金额:", Util.formatDisplayAmount(getAmount())  , true));

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
        if (!TextUtils.isEmpty(LargeAmountEnum.LargeAmount.getAdvertId())) {//广告id不为null，从广告进入
            event = String.format(ShoudanStatisticManager.Advert_Pay_Yor_You_Success, PayForYouEnum.PayForYou.getAdvertId());
        }else{
            event = ShoudanStatisticManager.Pay_Yor_You_Success_Homepage;
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

    public void unpackTransResult(JSONObject jb) {
    	super.optBaseData(jb.toString());
    	try{
    		setTime(jb.optString("transtime",""));//交易时间
    		setCardNo(jb.optString("pan",""));//还款卡号
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    @Override
    public TransactionType getType() {
        return TransactionType.RepayForYou;
    }

    //TODO
    @Override
    public String getAdditionalMsg() {
        return null;
    }
}
