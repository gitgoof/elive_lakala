package com.lakala.shoudan.activity.shoudan.finance.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/14.
 */
public class TransDetailProInfo implements Serializable{

    private String proName;
    private int isDefault;
    private String productId;
    private boolean isTick = false;

    public boolean isTick() {
        return isTick;
    }

    public void setTick(boolean isTick) {
        this.isTick = isTick;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
