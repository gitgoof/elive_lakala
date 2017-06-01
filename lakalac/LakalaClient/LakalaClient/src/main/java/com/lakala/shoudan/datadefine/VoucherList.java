package com.lakala.shoudan.datadefine;

import java.util.List;

/**
 * Created by linmq on 2016/6/13.
 */
public class VoucherList {
    private String sum_price;
    private String sum_count;
    private List<Voucher> vouchers;

    public String getSum_price() {
        return sum_price;
    }

    public VoucherList setSum_price(String sum_price) {
        this.sum_price = sum_price;
        return this;
    }

    public String getSum_count() {
        return sum_count;
    }

    public VoucherList setSum_count(String sum_count) {
        this.sum_count = sum_count;
        return this;
    }

    public List<Voucher> getVouchers() {
        return vouchers;
    }

    public VoucherList setVouchers(List<Voucher> vouchers) {
        this.vouchers = vouchers;
        return this;
    }
}
