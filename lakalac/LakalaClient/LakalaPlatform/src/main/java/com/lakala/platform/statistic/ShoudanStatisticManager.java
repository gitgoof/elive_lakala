package com.lakala.platform.statistic;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.avos.avoscloud.AVAnalytics;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;

import java.util.HashMap;
import java.util.Map;

public class ShoudanStatisticManager {

    //TODO 补充数据统计 last step

    private static ShoudanStatisticManager mShoudanStatisticManager;

    private ShoudanStatisticManager() {

    }

    public static ShoudanStatisticManager getInstance() {
        if (null == mShoudanStatisticManager) {
            mShoudanStatisticManager = new ShoudanStatisticManager();
        }
        return mShoudanStatisticManager;
    }

    public static String statics;

    public static void setStatics(String staticsIn) {
        statics = staticsIn;
    }

    public static String getStatics() {
        return statics;
    }

    /**
     * ===================运行App===================
     */

    /**
     * .App激活
     */
    public static String appActivate = "App激活";
    /**
     * .进程级别启动应用程序(注意点: 1.应用后台唤醒不在此列;)
     */
    public static String appLaunch = "App启动";
    public static String appChannel = "渠道%s";


    /**
     * ===================注册、登录===================
     */

    public static String Login_1 = "登录页-注册手机号";//从登录页进入注册页面
    /**
     * .用户登录
     */
    public static String Login_4 = "登录页";//进入登录页面
    /**
     * 登录成功
     */
    public static String Login_2 = "登录成功";//输入用户名密码登录成功进入到首页
    /**
     * 找回登录密码
     */

    public static String Login_5 = "登录页-找回密码";//从登录页进入找回登录密码页
    public static String Login_6 = "登录页-注册手机号-设置密码";//从登录页进入找回登录密码页
    public static String Login_7 = "登录页-注册手机号-设置密码-填写验证码-注册成功";//注册成功，返回登录页面
    public static String Login_8 = "登录页-找回密码-重置密码";//进入重置登录密码页面
    public static String Login_9 = "登录页-快速了解";//滑动页面底部快速了解图标
    public static String Login_11 = "登录页-注册手机号";//从登录页进入找回登录密码页


    /**
     * ===================刷卡收款===================
     */

    /**
     * 从首页选择刷卡收款进入刷卡收款页
     */
    public static String Collection_Home = "首页-收款金额";//收款金额录入界面
    public static String Collection_Public = "首页公共业务-收款金额";//收款金额录入界面
    public static String Collection_Succes = "刷卡成功-收款金额";//收款金额录入界面

    public static String Collection_Home_Page = "首页-收款金额-刷卡页面";//刷卡页面
    public static String Collection_Public_Page = "首页公共业务-收款金额-刷卡页面";//刷卡页面
    public static String Collection_Succes_Page = "刷卡成功-收款金额-刷卡页面";//刷卡页面

    public static String Collection_Home_Sign = "首页-收款金额-刷卡-签购单";//签购单界面
    public static String Collection_Public_Sign = "首页公共业务-收款金额-刷卡-签购单";//签购单界面
    public static String Collection_Succes_Sign = "刷卡成功-收款金额-刷卡-签购单";//签购单界面

    public static String Collection_Home_Send = "首页-收款金额-刷卡-签购单-%s-成功";//刷卡成功界面
    public static String Collection_Public_Send = "首页公共业务-收款金额-刷卡-签购单-%s-成功";//刷卡成功界面
    public static String Collection_Succes_Send = "刷卡成功-收款金额-刷卡-签购单-%s-成功";//刷卡成功界面

    /**
     * 从首页选择刷卡收款并且进入交易成功页
     */
    public static String Collection_Success_Homepage = "首页-刷卡收款-收款成功";
    /**
     * 从收款一级菜单选择刷卡收款进入刷卡收款页
     */
    public static String Collection = "收款-刷卡收款";
    /**
     * 从收款一级菜单选择刷卡收款并且进入交易成功页
     */
    public static String Collection_Success = "刷卡成功-收款金额-刷卡-签购单-发（不发）-成功";
    public static String SwipingSuccess = "（首页、首页公共业务、刷卡成功）-收款金额-刷卡";//刷卡界面关联刷卡器成功，并刷卡成功
    public static String SwipingSet = "首页公共业务-收款金额-挥卡设置";//挥卡设置界面
    public static String SignatureBills = "首页-收款金额-刷卡-签购单";//签购单
    /**
     * 从某条广告进入刷卡收款业务并且进入交易成功页
     */
    public static String Advert_Collection_Success = "广告(广告%s)-刷卡收款-收款成功";


    /**
     * ===================扫码收款===================
     */

    /**
     * 从首页选择扫码收款进入扫码收款页
     */
    public static String Scan_Code_Collection_Homepage = "首页-扫码收款";
    public static String Scan_Code_Collection_Homepage_Public = "首页公共业务-扫码收款";
    /**
     * 从首页选择扫码收款并且进入交易成功页
     */
    public static String Scan_Code_Collection_Success_Homepage = "首页-扫码收款-收款成功";
    /**
     * 从收款一级菜单选择扫码收款进入扫码收款页
     */
    public static String Scan_Code_Collection = "收款-扫码收款";
    /**
     * 从收款一级菜单选择扫码收款并且进入交易成功页
     */
    public static String Scan_Code_Collection_Success = "收款-扫码收款-收款成功";
    /**
     * 从某条广告进入扫码收款业务并且进入交易成功页
     */
    public static String Advert_Scan_Code_Collection_Success = "广告(广告%s)-扫码收款-收款成功";
    /**
     * 通过页面提示进行点击，进入扫码规则说明页面
     */
    public static String Scan_Code_Collection_Apply = "首页-扫码收款-扫码申请";
    /**
     * 扫码收款帮助界面
     */
    public static String Scan_Code_Collection_Help = "首页-扫码-帮助";
    public static String Scan_Code_Collection_Help_Public = "首页公共业务-扫码-帮助";
    /**
     * 点击二维码保存按钮
     */
    public static String Scan_Code_Collection_SaveBarCode = "首页-扫码-保存图片";
    public static String Scan_Code_Collection_SaveBarCode_Public = "首页公共业务-扫码-保存图片";
    /**
     * 进入扫码资料确认页面
     */
    public static String Scan_Code_Collection_ConfirmInfo = "首页-用户信息-扫码收款开通-扫码收款-扫码申请-资料确认";
    public static String Scan_Code_Collection_ConfirmInfo_Public = "首页公共业务-用户信息-扫码收款开通-扫码收款-扫码申请-资料确认";
    /**
     * 弹出申请成功提示框
     */
    public static String Scan_Code_Collection_DoApply_Homepage = "首页-用户信息-扫码收款开通-扫码收款-扫码申请-资料确认-确认申请";
    public static String Scan_Code_Collection_DoApply_HomepagePublic = "首页公共业务-用户信息-扫码收款开通-扫码收款-扫码申请-资料确认-确认申请";
    /**
     * 扫码协议详情页
     */
    public static String Scan_Code_Collection_BarCodeRule = "首页-用户信息-扫码收款开通-扫码收款-扫码申请-资料确认-扫码协议";


    /**
     * ===================大额收款===================
     */
    public static String LargeAmount_Collection = "首页-首页公共业务-大额收款";//从收款一级菜单选择大额收款进入大额收款页
    public static String LargeAmount_Collection_Page = "首页公共业务-大额收款-照片采集-刷卡-签购单";//签购单
    public static String LargeAmount_Collection_Send = "首页公共业务-大额收款-照片采集-刷卡-签购单-%s";//发送不发送

    /**
     * 点说明页的申请大额收款，进入提交申请页面
     */
    public static String LargeAmount_infoFill = "首页-大额收款-信息填写";
    /**
     * 点击提交申请，进入审核状态
     */
    public static String LargeAmount_DoApply = "首页-大额收款-信息填写-提交审核";
    /**
     * 大额收款规则说明页面
     */
    public static String LargeAmount_Rule = "首页公共业务-大额规则";
    /**
     * 大额收款申请信息添加页面
     */
    public static String LargeAmount_Collection_InfoApply = "首页公共业务-大额规则-申请信息";
    /**
     * 大额收款被收款人照片采集界面
     */
    public static String LargeAmount_Collection_PicRaise = "首页公共业务-大额收款-照片采集";
    /**
     * 大额收款刷卡界面
     */
    public static String LargeAmount_Collection_SwipingPage = "首页公共业务-大额收款-照片采集-（未关连刷卡器/关联刷卡器失败/刷卡失败）";
    /**
     * 大额收款业务说明
     */
    public static String LargeAmount_PicRaise_Instruction = "首页公共业务-大额收款-照片采集-业务说明";

    /**
     * 从收款一级菜单选择大额收款并且进入交易成功页
     */
    public static String LargeAmount_Collection_Success = "收款-大额收款-收款成功";
    /**
     * 从某条广告进入大额收款业务并且进入交易成功页
     */
    public static String Advert_LargeAmount_Collection_Success = "广告(广告%s)-大额收款-收款成功";


    /**
     * ===================撤销交易===================
     */
    /**
     * 从收款一级菜单选择撤销交易进入撤销交易页
     */
    public static String Undo = "首页公共业务-撤销交易";//从收款一级菜单选择撤销交易进入撤销交易页
    public static String Undo_Pwd = "首页公共业务-撤销交易-刷卡收单撤销密码";//撤销交易输入密码
    public static String Undo_Pwd_SwipeSuccess = "首页公共业务-撤销交易-刷卡收单撤销密码-刷卡";//刷卡界面关联刷卡器成功，并刷卡成功
    public static String Undo_Pwd_SwipeFailure = "首页公共业务-撤销交易-刷卡收单撤销密码-交易撤销刷卡界面";//交易撤销刷卡界面
    public static String Undo_Pwd_Failure = "首页公共业务-撤销交易-刷卡收单撤销密码-刷卡-撤销失败（%s）";//撤销失败界面
    public static String Undo_Success = "收款-撤销交易-撤销成功";//从收款一级菜单选择撤销交易并且进入交易成功页
    public static String Advert_Undo_Success = "广告(广告%s)-撤销交易-撤销成功";//从某条广告进入撤销业务并且进入交易成功页
    public static String Advert_Undo_Sign_Page = "首页公共业务-撤销交易-刷卡收单撤销密码-刷卡-签购单";//签购单
    public static String Advert_Undo_Sign_isSend = "首页公共业务-撤销交易-刷卡收单撤销密码-刷卡-签购单-%s";//发送到手机号弹出界面

    /**
     * ===================首页==================
     */
    public static String Do_Home = "首页";//首页
    public static String Do_Home_Public = "首页-首页公共业务";//首页公共业务


    /**
     * ===================立即提款==================
     */
    public static String Do_Homepage = "首页-立即提款";//从首页选择立即提款进入立即提款页
    public static String Do_Homepage_Apply = "首页-立即提款-申请开通";//弹出成功申请立即提款业务提示
    public static String Do_Homepage_Detail = "首页-立即提款-提款详情ID";//提款详情页面
    public static String Do_Homepage_Explain = "首页-立即提款-业务说明";//立即提款业务说明页面
    public static String Do_Homepage_Agreement = "首页-立即提款-立即提款协议";//点击立即提款协议，进入到协议页面
    public static String Do_Homepage_Input = "首页-立即提款-金额录入";//立即提款金额录入界面，进入到协议页面
    public static String Do_Public_Input = "首页公共业务-提款金额录入";//立即提款金额录入界面
    public static String Do_Ad_Input = "首页广告%s-提款金额录入";//立即提款金额录入界面
    public static String Do_Public_Record_Input = "首页公共业务-交易记录-提款金额录入";//立即提款金额录入界面
    public static String Do_Public_Input_Free = "首页-立即提款-金额录入-手续费确认";//立即提款手续费确认界面
    public static String Do_Public_Input_Public_Free = "首页公共业务-提款金额录入-手续费确认";//立即提款手续费确认界面
    public static String Do_Public_Input_Ad_Free = "首页广告%s-提款金额录入-手续费确认";//立即提款手续费确认界面
    public static String Do_Public_Input_Record_Free = "首页公共业务-交易记录-提款金额录入-手续费确认";//立即提款手续费确认界面
    public static String Do_Public_Input_Free_Explain = "首页-立即提款-金额录入-手续费确认-业务说明";//立即提款业务说明页面
    public static String Do_Public_Input_Public_Free_Explain = "首页公共业务-提款金额录入-手续费确认-业务说明";//立即提款业务说明页面
    public static String Do_Public_Input_Ad_Free_Explain = "首页广告%s-提款金额录入-手续费确认-业务说明";//立即提款业务说明页面
    public static String Do_Public_Input_Record_Free_Explain = "首页公共业务-交易记录-提款金额录入-手续费确认-业务说明";//立即提款业务说明页面


    public static String Do_Homepage_Rule = "首页-立即提款-业务说明";//立即提款业务说明页面
    public static String Do_Homepage_immediateWithDraw = "首页-立即提款-提款详情ID";//立即提款页面
    public static String Do_Success_Homepage = "首页-立即提款-提款成功";//从首页选择立即提款并且进入交易成功页
    public static String Do = "收款-立即提款";//从收款一级菜单选择立即提款进入立即提款页
    public static String Do_Success = "收款-立即提款-提款成功";//从收款一级菜单选择立即提款并且进入交易成功页
    public static String Do_Transaction_Management = "交易管理-立即提款";//从交易管理首页选择立即提款进入立即提款页
    public static String Do_Success_Transaction_Management = "交易管理-立即提款-提款成功";//从交易管理首页选择立即提款并且进入交易成功页
    public static String Advert_Do_Success = "广告(广告%s)-立即提款-提款成功";//从某条广告进入立即提款业务并且进入交易成功页


    /**
     * ===================交易管理==================
     */
    public static String Collection_Recode_Transaction_Management = "交易管理-收款记录";//从交易管理首页选择收款记录进入收款记录页
    public static String Collection_Recode = "收款-收款记录";//从收款一级菜单选择交易查询进入收款记录页
    public static String Life_Transaction_Transaction_Management = "交易管理-生活交易查询";//从交易管理首页选择生活交易查询进入生活交易查询页
    public static String Life_Transaction = "生活-生活交易查询";//从生活一级菜单选择交易查询进入生活交易查询页
    public static String Balance_of_Payment_Details_Transaction_Management = "交易管理-零钱收支明细";//从交易管理首页选择零钱收支明细进入收支明细页
    public static String Balance_of_Payment_Details = "首页-钱包首页-收支列表";//从钱包一级菜单选择收支明细进入收支明细页
    public static String Balance_of_Payment_Details_Into = "首页-钱包首页-收支列表-收款详情";//收支明细详情
    public static String Drawing_Recode = "交易管理-划款记录";//从收款一级菜单选择划款记录进入划款记录页
    /**
     * ===================交易记录==================
     */
    public static String TradeRecord_Homepage = "首页公共业务-交易记录";//交易记录首页
    public static String TradeRecord_CollectionRecord = "首页公共业务-交易记录-收款记录";//收款记录查询界面
    public static String TradeRecord_SwipeCollection = "首页公共业务-交易记录-收款记录-刷卡收款";//刷卡收款结果列表
    public static String TradeRecord_scanCodeCollection = "首页公共业务-交易记录-收款记录-扫码收款";//扫码收款结果列表
    public static String TradeRecord_bigLimitCollection = "首页公共业务-交易记录-收款记录-大额收款";//大额收款结果列表
    public static String TradeRecord_HuaKuanToday = "首页公共业务-交易记录-今日划款记录";//今日划款记录列表页
    public static String TradeRecord_HuaKuanSevenDay = "首页公共业务-交易记录-7日划款记录";//近7日划款记录列表页
    public static String TradeRecord_HuaKuanTwoweeks = "首页公共业务-交易记录-两周划款记录";//近两周划款记录列表页
    public static String TradeRecord_HuaKuanTodayDetail = "首页公共业务-交易记录-今日划款记录-划款详情";//划款记录详情页
    public static String TradeRecord_HuaKuanSevenDayDetail = "首页公共业务-交易记录-7日划款记录-划款详情";//划款记录详情页
    public static String TradeRecord_HuaKuanTwoweeksDetail = "首页公共业务-交易记录-两周划款记录-划款详情";//划款记录详情页
    public static String TradeRecord_FeesOfLife = "首页公共业务-交易记录-生活缴费";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_CreditPayback = "首页公共业务-交易记录-生活缴费-还款查询";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_Transfer = "首页公共业务-交易记录-生活缴费-转账查询";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_Recharge = "首页公共业务-交易记录-生活缴费-充值查询";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_Duobao = "首页公共业务-交易记录-生活缴费-夺宝查询";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_Jiaofei = "首页公共业务-交易记录-生活缴费-缴费查询";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_ZhuanXiang = "首页公共业务-交易记录-生活缴费-专享查询";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_CreditPaybackDetail = "首页公共业务-交易记录-生活缴费-还款查询-详情";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_TransferDetail = "首页公共业务-交易记录-生活缴费-转账查询-详情";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_RechargeDetail = "首页公共业务-交易记录-生活缴费-充值查询-详情";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_DuobaoDetail = "首页公共业务-交易记录-生活缴费-夺宝查询-详情";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_JiaofeiDetail = "首页公共业务-交易记录-生活缴费-缴费查询-详情";//生活缴费查询页面
    public static String TradeRecord_FeesOfLife_ZhuanXiangDetail = "首页公共业务-交易记录-生活缴费-专享查询-详情";//生活缴费查询页面
    public static String TradeRecord_Incomedetails = "首页公共业务-交易记录-收支明细";//零钱收支明细列表


    /**
     * ===================生活==================
     */
    //信用卡还款
    public static String Credit_Card_Repayment_home = "首页-信用卡还款";//还款页面填写
    public static String Credit_Card_Repayment_public = "首页公共业务-信用卡还款";//还款页面填写
    public static String Credit_Card_Repayment_direction = "定向业务-信用卡还款";//还款页面填写
    public static String Credit_Card_Repayment_advert = "广告-信用卡还款";//还款页面填写
    public static String Credit_Card_Repayment_paysuccess = "还款成功-信用卡还款";//还款页面填写
    public static String Credit_Card_Repayment_Success = "生活-信用卡还款-还款成功";//从生活一级菜单选择信用卡还款并且进入交易成功页
    public static String Credit_Card_HistoryList = "首页-信用卡还款-历史列表";//从生活一级菜单选择信用卡还款并且进入交易成功页
    public static String Credit_Card_checkSituation_home = "首页-信用卡还款-还款提醒（%s）-短信提醒（%s）";//还款提醒和短信提醒
    public static String Credit_Card_checkSituation_direction = "定向业务-信用卡还款-还款提醒（%s）-短信提醒（%s）";//还款提醒和短信提醒
    public static String Credit_Card_checkSituation_advert = "广告-信用卡还款-还款提醒（%s）-短信提醒（%s）";//还款提醒和短信提醒
    public static String Credit_Card_checkSituation_public = "首页公共业务-信用卡还款-还款提醒（%s）-短信提醒（%s）";//还款提醒和短信提醒
    public static String Credit_Card_checkSituation_paysuccess = "还款成功-信用卡还款-还款提醒（%s）-短信提醒（%s）";//还款提醒和短信提醒
    public static String Credit_Card_paySuccess_home = "首页-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡-还款成功";//还款提醒和短信提醒
    public static String Credit_Card_paySuccess_direction = "定向业务-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡-还款成功";//还款提醒和短信提醒
    public static String Credit_Card_paySuccess_advert = "广告-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡-还款成功";//还款提醒和短信提醒
    public static String Credit_Card_paySuccess_public = "首页公共业务-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡-还款成功";//还款提醒和短信提醒
    public static String Credit_Card_paySuccess_public_swipe_home = "首页-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡";//刷卡界面关联刷卡器成功，并刷卡
    public static String Credit_Card_paySuccess_public_swipe_public = "首页公共业务-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡";//刷卡界面关联刷卡器成功，并刷卡
    public static String Credit_Card_paySuccess_public_swipe_advert = "广告-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡";//刷卡界面关联刷卡器成功，并刷卡
    public static String Credit_Card_paySuccess_public_swipe_paysuccess = "还款成功-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡";//刷卡界面关联刷卡器成功，并刷卡
    public static String Credit_Card_paySuccess_public_swipe_direction = "定向业务-信用卡还款-还款提醒（%s）-短信提醒（%s）-刷卡";//刷卡界面关联刷卡器成功，并刷卡
    //手机充值
    public static String Phone_Recharge_Public = "首页公共业务-手机充值";//选择充值卡面值界面
    public static String Phone_Recharge_De = "定向业务-手机充值";//选择充值卡面值界面
    public static String Phone_Recharge_Ad = "广告-手机充值";//选择充值卡面值界面
    public static String Phone_Recharge_Succes = "充值成功-手机充值";//选择充值卡面值界面

    public static String Phone_Recharge = "（首页、首页公共业务、定向业务、广告、充值成功）-手机充值";//从生活一级菜单选择手机充值进入充值页
    public static String Phone_Recharge_Success = "（首页、首页公共业务、定向业务、广告、充值成功）-手机充值-刷卡-刷卡成功";//手机充值成功
    public static String Phone_Recharge_Swipe = "（首页、首页公共业务、定向业务、广告、充值成功）-手机充值-刷卡";//手机充值刷卡页面

    public static String Phone_Recharge_Swipe_Home = "首页-手机充值-刷卡";//手机充值刷卡页面
    public static String Phone_Recharge_Swipe_Public = "首页公共业务-手机充值-刷卡";//手机充值刷卡页面
    public static String Phone_Recharge_Swipe_De = "定向业务-手机充值-刷卡";//手机充值刷卡页面
    public static String Phone_Recharge_Swipe_Ad = "广告-充值成功-手机充值-刷卡";//手机充值刷卡页面
    public static String Phone_Recharge_Swipe_Success = "充值成功-手机充值-刷卡";//手机充值刷卡页面


    public static String Phone_Recharge_Success_Public = "首页公共业务-手机充值-刷卡-刷卡成功";//手机充值成功
    public static String Phone_Recharge_Success_De = "定向业务-手机充值-刷卡-刷卡成功";//手机充值成功
    public static String Phone_Recharge_Success_Ad = "广告-手机充值-刷卡-刷卡成功";//手机充值成功
    public static String Phone_Recharge_Success_Succes = "充值成功-手机充值-刷卡-刷卡成功";//手机充值成功

    /**
     * ===================理财==================
     */
    public static String Balance_Inquiry = "（首页、首页公共业务、定向业务、广告、查询成功）-余额查询-(未关连刷卡器/关联刷卡器失败/刷卡失败)";//余额查询界面
    public static String Balance_Inquiry_Success = "（首页、首页公共业务、定向业务、广告、查询成功）-余额查询-刷卡-查询成功";//余额查询界面显示查询成功

    public static String Balance_Inquiry_Home = "首页-余额查询-余额查询界面";//余额查询界面
    public static String Balance_Inquiry_Public = "首页公共业务-余额查询-余额查询界面";//余额查询界面
    public static String Balance_Inquiry_De = "定向业务-余额查询-余额查询界面";//余额查询界面
    public static String Balance_Inquiry_Ad = "广告-余额查询-余额查询界面";//余额查询界面
    public static String Balance_Inquiry_Succes = "查询成功-余额查询-余额查询界面";//余额查询界面

    public static String Balance_Inquiry_Home_Succes = "首页-余额查询-刷卡-查询成功";//余额查询界面显示查询成功
    public static String Balance_Inquiry_Public_Succes = "首页公共业务-余额查询-刷卡-查询成功";//余额查询界面显示查询成功
    public static String Balance_Inquiry_De_Succes = "定向业务-余额查询-刷卡-查询成功";//余额查询界面显示查询成功
    public static String Balance_Inquiry_Ad_Succes = "广告-余额查询-刷卡-查询成功";//余额查询界面显示查询成功
    public static String Balance_Inquiry_Succes_Succes = "查询成功-余额查询-刷卡-查询成功";//余额查询界面显示查询成功


    /**
     * ===================理财==================
     */
    public static String Finance_HomePage = "-理财";//从首页选择理财进入理财首页
    public static String Finance_Introduction = "-理财-业务说明";//理财业务说明
    public static String Finance_Product = "-理财-理财产品";//点击我要理财进入理财产品页面
    public static String Finance_Transaction_Details = "理财首页-交易明细";//从理财首页进入交易明细页
    public static String Finance_Assets_Transaction_Details = "-理财-交易明细";//从理财资产进入交易明细页
    public static String Finance = "-理财-理财资产";//从理财首页进入理财资产页
    public static String Finance_Product_Details = "-理财-理财产品_详情";//进入理财产品详情页
    public static String Finance_Product_Swipe = "-理财-理财产品_详情-购买理财-刷卡-未关连刷卡器";//刷卡汇款界面
    public static String Finance_Product_Swipe_Error = "-理财-理财产品_详情-购买理财-刷卡-刷卡失败";//刷卡汇款界面
    public static String Finance_Product_purchaseStyle = "-理财-理财产品_详情-购买理财";//购买理财方式设置界面
    public static String Finance_Product_takeOut = "-理财-理财资产-取出";
    public static String Finance_Product_takeOutSuccess = "-理财-理财产品-ID_详情";
    public static String Finance_Purchance_Success = "-理财-理财产品-%s_详情-购买理财-密码录入-购买成功";//购买理财成功页
    public static String Finance_Withdraw_Success = "理财-取出成功";//取出理财成功页
    public static String Advert_Finance_Purchance = "广告%s-理财-买入成功";//从某条广告进入理财业务并且进入交易成功页


    /**
     * ===================贷款==================
     */
    public static String Pay_Yor_You_Homepage = "首页-替你还";//从首页选择贷款进入替你还首页
    public static String Loan_List_Homepage = "-贷款";//从首页公共业务进入贷款
    public static String Loan_List_To_Product = "-贷款-查看贷款介绍";//从首页公共业务进入贷款，查看贷款产品介绍
    public static String Loan_List_To_Product_Applay = "-贷款-查看贷款介绍-立即申请";//从首页公共业务进入贷款，贷款产品介绍，并且在产品详情页点击立即申请
    public static String Loan_List_To_Applay = "-贷款-立即申请-填写申请信息";//从首页公共业务进入贷款，在该页面点击立即申请，填写申请信息
    public static String Loan_List_To_Applay_Write1 = "-贷款-立即申请-填写申请信息_贷款列表";//从首页公共业务进入贷款，在该页面点击立即申请，填写申请信息
    public static String Loan_Apply_Records = "-贷款-立即申请";//从首页公共业务进入贷款，在该页面点击立即申请
    public static String[] Loan_Apply_Records_end = {"全部记录", "浏览中", "申请中", "已放贷"};//从首页公共业务进入贷款，点击右上角“我的贷款”-全部记录（全部记录 浏览中 申请中 已放贷）
    public static String Loan_Apply_Records_All = "首页公共业务-我的贷款记录-";//从首页公共业务进入贷款，点击右上角“我的贷款”-全部记录（全部记录 浏览中 申请中 已放贷）
    public static String Loan_Apply_Records_All_Click = "首页公共业务-我的贷款记录-";//从首页公共业务进入贷款，点击右上角“我的贷款”-全部记录（全部记录 浏览中 申请中 已放贷）
    public static String Loan_List_Applay = "首页公共业务-我的贷款记录";//从首页公共业务进入贷款，点击右上角“我的贷款”
    public static String Loan_List_Product_Applay = "-贷款-查看贷款介绍-立即申请-填写申请信息";//从首页公共业务进入贷款，贷款产品介绍，并且在产品详情页点击立即申请后，填写申请信息
    public static String Loan_List_To_Applay_Write2 = "-贷款-查看贷款介绍-立即申请-填写申请信息_贷款列表";//从首页公共业务进入贷款，贷款产品介绍，并且在产品详情页点击立即申请后，填写申请信息
    public static String Loan_Pay_Yor_Yo = "-个人信息";//填写个人信息页面
    public static String Loan_Pay_Yor_Yo_Money = "-金额申请";//替你还金额申请界面
    public static String Loan_Pay_Yor_Yo_Money_Info = "-金额申请-业务说明";//替你还业务说明
    public static String Loan_Pay_Yor_Yo_Money_Detail = "-金额申请-历史记录-记录详情";//金额申请-历史记录-记录详情息
    public static String Loan_Pay_Yor_Yo_Money_Person = "-金额申请-个人信息";//个人信息
    public static String Loan_Pay_Yor_Yo_Money_Record = "-金额申请-历史记录";//替你还历史记录
    public static String Loan_Pay_Yor_Yo_Work_Bank_Info = "-进入替你还申请个人信息页面-工作信息页面";//填写工作信息页面
    public static String Loan_Pay_Yor_Yo_Work_Bank_Info1 = "-金额申请-个人信息-工作信息";//填写工作信息页面
    public static String Loan_Pay_Yor_Yo_Work_Bank_Info_Apllay = "-进入替你还申请个人信息页面-工作信息页面-信用卡信息页面-下一步审核";//立即申请
    public static String Loan_Pay_Yor_Yo_Work_Bank_Info_Apllay1 = "-金额申请-个人信息-工作信息-信用卡信息-立即申请";//立即申请
    public static String Loan_Pay_Yor_Yo_Work = "-进入替你还申请个人信息页面-工作信息页面-信用卡信息页面";//首页公共业务-贷款-该页面点击立即申请-进入替你还申请个人信息页面-工作信息页面-信用卡信息页面-下一步审核
    public static String Loan_Pay_Yor_Yo_Work1 = "--金额申请-个人信息-工作信息-信用卡信息";//首页公共业务-贷款-该页面点击立即申请-进入替你还申请个人信息页面-工作信息页面-信用卡信息页面-下一步审核
    public static String Pay_Yor_You_Success_Homepage = "首页-替你还-贷款成功";//从首页选择贷款进入替你还首页并且进入交易成功页
    public static String Advert_Pay_Yor_You_Success = "广告%s-替你还-贷款成功";//从某条广告进入替你还首页并且进入交易成功页


    /**
     * ===================活动专区==================
     */
    public static String Promotion_Homepage = "（首页、首页公共业务、定向业务、广告）-活动专区";//从首页进入活动专区首页
    public static String Promotion_Privilege_Purchase = "活动专区-专享购买";//从活动专区页进入专享购买活动页
    public static String Promotion_Privilege_Purchase_Success = "活动专区-专享购买-购买成功";//从活动专区页进入专享购买活动页并且进入交易成功页

    public static String Promotion_Home = "首页-活动专区";//从首页进入活动专区首页
    public static String Promotion_Public = "首页公共业务-活动专区";//从首页进入活动专区首页
    public static String Promotion_De = "定向业务-活动专区";//从首页进入活动专区首页
    public static String Promotion_Ad = "广告-活动专区";//从首页进入活动专区首页


    /**
     * ===================钱包==================
     */
    public static String Wallet_Homepage = "首页-钱包首页";//从首页进入钱包一级菜单页
    public static String Wallet_Recharge = "首页-钱包首页-零钱充值金额录入";//零钱充值金额录入界面
    public static String Wallet_Recharge_Success = "钱包-零钱充值-充值成功";//从钱包一级菜单选择零钱充值并且进入交易成功页
    public static String Advert_Recharge_Success = "广告(广告%s）-零钱充值-充值成功";//从某条广告进入零钱充值并且进入交易成功页
    public static String Wallet_Withdraw = "首页-钱包首页-零钱转出金额录入";//零钱转出录入界面
    public static String Wallet_Withdraw_Success = "首页-钱包首页-零钱转出金额录入-银行卡列表-添加银行卡-零钱转出成功界面";//零钱转出成功
    public static String Advert_Wallet_Withdraw_Success = "广告(广告%s)-零钱转出-转出成功";//从某条广告进入零钱转出并且进入交易成功页
    public static String Check_Red_Envelope = "首页-钱包首页-红包列表";//红包列表页面
    public static String Check_Red_Envelope_Rule = "首页-钱包首页-红包列表-红包规则";//红包使用规则页面
    public static String Check_Bankcard = "首页-钱包首页-银行卡列表";//银行卡列表页面
    public static String Wallet_Withdraw_BankCardSelect = "首页-钱包首页-零钱转出金额录入-银行卡列表";//银行卡选择列表
    public static String Check_Bankcard_Add_Success = "钱包-银行卡-添加银行卡成功";//从钱包一级菜单选择银行卡并且添加银行卡成功
    public static String Check_Bankcard_Delete_Success = "钱包-银行卡-删除银行卡成功";//从钱包一级菜单选择银行卡并且删除银行卡成功
    public static String Wallet_SwipingToCharge = "首页-钱包首页-零钱充值金额录入-充值方式确认-充值刷卡";//零钱充值刷卡操作界面
    public static String Wallet_Add_Bankcard = "首页-钱包首页-零钱转出金额录入-银行卡列表-添加银行卡";//银行卡添加界面
    public static String Wallet_Support_BankCard = "首页-钱包首页-零钱转出金额录入-银行卡列表-添加银行卡-支持银行列表";//银行卡支持


    /**
     * ===================商户管理==================
     */
    public static String Merchant_Merchant_Info_Upgtate_Comfirm = "首页-用户信息-用户信息-修改-升级";//升级按钮，进入升级审核状态
    public static String Merchant_Merchant_Info_Upgtate_Introduce = "首页-用户信息-用户信息-修改-业务说明";//业务说明页面
    public static String Merchant_Merchant_Info_Change_Comfirm = "首页-用户信息-用户信息-修改-重新审核";//审核按钮，进入重新审核状态
    public static String Merchant_Merchant_Info_Change = "-用户信息-用户信息-修改";//商户开通（升级）失败时，点击修改进入重新修改资料界面，可以修改审核未通过部分
    public static String Merchant_Merchant_Info_Upgtate = "-用户信息-用户信息-修改";//商户未升满级时，点击修改进入资料补充界面，可以补充进入下一级需要的资料信息。
    public static String Merchant_Merchant_Info = "-用户信息-用户信息";//用户基本信息页面
    public static String Merchant_Change_Name = "-用户信息-店铺名称";//店铺名称修改页面
    public static String Merchant_Change_Accout = "-用户信息-收款账户";//收款账户变更页面
    public static String Merchant_Change_Accout_BankList = "-用户信息-收款账户-查看支持银行";//查看支持银行
    public static String Merchant_Change_Accout_Submit = "首页-用户信息-收款账户-提交";//修改收款账户提交
    public static String Merchant_ll_msgType = "-用户信息-刷卡收款";//刷卡收款相关信息展开
    public static String Merchant_ll_jsType = "-用户信息-立即提款";//立即提款相关信息展开
    public static String Merchant_ll_smType = "-用户信息-扫码收款";//扫码收款相关信息展开
    public static String Merchant_ll_deType = "-用户信息-大额收款";//大额收款相关信息展开
    public static String Merchant_jsType = "-用户信息-立即提款开通";//商户未开通立即提款时，显示未开通字样，商户点击弹出提示框，商户点击确认后进入立即提款服务开通页面，商户点击取消留在当前页面
    public static String Merchant_jsType_Leave = "-用户信息-立即提款取消";//商户未开通立即提款时，显示未开通字样，商户点击弹出提示框，商户点击确认后进入立即提款服务开通页面，商户点击取消留在当前页面
    public static String Merchant_jsType_Open = "-用户信息-立即提款开通-申请开通";//弹出成功申请立即提款业务提示
    public static String Merchant_jsType_Rule = "-用户信息-立即提款开通-业务说明";//业务说明页
    public static String Merchant_jsType_Rule_Agreee = "-用户信息-立即提款开通-立即提款协议";//点击立即提款协议，进入到协议页面
    public static String Merchant_smType = "-用户信息-扫码收款开通-扫码收款";//点击确认后进入扫码收款二维码页面
    public static String Merchant_smType_Open = "-用户信息-扫码收款开通-扫码收款-扫码申请";//通过页面提示进行点击，进入扫码规则说明页面
    public static String Merchant_deType = "-用户信息-大额收款开通";//开通即进入到大额收款业务说明页
    public static String Merchant_deType_Open = "-用户信息-大额收款开通-信息填写";//点说明页的申请大额收款，进入提交申请页面
    public static String Merchant_deType_Applay = "-用户信息-大额收款开通-信息填写-提交审核";//点击提交申请，进入审核状态
    public static String Merchant_Info = "商户管理-商户信息";//从商户管理页选择商户信息进入商户信息详情页
    public static String Merchant_Info_Modify_Success = "商户管理-商户信息修改成功";//从商户管理页选择商户信息并且修改成功
    public static String Merchant_Info_Home = "商户开通";//商户开通首页（有注意事项）
    public static String Merchant_Info_Add = "商户开通-个人信息";//个人信息添加界面
    public static String Merchant_MerchantInfo_Add = "商户开通-个人信息-店铺信息";//店铺信息添加界面
    public static String Merchant_BankInfo_Add = "商户开通-个人信息-店铺信息-银行账号";//银行账户信息添加界面
    public static String Merchant_BankInfo_Add_Sub = "商户开通-个人信息-店铺信息-银行账号-提交";//提交审核成功界面
    public static String Update_Success_Merchant_Management = "商户管理-升级服务提交成功";//从商户管理页选择升级服务并且提交成功
    public static String Singn_Success_Merchant_Management = "商户管理-企业商户签约成功";//从商户管理页选择企业商户签约并且提交成功

    public static String Merchant_Management_MORE = "用户管理-更多服务";//从用户管理进入更多服务页面
    public static String FIRST_UPGRADE_SUCCESS = "完善资料-一次升级成功";//从用户管理进入完善资料页面填写资料并点”升级“进行升级
    public static String SECOND_UPGRADE_SUCCESS = "完善资料-二次升级";//从用户管理进入完善资料页面填写资料并点”升级“进行升级
    public static String THIRD_UPGRADE_SUCCESS = "完善资料-跑分升级";//从用户管理进入点击”升级“按钮进行升级
    public static String ACCOUNT_INFO_UPGRADE = "用户管理-用户信息-商户升级";//从用户管理页面进入用户信息（商户升级基本信息）
    public static String ACCOUNT_INFO_MERCHANT = "用户管理-用户信息-开通商户";//从用户管理页面进入用户信息（开通商户基本信息页面）
    public static String REGISTER_SUCCESS = "用户管理-商户开通";//未开通商户的用户点击立即开通跳转到商户开通页面填写资料直到点击提交审核按钮并提交成功
    public static String MENU_MERCHANT = "-用户信息";//用户信息
    public static String MENU_MERCHANT_UPdate = "-用户信息-商户升级";//商户升级信息修改并提交页面服务页面
    public static String MERCHANT_OPEN_RULE = "商户开通-个人信息-店铺信息-银行账号-开通协议";//收款宝业务合作协议页面
    public static String MERCHANT_OPEN_ApplySuceess = "商户开通-个人信息-店铺信息-银行账号-提交";//商户开通提交成功
    public static String Merchant_Level_Rules = "-用户信息-级别说明";//（首页、首页公共业务、消息中心）-用户信息-级别说明
    /**
     * ===================安全管理==================
     */
    public static String Security_Management_Homepage = "首页公共业务-密码管理";//从首页进入安全管理页
    public static String Security_Management_pwdChange = "首页公共业务-密码管理-修改登录密码";//从首页进入安全管理页
    public static String Security_Management_getSmsCode = "首页公共业务-密码管理-获取重置登录验证码";//从首页进入安全管理页
    public static String Security_Management_paypwdChange = "首页公共业务-密码管理-修改支付密码";//从首页进入安全管理页
    public static String Security_Management_getPaySmsCode = "首页公共业务-密码管理-获取重置支付验证码";//从首页进入安全管理页
    public static String Modify_Login_Pwd = "首页公共业务-密码管理-修改登录密码-修改成功";//从安全管理一级菜单选择修改登录密码并且修改成功
    public static String Modify_Login_czLoginPwd = "首页公共业务-密码管理-获取重置登录验证码-重置登录密码";//重置登录密码页面
    public static String Modify_Login_czLoginPwd_over = "首页公共业务-密码管理-获取重置登录验证码-重置登录密码-重置成功";//重置登录密码页面
    public static String Modify_Login_czLoginPwd_confirm = "首页公共业务-密码管理-修改支付密码-确认支付密码";//确认支付密码页面
    public static String Modify_Login_czLoginPwd_xiugaipwd = "首页公共业务-密码管理-获取重置支付验证码-修改支付密码";//修改支付密码页面
    public static String Modify_Login_czLoginPwd_confirmxiugai = "首页公共业务-密码管理-获取重置支付验证码-修改支付密码-确认支付密码";//修改支付密码页面
    public static String Set_Pay_Pwd_Success = "安全管理-设置支付密码成功";//从安全管理一级菜单选择设置支付密码并且设置成功
    public static String Modify_Pay_Pwd_Success = "安全管理-修改支付密码成功";//从安全管理一级菜单选择修改支付密码并且修改成功
    public static String Reset_Pay_Pwd_Success = "安全管理-重置支付密码成功";//从安全管理一级菜单选择重置支付密码并且重置成功
    public static String Exit_Login_Success = "首页公共业务-退出登录";//从安全管理一级菜单选择退出登录并且退出成功
    public static String Main_Pass_Admin = "首页公共业务-密码管理";//密码管理

    public static String MessageCenter_Trade = "消息中心-交易通知";//消息中心-交易通知


    /**
     * ===================更多==================
     */
    public static String More_Homenpage = "首页-更多";//从首页进入更多一级菜单页
    public static String Device_Management = "首页公共业务-更多-设备管理";//从更多一级菜单进入设备管理页
    public static String Transaction_Rules = "首页公共业务-更多-交易规则";//从更多一级菜单进入交易规则页

    public static String Level_Rules = "首页公共业务-用户信息-级别说明";//级别说明页面
    public static String Use_Help = "首页公共业务-更多-使用帮助";//从更多一级菜单进入使用帮助页
    public static String Follow_LaKaLa = "首页公共业务-更多-关注拉卡拉";//从更多一级菜单进入关注拉卡拉页
    public static String Join_ShouKuanBao = "更多-招商加盟";//从更多一级菜单进入招商加盟页
    public static String Buy_ShouKuanBao = "首页公共业务-更多-购买收款宝";//从更多一级菜单进入购买收款宝页
    public static String Aboout_Us = "首页公共业务-更多-关于我们";//从更多一级菜单进入关于我们页
    public static String More_MerchantSign = "首页公共业务-更多-企业商户签约";//
    public static String More_DeviceConnectManage = "首页公共业务-更多-设备管理-连接帮助";//
    public static String More_AddNewDevice = "首页公共业务-更多-设备管理-添加新设备";//


    /**
     * ===================一块夺宝==================
     */
    public static String Treasure_Buy_Homepage = "（首页、首页公共业务、定向业务、广告）-一块夺宝";//从首页进入一块夺宝页
    public static String Purchase_Product_Treasure_Buy = "一块夺宝-产品购买页";//从一块夺宝选择商品进入产品购买页
    public static String Purchase_Product_Success_Treasure_Buy = "一块夺宝-购买成功";//从一块夺宝选择商品进入产品购买页并进入交易成功页

    public static String Market_Mone_Success_Treasure_Buy = "活动购买成功-购买成功";//从广告活动选择商品进入产品购买页并进入交易成功页


    public static String Treasure_Buy_Home = "首页-一块夺宝";//从首页进入一块夺宝页
    public static String Treasure_Buy_Public = "首页公共业务-一块夺宝";//从首页进入一块夺宝页
    public static String Treasure_Buy_De = "定向业务-一块夺宝";//从首页进入一块夺宝页
    public static String Treasure_Buy_Ad = "广告-一块夺宝";//从首页进入一块夺宝页

    /**
     * ===================特约商户缴费==================
     */
    public static String Charge_Business_Selection = "首页-特约商户缴费";//从首页进入特约商户缴费页
    public static String Contribute_Payment_Amount_Input = "特约商户缴费-新疆招行福彩-交易成功";//从特约商户缴费以及菜单进入新疆招行福彩并且进入交易成功页
    public static String Rent_Collection_Amount = "特约商户缴费-平安收银宝-交易成功";//从特约商户缴费以及菜单进入平安收银宝并且进入交易成功页


    /**
     * ===================首页广告/分享==================
     */
    public static String Advert_ID = "点击广告(广告%s)";//从首页点击广告进入业务/广告详情页
    public static String wechatFriend = "分享广告（广告%s）到微信好友成功";//从广告详情页面选择分享到微信好友并且分享成功
    public static String wechatmoment = "分享广告（广告%s）到朋友圈成功";//在广告详情页面选择分享到朋友圈并且分享成功
    public static String weibo = "分享广告（广告%s）到微博成功";//在广告详情页面选择分享到微博并且分享成功
    public static String userMobile = "userMobile";//用户手机号
    public static String label = "label";
    /**
     * ===================消息中心==================
     */
    public static String MessageCenter_Business = "消息中心-业务通知";//从首页点击消息中心，进入业务通知页
    public static String MessageCenter_Publish = "消息中心-系统公告";//从首页点击消息中心，进入系统通知页
    public static String MessageCenter_Trade_scanCode = "消息中心-业务通知-扫码收款";//从首页点击消息中心，进入业务通知页，点击某一条消息的功能按钮，进入扫码收款业务
    public static String MessageCenter_Trade_firstUpgrade = "消息中心-业务通知-一类升级";//从首页点击消息中心，进入业务通知页，点击某一条消息中的“重新提交”，跳转至用户信息页面
    public static String MessageCenter_Trade_secondUpgrade = "消息中心-业务通知-二类升级";//从首页点击消息中心，进入业务通知页，点击某一条消息中的“重新提交”，跳转至用户信息页面
    public static String MessageCenter_Trade_merchantCheck = "消息中心-业务通知-商户审核";//从首页点击消息中心，进入业务通知页，点击某一条消息中的“重新提交”，跳转跳转至用户信息首页
    public static String MessageCenter_Trade_BLimit = "消息中心-业务通知-大额收款";//从首页点击消息中心，进入业务通知页，点击某一条消息的功能按钮，进入大额收款业务
    public static String MessageCenter_Trade_immediateWithdraw = "消息中心-业务通知-立即提款";//从首页点击消息中心，进入业务通知页，点击某一条消息的功能按钮，进入立即提款业务
    public static String MessageCenter_Trade_loan = "消息中心-业务通知-贷款";//从首页点击消息中心，进入业务通知页，点击某一条消息的功能按钮，进入贷款业务
    public static String MessageCenter_Trade_dealDetail = "消息中心-业务通知-交易详情";//从首页点击消息中心，进入交易通知页，点击某一条消息中的“查看详情”，跳转至交易详情页
    public static String MessageCenter_Publish_versionUpdate = "系统公告-版本升级";//从系统公告页面，点击跳转至版本升级
    public static String MessageCenter_Publish_business = "系统公告-跳转至业务";//从系统公告页面，点击跳转至业务
    public static String MessageCenter_Publish_detailsShare = "系统公告-详情页-分享";//从系统公告进入html页面，并点击分享
    /**
     * ===================转账汇款==================
     */
    public static String Transfer_Remittance_home = "首页-转账汇款";//汇款信息填写界面
    public static String Transfer_Remittance_public = "首页公共业务-转账汇款";//汇款信息填写界面
    public static String Transfer_Remittance_direction = "定向业务-转账汇款";//汇款信息填写界面
    public static String Transfer_Remittance_advert = "广告-转账汇款";//汇款信息填写界面
    public static String Transfer_Remittance = "汇款成功-转账汇款";//汇款信息填写界面
    public static String Transfer_Remittance_Success_home = "首页-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认-刷卡-汇款成功";
    public static String Transfer_Remittance_Success_direction = "定向业务-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认-刷卡-汇款成功";
    public static String Transfer_Remittance_Success_public = "首页公共业务-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认-刷卡-汇款成功";
    public static String Transfer_Remittance_Success_advert = "广告-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认-刷卡-汇款成功";
    public static String Transfer_commenPayee_home = "首页-转账汇款-常用收款人";//常用收款人页面
    public static String Transfer_commenPayee_advert = "广告-转账汇款-常用收款人";//常用收款人页面
    public static String Transfer_commenPayee_direction = "定向业务-转账汇款-常用收款人";//常用收款人页面
    public static String Transfer_commenPayee_public = "首页公共业务-转账汇款-常用收款人";//常用收款人页面
    public static String Transfer_commenPayee_transferSuccess = "汇款成功-转账汇款-常用收款人";//常用收款人页面
    public static String Transfer_supportbankList_home = "首页-转账汇款-查看支持银行";//查看支持银行列表页面
    public static String Transfer_supportbankList_direction = "定向业务）-转账汇款-查看支持银行";//查看支持银行列表页面
    public static String Transfer_supportbankList_public = "首页公共业务-转账汇款-查看支持银行";//查看支持银行列表页面
    public static String Transfer_supportbankList_advert = "广告-转账汇款-查看支持银行";//查看支持银行列表页面
    public static String Transfer_supportbankList_transferSuccess = "（首页、首页公共业务、定向业务、广告、汇款成功）-转账汇款-查看支持银行";//查看支持银行列表页面
    public static String Transfer_chargeRule_home = "首页-转账汇款-收费规则";//收费规则页面
    public static String Transfer_chargeRule_direction = "定向业务-转账汇款-收费规则";//收费规则页面
    public static String Transfer_chargeRule_public = "首页公共业务）-转账汇款-收费规则";//收费规则页面
    public static String Transfer_chargeRule_advert = "广告-转账汇款-收费规则";//收费规则页面
    public static String Transfer_chargeRule_transferSuccess = "（首页、首页公共业务、定向业务、广告、汇款成功）-转账汇款-收费规则";//收费规则页面
    public static String Transfer_readInstruction_home = "首页-转账汇款-汇款协议";//阅读汇款协议页面
    public static String Transfer_readInstruction_direction = "定向业务）-转账汇款-汇款协议";//阅读汇款协议页面
    public static String Transfer_readInstruction_public = "首页公共业务）-转账汇款-汇款协议";//阅读汇款协议页面
    public static String Transfer_readInstruction_advert = "广告-转账汇款-汇款协议";//阅读汇款协议页面
    public static String Transfer_readInstruction_transferSuccess = "（首页、首页公共业务、定向业务、广告、汇款成功）-转账汇款-汇款协议";//阅读汇款协议页面
    public static String Transfer_ingo = "首页、首页公共业务、定向业务、广告、汇款成功）-转账汇款-联系人（填/选）-到账时间（1/2）-短信（%s）-手机号（填/选）";//汇款信息填写界面
    public static String Transfer_confirm_home = "首页-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认";//确认转账信息界面
    public static String Transfer_confirm_direction = "定向业务-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认";//确认转账信息界面
    public static String Transfer_confirm_advert = "广告-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认";//确认转账信息界面
    public static String Transfer_confirm_public = "首页公共业务-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认";//确认转账信息界面
    public static String Transfer_confirm_huikuanSucess = "（首页、首页公共业务、定向业务、广告、汇款成功）-转账汇款-联系人（填/选）-到账时间（1/2）-短信（%s）-手机号（填/选）-信息确认";//确认转账信息界面
    public static String Transfer_SwipeSuccess = "（首页、首页公共业务、定向业务、广告、汇款成功）-转账汇款-联系人（填/选）-到账时间（1/2）-短信（%s）-手机号（填/选）-信息确认-（未关连刷卡器/关联刷卡器失败/刷卡失败）";//刷卡界面关联刷卡器成功，并刷卡成功
    public static String Transfer_payPage_home = "首页-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认-刷卡";//刷卡汇款界面
    public static String Transfer_payPage_advert = "广告-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认-刷卡";//刷卡汇款界面
    public static String Transfer_payPage_direction = "定向业务-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认-刷卡";//刷卡汇款界面
    public static String Transfer_payPage_public = "首页公共业务-转账汇款-联系人（%s）-到账时间（%s）-短信（%s）-手机号（%s）-信息确认-刷卡";//刷卡汇款界面

    /**
     * ============一日贷==================
     */
    public static String OneDayLoan_withdrawal = "立即提款-一日贷";//进入业务说明立即申请页面
    public static String OneDayLoan_direction = "定向业务-一日贷";//一日贷提款页

    public static String OneDayLoan_withdrawal_serviceDesc = "立即提款-一日贷-业务说明";//进入一日贷服务开通页面
    public static String OneDayLoan_direction_serviceDesc = "定向业务-一日贷-业务说明";//一日贷业务说明页面

    public static String OneDayLoan_withdrawal_serviceDesc_applied = "立即提款-一日贷-业务说明-申请提交";//进入一日贷申请提交界面
    public static String OneDayLoan_direction_inputmoney = "定向业务-一日贷-金额录入";//一日贷金额录入界面
    public static String OneDayLoan_direction_serviceDesc_applied = "定向业务-一日贷-业务说明-申请提交";//进入一日贷申请提交界面
    public static String OneDayLoan_withdrawal_inputmoney = "立即提款-一日贷-金额录入";//一日贷金额录入界面

    public static String OneDayLoan_withdrawal_serviceDesc_applied_open = "立即提款-一日贷-业务说明-申请提交-申请开通";//一日贷申请提交成功弹出提示界面
    public static String OneDayLoan_direction_serviceDesc_applied_open = "定向业务-一日贷-业务说明-申请提交-申请开通";//一日贷申请提交成功弹出提示界面
    public static String OneDayLoan_direction_inputmoney_submitfee = "定向业务-一日贷-金额录入-手续费确认";//一日贷手续费确认界面

    public static String OneDayLoan_withdrawal_serviceDesc_applied_open_agreement = "立即提款-一日贷-业务说明-申请提交-一日贷协议";//点击一日贷协议，进入到协议页面
    public static String OneDayLoan_direction_serviceDesc_applied_open_agreement = "定向业务-一日贷-业务说明-申请提交-一日贷协议";//点击一日贷协议，进入到协议页面
    public static String OneDayLoan_direction_inputmoney_submitfee_serviceDesc = "定向业务-一日贷-金额录入-手续费确认-业务说明";//一日贷业务说明页面

    public static String OneDayLoan_withdrawal_serviceDesc_applied_serDesc = "立即提款-一日贷-业务说明-申请提交-业务说明";//业务说明页
    public static String OneDayLoan_direction_serviceDesc_applied_serDesc = "定向业务-一日贷-业务说明-申请提交-业务说明";//业务说明页


    //无value
    public void onEvent(@Nullable String eventId, Context context) {
        LogUtil.print("events", eventId);
        if (TextUtils.isEmpty(eventId)) {
            com.lakala.library.util.LogUtil.print("exception eventId not be null");
            return;
        }
        onEvent(eventId, null, context);
    }
    //有value

    /**
     * 调用此方法，labelValue和mobile只有一个值
     *
     * @param eventId
     * @param labelValue
     */
    public void onEvent(String eventId, String labelValue, Context context) {
        Map<String, String> stringHashMap = new HashMap<String, String>();
        if (null != ApplicationEx.getInstance().getUser() && !"".equals(ApplicationEx.getInstance().getUser().getLoginName())) {
            stringHashMap.put(userMobile, ApplicationEx.getInstance().getUser().getLoginName());
        }
        if (null != labelValue) {
            stringHashMap.put(label, labelValue);
        }

        AVAnalytics.onEvent(context, eventId, stringHashMap);
    }

//    /**
//     * 调用此方法，labelValue和mobile只有一个值
//     * @param eventId
//     * @param labelValue
//     * @param map
//     */
//    public void onEvent(String eventId,String labelValue, Map<String, String> map, Context mContext){
//
//        if(null != labelValue){
//            map.put(label, labelValue);
//        }
//
//        AVAnalytics.onEvent(mContext, eventId, map);
//    }

}
