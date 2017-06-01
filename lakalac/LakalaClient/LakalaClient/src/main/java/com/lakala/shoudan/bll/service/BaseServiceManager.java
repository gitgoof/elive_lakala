package com.lakala.shoudan.bll.service;

import android.net.Uri;
import android.text.TextUtils;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.common.Parameters;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.BaseException;
import com.lakala.shoudan.util.ImageUtil;
import com.loopj.lakala.http.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 服务管理器的基础类
 * 提供通用的工具方法
 *
 * @author bob
 */
public class BaseServiceManager {
    private final static char charTable[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                    'u', 'v', 'w', 'x', 'y', 'z',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                    'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * 唯一ID，客户端可根据手机平台的特性，可选取设备号，
     * ESMI，或者其它可以唯一标示用户ID的字符串。
     */
    private String uid = null;

    /**
     * 验证码，uid+客户端密码 进行MD5消息摘要值。客户端密码为服务端
     * 与每个手机平台客户端应用程序之间约定的密码。
     */
    private String vercode = null;

    /**
     * 生成验证码。
     *
     * @throws NoSuchAlgorithmException
     */
    protected void generateVercode() {
        //随机产生 UID
        this.uid = generateUID(16);

        String origWord = "";
        origWord = uid + Parameters.androidClientID;

        //得到Md5 摘要算法实例
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md5.update(origWord.getBytes());

        //得到摘要数据
        byte[] md5Word = md5.digest();

        //将摘要数据格式化成字符串并保存。
        this.vercode = String.format(
                "%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
                md5Word[0],
                md5Word[1],
                md5Word[2],
                md5Word[3],
                md5Word[4],
                md5Word[5],
                md5Word[6],
                md5Word[7],
                md5Word[8],
                md5Word[9],
                md5Word[10],
                md5Word[11],
                md5Word[12],
                md5Word[13],
                md5Word[14],
                md5Word[15]);
    }

    public String getUid() {
        return uid;
    }

    public String getVercode() {
        return vercode;
    }


    /**
     * 执行 Put 请求，并添加安全认证参数到请求中。
     *
     * @param url
     * @param parameter
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void putRequest(String url, List<NameValuePair> parameter, ServiceResultCallback serviceResultCallback) //throws ParseException, IOException, BaseException
    {

        //由于服务端put 方法不能从body体里取出数据，所以将vercode放在url上。
        List<NameValuePair> vercode = new ArrayList<NameValuePair>();
        vercode = addVercode(vercode);
        filterUrlAndAppendMac(url, parameter);


        sendRequest(url, parameter, HttpRequest.RequestMethod.PUT, serviceResultCallback);
//
//        JSONObject json = RestWebService.putRequest(url, parameter,HttpUtil.EncodingCharset);
//
//		try {
//			resultForService = json2ResultForService(json);
//			checkToken(resultForService.retCode);
//		} catch (JSONException e) {
//			// json 解析异常服务返回了错误的数据，在这返回自定义的异常类。
//			throw new BaseException("",ExceptionCode.ServerResultDataError, 0);
//		}

        return;
    }


    private void sendRequest(String url, List<NameValuePair> parameter, HttpRequest.RequestMethod requestMethod, ServiceResultCallback serviceResultCallback) {

        sendRequest(url, parameter, null, requestMethod, serviceResultCallback);

    }

    private void sendRequest(String url, List<NameValuePair> parameter, Map<String, String> fileMap, HttpRequest.RequestMethod requestMethod, ServiceResultCallback serviceResultCallback) {

        BusinessRequest businessRequest = BusinessRequest.obtainRequest(null, requestMethod);
        businessRequest.setRequestURL(BusinessRequest.DEFAULT_REQUEST_VERSION + "/" + url);
        RequestParams requestParams = businessRequest.getRequestParams();
        if (parameter != null) {
            for (NameValuePair nameValuePair : parameter) {
                if (!TextUtils.isEmpty(nameValuePair.getValue()))
                    requestParams.put(nameValuePair.getName(), nameValuePair.getValue());
            }
        }

        if (fileMap != null) {

            for (String key : fileMap.keySet()) {
                try {
                    String uri = fileMap.get(key);
                    if (!TextUtils.isEmpty(uri)) {
                        requestParams.put(key, new ByteArrayInputStream(ImageUtil.getImgByte(Uri.parse(uri), ApplicationEx.getInstance())));
                    }

                } catch (IOException e) {
                    LogUtil.print("uploadFile not found", e);
                }

            }
        }

        businessRequest.setResponseHandler(serviceResultCallback);
        businessRequest.execute();

    }


    /**
     * 执行 Get 请求，并添加安全认证参数到请求中。
     *
     * @param url
     * @param parameter
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void getRequest(String url, List<NameValuePair> parameter, ServiceResultCallback serviceResultCallback)// throws ParseException, IOException, BaseException
    {

        parameter = addVercode(parameter);
        filterUrlAndAppendMac(url, parameter);

        sendRequest(url, parameter, HttpRequest.RequestMethod.GET, serviceResultCallback);

    }


    /**
     * 执行 Post 请求，并添加安全认证参数到请求中。
     *
     * @param url
     * @param parameter
     * @return
     */
    public void postRequest(String url, List<NameValuePair> parameter, ServiceResultCallback serviceResultCallback) {
        parameter = addVercode(parameter);
        filterUrlAndAppendMac(url, parameter);
        sendRequest(url, parameter, HttpRequest.RequestMethod.POST, serviceResultCallback);

    }

    /**
     * 执行 Post 请求，并添加安全认证参数到请求中。
     *
     * @param url
     * @param parameter
     * @param timeout
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void postRequest(String url, List<NameValuePair> parameter, int timeout, ServiceResultCallback serviceResultCallback) {
        parameter = addVercode(parameter);
        filterUrlAndAppendMac(url, parameter);
        sendRequest(url, parameter, HttpRequest.RequestMethod.POST, serviceResultCallback);
    }

    /**
     * 执行 Post 请求，并添加安全认证参数到请求中。
     *
     * @param url
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void postRequest(String url, Map<String, String> pics, List<NameValuePair> parameter, ServiceResultCallback serviceResultCallback) {

        //由于服务端 MutilPartPost 方法不能从body体里取出数据，所以将vercode放在url上。
        parameter = addVercode(parameter);
        filterUrlAndAppendMac(url, parameter);
        sendRequest(url, parameter, pics, HttpRequest.RequestMethod.POST, serviceResultCallback);

    }


    /**
     * 执行 Delete 请求，并添加安全认证参数到请求中。
     *
     * @param url
     * @param parameter
     * @return
     * @throws BaseException
     * @throws IOException
     * @throws ParseException
     */
    public void deleteRequest(String url, List<NameValuePair> parameter, ServiceResultCallback serviceResultCallback)// throws ParseException, IOException, BaseException
    {
        parameter = addVercode(parameter);
        filterUrlAndAppendMac(url, parameter);
        sendRequest(url, parameter, HttpRequest.RequestMethod.DELETE, serviceResultCallback);
    }

    /**
     * 验证userToken是否已赋值，如果没有则抛出 BaseException 异常
     *
     * @throws BaseException
     */
    public void UserTokenHavingIsValid() //throws BaseException
    {
//		if ("" == null || "" =="")
//		{
//			BaseException e = new BaseException("Have not logged in.",ExceptionCode.HaveNotLoggedIn,0);
//			throw e;
//		}
    }

    /**
     * 创建http请求参数对，如果参数 addUsertToken 为真则向参数对中添加userToken,termid
     *
     * @param addUsertToken
     * @return
     * @throws BaseException
     */
    public List<NameValuePair> createNameValuePair(boolean addUsertToken) //throws BaseException
    {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        if (addUsertToken) {
            String pasmNO = "";
            if (Util.isEmpty(ApplicationEx.getInstance().getSession().getCurrentKSN())) {//如果刷卡器串号为空，使用钱包id
                pasmNO = ApplicationEx.getInstance().getUser().getTerminalId();
            } else {//刷卡器串号不为空，则使用刷卡器串号
                pasmNO = ApplicationEx.getInstance().getSession().getCurrentKSN();
            }
            //如果不为空，则添加termid，否则后期url编码时报空指针异常（德阳银行用户过来后没有虚拟终端号）
            if (!Util.isEmpty(pasmNO)) {
                nameValuePairs.add(new BasicNameValuePair("termid", pasmNO));
            }
        }

        return nameValuePairs;
    }

    /**
     * 向http参数列表中添加全局参数，该方法添加的参数如下:<br>
     * ver<br>
     * uid<br>
     * vercode<br>
     * debug<br>
     * userToken<br>
     * mac<br>
     *
     * @param parameter           参数列表
     * @param isAddLoginParameter 是否添加登录参数， 若用户未登录则此参数无效。
     * @param isAddMac            是否添加 MAC 参数
     */
    public void fillGlobalTransactionParameter(
            List<NameValuePair> parameter,
            boolean isAddLoginParameter,
            boolean isAddMac) {

        parameter = addVercode(parameter);

        if (isAddLoginParameter && !Util.isEmpty("")) {
//			parameter.add(new BasicNameValuePair("userToken", ""));
        }

        if (isAddMac) {
//			MAC.mac(parameter);
        }
    }

    /**
     * 向参数列表中添加安全认证参数
     *
     * @param parameter
     * @return
     */
    private List<NameValuePair> addVercode(List<NameValuePair> parameter) {


        return parameter;
    }

    private String generateUID(int length) {
        long t = System.nanoTime() ^ System.currentTimeMillis();
        Random random = new Random(t);
        char[] uid = new char[length];

        for (int i = 0; i < uid.length; i++) {
            uid[i] = charTable[random.nextInt(charTable.length)];
        }

        return String.valueOf(uid);
    }


    /**
     * 根据url，过滤是否需要进行mac计算
     * 20130609赵鲜阳修改
     * 激活钱包时，设置支付密码使用两层加密，由于此接口（activeEWallet）不算mac，
     * 后端认为是老的接口，只有一层加密，解不开，所以这里给这个接口加上mac计算
     *
     * @param url
     * @return
     */
    private boolean shouldUseMac(String url) {
        return (url.contains("queryTrans")
                || url.contains("commitTransaction")
                || url.contains("queryRemitTrans")
                || url.contains("commitRemitTransaction")
                || url.contains("updateSZBItem")
                || url.contains("activeEWallet")
                || url.contains("resetPayPwd")
                || url.contains("commitOrderTransaction")
                || url.contains("payPwdSwitch")
                || url.contains("cTransaction"));

    }

    /**
     * 过滤url，并且添加mac字段
     * 由于putRequest方法中的addVercode，并没有使用parameter参数，所以updateSZBItem的mac计算就漏掉了
     * 此处将添加mac的逻辑单独封装一个方法，和addVercode独立开来，并且在每个addVercode方法之后使用此方法
     *
     * @param url
     * @param parameter
     */
    private void filterUrlAndAppendMac(String url, List<NameValuePair> parameter) {

    }

}
