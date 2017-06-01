package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by HJP on 2015/9/18.
 * 理财产品列表-请求参数
 */
public class ProdListRequest extends BaseRequest {
    private String ProductId;//产品代码
    public ProdListRequest(){

    }
    public ProdListRequest(String ProductId){
        this.ProductId=ProductId;
    }
    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }
}
