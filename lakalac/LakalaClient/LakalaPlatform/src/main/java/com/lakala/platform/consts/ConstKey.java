package com.lakala.platform.consts;

/**
 * Created by More on 15/1/22.
 */
public class ConstKey  {

    public static final int RESULT_BACK = 0x2316;
    public static final int RESULT_DELETE_BACK = 0x2317;
    public static final int REQUEST_ADD = 0x2318;
    public static final int RESULT_ADD_BACK = 0x2319;
    public static final int RESULT_PWD_BACK = 0x2320;
    public static final int REQUEST_BRANCH = 0x2321;
    public static final int RESULT_BRANCH  = 0x2322;
    public static final String TRANS_INFO = "trans_info";
    public static final String BANK_CARD_LIST="bank_card_list";

    public static final String SERIES = "series";

    public static final String NEW_GESTURE = "new_gesture";

//qq分享的key
    public static final String QQ_APP_ID="222222";

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
