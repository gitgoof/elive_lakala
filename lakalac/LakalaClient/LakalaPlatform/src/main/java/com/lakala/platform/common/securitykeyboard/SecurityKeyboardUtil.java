package com.lakala.platform.common.securitykeyboard;

import java.util.HashMap;

/**
 * 密码键盘使用工具类
 *
 * 1.拉卡拉密码（字母 + 数字）
 * 2.银行卡密码（纯数字键盘）
 * 3.验证码（纯数字键盘）
 * 3.图片验证码（字母 + 数字）
 * 3.自定义键盘
 *
 * Created by jerry on 14-5-20.
 */
public class SecurityKeyboardUtil {

    /**
     * 拉卡拉密码使用自定义键盘
     *
     * @param editText      安全输入框
     * @return 密码键盘管理类
     */
    public static SecurityKeyboardManager lklPassword(SecurityEditText editText,String name){
        HashMap<String,SecurityInfo> map = new HashMap<String, SecurityInfo>();
        SecurityInfo info = createSecurityInfo(editText, (short)0, (short)0, (short)32, (short)6, (short)0, false, map);
        map.put(name,info);
        SecurityKeyboardManager manager = new SecurityKeyboardManager(map);
        return manager;
    }

    /**
     * 拉卡拉密码使用自定义键盘
     *
     * @param editText      安全输入框
     * @return 密码键盘管理类
     */
    public static SecurityKeyboardManager lklPassword(SecurityEditText editText,String name,int textColor){
        HashMap<String,SecurityInfo> map = new HashMap<String, SecurityInfo>();
        SecurityInfo info = createSecurityInfo(editText, (short)0, (short)0, (short)32, (short)6, (short)0, false, map);
        info.setTextColor(textColor);
        map.put(name,info);
        SecurityKeyboardManager manager = new SecurityKeyboardManager(map);
        return manager;
    }

    /**
     * 银行卡密码使用自定义键盘
     *
     * @param editText      安全输入框
     * @return 密码键盘管理类
     */
    public static SecurityKeyboardManager bankCardPassword(SecurityEditText editText,String name){
        HashMap<String,SecurityInfo> map = new HashMap<String, SecurityInfo>();
        SecurityInfo info = createSecurityInfo(editText, (short)2, (short)1, (short)6, (short)6, (short)1, false, map);
        map.put(name,info);
        SecurityKeyboardManager manager = new SecurityKeyboardManager(map);
        return manager;
    }

    /**
     * 短信验证码使用自定义键盘
     *
     * @param editText      安全输入框
     * @return 密码键盘管理类
     */
    public static SecurityKeyboardManager smsVerifyCode(SecurityEditText editText,String name){
        HashMap<String,SecurityInfo> map = new HashMap<String, SecurityInfo>();
        SecurityInfo info = createSecurityInfo(editText, (short)2, (short)1, (short)6, (short)6, (short)0, false, map);
        map.put(name,info);
        SecurityKeyboardManager manager = new SecurityKeyboardManager(map);
        return manager;
    }

    /**
     * 图片验证码使用自定义键盘
     *
     * @param editText      安全输入框
     * @return 密码键盘管理类
     */
    public static SecurityKeyboardManager imgCaptcha(SecurityEditText editText,String name){
        HashMap<String,SecurityInfo> map = new HashMap<String, SecurityInfo>();
        SecurityInfo info = createSecurityInfo(editText, (short)2, (short)1, (short)4, (short)4, (short)0, false, map);
        map.put(name,info);
        SecurityKeyboardManager manager = new SecurityKeyboardManager(map);
        return manager;
    }

    /**
     * 拉卡拉密码使用自定义键盘
     *
     * @param editText      安全输入框
     * @param softkbdType   0：默认打开全键盘 1：默认打开数字键盘 2：只显示数字键盘
     * @param contentType
     * 0 任意按全键盘上的输入
     * 1 必须含有数字
     * 2 必须含有字母
     * 4 必须含有符号
     * 1 2 4 可以自由组合
     * 组合规则:此处可直接相加1+2(必须含有数字和字母)
     * @param maxLength 最大输入长度
     * @param minLength 最小输入长度
     * @param softkbdView 输入法样式  0：只有键盘,背景透明  1：多一个EditText,背景半透明
     * @param softkbdRandom 键盘显示是否乱序
     *
     * @return 密码键盘管理类
     */
    public static SecurityKeyboardManager customKeyboard(SecurityEditText editText,
                                                         short softkbdType,
                                                         short contentType,
                                                         short maxLength,
                                                         short minLength,
                                                         short softkbdView,
                                                         boolean softkbdRandom,
                                                         String name){
        HashMap<String,SecurityInfo> map = new HashMap<String, SecurityInfo>();
        SecurityInfo info = createSecurityInfo(editText, softkbdType, contentType, maxLength, minLength, softkbdView, softkbdRandom, map);
        map.put(name,info);
        SecurityKeyboardManager manager = new SecurityKeyboardManager(map);
        return manager;
    }

    /**
     * 创建密码键盘参数
     */
    private static SecurityInfo createSecurityInfo(SecurityEditText editText,
                                           short softkbdType,
                                           short contentType,
                                           short maxLength,
                                           short minLength,
                                           short softkbdView,
                                           boolean softkbdRandom,
                                           HashMap<String, SecurityInfo> map) {
        SecurityInfo info = new SecurityInfo();
        info.setSecurityEditText(editText);
        info.setSoftkbdType(softkbdType);
        info.setContentType(contentType);
        info.setMaxLength(maxLength);
        info.setMinLength(minLength);
        info.setSoftkbdView(softkbdView);
        info.setSoftkbdRandom(softkbdRandom);
        return info;
    }


}
