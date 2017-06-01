package com.lakala.shoudan.common.net.volley;

import android.os.Build;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.shoudan.common.util.AppUtil;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by More on 15/8/28.
 */
public class BaseVolleyJsonRequest extends JsonObjectRequest {

//    字段	字段名	必填	类型	说明
//    _SubChannelId	子渠道代码	M	ans	100027
//    _Platform	平台类型	C	ans	android
//            iOS
//    _DeviceModel	厂商及型号	C	ans
//    _OSVersion	手机操作系统版本	C	ans
//    _DeviceId	手机唯一标识手机唯一标识	C	ans
//    _AppVersion 客户端版本
//    C ans
//    _Guid 客户端guid
//    M ans
//    跟踪号
//    _AccessToken 验证Token
//    M ans
//    __TimeStamp	时间戳	M ans
//    __IsSimulator 是否是模拟器
//    C	ans	0：否
//    1：是
//    __PhoneType 手机型号
//    C ans

//    requestParams.put("_Platform", "android");
//    requestParams.put("_TimeStamp", new Date().getTime() + "");
//    requestParams.put("_GesturePwd", ApplicationEx.getInstance().getUser().isExistGesturePassword() ? "1" : "0");//0没有 1有
//    String deviceId = DeviceUtil.getDeviceId(context);
//    Calendar calendar = Calendar.getInstance();
//
//    //deviceid 年月日时分秒 5位随机数
//    String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
//            deviceId,
//            calendar,
//            calendar,
//            calendar,
//            calendar,
//            calendar,
//            calendar,
//            StringUtil.getRandom(5)
//    );
//    String md5Value = Digest.md5(guid);
//    requestParams.put("_Guid", md5Value);
//    requestParams.put("_DeviceId",deviceId);
//    requestParams.put("_DeviceModel", Build.MODEL);
//    requestParams.put("_SubChannelId", "10000025");

    private static final Map<String, String> headers = new HashMap<String, String>();

    static{
        headers.put("Platform", "android");
        headers.put("AppVersion", AppUtil.getAppVersionCode(ApplicationEx.getInstance()));
        headers.put("SubChannelId", "10000027");
        headers.put("BundleVersion", "");

//        headers.put("_TimeStamp",new Date().getTime() + "");
//        headers.put("_GesturePwd", "0");
//        headers.put("_Guid", "");
//        headers.put("_DeviceId", AppUtil.createGuid());
//        headers.put("_DeviceModel", Build.MODEL);

    }

    public BaseVolleyJsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public BaseVolleyJsonRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        return headers;

    }
}
