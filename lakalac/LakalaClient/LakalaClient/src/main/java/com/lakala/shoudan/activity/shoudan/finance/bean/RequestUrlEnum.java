package com.lakala.shoudan.activity.shoudan.finance.bean;

/**
 * Created by More on 15/9/2.
 *
 * 理财业务请求地址枚举
 *
 */
public enum RequestUrlEnum {

    STATE_QUERY("common/myFavoriteQry.do"),
    FUND_TOTAL("fund/fundTotalQry.do"),
    PRODUCT_LIST_QUERY("fund/fundProdListQry.do"),
    FUND_SIGN_UP("fund/fundSignUp.do",true),
    QUERY_OTP_PASSWORD("fun/queryOTPPassword.do",true),
    FUND_TRADELIST_QRY("fund/fundTradeListQry.do"),
    SHORT_CUT_SIGN("fund/shortcutSign.do",true),
    FUND_STATE_QRY("fund/fundStateQry.do",true),
    FUND_CONTLIST_QRY("fund/fundContListQry.do",true),
    SET_PAY_PWD("setting/setPayPwd.do",true),
    QUESTION_LIST_QRY("questionListQry.do"),
    USER_QUESTION_QRY("userQuestionQry.do"),
    ADD_USER_QUESTION("addUserQuestion.do"),
    VERIFY_QUESTION("verifyQuestion.do"),
    QUERY_FUND_RECORD("fund/fundTradeListQry.do"),
    CHECK_PAY_PWD("checkPayPwd.do",true),
    DO_PQY("common/doPay.do",true),
    FUND_PROD_LIST("fund/fundValidateProdListQry.do"),
    QRY_BUSI_BANKS("common/qryBusiBanks.do"),
    FUND_TRANSFERIN_MMT("fundTransferInMmt.do",true),
    INIT_FUND_TRANSFER_CARD("initFundTransferCard.do",true),
    FUND_TRANSFER_CARD_BILL("fundTransferCardBill.do"),
    QUERY_PAY("common/queryPay.do",true),
    QUERY_LINE_NO("queryLineNo.do"),
    FUND_WEEK_INCOMEQRY("fundWeekIncomeQry.do"),
    FUND_PROD_DETAIL_QRY("fundProdDetailQry.do"),
    COUNT_EARN_DATE("countEarnDate.do"),
    GET_SMS_CODE("fund/getSMSCode.do"),
    QUERY_WALLET_NO("queryWalletNo.do"),
    VERIFY_SMS_CODE("fund/verifySMSCode.do"),
    ASY_TRANS_TC("fund/asyTransTc.do",true),//异步TC
    SNC_TRANS_TC("fund/sycTransTc.do",true),
    CARD_QRY("lcardBinQry/cardQry.do");

    private String value;
    private boolean needMac = false;

    RequestUrlEnum(String value) {
        this.value = value;
    }
    RequestUrlEnum(String value,boolean needMac){
        this.value = value;
        this.needMac = needMac;
    }

    public String value(){
        return value;
    }

    public boolean isNeedMac() {
        return needMac;
    }
}
