package com.lakala.shoudan.activity.coupon.params;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * 代金券手续费
 * Created by huangjp on 2016/6/14.
 */
public class CouponFeeRequest extends CommonBaseRequest {
    private double amount;
    public CouponFeeRequest(Context context) {
        super(context);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
