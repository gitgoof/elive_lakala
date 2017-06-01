package com.lakala.platform.swiper.devicemanager.controller;

import java.io.Serializable;

/**
 * Created by LMQ on 2015/12/3.
 */
public enum TransactionType implements Serializable{

    Collection("收款","18X", true, true, true), // 收款
    Query("余额查询","M30000", true, false, false), // 余额查询
    Revocation("撤销","18Y", true, true, true), // 撤销
    ReardCard("读卡","X10000", true, true, true), // 读卡
    ScanRevocation("扫码撤销","W00003",true,  false, false),//扫码撤销
//    Register("注册", "", true, false),//注册
    RevocationAgain("撤销","18Y", true, true, true),// 再次撤销
    Remittance("转账汇款","M50010", true, false, false),//转账汇款
    CreditCardPayment("信用卡还款","M50001", true, false, false),//信用卡还款
    MobileRecharge("手机充值","M20020", true, false, false),//手机充值
    WebMall("社区商城","M50005", true, false, false),//社区商城
    PaymentForCall("收款","", true, false, false),
    RepayForYou("替你还","800003", true, false, false),
    ContributePayment("新疆福彩缴费","1CH", false, false, false),//新疆福彩缴款
    RentCollection("平安收银宝","1F2", true, false, false),//平安收租
    PRIVILEGEPURCHASE("专享购买","", true, false, false),//TODO 动态的busid
    TREASURE("一块夺宝","",true,false, false),
    LARGE_AMOUNT_COLLECTION("大额收款","X00001", true, true, true),
    BARCODE_COLLECTION("扫码收款","W00001", true, false, false),
    WITHDRAW("取出","1G3", true, false, false),
    WALLET_RECHARGE("零钱充值","800003", true, false, false),
    WALLET_TRANSFER("零钱转出","800005", true, false, false),
    APPLY("理财买入","1G2", true, false, false),
    HAPPYBEAN("欢乐豆充值","", true, false, false),
    COUPON("代金券收款","",true,false, false),
    PRIVILEGEPURCHASEE("付款","", true, false, false);//TODO 动态的busid

    //下发到设备的交易名称
    private String value;
    private Class mainClazz;
    private boolean supportQPBOC;

    public TransactionType setValue(String value) {
        this.value = value;
        return this;
    }

    TransactionType(String value, String busId, boolean supportIc, boolean newCommandProtocol, boolean supportQPBOC){
        this.value = value;
        this.busId = busId;
        this.newCommandProtocol = newCommandProtocol;
        this.supportIc = supportIc;
        this.supportQPBOC = supportQPBOC;
    }

    public String getValue(){
        return this.value;
    }

    /**
     * 请求业务对应的交易的BusinessId
     */
    private String busId;

    public String getBusId(){
        return busId;
    }

    /**
     * 该业务支持新流程
     */
    private boolean newCommandProtocol;

    public boolean isNewCommandProtocol() {
        return newCommandProtocol;
    }

    /**
     *
     */
    private boolean supportIc;

    public boolean isSupportIc() {
        return supportIc;
    }

    public Class getMainClazz() {
        return mainClazz;
    }

    public TransactionType setMainClazz(Class mainClazz) {
        this.mainClazz = mainClazz;
        return this;
    }

    public boolean isSupportQPBOC() {
        return supportQPBOC;
    }
}
