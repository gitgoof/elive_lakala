package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by HJP on 2015/9/18.
 * 交易详情理，财产品转入、转出明细-请求参数
 */
public class TradeListRequest extends BaseRequest{
    /**
     * 合同号
     */
    private String ContractId;
    /**
     * 产品代码
     */
    private String ProductId;
    /**
     * 页码
     */
    private String PageCount;
    /**
     * 查询个数
     */
    private String PageSize;

    public String getContractId() {
        return ContractId;
    }

    public void setContractId(String contractId) {
        ContractId = contractId;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getPageCount() {
        return PageCount;
    }

    public void setPageCount(String pageCount) {
        PageCount = pageCount;
    }

    public String getPageSize() {
        return PageSize;
    }

    public void setPageSize(String pageSize) {
        PageSize = pageSize;
    }
}
