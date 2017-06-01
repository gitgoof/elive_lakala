package com.lakala.elive.preenterpiece.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ousachisan on 2017/3/23.
 * <p>
 * 合作方预进件的列表也查询接口的Response(ELIVE_PARTNER_APPLY_001)
 */

public class PreEnPieceListResponse implements Serializable {

    private String message;
    private String resultCode;
    private ContentBean content;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean implements Serializable {

        public int total;
        public int pageNo;
        public int nextPage;
        public int pageSize;
        public int totalPage;
        public List<PartnerApplyInfo> rows;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageNo() {
            return pageNo;
        }

        public void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        public Integer getNextPage() {
            return nextPage;
        }

        public void setNextPage(Integer nextPage) {
            this.nextPage = nextPage;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public List<PartnerApplyInfo> getRows() {
            return rows;
        }

        public void setRows(List<PartnerApplyInfo> rows) {
            this.rows = rows;
        }

        public static class PartnerApplyInfo implements Serializable {

            //  申请ID
            public String applyId;//编辑修改工单情况下，该值必填 （后台根据此值判断新增和修改）

            public String merAddr;//地址

            public String mobile;

            public String createTimeStr;

            public String getCreateTimeStr() {
                return createTimeStr;
            }

            public void setCreateTimeStr(String createTimeStr) {
                this.createTimeStr = createTimeStr;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getMerAddr() {
                return merAddr;
            }

            public void setMerAddr(String merAddr) {
                this.merAddr = merAddr;
            }

            public String getApplyId() {
                return applyId;
            }

            public void setApplyId(String applyId) {
                this.applyId = applyId;
            }

            public String getApplyType() {
                return applyType;
            }

            public void setApplyType(String applyType) {
                this.applyType = applyType;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getMerchantName() {
                return merchantName;
            }

            public void setMerchantName(String merchantName) {
                this.merchantName = merchantName;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getContact() {
                return contact;
            }

            public void setContact(String contact) {
                this.contact = contact;
            }

            public String getMobileNo() {
                return mobileNo;
            }

            public void setMobileNo(String mobileNo) {
                this.mobileNo = mobileNo;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getCreateBy() {
                return createBy;
            }

            public void setCreateBy(String createBy) {
                this.createBy = createBy;
            }

            //  申请类型
            public String applyType;// 1.增终2.增应用3.增商4.增网5.信息变更6.撤机
            // 申请状态
            public String status;// 1：已提交、2：处理中、3：处理成功、4：处理失败
            //  商户名称
            public String merchantName;
            //  商户地址
            public String address;
            // 联系人
            public String contact;
            // 联系电话
            public String mobileNo;
            // 创建时间
            public String createTime;
            // 创建人
            public String createBy;
        }
    }

}
