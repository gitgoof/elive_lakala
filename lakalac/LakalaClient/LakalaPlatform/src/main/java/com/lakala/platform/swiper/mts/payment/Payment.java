package com.lakala.platform.swiper.mts.payment;

import android.content.Context;
import android.view.View;

import java.util.Map;

/**
 * 支付方式基类
 * 统一每种支付方式公共的元素，如数据（支付金额）、方法（getPaymentParams）等
 * Created by xyz on 14-1-5.
 */
public abstract class Payment {

    protected Context context;

    /**
     * 支付金额，使用当前支付方式的支付金额
     */
    protected double paymentAmount;

    /**
     * 是否选择此支付方式，即是否使用当前支付方式进行支付
     */
    protected boolean isSelectedToPay;

    protected EPaymentType paymentType;

    /**
     * 支付方式标记
     */
    protected String paymentLabel;

    /**
     * 当前支付方式需要的密码
     */
    protected String password;

    public Payment(Context context){
        this.context = context;
    }

    public EPaymentType getPaymentType() {
        return paymentType;
    }

    /**
     * 设置当前的支付方式
     * @param paymentType
     */
    public void setPaymentType(EPaymentType paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * 获取使用当前支付方式需要支付的金额
     * @return
     */
    public double getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * 设置使用当前支付方式需要支付的金额
     * @param paymentAmount
     */
    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentLabel() {
        return paymentLabel;
    }

    public void setPaymentLabel(String paymentLabel) {
        this.paymentLabel = paymentLabel;
    }

    protected String formatAmountToString(){
        return String.format("%.2f元", paymentAmount);
    }

    /**
     * 是否使用当前支付方式
     * @return false 说明不使用当前支付方式
     */
    public boolean isSelectedToPay() {
        return isSelectedToPay;
    }

    /**
     * 设置是否使用当前的支付方式
     * @param isSelectedToPay
     */
    public void setSelectedToPay(boolean isSelectedToPay) {
        this.isSelectedToPay = isSelectedToPay;
    }

    /**
     * 获得当前支付方式的密码，加密之后的密码
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置当前支付方式对应的密码
     * @param password 加密后的密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 加载当前支付方式的ui
     * @return
     */
    public abstract View loadView();

    /**
     * 获取支付参数
     * @return 返回当前支付方式的支付参数
     */
    public abstract Map<String,String> getPaymentParams();

    /**
     * 支付完成或者支付异常时，清除当前支付方式的数据
     */
    public abstract void clear();
}
