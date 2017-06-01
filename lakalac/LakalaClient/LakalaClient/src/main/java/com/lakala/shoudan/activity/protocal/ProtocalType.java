package com.lakala.shoudan.activity.protocal;

/**
 * Created by LMQ on 2015/11/20.
 */
public enum ProtocalType {
    NONE,
    SERVICE_PROTOCAL("拉卡拉收款宝服务协议", ProtocalUrl.LAKALA_USER),//拉卡拉服务协议
    WALLET_PROTOCAL("钱包服务协议", ProtocalUrl.WALLET_SERVICE),//钱包协议
    WALLET_DESCRIPTION,             //钱包说明
    MORE_HELP_PAGE("使用帮助", ProtocalUrl.SHOU_KUAN_BAO_URL),//更多帮助页面
    BIG_AMOUNT_REMITP_ROTOCAL("服务协议", ProtocalUrl.BIG_AMOUNT_REMIT_PROTOCAL_URL),//大额转账协议
    MOVIE_BUG_TICKET_HELP("购票帮助", ProtocalUrl.MOVIE_BUY_TICKET_HELP_URL),//电影票购票帮助
    HONG_BAO_USE_PROTOCAL("红包使用规则", ProtocalUrl.HONG_BAO_USE_PROTOCAL),//红包使用规则说明
    ZHUAN_ZHANG_FEE_RULE("收费规则", ProtocalUrl.ZHUAN_ZHANG_FEE_RULE),//转账收费规则
    MOBILE_REMITTANCE_MORE_PROTOCAL("手机号汇款", ProtocalUrl.MOBILE_REMITTANCE_MORE_PROTOCAL),//手机号汇款更多说明
    MOBILE_REMITTANCE_PROTOCAL("手机号汇款", ProtocalUrl.MOBILE_REMITTANCE_PROTOCAL),//手机号汇款服务协议
    NOCARD_REPAYMENT_PROTOCAL("无卡还款服务协议", ProtocalUrl.NOCARD_REPAYMENT_PROTOCAL),//无卡还款协议
    SHOU_KUAN_BAO_PROTOCAL("收款宝业务合作协议", ProtocalUrl.SHOU_KUAN_BAO_PROTOCAL),//收款宝服务协议
    BUY_LAKALA_SWIPE("购买拉卡拉手机收款宝", ProtocalUrl.BUY_LAKALA_SWIPE),//购买拉卡拉刷卡器
    JOIN_LAKALA("招商加盟", ProtocalUrl.JOIN_LAKALA),
    SHOUDAN_RATE_PROTOCAL("收费规则", ProtocalUrl.SHOUDAN_RATE_PROTOCAL),//收单费率
    TRANSACTION_RULES("收款宝交易规则", ProtocalUrl.TRANSACTION_RULES),            //收款宝交易规则
    ERWEIMA_RULES("扫码收款", ProtocalUrl.ERWEIMA_RULE),            //二维码收款帮助页面
    LEVEL_RULES("级别说明", ProtocalUrl.LEVEL_RULES),            //级别说明页面
    ELECTRONIC_SIGNATURE_HELP("收款-签名说明", ProtocalUrl.ELECTRONIC_SIGNATURE_HELP),      //电子签名帮助
    COLLECTION_CANCEL_HELP("撤销-使用帮助", ProtocalUrl.COLLECTION_CANCEL_HELP),         //撤销帮助*
    CONNECTION_HELP("连接帮助", ProtocalUrl.CONNECTION_HELP),                //连接帮助
    FOLLOW_LAKALA_HELP("关注拉卡拉帮助", ProtocalUrl.FOLLOW_LAKALA_HELP),                //关注拉卡拉帮助
    TEYUEJIAOFEI_PRO("特约商户缴费服务协议", ProtocalUrl.TEYUEJIAOFEI_PRO),//特约缴款服务协议

//    HELP_MERCHANT_COLLECTION("使用帮助",ProtocalUrl.HELP_MERCHANT_COLLECTION),//商户收款
//    HELP_CANCEL_TRANS("使用帮助",ProtocalUrl.HELP_CANCEL_TRANS),//撤销交易
//    HELP_CREDIT_CARD_BACK("使用帮助",ProtocalUrl.HELP_CREDIT_CARD_BACK),//信用卡还款
//    HELP_TRANSFER("使用帮助",ProtocalUrl.HELP_TRANSFER),//转账汇款
//    HELP_PRODUCT_ADAPTER("使用帮助",ProtocalUrl.HELP_PRODUCT_ADAPTER),//产品适配
//    HELP_PRODUCT_CON("使用帮助",ProtocalUrl.HELP_PRODUCT_CON),//产品链接
//    HELP_REGISTER_OPEN("使用帮助",ProtocalUrl.HELP_REGISTER_OPEN),//注册开通
//    HELP_ABOUT_SWIP("使用帮助",ProtocalUrl.HELP_ABOUT_SWIP),//关于刷卡
//    HELP_TRANS_SAFE("使用帮助",ProtocalUrl.HELP_TRANS_SAFE),//交易安全
//    HELP_INFO_SHENHE_UPDATE("使用帮助",ProtocalUrl.HELP_INFO_SHENHE_UPDATE),//资料审核与修改
//    HELP_MERCHANT_UP("使用帮助",ProtocalUrl.HELP_MERCHANT_UP),//商户级别升级
//    HELP_PRODUCT_SERVICE("使用帮助",ProtocalUrl.HELP_PRODUCT_SERVICE),//产品售后

    SETTLEMENT_MERCHANT_CHANGE("结算账户变更协议", ProtocalUrl.SETTLEMENT_MERCHANT_CHANGE),//结算账户变更协议
    NIGHT_HELP("使用帮助", ProtocalUrl.NIGHT_HELP),

    LOAN_HELP("替你还业务说明", ProtocalUrl.LOAN_HELP),
    LOAN_BANK_STORE("还款委托协议", ProtocalUrl.LOAN_BANK_STORE),
    LOAN_APPLY("业务办理协议", ProtocalUrl.LOAN_APPLY),
    D0_DESCRIPTION("业务说明", ProtocalUrl.D0_DESCRIPTION),//D0业务说明
    ONE_DAY_LOAN_NOTE("一日贷业务说明", ProtocalUrl.OnDayLoanNote),//一日贷业务说明
    D0_SERVICE("服务协议", ProtocalUrl.D0_SERVICE),
    ONE_DAY_SERVICE("拉卡拉一日贷服务协议", ProtocalUrl.OnDayLoanApply),

//    MESSAGE_DETAIL("使用帮助",ProtocalUrl.MESSAGE_DETAIL),

    //拉卡拉收款宝业务协议(注册)
    COOPERATION_AGREEMENT("拉卡拉收款宝业务合作协议", ProtocalUrl.COOPERATION_AGREEMENT),
    //大额收款服务协议
    LARGE_AMOUNT_COOPERATION_AGREEMENT("拉卡拉大额收款服务协议", ProtocalUrl.LARGE_AMOUNT_COOPERATION_AGREEMENT),
    SERVICE_WX("拉卡拉扫码收款服务协议", ProtocalUrl.SERVICE_WX),

    LARGE_AMOUNT_RULES("大额收款业务说明", ProtocalUrl.LARGE_AMOUNT_RULES),

    MERCHANT_CER_CAPTRUE("开户单样例", ProtocalUrl.MERCHANT_CER_CAPTRUE),

    SALES_CER_CAPTURE("销售凭证样例", ProtocalUrl.SALES_CER_CAPTURE),
    SCAN_COLLECTION_PROTOCOL("扫码收款", ProtocalUrl.SCAN_COLLECTION_PROTOCOL),
    SCAN_REVOCATION_PROTOCOL("帮助", ProtocalUrl.SCAN_REVOCATION_PROTOCOL),
    /**
     * 服务协议
     **/
    USER("收款宝服务协议", ProtocalUrl.USER),
    SHOU_KUAN_BAO_SERVICE_URL("收款宝业务合作协议", ProtocalUrl.SHOU_KUAN_BAO_SERVICE_URL),

    ALL_LCKH("拉卡拉代收服务协议", ProtocalUrl.ALL_LCKH),
    LC_JXJJXY("建信基金服务协议", ProtocalUrl.LC2_ALL_SG),
    LC_DSXY("拉卡拉代收服务协议", ProtocalUrl.LC_DSXY),
    LC_QYKZFXE("签约卡支付限额列表", ProtocalUrl.LC_QYKZFXE),
    RULES("取出规则", ProtocalUrl.RULES),
    LC_ALLNOTE("考拉理财总体介绍", ProtocalUrl.LC_ALLNOTE),
    LC_LC1NOTE("业务规则说明", ProtocalUrl.LC_LC1NOTE),
    LC_LC2NOTE("业务规则说明", ProtocalUrl.LC_LC2NOTE),
    LC_LC3NOTE("业务规则说明", ProtocalUrl.LC_LC3NOTE),
    OPEN_RIGHT_NOW("银联用户系统", ProtocalUrl.OPEN_RIGHT_NOW),
    BARCODE_PROTOCAL("服务协议", ProtocalUrl.SERVICE_WX),
    RED_PACKAGE_RULE("红包使用规则说明", ProtocalUrl.RED_PACKAGE_RULE),
    QUICK_PAY_SERVICE("快捷支付服务协议", ProtocalUrl.QUICK_PAY_SERVICE),
    REAL_NAME_AUTH_SERVICE("服务协议", ProtocalUrl.REAL_NAME_AUTH_SERVICE),
    LKL_QUICK_PAY_SERVICE("拉卡拉银行卡支付服务协议", ProtocalUrl.LKL_QUICK_PAY_SERVICE),
    LC1_ALL_SG("考拉1号产品服务及认购协议", ProtocalUrl.LC1_All_SG),
    LC2_ALL_SG("考拉2号产品服务及认购协议", ProtocalUrl.LC2_ALL_SG),
    LC3_ALL_SG("考拉3号产品服务及认购协议", ProtocalUrl.LC3_All_SG),
    IMMEDIATE_WITHDRAWAL_DESCRIPTION("业务说明", ProtocalUrl.IMMEDIATE_WITHDRAWAL_DESCRIPTION),
    MERCHANT_UPGRADE_DESCRIPTION("商户升级说明", ProtocalUrl.MERCHANT_UPGRADE_DESCRIPTION),
    MERCHANT_QUOTA_DESCRIPTION("商户额度说明", ProtocalUrl.MERCHANT_QUOTA_DESCRIPTION),
    GPS_PERMISSION("商户开通", ProtocalUrl.GPS),
    COUPON_PROTOCAL("代金券使用说明", ProtocalUrl.COUPON_PROTOCAL),
    CUSTOM_PROTOCAL;//自定义title和url
    String title;
    String url;

    ProtocalType(String title, String url) {
        this.title = title;
        this.url = url;
    }

    ProtocalType() {
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
