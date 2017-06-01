package com.lakala.shoudan.datadefine;

import org.json.JSONObject;

/**
 * Created by linmq on 2016/6/12.
 */
public class VoucherExplain {
    private String id;
    private String price;
    private String final_fee;
    private String exchange_content;
    private String exchange_address;
    private String item_id;
    private String notes;
    private String query_address;
    private String query_content;
    private String name;
    private double proportion;
    private String service_phone;
    private String description;
    private String shop_url;

    public static VoucherExplain obtain(JSONObject jsonObject){
        VoucherExplain voucherExplain = new VoucherExplain();
        String id = jsonObject.optString("id");
        String price = jsonObject.optString("price");
        String final_fee = jsonObject.optString("final_fee");
        String exchange_content = jsonObject.optString("exchange_content");
        String exchange_address = jsonObject.optString("exchange_address");
        String item_id = jsonObject.optString("item_id");
        String notes = jsonObject.optString("notes");
        String query_address = jsonObject.optString("query_address");
        String query_content = jsonObject.optString("query_content");
        String name = jsonObject.optString("name");
        String service_phone = jsonObject.optString("service_phone");
        String description = jsonObject.optString("description");
        String shop_url = jsonObject.optString("shop_url");
        double proportion = jsonObject.optDouble("proportion");
        voucherExplain.setId(id)
                      .setPrice(price)
                      .setFinal_fee(final_fee)
                      .setExchange_content(exchange_content)
                      .setExchange_address(exchange_address)
                      .setItem_id(item_id)
                      .setNotes(notes)
                      .setQuery_address(query_address)
                      .setQuery_content(query_content)
                      .setName(name)
                      .setService_phone(service_phone)
                      .setDescription(description)
                      .setShop_url(shop_url)
                      .setProportion(proportion);
        return voucherExplain;
    }

    public String getQuery_content() {
        return query_content;
    }

    public VoucherExplain setQuery_content(String query_content) {
        this.query_content = query_content;
        return this;
    }

    public String getId() {
        return id;
    }

    public VoucherExplain setId(String id) {
        this.id = id;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public VoucherExplain setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getFinal_fee() {
        return final_fee;
    }

    public VoucherExplain setFinal_fee(String final_fee) {
        this.final_fee = final_fee;
        return this;
    }

    public String getExchange_content() {
        return exchange_content;
    }

    public VoucherExplain setExchange_content(String exchange_content) {
        this.exchange_content = exchange_content;
        return this;
    }

    public String getExchange_address() {
        return exchange_address;
    }

    public VoucherExplain setExchange_address(String exchange_address) {
        this.exchange_address = exchange_address;
        return this;
    }

    public String getItem_id() {
        return item_id;
    }

    public VoucherExplain setItem_id(String item_id) {
        this.item_id = item_id;
        return this;
    }

    public String getNotes() {
        return notes;
    }

    public VoucherExplain setNotes(String notes) {
        this.notes = notes;
        return this;
    }

    public String getQuery_address() {
        return query_address;
    }

    public VoucherExplain setQuery_address(String query_address) {
        this.query_address = query_address;
        return this;
    }

    public String getName() {
        return name;
    }

    public VoucherExplain setName(String name) {
        this.name = name;
        return this;
    }

    public double getProportion() {
        return proportion;
    }

    public VoucherExplain setProportion(double proportion) {
        this.proportion = proportion;
        return this;
    }

    public String getService_phone() {
        return service_phone;
    }

    public VoucherExplain setService_phone(String service_phone) {
        this.service_phone = service_phone;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VoucherExplain setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getShop_url() {
        return shop_url;
    }

    public VoucherExplain setShop_url(String shop_url) {
        this.shop_url = shop_url;
        return this;
    }
}
