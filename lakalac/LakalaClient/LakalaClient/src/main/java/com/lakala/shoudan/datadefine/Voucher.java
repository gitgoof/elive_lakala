package com.lakala.shoudan.datadefine;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by linmq on 2016/6/13.
 */
public class Voucher implements Serializable{
    private String id;
    private String voucher_id;
    private String price;
    private String old_price;
    private String voucher_num;
    private String card_num;
    private StateEnum state;
    private String start_time;
    private String end_time;
    private String create_time;
    private String brand_name;
    private String Description;
    private TypeStyle voucher_type_style;

    public static Voucher obtain(JSONObject jsonObject){
        Voucher voucher = new Voucher();
        voucher.id = jsonObject.optString("id");
        voucher.voucher_id = jsonObject.optString("voucher_id");
        voucher.price = jsonObject.optString("price");
        voucher.old_price = jsonObject.optString("old_price");
        voucher.voucher_num = jsonObject.optString("voucher_num");
        voucher.card_num = jsonObject.optString("card_num");
        voucher.setState(jsonObject.optString("state"));
        voucher.start_time = jsonObject.optString("start_time");
        voucher.end_time = jsonObject.optString("end_time");
        voucher.create_time = jsonObject.optString("create_time");
        voucher.brand_name = jsonObject.optString("brand_name");
        voucher.Description = jsonObject.optString("description");
        voucher.setVoucher_type_style(jsonObject.optString("voucher_type_style"));
        return voucher;
    }

    public String getId() {
        return id;
    }

    public Voucher setId(String id) {
        this.id = id;
        return this;
    }

    public String getVoucher_id() {
        return voucher_id;
    }

    public Voucher setVoucher_id(String voucher_id) {
        this.voucher_id = voucher_id;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public Voucher setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getOld_price() {
        return old_price;
    }

    public Voucher setOld_price(String old_price) {
        this.old_price = old_price;
        return this;
    }

    public String getVoucher_num() {
        return voucher_num;
    }

    public Voucher setVoucher_num(String voucher_num) {
        this.voucher_num = voucher_num;
        return this;
    }

    public String getCard_num() {
        return card_num;
    }

    public Voucher setCard_num(String card_num) {
        this.card_num = card_num;
        return this;
    }

    public Voucher.StateEnum getStateEnum() {
        return state;
    }

    public Voucher setStateEnum(Voucher.StateEnum state) {
        this.state = state;
        return this;
    }

    public String getStart_time() {
        return start_time;
    }

    public Voucher setStart_time(String start_time) {
        this.start_time = start_time;
        return this;
    }

    public String getEnd_time() {
        return end_time;
    }

    public Voucher setEnd_time(String end_time) {
        this.end_time = end_time;
        return this;
    }

    public String getCreate_time() {
        return create_time;
    }

    public Voucher setCreate_time(String create_time) {
        this.create_time = create_time;
        return this;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public Voucher setBrand_name(String brand_name) {
        this.brand_name = brand_name;
        return this;
    }

    public String getDescription() {
        return Description;
    }

    public Voucher setDescription(String description) {
        Description = description;
        return this;
    }

    public TypeStyle getTypeStyleEnum() {
        return voucher_type_style;
    }

    public String getState() {
        return state == null?null: state.getValue();
    }

    public Voucher setState(String state) {
        this.state = StateEnum.obtainByValue(state);
        return this;
    }

    public String getVoucher_type_style() {
        return voucher_type_style == null?null:voucher_type_style.getValue();
    }

    public Voucher setVoucher_type_style(
            String typeStyle) {
        voucher_type_style = TypeStyle.obtainByValue(typeStyle);
        return this;
    }

    public enum TypeStyle{
        用户兑换("1"),
        注册代金券("2"),
        活动赠送("3");
        private String value;
        private static HashMap<String,TypeStyle> TYPE_MAP = new HashMap<String, TypeStyle>();
        static {
            for(TypeStyle state:values()){
                TYPE_MAP.put(state.getValue(),state);
            }
        }
        public static TypeStyle obtainByValue(String value){
            if(TYPE_MAP.containsKey(value)){
                return TYPE_MAP.get(value);
            }else {
                return null;
            }
        }


        TypeStyle(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum StateEnum{
        未使用("1"),
        禁用("2"),
        过期("3"),
        已使用("4"),
        已退券("5");
        private String value;
        private static HashMap<String,StateEnum> STATE_MAP = new HashMap<String, StateEnum>();
        static {
            for(StateEnum state:values()){
                STATE_MAP.put(state.getValue(),state);
            }
        }
        public static StateEnum obtainByValue(String value){
            if(STATE_MAP.containsKey(value)){
                return STATE_MAP.get(value);
            }else {
                return null;
            }
        }

        StateEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
