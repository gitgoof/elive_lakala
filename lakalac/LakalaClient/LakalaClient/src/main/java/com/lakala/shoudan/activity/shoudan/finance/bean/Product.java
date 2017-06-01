package com.lakala.shoudan.activity.shoudan.finance.bean;

/**
 * Created by LMQ on 2015/10/21.
 */
public class Product {
    private String MercId;
    private String mercName;
    private String ProductId;
    private String ProductName;

    public String getMercId() {
        return MercId;
    }

    public Product setMercId(String mercId) {
        MercId = mercId;
        return this;
    }

    public String getMercName() {
        return mercName;
    }

    public Product setMercName(String mercName) {
        this.mercName = mercName;
        return this;
    }

    public String getProductId() {
        return ProductId;
    }

    public Product setProductId(String productId) {
        ProductId = productId;
        return this;
    }

    public String getProductName() {
        return ProductName;
    }

    public Product setProductName(String productName) {
        ProductName = productName;
        return this;
    }
}
