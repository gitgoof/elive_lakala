package com.lakala.elive.map.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created
 */
public class ShopLatLonAloneReque implements Serializable{

    public   String authToken;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String shopId ;


    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
