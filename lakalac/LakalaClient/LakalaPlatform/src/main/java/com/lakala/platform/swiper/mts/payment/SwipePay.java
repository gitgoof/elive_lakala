package com.lakala.platform.swiper.mts.payment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.lakala.platform.R;

import java.util.Map;

/**
 * 刷卡支付
 * Created by xyz on 14-1-5.
 */
public class SwipePay extends Payment {

    private View layoutView;

    //刷卡支付金额显示文本
    private TextView swipePayAmountText;

    public SwipePay(Context context) {
        super(context);
        setPaymentLabel("刷卡支付");
        setPaymentType(EPaymentType.SWIPE_PAY);
    }

    @Override
    public View loadView() {
        layoutView = View.inflate(context, R.layout.plat_layout_swipe_pay, null);

        swipePayAmountText = (TextView)layoutView.findViewById(R.id.swipe_pay_amount);

        return layoutView;
    }

    @Override
    public Map<String, String> getPaymentParams() {
        return null;
    }

    @Override
    public void setPaymentAmount(double paymentAmount) {
        super.setPaymentAmount(paymentAmount);
        swipePayAmountText.setText(formatAmountToString());
    }

    @Override
    public void clear() {

    }
}
