package com.lakala.shoudan.activity.shoudan.finance.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/10/12.
 */
public class TransDetail implements Serializable {

        private String TradeName;
        private String TradeTime;
        private String ProdName;
        private double Amount;
        private String TradeType;
        private String Day;
        private String MoneyGo;
        private String Sid;

        public void setTradeName(String TradeName) {
            this.TradeName = TradeName;
        }

        public void setTradeTime(String TradeTime) {
            this.TradeTime = TradeTime;
        }

        public void setProdName(String ProdName) {
            this.ProdName = ProdName;
        }

        public void setAmount(double Amount) {
            this.Amount = Amount;
        }

        public void setTradeType(String TradeType) {
            this.TradeType = TradeType;
        }

        public void setDay(String Day) {
            this.Day = Day;
        }

        public void setMoneyGo(String MoneyGo) {
            this.MoneyGo = MoneyGo;
        }

        public void setSid(String Sid) {
            this.Sid = Sid;
        }

        public String getTradeName() {
            return TradeName;
        }

        public String getTradeTime() {
            return TradeTime;
        }

        public String getProdName() {
            return ProdName;
        }

        public double getAmount() {
            return Amount;
        }

        public String getTradeType() {
            return TradeType;
        }

        public String getDay() {
            return Day;
        }

        public String getMoneyGo() {
            return MoneyGo;
        }

        public String getSid() {
            return Sid;
        }

}
