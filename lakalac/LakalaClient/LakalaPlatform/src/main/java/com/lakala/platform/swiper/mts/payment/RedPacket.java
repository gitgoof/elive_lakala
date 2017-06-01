package com.lakala.platform.swiper.mts.payment;

import java.util.Date;

/**
 * 红包实体类
 * Created by xyz on 14-1-24.
 */
public class RedPacket {

    //红包金额
    private double amount;

    //红包描述（五元彩票红包）
    private String description;

    //红包到期时间
    private Date expiredDate;

    //是否支持合并
    private boolean isSupportMerge;

    public RedPacket(double amount, String description, Date expiredDate, boolean isSupportMerge) {
        this.amount = amount;
        this.description = description;
        this.expiredDate = expiredDate;
        this.isSupportMerge = isSupportMerge;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public boolean isSupportMerge() {
        return isSupportMerge;
    }

    public void setSupportMerge(boolean isSupportMerge) {
        this.isSupportMerge = isSupportMerge;
    }
}
