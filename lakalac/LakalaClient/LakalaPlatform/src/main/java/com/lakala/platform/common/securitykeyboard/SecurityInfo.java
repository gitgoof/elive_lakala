package com.lakala.platform.common.securitykeyboard;

/**
 * 使用密码键盘属性配置
 *
 * Created by jerry on 14-5-20.
 */
public class SecurityInfo {

    /** 使用安全键盘输入框 */
    private SecurityEditText securityEditText;

    /** 键盘类型:
     *  0：默认打开全键盘 1：默认打开数字键盘 2：只显示数字键盘
     *  0、1界面中可以切换
     */
    private short softkbdType;

    /** 输入框最小长度 */
    private short minLength;

    /** 输入框最大长度 */
    private short maxLength;

    /**
     * 输入内容类型:
     * 0 任意按全键盘上的输入
     * 1 必须含有数字
     * 2 必须含有字母
     * 4 必须含有符号
     * 1 2 4 可以自由组合
     * 组合规则:此处可直接相加1+2(必须含有数字和字母)
     */
    private short contentType;

    /**
     * 输入法样式  0：只有键盘,背景透明  1：多一个EditText,背景半透明
     */
    private short softkbdView;
    /**
     * 设置软键盘按键位置是否随机，初始化时指定。
     * 可选值：true、false，默认选择随机（true）。
     */
    private boolean softkbdRandom;

    //密码键盘文字颜色
    private int textColor = 0;

    public SecurityEditText getSecurityEditText() {
        return securityEditText;
    }

    public void setSecurityEditText(SecurityEditText securityEditText) {
        this.securityEditText = securityEditText;
    }

    public short getSoftkbdType() {
        return softkbdType;
    }

    public void setSoftkbdType(short softkbdType) {
        this.softkbdType = softkbdType;
    }

    public short getMinLength() {
        return minLength;
    }

    public void setMinLength(short minLength) {
        this.minLength = minLength;
    }

    public short getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(short maxLength) {
        this.maxLength = maxLength;
    }

    public short getContentType() {
        return contentType;
    }

    public void setContentType(short contentType) {
        this.contentType = contentType;
    }

    public short getSoftkbdView() {
        return softkbdView;
    }

    public void setSoftkbdView(short softkbdView) {
        this.softkbdView = softkbdView;
    }

    public boolean isSoftkbdRandom() {
        return softkbdRandom;
    }

    public void setSoftkbdRandom(boolean softkbdRandom) {
        this.softkbdRandom = softkbdRandom;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
