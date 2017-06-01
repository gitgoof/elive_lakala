package com.lakala.elive.beans;

/**
 * 人脸识别解析bean
 * Created by wenhaogu on 2017/3/9.
 */

public class FaceRecognitionResp {
//    {
//            "retCode": "000000",
//            "retMsg": "SUCCESS",
//            "retData": {
//                          "message": "",
//                          "photoResult": "",
//                          "idCardResult": ""
//                           "userPhotoBase64": "",
//                           "similarity": "",
//                          "idCardPhotoBase64": ""
//                          }
//    }

    private String retCode;
    private String retMsg;
    private RetData retData;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public RetData getRetData() {
        return retData;
    }

    public void setRetData(RetData retData) {
        this.retData = retData;
    }

    public static class RetData {
        private String message;
        private String photoResult;
        private String idCardResult;
        private String userPhotoBase64;
        private String similarity;
        private String idCardPhotoBase64;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
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

        public String getUserPhotoBase64() {
            return userPhotoBase64;
        }

        public void setUserPhotoBase64(String userPhotoBase64) {
            this.userPhotoBase64 = userPhotoBase64;
        }

        public String getSimilarity() {
            return similarity;
        }

        public void setSimilarity(String similarity) {
            this.similarity = similarity;
        }

        public String getIdCardPhotoBase64() {
            return idCardPhotoBase64;
        }

        public void setIdCardPhotoBase64(String idCardPhotoBase64) {
            this.idCardPhotoBase64 = idCardPhotoBase64;
        }

        @Override
        public String toString() {
            return "RetData{" +
                    "message='" + message + '\'' +
                    ", photoResult='" + photoResult + '\'' +
                    ", idCardResult='" + idCardResult + '\'' +
                    ", userPhotoBase64='" + userPhotoBase64 + '\'' +
                    ", similarity='" + similarity + '\'' +
                    ", idCardPhotoBase64='" + idCardPhotoBase64 + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FaceRecognitionResp{" +
                "retCode='" + retCode + '\'' +
                ", retMsg='" + retMsg + '\'' +
                ", retData=" + retData +
                '}';
    }
}
