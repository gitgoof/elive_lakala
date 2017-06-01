package com.lakala.elive.beans;

import java.util.List;

/**
 * Created by zhouzx on 2017/2/28.
 */
public class BankCardVerificationResp {

    public BankCardInfo getContent() {
        return content;
    }

    public void setContent(BankCardInfo content) {
        this.content = content;
    }

    private BankCardInfo content;


    private String resultCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;


    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public class BankCardInfo {

        /**
         * success : true
         * code : SUCCESS
         * message : 成功
         * cardno : 6226091210851618
         * cardbin : 622609
         * openningBank : 03080000
         * clearingBank : 03080000
         * bankName : 招商银行
         * type : D
         */

        private boolean success;
        private String code;
        private String message;
        private String cardno;
        private String cardbin;
        private String openningBank;
        private String clearingBank;
        private String bankName;
        private String type;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCardno() {
            return cardno;
        }

        public void setCardno(String cardno) {
            this.cardno = cardno;
        }

        public String getCardbin() {
            return cardbin;
        }

        public void setCardbin(String cardbin) {
            this.cardbin = cardbin;
        }

        public String getOpenningBank() {
            return openningBank;
        }

        public void setOpenningBank(String openningBank) {
            this.openningBank = openningBank;
        }

        public String getClearingBank() {
            return clearingBank;
        }

        public void setClearingBank(String clearingBank) {
            this.clearingBank = clearingBank;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
