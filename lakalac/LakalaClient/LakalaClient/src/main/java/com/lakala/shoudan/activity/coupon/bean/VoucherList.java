package com.lakala.shoudan.activity.coupon.bean;

/**
 * 用户代金券列表数据
 * Created by huangjp on 2016/6/3.
 */
//sum_price	可用代金券总金额
//sum_count	可用代金券张数
//vouchers [] 代金券列表
public class VoucherList {
    private String sum_price;
    private String sum_count;
    private CouponBean vouchers;

    public String getSum_price() {
        return sum_price;
    }

    public void setSum_price(String sum_price) {
        this.sum_price = sum_price;
    }

    public String getSum_count() {
        return sum_count;
    }

    public void setSum_count(String sum_count) {
        this.sum_count = sum_count;
    }

    public CouponBean getVouchers() {
        return vouchers;
    }

    public void setVouchers(CouponBean vouchers) {
        this.vouchers = vouchers;
    }
}
