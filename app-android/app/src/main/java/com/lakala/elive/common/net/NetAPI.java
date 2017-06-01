package com.lakala.elive.common.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lakala.elive.EliveApplication;
import com.lakala.elive.Session;
import com.lakala.elive.beans.AreaDataBean;
import com.lakala.elive.beans.ApplyIdReq;
import com.lakala.elive.beans.ApplyIdResp;
import com.lakala.elive.beans.BankCardVerificationReq;
import com.lakala.elive.beans.BankCardVerificationResp;
import com.lakala.elive.beans.DeleteTaskListReq;
import com.lakala.elive.beans.DeleteTaskListResp;
import com.lakala.elive.beans.ForgetCodeReq;
import com.lakala.elive.beans.ForgetSubmitReq;
import com.lakala.elive.beans.MccDataBean;
import com.lakala.elive.beans.NoticeReq;
import com.lakala.elive.beans.NoticeResp;
import com.lakala.elive.beans.TaskListReq;
import com.lakala.elive.beans.TaskListReqResp;
import com.lakala.elive.common.net.req.GetPhotoReq;
import com.lakala.elive.common.net.req.MerApplyDetailsReq;
import com.lakala.elive.common.net.req.MerApplyInfoReq;
import com.lakala.elive.common.net.req.MerApplyInfoReq15;
import com.lakala.elive.common.net.req.MerApplyInfoReq16;
import com.lakala.elive.common.net.req.MerApplyInfoReq2;
import com.lakala.elive.common.net.req.MerApplyInfoReq3;
import com.lakala.elive.common.net.req.MerApplyInfoReq8;
import com.lakala.elive.common.net.req.MerApplyListReq;
import com.lakala.elive.common.net.req.MerDictionaryReq;
import com.lakala.elive.common.net.req.PhotoDiscernReq;
import com.lakala.elive.common.net.req.TaskListBaseReq;
import com.lakala.elive.common.net.req.UserReqInfo;
import com.lakala.elive.common.net.req.base.RequestInfo;
import com.lakala.elive.common.net.resp.DefReportRespInfo;
import com.lakala.elive.common.net.resp.DictDetailRespInfo;
import com.lakala.elive.common.net.resp.DictTypeRespInfo;
import com.lakala.elive.common.net.resp.FunMenuRespInfo;
import com.lakala.elive.common.net.resp.GetPhotoResq;
import com.lakala.elive.common.net.resp.MerApplyDetailsResp;
import com.lakala.elive.common.net.resp.MerApplyInfoRes;
import com.lakala.elive.common.net.resp.MerApplyInfoRes2;
import com.lakala.elive.common.net.resp.MerApplyInfoRes3;
import com.lakala.elive.common.net.resp.MerDetailRespInfo;
import com.lakala.elive.common.net.resp.MerDictionaryResp;
import com.lakala.elive.common.net.resp.MerShopPageRespInfo;
import com.lakala.elive.common.net.resp.MyMerchantsListResp;
import com.lakala.elive.common.net.resp.NoticePageRespInfo;
import com.lakala.elive.common.net.resp.PhotoDiscernResp;
import com.lakala.elive.common.net.resp.ReportRespInfo;
import com.lakala.elive.common.net.resp.ReportTypeRespInfo;
import com.lakala.elive.common.net.resp.TaskPageRespInfo;
import com.lakala.elive.common.net.resp.TaskRespInfo;
import com.lakala.elive.common.net.resp.TermDetailRespInfo;
import com.lakala.elive.common.net.resp.TermListRespInfo;
import com.lakala.elive.common.net.resp.UserLoginRespInfo;
import com.lakala.elive.common.net.resp.VersionRespInfo;
import com.lakala.elive.common.net.resp.VisitDetailRespInfo;
import com.lakala.elive.common.net.resp.VisitHisPageRespInfo;
import com.lakala.elive.common.net.resp.VoteItemListRespInfo;
import com.lakala.elive.common.net.resp.VotePageRespInfo;
import com.lakala.elive.common.net.resp.base.ResponseInfo;
import com.lakala.elive.common.utils.GsonJsonUtils;
import com.lakala.elive.common.utils.Logger;
import com.lakala.elive.map.bean.ShopLatLonAloneReque;
import com.lakala.elive.map.bean.ShopLatLonAloneRespon;
import com.lakala.elive.map.bean.ShopLatLonReque;
import com.lakala.elive.map.bean.ShopLatLonRespon;
import com.lakala.elive.market.merqcodebind.MerAccountRequ;
import com.lakala.elive.market.merqcodebind.MerAccountResp;
import com.lakala.elive.preenterpiece.request.PreEnPieceDetailRequ;
import com.lakala.elive.preenterpiece.request.PreEnPieceListRequ;
import com.lakala.elive.preenterpiece.request.PreEnPieceOcrPhoneRequ;
import com.lakala.elive.preenterpiece.request.PreEnPieceSubmitInfoRequ;
import com.lakala.elive.preenterpiece.response.PreEnPieceDetailResponse;
import com.lakala.elive.preenterpiece.response.PreEnPieceListResponse;
import com.lakala.elive.preenterpiece.response.PreEnPieceOcrPhoneResponse;
import com.lakala.elive.preenterpiece.response.PreEnPieceSubmitInfoResponse;
import com.lakala.elive.qcodeenter.request.QCodeBindRequ;
import com.lakala.elive.qcodeenter.request.QCodeListRequ;
import com.lakala.elive.qcodeenter.response.QCodeBindResponse;
import com.lakala.elive.qcodeenter.response.QCodeListResponse;


/**
 * 为网络数据请求提供统一的接口模块封装
 *
 * @author hongzhiliang
 */
public class NetAPI {

    public static final String TAG = NetAPI.class.getSimpleName();

    /*
     *  主机地址
     *
     *  http://10.7.40.137:8080/
     *
     *  10.7.39.139
     *  10.7.39.137
     *
     *  测试环境
     *           179  (esb)
     *  10.7.111.178  (web)
     *
     *  10.7.39.114
     *
     *  https://mwork.lakala.com:8443/
     *
     *  http://mwork.lakala.com:8088/
     *
     *  10.7.40.137
     *  http://10.7.40.137:8080/
     *
     *  公网地址
     *  http://101.230.219.150:8088/
     *  http://elive.lakala.com:8088/
     *  https://elive.lakala.com:8443/
     *  http://10.7.39.160:8080
     *
     *10.7.34.190
     *  #10.7.34.121
     *10.7.34.159
     */
       public static  String PUB_HOST = "https://elive.lakala.com:8443/";
//   public static  String ESB_HOST = "https://elive.lakala.com:8443/";

     public static String ESB_HOST = "http://10.7.111.179:8080/";
    //     public static String ESB_HOST = "http://10.7.40.86:8080/";
//    public static String ESB_HOST = "http://10.7.34.138:8080/";
    /**
     * 网络加密密钥
     **/
    public static final String netEncryKey = "$%^%^&*%^&*%^&";
    /**
     * 生产 host地址
     */
    public static final String PUB_DATA_API = PUB_HOST + "/elive-rainbow/rainbowFacade/";

    /**
     * 数据接口 host地址
     */
    public static final String ESB_DATA_API = ESB_HOST + "/elive-rainbow/rainbowFacade/";

    /*
     * Report 用户验证接口 和 UI 请求 host地址
     */
    public static final String REPORT_HOST = "https://elive.lakala.com:8443/";

    //Report 查询条件
    public static final String REPORT_DATA_QUERY_API = REPORT_HOST + "/lakala-report/mobile/report_mobile!init.action";

    //Report 查询结果
    public static final String REPORT_DATA_RESULT_API = REPORT_HOST + "/lakala-report/mobile/report_mobile!result.action";



    /**
     * ESB 接口 URL
     */
    public static final String[] API_URLS = {

            //user login 0
            ESB_DATA_API + "ELIVE_SYS_001.json",

            //user check code get 1
            ESB_DATA_API + "ELIVE_SYS_002.json",

            //user code check 2
            ESB_DATA_API + "ELIVE_SYS_003.json",

            //user pwd set 3
            ESB_DATA_API + "ELIVE_SYS_004.json",

            //GET DICT DATA 4
            ESB_DATA_API + "ELIVE_SYS_005.json",

            //GET Menu 5
            ESB_DATA_API + "ELIVE_SYS_006.json",

            //GET Vesrion Code
            ESB_DATA_API + "ELIVE_SYS_007.json",

            //merchant shop list
            ESB_DATA_API + "ELIVE_WF_001.json",

            //insert merchant visit log
            ESB_DATA_API + "ELIVE_WF_002.json",

            // mershop info detail
            ESB_DATA_API + "ELIVE_WF_003.json",

            // show visit log list
            ESB_DATA_API + "ELIVE_WF_004.json",

            // query elive edit mer shop info
            ESB_DATA_API + "ELIVE_WF_005.json",

            // submit elive edit mer shop info
            ESB_DATA_API + "ELIVE_WF_006.json",

            //终端列表
            ESB_DATA_API + "ELIVE_WF_007.json",

            //终端详情
            ESB_DATA_API + "ELIVE_WF_008.json",

            //拜访详情
            ESB_DATA_API + "ELIVE_WF_009.json",

            // 获取报表分类数据 16
            ESB_DATA_API + "MOB_RPT_010.json",

            // 获取报表列表数据 17
            ESB_DATA_API + "MOB_RPT_005.json",

            // 获取用户默认报表数据 18
            ESB_DATA_API + "MOB_RPT_006.json",

            // 新增关注 19
            ESB_DATA_API + "MOB_RPT_007.json",

            // 取消关注 20
            ESB_DATA_API + "MOB_RPT_008.json",

            // 关注列表 21
            ESB_DATA_API + "MOB_RPT_009.json",

            // 通知列表 22
            ESB_DATA_API + "ELIVE_MSG_001.json",

            // 通知确认 23
            ESB_DATA_API + "ELIVE_MSG_002.json",

            //投票列表 24
            ESB_DATA_API + "ELIVE_VOTE_001.json",

            //投票详情列表 25
            ESB_DATA_API + "ELIVE_VOTE_002.json",

            //用户新增投票 26
            ESB_DATA_API + "ELIVE_VOTE_003.json",

            //工单列表处理 27
            ESB_DATA_API + "ELIVE_WF_010.json",

            //更新工单状态接口 28
            ESB_DATA_API + "ELIVE_WF_011.json",

            //工单签到接口 29
            ESB_DATA_API + "ELIVE_WF_012.json",

            //进件接口 结算信息 30
            ESB_DATA_API + "ELIVE_MER_APPLY_001.json",

            //进件接口 营业执照信息 31
            ESB_DATA_API + "ELIVE_MER_APPLY_002.json",

            //进件接口 机具信息 32
            ESB_DATA_API + "ELIVE_MER_APPLY_003.json",

            //进件接口 附件上传 33
            ESB_DATA_API + "ELIVE_MER_APPLY_008.json",

            //工单详情 34
            ESB_DATA_API + "ELIVE_WF_013.json",

            //数据字典大类 35
            ESB_DATA_API + "ELIVE_SYS_008.json",

            //进件字典查询 36
            ESB_DATA_API + "ELIVE_MER_APPLY_009.json",

            //ocr图片识别接口 37
            ESB_DATA_API + "ELIVE_MER_APPLY_010.json",

            //查询商户进件申请列表	 38
            ESB_DATA_API + "ELIVE_MER_APPLY_006.json",

            //商户进件申请详情 39
            ESB_DATA_API + "ELIVE_MER_APPLY_007.json",

            //GET Vesrion Code 40
            ESB_DATA_API + "ELIVE_SYS_009.json",

            //GET Notice 41
            ESB_DATA_API + "ELIVE_MSG_003.json",

            //GET BANK_VERIFICATION 42
            ESB_DATA_API + "ELIVE_MER_APPLY_014.json",

            //GET applyId 43
            ESB_DATA_API + "ELIVE_MER_APPLY_013.json",

            //GET 获取照片接口 44
            ESB_DATA_API + "ELIVE_MER_APPLY_004.json",

            //驳回重新请求 45
            ESB_DATA_API + "ELIVE_MER_APPLY_015.json",

            //新增用户当前列表 46
            ESB_DATA_API + "ELIVE_TASK_ORDER_002.json",


            //当前用户任务列表获取47
            ESB_DATA_API + "ELIVE_TASK_ORDER_001.json",

            //删除用户任务列表获取48
            ESB_DATA_API + "ELIVE_TASK_ORDER_003.json",

            //人脸识别49
            ESB_DATA_API + "ELIVE_MER_APPLY_016.json",

             ////////预进件接口
            //商户预进件列表50
            ESB_DATA_API + "ELIVE_PARTNER_APPLY_001.json",
            //预进件详情51
            ESB_DATA_API + "ELIVE_PARTNER_APPLY_002.json",
            //提交商户基本信息52
            ESB_DATA_API + "ELIVE_PARTNER_APPLY_003.json",
            //OCR和图片上传53
            ESB_DATA_API + "ELIVE_PARTNER_APPLY_004.json",
            //预进件列表删除54
            ESB_DATA_API + "ELIVE_PARTNER_APPLY_005.json",
            //Q码的商户列表55
            ESB_DATA_API + "qcodeApplyList.json",
            //Q码绑定56
            ESB_DATA_API + "qCodeApplyBind.json",
            // 加载MCC 数据字典
            ESB_DATA_API + "loadMccInfoList.json",
            // 加载地区 数据字典
            ESB_DATA_API + "loadAreacInfoList.json",
            //Q码删除59
            ESB_DATA_API + "qCodeApplyUnBind.json",
            //存量商户的账户
            ESB_DATA_API + "getMerShopSettleList.json",
            //自动载入未完成工单
            ESB_DATA_API + "loadWfTaskList.json",
            // 导入历史未完成个人定制任务
            ESB_DATA_API + "loadHisOrderTaskList.json",
            // 获取多个网点经纬度
            PUB_DATA_API + "getShopLocationList.json",

            // 获取单个网点经纬度  64
            PUB_DATA_API + "getShopLocation.json",

    };




    /**
     * 登录
     */
    public static final int ACTION_USER_LOGIN_SUBMIT = 0;

    public static final int ACTION_GET_CHECK_CODE = 1;

    public static final int ACTION_USER_CHECK_SUBMIT = 2;

    public static final int ACTION_PWD_SET_SUBMIT = 3;

    public static final int ACTION_GET_DICT_DATA = 4;

    public static final int ACTION_GET_DICT_DATA_BIG = 35;

    public static final int ACTION_GET_FUNCTON_MENU = 5;

    public static final int ACTION_GET_APP_VESION_CODE = 6;

    public static final int ACTION_MER_PAGE_LIST = 7;

    public static final int ACTION_ADD_VISIT_LOG = 8;

    public static final int ACTION_MER_SHOP_DETAIL = 9;

    public static final int ACTION_VISIT_PAGE_LIST = 10;

    public static final int ACTION_GET_ELIVE_SHOP_INFO = 11;

    public static final int ACTION_EDIT_ELIVE_SHOP_INFO = 12;

    public static final int ACTION_SHOP_TERM_LIST = 13;

    public static final int ACTION_SHOP_TERM_DETAIL = 14;

    public static final int ACTION_SHOP_VISIT_DETAIL = 15;

    //public static final int ACTION_ORDER_LIST_PAGE = 9;

    //******************************************************************/

    /**
     * 获取报表分类数据
     */
    public static final int ACTION_REPORT_TYPE_LIST = 16;

    /**
     * 获取报表列表数据
     */
    public static final int ACTION_REPORT_INFO_LIST = 17;

    /**
     * 获取用户默认的报表数据
     */
    public static final int ACTION_USER_DEFAULT_REPORT = 18;

    /**
     * 新增关注
     */
    public static final int ACTION_ADD_ATTENTION = 19;

    /**
     * 取消关注
     */
    public static final int ACTION_REMOVE_ATTENTION = 20;

    /**
     * 关注列表
     */
    public static final int ACTION_LIST_ATTENTION = 21;

    /**
     * 关注列表
     */
    public static final int ACTION_LIST_MSG = 22;

    /**
     * 通知确认列表
     */
    public static final int ACTION_CHECK_MSG = 23;

    /**
     * 用户投票列表
     */
    public static final int ACTION_LIST_VOTE = 24;

    /**
     * 用户投票详情列表
     */
    public static final int ACTION_VOTE_DETAIL_LIST = 25;

    /**
     * 用户新增投票
     */
    public static final int ACTION_ADD_USER_VOTE = 26;

    /**
     * 外勤任务工单查询  27
     */
    public static final int ACTION_TASK_DEAL_LIST = 27;

    /**
     * 更新工单状态接口  28
     */
    public static final int ACTION_TASK_STATUS_UPDATE = 28;

    /**
     * 工单签到接口  29
     */
    public static final int ACTION_TASK_VISIT = 29;

    /**
     * 工单详情接口  34
     */
    public static final int ACTION_TASK_DETAIL = 34;

    /**
     * 进件接口 结算信息 30
     */
    public static final int ACTION_MER_APPLY = 30;

    /**
     * 进件接口 营业执照信息 31
     */
    public static final int ACTION_MER_APPLY2 = 31;

    /**
     * 进件接口 机具信息 31
     */
    public static final int ACTION_MER_APPLY3 = 32;

    /**
     * 进件接口 机具信息 32
     */
    public static final int ACTION_MER_APPLY8 = 33;

    /**
     * 进件字典查询 36
     */
    public static final int ACTION_MER_APPLY_DICTIONARY = 36;

    /**
     * ocr图片识别接口 37
     */
    public static final int ACTION_PHOTO_DISCERN = 37;

    /**
     * 查询商户进件申请列表 38
     */
    public static final int ACTION_MER_APPLY_LIST = 38;

    /**
     * 查询商户进件申请列表 39
     */
    public static final int ACTION_MER_APPLY_DETAILS = 39;

    public static final int ACTION_FORGET_SUBMIT = 40;

    public static final int ACTION_NOTICE = 41;

    public static final int ACTION_BANK_VERIFICATION = 42;

    public static final int ACTION_APPLYID = 43;

    //获取照片
    public static final int ACTION_MER_APPLY4 = 44;

    //驳回重新提交
    public static final int ACTION_MER_APPLY_015 = 45;

    //新增用户当前列表
    public static final int ACTION_ADD_DOT_AND_ORDER = 46;

    //当前用户任务列表获取
    public static final int ACTION_GET_TASK_LIST = 47;

    //删除用户任务列表获取
    public static final int ACTION_DELETE_TASK_LIST = 48;

    //人脸识别
    public static final int ACTION_MER_APPLY_016 = 49;

    /**
     * 商户预进件列表
     */
    public static final int ELIVE_PARTNER_APPLY_001 = 50;
    /**
     * 预进件详情
     */
    public static final int ELIVE_PARTNER_APPLY_002 = 51;
    /**
     * 预进件提交商户基本信息
     */
    public static final int ELIVE_PARTNER_APPLY_003 = 52;
    /**
     * 预进件OCR和图片上传
     */
    public static final int ELIVE_PARTNER_APPLY_004 = 53;

    /**
     * 预进件列表删除
     */
    public static final int ELIVE_PARTNER_APPLY_005 = 54;
    /**
     * Q码绑定的商户列表
     */
    public static final int qcodeApplyList = 55;
    /**
     * Q码绑定
     */
    public static final int qCodeApplyBind = 56;
    /**
     * 加载MCC 数据字典
     */
    public static final int loadMccInfoList = 57;
    /**
     * 加载地址 数据字典
     */
    public static final int loadAreacInfoList = 58;
    /**
     * Q码删除
     */
    public static final int qCodeApplyUnBind=59;

    /**
     * 获取存量商户的账户
     */
    public static final int getMerShopSettleList=60;
    /**
     *自动载入未完成工单
     */
    public static final int loadWfTaskList=61;
    /**
     *导入历史未完成个人定制任务
     */
    public static final int loadHisOrderTaskList=62;

    /**
     *获取多个网点经纬度
     */
    public static final int   getShopLocationList=63;


    /**
     *获取单个网点经纬度
     */
    public static final int  getShopLocation=64;

    //验证成功
    public static final String SUCCESS = "00";

    //用户名或密码错误
    public static final String ERROR_PWD = "01";
    //无效用户
    public static final String INVALID = "02";
    //账号锁定
    public static final String LOCKED = "03";
    //未知错误
    public static final String OTHER = "99";

    public static final String SYS_CLIENT_ERR = "网络异常!";
    public static final String SYS_SERVER_ERR = "服务器端异常";


    //bmcp数据字典查询
    public static final int BMCP_SELECTOR_REQUEST = 0;
    public static final int BANK = 110;//银行查询
    public static final int MER_BIZ_CONTENT = 111;//经营内容查询
    public static final int SHOP_AREA = 112;//营业面积查询
    public static final int SETTLE_PERIOD = 113;//结算周期
    public static final int DEVICE_DRAW_METHOD = 114;//终端领用方式
    public static final int TERMINAL_TYPE = 115;

    public static final int SETTLE_PERIOD_Q = 116;//Q码结算周期

    //bmcp地区查询 1
    public static final int BMCP_REGION_REQUEST = 1;
    //bmcp mcc查询 1
    public static final int BMCP_MCC_REQUEST = 2;

    private static int defaultRequestTime = 15 * 1000;

    /**
     * 用户 ESB Session 登录验证
     *
     * @param context
     * @param handler
     */
    public static void userLoginSubmit(Context context, final ApiRequestListener handler, UserReqInfo loginReqInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<UserLoginRespInfo> gsonRequest = null;
        try {
            loginReqInfo.setDevCode(Session.get(context).getDeviceId());
//            loginReqInfo.setDevCode("867581024066665");
            gsonRequest = new GsonRequest<UserLoginRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_USER_LOGIN_SUBMIT],
                    GsonJsonUtils.parseObj2Json(loginReqInfo),
                    UserLoginRespInfo.class,
                    new Response.Listener<UserLoginRespInfo>() {// 成功处理
                        public void onResponse(UserLoginRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode())) {
                                //需要进行业务的相关逻辑处理
                                handler.onSuccess(ACTION_USER_LOGIN_SUBMIT, obj.getContent());
                            } else {
                                handler.onError(ACTION_USER_LOGIN_SUBMIT, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_USER_LOGIN_SUBMIT, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_USER_LOGIN_SUBMIT, SYS_CLIENT_ERR);
        }
    }

    /**
     * 获取验证码
     *
     * @param context
     * @param handler
     */
    public static void getUserCheckCode(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_CHECK_CODE],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            Logger.i(TAG, "getUserCheckCode onResponse:" + obj);
                            if (SUCCESS.equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_GET_CHECK_CODE, obj.getMessage());
                            } else {
                                handler.onError(ACTION_GET_CHECK_CODE, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_GET_CHECK_CODE, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_GET_CHECK_CODE, SYS_CLIENT_ERR);
        }
    }


    public static void getForgetCode(Context context, final ApiRequestListener handler, ForgetCodeReq requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_CHECK_CODE],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            Logger.i(TAG, "getUserCheckCode onResponse:" + obj);
                            if (SUCCESS.equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_GET_CHECK_CODE, obj.getMessage());
                            } else {
                                handler.onError(ACTION_GET_CHECK_CODE, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_GET_CHECK_CODE, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_GET_CHECK_CODE, SYS_CLIENT_ERR);
        }
    }

    /**
     * 获取常用数据字典
     *
     * @param context
     * @param handler
     */
    public static void getDictDataList(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<DictDetailRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<DictDetailRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_DICT_DATA],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    DictDetailRespInfo.class,
                    new Response.Listener<DictDetailRespInfo>() {// 成功处理
                        public void onResponse(DictDetailRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                Logger.e("ACTION_GET_DICT_DATA", "getDictDataList:" + obj.getContent());
                                handler.onSuccess(ACTION_GET_DICT_DATA, obj.getContent());
                            } else {
                                handler.onError(ACTION_GET_DICT_DATA, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_GET_DICT_DATA, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_GET_DICT_DATA, SYS_CLIENT_ERR);
        }
    }

    /**
     * 获取常用数据字典
     * 根据附加大类
     *
     * @param context
     * @param handler
     */
    public static void getDictDataListByBig(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<DictTypeRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<DictTypeRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_DICT_DATA_BIG],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    DictTypeRespInfo.class,
                    new Response.Listener<DictTypeRespInfo>() {// 成功处理
                        public void onResponse(DictTypeRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_GET_DICT_DATA_BIG, obj.getContent());
                            } else {
                                handler.onError(ACTION_GET_DICT_DATA_BIG, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_GET_DICT_DATA_BIG, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_GET_DICT_DATA_BIG, SYS_CLIENT_ERR);
        }
    }

    /**
     * 获取功能菜单
     *
     * @param context
     * @param handler
     */
    public static void getFunctionMenu(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<FunMenuRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<FunMenuRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_FUNCTON_MENU],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    FunMenuRespInfo.class,
                    new Response.Listener<FunMenuRespInfo>() {// 成功处理
                        public void onResponse(FunMenuRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                Logger.i("getFunctionMenu", "" + obj.getContent());
                                handler.onSuccess(ACTION_GET_FUNCTON_MENU, obj.getContent());
                            } else {
                                handler.onError(ACTION_GET_FUNCTON_MENU, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_GET_FUNCTON_MENU, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_GET_FUNCTON_MENU, SYS_CLIENT_ERR);
        }
    }

    /**
     * 获取应用版本
     *
     * @param context
     * @param handler
     */
    public static void getAppVersionCode(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<VersionRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<VersionRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_APP_VESION_CODE],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    VersionRespInfo.class,
                    new Response.Listener<VersionRespInfo>() {// 成功处理
                        public void onResponse(VersionRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_GET_APP_VESION_CODE, obj.getContent());
                            } else {
                                handler.onError(ACTION_GET_APP_VESION_CODE, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_GET_APP_VESION_CODE, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_GET_APP_VESION_CODE, SYS_CLIENT_ERR);
        }
    }

    /**
     * 提交受信码验证
     *
     * @param context
     * @param handler
     */
    public static void userCheckSubmit(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_USER_CHECK_SUBMIT],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_USER_CHECK_SUBMIT, null);
                            } else {
                                handler.onError(ACTION_USER_CHECK_SUBMIT, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_USER_CHECK_SUBMIT, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_USER_CHECK_SUBMIT, SYS_CLIENT_ERR);
        }
    }

    /**
     * 用户密码修改
     *
     * @param context
     * @param handler
     */
    public static void userPwdsetSubmit(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_PWD_SET_SUBMIT],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_PWD_SET_SUBMIT, obj.getMessage());
                            } else {
                                handler.onError(ACTION_PWD_SET_SUBMIT, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_PWD_SET_SUBMIT, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_PWD_SET_SUBMIT, SYS_CLIENT_ERR);
        }
    }

    /**
     * 新增商户拜访记录
     *
     * @param context
     * @param handler
     */
    public static void addMerVisitLog(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_ADD_VISIT_LOG],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_ADD_VISIT_LOG, obj.getMessage());
                            } else {
                                handler.onError(ACTION_ADD_VISIT_LOG, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_ADD_VISIT_LOG, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            Logger.e("addMerVisitLog:", "Exception:" + e.toString());
            e.printStackTrace();
            handler.onError(ACTION_ADD_VISIT_LOG, SYS_CLIENT_ERR);
        }
    }

    /**
     * 分页
     * <p>
     * 查询商户网点拜访记录列表
     *
     * @param context
     * @param handler
     */
    public static void queryMerVisitList(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<VisitHisPageRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<VisitHisPageRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_VISIT_PAGE_LIST],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    VisitHisPageRespInfo.class,
                    new Response.Listener<VisitHisPageRespInfo>() {// 成功处理
                        public void onResponse(VisitHisPageRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_VISIT_PAGE_LIST, obj.getContent());
                            } else {
                                handler.onError(ACTION_VISIT_PAGE_LIST, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_VISIT_PAGE_LIST, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_ADD_VISIT_LOG, SYS_CLIENT_ERR);
        }
    }

    /**
     * 分页查询
     * 商户网点信息列表
     *
     * @param context
     * @param handler
     */
    public static void queryMerShopList(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {

        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerShopPageRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerShopPageRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_PAGE_LIST],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    MerShopPageRespInfo.class,
                    new Response.Listener<MerShopPageRespInfo>() {// 成功处理
                        public void onResponse(MerShopPageRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_MER_PAGE_LIST, obj.getContent());
                            } else {
                                handler.onError(ACTION_MER_PAGE_LIST, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_PAGE_LIST, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_MER_PAGE_LIST, SYS_CLIENT_ERR);
        }
    }

    /**
     * 商户网点信息详情
     *
     * @param context
     * @param handler
     * @param requestInfo
     */
    public static void queryMerShopInfoDetail(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerDetailRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerDetailRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_SHOP_DETAIL],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    MerDetailRespInfo.class,
                    new Response.Listener<MerDetailRespInfo>() {// 成功处理
                        public void onResponse(MerDetailRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                Logger.e("queryTermDetail", "queryTermDetail" + obj.getContent());
                                handler.onSuccess(ACTION_MER_SHOP_DETAIL, obj.getContent());
                            } else {
                                handler.onError(ACTION_MER_SHOP_DETAIL, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_SHOP_DETAIL, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_MER_SHOP_DETAIL, SYS_CLIENT_ERR);
        }
    }

    /**
     * Elive平台商户网点信息详情
     *
     * @param context
     * @param handler
     * @param requestInfo
     */
    public static void queryEliveMerShopInfoDetail(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerDetailRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerDetailRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_ELIVE_SHOP_INFO],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    MerDetailRespInfo.class,
                    new Response.Listener<MerDetailRespInfo>() {// 成功处理
                        public void onResponse(MerDetailRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_GET_ELIVE_SHOP_INFO, obj.getContent());
                            } else {
                                handler.onError(ACTION_GET_ELIVE_SHOP_INFO, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_GET_ELIVE_SHOP_INFO, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_GET_ELIVE_SHOP_INFO, SYS_CLIENT_ERR);
        }
    }

    /**
     * 提交编辑商户网点信息详情
     *
     * @param context
     * @param handler
     * @param requestInfo
     */
    public static void editMerShopInfoDetail(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_EDIT_ELIVE_SHOP_INFO],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_EDIT_ELIVE_SHOP_INFO, obj.getMessage());
                            } else {
                                handler.onError(ACTION_EDIT_ELIVE_SHOP_INFO, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_EDIT_ELIVE_SHOP_INFO, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {

            e.printStackTrace();
            handler.onError(ACTION_EDIT_ELIVE_SHOP_INFO, SYS_CLIENT_ERR);
        }
    }

    /**
     * 查询网点终端列表
     *
     * @param context
     * @param handler
     */
    public static void queryShopTermList(final Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<TermListRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<TermListRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_SHOP_TERM_LIST],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    TermListRespInfo.class,
                    new Response.Listener<TermListRespInfo>() {// 成功处理
                        public void onResponse(TermListRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_SHOP_TERM_LIST, obj.getContent());
                            } else {
                                handler.onError(ACTION_SHOP_TERM_LIST, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_SHOP_TERM_LIST, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_SHOP_TERM_LIST, SYS_CLIENT_ERR);
        }
    }

    /**
     * 查询网点终端详情
     *
     * @param context
     * @param handler
     */
    public static void queryTermDetail(final Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<TermDetailRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<TermDetailRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_SHOP_TERM_DETAIL],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    TermDetailRespInfo.class,
                    new Response.Listener<TermDetailRespInfo>() {// 成功处理
                        public void onResponse(TermDetailRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                Logger.e("queryTermDetail", "queryTermDetail" + obj.getContent());
                                handler.onSuccess(ACTION_SHOP_TERM_DETAIL, obj.getContent());
                            } else {
                                handler.onError(ACTION_SHOP_TERM_DETAIL, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_SHOP_TERM_DETAIL, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_SHOP_TERM_DETAIL, SYS_CLIENT_ERR);
        }
    }

    /**
     * 查询网点拜访详情
     *
     * @param context
     * @param handler
     */
    public static void queryVisitDetail(final Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<VisitDetailRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<VisitDetailRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_SHOP_VISIT_DETAIL],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    VisitDetailRespInfo.class,
                    new Response.Listener<VisitDetailRespInfo>() {// 成功处理
                        public void onResponse(VisitDetailRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_SHOP_VISIT_DETAIL, obj.getContent());
                            } else {
                                handler.onError(ACTION_SHOP_VISIT_DETAIL, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_SHOP_VISIT_DETAIL, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_SHOP_VISIT_DETAIL, SYS_CLIENT_ERR);
        }
    }

    //*******************************报表相关***************************************/

    /**
     * 数据类型获取 Demo
     *
     * @param context
     * @param handler
     */
    public static void getReportTypeList(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ReportTypeRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ReportTypeRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_REPORT_TYPE_LIST],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    ReportTypeRespInfo.class,
                    new Response.Listener<ReportTypeRespInfo>() {// 成功处理
                        public void onResponse(ReportTypeRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_REPORT_TYPE_LIST, obj.getContent());
                            } else {
                                handler.onError(ACTION_REPORT_TYPE_LIST, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_REPORT_TYPE_LIST, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_REPORT_TYPE_LIST, SYS_CLIENT_ERR);
        }
    }


    /**
     * 默认报表数据获取
     *
     * @param context
     * @param handler
     */
    public static void getDefaultReportInfo(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<DefReportRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<DefReportRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_USER_DEFAULT_REPORT],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    DefReportRespInfo.class,
                    new Response.Listener<DefReportRespInfo>() {// 成功处理
                        public void onResponse(DefReportRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                if (obj != null) {
                                    handler.onSuccess(ACTION_USER_DEFAULT_REPORT, obj.getContent());
                                } else {
                                    handler.onError(ACTION_USER_DEFAULT_REPORT, obj.getMessage());
                                }
                            } else {
                                handler.onError(ACTION_USER_DEFAULT_REPORT, obj.getMessage());
                            }

                        }
                    },
                    new Response.ErrorListener() {// 出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_USER_DEFAULT_REPORT, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_USER_DEFAULT_REPORT, SYS_CLIENT_ERR);
        }
    }

    /**
     * 报表列表数据获取
     *
     * @param context
     * @param handler
     */
    public static void getReportInfoList(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ReportRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ReportRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_REPORT_INFO_LIST],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ReportRespInfo.class,
                    new Response.Listener<ReportRespInfo>() {// 成功处理
                        public void onResponse(ReportRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                if (obj != null) {
                                    handler.onSuccess(ACTION_REPORT_INFO_LIST, obj.getContent());
                                } else {
                                    handler.onError(ACTION_REPORT_INFO_LIST, obj.getMessage());
                                }
                            } else {
                                handler.onError(ACTION_REPORT_INFO_LIST, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_REPORT_INFO_LIST, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_REPORT_INFO_LIST, SYS_CLIENT_ERR);
        }
    }

    /**
     * 新增关注
     *
     * @param context
     * @param handler
     * @param requestInfo
     */
    public static void addAttention(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        try {
            Log.e(TAG, "addAttention   " + GsonJsonUtils.parseObj2Json(requestInfo));
            GsonRequest<ResponseInfo> gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_ADD_ATTENTION],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            Log.e(TAG, "addAttention onResponse  " + obj);
                            Logger.e("addAttention:", "addAttention：" + obj.getMessage());
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_ADD_ATTENTION, obj.getMessage());
                            } else {
                                handler.onError(ACTION_ADD_ATTENTION, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "addAttention onErrorResponse " + error.getMessage());
                            handler.onError(ACTION_ADD_ATTENTION, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_ADD_ATTENTION, SYS_CLIENT_ERR);
        }
    }

    /**
     * 移除关注
     *
     * @param context
     * @param handler
     * @param requestInfo
     */
    public static void removeAttention(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        try {
            GsonRequest<ResponseInfo> gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_REMOVE_ATTENTION],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_REMOVE_ATTENTION, obj.getMessage());
                            } else {
                                handler.onError(ACTION_REMOVE_ATTENTION, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_REMOVE_ATTENTION, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_REMOVE_ATTENTION, SYS_CLIENT_ERR);
        }
    }

    /**
     * 移除关注
     *
     * @param context
     * @param handler
     * @param requestInfo
     */
    public static void listAttention(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ReportRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ReportRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_LIST_ATTENTION],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ReportRespInfo.class,
                    new Response.Listener<ReportRespInfo>() {// 成功处理
                        public void onResponse(ReportRespInfo obj) {
                            // 需要进行业务的相关逻辑处理
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                if (obj != null) {
                                    handler.onSuccess(ACTION_LIST_ATTENTION, obj.getContent());
                                } else {
                                    handler.onError(ACTION_LIST_ATTENTION, obj.getMessage());
                                }
                            } else {
                                handler.onError(ACTION_LIST_ATTENTION, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_LIST_ATTENTION, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_SHOP_VISIT_DETAIL, SYS_CLIENT_ERR);
        }

    }


    /**
     * 通知列表
     *
     * @param context
     * @param handler
     */
    public static void listNoticeInfo(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<NoticePageRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<NoticePageRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_LIST_MSG],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    NoticePageRespInfo.class,
                    new Response.Listener<NoticePageRespInfo>() {// 成功处理
                        public void onResponse(NoticePageRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_LIST_MSG, obj.getContent());
                            } else {
                                handler.onError(ACTION_LIST_MSG, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_LIST_MSG, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行

        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_LIST_MSG, SYS_CLIENT_ERR);
        }
    }

    /**
     * 更新通知阅读
     *
     * @param context
     * @param handler
     */
    public static void updateNoticeRead(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {

        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_CHECK_MSG],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_CHECK_MSG, obj.getMessage());
                            } else {
                                handler.onError(ACTION_CHECK_MSG, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_CHECK_MSG, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_CHECK_MSG, SYS_CLIENT_ERR);
        }

    }


    /**
     * 投票列表
     *
     * @param context
     * @param handler
     */
    public static void listVoteInfo(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<VotePageRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<VotePageRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_LIST_VOTE],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    VotePageRespInfo.class,
                    new Response.Listener<VotePageRespInfo>() {// 成功处理
                        public void onResponse(VotePageRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_LIST_VOTE, obj.getContent());
                            } else {
                                handler.onError(ACTION_LIST_VOTE, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_LIST_VOTE, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_LIST_VOTE, SYS_CLIENT_ERR);
        }
    }


    /**
     * 查询投票列表
     *
     * @param context
     * @param handler
     */
    public static void listVoteItemDetail(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<VoteItemListRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<VoteItemListRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_VOTE_DETAIL_LIST],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    VoteItemListRespInfo.class,
                    new Response.Listener<VoteItemListRespInfo>() {// 成功处理
                        public void onResponse(VoteItemListRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {

                                handler.onSuccess(ACTION_VOTE_DETAIL_LIST, obj.getContent());
                            } else {
                                handler.onError(ACTION_VOTE_DETAIL_LIST, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_VOTE_DETAIL_LIST, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_VOTE_DETAIL_LIST, SYS_CLIENT_ERR);
        }

    }


    /**
     * 新增用户投票
     *
     * @param context
     * @param handler
     */
    public static void addUserVote(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {

        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_ADD_USER_VOTE],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_ADD_USER_VOTE, obj.getMessage());
                            } else {
                                handler.onError(ACTION_ADD_USER_VOTE, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_ADD_USER_VOTE, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_ADD_USER_VOTE, SYS_CLIENT_ERR);
        }

    }

    /**
     * 分页查询
     * 工单信息列表
     *
     * @param context
     * @param handler
     */
    public static void queryTaskDealList(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<TaskPageRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<TaskPageRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_TASK_DEAL_LIST],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    TaskPageRespInfo.class,
                    new Response.Listener<TaskPageRespInfo>() {// 成功处理
                        public void onResponse(TaskPageRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_TASK_DEAL_LIST, obj.getContent());
                            } else {
                                handler.onError(ACTION_TASK_DEAL_LIST, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_TASK_DEAL_LIST, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_TASK_DEAL_LIST, SYS_CLIENT_ERR);
        }
    }


    /**
     * 用户工单状态更新
     *
     * @param context
     * @param handler
     */
    public static void taskStatusUpdate(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {

        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_TASK_STATUS_UPDATE],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_TASK_STATUS_UPDATE, obj.getMessage());
                            } else {
                                handler.onError(ACTION_TASK_STATUS_UPDATE, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_TASK_STATUS_UPDATE, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_TASK_STATUS_UPDATE, SYS_CLIENT_ERR);
        }

    }


    /**
     * 工单任务签到
     *
     * @param context
     * @param handler
     */
    public static void addTaskVisit(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {

        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_TASK_VISIT],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_TASK_VISIT, obj.getMessage());
                            } else {
                                handler.onError(ACTION_TASK_VISIT, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_TASK_VISIT, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_TASK_VISIT, SYS_CLIENT_ERR);
        }

    }

    /**
     * 工单任务签到
     *
     * @param context
     * @param handler
     */
    public static void getTaskDetail(Context context, final ApiRequestListener handler, RequestInfo requestInfo) {

        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<TaskRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<TaskRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_TASK_DETAIL],
                    GsonJsonUtils.parseObj2Json(requestInfo), //用户验证信息
                    TaskRespInfo.class,
                    new Response.Listener<TaskRespInfo>() {// 成功处理
                        public void onResponse(TaskRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_TASK_DETAIL, obj.getContent());
                            } else {
                                handler.onError(ACTION_TASK_DETAIL, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_TASK_DETAIL, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
            handler.onError(ACTION_TASK_VISIT, SYS_CLIENT_ERR);
        }
    }


    /**
     * 进件 结算信息
     *
     * @param context
     * @param handler
     * @param req
     */
    public static void merApply(Context context, final ApiRequestListener handler, MerApplyInfoReq req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerApplyInfoRes> gsonRequest = null;

        try {
            gsonRequest = new GsonRequest<MerApplyInfoRes>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY],
                    GsonJsonUtils.parseObj2Json(req),
                    MerApplyInfoRes.class,
                    new Response.Listener<MerApplyInfoRes>() {
                        @Override
                        public void onResponse(MerApplyInfoRes response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.cancelAll(ACTION_MER_APPLY);
            gsonRequest.setTag(ACTION_MER_APPLY);
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进件 营业执照信息
     */
    public static void merApply2(Context context, final ApiRequestListener handler, final MerApplyInfoReq2 req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerApplyInfoRes2> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerApplyInfoRes2>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY2],
                    GsonJsonUtils.parseObj2Json(req),
                    MerApplyInfoRes2.class,
                    new Response.Listener<MerApplyInfoRes2>() {
                        @Override
                        public void onResponse(MerApplyInfoRes2 response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY2, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY2, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY2, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 进件 营业执照信息
     */
    public static void merApply3(Context context, final ApiRequestListener handler, MerApplyInfoReq3 req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerApplyInfoRes3> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerApplyInfoRes3>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY3],
                    GsonJsonUtils.parseObj2Json(req),
                    MerApplyInfoRes3.class,
                    new Response.Listener<MerApplyInfoRes3>() {
                        @Override
                        public void onResponse(MerApplyInfoRes3 response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY3, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY3, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY3, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进件 营业执照信息
     */
    public static void merApply8(Context context, final ApiRequestListener handler, MerApplyInfoReq8 req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerApplyInfoRes> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerApplyInfoRes>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY8],
                    GsonJsonUtils.parseObj2Json(req),
                    MerApplyInfoRes.class,
                    new Response.Listener<MerApplyInfoRes>() {
                        @Override
                        public void onResponse(MerApplyInfoRes response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY8, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY8, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY8, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进件字典查询
     */
    public static void merDictionaryReq(Context context, final ApiRequestListener handler, MerDictionaryReq req, final int Type) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerDictionaryResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerDictionaryResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY_DICTIONARY],
                    GsonJsonUtils.parseObj2Json(req),
                    MerDictionaryResp.class,
                    new Response.Listener<MerDictionaryResp>() {
                        @Override
                        public void onResponse(MerDictionaryResp response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                if (response.getContent().isSuccess()){
                                    handler.onSuccess(Type, response);
                                }else {
                                    handler.onError(Type, response.getContent().getMessage());
                                }
                            } else {
                                handler.onError(Type, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(Type, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * ocr图片识别接口
     */

    public static RequestQueue photoDiscernReq(Context context, final ApiRequestListener handler, final PhotoDiscernReq req, final int type) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<PhotoDiscernResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<PhotoDiscernResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_PHOTO_DISCERN],
                    GsonJsonUtils.parseObj2Json(req),
                    PhotoDiscernResp.class,
                    new Response.Listener<PhotoDiscernResp>() {
                        @Override
                        public void onResponse(PhotoDiscernResp response) {
                            if(response == null){
                                handler.onError(type, "返回为空!");
                                return;
                            }
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(type, response);
                            } else {
                                handler.onError(type, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(type, SYS_SERVER_ERR);
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000, 0, 1.0f));
            gsonRequest.setTag(ACTION_PHOTO_DISCERN);
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mQueue;
    }

    /**
     * 查询商户进件申请列表
     */
    public static void merApplyListReq(Context context, final ApiRequestListener handler, MerApplyListReq req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MyMerchantsListResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MyMerchantsListResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY_LIST],
                    GsonJsonUtils.parseObj2Json(req),
                    MyMerchantsListResp.class,
                    new Response.Listener<MyMerchantsListResp>() {
                        @Override
                        public void onResponse(MyMerchantsListResp response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY_LIST, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY_LIST, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY_LIST, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 商户进件申请详情
     */
    public static void merApplyDetailsReq(Context context, final ApiRequestListener handler, MerApplyDetailsReq req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerApplyDetailsResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerApplyDetailsResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY_DETAILS],
                    GsonJsonUtils.parseObj2Json(req),
                    MerApplyDetailsResp.class,
                    new Response.Listener<MerApplyDetailsResp>() {
                        @Override
                        public void onResponse(MerApplyDetailsResp response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY_DETAILS, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY_DETAILS, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY_DETAILS, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void forgetsubmit(Context context, final ApiRequestListener handler, ForgetSubmitReq requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_FORGET_SUBMIT],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            Logger.i(TAG, "getUserCheckCode onResponse:" + obj);
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_FORGET_SUBMIT, obj.getMessage());
                            } else {
                                handler.onError(ACTION_FORGET_SUBMIT, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_FORGET_SUBMIT, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_FORGET_SUBMIT, SYS_CLIENT_ERR);
        }
    }


    public static void getNotify(Context context, final ApiRequestListener handler, NoticeReq requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<NoticeResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<NoticeResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_NOTICE],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    NoticeResp.class,
                    new Response.Listener<NoticeResp>() {// 成功处理
                        public void onResponse(NoticeResp obj) {
                            Logger.i(TAG, "getUserCheckCode onResponse:" + obj);
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_NOTICE, obj);
                            } else {
                                handler.onError(ACTION_NOTICE, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_NOTICE, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_NOTICE, SYS_CLIENT_ERR);
        }
    }


    public static void bankCardVerification(Context context, final ApiRequestListener handler, BankCardVerificationReq requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<BankCardVerificationResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<BankCardVerificationResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_BANK_VERIFICATION],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    BankCardVerificationResp.class,
                    new Response.Listener<BankCardVerificationResp>() {// 成功处理
                        public void onResponse(BankCardVerificationResp obj) {
                            Logger.i(TAG, "getUserCheckCode onResponse:" + obj);
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_BANK_VERIFICATION, obj);
                            } else {
                                handler.onError(ACTION_BANK_VERIFICATION, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_BANK_VERIFICATION, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_BANK_VERIFICATION, SYS_CLIENT_ERR);
        }
    }

    public static void getApplyId(Context context, final ApiRequestListener handler, ApplyIdReq requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ApplyIdResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ApplyIdResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_APPLYID],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ApplyIdResp.class,
                    new Response.Listener<ApplyIdResp>() {// 成功处理
                        public void onResponse(ApplyIdResp obj) {
                            Logger.i(TAG, "getUserCheckCode onResponse:" + obj);
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(ACTION_APPLYID, obj);
                            } else {
                                handler.onError(ACTION_APPLYID, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_APPLYID, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(ACTION_APPLYID, SYS_CLIENT_ERR);
        }
    }

    /**
     * 进件 获取照片
     */
    public static void merApply4(Context context, final ApiRequestListener handler, GetPhotoReq req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<GetPhotoResq> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<GetPhotoResq>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY4],
                    GsonJsonUtils.parseObj2Json(req),
                    GetPhotoResq.class,
                    new Response.Listener<GetPhotoResq>() {
                        @Override
                        public void onResponse(GetPhotoResq response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY4, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY4, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY4, "查找图片失败");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 驳回重新提交
     */
    public static void merApply15(Context context, final ApiRequestListener handler, MerApplyInfoReq15 req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerApplyInfoRes3> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerApplyInfoRes3>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY_015],
                    GsonJsonUtils.parseObj2Json(req),
                    MerApplyInfoRes3.class,
                    new Response.Listener<MerApplyInfoRes3>() {
                        @Override
                        public void onResponse(MerApplyInfoRes3 response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY_015, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY_015, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY_015, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 人脸识别结果上传
     */
    public static void merApply16(Context context, final ApiRequestListener handler, MerApplyInfoReq16 req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerApplyInfoRes3> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerApplyInfoRes3>(
                    Request.Method.POST,
                    API_URLS[ACTION_MER_APPLY_016],
                    GsonJsonUtils.parseObj2Json(req),
                    MerApplyInfoRes3.class,
                    new Response.Listener<MerApplyInfoRes3>() {
                        @Override
                        public void onResponse(MerApplyInfoRes3 response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_MER_APPLY_016, response);
                            } else {
                                handler.onError(ACTION_MER_APPLY_016, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_MER_APPLY_016, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 任务添加网点
     */
    public static void addDotAndorder(Context context, final ApiRequestListener handler, DotAndorderReq req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<DotAndorderResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<DotAndorderResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_ADD_DOT_AND_ORDER],
                    GsonJsonUtils.parseObj2Json(req),
                    DotAndorderResp.class,
                    new Response.Listener<DotAndorderResp>() {
                        @Override
                        public void onResponse(DotAndorderResp response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_ADD_DOT_AND_ORDER, response);
                            } else {
                                handler.onError(ACTION_ADD_DOT_AND_ORDER, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_ADD_DOT_AND_ORDER, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除任务
     * @param context
     * @param handler
     * @param req
     */
    public static void deleteTaskList(Context context, final ApiRequestListener handler, DeleteTaskListReq req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<DeleteTaskListResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<DeleteTaskListResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_DELETE_TASK_LIST],
                    GsonJsonUtils.parseObj2Json(req),
                    DeleteTaskListResp.class,
                    new Response.Listener<DeleteTaskListResp>() {
                        @Override
                        public void onResponse(DeleteTaskListResp response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_DELETE_TASK_LIST, response);
                            } else {
                                handler.onError(ACTION_DELETE_TASK_LIST, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_DELETE_TASK_LIST, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求任务列表
     * @param context
     * @param handler
     * @param req
     */
    public static void getTaskList(Context context, final ApiRequestListener handler, TaskListReq req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<TaskListReqResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<TaskListReqResp>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_TASK_LIST],
                    GsonJsonUtils.parseObj2Json(req),
                    TaskListReqResp.class,
                    new Response.Listener<TaskListReqResp>() {
                        @Override
                        public void onResponse(TaskListReqResp response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ACTION_GET_TASK_LIST, response);
                            } else {
                                handler.onError(ACTION_GET_TASK_LIST, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ACTION_GET_TASK_LIST, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////预进件接口
    /**
     * 预进件商户列表
     */
    public static void preEnterPieListRequest(Context context, final ApiRequestListener handler, PreEnPieceListRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<PreEnPieceListResponse> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<PreEnPieceListResponse>(
                    Request.Method.POST,
                    API_URLS[ELIVE_PARTNER_APPLY_001],
                    GsonJsonUtils.parseObj2Json(req),
                    PreEnPieceListResponse.class,
                    new Response.Listener<PreEnPieceListResponse>() {
                        @Override
                        public void onResponse(PreEnPieceListResponse response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                Log.e("QQQQQQQQQQ","QQQQQQQQQQQQQQQ");
                                handler.onSuccess(ELIVE_PARTNER_APPLY_001, response);
                            } else {
                                Log.e("WWWWWWWWWW","WWWWWWWWWWWWWW");
                                handler.onError(ELIVE_PARTNER_APPLY_001, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ELIVE_PARTNER_APPLY_001, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 预进件商户详情
     */
    public static void preEnterPieDetailRequest(Context context, final ApiRequestListener handler, PreEnPieceDetailRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<PreEnPieceDetailResponse> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<PreEnPieceDetailResponse>(
                    Request.Method.POST,
                    API_URLS[ELIVE_PARTNER_APPLY_002],
                    GsonJsonUtils.parseObj2Json(req),
                    PreEnPieceDetailResponse.class,
                    new Response.Listener<PreEnPieceDetailResponse>() {
                        @Override
                        public void onResponse(PreEnPieceDetailResponse response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ELIVE_PARTNER_APPLY_002, response);
                            } else {
                                handler.onError(ELIVE_PARTNER_APPLY_002, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ELIVE_PARTNER_APPLY_002, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 预进件提交商户基本信息
     */
    public static void preEnterPieSubmitInfoRequest(Context context, final ApiRequestListener handler, PreEnPieceSubmitInfoRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<PreEnPieceSubmitInfoResponse> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<PreEnPieceSubmitInfoResponse>(
                    Request.Method.POST,
                    API_URLS[ELIVE_PARTNER_APPLY_003],
                    GsonJsonUtils.parseObj2Json(req),
                    PreEnPieceSubmitInfoResponse.class,
                    new Response.Listener<PreEnPieceSubmitInfoResponse>() {
                        @Override
                        public void onResponse(PreEnPieceSubmitInfoResponse response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ELIVE_PARTNER_APPLY_003, response);
                            } else {
                                handler.onError(ELIVE_PARTNER_APPLY_003, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ELIVE_PARTNER_APPLY_003, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 预进件OCR和图片上传
     */
    public static void preEnterPieOcrOrPhoneRequest(Context context, final ApiRequestListener handler, PreEnPieceOcrPhoneRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<PreEnPieceOcrPhoneResponse> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<PreEnPieceOcrPhoneResponse>(
                    Request.Method.POST,
                    API_URLS[ELIVE_PARTNER_APPLY_004],
                    GsonJsonUtils.parseObj2Json(req),
                    PreEnPieceOcrPhoneResponse.class,
                    new Response.Listener<PreEnPieceOcrPhoneResponse>() {
                        @Override
                        public void onResponse(PreEnPieceOcrPhoneResponse response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ELIVE_PARTNER_APPLY_004, response);
                            } else {
                                handler.onError(ELIVE_PARTNER_APPLY_004, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ELIVE_PARTNER_APPLY_004, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 预进件列表删除
     */
    public static void preEnterPieListDeleRequest(Context context, final ApiRequestListener handler, PreEnPieceDetailRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<PreEnPieceDetailResponse> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<PreEnPieceDetailResponse>(
                    Request.Method.POST,
                    API_URLS[ELIVE_PARTNER_APPLY_005],
                    GsonJsonUtils.parseObj2Json(req),
                    PreEnPieceDetailResponse.class,
                    new Response.Listener<PreEnPieceDetailResponse>() {
                        @Override
                        public void onResponse(PreEnPieceDetailResponse response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(ELIVE_PARTNER_APPLY_005, response);
                            } else {
                                handler.onError(ELIVE_PARTNER_APPLY_005, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(ELIVE_PARTNER_APPLY_005, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Q码的列表查询
     */
    public static void qCodeListRequest(Context context, final ApiRequestListener handler, QCodeListRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<QCodeListResponse> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<QCodeListResponse>(
                    Request.Method.POST,
                    API_URLS[qcodeApplyList],
                    GsonJsonUtils.parseObj2Json(req),
                    QCodeListResponse.class,
                    new Response.Listener<QCodeListResponse>() {
                        @Override
                        public void onResponse(QCodeListResponse response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(qcodeApplyList, response);
                            } else {
                                handler.onError(qcodeApplyList, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(qcodeApplyList, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Q码绑定
     */
    public static void qCodeBindRequest(Context context, final ApiRequestListener handler, QCodeBindRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<QCodeBindResponse> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<QCodeBindResponse>(
                    Request.Method.POST,
                    API_URLS[qCodeApplyBind],
                    GsonJsonUtils.parseObj2Json(req),
                    QCodeBindResponse.class,
                    new Response.Listener<QCodeBindResponse>() {
                        @Override
                        public void onResponse(QCodeBindResponse response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(qCodeApplyBind, response);
                            } else {
                                handler.onError(qCodeApplyBind, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(qCodeApplyBind, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Q码绑定删除
     */
    public static void qCodeDelectRequest(Context context, final ApiRequestListener handler, QCodeBindRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<QCodeBindResponse> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<QCodeBindResponse>(
                    Request.Method.POST,
                    API_URLS[qCodeApplyUnBind],
                    GsonJsonUtils.parseObj2Json(req),
                    QCodeBindResponse.class,
                    new Response.Listener<QCodeBindResponse>() {
                        @Override
                        public void onResponse(QCodeBindResponse response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(qCodeApplyUnBind, response);
                            } else {
                                handler.onError(qCodeApplyUnBind, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(qCodeApplyUnBind, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取mcc数据字典
     * @param context
     * @param handler
     */
    public static void getMccDataRequest(Context context, final ApiRequestListener handler,final int code) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MccDataBean> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MccDataBean>(
                    Request.Method.POST,
                    API_URLS[loadMccInfoList],
                    "",
                    MccDataBean.class,
                    new Response.Listener<MccDataBean>() {
                        @Override
                        public void onResponse(MccDataBean response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(code, response);
                            } else {
                                handler.onError(code, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(code, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 地址数据字典获取
     * @param context
     * @param handler
     */
    public static void getAddressDataRequest(Context context, final ApiRequestListener handler,final int code) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<AreaDataBean> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<AreaDataBean>(
                    Request.Method.POST,
                    API_URLS[loadAreacInfoList],
                    "",
                    AreaDataBean.class,
                    new Response.Listener<AreaDataBean>() {
                        @Override
                        public void onResponse(AreaDataBean response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(code, response);
                            } else {
                                handler.onError(code, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(code, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAllDictDataList(Context context,
                                          final ApiRequestListener handler,
                                          RequestInfo requestInfo,
                                          final int code) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<DictDetailRespInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<DictDetailRespInfo>(
                    Request.Method.POST,
                    API_URLS[ACTION_GET_DICT_DATA],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    DictDetailRespInfo.class,
                    new Response.Listener<DictDetailRespInfo>() {// 成功处理
                        public void onResponse(DictDetailRespInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(code, obj);
                            } else {
                                handler.onError(code, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(code, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            handler.onError(code, SYS_CLIENT_ERR);
        }
    }


    /**
     * 获取存量网点的结算账户
     */
    public static void getMerAccountRequest(Context context, final ApiRequestListener handler, MerAccountRequ req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<MerAccountResp> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<MerAccountResp>(
                    Request.Method.POST,
                    API_URLS[getMerShopSettleList],
                    GsonJsonUtils.parseObj2Json(req),
                    MerAccountResp.class,
                    new Response.Listener<MerAccountResp>() {
                        @Override
                        public void onResponse(MerAccountResp response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(getMerShopSettleList, response);
                            } else {
                                handler.onError(getMerShopSettleList, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(getMerShopSettleList, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动载入未完成工单
     * @param context
     * @param handler
     * @param requestInfo
     */
    public static void loadWfTaskList(Context context, final ApiRequestListener handler, TaskListBaseReq requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[loadWfTaskList],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(loadWfTaskList, obj.getMessage());
                            } else {
                                handler.onError(loadWfTaskList, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(loadWfTaskList, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {

            e.printStackTrace();
            handler.onError(loadWfTaskList, SYS_CLIENT_ERR);
        }
    }

    /**
     * 导入历史未完成个人定制任务
     * @param context
     * @param handler
     * @param requestInfo
     */
    public static void loadHisOrderTaskList(Context context, final ApiRequestListener handler, TaskListBaseReq requestInfo) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ResponseInfo> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ResponseInfo>(
                    Request.Method.POST,
                    API_URLS[loadHisOrderTaskList],
                    GsonJsonUtils.parseObj2Json(requestInfo),
                    ResponseInfo.class,
                    new Response.Listener<ResponseInfo>() {// 成功处理
                        public void onResponse(ResponseInfo obj) {
                            if (SUCCESS.equals(obj.getResultCode()) || "SUCCESS".equals(obj.getResultCode())) {
                                handler.onSuccess(loadHisOrderTaskList, obj.getMessage());
                            } else {
                                handler.onError(loadHisOrderTaskList, obj.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {// 系统出错处理
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(loadHisOrderTaskList, SYS_SERVER_ERR);
                        }
                    });
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {

            e.printStackTrace();
            handler.onError(ACTION_EDIT_ELIVE_SHOP_INFO, SYS_CLIENT_ERR);
        }
    }


    /**
     * 根据网点号获取经纬度
     */
    public static void getShopLatLonRequest(Context context, final ApiRequestListener handler, ShopLatLonReque req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ShopLatLonRespon> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ShopLatLonRespon>(
                    Request.Method.POST,
                    API_URLS[getShopLocationList],
                    GsonJsonUtils.parseObj2Json(req),
                    ShopLatLonRespon.class,
                    new Response.Listener<ShopLatLonRespon>() {
                        @Override
                        public void onResponse(ShopLatLonRespon response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(getShopLocationList, response);
                            } else {
                                handler.onError(getShopLocationList, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(getShopLocationList, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据单个网点号获取经纬度
     */
    public static void getShopLatLonAloneRequest(Context context, final ApiRequestListener handler, ShopLatLonAloneReque req) {
        RequestQueue mQueue = EliveApplication.getHttpQueue();
        GsonRequest<ShopLatLonAloneRespon> gsonRequest = null;
        try {
            gsonRequest = new GsonRequest<ShopLatLonAloneRespon>(
                    Request.Method.POST,
                    API_URLS[getShopLocation],
                    GsonJsonUtils.parseObj2Json(req),
                    ShopLatLonAloneRespon.class,
                    new Response.Listener<ShopLatLonAloneRespon>() {
                        @Override
                        public void onResponse(ShopLatLonAloneRespon response) {
                            if (response.getResultCode().equals(SUCCESS) || "SUCCESS".equals(response.getResultCode())) {
                                handler.onSuccess(getShopLocation, response);
                            } else {
                                handler.onError(getShopLocation, response.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handler.onError(getShopLocation, "网络异常,请稍后再试");
                        }
                    }
            );
            gsonRequest.setRetryPolicy(new DefaultRetryPolicy(defaultRequestTime, 0, 1.0f));
            mQueue.add(gsonRequest); // 执行
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}