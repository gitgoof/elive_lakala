package com.lakala.platform.http;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.encryption.Digest;
import com.lakala.library.encryption.MTSMac;
import com.lakala.library.encryption.Mac;
import com.lakala.library.exception.BaseException;
import com.lakala.library.exception.HttpException;
import com.lakala.library.exception.ServerResultDataException;
import com.lakala.library.exception.TradeException;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.AppUtil;
import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.bean.Session;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.platform.common.PropertiesUtil;
import com.lakala.platform.common.Utils;
import com.lakala.platform.common.avos.AvosSdkManager;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.platform.config.Config;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.ui.dialog.ProgressDialog;
import com.loopj.lakala.http.AsyncHttpClient;
import com.loopj.lakala.http.RequestParams;

import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 业务请求基础类
 * <p/>
 * Created by Michael on 14-16-10
 */
public class BusinessRequest extends HttpRequest {

    //默认版本号
    public static final String DEFAULT_REQUEST_VERSION = "v1.0";

    public static final String CHN_CODE = "LAKALASD";
    public static final String CHN_TYPE = "02101";

    public static final String X_B_TOKEN = "X-B-TOKEN";

    public static final String SUCCESS_CODE = "000000";
    public static final String TOKEN_ERROR = "000104";
    public static final String REFRESH_TOKEN_ERROR = "000103";
    public static final String TIMEOUT_CODE = "000009";

    //该客户端默认服务器
    public static final String SCHEME_MSVR = "msvr://";
    //本地
    public static final String SCHEME_LOCAL = "local://";
    //mts 服务器
    public static final String SCHEME_MTS = "mts://";

    /**
     * User_Identifier 参数，该参数是用 LoginName 做 MD5 生成的。
     */
    public static final String USER_IDENTIFIER_KEY = "User_Identifier";

    private static final String TAG = BusinessRequest.class.getName();
    //base url
    private static String BASE_URL = PropertiesUtil.getUrl();

    //自动处理 progress dialog
    private boolean autoShowProgress;
    private boolean autoShowToast = false;
    private ProgressDialog progressDialog;
    //progress message
    private String progressMessage;
    //当前请求 scheme
    private String currentScheme;

    /**
     * 根据请求地址和请求方法获取request对象
     *
     * @param url    当前请求的 url 地址
     * @param method 当前请求的 method 方法
     * @return {@link com.lakala.platform.http.BusinessRequest}
     */
    public static BusinessRequest obtainRequest(String url, RequestMethod method) {
        return obtainRequest(null, url, method);
    }

    /**
     * 根据上下文，请求地址，请求方法获取request对象,该方法自动处理 progress dialog
     *
     * @param context 当前请求的 context
     * @param url     当前请求的 url 地址
     * @param method  当前请求的 method 方法
     * @return {@link com.lakala.platform.http.BusinessRequest}
     */
    public static BusinessRequest obtainRequest(Context context, String url, RequestMethod method) {
        BusinessRequest request = new BusinessRequest(context);
        request.setRequestURL(url);
        request.setRequestMethod(method);
        request.setAutoShowProgress(false);
        return request;
    }

    /**
     * 根据上下文，请求地址，请求方法获取request对象
     *
     * @param context          当前请求的 context
     * @param url              当前请求的 url 地址
     * @param method           当前请求的 method 方法
     * @param autoShowProgress 是否自动处理当前请求的 progress dialog
     * @return {@link com.lakala.platform.http.BusinessRequest}
     */
    public static BusinessRequest obtainRequest(Context context, String url, RequestMethod method, boolean autoShowProgress) {
        BusinessRequest request = new BusinessRequest(context);
        request.setRequestURL(url);
        request.setRequestMethod(method);
        request.setAutoShowProgress(autoShowProgress);
        return request;
    }

    /**
     * 根据上下文，请求地址，scheme，请求方法获取request对象
     *
     * @param context          当前请求的 context
     * @param scheme           scheme
     * @param url              当前请求的 url 地址
     * @param method           当前请求的 method 方法
     * @param autoShowProgress 是否自动处理当前请求的 progress dialog
     * @return {@link com.lakala.platform.http.BusinessRequest}
     */
    public static BusinessRequest obtainRequest(Context context, String scheme, String url, RequestMethod method, boolean autoShowProgress) {
        BusinessRequest request;
        if (scheme.equals(SCHEME_MTS)) {
            request = new BusinessRequest(new MTSHttpResponseHandler(), context);
            if (!url.contains(SCHEME_MTS) && !url.contains("://")) {
                url = SCHEME_MTS.concat(url);
            }
        } else {
            request = new BusinessRequest(context);
        }
        request.setCurrentScheme(scheme);
        request.setRequestURL(url);
        request.setRequestMethod(method);
        request.setAutoShowProgress(autoShowProgress);
        return request;
    }

    public BusinessRequest(Context context) {
        this(new ResultDataResponseHandler(
                new ServiceResultCallback() {
                    @Override
                    public void onSuccess(ResultServices resultServices) {

                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {

                    }
                }
        ), context);
    }

    /**
     * constructor
     *
     * @param responseHandler 当前请求的Http解析器
     */
    public BusinessRequest(HttpResponseHandler responseHandler, Context context) {
        super(responseHandler, context);

        addMustHeader();//增加报文头
//        this.requestParams = new CommonRequestParams();
//        ((CommonRequestParams)(this.requestParams)).addCommonParams();
    }


    /**
     * 添加请求头
     */
    private void addMustHeader() {
        setHeader(AsyncHttpClient.SHA1_KEY,AsyncHttpClient.SHA1);
        setHeader(AsyncHttpClient.CRC32_KEY,AsyncHttpClient.CRC32);
        setHeader(AsyncHttpClient.HEADER_CONTENT_TYPE, AsyncHttpClient.DEFAULT_HEADER);
//        setHeader(AsyncHttpClient.X_Client_Ver, "V"+AppUtil.getAppVersionCode(ApplicationEx.getInstance()));
        setHeader(AsyncHttpClient.X_Client_Plantform, "Android");
        setHeader(AsyncHttpClient.X_Device_Id, DeviceUtil.getDeviceId(ApplicationEx.getInstance()));
        String accessToken = ApplicationEx.getInstance().getSession().getUser().getAccessToken();
        if (null != accessToken && !"".equals(accessToken) && !"null".equals(accessToken)) {
            setHeader(AsyncHttpClient.AUTHORIZATION, "Bearer " + accessToken);
        }

        LocationManager locationManager = LocationManager.getInstance();
        if (LocationManager.getInstance().getLatitude() != 0) {
            setHeader(AsyncHttpClient.X_Device_Location, "" + locationManager.getLatitude() + "," + locationManager.getLongitude());
        }
        setHeader(AsyncHttpClient.X_Client_Ver, (AppUtil.getAppVersionCode(ApplicationEx.getInstance())).toString());

        setHeader(AsyncHttpClient.X_Client_Series, UUID.randomUUID().toString().replace("-", ""));//UUID 去掉横杆

        setHeader(AsyncHttpClient.X_Client_PV, "MPOS");
        if (!TextUtils.isEmpty(AvosSdkManager.installationId)) {
            setHeader(AsyncHttpClient.X_Push_ID, AvosSdkManager.installationId);
        }

        setHeader(AsyncHttpClient.X_Device_Model, DeviceUtil.getPhoneModel());
        setHeader(AsyncHttpClient.X_Device_Manufacture, DeviceUtil.getPhoneManufacturer());
        setHeader(AsyncHttpClient.X_Tel_OP, DeviceUtil.getPhoneISP(ApplicationEx.getInstance()));
        setHeader(AsyncHttpClient.X_Device_Name, Build.DEVICE);
        setHeader(AsyncHttpClient.X_IMEI, DeviceUtil.getIMEI(ApplicationEx.getInstance()));
        setHeader(AsyncHttpClient.X_IMSI, DeviceUtil.getIMSI(ApplicationEx.getInstance()));
    }


    /**
     * 获取BaseUrl
     *
     * @return Base Url
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * 设置自动处理 progress dialog
     *
     * @param autoShowProgress 设置是否显示 ProgressBar
     */
    public BusinessRequest setAutoShowProgress(boolean autoShowProgress) {
        this.autoShowProgress = autoShowProgress;
        return this;
    }

    /**
     * 设置自动toast
     *
     * @param autoToast 设置是否自动显示 设置自动toast
     */
    public BusinessRequest setAutoShowToast(boolean autoToast) {
        this.autoShowToast = autoToast;
        return this;
    }

    /**
     * 设置 progress message
     *
     * @param message ProgressBar 的消息文本
     * @return
     */
    public BusinessRequest setProgressMessage(String message) {
        this.progressMessage = message;
        return this;
    }

    /**
     * 获取当前 scheme
     *
     * @return String
     */
    public String getCurrentScheme() {
        return currentScheme;
    }

    /**
     * 设置当前 scheme
     *
     * @param currentScheme scheme
     * @return String
     */
    public BusinessRequest setCurrentScheme(String currentScheme) {
        this.currentScheme = currentScheme;
        return this;
    }

    @Override
    protected void onExecuteBefore() {
        requestURL = replaceHostByUrlSecheme(requestURL);

        if (!requestURL.contains("://")) {
            //URL 不是绝对路径则，在前面添加主机头。
            requestURL = BASE_URL.concat(requestURL);
        }

        User user = ApplicationEx.getInstance().getUser();
        String loginName = user != null ? user.getLoginName() : null;

        //如果当前请求启用了缓存规则，则在参数中添加一个 User_Identifier参数，该参数是用 LoginName 做 MD5 生成的。
        //该参数可用于分别缓存用户化的数据。
        if (this.cacheRuleName != null && loginName != null) {
            this.requestParams.add(USER_IDENTIFIER_KEY, Digest.md5(loginName));
        }

        if (TextUtils.isEmpty((String) requestParams.get("mac")) && (requestURL.contains("trade") || requestURL.contains("cTransaction") || requestURL.contains("qTransaction"))) {
            if (!requestURL.contains("ewallet")) {
                addMac();
            }
        }


        //暂时只判断 mts
        if (!TextUtils.isEmpty(currentScheme) && currentScheme.equals(SCHEME_MTS)) {
            addMTSCommonParams();
            addMTSHead();
            addMTSMac();
            LogUtil.print("", "" + requestParams.getUrlParams());
            return;
        }

        if (requestURL.contains("product/financial")) {
            addMTSHead();
        }

        List<BasicNameValuePair> list=requestParams.getUrlParams();
        String str="\n";
        for (int i=0;i<list.size();i++){
            str=str+list.get(i).getName()+"="+list.get(i).getValue()+"\n";
        }
        LogUtil.print("------>", "" + str);

    }

    /**
     * 设置accept
     *
     * @param value 当前请求 Head 的 Accept 的值
     */
    public void setAccept(String value) {
        setHeader("Accept", value);
    }

    /**
     * 根据 URL 的 Secheme 替换主机地址
     *
     * @param url 当前请求的 url 地址
     * @return 当前请求的 url 地址
     */
    public static String replaceHostByUrlSecheme(String url) {
        if (url == null) {
            return "";
        }

        //绝对路径
        if (url.contains(SCHEME_MSVR)) {
            url = BASE_URL + url.substring(SCHEME_MSVR.length());
        } else if (url.contains(SCHEME_LOCAL)) {
            String rootPath = Config.getWebAppRootPath();
            url = rootPath.concat(url.substring(SCHEME_LOCAL.length()));
        } else if (url.contains(SCHEME_MTS)) {
            url = Config.getMTSUrl() + url.substring(SCHEME_MTS.length());
        }

        return url;
    }

    /**
     * 加载 MTS 公共参数
     */
    private void addMTSCommonParams() {
        requestParams.put("_Platform", "android");
        requestParams.put("_TimeStamp", new Date().getTime() + "");
        requestParams.put("_GesturePwd", ApplicationEx.getInstance().getUser().isExistGesturePassword() ? "1" : "0");//0没有 1有
        String deviceId = DeviceUtil.getDeviceId(context);
        Calendar calendar = Calendar.getInstance();

        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );
        String md5Value = Digest.md5(guid);
        requestParams.put("_Guid", md5Value);
        requestParams.put("_DeviceId", deviceId);
        requestParams.put("_DeviceModel", Build.MODEL);
        requestParams.put("_SubChannelId", "10000027");
        requestParams.put("_AccessToken", ApplicationEx.getInstance().getSession().getUser().getAccessToken());
        requestParams.put("_RefreshToken", ApplicationEx.getInstance().getSession().getUser().getRefreshToken());
    }

    /**
     * 加载钱包MTS公共参数
     */

    public void addWalletMTSCommonParams() {
        requestParams.put("platform", "android");
        requestParams.put("timeStamp", new Date().getTime() + "");
        requestParams.put("gesturePwd", ApplicationEx.getInstance().getUser().isExistGesturePassword() ? "1" : "0");//0没有 1有
        String deviceId = DeviceUtil.getDeviceId(context);
        Calendar calendar = Calendar.getInstance();

        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );
        String md5Value = Digest.md5(guid);
        requestParams.put("guid", md5Value);
        requestParams.put("deviceId", deviceId);
        requestParams.put("deviceModel", Build.MODEL);
        requestParams.put("subChannelId", "10000027");
        requestParams.put("accessToken", ApplicationEx.getInstance().getSession().getUser().getAccessToken());
        requestParams.put("refreshToken", ApplicationEx.getInstance().getSession().getUser().getRefreshToken());
    }

    /**
     * 添加 MTS mac
     */
    private void addMTSMac() {
        String macString = MTSMac.resolveMacData(requestParams.getNameValuePairs());

        if (StringUtil.isEmpty(macString)) {
            return;
        }

        //VirtualTerminalKey
        Session session = ApplicationEx.getInstance().getSession();
        User user = ApplicationEx.getInstance().getUser();
        String ksn = "";
        if (!TextUtils.isEmpty(session.getCurrentKSN())) {
            //优先使用收款宝终端号
            ksn = session.getCurrentKSN();
        } else {
            ksn = user.getMtsTerminalId();
        }
        try {
            String mac = CommonEncrypt.generateMac(ksn, macString);
            if (StringUtil.isNotEmpty(mac) && mac.length() > 8) {
                mac = mac.substring(0, 8);
                requestParams.put("_MacValue", mac);
            }
        } catch (Exception e) {
            //Silent
        }

    }

    /**
     * 添加 MTS head
     */
    private void addMTSHead() {
        setHeader("Platform", "android");
        setHeader("AppVersion", AppUtil.getAppVersionCode(ApplicationEx.getInstance()));
        setHeader("SubChannelId", "10000027");
        setHeader("SubChannelId", "10000027");
        setHeader("_IsNewMPOS", "1");

    }

    private String getRunningActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
    }

    /**
     * 显示 ProgressBar
     */
    private void showProgressDialog() {
        if (getContext() == null) {
            return;
        }

        if (getContext() instanceof FragmentActivity && !((FragmentActivity) getContext()).isFinishing()) {
            progressDialog = new ProgressDialog(context);
            //如果是交易类接口不允许关闭进度条
            if (getRequestURL().contains("trade")) {
                progressDialog.setCancelable(false);
            }
            if (!getRunningActivityName(getContext()).equals(getContext().getClass().getName())) {
                return;
            }

            FragmentActivity activity = ((FragmentActivity) getContext());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setProgressMessage(progressMessage);
                    progressDialog.show();
                }
            });
        }
    }

    /**
     * 隐藏 ProgressBar
     */
    private void dismissProgressDialog() {
        if (progressDialog != null && getContext() != null) {

            ((FragmentActivity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        progressDialog = null;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Config.isDebug()) {
            //请求
            LogUtil.print(TAG, "Url : " + getRequestURL());
            LogUtil.print(TAG, "Params : " + getRequestParams().toString());
        }
        if (autoShowProgress) {
            showProgressDialog();
        }
    }

    @Override
    public void onSuccess() {
        super.onSuccess();

    }

    @Override
    public void onFailure(BaseException error) {
        super.onFailure(error);

        //交易失败
        if (error instanceof TradeException) {
            autoToastErrorMessage(error.getMessage());
            if (!currentScheme.equals(SCHEME_MTS)) {
                return;
            }
            String code = ((TradeException) error).getTradeCode();
            try {
                Class cls = Class.forName("com.lakala.shoudan.util.LoginUtil");
                Method method = cls.getDeclaredMethod("checkLoginOut", String.class, Context.class);
                method.invoke(null, code, ApplicationEx.getInstance());
            } catch (Exception e) {
                // Silent
            }
        }
        //数据异常
        else if (error instanceof ServerResultDataException) {
            autoToastErrorMessage(ApplicationEx.getInstance().getString(R.string.plat_http_002));
        }
        //http error
        else if (error instanceof HttpException) {
            if (((HttpException) error).getStatusCode() < 1000) {
//                String msg = String.format("%s %d",
//                        ApplicationEx.getInstance().getString(R.string.plat_http_003),
//                        ((HttpException) error).getStatusCode());
//                autoToastErrorMessage(msg);
            } else {
                switch (((HttpException) error).getStatusCode()) {
                    case HttpException.ERRCODE_REQUEST_CERTIFICATE_INVAILD:
                    case HttpException.ERRCODE_REQUEST_CERTIFICATE_HAS_BADDATE:
                        autoToastErrorMessage(ApplicationEx.getInstance().getString(R.string.plat_http_001));
                        break;

                    case HttpException.ERRCODE_REQUEST_NOT_INTERNET:
                    case HttpException.ERRCODE_REQUEST_NOT_NETWORK:
                    case HttpException.ERRCODE_REQUEST_CANNOT_CONNECT_TO_HOST:
                        autoToastErrorMessage(ApplicationEx.getInstance().getString(R.string.network_fail));
                        break;

                    case HttpException.ERRCODE_REQUEST_TIMEOUT:
                    case HttpException.ERRCODE_REQUEST_UNKNOWN_ERROR:
                    default:
                        autoToastErrorMessage(ApplicationEx.getInstance().getString(R.string.network_fail));
                        break;

                    case HttpException.ERRCODE_REQUEST_PARASE_DATA_FAIL:
                        autoToastErrorMessage(ApplicationEx.getInstance().getString(R.string.plat_http_002));
                        break;

                    case HttpException.ERRCODE_REQUEST_TRANSACTION_FAIL:
                        autoToastErrorMessage(error.getMessage());
                        break;
                }
            }
        }
    }

    @Override
    public void onFinish() {
        if (Config.isDebug()) {
            //应答
//            HttpResponseHandler responseHandler = getResponseHandler();
//            LogUtil.print(TAG, "Code : " + responseHandler.getResultCode());
//            LogUtil.print(TAG, "MSG : " + responseHandler.getResultMessage());
//            LogUtil.print(TAG, "Data : " + responseHandler.getResultData());
        }

        super.onFinish();

        if (autoShowProgress) {
            dismissProgressDialog();
        }
    }

    /**
     * toast 提示错误信息
     *
     * @param msg
     */
    private void autoToastErrorMessage(String msg) {
        if (autoShowToast) {
            ToastUtil.toast(ApplicationEx.getInstance(), msg);
        }
    }

    /**
     * 设置新URL;
     *
     * @param newUrl
     */
    public static void setBASE_URL(String newUrl) {
        BASE_URL = newUrl;
    }

    /**
     * 生成交易需要的RequestParams
     *
     * @param swiperInfo
     * @return
     */
    public RequestParams obtainTransRequestParams(SwiperInfo swiperInfo) {

        requestParams.put("otrack", swiperInfo.getEncTracks());
        requestParams.put("rnd", swiperInfo.getRandomNumber());
        requestParams.put("pinkey", swiperInfo.getPin());
        requestParams.put("posemc", swiperInfo.getPosemc());
        requestParams.put("icc55", swiperInfo.getIcc55());
        requestParams.put("cardsn", swiperInfo.getCardsn());
        requestParams.put("track2", swiperInfo.getTrack2());
        if (swiperInfo.getCardType() != SwiperInfo.CardType.MSC)
            requestParams.put("pan", swiperInfo.getMaskedPan());
        requestParams.put("series", Utils.createSeries());
        requestParams.put("track1", swiperInfo.getTrack1());
        requestParams.put("termid", ApplicationEx.getInstance().getSession().getCurrentKSN());
        requestParams.put("chntype", CHN_TYPE);
        requestParams.put("chncode", CHN_CODE);


        return requestParams;
    }


    public void addMac() {
        String macString = Mac.resolveMacData(requestParams.getNameValuePairs());

        LogUtil.print(requestParams.getNameValuePairs().toString());

        //VirtualTerminalKey
        Session session = ApplicationEx.getInstance().getSession();
        User user = ApplicationEx.getInstance().getUser();
        String ksn = "";
        if (requestParams.get("termid") != null) {
            ksn = (String) requestParams.get("termid");
        } else {

            if (!TextUtils.isEmpty(session.getCurrentKSN())) {
                //优先使用收款宝终端号
                ksn = session.getCurrentKSN();
            } else {
                ksn = user.getTerminalId();
            }

        }
        String mac = CommonEncrypt.generateMac(ksn, macString);
        if (StringUtil.isNotEmpty(mac) && mac.length() > 8) {
            mac = mac.substring(0, 8);
            requestParams.put("mac", mac);
        }
    }


    public static String getMac(HttpRequestParams params) {
        String macString = Mac.resolveMacData(params.getNameValuePairs());

        LogUtil.print(params.getNameValuePairs().toString());

        //VirtualTerminalKey
        Session session = ApplicationEx.getInstance().getSession();
        User user = ApplicationEx.getInstance().getUser();
        String ksn = "";
        if (params.get("termid") != null) {
            ksn = (String) params.get("termid");
        } else {

            if (!TextUtils.isEmpty(session.getCurrentKSN())) {
                //优先使用收款宝终端号
                ksn = session.getCurrentKSN();
            } else {
                ksn = user.getTerminalId();
            }

        }
        String mac = CommonEncrypt.generateMac(ksn, macString);
        if (StringUtil.isNotEmpty(mac) && mac.length() > 8) {
            mac = mac.substring(0, 8);
          //  params.put("mac", mac);
            return  mac;
        }
        return  mac;
    }


    public HttpRequest setResponseHandler(ServiceResultCallback serviceResultCallback) {
        return super.setResponseHandler(new ResultDataResponseHandler(serviceResultCallback));
    }
}
