package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by HJP on 2015/10/19.
 */
public class FundProdDetailQryRequest extends BaseRequest{
    private String ProductId;
    public FundProdDetailQryRequest(String ProductId){
        this.ProductId=ProductId;
    }
    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }
}
