package com.lakala.shoudan.activity.shoudan.merchantpayment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.common.util.StringUtil;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.payment.base.TransferInfoEntity;
import com.lakala.shoudan.activity.payment.base.BaseTransInfo;

import org.json.JSONObject;

public class MerchantPayTransInfo extends BaseTransInfo implements Serializable {

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
    public void unpackValidateResult(JSONObject jsonObject) {

    }

    @Override
    public String getStatisticTransResult() {
        return ShoudanStatisticManager.Contribute_Payment_Amount_Input;
    }

    @Override
    public String getStatisticSignPage() {
        return null;
    }

    @Override
    public String getStatisticIsSend() {
        return null;
    }

    private String amount;// 缴款金额

	private String organizationName;//所属机构
	private String merchantNum;//商户编号

    private String billNo;

    private String querySid;

    private String instBill;

    private String price;

    private String fee;

    private String feeSid;

    private String mercname;

    private String address;

    private boolean isBind;

    public boolean isBind() {
        return isBind;
    }


    public void setBind(String isBind) {
        if("0".equals(isBind)){
            this.isBind = false;
        }else{
            this.isBind = true;
        }

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMercname() {
        return mercname;
    }

    public void setMercname(String mercname) {
        this.mercname = mercname;
    }

    public String getFeeSid() {
        return feeSid;
    }

    public void setFeeSid(String feeSid) {
        this.feeSid = feeSid;
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

    public String getInstBill() {
        return instBill;
    }

    public void setInstBill(String instBill) {
        this.instBill = instBill;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getQuerySid() {
        return querySid;
    }

    public void setQuerySid(String querySid) {
        this.querySid = querySid;
    }

    public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getMerchantNum() {
		return merchantNum;
	}

	public void setMerchantNum(String merchantNum) {
		this.merchantNum = merchantNum;
	}

	@Override
	public String getTransTypeName() {
		return "特约商户缴费";
	}

    @Override
    public String getRepayName() {
        return "缴费";
    }

    @Override
	public String getSwipeAmount() {
		return price;
	}

	@Override
	public List<TransferInfoEntity> getResultInfo() {
        List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();

        transferInfoEntities.add(new TransferInfoEntity("商户编号", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));
        transferInfoEntities.add(new TransferInfoEntity("交易时间", getSyTm()));
        transferInfoEntities.add(new TransferInfoEntity("付款卡号",StringUtil.formatCardNumberN6S4N4(getPayCardNo())));

        transferInfoEntities.add(new TransferInfoEntity("缴款金额", Util.formatDisplayAmount(amount),true));
        transferInfoEntities.add(new TransferInfoEntity("手续费", Util.formatDisplayAmount(fee), true));
        transferInfoEntities.add(new TransferInfoEntity("刷卡金额", Util.formatDisplayAmount(price),true));

        return transferInfoEntities;
	}

	@Override
	public List<TransferInfoEntity> getConfirmInfo() {
		List<TransferInfoEntity> transferInfoEntities = new ArrayList<TransferInfoEntity>();
		transferInfoEntities.add(new TransferInfoEntity("缴费金额", Util.formatDisplayAmount(amount),true));
		transferInfoEntities.add(new TransferInfoEntity("手续费", Util.formatDisplayAmount(fee), true));
        transferInfoEntities.add(new TransferInfoEntity("商户名称",mercname));
        //transferInfoEntities.add(new TransferInfoEntity("负责人", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getRealName()));
        transferInfoEntities.add(new TransferInfoEntity("商户地址",address));
        transferInfoEntities.add(new TransferInfoEntity("网点编号", instBill));

		return transferInfoEntities;
	}

	@Override
	public List<TransferInfoEntity> getBillInfo() {
		return getConfirmInfo();
	}

    @Override
    public TransactionType getType() {
        return TransactionType.ContributePayment;
    }


    @Override
    public String getAdditionalMsg() {
        return "商户编号:" + instBill;
    }
}
