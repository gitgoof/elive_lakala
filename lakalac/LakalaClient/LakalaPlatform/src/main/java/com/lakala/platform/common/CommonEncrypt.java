package com.lakala.platform.common;

import com.lakala.library.DebugConfig;
import com.lakala.library.encryption.RSAEncrypt;
import com.lakala.library.jni.LakalaNative;
import com.lakala.platform.swiper.TerminalKey;

/**
 * Created by wangchao on 14-3-1.
 */
public class CommonEncrypt {

    /**
     * pinKey加密
     *
     * @param ksn  该ksn必须先做终端签到
     * @param password  源数据为明文,先拿签到的工作密钥做Des后做rsa加密
     * @return
     */
    public static String pinKeyDesRsaEncrypt(String ksn, String password){
        String pwd      = LakalaNative.encryptPwd(TerminalKey.getMasterKey(ksn), TerminalKey.getWorkKey(ksn), password, DebugConfig.DEV_ENVIRONMENT);
        return rsaPinKeyEncrypt(pwd);
    }

    /**
     * 登录密码加密
     *
     * @param password
     * @return
     */
    public static String loginEncrypt(String password){
        RSAEncrypt rsaEncrypt = new RSAEncrypt(LakalaNative.getLoginPublicKey());
        return rsaEncrypt.encrypt(password.getBytes()).toUpperCase();
    }

    /**
     * 带密码键盘的pin加密
     * @param password 一般为Des加密过后的password,注:由pos返回回来的password就是做了Des加密
     * @return
     */
    public static String rsaPinKeyEncrypt(String password){

        if("".equals(password)){
            return "";
        }
        return new RSAEncrypt(LakalaNative.getPasswordPublicKey(DebugConfig.DEV_ENVIRONMENT)).encrypt(password.getBytes()).toUpperCase();

    }

    /**
     * 依据Mab计算出mac
     * @param ksn
     * @param macString
     * @return
     */
    public static String generateMac(String ksn, String macString){
        return LakalaNative.generateMac(TerminalKey.getMasterKey(ksn),
                TerminalKey.getMacKey(ksn),
                macString,
                DebugConfig.DEV_ENVIRONMENT);
    }

}
