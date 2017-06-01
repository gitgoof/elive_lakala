package com.lakala.elive.beans;

import java.io.Serializable;
import java.util.List;

/**
 * 地区数据字典
 * Created by gaofeng on 2017/4/18.
 */

public class AreaDataBean implements Serializable {
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

    public static class ContentBean{
        private List<AreaInfoItem> areaInfoList;
        private List<AreaInfoItem> cityInfoList;
        private List<AreaInfoItem> proInfoList;

        public List<AreaInfoItem> getAreaInfoList() {
            return areaInfoList;
        }

        public void setAreaInfoList(List<AreaInfoItem> areaInfoList) {
            this.areaInfoList = areaInfoList;
        }

        public List<AreaInfoItem> getCityInfoList() {
            return cityInfoList;
        }

        public void setCityInfoList(List<AreaInfoItem> cityInfoList) {
            this.cityInfoList = cityInfoList;
        }

        public List<AreaInfoItem> getProInfoList() {
            return proInfoList;
        }

        public void setProInfoList(List<AreaInfoItem> proInfoList) {
            this.proInfoList = proInfoList;
        }
    }
    // "areaCode":"690002","areaName":"涪陵区",
    // "areaZoneVOs":[],"cityCode":"6900",
    // "cityName":"重庆","provCode":"996900","provName":"重庆

    /**
     * 地址详细信息
     */
    public static class AreaInfoItem{
        private String areaCode;
        private String areaName;
        private String cityCode;
        private String cityName;
        private String provCode;
        private String provName;

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }

        public String getAreaName() {
            return areaName;
        }

        public void setAreaName(String areaName) {
            this.areaName = areaName;
        }

        public String getCityCode() {
            return cityCode;
        }

        public void setCityCode(String cityCode) {
            this.cityCode = cityCode;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getProvCode() {
            return provCode;
        }

        public void setProvCode(String provCode) {
            this.provCode = provCode;
        }

        public String getProvName() {
            return provName;
        }

        public void setProvName(String provName) {
            this.provName = provName;
        }
    }
}
