package com.lakala.shoudan.activity.merchant.upgrade;

import android.text.TextUtils;

/**
 * Created by More on 15/12/3.
 */

//管理 图片上传的本地路径, 和 上送后的服务端文件路径
public class PicUploadBean {

    //本地
    private String localFile;

    //服务端
    private String uploadedFile;

    public PicUploadBean() {
    }

    public PicUploadBean(String localFile, String uploadedFile) {
        this.localFile = localFile;
        this.uploadedFile = uploadedFile;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
        //更新本地照片后删除上送路径,解决提交一半失败,又更新了照片导致问题的bug
        uploadedFile = "";
    }

    public String getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(String uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public boolean hasPicked(){
        return !TextUtils.isEmpty(localFile);
    }

    public boolean hasUploaded(){
        return !TextUtils.isEmpty(uploadedFile);
    }

}
