package com.lakala.shoudan.datadefine;

import java.io.Serializable;

/**
 * 单条收款记录详情
 *
 * @author More
 */
public class RecordDetail implements Serializable {

    /**
     * 终端号
     */
    private String pasm;
    /**
     * 付款卡号
     */
    private String paymentAccount;
    /**
     * 付款时间
     */
    private String dealDateTime;
    /**
     * 交易金额
     */
    private String dealAmount;
    /**
     * 交易类型
     */
    private String dealTypeName;
    /**
     * 交易类型码
     */
    private String dealTypeCode;
    /**
     * 交易流水
     */
    private String sysSeq;
    /**
     * 手续费
     */
    private String handlingCharge;
    /**
     * 付款手机号
     */
    private String paymentMobile;
    /**
     * 可否被撤销
     */
    private String isWithDraw;
    /**
     * 交易状态
     */
    private String status;
    /**
     * 撤销记录
     */
    private Object withDrawInfo;
    /**
     * Sid
     */
    private String sid;//流水号
    /**
     * 业务名
     */
    private String busname;//业务名

    private String authCode;

    private String merChantCode;

    private String posOGName;

    /**
     * 批次号
     */
    private String batchNo;

    private String voucherCode;

    private boolean needCancelPwd;

    private String merchantName;

    private String collectionAccount;

    private String series;

    /* *
    * 交易状态
    */
    private EPosStatus posStatus; //交易状态 X0 收款成功,X1 收款失败,Y0 撤销成功,Y1 撤销失败

    private boolean sign;

    public boolean isSign() {
        return sign;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getSeries() {
        return series;
    }

    /**
     * 消费备注
     */
    private String tips;

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public void setCollectionAccount(String collectionAccount) {
        this.collectionAccount = collectionAccount;
    }

    public String getCollectionAccount() {
        return collectionAccount;
    }


    public String getSid() {
        return sid;
    }

    /**
     * 设置 Sid
     *
     * @param sid
     */
    public void setSid(String sid) {
        this.sid = sid;
    }

    /**
     * 获取业务名
     *
     * @return
     */
    public String getBusname() {
        return busname;
    }

    /**
     * 设置业务名
     *
     * @param busname
     */
    public void setBusname(String busname) {
        this.busname = busname;
    }

    /**
     * 设置终端号
     *
     * @param pasm
     */
    public void setPasm(String pasm) {
        this.pasm = pasm;
    }

    /**
     * 获取终端号
     *
     * @return
     */
    public String getPasm() {
        return pasm;
    }

    /**
     * 付款帐号
     *
     * @param paymentAccount
     */
    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    /**
     * 获取付款卡号
     *
     * @return
     */
    public String getPaymentAccount() {
        return paymentAccount;
    }

    /**
     * 设置交易时间
     *
     * @param dealDateTime
     */
    public void setDealDateTime(String dealDateTime) {
        this.dealDateTime = dealDateTime;
    }

    /**
     * 获取交易时间
     *
     * @return
     */
    public String getDealDateTime() {
        return dealDateTime;
    }

    /**
     * 设置交易金额
     *
     * @param dealAmount
     */
    public void setDealAmount(String dealAmount) {
        this.dealAmount = dealAmount;
    }

    /**
     * 获取交易金额
     *
     * @return
     */
    public String getDealAmount() {
        return dealAmount;

    }

    /**
     * 交易类型名
     *
     * @param dealTypeName
     */
    public void setDealTypeName(String dealTypeName) {
        if ("个人转账".equals(dealTypeName)) {
            this.dealTypeName = "转账";
            return;
        }

        this.dealTypeName = dealTypeName;
    }

    /**
     * 交易类型名
     *
     * @return
     */
    public String getDealTypeName() {
        return dealTypeName;
    }

    /**
     * 交易类型码
     *
     * @param dealTypeCode
     */
    public void setDealTyepCode(String dealTypeCode) {
        this.dealTypeCode = dealTypeCode;
    }

    /**
     * 交易类型码
     *
     * @return
     */
    public String getDealTypeCode() {
        return dealTypeCode;
    }

    /**
     * 检索参考号
     *
     * @param sysSeq
     */
    public void setSysSeq(String sysSeq) {
        this.sysSeq = sysSeq;
    }

    /**
     * 检索参考号
     *
     * @return
     */
    public String getSysSeq() {
        return sysSeq;
    }

    /**
     * 手续费
     *
     * @param handlingCharge
     */
    public void setHandlingCharge(String handlingCharge) {
        this.handlingCharge = handlingCharge;
    }

    /**
     * 手续费
     *
     * @return
     */
    public String getHandlingCharge() {
        return handlingCharge;
    }

    /**
     * 付款手机号
     *
     * @param paymentMobile
     */
    public void setPaymentMobile(String paymentMobile) {
        this.paymentMobile = paymentMobile;
    }

    /**
     * 付款手机号
     *
     * @return
     */
    public String getPaymentMoblile() {
        return paymentMobile;
    }

    /**
     * 交易状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 交易状态
     * Success or Failure
     *
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * 是否可以被撤销
     *
     * @param isWithDraw
     */
    public void setIsWithDraw(String isWithDraw) {
        this.isWithDraw = isWithDraw;
    }

    /**
     * 是否可以被撤销
     *
     * @return
     */
    public String getIsWithDraw() {
        return isWithDraw;
    }

    /**
     * 交易类型
     *
     * @param dealTypeCode
     */
    public void setDealTypeCode(String dealTypeCode) {
        this.dealTypeCode = dealTypeCode;
    }

    /**
     * 交易记录
     *
     * @param withDrawInfo
     */
    public void setWithDrawInfo(Object withDrawInfo) {
        this.withDrawInfo = withDrawInfo;
    }

    /**
     * 授权码
     *
     * @param authCode
     */
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    /**
     * 收单机构
     * //X0 收款成功,X1 收款失败,Y0 撤销成功,Y1 撤销失败
     *
     * @param posStatus
     */
    public void setPosStatus(String posStatus) {
        if (posStatus.equals("X0")) {
            this.posStatus = EPosStatus.RECEIVE_SUCCESS;
        } else if (posStatus.equals("X1")) {
            this.posStatus = EPosStatus.RECEIVE_FAILURE;
        } else if (posStatus.equals("Y0")) {
            this.posStatus = EPosStatus.REVOCATION_SUCCESS;
        } else if (posStatus.equals("Y1")) {
            this.posStatus = EPosStatus.REVOCATION_FAILURE;
        }

    }

    /**
     * 商户名
     *
     * @param merChantCode
     */
    public void setMerChantCode(String merChantCode) {
        this.merChantCode = merChantCode;
    }

    /**
     * 收单机构
     *
     * @param posOGName
     */
    public void setPosOGName(String posOGName) {
        this.posOGName = posOGName;
    }

    /**
     * 批次号
     *
     * @param batchNo
     */
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    /**
     * 凭证号
     *
     * @param voucherCode
     */
    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    /**
     * //X0 收款成功,X1 收款失败,Y0 撤销成功,Y1 撤销失败
     *
     * @param needCancelPwd
     */
    public void setNeedCancelPwd(String needCancelPwd) {
        if (needCancelPwd.equals("true")) {
            this.needCancelPwd = true;
        } else {
            this.needCancelPwd = false;
        }
    }

    /**
     * 付款手机
     *
     * @return
     */
    public String getPaymentMobile() {
        return paymentMobile;
    }

    /**
     * 撤销记录
     *
     * @return
     */
    public Object getWithDrawInfo() {
        return withDrawInfo;
    }

    /**
     * 授权码
     *
     * @return
     */
    public String getAuthCode() {
        return authCode;
    }

    /**
     * 收款状态
     *
     * @return
     */
    public EPosStatus getPosStatus() {
        return posStatus;
    }

    /**
     * 获取商户号
     *
     * @return
     */
    public String getMerChantCode() {
        return merChantCode;
    }

    /**
     * 获取收单机构
     *
     * @return
     */
    public String getPosOGName() {
        return posOGName;
    }

    /**
     * 获取批次号
     *
     * @return
     */
    public String getBatchNo() {
        return batchNo;
    }

    /**
     * 获取凭证号
     */
    public String getVoucherCode() {
        return voucherCode;
    }

    /**
     * 是否需要输入 pin
     *
     * @return
     */
    public boolean isNeedCancelPwd() {
        return needCancelPwd;
    }

    /**
     * 设置商户名
     *
     * @param merchantName
     */
    public void setMechantName(String merchantName) {
        this.merchantName = merchantName;
    }

    /**
     * 商户名
     */
    public String getMerchantName() {
        return merchantName;
    }


//    public static RecordDetail parseObject(JSONObject jsonObject) {
//        RecordDetail recordDetail = new RecordDetail();
//        try {
//            recordDetail.setPasm(jsonObject.optString("pasm", ""));
//            recordDetail.setPaymentAccount(jsonObject.optString("paymentAccount", ""));
//            recordDetail.setDealDateTime(jsonObject.optString("dealDateTime", ""));
//            recordDetail.setDealAmount(jsonObject.optString("dealAmount", ""));
//            recordDetail.setDealTypeName(jsonObject.optString("dealTypeName", ""));
//            recordDetail.setDealTyepCode(jsonObject.optString("dealTypeCode", ""));
//            recordDetail.setSysSeq(jsonObject.optString("sysSeq", ""));
//            recordDetail.setPaymentMobile(jsonObject.optString("paymentMobile", ""));
//            recordDetail.setIsWithDraw(jsonObject.optString("isWithDraw", ""));
//            recordDetail.setStatus(jsonObject.optString("status", ""));
//            JSONObject json_withdrawinfo = jsonObject.getJSONObject("withDrawInfo");
//            WithdrawInfo info = new WithdrawInfo();
//            WithdrawInfo withdrawInfo = info.parseObject(json_withdrawinfo);
//            recordDetail.setWithDrawInfo(withdrawInfo);
//            recordDetail.setSid(jsonObject.optString("sid", ""));
//            recordDetail.setBusname(jsonObject.optString("busname", ""));
//            recordDetail.setAuthCode(jsonObject.optString("authCode", ""));
//            recordDetail.setMerChantCode(jsonObject.optString("merChantCode", ""));
//            recordDetail.setPosOGName(jsonObject.optString("posOGName", ""));
//            recordDetail.setBatchNo(jsonObject.optString("batchNo", ""));
//            recordDetail.setNeedCancelPwd(jsonObject.optString("needCancelPwd", ""));
//            recordDetail.setMechantName(jsonObject.optString("merchantName", ""));
//            recordDetail.setCollectionAccount(jsonObject.optString("collectionAccount", ""));
//            recordDetail.setSysSeq(jsonObject.optString("sysRef", ""));
//            recordDetail.setPosStatus(jsonObject.optString("posStatus", ""));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return recordDetail;
//    }

    @Override
    public String toString() {
        return "RecordDetail{" +
                "pasm='" + pasm + '\'' +
                ", paymentAccount='" + paymentAccount + '\'' +
                ", dealDateTime='" + dealDateTime + '\'' +
                ", dealAmount='" + dealAmount + '\'' +
                ", dealTypeName='" + dealTypeName + '\'' +
                ", dealTypeCode='" + dealTypeCode + '\'' +
                ", sysSeq='" + sysSeq + '\'' +
                ", handlingCharge='" + handlingCharge + '\'' +
                ", paymentMobile='" + paymentMobile + '\'' +
                ", isWithDraw='" + isWithDraw + '\'' +
                ", status='" + status + '\'' +
                ", withDrawInfo=" + withDrawInfo +
                ", sid='" + sid + '\'' +
                ", busname='" + busname + '\'' +
                ", authCode='" + authCode + '\'' +
                ", merChantCode='" + merChantCode + '\'' +
                ", posOGName='" + posOGName + '\'' +
                ", batchNo='" + batchNo + '\'' +
                ", voucherCode='" + voucherCode + '\'' +
                ", needCancelPwd=" + needCancelPwd +
                ", merchantName='" + merchantName + '\'' +
                ", collectionAccount='" + collectionAccount + '\'' +
                ", sysRef='" + series + '\'' +
                ", posStatus=" + posStatus +
                '}';
    }

    /**
     * 订单状态
     */
    public enum EPosStatus {
        /**
         * 收款成功
         */
        RECEIVE_SUCCESS,
        /**
         * 收款失败
         */
        RECEIVE_FAILURE,
        /**
         * 撤销成功
         */
        REVOCATION_SUCCESS,
        /**
         * 撤销失败
         */
        REVOCATION_FAILURE;
    }

}
