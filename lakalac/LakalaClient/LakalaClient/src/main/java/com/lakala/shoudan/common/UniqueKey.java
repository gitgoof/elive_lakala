package com.lakala.shoudan.common;

import com.lakala.platform.common.ApplicationEx;

/**
 * Created by HUASHO on 2015/1/27.
 * 定义intent跳转过程用到的key值
 */
public class UniqueKey {

    public final static String  ZHUANGZHANG_ID = "M50010";//转账汇款业务代码
    public final static String  HUANDAIKUAN_ID="168";//个人还贷	业务代码
    public final static String  XINYONGKA_ID="M50001";//信用卡还款业务代码
    public final static String  WOYAOSHOUKUAN_ID="17L";//	我要收款	业务代码
    public final static String DAEKAITONGOPEN_ID = "M20021";//大额开通开通接口业务Id
    public final static String  QIANYUEKA_XYK_ID="18I";//信用卡还款业务代码
    public final static String  QIANYUEKA_ID="1eh";//签约卡业务代码


    public static final String CREDIT_CARD_NUMBER = "credit_card_number";//信用卡卡号
    public final static String PHONE_NUMBER = "phone_number";//手机号码
    public final static String SELECT_BANK = "select_bank";//选择银行
    public static final String KEY_CARDINFO = "cardInfo";//信用卡信息
    public static final String TRANS_INFO = "trans_info";//交易信息保存

    public final static int BANK_LIST_CODE = 0x999;//选择银行传递码
    public final static int BANK_HISTORY_CODE = 0x888;//手输获取收款账号传递码
    public final static int BANK_INPUT_CODE = 0x666;//历史获取收款账号传递码
    public final static int	CARD_NUMBER_REQUEST_CODE = 1;//返回卡号的requestCode
    public final static int	PHONE_NUMBER_REQUEST_CODE = 2;//返回电话号码的requestCode

    public final static String STATUS_BAR_HEIGHT = "series";


    //Shoudan

    public static String getMerchantPaymentOpenStatus(){

        return ApplicationEx.getInstance().getUser().getLoginName() + "if_open";
    }


    public final static String announcement		= "announcement";


    /** 单笔充值限额*/
    public final static String PrepaidTypeQBCZXE	= "QBCZXE";
    /** 钱包转账限额 */
    public final static String PrepaidTypeQBZZXE	= "QBZZXE";
    /** 钱包提现限额 */
    public final static String PrepaidTypeQBTXXE	= "QBTXXE";
    /** 充值页面提醒 */
    public final static String PrepaidTypeQBSMPZ	= "QBSMPZ";


    //返回卡号的requestCode
    public final static int	cardNumberRequestCode = 1;
    //返回电话号码的requestCode
    public final static int	phoneNumberRequestCode = 2;


    //账单号账户
    public final static String saoyisaoZhangDanHao = "saoyisaoZhangdanhao";
    public final static String saoyisaoZhangDanHaofroId = "saoyisaoZhangdanhaoforid";
    //是否从收账记录进入
    public final static String isFromRecord  = "isFromRecord";
    //是否从找回支付密码进入
    public final static String isFromBackPayPWD  = "isFromBackPayPWD";


    /**手机号*/
    public final static String	phoneNumber = "phoneNumber";

    /**   姓名  */
    public final static String	name = "name";


    /**   获取开户行code        */
    public final static String 	bankCode="bankCode";
    //交易结束页面消息标题
    public final static String	tranFinaleTitle = "tranFinaleTitle";

    //交易结束页面消息内容
    public final static String	tranFinaleContent = "tranFinaleContent";

    //扫一扫清空历史堆栈
    public final static String	saoyisaoClearTop = "com.lakala.shoudan.saoyisao.cleartop";

    /** 是否是首页 */
    public final static String	wizardShowFlag = "wizardShowFlag";

    /**   选择银行          */
    public final static String  selectBank="selectBank";


    /**   选择银行传递码          */
    public final static int 	bankListCode=0x999;

    /**  查询交易类型*/
    public final static String  queryTranCategory="queryTranCategory";


    /** 首页引导页是否显示  */
    public final static String	wizardForApp = "wizardForApp";
    /** 支付类引导页是否显示 */
    public final static String	wizardForPay = "wizardForPay";

    //用户名
    public final static String userName= "userName";

    /**最新更新主题后的软件版本号	*/
    public final static String updateVersionCode= "updateVersionCode";

    // 推送是否开启键
    public final static String pushEnable = "pushEnable";
    // 推荐时间段键值,对应的value 是小时
    public final static String pushTimeHour = "pushTimeHour";
    // 已经完成的推送的时间
    public final static String pushedDateTime = "pushedDateTime";

    /**	业务代码	*/
    public final static String  busid= "busid";
    /**	信用卡还款业务代码*/
    public final static String  xinyongkaId="M50001";
    /**	签约卡业务代码*/
    public final static String  qyuekaId="1eh";
    /**	信用卡还款业务代码*/
    public final static String  qianyueka_xyk="18I";
    /**	转账汇款	业务代码*/
    public final static String  zhuanzhangId="M50010";
    /**	转账汇款	业务代码*/
    public final static String  zhuanzhangyinhangka="300007";
    /**	我要收款	业务代码*/
    public final static String  woYaoShouKuanId="17L";
    /**	手机充值	业务代码*/
    public final static String  mobileChargeId="M20020";
    /**	账单号付款业务代码*/
    public final static String  zhangdanId="M50005";
    /**	支付宝交易号付款	业务代码*/
    public final static String  alipayCodeId="M50002";
    /**	财付通充值业务代码*/
    public final static String  tenpayId="M50004";
    /**	支付宝充值码业务代码*/
    public final static String  alipaychargeId="M50003";
    /**	游戏充值卡业务代码*/
    public final static String  youxiId="M50007";
    /**	账户直充	业务代码*/
    public final static String  zhichongId="M50006";
    /**	公益捐款	业务代码*/
    public final static String  gongyiId="M50008";
    /**	个人还贷	业务代码*/
    public final static String  huanDaiKuaniId="168";
    /** 账单分期 业务代码 */
    public final static String zhangdanfeiqiId = "16X";

    /** 收单业务代码
     * 手机收单  18X_1 对私  18X_2_1 对公
     * 获取银行列表的参数 */
    public final static String individualId = "18X";

    public final static String companyId = "18X_2";

    /** 账单分期 业务代码---用于账单分期跳转到“稍后还款---立即还款”界面 */
    public final static String zhangdanfeiqiforDredgeId = "zhangdanfenqifordredge";
    /** 电影票  业务代码 */
    public final static String dianYingPiao = "test_dianYingPiaoBussiness";
    /**	公缴 通信费*/
    public final static String  gongjiaoId="gongjiao";

    /**	CBF*/
    public final static String  cbf_fromId="cbf_fromid";
    public final static String  cbf_Action="cbf_Action";
    /**	保险理财*/
    public final static String  baoxianlicaiId_query="174";
    public final static String  baoxianlicaiId_pay="173";
    /** 交通违章 */
    public final static String  jiaotongweizhang="172";

    /**行业卡*/
    public final static String  hangyeka = "22222222222" ;//测试
    /**红包使用规则id*/
    public final static String HONGBAOGUIZE = "M99999";


    /** 登陆 */
    public final static String dengluId = "LOGIN";

    /** 大额开通开通接口业务Id */
    public final static String daEKaiTongOpenId = "M20021";
    /**钱包充值*/
    public final static String qianbaochongzhiId = "800003";
    /**钱包转账-到钱包*/
    public final static String qianbaozhuanzhangId = "800004";
    /**钱包转账-提现*/
    public final static String tixianId = "800005";

    /**收款宝 暂定*/
    public final static String shoukuanbaoId = "B00024";

    //usage    true    卡用途 string  2:信用卡还款3:转账汇款;4:手机号收款 5:AA分账  6:还贷款 7:钱包提现银行卡 8：我要收款卡
    /**	我要收款卡用途类别*/
    public final static String  woyaoshoukuan_Usage  ="8";

    /*********************************************************************************************/
    /*** start 标识来源界面，用于公共界面的辨别跳转源 start */
    /********************************************************************************************/
    /** 源来Key */
    public final static String COME_FROM = "from";

    /** 理财保险 */
    public final static int FROM_LCBX = 0;
    /** 理财保险 旅游目的地*/
    public final static int FROM_LCBX_MDD = 1;
    /** 违章查询 */
    public final static int FROM_WZCX = 2;
    /** 交罚办理协议 */
    public final static int FROM_WZCX_Servic_Agreements = 5;

    /** 转账汇款收费规则 */
    public final static int FROM_ZHHK_SHOUFEI_GUIZE = 10;
    /** 刷卡器使用说明 */
    public final static int SWIPER_CARD_PROMPT = 11;
    /** 我要收款界面跳转至选择银行界面*/
    public final static int FROM_SHOUKUAN_TO_BANK = 12;
    /** 我要收款收费规则 */
    public final static int FROM_WYSK_SHOUFEI_GUIZE = 13;
    /** 转账收费规则 */
    public final static int FROM_ZZ_SHOUFEI_GUIZE = 14;
    /** 红包使用规则*/
    public final static int FROM_HONGBAO_GUIZE = 15 ;
    /** 从转帐到银行卡的添加银行卡界面跳转*/

    /** 我要收款的二维码页面跳转过来*/
    public final static String ACTION_FROM_WYSK_TO_ADDINFO = "comefromwysktoaddinfo";


    /****start->消息中心**********************************/
    public static final String KEY_MESSAGE_CATEGORE = "category";//获取公告类型key值

    public static final String PREFERENCE_SERIES_KEY = "SERIES"; //保存在share中的发送方跟踪号(6位数字)
    public static final int PREFERENCE_SERIES_MAX_VALUE = 999999; //保存在share中的发送方跟踪号(最大数字)


    //小米杜比
    public static final String PHONE_MODEL = "MI2";
    public static final String STABLETAG = "JLB";
    public static final String STABLEVERSION = "JLB16.0";//稳定版必须大于等于此版本
    public static final String ENGVERSION = "3.5.7";//必须大于此版本才能使用广播关闭杜比
    public static final String ACTION_DOLBY_UPDATE = "com.xiaomi.action_dolby_update";//更新杜比状态
    public static final String CHECK_STATUS_ACTION = "com.dolby.dm.srvcmd.checkstatus";//检测杜比音效

    public static final String FLOATINGLAYER_SHOWED = "floating_layer_showed";


    public static final String MER_LEVEL_INFO = "MerLevelInfo";
}
