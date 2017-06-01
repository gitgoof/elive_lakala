package com.lakala.shoudan.datadefine;

public class Constants {

	/**
	 * 
	 * Intent Key's Value Class
	 * @author More
	 *
	 */
	public static class IntentKey{

        /**
         * 交易金额
         */
		public static final String AMOUNT_INTENT_KEY = "Intent_Amount";
        /**
         * 钱包初始化信息
         */
		public static final String WALLET_INFO = "wallet_info";

        /**
         * 钱包充值限额信息
         */
        public static final String WALLET_RECHARGE_LIMIT = "limit_info";
		/**
		 * 划款金额
		 */
		public static final String TRANSFER_INTENT_KEY="Intent_Transfer_Money";

        /**
         * pin
         */
		public static final String PIN_INTENT_KEY = "Intent_Pin_Key";

        /**
         * 商户名
         */
		public static final String MRCH_NAME_KEY = "Intent_Merchant_Name_key";

        /**
         * 交易结果码
         */
		public static final String RESULT_CODE = "Result_Code";

        /**
         * 错误原因
         */
		public static final String ERROR = "Error";

        /**
         * 收款记录单笔详情
         */
		public static final String RECORD_DETAL = "Record_detail";

        /**
         * 用户密码
         */
        public static final String PASSWORD = "Password";

        /**
         * 用户信息
         */
        public static final String MERCHANT_INFO = "Merchant_info";

        /**
         * 用户信息修改
         */
        public static final String MODIFY_ACCOUNTINFO = "Modify_accountInfo";

        /**
         * 收单详情 key
         */
        public static  final String REVOCATION ="Revocation";

        /**
         * 撤销上送数据
         */
        public static  final String REVOCATION_SUBMIT ="Revocation_Submit";

        /**
         * 刷卡pin相关信息
         */
        public static  final String SWIP_INFO ="Swip_Info";

        /**
         * 交易结果
         */
        public static  final String TRANS_RESULT = "Trans_Result";

        /**
         * 手机号
         */
        public static  final String PHONE_NUM = "Phone_Num";

        /**
         * 卡类型(信用卡/借记卡)
         */
        public static  final String CARD_TYPE = "Card_type";

        /**
         * 可撤销交易记录
         */
        public static final String CANCELABLE_RECORDS = "Cancelable_Records";


        /**
         * 还款卡号
         */
        public static final String CREDITCARD_PAYMENT = "creditcard_payment";

        /**
         * 转账汇款上送信息
         */
        public static final String REMITTANCE_INFO = "remittance info";
        /**
         * 交易信息保存
         */
        public static final String TRANS_INFO = "trans_info";
        /**
         *
         */
        public static final String INCOME_INFO="income_info";
        /**
         * 理财状态
         */
        public static final String  TRANS_STATE="trans_state";

        public static final String CLEAR_ALL = "clear_all";

        /**
         * 输入金额的类型(收款/缴费)
         */
        public static final String INPUTAMMOUNT_TYPE = "inputamount_type";
        
        /**
         * 替你还是否来自信用卡还款界面
         */
        public static final String BACK_SHOW_INFO = "backShowInfo";
        
        public static final String FAILED_REASON = "failed_reason";
        
        /**
         * 商户升档  商户级别
         */
        public static final String MERCHANT_LEVEL = "merchant_level";

    }


    /**
	 * 
	 * 交易结果
	 * 
	 * @author More
	 *
	 */
	public static class ResultCode{

        /**
         * 成功
         */
		public static final int SUCCESS =  1;

        /**
         * 失败
         */
		public static final int FAIL = -1;

        /**
         * 超时
         */
		public static final int TIMEOUT= 0;

        /**
         * 未知错误
         */
        public static final int UNKNOWN = 2;
		
	}

    public static class SPConstants{

        public static final String isLphone = "is_lphone";

        public static final int FIRSTINIT = -1;

        public static final int IS_LPHONE = 0;

        public static final int NOT_LPHONE = 1;

    }
    
    public static class InputAmountType {

    	public static final int COLLECTION_AMOUNT = 1;//收款
    	
    	public static final int MERCHANT_PAY = 2;//特约商户缴费

    }
    // 新浪微博key
    public static final String APP_KEY = "2694955114";

    //新浪微博REDIRECT_URL
    public static final String REDIRECT_URL = "http://www.sina.com";
    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     *
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     *
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     *
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    // 微信key
    public static final String WeChat_APP_KEY = "wx043bb4af89ad12af";
}
