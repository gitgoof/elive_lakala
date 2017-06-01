package com.lakala.platform.common.securitykeyboard;

import java.util.HashMap;
import java.util.Map;

import cn.cloudcore.iprotect.plugin.CEditTextAttrSet;

/**
 * 密码键盘管理
 *
 * 使用对象：
 * 1.拉卡拉密码
 * 2.银行卡密码
 * 3.短信验证码
 *
 * 使用方法:
 * 1.实例化,初始化属性
 * 2.getText(时间戳) 获取文本输入内容(单次加密,需要服务端解密)
 * 3.onDestroy Activity销毁时销毁组件
 *
 * Created by jerry on 14-5-19.
 */
public class SecurityKeyboardManager {

    private HashMap<String,SecurityInfo> mSecurityInfos;

    public SecurityKeyboardManager(HashMap<String,SecurityInfo> securityInfos){
        this.mSecurityInfos = securityInfos;
        for (Map.Entry<String,SecurityInfo> entry : securityInfos.entrySet()) {
            String name = entry.getKey();
            SecurityInfo securityInfo = entry.getValue();
            // 创建安全输入系统的属性
            CEditTextAttrSet attrs = new CEditTextAttrSet();
            //若同时使用多个控件,作伪唯一标示
            attrs.name = name;
            //打开键盘时，首先显示全键盘p
            attrs.softkbdType = securityInfo.getSoftkbdType();
            //设置输入内容类型
            attrs.contentType = securityInfo.getContentType();
            //设置键盘样式
            attrs.softkbdView = securityInfo.getSoftkbdView();
            //设置键盘是否乱序
            attrs.kbdRandom   = securityInfo.isSoftkbdRandom();
            //设置长度
            attrs.maxLength   = securityInfo.getMaxLength();

            if (securityInfo.getTextColor() != 0) {
                attrs.textColor   = securityInfo.getTextColor();
            }
            //初始化控件，未初始化，将不能使用
            securityInfo.getSecurityEditText().initialize(attrs);
        }
    }

    /**
     * 获取文本输入框
     */
    public String getText(String name){
        // 获得服务器时间，这里是本地当前时间，单位：秒
        Long msec = System.currentTimeMillis();
        String timeStamp = String.valueOf(msec / 1000);
        // 获得输入加密后的输入内容
        return mSecurityInfos == null ? "" :
                mSecurityInfos.get(name).getSecurityEditText().getValue(null);
    }

    /**
     * 页面结束销毁
     */
    public void onDestory(){
        if (mSecurityInfos == null) return;
        for (Map.Entry<String,SecurityInfo> entry : mSecurityInfos.entrySet()) {
            SecurityInfo info = entry.getValue();
            info.getSecurityEditText().onDestroy();
        }
        mSecurityInfos.clear();
        mSecurityInfos = null;
    }



}
