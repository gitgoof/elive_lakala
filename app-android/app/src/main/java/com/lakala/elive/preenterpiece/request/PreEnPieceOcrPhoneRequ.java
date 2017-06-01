package com.lakala.elive.preenterpiece.request;

import com.lakala.elive.beans.MerAttachFile;

import java.io.Serializable;

/**
 * Created by ousachisan on 2017/3/23.
 * //合作方预进件的列表也查询接口的Request(ELIVE_PARTNER_APPLY_001)
 */
public class PreEnPieceOcrPhoneRequ implements Serializable {

    //  授权令牌
    public String authToken;
    //  预进件申请信息
    public PartnerApplyInfo merApplyInfo;
    // 验证账户相关附件
    public MerAttachFile merAttachFile;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public PartnerApplyInfo getMerApplyInfo() {
        return merApplyInfo;
    }

    public void setMerApplyInfo(PartnerApplyInfo merApplyInfo) {
        this.merApplyInfo = merApplyInfo;
    }

    public MerAttachFile getMerAttachFile() {
        return merAttachFile;
    }

    public void setMerAttachFile(MerAttachFile merAttachFile) {
        this.merAttachFile = merAttachFile;
    }

    public static class PartnerApplyInfo implements Serializable {
        public String applyId; //申请ID
        public String applyType;//申请类型
        public String applyChannel;// 申请渠道
        public String process;// 当前步骤

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

        public String getApplyChannel() {
            return applyChannel;
        }

        public void setApplyChannel(String applyChannel) {
            this.applyChannel = applyChannel;
        }

        public String getProcess() {
            return process;
        }

        public void setProcess(String process) {
            this.process = process;
        }
    }

//    public static class MerAttachFile {
//
//        // 申请ID
//        public String applyId;
//        // 商户编号
//        public String merchantId;
//        //  文件名称
//        public String fileName;
//        //  文件小类
//        public String fileType;
//        //  文件内容
//        public String fileContent;
//        //  是否要OCR
//        public String isOcr;
//        //  OCR类型
//        public String ocrType;
//        //  文件大类
//        public String segment;
//        //  文件序号
//        public String comments;
//
//        public String getApplyId() {
//            return applyId;
//        }
//
//        public void setApplyId(String applyId) {
//            this.applyId = applyId;
//        }
//
//        public String getMerchantId() {
//            return merchantId;
//        }
//
//        public void setMerchantId(String merchantId) {
//            this.merchantId = merchantId;
//        }
//
//        public String getFileName() {
//            return fileName;
//        }
//
//        public void setFileName(String fileName) {
//            this.fileName = fileName;
//        }
//
//        public String getFileType() {
//            return fileType;
//        }
//
//        public void setFileType(String fileType) {
//            this.fileType = fileType;
//        }
//
//        public String getFileContent() {
//            return fileContent;
//        }
//
//        public void setFileContent(String fileContent) {
//            this.fileContent = fileContent;
//        }
//
//        public String getIsOcr() {
//            return isOcr;
//        }
//
//        public void setIsOcr(String isOcr) {
//            this.isOcr = isOcr;
//        }
//
//        public String getOcrType() {
//            return ocrType;
//        }
//
//        public void setOcrType(String ocrType) {
//            this.ocrType = ocrType;
//        }
//
//        public String getSegment() {
//            return segment;
//        }
//
//        public void setSegment(String segment) {
//            this.segment = segment;
//        }
//
//        public String getComments() {
//            return comments;
//        }
//
//        public void setComments(String comments) {
//            this.comments = comments;
//        }
//    }
}
