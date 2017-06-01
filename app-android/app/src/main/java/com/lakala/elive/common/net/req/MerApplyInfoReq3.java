package com.lakala.elive.common.net.req;

import com.lakala.elive.beans.MerOpenInfo;
import com.lakala.elive.beans.TerminalInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 进件接口 营业执照信息
 * Created by wenhaogu on 2017/1/12.
 */

public class MerApplyInfoReq3 implements Serializable {
    private String authToken;
    private String submitInfoType;
    private MerOpenInfo merOpenInfo;
    private TerminalInfo terminalInfo;
    private List<CardAppRateInfo> cardAppRateInfoList;

    public String getSubmitInfoType() {
        return submitInfoType;
    }

    public void setSubmitInfoType(String submitInfoType) {
        this.submitInfoType = submitInfoType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public MerOpenInfo getMerOpenInfo() {
        return merOpenInfo;
    }

    public void setMerOpenInfo(MerOpenInfo merOpenInfo) {
        this.merOpenInfo = merOpenInfo;
    }

    public TerminalInfo getTerminalInfo() {
        return terminalInfo;
    }

    public void setTerminalInfo(TerminalInfo terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    public List<CardAppRateInfo> getCardAppRateInfoList() {
        return cardAppRateInfoList;
    }

    public void setCardAppRateInfoList(List<CardAppRateInfo> cardAppRateInfoList) {
        this.cardAppRateInfoList = cardAppRateInfoList;
    }


    /**
     * 进件--卡应用开通扣率设置列表
     */
    public static class CardAppRateInfo implements Serializable {
        private String applyId;
        private String terminalId;
        private String cardType;
        private String baseFeeRate;
        private String baseFeeMax;
        private String hasTransAmt;
        private String perTransLimit;
        private String dayTransLimit;
        private String monthTransLimit;


        public CardAppRateInfo() {
        }

        public CardAppRateInfo(String cardType, String baseFeeRate, String baseFeeMax) {
            this.cardType = cardType;
            this.baseFeeRate = baseFeeRate;
            this.baseFeeMax = baseFeeMax;
        }

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public String getBaseFeeRate() {
            return baseFeeRate;
        }

        public void setBaseFeeRate(String baseFeeRate) {
            this.baseFeeRate = baseFeeRate;
        }

        public String getBaseFeeMax() {
            return baseFeeMax;
        }

        public void setBaseFeeMax(String baseFeeMax) {
            this.baseFeeMax = baseFeeMax;
        }

        public String getHasTransAmt() {
            return hasTransAmt;
        }

        public void setHasTransAmt(String hasTransAmt) {
            this.hasTransAmt = hasTransAmt;
        }

        public String getPerTransLimit() {
            return perTransLimit;
        }

        public void setPerTransLimit(String perTransLimit) {
            this.perTransLimit = perTransLimit;
        }

        public String getDayTransLimit() {
            return dayTransLimit;
        }

        public void setDayTransLimit(String dayTransLimit) {
            this.dayTransLimit = dayTransLimit;
        }

        public String getMonthTransLimit() {
            return monthTransLimit;
        }

        public void setMonthTransLimit(String monthTransLimit) {
            this.monthTransLimit = monthTransLimit;
        }
    }
}
