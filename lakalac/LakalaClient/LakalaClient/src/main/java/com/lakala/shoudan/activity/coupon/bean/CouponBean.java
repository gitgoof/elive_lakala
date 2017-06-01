package com.lakala.shoudan.activity.coupon.bean;

/**
 * 代金券列表的bean
 * Created by huangjp on 2016/5/31.
 */

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 id	代金券id	N	String
 voucher_id	代金券类型id	N	String
 price	金额	N	String
 old_price	代金券原始金额	N	String
 voucher_num	用户优惠券码值	N	String
 card_num	所使用的手机号	N	String
 state	状态	N	String	1,未使用;
                            2,禁用,
                            3过期,
                            4,已使用,
                            5已退券
 start_time	有效期开始时间	N	String
 end_time	有效期结束时间	N	String
 create_time	获取时间	N	String
 brand_name	代金券名称	N	String
 description	描述	N	String
 voucher_type_style	样式 	N	String	1、用户兑换代，
                                        2、注册代金券，
                                        3、活动赠送
 */
public class CouponBean implements Serializable {

    private String id;
    private String voucherId;
    private String price;
    private String oldPrice;
    private String voucherNum;
    private String cardNum;
    private CouponState state;
    private String startTime;
    private String endTime;
    private String createTime;
    private String brandName;
    private String description;
    private String voucherTypeStyle;

    public static CouponBean obtain(JSONObject jsonObject){
        CouponBean coupon=new CouponBean();
        coupon.setDescription(jsonObject.optString("description"));
        coupon.setEndTime(jsonObject.optString("end_time"));
        coupon.setPrice(jsonObject.optString("price"));
        coupon.setState(CouponBean.CouponState.fromString(jsonObject.optString("state")));
        return coupon;
    }

    public CouponBean(){

    }
    public CouponBean(String price,String description,String endTime,String num){
        this.price=price;
        this.description=description;
        this.endTime=endTime;
        this.voucherNum=num;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }

    public String getVoucherNum() {
        return voucherNum;
    }

    public void setVoucherNum(String voucherNum) {
        this.voucherNum = voucherNum;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public CouponState getState() {
        return state;
    }

    public void setState(CouponState state) {
        this.state = state;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVoucherTypeStyle() {
        return voucherTypeStyle;
    }

    public void setVoucherTypeStyle(String voucherTypeStyle) {
        this.voucherTypeStyle = voucherTypeStyle;
    }
    /**
     1,未使用;
     2,禁用,
     3过期,
     4,已使用,
     5已退券
     */
    public enum CouponState{
        UNUSED("1"),
        FORBIDEN("2"),
        OVERDUE("3"),
        USED("4"),
        INVALID("5");
        private String state;
        CouponState(String s) {
            this.state=s;
        }
        private static final Map<String, CouponState> stringToEnum = new HashMap<String, CouponState>();
        static {
            // Initialize map from constant name to enum constant
            for(CouponState merchantStatus : values()) {
                stringToEnum.put(merchantStatus.toString(), merchantStatus);
            }
        }

        public static CouponState fromString(String symbol) {
            return stringToEnum.get(symbol);
        }

        @Override
        public String toString() {
            return state;
        }
    }
}
