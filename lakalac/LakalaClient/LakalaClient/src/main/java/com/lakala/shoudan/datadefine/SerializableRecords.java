package com.lakala.shoudan.datadefine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by More on 13-12-9.
 */
public class SerializableRecords implements Serializable {
    /**
     * 当前查询的页数
     */
    private int prePage = 0;
    /**
     * 总页数
     */
    private int totalPage = 0;

    /**
     * 原为pageResult中的总笔数,现改为收款交易的全部交易笔数
     */
    private int totalCount = 0;

    /**
     * 撤销总笔数
     */
    private int cancelCount = 0;

    /**
     * 收款总笔数
     */
    private int collectionCount = 0;

    /**
     * 单类型汇总
     */
    private List<TransTotal> transTotalList;


    /**
     * 已查询到的可撤销的交易记录
     */
    private List<RecordDetail> recordDetailList = new ArrayList<RecordDetail>();

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public void setRecordDetailList(List<RecordDetail> recordDetailList) {
        this.recordDetailList = recordDetailList;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<RecordDetail> getRecordDetailList() {
        return recordDetailList;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public int getTotalCount(){
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public static class TransTotal implements Serializable{

        private String dealTypeName;

        private String successCount;

        private String successAmount;

        private String dealTypeCode;

        public String getDealTypeCode() {
            return dealTypeCode;
        }

        public void setDealTypeCode(String dealTypeCode) {
            this.dealTypeCode = dealTypeCode;
        }

        public String getDealTypeName() {
            return dealTypeName;
        }

        public void setDealTypeName(String dealTypeName) {
            if("个人转账".equals(dealTypeName)){
                this.dealTypeName = "转账";
                return;
            }
            this.dealTypeName = dealTypeName;
        }

        public String getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(String successCount) {
            this.successCount = successCount;
        }

        public String getSuccessAmount() {
            return successAmount;
        }

        public void setSuccessAmount(String successAmount) {
            this.successAmount = successAmount;
        }
    }

    public List<TransTotal> getTransTotalList() {
        return transTotalList;
    }

    public void setTransTotalList(List<TransTotal> transTotalList) {
        this.transTotalList = transTotalList;
    }
}
