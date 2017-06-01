package com.lakala.platform.swiper.mts.protocal;

import java.io.Serializable;

/**
 * 协议类型
 */
public enum EProtocalType implements Serializable {

        NONE(""),
        //拉卡拉服务协议
        SERVICE_PROTOCAL(ProtocalUtil.LAKALA_SERVER_URL),
        //新手指南
        SERVICE_XSZN(ProtocalUtil.LAKALA_XSZN_URL),
        //拉卡拉账单管理使用协议
        ZDGL_PROTOCAL(ProtocalUtil.ZHANGDANGUANLI_PROTOCOL_URL),
        //更换登陆手机号协议
        CHANGE_MOBILE_PROTOCAL(ProtocalUtil.REPLACE_USER_PHONE_URL),
        //刷卡器使用说明
        SWIPE_INTRODUCE(ProtocalUtil.SWIPE_INTRODUCE_URL),
        //快捷支付协议
        QUICK_PAYMENT(ProtocalUtil.QUICK_PAYMENT_URL),
        //实名认证说明
        REAL_NAME_AUTH(ProtocalUtil.REAL_NAME_AUTH_URL),
        ;

        private String value;

        EProtocalType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

}