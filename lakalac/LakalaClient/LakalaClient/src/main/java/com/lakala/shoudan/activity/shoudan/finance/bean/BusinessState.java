package com.lakala.shoudan.activity.shoudan.finance.bean;

/**
 * Created by More on 15/9/2.
 *
 * 理财y业务开通状态 *
 */
public enum BusinessState {

    /**
     * 未开户
     */
    NONE,
    /**
     * 已激活
     */
    ENABLED,
    /**
     * 未激活
     */
    DISABLED,
    /**
     * 开户失败，可重新开户
     */
    FAILED;

    /**
     * Created by HJP on 2015/9/16.
     */
    public static class TradeListInfo {
        /**
         * 总条数
         */
        private int TotalCount;
        /**
         * 当前页数
         */
        private int CurruntPage;
        /**
         * 每页记录数
         */
        private int PageSize;
        /**
         * 资金变动流水号
         */
        private String Sid;
        /**
         * 产品名称
         */
        private String ProdName;
        /**
         * 交易日期
         */
        private String Day;
        /**
         * 金额
         */
        private double Amount;
        /**
         * 交易码
         */
        private String TradeName;

        /**
         * 交易类型 1申购,2赎回,3本金返还,4收益返还,5转让,6退款
         */
        private int TradeType;

        /**
         * 交易具体时间
         */
        private String TradeTime;

        /**
         * 资金去向
         */
        private String MoneyGo;


        public int getTotalCount() {
            return TotalCount;
        }

        public void setTotalCount(int totalCount) {
            TotalCount = totalCount;
        }

        public int getCurruntPage() {
            return CurruntPage;
        }

        public void setCurruntPage(int curruntPage) {
            CurruntPage = curruntPage;
        }

        public int getPageSize() {
            return PageSize;
        }

        public void setPageSize(int pageSize) {
            PageSize = pageSize;
        }

        public String getSid() {
            return Sid;
        }

        public void setSid(String sid) {
            Sid = sid;
        }

        public String getProdName() {
            return ProdName;
        }

        public void setProdName(String prodName) {
            ProdName = prodName;
        }

        public String getDay() {
            return Day;
        }

        public void setDay(String day) {
            Day = day;
        }

        public double getAmount() {
            return Amount;
        }

        public void setAmount(double amount) {
            Amount = amount;
        }

        public String getTradeName() {
            return TradeName;
        }

        public void setTradeName(String tradeName) {
            TradeName = tradeName;
        }

        public int getTradeType() {
            return TradeType;
        }

        public void setTradeType(int tradeType) {
            TradeType = tradeType;
        }

        public String getTradeTime() {
            return TradeTime;
        }

        public void setTradeTime(String tradeTime) {
            TradeTime = tradeTime;
        }

        public String getMoneyGo() {
            return MoneyGo;
        }

        public void setMoneyGo(String moneyGo) {
            MoneyGo = moneyGo;
        }
    }
}
