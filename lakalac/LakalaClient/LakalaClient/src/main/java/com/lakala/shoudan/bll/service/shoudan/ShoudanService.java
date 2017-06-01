package com.lakala.shoudan.bll.service.shoudan;

import android.text.TextUtils;

import com.lakala.library.encryption.Mac;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.BankInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RepaymentPersonInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.RepaymentWorkInfo;
import com.lakala.shoudan.bll.service.Service_ShouDan;
import com.lakala.shoudan.common.AreaInfoCallback;
import com.lakala.shoudan.common.LoanInfoCallback;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.RegisterCallback;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.BaseException;
import com.lakala.shoudan.datadefine.RecordDetail;
import com.lakala.shoudan.datadefine.ShoudanRegisterInfo;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 联网服务 Created by More on 14-1-14.
 */
public class ShoudanService extends Service_ShouDan {

    private static ShoudanService instance = null;

    /**
     * 私有构造方法,通过静态方法获取实例对象
     */
    private ShoudanService() {

    }

    /**
     * 获取收款宝实例,添加了同步锁
     */
    public static synchronized ShoudanService getInstance() {

        if (instance == null) {
            instance = new ShoudanService();
            instance.generateVercode();
        }
        return instance;
    }


    private ShoudanRegisterInfo unpackMechantInfo(JSONObject retData)
            throws JSONException {
        ShoudanRegisterInfo merchantInfo = new ShoudanRegisterInfo();

        String merchantStatus = retData.getString("merchantStatus");// 商户状态,0未开通,1已开通,2冻结

        return merchantInfo;
    }


    /**
     * 开通商户
     *
     * @param shoudanRegisterInfo
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws JSONException
     */
    public void register(ShoudanRegisterInfo shoudanRegisterInfo, RegisterCallback s) {

        merchantManage(shoudanRegisterInfo, s);
    }

    /**
     * 交易查询
     */

    private String DEFAULT_PAGE_SIZE = "20";
    public static final String DEAL_DATE_TIME = "dealStartDateTime";// 交易时间
    public static final String DEAL_TYPE_NAME = "dealTypeName";// 交易类型
    public static final String DEAL_TYPE_CODE = "dealTypeCode2"; // 交易类型码
    public static final String DEAL_AMOUNT = "dealAmount";// 交易金额
    public static final String HANDLING_CHARGE = "handlingCharge";// 手续费
    public static final String PAYMENT_ACCOUNT = "paymentAccount";// 付款卡号
    public static final String COLLECTION_ACCOUNT = "collectionAccount";// 收款卡号
    public static final String STATUS = "status";// 交易状态
    public static final String SYS_SEQ = "sysSeq";// 检索号
    public static final String SID = "sid";// 交易Sid
    public static final String PASM = "pasm";// 刷卡器序列号
    public static final String PAYMENT_MOBILE = "paymentMobile";// 支付手机号
    public static final String SERIES = "series";// 流水号
    public static final String WITH_DRAW = "withdraw";// 是否被撤销
    public static final String AUTHCODE = "authCode";// 授权码
    public static final String POSSTATUS = "posStatus"; // X0 收款成功,X1 收款失败,Y0
    // 撤销成功,Y1 撤销失败
    public static final String MERCHANTCODE = "merchantCode"; // 商户号
    public static final String POSOGNAME = "posOgName";// 收单机构
    public static final String BATCHNO = "batchNo";// 批次号
    public static final String VOUCHER_CODE = "voucherCode";// 凭证
    public static final String NEED_CANCLE_PWD = "needCancelPasswd";// 是否需要输入pin
    public static final String BUS_NAME = "busName";// 业务名
    private final String SUCCESS_COUNT = "successCount"; // 成功笔数
    private final String SUCCESS_AMOUNT = "successAmount"; // 成功金额
    private final String CANCEL_COUNT = "cancelCount"; // 撤销笔数
    private final String CANCEL_AMOUNT = "cancelAmount"; // 撤销金额
    private final String MERCHANT_NAME = "merchantName";// 商户名

    /**
     * 解析单条交易记录
     *
     * @param detailJsonObject
     * @return
     * @throws JSONException
     */
    private RecordDetail unpackRecordJson(JSONObject detailJsonObject)
            throws JSONException {
        RecordDetail record = new RecordDetail();
        String dealStartDateTime = Util.trim(detailJsonObject
                .getString(DEAL_DATE_TIME));// 交易时间
        record.setDealDateTime(dealStartDateTime);
        String dealTypeName = Util.trim(detailJsonObject
                .getString(DEAL_TYPE_NAME));// 交易类型
        record.setDealTypeName(dealTypeName);
        String dealTypeCode = Util.trim(detailJsonObject
                .getString(DEAL_TYPE_CODE));// 交易类型码
        record.setDealTyepCode(dealTypeCode);
        String dealAmount = Util.trim(detailJsonObject.getString(DEAL_AMOUNT));// 交易金额
        // dealAmount=Util.formatAmount(Double.parseDouble(dealAmount)/100+"");
        dealAmount = Double.parseDouble(dealAmount) / 100 + "";
        record.setDealAmount(dealAmount);
        String handlingCharge = Util.trim(detailJsonObject
                .getString(HANDLING_CHARGE));// 手续费
        if (handlingCharge != null && !handlingCharge.equals("")) {
            handlingCharge = Util.formatAmount(Double
                    .parseDouble(handlingCharge) / 100 + "");
        }
        record.setHandlingCharge(handlingCharge);
        String paymentAccount = Util.trim(detailJsonObject
                .getString(PAYMENT_ACCOUNT));// 付款卡号
        record.setPaymentAccount(paymentAccount);
        String collectionAccount = Util.trim(detailJsonObject
                .getString(COLLECTION_ACCOUNT));// 收款卡号
        record.setCollectionAccount(collectionAccount);
        String status = Util.trim(detailJsonObject.getString(STATUS));// 交易状态
        record.setStatus(status);
        String sysSeq = Util.trim(detailJsonObject.getString(SYS_SEQ));// 检索参考号
        record.setSysSeq(sysSeq);
        String sid = Util.trim(detailJsonObject.getString(SID));// 交易的 Sid
        record.setSid(sid);
        String pasm = Util.trim(detailJsonObject.getString(PASM));// 刷卡器序列号
        record.setPasm(pasm);
        String paymentMobile = Util.trim(detailJsonObject
                .getString(PAYMENT_MOBILE));// 支付手机号
        record.setPaymentMobile(paymentMobile);
        String series = Util.trim(detailJsonObject.getString(SERIES));
        record.setSeries(series);
        String isWithdraw = Util.trim(detailJsonObject.getString(WITH_DRAW));// 是否被撤销
        record.setIsWithDraw(isWithdraw);
        String authCode = Util.trim(detailJsonObject.getString(AUTHCODE));// 授权码
        record.setAuthCode(authCode);
        String posStatus = Util.trim(detailJsonObject.getString(POSSTATUS));// 收款状态
        // X0
        // 收款成功,X1
        // 收款失败,Y0
        // 撤销成功,Y1
        // 撤销失败
        record.setPosStatus(posStatus);
        String merChantCode = Util.trim(detailJsonObject
                .getString(MERCHANTCODE));// 商户号
        record.setMerChantCode(merChantCode);
        String merchantName = Util.trim(detailJsonObject
                .getString(MERCHANT_NAME));// 商户名
        record.setMechantName(merchantName);
        String posOGName = Util.trim(detailJsonObject.getString(POSOGNAME));// 收单机构
        record.setPosOGName(posOGName);
        String batchNo = Util.trim(detailJsonObject.getString(BATCHNO)); // 批次号
        record.setBatchNo(batchNo);
        String voucherCode = Util
                .trim(detailJsonObject.getString(VOUCHER_CODE));// 凭证号
        record.setVoucherCode(voucherCode);
        String needCancelpwd = Util.trim(detailJsonObject
                .getString(NEED_CANCLE_PWD));// 是否需要输入 pin
        record.setNeedCancelPwd(needCancelpwd);
        String busName = Util.trim(detailJsonObject.getString(BUS_NAME));// 业务名
        record.setBusname(busName);
        if (!detailJsonObject.isNull("notesdesc")) {
            record.setTips(detailJsonObject.getString("notesdesc"));
        }
        record.setSign(detailJsonObject.optBoolean("sign", false));
        return record;
    }

    /**
     * 解析 格式为 JSONArry 的全部交易记录
     *
     * @param recordArray
     * @return
     * @throws JSONException
     */
    private List<RecordDetail> unpackRecordsJsonArray(JSONArray recordArray)
            throws JSONException {
        List<RecordDetail> recordDetailList = new ArrayList<RecordDetail>();
        for (int i = 0; i < recordArray.length(); i++) {
            JSONObject detailJsonObject = recordArray.getJSONObject(i);
            RecordDetail record = unpackRecordJson(detailJsonObject);
            // 只显示成功的便民业务
            if (record.getPosStatus() != RecordDetail.EPosStatus.RECEIVE_SUCCESS
                    && (!"收款".equals(record.getDealTypeName()) && !"P08".equals(record.getDealTypeCode()) && !"P09".equals(record.getDealTypeCode())))
                continue;
            recordDetailList.add(record);
        }
        return recordDetailList;
    }

    /**
     * 解析获取可以撤销的交易记录 isWathDra == "true"
     *
     * @param recordArray
     * @return
     * @throws JSONException
     */
    private List<RecordDetail> unpackRJsonArryForCancelableRecords(
            JSONArray recordArray) throws JSONException {
        List<RecordDetail> recordDetailList = new ArrayList<RecordDetail>();
        for (int i = 0; i < recordArray.length(); i++) {
            JSONObject detailJsonObject = recordArray.getJSONObject(i);
            RecordDetail record = unpackRecordJson(detailJsonObject);
            if (record.getIsWithDraw() != null
                    && record.getIsWithDraw().equals("true"))
                recordDetailList.add(record);
        }
        return recordDetailList;
    }

    /**
     * 交易类型
     */
    public static class QueryType {

        public static final String ALL = "all";
        public static final String COLLECTION = "";
        public static final String REMITTANCE = "";
        public static final String CREDIT_CARD_PAYMENT = "";
        public static final String MOBILE_RECHARGE = "";

    }

    /**
     * 撤销查询统一调用此接口
     *
     * @param psamNo    ksn
     * @param startPage
     * @param startTime
     * @param endTime
     * @param cardNo
     * @return
     * @throws BaseException
     * @throws ParseException
     * @throws IOException
     * @throws JSONException
     */
    public void queryRevocationRecords(boolean ifWithdraw, int startPage, String startTime, String endTime, String tradeType,
                                       String cardNo, String busId, String sid, String imagepaybill, String psamNo, ServiceResultCallback callback) {

        int isWithdraw = ifWithdraw == true ? 1 : 0;//撤销为1
        getAcquiringTradeList(isWithdraw, startPage, DEFAULT_PAGE_SIZE, startTime, endTime, tradeType, cardNo, busId, sid, imagepaybill, psamNo, callback);
    }

    /**
     * 交易查询统一调用此接口
     *
     * @param isWithdraw
     * @param startPage
     * @param startTime
     * @param endTime
     * @param tradeType
     * @param callback
     */
    public void queryTradeRecords(boolean isWithdraw, int startPage, String startTime, String endTime, String tradeType, ServiceResultCallback callback) {

        queryRevocationRecords(isWithdraw, startPage, startTime, endTime, tradeType, "", "", "", "", "", callback);
    }

    /**
     * 收款宝4.4消息中心查询交易详情
     * @param isWithdraw
     * @param tradeTime
     * @param cardNo
     * @param busId
     * @param sid
     * @param imagepaybill
     * @param psamNo
     * @param callback
     */
    public void queryTradeRecord(int isWithdraw, String tradeTime, String cardNo, String busId, String sid, String imagepaybill, String psamNo, ServiceResultCallback callback) {
        getAcquiringTradeList(isWithdraw, tradeTime, cardNo, busId, sid, imagepaybill, psamNo, callback);
    }

    /**
     * 新交易查询接口
     *
     * @param isWithdraw   0、查询；1、撤销
     * @param tradeTime    交易类型
     * @param cardNo       转出卡号（收款交易撤销使用必填）
     * @param busId        （撤销使用必填：收款18X，扫码：W00001）
     * @param sid
     * @param imagepaybill 扫码订单号（扫码交易并且撤销使用必填）
     * @param psamNo       刷卡器ksn
     */
    protected void getAcquiringTradeList(int isWithdraw, String tradeTime, String cardNo, String busId, String sid, String imagepaybill, String psamNo, ServiceResultCallback callback) {
        StringBuffer url = new StringBuffer();
        url.append("sitemessage/getTradeDetails");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//        nameValuePairs.add(new BasicNameValuePair("isWithdraw", String.valueOf(isWithdraw)));
        nameValuePairs.add(new BasicNameValuePair("tradeTime", tradeTime));
        if (!TextUtils.isEmpty(cardNo))
            nameValuePairs.add(new BasicNameValuePair("cardNo", cardNo));
        nameValuePairs.add(new BasicNameValuePair("busId", busId));
        nameValuePairs.add(new BasicNameValuePair("sid", sid));
        if (!TextUtils.isEmpty(imagepaybill))
            nameValuePairs.add(new BasicNameValuePair("imagepaybill", imagepaybill));
        if (!TextUtils.isEmpty(psamNo))
            nameValuePairs.add(new BasicNameValuePair("psamNo", psamNo));
        postRequest(url.toString(), nameValuePairs, callback);
    }

    /**
     * 收款 上送
     *
     * @param amountStr
     * @param swipeInfo
     * @param phoneNum
     * @return
     * @throws IOException
     * @throws JSONException
     * @throws BaseException
     */
    public void transactionCommit(String amountStr,
                                  SwiperInfo swipeInfo, String phoneNum, String chantype,
                                  String cardsn, String icc55, String posemc, String track2
            , ServiceResultCallback s) {

        acquirerTransaction(
                ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo(), amountStr, "0",
                swipeInfo.getEncTracks(), swipeInfo.getPin(),
                swipeInfo.getRandomNumber(), "1", phoneNum, "", chantype,
                cardsn, icc55, posemc, track2, swipeInfo.getMaskedPan(),
                swipeInfo.getTrack1(), s);

    }

    /**
     * 撤销交易
     *
     * @param revSubmit
     * @param swipeInfo
     * @return
     * @throws BaseException
     * @throws JSONException
     * @throws IOException
     */
    public void cancelCollection(RecordDetail revSubmit,
                                 SwiperInfo swipeInfo, ServiceResultCallback serviceResultCallback) {

        String lpmercd = revSubmit.getMerChantCode();// revSubmit.getCollectionAccount();//商户号
        String sid = revSubmit.getSid();// sid
        String pan = revSubmit.getPaymentAccount();// 付款卡号
        String amount = revSubmit.getDealAmount();// 交易金额
        String srcseries = revSubmit.getSeries();
        String srcsysref = revSubmit.getSysSeq();
        String mobileNum = revSubmit.getPaymentMobile();// 付款人手机号

        String otrack = swipeInfo.getEncTracks();
        String pinkey = (swipeInfo.getPin() == null ? "" : swipeInfo.getPin());
        String rnd = swipeInfo.getRandomNumber();
        String srcauth = "";

        String posmemc = swipeInfo.getPosemc();
        String icc55 = swipeInfo.getIcc55();
        String cardsn = swipeInfo.getCardsn();
        String track2 = swipeInfo.getTrack2();
        String chntype = swipeInfo.getChntype();
        String track1 = swipeInfo.getTrack1();
        revocationTransaction(lpmercd,// 商户号
                sid,// sid
                pan, // 转出卡号
                amount, // 金额
                otrack, // 磁道,(包括二三磁道)
                pinkey, // 个人密码
                rnd, // 随机数
                srcseries, // 原发送流水
                srcsysref, // 原检索号
                srcauth,// 原授权码
                mobileNum, posmemc, icc55, cardsn, track2, chntype, track1, serviceResultCallback);// 付款人手机号

    }


    /**
     * 验证密码
     *
     * @param pwd
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws JSONException
     */
    public void verifyPwd(String pwd, ServiceResultCallback s) {

        verifyUserPWD(pwd, s);

    }


    /**
     * Asynchronous Upload Tc
     *
     * @param swiperInfo
     * @param lastSid
     * @return
     * @throws IOException
     * @throws BaseException
     */
    public void asynchronousUploadTCForPaymentForcall(
            String busId, String termId, SwiperInfo swiperInfo, String lastSid,
            String syTm, String sysRef, String acinstcode)
            throws JSONException, IOException, BaseException {
//TODO
//		uploadAsyncTCForPaymentForCall(
//				busId, termId, swiperInfo.getIcc55(),
//				swiperInfo.createScpic55(), swiperInfo.getTCValue(), lastSid,
//				syTm, sysRef, acinstcode);
//
    }

    public interface PhoneQueryListener {

        public void onSuccess(String phoneStr);

        public void onEvent(HttpConnectEvent connectEvent);
    }

    public void queryLakalaService(final PhoneQueryListener phoneQueryListener) {
        // G_CSH
        getCommonDict("G_CSH", new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                JSONObject jb = null;
                String tel = null;
                try {
                    jb = new JSONObject(resultServices.retData);

                    JSONObject gcsh = (JSONObject) jb.getJSONObject("G_CSH");
                    tel = gcsh.optString("tel", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                phoneQueryListener.onSuccess(tel);

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                phoneQueryListener.onEvent(connectEvent);
            }
        });

    }

    /**
     * G_CMB_AREA 查询地区
     *
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void queryAreaInfoCMB(String type, ServiceResultCallback s) throws ParseException,
            IOException, BaseException {

        getCommonDict(type, s);


    }

    /**
     * 查询地区
     *
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void queryAreaInfo(AreaInfoCallback s) {

        getCommonDict("G_MT_AREA", s);


    }


    /**
     * 获取商户开通状态
     *
     * @throws BaseException
     * @throws IOException
     */
    public void queryMerchantStatus(ServiceResultCallback s) {

        // 3I050000301

        // product_sub_code='3I060002601' 平安收租宝

        getMerchantStatus(s);

    }

    public void rentInpanQuery(ServiceResultCallback s) {


        StringBuffer url = new StringBuffer();
        url.append("accountInfo");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("busid", "1F2"));

        getRequest(url.toString(), nameValuePairs, s);

    }

    /**
     * 查询商户状态列表
     *
     * @throws BaseException
     * @throws ParseException
     * @throws IOException
     * @throws JSONException
     */
    public void queryMerChantStatusList(ServiceResultCallback s) throws BaseException,
            ParseException, IOException, JSONException {

        String url = "business/status";
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        getRequest(url.toString(), nameValuePairs, s);

    }

    /**
     * 上传商户升级相应照片
     *
     * @return
     * @throws Exception
     */
    public void upgradeUpload(String photoPath, ServiceResultCallback s) {
        String url = "business/pic/upload/";
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("data", (photoPath));
        postRequest(url.toString(), fileMap, null, s);

    }

    // pic_0 身份证正面文件名 String
    // pic_1 身份证反面文件名 String
    // pic_2 持证照文件名 String
    // pic_3 营业执照 String
    // pic_4 门头照 String
    // pic_5 营业场所产权或租赁证明 String

    /**
     * 商户升档(个人)对私
     *
     * @throws BaseException
     * @throws ParseException
     * @throws IOException
     * @throws JSONException
     */
    public void merChantUpgradeCompany(String merPath, String mtouPath, String zuli, ServiceResultCallback s) {


        String url = ("business/mer/upapply/");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("pic_0", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getPicPath1()));
        nameValuePairs.add(new BasicNameValuePair("pic_1", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getPicPath2()));
        nameValuePairs.add(new BasicNameValuePair("pic_3", merPath));
        nameValuePairs.add(new BasicNameValuePair("pic_4", mtouPath));
        nameValuePairs.add(new BasicNameValuePair("pic_5", zuli));

        postRequest(url.toString(), nameValuePairs, s);

    }

    /**
     * 商户升档(企业)对公
     *
     * @throws BaseException
     * @throws ParseException
     * @throws IOException
     * @throws JSONException
     */
    public void merChantUpgradeIndividual(String picPersonal, ServiceResultCallback s) {

        String url = ("business/mer/upapply/");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("pic_0", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getPicPath1()));
        nameValuePairs.add(new BasicNameValuePair("pic_1", ApplicationEx.getInstance().getUser().getMerchantInfo().getUser().getIdCardInfo().getPicPath2()));

        nameValuePairs
                .add(new BasicNameValuePair("pic_2", picPersonal));
        postRequest(url.toString(), nameValuePairs, s);

    }


    /**
     * 商户升档 A2  2.5.3去掉该需求
     *
     * @throws BaseException
     * @throws ParseException
     * @throws IOException
     * @throws JSONException
     */
//	public void merChantUpgrade(List<String> filelists)
//			throws BaseException, ParseException, IOException, JSONException {
//		UserTokenHavingIsValid();
//
//		
//		StringBuffer url = new StringBuffer();
//		url.append(Parameters.serviceURL).append("business/mer/newupapply/")
//				.append(ApplicationEx.getInstance().getUser().getLoginName()).append(".json");
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		nameValuePairs.add(new BasicNameValuePair("userToken",
//				""));
//		nameValuePairs.add(new BasicNameValuePair("verifyType", "1"));
//		nameValuePairs.add(new BasicNameValuePair("lv", "A2"));
//		nameValuePairs.add(new BasicNameValuePair("pic_0", filelists.get(0)));
//		nameValuePairs.add(new BasicNameValuePair("pic_1", filelists.get(1)));
//		nameValuePairs.add(new BasicNameValuePair("pic_2", filelists.get(2)));
//		postRequest(url.toString(), nameValuePairs, s);
//
//		
//	}

    // pic_0 身份证正面文件名 String
    // pic_1 身份证反面文件名 String
    // pic_2 持证照文件名 String
    // pic_3 营业执照 String
    // pic_4 门头照 String
    // pic_5 营业场所产权或租赁证明 String
    // pic_6 店内照 String

    /**
     * 商户升档 A3
     *
     * @param filelists
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void UpgradeMerchant(List<String> filelists, String branchBankno, String branchBankname, ServiceResultCallback s) {

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("business/mer/newupapply/")
                .append(ApplicationEx.getInstance().getUser().getLoginName()).append(".json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("lv", "A3"));
        nameValuePairs.add(new BasicNameValuePair("pic_0", filelists.get(0)));
        nameValuePairs.add(new BasicNameValuePair("pic_1", filelists.get(1)));
        nameValuePairs.add(new BasicNameValuePair("pic_2", filelists.get(2)));
        nameValuePairs.add(new BasicNameValuePair("pic_3", filelists.get(3)));
        nameValuePairs.add(new BasicNameValuePair("pic_4", filelists.get(4)));
        nameValuePairs.add(new BasicNameValuePair("pic_5", filelists.get(5)));
        nameValuePairs.add(new BasicNameValuePair("pic_6", filelists.get(6)));
        nameValuePairs.add(new BasicNameValuePair("openningBank", branchBankno));
        nameValuePairs.add(new BasicNameValuePair("openningBankName", branchBankname));
        postRequest(url.toString(), nameValuePairs, s);

    }

    /**
     * 替你还
     */

    private List<NameValuePair> getLoanCommonParams() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("loginName", ApplicationEx.getInstance().getUser().getLoginName()));
        return nameValuePairs;
    }

    /**
     * 查询随机数 9
     *
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void getRandom(ServiceResultCallback s) {

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("business/loan/random.json");

        getRequest(url.toString(), getLoanCommonParams(), s);

    }

    /**
     * 短信验证码
     *
     * @param orderno
     * @return
     * @throws ParseException
     * @throws BaseException
     * @throws IOException
     */
    public void getSmsCode(String orderno, ServiceResultCallback s) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("orderno", orderno));
        nameValuePairs.addAll(getLoanCommonParams());
        getRequest("business/loan/applyverfcode", nameValuePairs, s);
    }

    /**
     * @param mobile
     * @param creditpan 额度校验 7
     * @return
     * @throws ParseException
     * @throws BaseException
     * @throws IOException
     */
    public void getLimit(String mobile, String creditpan, ServiceResultCallback s) {
        StringBuffer url = new StringBuffer(Parameters.serviceURL).append("business/loan/limit.json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile",
                mobile));
        nameValuePairs.add(new BasicNameValuePair("creditpan",
                creditpan));
        nameValuePairs.addAll(getLoanCommonParams());
        getRequest(url.toString(), nameValuePairs, s);
    }

    /**
     * 查询费率 8
     *
     * @return
     * @throws ParseException
     * @throws BaseException
     * @throws IOException
     * @throws JSONException
     */
    public void getRate(ServiceResultCallback s) {
        getRequest("business/loan/rate", getLoanCommonParams(), s);
//		System.out.println(result.retCode+":"+result.errMsg+":"+result.retData);
    }

    /**
     * 试算 1
     *
     * @param orderno  订单号,随机数
     * @param applyamt 申请金额
     * @param period   周期
     * @param rate     费率
     * @param curcode  币种
     * @param termid   终端号
     * @return
     */
    public void trial(String orderno, String applyamt, String period, String rate, String curcode, String termid, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("orderno", orderno));
        nameValuePairs.add(new BasicNameValuePair("applyamt", applyamt));
        nameValuePairs.add(new BasicNameValuePair("period", period));
        nameValuePairs.add(new BasicNameValuePair("rate", rate));
        nameValuePairs.add(new BasicNameValuePair("curcode", curcode));
        nameValuePairs.add(new BasicNameValuePair("termid", termid));
        nameValuePairs.addAll(getLoanCommonParams());
        postRequest("business/loan/order/1", nameValuePairs, s);

    }

    /**
     * 单位信息入库 3
     *
     * @param orderno
     * @param workInfo
     * @return
     * @throws ParseException
     * @throws BaseException
     * @throws IOException
     */
    public void workInfoStorage(String orderno, RepaymentWorkInfo workInfo, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("orderno", orderno));
        nameValuePairs.add(new BasicNameValuePair("companyname", workInfo.getCompanyname()));
        nameValuePairs.add(new BasicNameValuePair("position", workInfo.getPosition()));
        nameValuePairs.add(new BasicNameValuePair("cpcrcodes", workInfo.getApcrcodes()));
        nameValuePairs.add(new BasicNameValuePair("cpcrnames", workInfo.getApcrnames()));
        nameValuePairs.add(new BasicNameValuePair("companyaddress", workInfo.getCompanyaddress()));
        nameValuePairs.add(new BasicNameValuePair("companytel", workInfo.getCompanytel()));
        nameValuePairs.add(new BasicNameValuePair("income", workInfo.getIncome()));
        nameValuePairs.add(new BasicNameValuePair("contactname", workInfo.getContactname()));
        nameValuePairs.add(new BasicNameValuePair("contactmobile", workInfo.getContactmobile()));
        nameValuePairs.addAll(getLoanCommonParams());
        postRequest("business/loan/order/3", nameValuePairs, s);


    }

    /**
     * 个人信息入库 2
     *
     * @param orderno
     * @param personInfo
     * @return
     * @throws ParseException
     * @throws BaseException
     * @throws IOException
     */
    public void personInfoStorage(String orderno, RepaymentPersonInfo personInfo, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("orderno", orderno));
        nameValuePairs.add(new BasicNameValuePair("applicant", personInfo.getApplicantName()));
        nameValuePairs.add(new BasicNameValuePair("certno", personInfo.getCertno()));
        nameValuePairs.add(new BasicNameValuePair("apcrcodes", personInfo.getApcrcodes()));
        nameValuePairs.add(new BasicNameValuePair("apcrnames", personInfo.getApcrnames()));
        nameValuePairs.add(new BasicNameValuePair("address", personInfo.getAddress()));
        nameValuePairs.add(new BasicNameValuePair("highestlevel", personInfo.getHighestlevel()));
        nameValuePairs.add(new BasicNameValuePair("email", personInfo.getEmail()));
        nameValuePairs.add(new BasicNameValuePair("certfirstimg", personInfo.getCertfirstimg()));
        nameValuePairs.add(new BasicNameValuePair("certsecondimg", personInfo.getCertsecondimg()));
        nameValuePairs.add(new BasicNameValuePair("nowphoto", personInfo.getNowphoto()));
        nameValuePairs.addAll(getLoanCommonParams());
        postRequest("business/loan/order/2", nameValuePairs, s);


    }

    /**
     * 银行卡信息入库 4
     *
     * @param orderno
     * @return
     * @throws ParseException
     * @throws BaseException
     * @throws IOException
     */
    public void bankStorage(String orderno, BankInfo bankInfo, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("orderno", orderno));
        nameValuePairs.add(new BasicNameValuePair("creditcard", bankInfo.getCreditCard()));
        nameValuePairs.add(new BasicNameValuePair("creditbank", bankInfo.getCreditbank()));
        nameValuePairs.add(new BasicNameValuePair("debitcard", bankInfo.getDebitcard()));
        nameValuePairs.add(new BasicNameValuePair("debitbank", bankInfo.getDebitbank()));
        nameValuePairs.addAll(getLoanCommonParams());
        postRequest("business/loan/order/4", nameValuePairs, s);
        //返回用户可贷额度和返回订单号

    }

    /**
     * 替你还文件上传
     *
     * @param bitmap
     * @param bitmap2
     * @param bitmap3
     * @return
     * @throws IOException
     * @throws BaseException
     * @throws ParseException
     */
    public void fileUpload(String orderno, String bitmap,
                           String bitmap2, String bitmap3, ServiceResultCallback s) {

        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("certfirstimg", (bitmap));
        fileMap.put("certsecondimg", bitmap2);
        fileMap.put("nowphoto", bitmap3);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("orderno", orderno));
        nameValuePairs.addAll(getLoanCommonParams());
        postRequest("business/loan/file", fileMap, nameValuePairs, s);
    }


    /**
     * 借款申请 5
     *
     * @param orderno  //订单号
     * @param applyamt 申请金额
     * @param period   //周期
     * @param smscode  //短信验证
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void loanApply(String orderno, String applyamt, String period, String smscode, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("orderno", orderno));
        nameValuePairs.add(new BasicNameValuePair("applyamt", applyamt));
        nameValuePairs.add(new BasicNameValuePair("period", period));
        nameValuePairs.add(new BasicNameValuePair("smscode", smscode));
        nameValuePairs.addAll(getLoanCommonParams());
        postRequest("business/loan/apply", nameValuePairs, s);


    }

    /**
     * 借款信息回显 6
     *
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     * @throws JSONException
     */
    public void loanInfoBackShow(LoanInfoCallback callback) {//,IOException,ParseException{

        getRequest("business/loan/apply", getLoanCommonParams(), callback);

    }

//	/**
//	 * 用户申请失败信息查询 11
//	 * @param orderno
//	 * @return
//	 * @throws BaseException
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//	public void getApplyFailedInfo(String orderno, ServiceResultCallback s){//,IOException,ParseException{
//
//		StringBuffer url = new StringBuffer(Parameters.serviceURL).append("business/loan/apply/").append(orderno).append(".json");
//		getRequest(url.toString(), getLoanCommonParams(),s);
//
//	}

    /**
     * 1.18.12.	查询用户申请失败记录
     * 注： 请求路径和信息回显一样？
     * url改为/business/loan/applys.json原来是/business/loan/apply.json
     *
     * @param orderno
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getApplyFailedRecord(String orderno, ServiceResultCallback s) {//,IOException,ParseException{
        StringBuffer url = new StringBuffer(Parameters.serviceURL).append("business/loan/applys.json");
        getRequest(url.toString(), getLoanCommonParams(), s);
    }

    /**
     * 1.18.13.	查询用户最近的贷款申请
     *
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getApplyLatest(ServiceResultCallback s) {//,IOException,ParseException{

        getRequest("business/loan/latest", getLoanCommonParams(), s);

    }

    /**
     * 1.18.14.	查询用户历史交易
     *
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getHistoryTrans(int currentpage, int pagesize, String loantype, ServiceResultCallback s) {//,IOException,ParseException{

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("currentpage", String.valueOf(currentpage)));
        nameValuePairs.add(new BasicNameValuePair("pagesize", String.valueOf(pagesize)));
        nameValuePairs.add(new BasicNameValuePair("loantype", loantype));
        nameValuePairs.addAll(getLoanCommonParams());
        getRequest("business/loan/trade", nameValuePairs, s);

    }

    /**
     * 1.18.15.	查询签约帐户
     *
     * @param contractno 贷款合同编号
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getSignedAccount(String contractno, ServiceResultCallback s) {//,IOException,ParseException{

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("contractno", contractno));
        nameValuePairs.addAll(getLoanCommonParams());
        getRequest("business/loan/pan", nameValuePairs, s);

    }

    /**
     * 1.18.16.	更换签约帐户
     *
     * @param contractno 贷款合同编号
     * @param debitcard  新借记卡卡号
     * @param debitbank  新借记卡开户行
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void updateSignedAccouont(String contractno, String debitcard, String debitbank, ServiceResultCallback s) {//,IOException,ParseException{

        StringBuffer url = new StringBuffer(Parameters.serviceURL).append("business/loan/pan.json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("contractno", contractno));
        nameValuePairs.add(new BasicNameValuePair("debitcard", debitcard));
        nameValuePairs.add(new BasicNameValuePair("debitbank", debitbank));
        nameValuePairs.addAll(getLoanCommonParams());
        postRequest(url.toString(), nameValuePairs, s);

    }

    /**
     * 查询还款明细 17
     *
     * @param contractno 贷款合同编号
     * @return
     */
    public void getLoanDetails(String contractno, ServiceResultCallback s) {//,IOException,ParseException{
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("contractno", contractno));
        nameValuePairs.addAll(getLoanCommonParams());
        getRequest("business/loan/pay", nameValuePairs, s);

    }

    /**
     * 获取系统参数 18
     *
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getSysConfig(ServiceResultCallback s) {//,IOException,ParseException{

        StringBuffer url = new StringBuffer(Parameters.serviceURL).append("business/loan/conf.json");
        getRequest(url.toString(), getLoanCommonParams(), s);

    }

    /**
     * 1.18.19.	用户直接修改额度
     *
     * @param orderno    订单号
     * @param creditcard 信用卡号
     * @param applyamt   申请金额
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void updateUserLimit(String orderno, String creditcard, String applyamt, ServiceResultCallback s) {

        StringBuffer url = new StringBuffer(Parameters.serviceURL).append("business/loan/quota.json");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("orderno", orderno));
        nameValuePairs.add(new BasicNameValuePair("creditcard", creditcard));
        nameValuePairs.add(new BasicNameValuePair("applyamt", applyamt));
        nameValuePairs.addAll(getLoanCommonParams());
        postRequest(url.toString(), nameValuePairs, s);

    }

    /**
     * 1.18.21.	获取文件
     *
     * @param contractno 贷款合同
     * @param phototype  照片类型
     *                   1：正面照片2：反面照片3：上半身照片
     *                   不传为空时，代表查询全部；传单个数字时，代表查询相对应的单个照片；组合条件时，如传入1,2时，代表查询正面照片和反面照片。（,注意为英文状态下的逗号）
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void downloadLoanFiles(String contractno, String phototype, ServiceResultCallback s)
            throws BaseException, IOException, ParseException {

        StringBuffer url = new StringBuffer(Parameters.serviceURL).append("business/loan/file.json");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("contractno", contractno));
        nameValuePairs.add(new BasicNameValuePair("phototype", phototype));
        nameValuePairs.addAll(getLoanCommonParams());
        getRequest(url.toString(), nameValuePairs, s);


    }

    /**
     * 通过卡号获取银行卡信息
     *
     * @param cardno
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getBankInfoByCardno(String cardno, ServiceResultCallback s) {
        List<NameValuePair> params = getLoanCommonParams();
        params.add(new BasicNameValuePair("cardNo", cardno));
        getRequest("getbankByCardNo", params, s);


    }

    /**
     * 获取省信息
     *
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getProvinceList(ServiceResultCallback s) {
        getRequest("getProvinceList", getLoanCommonParams(), s);


    }

    /**
     * 获取市
     *
     * @param proviceCode
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getCityListOfProvince(String proviceCode, ServiceResultCallback s) {

        List<NameValuePair> params = getLoanCommonParams();
        params.add(new BasicNameValuePair("provinceId", proviceCode));
        getRequest("getCityListOfProvince", params, s);


    }

    /**
     * 获取区
     *
     * @param cityCode
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getDistrictList(String cityCode, ServiceResultCallback s) {


        List<NameValuePair> params = getLoanCommonParams();
        params.add(new BasicNameValuePair("cityId", cityCode));
        getRequest("getDistrictList", params, s);


    }

    public void repayLoan(SwiperInfo swiperInfo, String amount, String contractNo, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = createNameValuePair(true);


        nameValuePairs.add(new BasicNameValuePair("busid", "1EH"));                    //业务代码ID true
//        nameValuePairs.add(new BasicNameValuePair("busid","TAF"));					//收款T菜单业务代码ID true
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));                //商户号码 true
        nameValuePairs.add(new BasicNameValuePair("amount", Utils.yuan2Fen(amount)));//总金额 true
        nameValuePairs.add(new BasicNameValuePair("fee", "000000000000"));                        //手续费 true
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));    //交易类型 true
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));    //发送时间 true
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));//手机号码 true
        nameValuePairs.add(new BasicNameValuePair("otrack", swiperInfo.getEncTracks()));                //磁道（包括二三磁道）
        nameValuePairs.add(new BasicNameValuePair("pinkey", swiperInfo.getPin()));                //个人密码 true
        nameValuePairs.add(new BasicNameValuePair("rnd", swiperInfo.getRandomNumber()));                        //随机数
        nameValuePairs.add(new BasicNameValuePair("issms", "1"));
        nameValuePairs.add(new BasicNameValuePair("mobileno", ""));            //付款人通知手机号
        nameValuePairs.add(new BasicNameValuePair("tips", ""));                    //消费备注
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));//输入条件(跟安全相关
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("posemc", swiperInfo.getPosemc()));//
        nameValuePairs.add(new BasicNameValuePair("icc55", swiperInfo.getIcc55()));//
        nameValuePairs.add(new BasicNameValuePair("cardsn", swiperInfo.getCardsn()));
        nameValuePairs.add(new BasicNameValuePair("track2", swiperInfo.getTrack2()));
        nameValuePairs.add(new BasicNameValuePair("track1", swiperInfo.getTrack1()));
        nameValuePairs.add(new BasicNameValuePair("curcode", "156"));
        nameValuePairs.add(new BasicNameValuePair("creditno", contractNo));
        nameValuePairs.add(new BasicNameValuePair("type", "PR"));

        if (swiperInfo.getCardType() != SwiperInfo.CardType.MSC)
            nameValuePairs.add(new BasicNameValuePair("pan", swiperInfo.getMaskedPan()));
        postRequest("trade", nameValuePairs, s);
    }


    /**
     * 支行信息
     */
    public void getBranchInfo(String bankCode, String areaCode, ServiceResultCallback s) {//,IOException,BaseException{

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("business/conf/bank.json");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("code", bankCode));
        nameValuePairs.add(new BasicNameValuePair("areaCode", areaCode));

        getRequest(url.toString(), nameValuePairs, s);

    }

    public void getLocalInfo(String code, ServiceResultCallback s) throws JSONException, IOException, BaseException {
        queryBankProviceArea(code, s);
    }

    public void queryBankProviceArea(String code, ServiceResultCallback s) {//,IOException,BaseException{

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("business/conf/area.json");

        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        if (null != code && !"".equals(code)) {
            parameter.add(new BasicNameValuePair("code", code));
        }

        getRequest(url.toString(), parameter, s);


    }


    //立即提款状态请求
    public void isOpenD0(ServiceResultCallback s) {// BaseException, IOException {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "T0"));

//        this.getRequest("accountInfo", nameValuePairs, s);
        this.getRequest("business/d0/queryMerStatus", nameValuePairs, s);

    }
    //秒到状态请求
    public void isOpenD02(ServiceResultCallback s) {// BaseException, IOException {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "T0"));

        this.getRequest("business/d0/queryMerStatus", nameValuePairs, s);

    }

    public void isOpenOnDayLoan2(ServiceResultCallback s) {// BaseException, IOException {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "T0"));

        this.getRequest("business/d0/queryOnedayLoanBusiness", nameValuePairs, s);

    }
    //一日贷状态请求
    public void isOpenOnDayLoan(ServiceResultCallback s) {// BaseException, IOException {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("busid", "T0"));

        this.getRequest("business/d0/queryOnedayLoanStatus", nameValuePairs, s);

    }


    //3.0 新增接口

    /**
     * 1.25实名认证
     *
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void queryRealNameAuth(ServiceResultCallback s) {//,IOException,BaseException{

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL)
                .append("business/realname/merchant/")
                .append(ApplicationEx.getInstance().getUser().getLoginName())
                .append(".json");

        List<NameValuePair> parameter = new ArrayList<NameValuePair>();

        getRequest(url.toString(), parameter, s);


    }

    /**
     * 大额开户
     *
     * @param uploadSid 上传流水号
     * @param industry  行业代码
     * @param idcardNo  证件号
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void largeLitmitApply(String uploadSid, String industry, String idcardNo, String realName, ServiceResultCallback s) {//,IOException,BaseException{

        String url = "business/blimit/apply";
        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        parameter.add(new BasicNameValuePair("uploadSid", uploadSid));
        parameter.add(new BasicNameValuePair("industry", industry));
        parameter.add(new BasicNameValuePair("idcardNo", idcardNo));
        parameter.add(new BasicNameValuePair("realname", realName));

        postRequest(url.toString(), parameter, s);


    }

    /**
     * 大额交易信息上送
     *
     * @param uploadSid 上传流水号
     * @param mobile    付款人手机号
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void largeLimitInfoTrade(String uploadSid, String mobile, ServiceResultCallback s) {//,IOException,BaseException{

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL)
                .append("business/blimit/trade/")
                .append(ApplicationEx.getInstance().getUser().getLoginName())
                .append(".json");

        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        parameter.add(new BasicNameValuePair("uploadSid", uploadSid));
        parameter.add(new BasicNameValuePair("mobile", mobile));

        postRequest(url.toString(), parameter, s);


    }

    /**
     * 获取广告
     *
     * @param sheet      版面 空为全部 INDEX:首页
     * @param resolution 分辨率S:小M:中L:大  不送为默认
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void getAds(String sheet, String resolution, ServiceResultCallback s) {//{

        StringBuffer url = new StringBuffer();
        url.append("advert/advertInfo");

        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        if (!TextUtils.isEmpty(sheet)) {
            parameter.add(new BasicNameValuePair("sheet", sheet));
        }
        if (!TextUtils.isEmpty(resolution)) {
            parameter.add(new BasicNameValuePair("resolution", resolution));
        }

        getRequest(url.toString(), parameter, s);


    }

    public void getOLD_BANK_CODE_MAP(ServiceResultCallback s) {// IOException, BaseException {
        String url = ("getCommonDict");
        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        parameter.add(new BasicNameValuePair("param", "OLD_BANK_CODE_MAP"));
        getRequest(url.toString(), parameter, s);

    }


    /**
     * @param btype
     * @param map
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void uploadImages(String btype, Map<String, String> map, ServiceResultCallback s) {

        String url = ("business/upload");

        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        parameter.add(new BasicNameValuePair("btype", btype));
        parameter.add(new BasicNameValuePair("type", "pic"));


        postRequest(url.toString(), map, parameter, s);


    }

    public void uploadLargeAmountApplyImages(String picCertFront, String picCerBack, String merchantCer, ServiceResultCallback s) {

        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("picCertFront", picCertFront);
        fileMap.put("picCertBack", picCerBack);
        fileMap.put("picAgreement", merchantCer);

        uploadImages("blimit", fileMap, s);

    }

    public void uploadLargeAmountTradeInfoImages(String picCard, String picHold, String picCert, ServiceResultCallback s) {
        Map<String, String> fileMap = new HashMap<>();
        fileMap.put("picCard", picCard);
        fileMap.put("picHold", picHold);
        fileMap.put("picCert", picCert);

        uploadImages("blimittrade", fileMap, s);

    }


    /**
     * 获取上送图片
     *
     * @param btype
     * @param type
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws BaseException
     */
    public void getThreePic(String btype, String type, ServiceResultCallback s) {//,IOException,BaseException{

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL)
                .append("business/upload/")
                .append(type).append("/").append(btype).append("/")
                .append(ApplicationEx.getInstance().getUser().getLoginName())
                .append(".json");

        List<NameValuePair> parameter = new ArrayList<NameValuePair>();
        parameter.add(new BasicNameValuePair("btype", btype));

        getRequest(url.toString(), parameter, s);


    }


    public void getLargeAmountAccess(ServiceResultCallback s) {

        String url = "business/blimit/query";
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        this.postRequest(url.toString(), nameValuePairs, s);

        //设置若接口获取失败时的默认状态

    }

    public void getBarcodeAccess(ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        //微信开户查询
        this.getRequest("business/imagepay/apply/wechat", nameValuePairs, s);

    }

    /**
     * 获取商户二维码
     *
     * @param s
     */
    public void getMerQRCode(ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        this.getRequest("business/imagepay/apply/getMerQRCode", nameValuePairs, s);

    }

    public void openScancode(ServiceResultCallback s, boolean isC_B) {
        //we
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (isC_B) {
            nameValuePairs.add(new BasicNameValuePair("openType", String.valueOf(2)));
        } else
            nameValuePairs.add(new BasicNameValuePair("openType", String.valueOf(1)));
        this.postRequest("business/imagepay/apply/wechat/", nameValuePairs, s);

    }

    public void scancodeCollection(String amount, String code, int timeout, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));

        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));    //发送机构号
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));    //发送方跟踪号
        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getUser().getTerminalId()));    //终端号
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));    //商户号
        nameValuePairs.add(new BasicNameValuePair("dimencode", code));    //二维码
        nameValuePairs.add(new BasicNameValuePair("amount", Utils.yuan2Fen(amount)));    //消费金额
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));    //交易传输时间
        nameValuePairs.add(new BasicNameValuePair("rnd", Mac.getRnd()));    //随机数?
        nameValuePairs.add(new BasicNameValuePair("busid", "W00001"));    //交易代码


        this.postRequest("cTransaction", nameValuePairs, timeout, s);

    }

    public void scancodeQuery(String srcsid, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));

        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));    //发送机构号
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));    //发送方跟踪号
        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getUser().getTerminalId()));    //终端号
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));    //商户号
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));    //交易传输时间
        nameValuePairs.add(new BasicNameValuePair("rnd", Mac.getRnd()));    //随机数?
        nameValuePairs.add(new BasicNameValuePair("busid", "W00002"));    //交易代码
        nameValuePairs.add(new BasicNameValuePair("srcsid", srcsid));    //交易ID

        this.postRequest("qTransaction", nameValuePairs, s);

    }

    public void scancodeRevocation(String srcsid, int timeout, String series, ServiceResultCallback s) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));    //发送机构号
        nameValuePairs.add(new BasicNameValuePair("series", series));    //发送方跟踪号
        nameValuePairs.add(new BasicNameValuePair("termid", ApplicationEx.getInstance().getUser().getTerminalId()));    //终端号
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));    //商户号
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));    //交易传输时间
        nameValuePairs.add(new BasicNameValuePair("rnd", Mac.getRnd()));    //随机数?
        nameValuePairs.add(new BasicNameValuePair("busid", "W00003"));    //交易代码
        nameValuePairs.add(new BasicNameValuePair("srcsid", srcsid));    //交易id

        this.postRequest("cTransaction", nameValuePairs, timeout, s);

    }

    public void largeAmountCollection(SwiperInfo swiperInfo, String amount, String srcSid, String mobile, ServiceResultCallback s) {

        StringBuffer url = new StringBuffer();
        url.append(Parameters.serviceURL).append("commitTransaction.json");

        List<NameValuePair> nameValuePairs = createNameValuePair(true);

        nameValuePairs.add(new BasicNameValuePair("busid", "X00001"));                    //业务代码ID true
//        nameValuePairs.add(new BasicNameValuePair("busid","TAF"));					//收款T菜单业务代码ID true
        nameValuePairs.add(new BasicNameValuePair("lpmercd", ApplicationEx.getInstance().getUser().getMerchantInfo().getMerNo()));                //商户号码 true
        nameValuePairs.add(new BasicNameValuePair("amount", amount));                //总金额 true
        nameValuePairs.add(new BasicNameValuePair("fee", "000000000000"));                        //手续费 true
        nameValuePairs.add(new BasicNameValuePair("series", Util.createSeries()));    //交易类型 true
        nameValuePairs.add(new BasicNameValuePair("tdtm", Util.dateForWallet()));    //发送时间 true
        nameValuePairs.add(new BasicNameValuePair("mobile", ApplicationEx.getInstance().getUser().getLoginName()));//手机号码 true
        nameValuePairs.add(new BasicNameValuePair("otrack", swiperInfo.getEncTracks()));                //磁道（包括二三磁道）
        nameValuePairs.add(new BasicNameValuePair("pinkey", swiperInfo.getPin()));                //个人密码 true
        nameValuePairs.add(new BasicNameValuePair("rnd", swiperInfo.getRandomNumber()));                        //随机数
        nameValuePairs.add(new BasicNameValuePair("issms", "1"));
        nameValuePairs.add(new BasicNameValuePair("mobileno", mobile));            //付款人通知手机号
        nameValuePairs.add(new BasicNameValuePair("srcsid", srcSid));//照片上送sid
        nameValuePairs.add(new BasicNameValuePair("chntype", "02101"));//输入条件(跟安全相关
        nameValuePairs.add(new BasicNameValuePair("chncode", "LAKALASD"));
        nameValuePairs.add(new BasicNameValuePair("posemc", swiperInfo.getPosemc()));//
        nameValuePairs.add(new BasicNameValuePair("icc55", swiperInfo.getIcc55()));//
        nameValuePairs.add(new BasicNameValuePair("cardsn", swiperInfo.getCardsn()));
        nameValuePairs.add(new BasicNameValuePair("track2", swiperInfo.getTrack2()));
        nameValuePairs.add(new BasicNameValuePair("track1", swiperInfo.getTrack1()));
        if (swiperInfo.getIcc55() != null && swiperInfo.getIcc55().length() != 0)
            nameValuePairs.add(new BasicNameValuePair("pan", swiperInfo.getMaskedPan()));
        postRequest(url.toString(), nameValuePairs, s);


    }

    public void getScancodeLimit(ServiceResultCallback s) {// IOException, BaseException {

        getCommonDict("SIGN_PAY", s);
    }

    public void getLargeAmountLimit(ServiceResultCallback s) {// JSONException, ParseException,
        // G_CSH
        getCommonDict("BLIMIT", s);

    }
//
//    是否允许降级
//    /getCommonDict
//            param=IC_DOWN_ENABLED
//    true 允许
//    false 不允许

    public void getIcDownEnable(ServiceResultCallback s) {// JSONException, ParseException,
        // G_CSH
        getCommonDict("IC_DOWN_ENABLED", s);
    }


}