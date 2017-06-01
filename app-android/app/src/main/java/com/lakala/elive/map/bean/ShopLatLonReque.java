package com.lakala.elive.map.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created
 */
public class ShopLatLonReque implements Serializable{

    public   String authToken;
    public List<String>  shopIds ;

    public List<String>getShopIds() {
        return shopIds;
    }

    public void setShopIds(List<String> shopIds) {
        this.shopIds = shopIds;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
