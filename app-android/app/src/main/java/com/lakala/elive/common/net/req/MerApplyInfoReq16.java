package com.lakala.elive.common.net.req;


import java.util.List;

/**
 * 人脸识别结果提交
 * Created by wenhaogu on 2017/3/8.
 */

public class MerApplyInfoReq16 {
    public String authToken;
    private MerchantUserAuthInfo merUserAuthInfo;
    private List<AttachmentFileVO> attachments;

    public MerchantUserAuthInfo getMerUserAuthInfo() {
        return merUserAuthInfo;
    }

    public void setMerUserAuthInfo(MerchantUserAuthInfo merUserAuthInfo) {
        this.merUserAuthInfo = merUserAuthInfo;
    }

    public List<AttachmentFileVO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentFileVO> attachments) {
        this.attachments = attachments;
    }

    public static class MerchantUserAuthInfo {
        private String applyId;
        private String idName;
        private String idCard;
        private String photoResult;
        private String idCardResult;
        private String similarity;
        private String resultMsg;
        private String sysCode;
        private String sysMsg;
        private String authCnt;

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getIdName() {
            return idName;
        }

        public void setIdName(String idName) {
            this.idName = idName;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getPhotoResult() {
            return photoResult;
        }

        public void setPhotoResult(String photoResult) {
            this.photoResult = photoResult;
        }

        public String getIdCardResult() {
            return idCardResult;
        }

        public void setIdCardResult(String idCardResult) {
            this.idCardResult = idCardResult;
        }

        public String getSimilarity() {
            return similarity;
        }

        public void setSimilarity(String similarity) {
            this.similarity = similarity;
        }

        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }

        public String getSysCode() {
            return sysCode;
        }

        public void setSysCode(String sysCode) {
            this.sysCode = sysCode;
        }

        public String getSysMsg() {
            return sysMsg;
        }

        public void setSysMsg(String sysMsg) {
            this.sysMsg = sysMsg;
        }

        public String getAuthCnt() {
            return authCnt;
        }

        public void setAuthCnt(String authCnt) {
            this.authCnt = authCnt;
        }
    }

    public static class AttachmentFileVO {
        private String applyId;
        private String fileName;
        private String fileType;
        private String fileContent;
        private String segment;

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
        }

        public String getSegment() {
            return segment;
        }

        public void setSegment(String segment) {
            this.segment = segment;
        }
    }
}
