package com.lakala.platform.request;

import android.app.Activity;

import com.lakala.core.http.HttpRequest;
import com.lakala.platform.http.BusinessRequest;

/**
 * Created by LMQ on 2015/2/10.
 */
public class RequestFactory {
    public static BusinessRequest getRequest(Activity activity, Type type) {
        BusinessRequest request = new BusinessRequest(activity);
        switch (type) {
            case B_C:
                request.setRequestURL("business/imagepay/apply/wechat");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
            case BANK_BY_CARD: {//根据银行卡号获取卡信息
                request.setRequestURL("v1.0/common/bankcard/{cardNo}/bank/{type}");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case CREDIT_CARD_INFO: {//信用卡信息
                request.setRequestURL("v1.0/common/bankcard/{cardNo}/creditTips");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case CHECK_PWD: {//验证登录密码
                request.setRequestURL("v1.0/auth/check");
                break;
            }
            case QUERY_MER_STATUS: {//商户D0状态查询
                request.setRequestURL("v1.0/business/d0/queryMerStatus");
                break;
            }
            case BANK_LIST: {
                request.setRequestURL("v1.0/dict/bank");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case BANK_LIST_CREDIT: {
                request.setRequestURL("v1.0/user/bankcard/repayment");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case BANK_LIST_TRANSFER: {
                request.setRequestURL("v1.0/user/bankcard/transfer");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case DELETE_CARD_CREDIT: {
                request.setRequestURL("v1.0/user/bankcard/repayment/{cardId}");
                request.setRequestMethod(HttpRequest.RequestMethod.DELETE);
                break;
            }
            case DELETE_MESSAGE: {
                request.setRequestURL("v1.0/sitemessage/delete");
                request.setRequestMethod(HttpRequest.RequestMethod.DELETE);
                break;
            }
            case DELETE_CARD_TRANSFER: {
                request.setRequestURL("v1.0/user/bankcard/transfer/{cardId}");
                request.setRequestMethod(HttpRequest.RequestMethod.DELETE);
                break;
            }
            case TRADE: {
                request.setRequestURL("v1.0/trade");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                request.setAutoShowProgress(true);
                break;
            }
            case FEE_TRANSFER: {
                request.setRequestURL("v1.0/businfo/transfer/fee/0");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case MOBILE_CHARGE: {
                request.setRequestURL("v1.0/user/his/mobilecharge");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                request.setAutoShowProgress(true);
                break;
            }
            case MOBILE_CHARGE_DELETE: {
                request.setRequestURL("v1.0/user/his/mobilecharge/{mobileId}");
                request.setRequestMethod(HttpRequest.RequestMethod.DELETE);
                break;
            }
            case MOBILE_CHARGE_AMOUNT: {
                request.setRequestURL("v1.0/businfo/mobilecharge/amount");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case SUMMARY: {
                request.setAutoShowProgress(false);
                request.setRequestURL("v1.0/merchant/summary");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case MERCHANT_APPLICATION: {
                request.setRequestURL("v1.0/merchant/application");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case COLLECTION_INFO: {
                request.setRequestURL("v1.0/merchant/collectionInfo");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case ADVERT: {
                request.setRequestURL("v1.0/advert/advertInfo");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case SET_PAYMENT_PWD: {
                request.setRequestURL("v1.0/user/security/paypwd");
                request.setRequestMethod(HttpRequest.RequestMethod.PUT);
                break;
            }
            case GET_SECURITY_QUESTION_LIST: {
                request.setRequestURL("v1.0/commons/security/pwdquestion");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case SET_SECURITY_QUESTION: {
                request.setRequestURL("v1.0/user/security/pwdquestion");
                request.setRequestMethod(HttpRequest.RequestMethod.PUT);
                break;
            }
            case WALLET_TRANSFER_GENERATE_BILL: {
                request.setRequestURL("v1.0/ewallet/trans/bill");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case WALLET_DETAIL_QRY: {
                request.setRequestURL("v1.0/ewallet/cash/trade");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case WALLET_TO_BANK_GENERATE_BILL: {
                request.setRequestURL("v1.0/ewallet/trans/bill");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case WALLET_RECHARGE_BILL: {
                request.setRequestURL("v1.0/ewallet/recharge/bill");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case MY_WALLET: {
                request.setRequestURL("v1.0/ewallet/user/card");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case WALLET_GET_USER_TYPE: {
                request.setRequestURL("v1.0/ewallet/user/type");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case WALLET_RECHARGE_LIMIT_QRY: {
//                request.setRequestURL("v1.0/ewallet/recharge/bill/limit");
                request.setRequestURL("v1.0/ewallet/recharge/bill/limitNew");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case WALLET_TOBANK_ACCOUNT_INFO: {
                request.setRequestURL("v1.0/ewallet/trans/card/in");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case WALLET_TO_BANK_CARD_INFO: {
                request.setRequestURL("v1.0/ewallet/trans/card/out");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case RED_PACKAGE_QRY: {
                request.setRequestURL("v1.0/ewallet/gift");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case QUERY_CARD_INFO: {
                request.setRequestURL("v1.0/ewallet/user/pocketcard");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case SUPPORTED_BUSINESS_BANK_LIST: {
                request.setRequestURL("v1.0/proxy/mpos/commons/bank");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case QUICK_SIGN: {
                request.setRequestURL("v1.0/ewallet/sign");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case QUICK_UNSIGN: {
                request.setRequestURL("v1.0/ewallet/unsign");
                request.setRequestMethod(HttpRequest.RequestMethod.DELETE);
                break;
            }
            case QUICK_SMS_SIGN: {
                request.setRequestURL("v1.0/ewallet/sign/sms");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case QUICK_SMS_UNSIGN: {
                request.setRequestURL("v1.0/ewallet/unsign/sms");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case SMALL_AMOUNT_CANCEL_PWD: {
                request.setRequestURL("v1.0/ewallet/nopasswd");
                request.setRequestMethod(HttpRequest.RequestMethod.PUT);
                break;
            }
            case QRY_USER_SECURITY_QUESTION: {
                request.setRequestURL("v1.0/user/security/pwdquestion");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case VERIFY_USER_QUESTION: {
                request.setRequestURL("v1.0/user/security/pwdquestion/check");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case WALLET_TRADE: {
                request.setRequestURL("v1.0/ewallet/trade");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case QRY_QUICK_RELEASE_CARD_BIN: {
                request.setRequestURL("v1.0/ewallet/cardbin");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case COMPREHENSIVE_INFORMATION: {
                request.setRequestURL("v1.0/user/security");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case WALLET_TRANSFER_CARD_BIN: {
                request.setRequestURL("v1.0/ewallet/cash/cardbin");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case VERIFY_PAY_PWD: {
                request.setRequestURL("v1.0/user/security/paypwd");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case MESSAGE_RESET_PAY_PWD: {
                request.setRequestURL("v1.0/user/security/sms");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case VERIFY_MESSAGE: {
                request.setRequestURL("v1.0/user/security/smsverify");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case MARKET_EFFORTS: {
                request.setRequestURL("v1.0/marketefforts");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case REAL_NAME_AHTU: {
                request.setRequestURL("v1.0/ewallet/checkRealName");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case GET_QYTZ: {
                request.setRequestURL("v1.0/ewallet/quickSkipSign");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case CHECK_QYTZ: {
                request.setRequestURL("v1.0/ewallet/queryQuickSkipSign");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case APPLY_SMS_UNSIGN: {
                request.setRequestURL("v1.0/ewallet/getRealNameAuthApplyCheckCode");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case REAL_NAME_AHTUS: {
                request.setRequestURL("v1.0/ewallet/realNameAuth");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case PARTNER_PAY: {
                request.setRequestURL("v1.0/partner/pay");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case COLLECTION_DETAIL_QUERY: {
                request.setRequestURL("v1.0/meta/MPOS_RECEIPT_TRANS_QUERY");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case LIFE_DETAIL_QUERY: {
                request.setRequestURL("v1.0/meta/MPOS_LIVE_TRANS_QUERY");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case SECURITY_fLAG: {
                request.setRequestURL("v1.0/user/security/flag");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case Loan_List_Homepage: {
                request.setRequestURL("v1.0/loan/getLoanListAndDetail");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case Loan_Apply_Check: {
                request.setRequestURL("v1.0/loan/applyCheck");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case ADD_USER_INFO: {
                request.setRequestURL("v1.0/loan/addUserInfo");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case UPLOAD_TC: {
                request.setRequestURL("v1.0/ewallet/asytranstc");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case EWALLET_PAYTYPE: {
                request.setRequestURL("v1.0/ewallet/paytype");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case LOAN_APPLY_RECORDS: {
                request.setRequestURL("v1.0/loan/getLoanApplyRecords");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case QRY_PAYSMS: {
                request.setRequestURL("v1.0/ewallet/qryPaySms");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case CHECK_PAY_PWD: {
                request.setRequestURL("v1.0/ewallet/checkPayPwd");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case QUERY_MerLevelAndLimit: {
                request.setRequestURL("v1.0/merchant/queryMerLevelAndLimit");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case MER_UPGRADE: {
                request.setRequestURL("v1.0/merchant/application/upgrade");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case UPGRADE_FIRST: {
                request.setRequestURL("v1.0/merchant/upgrade/first");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case UPGRADE_SECOND: {
                request.setRequestURL("v1.0/merchant/upgrade/second");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case UPGRADE_THIRD: {
                request.setRequestURL("v1.0/merchant/upgrade/third");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case UPDATE_STATUS: {
                request.setRequestURL("v1.0/merchant/upgrade/state");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case UPDATE_MERCHAT_BUSINESSNAME: {
                request.setRequestURL("v1.0/merchant/getMerchantStatus");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case GET_DREDGE_STATUS: {
                request.setRequestURL("v1.0/merchant/getMerchantBusinessStatus");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case UPDATE_MERCHAT_ACCOUNTNO: {
                request.setRequestURL("v1.0/merchant/getMerchantStatus");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case UPDATE_MERCHAT_INGO: {
                request.setRequestURL("v1.0/merchant/updateMerchantInfo");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case COUPON_LIST: {
                request.setRequestURL("v1.0/points/getVoucherList");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case COUPIN_PAY: {
                request.setRequestURL("");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case COUPON_FEE: {
                request.setRequestURL("v1.0/points/getFee");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case COUPON_STATUS: {
                request.setRequestURL("v1.0/ewallet/qryPaySms");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case POINTS_FIRST: {
                request.setRequestURL("v1.0/points/firstEnter");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case POINTS_GET_CODE: {
                request.setRequestURL("v1.0/points/getCode");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case POINTS_CHECK_CODE: {
                request.setRequestURL("v1.0/points/checkCode");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }
            case VOUCHER_EXPLAIN: {
                request.setRequestURL("v1.0/points/getVoucherExplain");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case CASH_RECORDLIST: {
                request.setRequestURL("v1.0/points/getCashRecordList");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case MERCHANT_LOCATION: {
                request.setRequestURL("v1.0/user/location/getClientLocationInfo");
                request.setRequestMethod(HttpRequest.RequestMethod.GET);
                break;
            }
            case COUPON_TRADE: {
                request.setRequestURL("v1.0/points/trade");
                request.setRequestMethod(HttpRequest.RequestMethod.POST);
                break;
            }

        }
        return request;
    }

    public enum Type {
        /**
         * 根据银行卡号获取卡信息（信用卡还款）
         */
        BANK_BY_CARD,
        /**
         * 删除信用卡还款记录
         */
        DELETE_CARD_CREDIT,
        /**
         * 删除银行卡信息-转账汇款
         */
        DELETE_CARD_TRANSFER,
        /**
         * 信用卡信息
         */
        CREDIT_CARD_INFO,
        CHECK_PWD,
        /**
         * 获取银行列表
         */
        BANK_LIST,
        /**
         * 查询收款记录
         */
        COLLECTION_DETAIL_QUERY,
        /**
         * 生活交易查询
         */
        LIFE_DETAIL_QUERY,
        /**
         * 查询历史还款记录-信用卡还款
         */
        BANK_LIST_CREDIT,
        /**
         * 常用收款人-转账汇款
         */
        BANK_LIST_TRANSFER,
        /**
         * 基础交易接口
         */
        TRADE,
        /**
         * 转账汇款手续费
         */
        FEE_TRANSFER,
        /**
         * 手机充值历史
         */
        MOBILE_CHARGE,
        /**
         * 删除手机充值历史
         */
        MOBILE_CHARGE_DELETE,
        /**
         * 手机充值面额
         */
        MOBILE_CHARGE_AMOUNT,
        /**
         * 商户信息摘要
         */
        SUMMARY,
        /**
         * 申请单查询
         */
        MERCHANT_APPLICATION,
        /**
         * 今日收款金额
         */
        COLLECTION_INFO,
        /**
         * 商户D0状态查询
         */
        QUERY_MER_STATUS,
        /**
         * 广告轮播
         */
        ADVERT,
        /**
         * 设置和修改支付密码
         */
        SET_PAYMENT_PWD,
        /**
         * 查询密保问题列表
         */
        GET_SECURITY_QUESTION_LIST,
        /***
         * 设置用户密保问题
         */
        SET_SECURITY_QUESTION,
        /**
         * 提现卡bin信息查询
         */
        WALLET_TRANSFER_CARD_BIN,
        /**
         * 生成钱包提现账单
         */
        WALLET_TRANSFER_GENERATE_BILL,
        /**
         * 钱包充值生成账单
         */
        WALLET_RECHARGE_BILL,
        /**
         * 钱包交易
         */
        WALLET_TRADE,
        /**
         * 异步上送TC
         */
        UPLOAD_TC,
        /**
         * 钱包明细查询
         */
        WALLET_DETAIL_QRY,
        /**
         * 钱包转出到银行卡生成账单
         */
        WALLET_TO_BANK_GENERATE_BILL,
        /**
         * 我的银行卡
         */
        MY_WALLET,
        /**
         * @create time:2017.1.4
         * @author:chenss
         * TODO:获取用户级别(此处区别于商户级别) 作用于理财，一块夺宝，钱包的话术是否显示
         */
        WALLET_GET_USER_TYPE,
        /**
         * 查询充值限额
         */
        WALLET_RECHARGE_LIMIT_QRY,
        /**
         * 钱包转出到银行卡查询收款人信息
         */
        WALLET_TOBANK_ACCOUNT_INFO,
        /**
         * 钱包转出到银行卡查询输入卡信息
         */
        WALLET_TO_BANK_CARD_INFO,
        /**
         * 查询红包信息
         */
        RED_PACKAGE_QRY,
        /**
         * 支持业务银行列表
         */
        SUPPORTED_BUSINESS_BANK_LIST,
        /**
         * 查询零钱卡信息
         */
        QUERY_CARD_INFO,
        /**
         * 快捷签约
         */
        QUICK_SIGN,
        /**
         * 快捷解约
         */
        QUICK_UNSIGN,
        /**
         * 快捷签约验证码
         */
        QUICK_SMS_SIGN,
        /**
         * 获取快捷解约验证码
         */
        QUICK_SMS_UNSIGN,
        /**
         * 小额免密
         */
        SMALL_AMOUNT_CANCEL_PWD,
        /**
         * 查询用户密保问题
         */
        QRY_USER_SECURITY_QUESTION,
        /**
         * 校验用户密保问题
         */
        VERIFY_USER_QUESTION,
        /**
         * 查询快捷卡bin
         */
        QRY_QUICK_RELEASE_CARD_BIN,
        /**
         * 获取用户综合安全信息
         */
        COMPREHENSIVE_INFORMATION,
        /**
         * 校验支付密码
         */
        VERIFY_PAY_PWD,
        /**
         * 重置支付密码短信验证（获取）
         */
        MESSAGE_RESET_PAY_PWD,
        /**
         * 验证验证码
         */
        VERIFY_MESSAGE,
        /**
         * 获取市场获取
         */
        MARKET_EFFORTS,
        /**
         * 钱包实名认证校验
         */
        REAL_NAME_AHTU,
        /**
         * .实名认证快捷跳转签约
         */
        GET_QYTZ,
        /**
         * .1.1.5.快捷跳转签约查询
         */
        CHECK_QYTZ,
        /**
         * .1.1.3.零钱银行卡实名认证
         */
        REAL_NAME_AHTUS,
        /**
         * .1.1.5.快捷跳转签约查询
         */
        APPLY_SMS_UNSIGN,
        /**
         * /user/security/flag
         */
        SECURITY_fLAG,
        /**
         * 贷款列表及详情
         */
        Loan_List_Homepage,
        /**
         * 4.	贷款“马上申请”校验
         */
        Loan_Apply_Check,
        /**
         * 4.1.	用户信息完善
         */
        ADD_USER_INFO,
        /**
         * 商户支付信息解密
         */
        PARTNER_PAY,
        /**
         * 支付方式
         */
        EWALLET_PAYTYPE,
        /**
         * 3.	我的贷款记录
         */
        LOAN_APPLY_RECORDS,
        /**
         * 快捷购买短信验证码获取
         */
        QRY_PAYSMS,
        /**
         * 校验支付密码
         */
        CHECK_PAY_PWD,
        /**
         * 4.3.12.商户等级与额度查询
         */
        QUERY_MerLevelAndLimit,
        /**
         * 商户升级
         */
        MER_UPGRADE,
        /**
         * 商户一类升级
         */
        UPGRADE_FIRST,
        /**
         * 商户二类升级
         */
        UPGRADE_SECOND,
        /**
         * 跑分升级
         */
        UPGRADE_THIRD,
        /**
         * 升级状态
         */
        UPDATE_STATUS,
        /**
         * 用户管理状态
         */
        UPDATE_MERCHAT_BUSINESSNAME,
        /**
         * 获取业务开通状态
         */
        GET_DREDGE_STATUS,
        /**
         * 用户管理状态
         */
        UPDATE_MERCHAT_ACCOUNTNO,
        /**
         * 用户信息修改
         */
        UPDATE_MERCHAT_INGO,
        /**
         * 积分购-获取用户代金券列表
         */
        COUPON_LIST,
        /**
         * 代金券收款
         */
        COUPIN_PAY,
        /**
         * 代金券收款手续费
         */
        COUPON_FEE,
        /**
         * 代金券状态
         */
        COUPON_STATUS,
        /**
         * 是否首次进入积分购
         */
        POINTS_FIRST,
        /**
         * 积分购-申请短信
         */
        POINTS_GET_CODE,
        /**
         * 积分购-验证短信
         */
        POINTS_CHECK_CODE,
        /**
         * 积分购-查询积分及兑换信息
         */
        VOUCHER_EXPLAIN,
        /**
         * 积分购-账单明细
         */
        CASH_RECORDLIST,
        /**
         * 经纬度定位
         */
        MERCHANT_LOCATION,
        /**
         * 代金券消费交易
         */
        COUPON_TRADE,
        //B扫C交易
        B_C,


        //删除消息
        DELETE_MESSAGE;
    }
}
