package com.lakala.elive.common.net.req;

/**
 * 获取照片请求bean
 * Created by wenhaogu on 2017/3/1.
 */

public class GetPhotoReq {
    private String mediaId;

    public GetPhotoReq() {
    }

    public GetPhotoReq(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
