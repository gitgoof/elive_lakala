package com.lakala.elive;

import android.os.Environment;

import com.lakala.elive.beans.DictDetailInfo;
import com.lakala.elive.common.net.NetAPI;
import com.lakala.elive.map.activity.MapStoreShowAct;
import com.lakala.elive.market.activity.DailyWorkListActivity;
import com.lakala.elive.market.activity.DealListActivity;
import com.lakala.elive.merapply.activity.EnterPieceActivity;
import com.lakala.elive.task.activity.TaskListActivity;
import com.lakala.elive.message.activity.NoticeListActivity;
import com.lakala.elive.message.activity.VoteListActivity;
import com.lakala.elive.preenterpiece.PreEnterPieceListActivity;
import com.lakala.elive.report.activity.ReportMainActivity;
import com.lakala.elive.user.activity.DemoListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface     Constants {

	/**
	 * SPLASH 延迟页面时间
	 */
    long SPLASH_DELAY_MILLIS = 3000;

    /**
     *  图片本地保存到SD卡的路径
     */
    String savePath = "/mnt/sdcard/";

    //如果您还没有customerId、customerKey，请与考拉征信对接人联系。注意：测试与生产的customerId、customerKey是不相同的。
    String customerId = "201505140000000001";
    String customerKey = "N4L30XJ4";


    int[] HOME_MENU_ICON = {
            R.drawable.menu_elive_bmcp_order,
            R.drawable.menu_elive_daily_work,
            R.drawable.menu_elive_report,
            R.drawable.menu_elive_notice,
            R.drawable.from_6
    };

    String[] HOME_MENU_NAME = {
            "工单处理",
            "日常维护",
            "报表",
            "公告",
			"Demo案例"
	};

    //页面参数传递 Extras
    String EXTRAS_MER_INFO = "extras.merchant";
    String EXTRAS_TERM_INFO = "extras.term";
    String EXTRAS_MER_VISIT_INFO = "extras.merchant.visit"; //网点签到
    String EXTRAS_MER_EDIT_INFO = "extras.merchant.edit"; //编辑页面
    String EXTRAS_MER_SCREENSHOT_INFO = "extras.merchant.screenshot"; //编辑页面
    String EXTRAS_MER_SCREENSHOT_ID = "extras.merchant.screenshot.id"; //编辑页面
    String REPORT_ATTENTION_UPDATE = "report.attention.update"; //报表更新
    String EXTRAS_NOTICE_INFO = "extras.notice";
    String EXTRAS_TASK_VOTE_INFO = "extras.task.vote";
    String EXTRAS_TASK_DEAL_INFO = "extras.task.deal";//外勤工单处理

    //商户拜访采集图片保存
    String MER_IMG_SAVE_PATH = Environment.getExternalStorageDirectory() + "/com.lakala.elive/";
    String MER_IMG_TMP_FILE = Environment.getExternalStorageDirectory() + "/elive_tmp_image.jpg";

    int TAKE_PICTURE_NORMAL = 1;
    int TAKE_PICTURE_CLEAR = 2;
    int TAKE_SCAN = 1;

    int TAKE_EIDIT_TERM = 2;

    int MER_IMG_SIZE = 10;

    int TAKE_MER_LOCATION = 1;

    //Preference XML 保存的内容
    String KEY_SP_REMEMBER_PWD_CHECK = "REMEMBER_PWD_CHECK";
    String KEY_SP_USER_NAME = "USERNAME";
    String KEY_SP_USER_PWD = "PASSWORD";
    String KEY_SP_ELIVE_USER_AUTHOR = "USERAUTHER";//USER_AUTHOR

    //返回成功码
    String RET_CODE_SUCCESS = "SUCCESS";

    int MER_VISIT_IMG_SIZE = 3;

    String ELIVE_SD_SAVE_PATH = Environment.getExternalStorageDirectory() + "/com.lakala.elive/";

    /**
     *  apk下载保存完整路径名
     */
    String saveFileName = ELIVE_SD_SAVE_PATH + "lakala_elive.apk";

    //app菜单
    Map<String, Class<?>> menuListMap = new HashMap<String, Class<?>>() {{
        put("08", DailyWorkListActivity.class); //日常维护
        put("09", DealListActivity.class); //工单处理
        put("00", DemoListActivity.class); //测试案例
        put("01", ReportMainActivity.class); //测试案例
        put("05", NoticeListActivity.class); //公告列表
        put("10", VoteListActivity.class); //投票
        put("11", EnterPieceActivity.class); //进件
        put("03", TaskListActivity.class); //任务列表
        put("12", PreEnterPieceListActivity.class); //预进件
        put("13", MapStoreShowAct.class); // 网点地图
    }};


    int DEFAULT_PAGE_SIZE = 15;

    String[] dictDataList = new String[]{
        "IS_EXCHANGE_SIGN",
        "NO_BIND_ELE_PLATFORM_REASON",
        "BIND_ELE_PLATFORM_RESULT",
        "SWING_UPGRADE_RESULT",
        "NONCONN_CHANGE_RESULT",
        "NO_TRAN_ANALYSE_RESULT",
        "OTHER_POS_ADVANCE",
        "CARD_APP_TYPE",
        "NO_TRAN_REASON",
        "VISIT_TYPE",
        "MER_INFO_TRUTH",
        "MER_MATURE",
        "MER_LEVEL",
        "MER_MAIN_SALE",
        "PRODUCT_CLASS",
        "MER_SHOP_AREA",
        "MER_CLASS",
        "TERM_TYPE",
        "DEVICE_DRAW_METHOD",
        "SYS_PRODUCT_CLASS",
        "DEVICE_CHECK_STATUS",
        "TASK_CHANNEL", //任务来源
        "TASK_STATUS",  //任务状态
        "APP_TASK_STATUS",  //任务状态
        "TASK_TYPE",  //任务类型
        "TASK_LEVEL",  //任务等级
        "TASK_RESULT",  //任务处理结果
        "MER_BIZ_CONTENT",  //经营内容
        "SHOP_AREA",  //经营面积
        "SETTLE_PERIOD",  //结算周期
        "TERMINAL_TYPE",  //机型
        "SETTLE_PERIOD_PUBLIC",  //对公
        "SETTLE_PERIOD_PRIVATE", //对私
            "SETTLE_PERIOD_Q"//Q码进件的结算周期
    };

    String IS_EXCHANGE_SIGN = "IS_EXCHANGE_SIGN";
    String NO_BIND_ELE_PLATFORM_REASON = "NO_BIND_ELE_PLATFORM_REASON";
    String BIND_ELE_PLATFORM_RESULT = "BIND_ELE_PLATFORM_RESULT";
    String SWING_UPGRADE_RESULT = "SWING_UPGRADE_RESULT";
    String NONCONN_CHANGE_RESULT = "NONCONN_CHANGE_RESULT";
    String NO_TRAN_ANALYSE_RESULT = "NO_TRAN_ANALYSE_RESULT";
    String OTHER_POS_ADVANCE = "OTHER_POS_ADVANCE";
    String CARD_APP_TYPE = "CARD_APP_TYPE";
    String NO_TRAN_REASON = "NO_TRAN_REASON";
    String VISIT_TYPE = "VISIT_TYPE";
    String MER_INFO_TRUTH = "MER_INFO_TRUTH";
    String MER_MATURE = "MER_MATURE";
    String MER_LEVEL = "MER_LEVEL";
    String MER_MAIN_SALE = "MER_MAIN_SALE";
    String PRODUCT_CLASS = "PRODUCT_CLASS";
    String MER_SHOP_AREA = "MER_SHOP_AREA";
    String MER_CLASS = "MER_CLASS";
    String TERM_TYPE = "TERM_TYPE";
    String DEVICE_DRAW_METHOD = "DEVICE_DRAW_METHOD";
    String SYS_PRODUCT_CLASS = "SYS_PRODUCT_CLASS";
    String DEVICE_CHECK_STATUS = "DEVICE_CHECK_STATUS";
    String TASK_CHANNEL = "TASK_CHANNEL";//任务来源
    String TASK_STATUS = "APP_TASK_STATUS";//任务状态
    String TASK_TYPE = "TASK_TYPE"; //任务类型
    String TASK_LEVEL = "TASK_LEVEL";//任务等级
    String TASK_RESULT = "TASK_RESULT";//任务等级

    String ADDR_STR = "addrStr";

    int SP_VERTICLE_PIXELS = 100;
    int SP_HOR_PIXELS = 0;
    int VISIT_COMMENT_LEN = 500;


    String MER_EDIT = "EDIT";
    String MER_VISIT = "VISIT";

    //报表
    String WEBVIEW_LOAD_ERR_PAGE = "file:///android_asset/www/error.html";


    //Report 查询条件
    String ELIVE_DATA_QUERY_API = NetAPI.REPORT_HOST + "/lakala-elive/mobile/report_mobile!init.action";

    //Report 查询结果
    String ELIVE_DATA_RESULT_API = NetAPI.REPORT_HOST + "/lakala-elive/mobile/report_mobile!result.action";

    //Report 查询条件
    String REPORT_DATA_QUERY_API = NetAPI.REPORT_HOST + "/lakala-report/mobile/report_mobile!init.action";

    //Report 查询结果
    String REPORT_DATA_RESULT_API = NetAPI.REPORT_HOST + "/lakala-report/mobile/report_mobile!result.action";

    ArrayList<DictDetailInfo> shopCreateTimeDictDataList = new ArrayList<DictDetailInfo>() {
        {
            add(new DictDetailInfo("-1","不限"));
            add(new DictDetailInfo("2","昨天"));
            add(new DictDetailInfo("3","三天内"));
            add(new DictDetailInfo("7","一周内"));
            add(new DictDetailInfo("30","一月内"));
        }
    };

    ArrayList<DictDetailInfo> shopVistitTimeDictDataList = new ArrayList<DictDetailInfo>() {
        {
            add(new DictDetailInfo("-1","不限"));
            add(new DictDetailInfo("1","当天"));
            add(new DictDetailInfo("3","三天内"));
            add(new DictDetailInfo("7","一周内"));
            add(new DictDetailInfo("30","一月内"));
        }
    };

    ArrayList<DictDetailInfo> delayDictDataList = new ArrayList<DictDetailInfo>() {
        {
            add(new DictDetailInfo("-1","不限"));
            add(new DictDetailInfo("1","当天"));
            add(new DictDetailInfo("3","三天内"));
            add(new DictDetailInfo("7","一周内"));
            add(new DictDetailInfo("0","超时"));
        }
    };

    ArrayList<DictDetailInfo> finshDictDataList = new ArrayList<DictDetailInfo>() {
        {
            add(new DictDetailInfo("-1","不限"));
            add(new DictDetailInfo("1","当天"));
            add(new DictDetailInfo("3","三天内"));
            add(new DictDetailInfo("7","一周内"));
            add(new DictDetailInfo("30","一月内"));
        }
    };

    final class MessageType {
        public final static String TASK_DEAL = "TASK_DEAL";
        public final static String TASK_DEAL_STAUTS_UPDATE = "TASK_DEAL_STAUTS_UPDATE";
        public final static String TASK_DEAL_REJUCT = "TASK_DEAL_REJUCT";
        public final static String SHOP_VISIT = "SHOP_VISIT";

    }

    String OCR_YES = "1";//OCR
    String OCR_NO = "0";//不要OCR


    final class MER_IMAGE_TYPE {
        public final static String ID_CARD_FRONT = "ID_CARD_FRONT";//身份证正面
        public final static String ID_CARD_BEHIND = "ID_CARD_BEHIND";//身份证反面
        public final static String BANK_CARD = "BANK_CARD";//银行卡
        public final static String XY = "XY";//商户协议
        public final static String KH = "KH";//开户许可

    }

    final class MER_IMAGE_BIG_TYPE {
        public final static String APPLY_ACCOUNT = "APPLY_ACCOUNT";//结算账户验证附件
        public final static String APPLY_LICENSE = "APPLY_LICENSE";//营业执照
        public final static String APPLY_SUP_PROOF = "APPLY_SUP_PROOF";//补充证明

    }

    final class MER_IMAGE_STEP_TYPE {
        public final static int TYPE_ID_CARD_FRONT = 1;//身份证正面
        public final static int TYPE_ID_CARD_BEHIND = 2;//身份证反面
        public final static int TYPE_BANK_CARD = 3;//银行卡
        public final static int TYPE_XY = 4;//商户协议
        public final static int TYPE_ACCOUNT_LICENSE = 5;//开户许可

    }

    final class BMCP_APPLY_STATUS {
        public final static String ENTER = "ENTER"; //ENTER:待录入（未录入完成）,
        public final static String AUDIT = "AUDIT";// AUDIT:待审核（未同步到bmcp）,
        public final static String AUDITING = "AUDITING";// AUDITING:审核中（已同步到bmcp）,
        public final static String REBUT = "REBUT";// REBUT:审核失败,
        public final static String STORAGED = "STORAGED";// STORAGED:审核成功
    }

    //
    public static String QCODE_COMPILE_STATUE="QCODE_COMPILE_STATUE";
}
