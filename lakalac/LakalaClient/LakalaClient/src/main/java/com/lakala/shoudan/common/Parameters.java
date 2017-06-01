package com.lakala.shoudan.common;


import com.lakala.shoudan.BuildConfig;


/**
 * 全局参数类,提供全局生命周期的静态数据
 * @author 金同宝
 *
 */
public class Parameters {

    public static int screenWidth;//屏幕宽度
    public static int screenHeight;//屏幕高度
    public static int statusBarHeight;
    public final static String SECURITY_QUESTION="SECURITY_QUESTION";

    public final static String imageCachePath ="/lakala/imgCache/";//图片缓存路径

    public static String rentInpan = "";

    /**
     *
     * 用户状态
     *
     *  true未通过,可以被修改(null，“REJECT”，“NONE" 可以提交)
     *  false通过不可被修改( PASS，APPLY不能提交)
     *
     */
//    public static boolean authState = true;

    /**
     *  调试标记，正式版必须置为 false 。
     *  开启调试后，所有的http请求均会添加 debug=1 参数。
     *  控制是否做签名验证，为true时，不进行签名验证；为false，且当版本号大于等于13时，进行签名验证
     */
    public static final boolean debug = BuildConfig.DEBUG;

    /**
     * 生产,测试服务器地址开关
     * true,使用测试服务器
     * false,使用生产服务器
     */
    public static final boolean useDeveloperURL = BuildConfig.URL.contains("mpos.lakala.com");

    /**主服务器地址，该地址在  initServerAddress() 中初始化。*/
    public static String serviceURL = BuildConfig.URL + "v1.0/";

    //默认改改点击跳转URL
    public static final String DEFAULT_AD_URL = "https://download.lakala.com/lklmbl/skbadv/skb_adv07.html";


    /**
     * 替你还路径
     */
    public static final String LOAN_PATH = "https://download.lakala.com/lklmbl/html/loan/";



    //TODO should be included in user info
    /** 扫码收款交易查询次数，默认9次 */
    public static int repeatCount = 9;
    /** 扫码收款交易查询间隔时间，默认5S */
    public static int repeatTime = 5;
    /** 扫码交易超时时间，默认60S */
    public static int timeout = 60;/**理财支付密码错误*/

    //交易超时统一code
    public static final String tranTimeOut = "1030";
	
	/**Android 客户端 ID*/
	public static final String androidClientID = "asdfas@weeweaPos@saqwqwqqw228228()#44%";

	public static void clear(){

        Parameters.rentInpan = "";


	}

    //shanghutong
    /*************************** Html页面 ***************************/
    /**帮助页面前缀*/
    public final static String HELP_URL_PREFIX = "https://download.lakala.com/lklmbl/html/sht/";
    /**bao名*/
    public final static String PACKAGENAME = BuildConfig.APPLICATION_ID;
    //下载服务发送广播:开始下载
    public final static String DOWNLOAD_SERVICE_DOWNLOAD_START_ACTION 		= PACKAGENAME.concat(".broadcast.download.start");
    //下载服务发送广播：更新进度条
    public final static String DOWNLOAD_SERVICE_DOWNLOAD_PROGRESS_ACTION    = PACKAGENAME.concat(".broadcast.download.progress");
    //下载服务发送广播:下载已经完成
    public final static String DOWNLOAD_SERVICE_DOWNLOAD_COMPLETE_ACTION	= PACKAGENAME.concat(".broadcast.download.complete");
    //下载服务发送广播:断网的状态
    public final static String DOWNLOAD_SERVICE_DISCONNECT_NETWORK_ACTION	= PACKAGENAME.concat(".broadcast.download.error");
    //下载服务发送广播:取消下载
    public final static String DWONLOAD_SERVICE_DOWNLOAD_CANCEL_ACTION		= PACKAGENAME.concat(".broadcast.download.cancel");


}
