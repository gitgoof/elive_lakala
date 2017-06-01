package com.lakala.elive.common.net.resp;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wenhaogu on 2017/1/22.
 */

public class MyMerchantsListResp implements Serializable {

    private String commandId;
    private ContentBean content;
    private String resultCode;
    private int resultDataType;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public int getResultDataType() {
        return resultDataType;
    }

    public void setResultDataType(int resultDataType) {
        this.resultDataType = resultDataType;
    }

    public static class ContentBean {

        private int firstPage;
        private int lastPage;
        private int nextIndex;
        private int nextPage;
        private int pageNo;
        private int pageSize;
        private int previousIndex;
        private int priorPage;
        private int recordCount;
        private int startIndex;
        private int totalPage;
        private List<RowsBean> rows;

        public int getFirstPage() {
            return firstPage;
        }

        public void setFirstPage(int firstPage) {
            this.firstPage = firstPage;
        }

        public int getLastPage() {
            return lastPage;
        }

        public void setLastPage(int lastPage) {
            this.lastPage = lastPage;
        }

        public int getNextIndex() {
            return nextIndex;
        }

        public void setNextIndex(int nextIndex) {
            this.nextIndex = nextIndex;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPreviousIndex() {
            return previousIndex;
        }

        public void setPreviousIndex(int previousIndex) {
            this.previousIndex = previousIndex;
        }

        public int getPriorPage() {
            return priorPage;
        }

        public void setPriorPage(int priorPage) {
            this.priorPage = priorPage;
        }

        public int getRecordCount() {
            return recordCount;
        }

        public void setRecordCount(int recordCount) {
            this.recordCount = recordCount;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<RowsBean> getRows() {
            return rows;
        }

        public void setRows(List<RowsBean> rows) {
            this.rows = rows;
        }

        public static class RowsBean {


            private String accountKind;
            private String address;
            private String applyChannel;
            private String applyChannelName;
            private String applyId;
            private long applyTime;
            private String applyTimeStr;
            private String applyType;
            private String applyTypeName;
            private String createBy;
            private long createTime;
            private String createTimeStr;
            private String deviceDrawMethod;
            private String merchantName;
            private String mobile;
            private String process;
            private String resultId;
            private String status;
            private String statusName;
            private String updateBy;
            private long updateTime;

            public String getAccountKind() {
                return accountKind;
            }

            public void setAccountKind(String accountKind) {
                this.accountKind = accountKind;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getApplyChannel() {
                return applyChannel;
            }

            public void setApplyChannel(String applyChannel) {
                this.applyChannel = applyChannel;
            }

            public String getApplyChannelName() {
                return applyChannelName;
            }

            public void setApplyChannelName(String applyChannelName) {
                this.applyChannelName = applyChannelName;
            }

            public String getApplyId() {
                return applyId;
            }

            public void setApplyId(String applyId) {
                this.applyId = applyId;
            }

            public long getApplyTime() {
                return applyTime;
            }

            public void setApplyTime(long applyTime) {
                this.applyTime = applyTime;
            }

            public String getApplyTimeStr() {
                return applyTimeStr;
            }

            public void setApplyTimeStr(String applyTimeStr) {
                this.applyTimeStr = applyTimeStr;
            }

            public String getApplyType() {
                return applyType;
            }

            public void setApplyType(String applyType) {
                this.applyType = applyType;
            }

            public String getApplyTypeName() {
                return applyTypeName;
            }

            public void setApplyTypeName(String applyTypeName) {
                this.applyTypeName = applyTypeName;
            }

            public String getCreateBy() {
                return createBy;
            }

            public void setCreateBy(String createBy) {
                this.createBy = createBy;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public String getCreateTimeStr() {
                return createTimeStr;
            }

            public void setCreateTimeStr(String createTimeStr) {
                this.createTimeStr = createTimeStr;
            }

            public String getDeviceDrawMethod() {
                return deviceDrawMethod;
            }

            public void setDeviceDrawMethod(String deviceDrawMethod) {
                this.deviceDrawMethod = deviceDrawMethod;
            }

            public String getMerchantName() {
                return merchantName;
            }

            public void setMerchantName(String merchantName) {
                this.merchantName = merchantName;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getProcess() {
                return process;
            }

            public void setProcess(String process) {
                this.process = process;
            }

            public String getResultId() {
                return resultId;
            }

            public void setResultId(String resultId) {
                this.resultId = resultId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getStatusName() {
                return statusName;
            }

            public void setStatusName(String statusName) {
                this.statusName = statusName;
            }

            public String getUpdateBy() {
                return updateBy;
            }

            public void setUpdateBy(String updateBy) {
                this.updateBy = updateBy;
            }

            public long getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(long updateTime) {
                this.updateTime = updateTime;
            }
        }
    }
}
