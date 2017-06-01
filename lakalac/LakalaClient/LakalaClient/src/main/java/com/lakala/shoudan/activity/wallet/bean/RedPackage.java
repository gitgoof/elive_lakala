package com.lakala.shoudan.activity.wallet.bean;

import java.util.List;

/**
 * Created by fengx on 2015/12/1.
 */
public class RedPackage {

    private String GiftNum;
    private String PageSize;
    private List<GiftListEntity> GiftList;

    public void setGiftNum(String GiftNum) {
        this.GiftNum = GiftNum;
    }

    public void setPageSize(String PageSize) {
        this.PageSize = PageSize;
    }

    public void setGiftList(List<GiftListEntity> GiftList) {
        this.GiftList = GiftList;
    }

    public String getGiftNum() {
        return GiftNum;
    }

    public String getPageSize() {
        return PageSize;
    }

    public List<GiftListEntity> getGiftList() {
        return GiftList;
    }

    public static class GiftListEntity {
        private String GiftValue;
        private String GiftNum;
        private String MergeFlag;
        private String GiftEndDate;
        private String GiftStartDate;
        private String GiftName;
        private String GiftState;
        private String GiftSubAcNo;
        private String Remark;
        private String GiftBalance;
        /**
         * GiftRangeCode : 0100
         * GiftRangeName : 彩票通用红包
         */

        private List<RangeListEntity> RangeList;

        public void setGiftValue(String GiftValue) {
            this.GiftValue = GiftValue;
        }

        public void setGiftNum(String GiftNum) {
            this.GiftNum = GiftNum;
        }

        public void setMergeFlag(String MergeFlag) {
            this.MergeFlag = MergeFlag;
        }

        public void setGiftEndDate(String GiftEndDate) {
            this.GiftEndDate = GiftEndDate;
        }

        public void setGiftStartDate(String GiftStartDate) {
            this.GiftStartDate = GiftStartDate;
        }

        public void setGiftName(String GiftName) {
            this.GiftName = GiftName;
        }

        public void setGiftState(String GiftState) {
            this.GiftState = GiftState;
        }

        public void setGiftSubAcNo(String GiftSubAcNo) {
            this.GiftSubAcNo = GiftSubAcNo;
        }

        public void setRemark(String Remark) {
            this.Remark = Remark;
        }

        public void setGiftBalance(String GiftBalance) {
            this.GiftBalance = GiftBalance;
        }

        public void setRangeList(List<RangeListEntity> RangeList) {
            this.RangeList = RangeList;
        }

        public String getGiftValue() {
            return GiftValue;
        }

        public String getGiftNum() {
            return GiftNum;
        }

        public String getMergeFlag() {
            return MergeFlag;
        }

        public String getGiftEndDate() {
            return GiftEndDate;
        }

        public String getGiftStartDate() {
            return GiftStartDate;
        }

        public String getGiftName() {
            return GiftName;
        }

        public String getGiftState() {
            return GiftState;
        }

        public String getGiftSubAcNo() {
            return GiftSubAcNo;
        }

        public String getRemark() {
            return Remark;
        }

        public String getGiftBalance() {
            return GiftBalance;
        }

        public List<RangeListEntity> getRangeList() {
            return RangeList;
        }

        public static class RangeListEntity {
            private String GiftRangeCode;
            private String GiftRangeName;

            public void setGiftRangeCode(String GiftRangeCode) {
                this.GiftRangeCode = GiftRangeCode;
            }

            public void setGiftRangeName(String GiftRangeName) {
                this.GiftRangeName = GiftRangeName;
            }

            public String getGiftRangeCode() {
                return GiftRangeCode;
            }

            public String getGiftRangeName() {
                return GiftRangeName;
            }
        }
    }
}
