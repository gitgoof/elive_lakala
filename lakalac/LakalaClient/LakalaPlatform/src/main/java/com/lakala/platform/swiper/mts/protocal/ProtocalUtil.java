package com.lakala.platform.swiper.mts.protocal;

import com.lakala.platform.common.PackageFileManager;
import com.lakala.platform.common.PropertiesUtil;

/**
 * 服务协议工具类URL
 *
 * Created by jerry on 14-3-28.
 */
public class ProtocalUtil {

    private ProtocalUtil() {
    }

    /**
     * 获取地址
     * @param isLocal true:本地URL  false:网络端URL
     */
    public static String getServerUrl(boolean isLocal,String pathName){
        String url = "";
        if (isLocal){
            url = PackageFileManager.getInstance().getAgreementsDirPath();
        } else {
            url = PropertiesUtil.getUrl()+"agreement/";
        }
        return url + pathName;
    }

    /** 帮助页面前缀 */
    public final static String LAKALA_SERVER_URL = "lakala_fwxy.html";

    /** 新手指南    */
    public final static String LAKALA_XSZN_URL = "lakala_xszn.html";

    /** 账单管理服务协议 */
    public final static String ZHANGDANGUANLI_PROTOCOL_URL = "lakala_xykzdyhxy.html";

    /** 更换登陆手机号协议 */
    public final static String REPLACE_USER_PHONE_URL = "lakala_ghsjhsm.html";

    /** 刷卡器使用说明 */
    public final static String SWIPE_INTRODUCE_URL = "lakala_skqsm.html";

    /** 快捷支付协议 */
    public final static String QUICK_PAYMENT_URL = "lakala_kjzfxy.html";

    /** 实名认证说明 */
    public final static String REAL_NAME_AUTH_URL = "identification.html";

}
