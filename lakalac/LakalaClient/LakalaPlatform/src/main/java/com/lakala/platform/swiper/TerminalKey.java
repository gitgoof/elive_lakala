package com.lakala.platform.swiper;

import android.text.TextUtils;

import com.lakala.core.http.HttpRequest;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.R;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 终端密钥类
 * 此类存储并管理和终端相关的主密钥、工作密钥、MAC密钥
 *
 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 *  字段说明（供N久后代码复查）：由于很早历史原因，服务端返回秘钥字段命名错乱，特此注释一下
 *  PinKey： 工作秘钥
 *  WorkKey： 主密钥
 *  MacKey： mac秘钥
 *                                  14.9.10
 *  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 * @author xyz
 */
public class TerminalKey {

    private static Map<String, String> tpkMap;    //主密钥数组，命名沿用服务端返回参数

    private static Map<String, String> pikMap;    //工作密钥数组，命名沿用服务端返回参数

    private static Map<String, String> macMap;    //mac密钥数组，命名沿用服务端返回参数

    private static String telecode;//线路号

    private static Map<String, String> lineNoMap = new HashMap<>();

    public static String getLineNo(String terminalId){
        return lineNoMap.get(terminalId);
    }

    public static void setLineNo(String terminalId, String lineNo){
        lineNoMap.put(terminalId, lineNo);
    }

    /**
     * 通过终端号，获取对应的主密钥
     *
     * @param termid
     * @return
     */
    public static String getMasterKey(String termid) {
        if (tpkMap == null || tpkMap.size()==0){
            fillDefault();
        }
        return tpkMap == null ? "" : tpkMap.get(termid);
    }

    /**
     * 根据终端号获取对应的工作密钥
     *
     * @param termid
     * @return
     */
    public static String getWorkKey(String termid) {
        if (pikMap == null || pikMap.size()==0){
            fillDefault();
        }
        return pikMap == null ? "" : pikMap.get(termid);
    }

    /**
     * 根据终端号，获取对应的mac密钥
     *
     * @param termid
     * @return
     */
    public static String getMacKey(String termid) {
        if (macMap == null || macMap.size()==0){
            fillDefault();
        }
        return macMap == null ? "" : macMap.get(termid);
    }

    /**
     *初始化登录下发的密钥对。（主要为了防止二次登录后，初始秘钥对为空的情况）
     */
    private static void fillDefault(){
        User user = ApplicationEx.getInstance().getUser();
        setMac(user.getMtsTerminalId(), user.getMtsMacKey());//保存mac密钥
        setPik(user.getMtsTerminalId(), user.getMtsPinKey());//保存工作密钥
        setTpk(user.getMtsTerminalId(), user.getMtsWorkKey());//保存主密钥
    }
    /**
     * 判断termid对应的密钥是否存在
     *
     * @param termid
     * @return
     */
    public static boolean hasKey(String termid) {
        //终端签到获得到工作密钥，所以此处判断是否有termid对应的工作密钥
        return (pikMap != null && pikMap.containsKey(termid));
    }

    /**
     * 获取密钥数组
     *
     * @param keyMap    需要设置密钥的map
     * @param jsonArray 服务端返回包含密钥数组的jsonArray
     */
    private static void obtainKeys(Map<String, String> keyMap, JSONArray jsonArray) {
        for (int index = 0; index < jsonArray.length(); index++) {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(index);
                Iterator<String> iterator = jsonObject.keys();
                String key = iterator.next();
                keyMap.put(key, (String) jsonObject.get(key));
            } catch (JSONException e) {
            }
        }
    }

    public static void setTpkMap(JSONArray jsonArray) {
        if (tpkMap == null) {
            tpkMap = new HashMap<String, String>();
        }
        obtainKeys(tpkMap, jsonArray);

    }

    public static void setPikMap(JSONArray jsonArray) {
        if (pikMap == null) {
            pikMap = new HashMap<String, String>();
        }
        obtainKeys(pikMap, jsonArray);
    }

    public static void setMacMap(JSONArray jsonArray) {
        if (macMap == null) {
            macMap = new HashMap<String, String>();
        }
        obtainKeys(macMap, jsonArray);
    }

    /**
     * 保存主密钥
     */
    public static void setTpk(String id, String key) {
        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(key)) return;
        if (tpkMap == null) tpkMap = new HashMap<String, String>();
        tpkMap.put(id, key);
    }

    /**
     * 保存工作密钥
     */
    public static void setPik(String id, String key) {
        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(key)) return;
        if (pikMap == null) pikMap = new HashMap<String, String>();
        pikMap.put(id, key);
    }

    /**
     * 保存mac密钥
     */
    public static void setMac(String id, String key) {
        if (StringUtil.isEmpty(id) || StringUtil.isEmpty(key)) return;
        if (macMap == null) macMap = new HashMap<String, String>();
        macMap.put(id, key);
    }

    /**
     * 清空密钥<br/>
     * 由于第二次终端签到的时候，服务端上次的密钥全部失效，所以需要清理
     */
    public static void clear() {
        if (tpkMap != null && !tpkMap.isEmpty()) {
            tpkMap.clear();
        }

        if (pikMap != null && !pikMap.isEmpty()) {
            pikMap.clear();
        }

        if (macMap != null && !macMap.isEmpty()) {
            macMap.clear();
        }

        if(lineNoMap !=null & !lineNoMap.isEmpty()){
            lineNoMap.clear();
        }
    }

    /**
     * 如果登录后又在4.4登录，会导致终端签到的密钥失效，服务端做了调整，会自动再做一次签到
     * 此方法拦截报文，重新设置签到后获得的key
     */
    public static void resetTerminalSignKeys(Object response){

        String workKey = "";
        String macKey = "";
        String pinKey = "";
        String terminalId = "";
        try {
            //如果是B端发的请求会返回原始报文，需要重新解析
            if(response instanceof String){
                JSONObject responseObj = new JSONObject((String)response);
                String resultCode = responseObj.optString("_ReturnCode");
                JSONObject object = responseObj.optJSONObject("_ReturnData");
                if (!resultCode.equals("TS0000") || object == null) {
                    return;
                }
                workKey    = object.optString("WorkKey","");
                macKey     = object.optString("MacKey","");
                pinKey     = object.optString("PinKey","");
                terminalId = object.optString("TerminalId","");
            }else if(response instanceof JSONObject){
                JSONObject object = (JSONObject) response;
                workKey    = object.optString("WorkKey","");
                macKey     = object.optString("MacKey","");
                pinKey     = object.optString("PinKey","");
                terminalId = object.optString("TerminalId","");
            }
        } catch (JSONException e) {
            CrashlyticsUtil.logException(e);
        }

        if (StringUtil.isNotEmpty(workKey) &&
                StringUtil.isNotEmpty(macKey) &&
                StringUtil.isNotEmpty(pinKey)){
            User user = ApplicationEx.getInstance().getUser();
            //当重新下发秘钥对是虚拟终端秘钥对时，进行更新
            if (terminalId.equals(user.getTerminalId())){
//                user.setWorkKey(workKey);
//                user.setMacKey(macKey);
//                user.setPinKey(pinKey);  ???
                user.save();

                setMac(terminalId,macKey);//保存mac密钥
                setPik(terminalId,pinKey );//保存工作密钥
                setTpk(terminalId,workKey);//保存主密钥
            }
        }
    }


    public interface  VirtualTerminalSignUpListener{

        /**
         * 签到开始
         */
        void onStart();

        /**
         * 虚拟终端签到失败
         * @param msg 失败原因
         */
        void onError(String msg);

        /**
         * 虚拟终端签到成功
         */
        void onSuccess();

    }

    public static boolean hadVirtualSigned(){
        String terminalId = ApplicationEx.getInstance().getUser().getTerminalId();
        return !TextUtils.isEmpty(terminalId) && !TextUtils.isEmpty(getMacKey(terminalId));
    }

    /**
     * 进行虚拟终端签到
     * @param virtualTerminalSignUpListener
     */
    public static void virtualTerminalSignUp(final VirtualTerminalSignUpListener virtualTerminalSignUpListener){
        //用户登录下发的虚拟终端号
        String terminalId = ApplicationEx.getInstance().getUser().getTerminalId();

        if(!TextUtils.isEmpty(terminalId) && !TextUtils.isEmpty(getMacKey(terminalId))){

            virtualTerminalSignUpListener.onStart();
            virtualTerminalSignUpListener.onSuccess();
            return;
        }

        BusinessRequest businessRequest = BusinessRequest.obtainRequest("v1.0/terminal/user/vt", HttpRequest.RequestMethod.GET);
        businessRequest.setResponseHandler(
                new ResultDataResponseHandler(new ServiceResultCallback() {
                    @Override
                    public void onSuccess(final ResultServices resultServices) {

                        try{
                            if(BusinessRequest.SUCCESS_CODE.equals(resultServices.retCode)){

                                JSONObject data = new JSONObject(resultServices.retData);
                                String ksn = data.optString("psamNo");
                                ApplicationEx.getInstance().getUser().setTerminalId(ksn);
                                setPikMap(data.optJSONArray("PIKs"));
                                setMacMap(data.optJSONArray("MACs"));
                                setTpkMap(data.optJSONArray("TPKs"));
                                setTelecode(data.optString("lineNo"));

                                if(virtualTerminalSignUpListener !=  null)
                                    virtualTerminalSignUpListener.onSuccess();


                            }else{
                                if(virtualTerminalSignUpListener !=  null)
                                    virtualTerminalSignUpListener.onError(resultServices.retMsg);
                            }


                        }catch (Exception e){
                            if(virtualTerminalSignUpListener !=  null){
                                virtualTerminalSignUpListener.onError(ApplicationEx.getInstance().getString(R.string.plat_http_error));
                            }
                        }

                    }

                    @Override
                    public void onEvent(HttpConnectEvent connectEvent) {
                        if(virtualTerminalSignUpListener !=  null)
                            virtualTerminalSignUpListener.onError(ApplicationEx.getInstance().getString(R.string.plat_http_error));
                    }
                }) {

                    @Override
                    public void onStart() {
                        super.onStart();
                        if(virtualTerminalSignUpListener !=  null)
                            virtualTerminalSignUpListener.onStart();
                    }

                }


        );
        businessRequest.execute();


    }

    public static String getTelecode() {
        return telecode;
    }

    public static void setTelecode(String telecode) {
        TerminalKey.telecode = telecode;
    }
}
