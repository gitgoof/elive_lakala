package com.lakala.shoudan.bll.service;

import android.content.Context;
import android.text.TextUtils;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.activity.shoudan.webmall.privilege.PrivilegePurchaseTransInfo;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.AreaEntity;
import com.lakala.shoudan.datadefine.BaseException;
import com.lakala.shoudan.datadefine.Message;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 通用服务类接口 描述: 主要提供以下通用的接口
 * <p/>
 * <pre>
 * 		用户注册
 * 		用户开通
 * 		用户登录
 * 		获取省列表
 * 		获取区县列表
 * 		上传身份证照片
 * 		发送短信校验码
 * 		校验短信验证码
 * </pre>
 * <p/>
 * 在调用接口前必需调用 下列方法初始化 vercode:<br>
 * generateVercode(); <br>
 * 建议使用 getInstance 方法获取实例。
 *
 * @author bob
 */
public class CommonServiceManager extends BaseServiceManager {

    private static CommonServiceManager instance = null;

    /**
     * 构造方法私有化，只能通过静态方法getInstance方法获取
     *
     * @see #getInstance()
     */
    private CommonServiceManager() {

    }

    /**
     * 创建 CommonServiceManager 类实例
     *
     * @return
     * @{// NoSuchAlgorithmException
     */
    public static synchronized CommonServiceManager getInstance() {

        if (instance == null) {
            instance = new CommonServiceManager();
            instance.generateVercode();
        }
        return instance;
    }

    /******************************************** 用户中心相关 **************************************************/

    /**
     * @param loginName  登录名
     * @param password   口令
     * @param pwdLevel   密码等级
     * @param mobileNum  手机号
     * @param realName   真实姓名
     * @param email      Email
     * @param idCardType 身份证：ID,军官证：MILITARY_ID,学生证：STUDENT_CARD,护照：PASSPORT,其他：OTHER
     * @param idCardId   如果选择证件类型,则此项不能为空
     * @param province
     * @param city
     * @param district
     * @param homeAddr
     * @param zipCode
     * @return
     */
    public void register(
            String loginName, // 登录名
            String password, // 口令
            String pwdLevel,
            String mobileNum, // 手机号
            String realName, // 真实姓名
            String email, // email
            String idCardType, String idCardId, String province, String city,
            String district, String homeAddr, String zipCode, ServiceResultCallback serviceResultCallback) {

        String url = "register/";
        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("loginName", loginName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("mobileNum", mobileNum));
        nameValuePairs.add(new BasicNameValuePair("realName", realName));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("pwdSecLevel", pwdLevel));
        nameValuePairs.add(new BasicNameValuePair("idCardType", idCardType));
        nameValuePairs.add(new BasicNameValuePair("idCardInfo.idCardId", idCardId));
        nameValuePairs.add(new BasicNameValuePair("address.province", province));
        nameValuePairs.add(new BasicNameValuePair("address.city", city));
        nameValuePairs.add(new BasicNameValuePair("address.district", district));
        nameValuePairs.add(new BasicNameValuePair("address.homeAddr", homeAddr));
        nameValuePairs.add(new BasicNameValuePair("address.zipCode", zipCode));
        nameValuePairs.add(new BasicNameValuePair("imei", Util.getIMEI()));
        nameValuePairs.add(new BasicNameValuePair("imsi", Util.getIMSI()));

        postRequest(url.toString(), nameValuePairs, serviceResultCallback);


    }


    /**
     * 手续费查询  2.5.3去掉
     *
     * @param mobile
     * @param inpan    信用卡号
     * @param amount   还款金额
     * @param issms    是否发送短信 0-不发送，1-发送
     * @param mobileno 手机号码，Issms=1时，必选
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void qtShouXuFeiChaXun(
            String mobile,
            String inpan,
            String amount,
            String issms,
            String mobileno,
            String termid,
            ServiceResultCallback callback
    ) {


        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
        nameValuePairs.add(new BasicNameValuePair("inpan", Util.formatString(inpan)));
        nameValuePairs.add(new BasicNameValuePair("amount", amount));
        nameValuePairs.add(new BasicNameValuePair("issms", issms));
        nameValuePairs.add(new BasicNameValuePair("mobileno", mobileno));
        nameValuePairs.add(new BasicNameValuePair("termid", termid));
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
//        nameValuePairs.add(new BasicNameValuePair("chncode", CHN_CODE));
        //业务代码
        nameValuePairs.add(new BasicNameValuePair("busid", "M50001"));

        this.postRequest(
                Parameters.serviceURL.concat("queryTrans.json"),
                nameValuePairs, callback);

    }

    /**
     * 信用卡还款
     *
     * @param mobile
     * @param srcsid   上一次查询的sid号
     * @param inpan    信用卡号
     * @param amount   还款金额
     * @param otrack   磁道,借记卡磁道信息（包括2，3磁道）
     * @param pinkey   个人密码，用交互工作密钥加密上送
     * @param issms    是否发送短信 0-不发送，1-发送
     * @param mobileno 手机号码，Issms=1时，必选
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void ctXinYongKaHuanKuan(
            String mobile,
            String srcsid,
            String inpan,
            String amount,
            String otrack,
            String pinkey,
            String issms,
            String mobileno,
            String rnd,
            String track1,
            SwiperInfo swiperInfo,
            ServiceResultCallback callback
    ) {


        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
        nameValuePairs.add(new BasicNameValuePair("srcsid", srcsid));
        nameValuePairs.add(new BasicNameValuePair("inpan", Util.formatString(inpan)));
        nameValuePairs.add(new BasicNameValuePair("amount", amount));
        nameValuePairs.add(new BasicNameValuePair("otrack", otrack));
        nameValuePairs.add(new BasicNameValuePair("pinkey", pinkey));
        nameValuePairs.add(new BasicNameValuePair("issms", issms));
        nameValuePairs.add(new BasicNameValuePair("mobileno", mobileno));
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));//输入条件(跟安全相关

        nameValuePairs.add(new BasicNameValuePair("rnd", rnd));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));//终端流水号
        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getSession().getCurrentKSN()));
        // 业务代码ID（固定值）=18
        nameValuePairs.add(new BasicNameValuePair("busid", "M90000"));

        if (!"".equals(swiperInfo.getIcc55())) {
            nameValuePairs.add(new BasicNameValuePair("pan", swiperInfo.getMaskedPan()));
            nameValuePairs.add(new BasicNameValuePair("posemc", swiperInfo.getPosemc()));//
            nameValuePairs.add(new BasicNameValuePair("icc55", swiperInfo.getIcc55()));//
            nameValuePairs.add(new BasicNameValuePair("cardsn", swiperInfo.getCardsn()));
            nameValuePairs.add(new BasicNameValuePair("track2", swiperInfo.getTrack2()));
        }

        this.postRequest(
                Parameters.serviceURL.concat("commitTransaction.json"),
                nameValuePairs, callback);
    }


    /**
     * 款历史卡记录信息查询
     *
     * @param loginName
     * @param cardId    卡信息Id,用于单条记录查询
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void getUserCardRecordForXYKHK(String loginName,
                                          String cardId, ServiceResultCallback callback) {


        String url = ("/getUserCardRecordForXYKHK");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        this.getRequest(url.toString(), nameValuePairs, callback);

    }

    /**
     * 用户登录
     *
     * @param loginName 登录名称
     * @param password  登录密码
     * @return resultForService
     * @{// BaseException
     * @{// IOException
     * @{// ParseException
     */
    @SuppressWarnings("unused")
    public void getUserLoginState(String loginName, String password, String sigVerif, ServiceResultCallback serviceResultCallback) {// ParseException, IOException, BaseException {


        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL);
        url.append("getUserLoginState/");
        url.append(loginName);
        url.append(".json");
        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("loginName", loginName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("imei", Util.getIMEI()));
        nameValuePairs.add(new BasicNameValuePair("imsi", Util.getIMSI()));
        int versionCode = Integer.parseInt(Util.getVersionCode());
        if (!Parameters.debug && versionCode >= 13) {
            nameValuePairs.add(new BasicNameValuePair("sigVerif", sigVerif));
        }

        getRequest(url.toString(), nameValuePairs, serviceResultCallback);

    }

    /**
     * 修改密码
     *
     * @param loginName   登录名
     * @param password    旧密码
     * @param newPassword 新密码
     * @param pwdSecLevel 密码等级
     * @return resultForService
     * @{// BaseException
     * @{// IOException
     * @{// ParseException
     */
    public void updateUserPwd(String loginName, String password,
                              String newPassword, String pwdSecLevel, ServiceResultCallback serviceResultCallback) {// ParseException,{

        // 设置参数
        List<NameValuePair> nameValuePairs = createNameValuePair(true);
        nameValuePairs.add(new BasicNameValuePair("loginName", loginName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("newPassword", newPassword));
        nameValuePairs.add(new BasicNameValuePair("pwdSecLevel", pwdSecLevel));

        putRequest(
                Parameters.serviceURL.concat("updateUserPwd/" + loginName + ".json"),
                nameValuePairs, serviceResultCallback);

    }

    // 修改手机号码,暂时不实现

    // 修改手机号码??需要修改手机号码?

    /**
     * 检查用户是否可用状态
     *
     * @param loginName
     * @return
     * @{// BaseException
     * @{// IOException
     * @{// ParseException
     */
    public void getLoginNameState(String loginName, ServiceResultCallback serviceResultCallback) {// ParseException, IOException, BaseException {


        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL);
        url.append("getLoginNameState/");
        url.append(loginName);
        url.append(".json");
        getRequest(url.toString(), null, serviceResultCallback);

    }


    /**
     * 收款宝开通商户上传身份证照片
     *
     * @param loginName 登录名
     * @param idcard1   身份证照片文件数据1
     * @param idcard2   身份证照片文件数据2
     * @return
     * @{// ParseException
     * @{// IOException
     * @{// BaseException
     */
    public void idCardImageUploadPos(String loginName, String idcard1,
                                     String idcard2, String userName, String cardType, String cardNo, String email, ServiceResultCallback serviceResultCallback) {// ParseException, IOException, BaseException {

        String url = ("idCardImageUploadPOS/");

        Map<String, String> pics = new HashMap<>();
        pics.put("idcard1", idcard1);
        pics.put("idcard2", idcard2);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("userName", userName));
        nameValuePairs.add(new BasicNameValuePair("idCardType", cardType));
        nameValuePairs.add(new BasicNameValuePair("idCardId", cardNo));
        nameValuePairs.add(new BasicNameValuePair("email", email));
        postRequest(url.toString(), pics, nameValuePairs, serviceResultCallback);
    }

    public void merLevelFileUpload(String filePath, ServiceResultCallback callback) {
        String desc = "升级文件上传";
        String fileType = "UPGRADE_PIC";
        fileUpload(filePath, fileType, desc, desc, callback);
    }

    private void fileUpload(String filePath, String fileType, String fileDesc, String fileAddition, ServiceResultCallback callback) {
        String url = "fileUpload/";
        Map<String, String> files = new HashMap<>();
        files.put("uploadFileContent", filePath);
        List<NameValuePair> values = new ArrayList<>();
        values.add(new BasicNameValuePair("fileType", fileType));
        values.add(new BasicNameValuePair("fileDesc", fileDesc));
        values.add(new BasicNameValuePair("fileAddition", fileAddition));
        postRequest(url.toString(), files, values, callback);
    }


    /**
     * 发送短信校验码
     *
     * @param phoneNumber 电话号码
     * @param smsType     短信模板 = 1：普通验证码短信;2：重置密码短信;
     * @return resultForService
     * @{// BaseException
     * @{// IOException
     * @{// ParseException
     */
    public void getMobileVerifyCode(String phoneNumber,
                                    String smsType, ServiceResultCallback s) {// ParseException, IOException, BaseException {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("smsType", smsType));

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL);
        url.append("getMobileVerifyCode/");
        url.append(phoneNumber);
        url.append(".json");

        getRequest(url.toString(), nameValuePairs, s);


    }


    /**
     * 获取银行快速支付列表
     *
     * @param bankCode 银行code
     * @param busId    业务Id
     * @return
     * @{// ParseException
     * @{// IOException
     * @{// BaseException
     */
    public void getBankListForPay(String bankCode, String busId, ServiceResultCallback serviceResultCallback) {// ParseException, IOException, BaseException {
        // 设置参数
        StringBuffer url = new StringBuffer();
        url.append("getBankListForPay");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("bankCode", bankCode));

        if (!Util.isEmpty(busId)) {
            /*
             * 因为 2.2.0 以前版本中银行列表在显示一个本地没有图标的银行时会错误的显示银行图标，
			 * 所以从 3.0.0 版开始在 busid 后面添加"_1",这样就不会影响老版本的客户端。
			 */
            busId = busId.concat("_1");
            nameValuePairs.add(new BasicNameValuePair("busId", busId));
        }

        getRequest(url.toString(), nameValuePairs, serviceResultCallback);


    }

    /**
     * 获取银行快速支付列表 替你还  储蓄卡
     *
     * @param bankCode 银行code
     * @param busId    业务Id
     * @return
     * @{// ParseException
     * @{// IOException
     * @{// BaseException
     */
    public void getBankListForLoan(String bankCode, String busId, ServiceResultCallback s) {// ParseException, IOException, BaseException {
        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("bankCode", bankCode));


        nameValuePairs.add(new BasicNameValuePair("busId", busId));


        getRequest("getBankListForPay", nameValuePairs, s);

    }

    /**
     * 获取转账汇款手续费
     *
     * @param feeId 0：获取所有手续费   1：慢转账   2：快速转账    3：实时转账
     * @return
     * @{// ParseException
     * @{// IOException
     * @{// BaseException
     * @type 业务Id    false   string
     * @level 支付级别    false   string
     * @channel 支付渠道    false   string
     */
    public void getFee(String feeId, String type, String level, String channel, ServiceResultCallback serviceResultCallback) {// ParseException,

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL);
        url.append("getFee/");
        url.append(feeId);
        url.append(".json");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("type", type));
        nameValuePairs.add(new BasicNameValuePair("level", level));
        nameValuePairs.add(new BasicNameValuePair("channel", channel));
        getRequest(url.toString(), nameValuePairs, serviceResultCallback);

    }


    /**
     * 版本更新检查
     *
     * @param version 当前用户版本
     * @return
     * @{// ParseException
     * @{// IOException
     * @{// BaseException
     */
    public void checkAppUpdate(String version, ServiceResultCallback s) {// ParseException, IOException, BaseException {

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL);
        url.append("getShuakaqiList.json");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("version", version));
        getRequest(url.toString(), nameValuePairs, s);


    }


    /**
     * 根据银行卡号获取卡信息
     *
     * @param cardNo 银行卡号
     * @return
     * @{// BaseException
     * @{// ParseException
     * @{// IOException
     */
    public void getbankByCardNo(String cardNo, String bankCode, ServiceResultCallback serviceResultCallback) {// BaseException, ParseException, IOException {
        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL);
        url.append("getbankByCardNo/");
        url.append(Util.formatString(cardNo));
        url.append(".json");


        List<NameValuePair> nameValuePairs = createNameValuePair(false);
        nameValuePairs.add(new BasicNameValuePair("bankCode_tl", bankCode));
        getRequest(url.toString(), nameValuePairs, serviceResultCallback);

    }


    /**
     * 4.3版本之前获取消息列表
     *
     * @param loginName
     * @param msgType
     * @return
     */
    public void getMessageList(String loginName, Message.MSG_TYPE msgType, ServiceResultCallback serviceResultCallback) {// BaseException, IOException {
        getMessageList(loginName, msgType, false, serviceResultCallback);
    }

    public void getMessageList(String loginName, Message.MSG_TYPE msgType, boolean setRead, ServiceResultCallback callback) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("loginName", loginName));
        params.add(new BasicNameValuePair("page", "1"));
        params.add(new BasicNameValuePair("pageSize", "20"));
        params.add(new BasicNameValuePair("msgType", msgType.name()));
        params.add(new BasicNameValuePair("setRead", String.valueOf(setRead)));
        getRequest("message/msg", params, callback);
    }

    public void getIsOneDay(ServiceResultCallback callback) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        getRequest("business/d0/getThirdBusinessShow", params, callback);
    }

    public void getAd1(ServiceResultCallback callback) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        getRequest("infoDirectBus/msgSimple", params, callback);
    }

    /**
     * 新版本消息
     *
     * @param loginName             用户名
     * @param msgType               消息类型
     * @param serviceResultCallback 消息回调
     */
    public void getMessageList2(int page, int pageSize, String loginName, Message.MSG_TYPE msgType, ServiceResultCallback serviceResultCallback) {// BaseException, IOException {
        getMessageList2(page, pageSize, loginName, msgType, false, serviceResultCallback);
    }

    public void getMessageList2(int page, int pageSize, String loginName, Message.MSG_TYPE msgType, boolean setRead, ServiceResultCallback callback) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("loginName", loginName));
        params.add(new BasicNameValuePair("page", String.valueOf(page)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(pageSize)))
        ;
        params.add(new BasicNameValuePair("msgType", msgType.name()));
        params.add(new BasicNameValuePair("setRead", String.valueOf(setRead)));
        getRequest("sitemessage/msg", params, callback);
    }

    public void getAdBottomMessage(final ServiceResultCallback callback) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        getRequest("infoDirectBus/msgMulti", params, callback);
    }

    /**
     * 设置消息已读
     *
     * @param id
     * @param
     */
    public void setMessageReaded(String id) {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                LogUtil.print("=====消息已读");
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print("=====消息读取失败");
            }
        };
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("id", id));
        putRequest("sitemessage/readed", params, callback);
    }

    /**
     * 获取消息条数
     *
     * @param loginName
     * @return
     */
    public void getMessageCount(String loginName, ServiceResultCallback s) {// IOException, BaseException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("loginName", loginName));
        getRequest("message/msg/unread/count", params, s);
    }

    /**
     * 删除消息
     *
     * @param id
     * @param
     */
    public void deleteMessage(final String id, ServiceResultCallback callback) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("id", id));
        postRequest("sitemessage/delete", params, callback);
    }

    /**
     * @param
     * @param msgType 消息的类型
     * @param s
     */
    public void getMessageCount2(String msgType, ServiceResultCallback s) {// IOException, BaseException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("loginName", loginName));
        if (TextUtils.equals(msgType, "Business"))
            params.add(new BasicNameValuePair("msgType", msgType));
        else if (TextUtils.equals(msgType, "Trade"))
            params.add(new BasicNameValuePair("msgType", msgType));
        else if (TextUtils.equals(msgType, "Publish"))
            params.add(new BasicNameValuePair("msgType", msgType));
        getRequest("sitemessage/unread/amount", params, s);
    }


    /**
     * D+0 业务开通
     *
     * @param idcardNo
     * @return
     * @{// BaseException
     */
    public void toOpenD0(String idcardNo, ServiceResultCallback s) {// BaseException, IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("bankCode",bankCode));
        params.add(new BasicNameValuePair("idcardNo", idcardNo));
        params.add(new BasicNameValuePair("realname", ApplicationEx.getInstance().getUser()
                .getMerchantInfo().getUser()
                .getRealName()));
        postRequest("business/d0/openAccount", params, s);
//        postRequest("business/t0/apply", params, s);


    }

    /**
     * 立即提款业务申请，信用卡号验证
     *
     * @param idcardNo 身份证
     * @param bankCard 信用卡号
     *                 真实姓名
     * @return
     * @{// BaseException
     */
    public void toCheckCreditCard(String idcardNo, String bankCard, ServiceResultCallback s) {// BaseException, IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("bankCode",bankCode));
        params.add(new BasicNameValuePair("idcardNo", idcardNo));
        params.add(new BasicNameValuePair("realname", ApplicationEx.getInstance().getUser()
                .getMerchantInfo().getUser()
                .getRealName()));
        params.add(new BasicNameValuePair("bankCard", bankCard));
        postRequest("business/d0/verifyAccount", params, s);

    }

    /**
     * D+0 业务开通(秒到)
     *
     * @param idcardNo
     * @return
     * @{// BaseException
     */
    public void toOpenD02(String idcardNo, ServiceResultCallback s, boolean isOneDay) {// BaseException, IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("bankCode",bankCode));
        params.add(new BasicNameValuePair("idcardNo", idcardNo));
        params.add(new BasicNameValuePair("realname", ApplicationEx.getInstance().getUser()
                .getMerchantInfo().getUser()
                .getRealName()));
        if (isOneDay) {
            params.add(new BasicNameValuePair("applyType", "1"));
        }
        postRequest("business/d0/openAccount", params, s);

    }

    public void toOpenD02(String idcardNo, ServiceResultCallback s) {// BaseException, IOException {
        toOpenD02(idcardNo, s, false);
    }

    /**
     * 获取可提款余额
     *
     * @return
     * @{// BaseException
     * @{// IOException
     */
    public void getAccountBalance(ServiceResultCallback s) {// BaseException, IOException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        getRequest("business/t0/acount", params, s);

    }

    /**
     * 设置一日贷不再提醒
     *
     * @return
     * @{// BaseException
     * @{// IOException
     */
    public void setNoRemind(ServiceResultCallback s) {// BaseException, IOException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        getRequest("business/d0/updateD0notice", params, s);

    }

    /**
     * 获取今日收款金额
     *
     * @return
     * @{// BaseException
     * @{// IOException
     */
    public void queryTodayCollection(TodayCollectionCallback s) {// BaseException, IOException, JSONException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        getRequest("business/query/collection/today", params, s);
    }

    public static abstract class TodayCollectionCallback implements ServiceResultCallback {
        @Override
        public void onSuccess(ResultServices resultServices) {
            Double retAmount = null;
            if (resultServices.isRetCodeSuccess()) {
                try {
                    JSONObject jsonObject = new JSONObject(resultServices.retData);
                    if (!jsonObject.isNull("amount")) {
                        double amount = jsonObject.getDouble("amount");
                        retAmount = amount;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            onSuccess(retAmount);
        }

        public abstract void onSuccess(Double amount);
    }

    /**
     * 进行试算以获取手续费
     *
     * @param amount 提款金额
     * @return
     * @{// BaseException
     * @{// IOException
     */
    public void getT0Fee(String amount, ServiceResultCallback s) {// BaseException, IOException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("amount", amount));
        getRequest("business/t0/cash", params, s);

    }

    /**
     * 查询信用卡还款提示语
     *
     * @param creditcard 信用卡卡号
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void getCreditTips(String creditcard, ServiceResultCallback callback) {


        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL);
        url.append("v1.0/common/bankcard/");
        url.append(Util.formatString(creditcard));
        url.append("/creditTips");

        this.getRequest(url.toString(), nameValuePairs, callback);
    }

    /**
     * 进行提款交易
     *
     * @param amount 提款金额
     * @return
     * @{// BaseException
     * @{// IOException
     */
    public void getT0Cash(String amount, ServiceResultCallback s) {// BaseException, IOException {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("amount", amount));
        postRequest("business/t0/cash", params, s);

    }


    public void getCashHis(Calendar beginDate, Calendar endDate,
                           int page, ServiceResultCallback s) {// BaseException,

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String begin = format.format(beginDate.getTime());
        String end = format.format(endDate.getTime());
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("beginDate", begin));
        params.add(new BasicNameValuePair("endDate", end));
        params.add(new BasicNameValuePair("startPage", String.valueOf(page)));
        params.add(new BasicNameValuePair("pageSize", String.valueOf(20)));
        getRequest("business/cash/his", params, s);

    }

    /**
     * 获取收单开户地区信息列表
     *
     * @param code 地区代码
     * @return
     * @{// IOException
     * @{// BaseException
     */
    public void getAreaList(String code, ServiceResultCallback s) {// IOException, BaseException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        if (!TextUtils.isEmpty(code)) {
            params.add(new BasicNameValuePair("code", code));
        }
        getRequest("business/conf/area",
                params, s);

    }

    public void getBankList(String code, String areaCode, ServiceResultCallback s) {// IOException, BaseException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        if (!TextUtils.isEmpty(code)) {
            params.add(new BasicNameValuePair("code", code));
        }
        if (!TextUtils.isEmpty(areaCode)) {
            params.add(new BasicNameValuePair("areaCode", areaCode));
        }
        getRequest(
                "business/conf/bank",
                params, s);

    }

    /**
     * 查询卡BIN
     *
     * @param busid  账户类型  18X_1:个人账户
     *               18X_2_1：对公账户
     * @param cardNo 卡号
     * @return
     * @{// BaseException
     * @{// IOException
     */
    public void getCardBIN(BankBusid busid, String cardNo, ServiceResultCallback s) {// IOException, BaseException {

        // 设置参数
        StringBuffer url = new StringBuffer();
        url.append("common/bankcard/");
        url.append(cardNo);
        url.append("/bin");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busId", busid.getValue()));
        nameValuePairs.add(new BasicNameValuePair("cardNo", cardNo));

        getRequest(url.toString(), nameValuePairs, s);
    }


    /**
     * 账单号付款
     *
     * @param mobile 用户手机号
     * @param srcsid 上一次查询的sid号
     * @param billno 账单号
     * @param amount 金额
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void ctZhangDanHaoFuKuan(
            String mobile,
            String srcsid,
            String billno,
            String amount,
            SwiperInfo swiperInfo,
            ServiceResultCallback s
    ) {


        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
        nameValuePairs.add(new BasicNameValuePair("srcsid", srcsid));
        nameValuePairs.add(new BasicNameValuePair("billno", billno));
        nameValuePairs.add(new BasicNameValuePair("amount", amount));
        nameValuePairs.add(new BasicNameValuePair("otrack", swiperInfo.getEncTracks()));
        nameValuePairs.add(new BasicNameValuePair("pinkey", swiperInfo.getPin()));
        nameValuePairs.add(new BasicNameValuePair("rnd", swiperInfo.getRandomNumber()));

        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getSession().getCurrentKSN()));

        //IC交易
        if (!"".equals(swiperInfo.getIcc55())) {
            nameValuePairs.add(new BasicNameValuePair("pan", swiperInfo.getMaskedPan()));
            nameValuePairs.add(new BasicNameValuePair("posemc", swiperInfo.getPosemc()));//
            nameValuePairs.add(new BasicNameValuePair("icc55", swiperInfo.getIcc55()));//
            nameValuePairs.add(new BasicNameValuePair("cardsn", swiperInfo.getCardsn()));
            nameValuePairs.add(new BasicNameValuePair("track2", swiperInfo.getTrack2()));
        }


        //收款宝 渠道
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));

        // 业务代码ID
        nameValuePairs.add(new BasicNameValuePair("busid", "M20013"));

        postRequest(
                Parameters.serviceURL.concat("commitTransaction.json"),
                nameValuePairs, s);


    }

    /**
     * 特权购买付款
     */
    public void ctPrivilegePurchase(PrivilegePurchaseTransInfo mPrivilegePurchaseTransInfo, SwiperInfo swiperInfo, ServiceResultCallback serviceResultCallback) {


        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //收款宝 渠道
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        if (!TextUtils.isEmpty(mPrivilegePurchaseTransInfo.getBuynum())) {
            nameValuePairs.add(new BasicNameValuePair("buynum", mPrivilegePurchaseTransInfo.getBuynum()));
        }

        // 业务代码ID
        nameValuePairs.add(new BasicNameValuePair("busid", mPrivilegePurchaseTransInfo.getBusid()));
        nameValuePairs.add(new BasicNameValuePair("amount", mPrivilegePurchaseTransInfo.getAmount()));
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));
        nameValuePairs.add(new BasicNameValuePair("tdtm", mPrivilegePurchaseTransInfo.getTdtm()));
        nameValuePairs.add(new BasicNameValuePair("mobile", mPrivilegePurchaseTransInfo.getMobile()));

        nameValuePairs.add(new BasicNameValuePair("otrack", swiperInfo.getEncTracks()));
        nameValuePairs.add(new BasicNameValuePair("pinkey", swiperInfo.getPin()));
        nameValuePairs.add(new BasicNameValuePair("rnd", swiperInfo.getRandomNumber()));
        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getSession().getCurrentKSN()));

        nameValuePairs.add(new BasicNameValuePair("msgdigest", mPrivilegePurchaseTransInfo.getMsgdigest()));
        nameValuePairs.add(new BasicNameValuePair("ordtime", mPrivilegePurchaseTransInfo.getOrdtime()));
        nameValuePairs.add(new BasicNameValuePair("ordexpdat", mPrivilegePurchaseTransInfo.getOrdexpdat()));

        //IC交易
        nameValuePairs.add(new BasicNameValuePair("posemc", swiperInfo.getPosemc()));//
        if (!"".equals(swiperInfo.getIcc55())) {
            nameValuePairs.add(new BasicNameValuePair("pan", swiperInfo.getMaskedPan()));
            nameValuePairs.add(new BasicNameValuePair("icc55", swiperInfo.getIcc55()));//
            nameValuePairs.add(new BasicNameValuePair("cardsn", swiperInfo.getCardsn()));
            nameValuePairs.add(new BasicNameValuePair("track2", swiperInfo.getTrack2()));
        }

        nameValuePairs.add(new BasicNameValuePair("type", "PR"));//支付类型
        nameValuePairs.add(new BasicNameValuePair("curcode", "156"));//
        nameValuePairs.add(new BasicNameValuePair("mercid", mPrivilegePurchaseTransInfo.getMercid()));//商户id
        nameValuePairs.add(new BasicNameValuePair("ordno", mPrivilegePurchaseTransInfo.getOrderNo()));//
//        nameValuePairs.add(new BasicNameValuePair("track1", ""));//
//        nameValuePairs.add(new BasicNameValuePair("acctname", ""));//
//        nameValuePairs.add(new BasicNameValuePair("idnumber", ""));//
//        nameValuePairs.add(new BasicNameValuePair("voutype", ""));//
//        nameValuePairs.add(new BasicNameValuePair("famount", ""));//
//        nameValuePairs.add(new BasicNameValuePair("giftno", ""));//
//        nameValuePairs.add(new BasicNameValuePair("orderRnd", orderRnd));//

        postRequest(
                Parameters.serviceURL.concat("commitOrderTransaction.json"),
                nameValuePairs, serviceResultCallback);


    }


    /**
     * 账单号查询接口
     *
     * @param mobile   用户手机号
     * @param billno   账单号
     * @param amount   付款金额
     * @param mobileno 通知手机号
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void qtZhangDanHaoFuKuanChaXun(
            String mobile,
            String billno,
            String amount,
            String mobileno,
            ServiceResultCallback serviceResultCallback
    ) {


        // 设置参数
        List<NameValuePair> nameValuePairs = createNameValuePair(true);
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
        nameValuePairs.add(new BasicNameValuePair("billno", billno));
        nameValuePairs.add(new BasicNameValuePair("amount", amount));
        nameValuePairs.add(new BasicNameValuePair("mobileno", mobileno));
        // 业务代码ID
        nameValuePairs.add(new BasicNameValuePair("busid", "M50005"));

        postRequest(
                Parameters.serviceURL.concat("queryTrans.json"),
                nameValuePairs, serviceResultCallback);

    }


    /**
     * 手机收银台   创建支付账单号（快捷）
     *
     * @param merId   商户编号
     * @param orderId 拉卡拉为商户分配的固定账单号和用户订单号
     * @param amount  账单金额
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void payplatformGetShortcut(String ver, String merId, String orderId,
                                       String amount, String minCode, String macType, String mac, String expriredtime,
                                       String desc, String randnum, String productName, ServiceResultCallback s) {


        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        String redirectURL = "";

        nameValuePairs.add(new BasicNameValuePair("ver", ver));//版本号
        nameValuePairs.add(new BasicNameValuePair("merId", merId)); // 商户编号
        nameValuePairs.add(new BasicNameValuePair("orderId", orderId)); // 订单号
        nameValuePairs.add(new BasicNameValuePair("amount", amount)); // 支付金额
        nameValuePairs.add(new BasicNameValuePair("minCode", minCode));// 商户编号（拉卡拉为商户分配）
        nameValuePairs.add(new BasicNameValuePair("expriredtime", expriredtime)); // 失效时间
        nameValuePairs.add(new BasicNameValuePair("randnum", randnum)); // 随机数
        nameValuePairs.add(new BasicNameValuePair("macType", macType)); // 效验类型
        nameValuePairs.add(new BasicNameValuePair("mac", mac)); // 效验数据
        try {
            nameValuePairs.add(new BasicNameValuePair("desc", new String(desc.getBytes("UTF-8"), "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        nameValuePairs.add(new BasicNameValuePair("productName", productName));
        nameValuePairs.add(new BasicNameValuePair("redirectURL", redirectURL));

        getRequest(Parameters.serviceURL.concat("createBill.json"),
                nameValuePairs, s);

    }


    /**
     * 收银台使用账单号付款接口
     */
    /**
     * ****************************************************************
     */
    public void queryBillNoPayPlatform(
            Context context,
            String mobile,
            String billno,
            String amount,
            String mobileno,
            ServiceResultCallback s
    ) {

        List<NameValuePair> nameValuePairs = createNameValuePair(true);
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
        nameValuePairs.add(new BasicNameValuePair("billno", billno));
        nameValuePairs.add(new BasicNameValuePair("amount", amount));
        nameValuePairs.add(new BasicNameValuePair("mobileno", mobileno));
        nameValuePairs.add(new BasicNameValuePair("busid", "M50005")); // 业务代码ID

        postRequest(
                Parameters.serviceURL.concat("queryTrans.json"),
                nameValuePairs, s);


    }

    /**
     * 账单号付款
     *
     * @param mobile 用户手机号
     * @param srcsid 上一次查询的sid号
     * @param billno 账单号
     * @param amount 金额
     * @param otrack 磁道，借记卡磁道信息（包括2，3磁道）
     * @param pinkey 个人密码，用交互工作密钥加密上送
     * @param rnd    随机数，由刷卡器刷卡时返回
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void payforPayPlatform(
            Context context,
            String mobile,
            String srcsid,
            String billno,
            String amount,
            String otrack,
            String pinkey,
            String rnd,
            ServiceResultCallback s
    ) {


        // 设置参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));
        nameValuePairs.add(new BasicNameValuePair("srcsid", srcsid));
        nameValuePairs.add(new BasicNameValuePair("billno", billno));
        nameValuePairs.add(new BasicNameValuePair("amount", amount));
        nameValuePairs.add(new BasicNameValuePair("otrack", otrack));
        nameValuePairs.add(new BasicNameValuePair("pinkey", pinkey));
        nameValuePairs.add(new BasicNameValuePair("rnd", rnd));
        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getSession().getCurrentKSN()));
        // 业务代码ID
        nameValuePairs.add(new BasicNameValuePair("busid", "M20013"));

        postRequest(
                Parameters.serviceURL.concat("commitTransaction.json"),
                nameValuePairs, s);

    }

    /**
     * 获取充值金额
     * 3.2版本新接口:Url用serviceNewURL
     *
     * @param mobile 充值手机号
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getMobileBureau(String mobile, ServiceResultCallback serviceResultCallback) {
        // 设置参数
        StringBuffer url = new StringBuffer();
        url.append("common/dict/");
        url.append(mobile);
        url.append("/getMobileBureau");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile", mobile));

        this.getRequest(url.toString(), nameValuePairs, serviceResultCallback);

    }

    /**
     * @param branch             支行信息
     * @param idCardFileName     本人手持身份证照片名称
     * @param settleFileName     本人手持结算卡照片名称
     * @param busLicenceFileName 营业执照号码
     * @param callback
     */
    public void merchantUpgrade(AreaEntity branch, String idCardFileName, String settleFileName, String busLicenceFileName, ServiceResultCallback callback) {
        String url = "merchant/application/upgrade";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (branch != null) {
            params.add(new BasicNameValuePair("openBankBranchCode", branch.getCode()));
            params.add(new BasicNameValuePair("openBankBranchName", branch.getName()));
        }
        params.add(new BasicNameValuePair("idCardFileName", idCardFileName));
        params.add(new BasicNameValuePair("settleFileName", settleFileName));
        params.add(new BasicNameValuePair("busLicenceFileName", busLicenceFileName));
        this.postRequest(url, params, callback);
    }

    /**
     * 商户升级提示信息
     *
     * @param callback
     */
    public void merchantUpgradeTipsMsg(ServiceResultCallback callback) {
        String url = "merchant/upgradeTipsMsg";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        this.getRequest(url, params, callback);
    }

    public void getSignDetail(String sid, ServiceResultCallback callback) {
        StringBuilder url = new StringBuilder();
        url.append("trade/getSignDetail/").append(sid);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(sid)) {
            params.add(new BasicNameValuePair("sid", sid));
        }
        this.getRequest(url.toString(), params, callback);
    }
}
