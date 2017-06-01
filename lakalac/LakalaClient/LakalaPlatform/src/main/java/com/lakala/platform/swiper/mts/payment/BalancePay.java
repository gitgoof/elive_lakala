package com.lakala.platform.swiper.mts.payment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lakala.platform.R;

import java.util.Map;

/**
 * 钱包余额支付
 * Created by xyz on 14-1-5.
 */
public class BalancePay extends Payment implements View.OnClickListener{

    //支付方式布局
    private View layoutView;

    //余额支付金额显示文本
    private TextView balancePayText;

    //钱包余额
    private double balance;


    public BalancePay(Context context) {
        super(context);
        setPaymentType(EPaymentType.BALANCE_PAY);
        setPaymentLabel("钱包支付");
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public View loadView() {

        layoutView = View.inflate(context, R.layout.plat_layout_balance_pay, null);

        balancePayText = (TextView)layoutView.findViewById(R.id.balance_pay_amount);

        return layoutView;
    }

    /**
     * 设置支付金额
     * @param paymentAmount
     */
    @Override
    public void setPaymentAmount(double paymentAmount) {
        super.setPaymentAmount(paymentAmount);
        balancePayText.setText(formatAmountToString());
    }

    @Override
    public Map<String, String> getPaymentParams() {
        return null;
    }

    @Override
    public void clear() {

    }

    /**
     * 获取钱包余额
     * 到达支付页面，从服务端实时获取余额，保证余额正确
     */
    public void requestBalance(){

    }

    @Override
    public void onClick(View view) {
    }
}
