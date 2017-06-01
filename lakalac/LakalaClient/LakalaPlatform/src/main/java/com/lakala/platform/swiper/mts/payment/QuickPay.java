package com.lakala.platform.swiper.mts.payment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lakala.platform.R;

import java.util.List;
import java.util.Map;

/**
 * 银行卡快捷支付
 * Created by xyz on 14-1-5.
 */
public class QuickPay extends Payment implements View.OnClickListener{

    //支付方式布局
    private View layoutView;

    //快捷支付银行卡信息
    private TextView quickPayLabel;

    //快捷支付金额显示文本
    private TextView quickPayAmountText;

    //用户添加过的快捷银行卡
    private List<QuickCard> quickCards;

    public QuickPay(Context context) {
        super(context);
        setPaymentType(EPaymentType.QUICK_PAY);
        setPaymentLabel("快捷银行卡支付");
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public View loadView() {
        layoutView = View.inflate(context, R.layout.plat_layout_quick_pay, null);

        quickPayLabel = (TextView)layoutView.findViewById(R.id.quick_pay_label);
        quickPayAmountText = (TextView)layoutView.findViewById(R.id.quick_pay_amount);

        return layoutView;
    }

    @Override
    public void setPaymentAmount(double paymentAmount) {
        super.setPaymentAmount(paymentAmount);
        quickPayAmountText.setText(formatAmountToString());
    }

    /**
     * 设置快捷支付银行卡信息
     * @param bankInfo
     */
    public void setQuickPayLabel(String bankInfo){
        quickPayLabel.setText(bankInfo);
    }

    @Override
    public Map<String, String> getPaymentParams() {
        return null;
    }

    @Override
    public void clear() {

    }

    /**
     * 获取当前用户添加过的快捷支付银行卡
     */
    private void requestQuickCards(){

    }
}
