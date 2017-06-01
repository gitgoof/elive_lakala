package com.lakala.platform.swiper.devicemanager.connection.devicevalidate;

import android.content.Context;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.TerminalKey;
import com.newland.mtype.module.common.security.GetDeviceInfo;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by More on 15/1/15.
 */
public class DeviceOnlineValidate {

    protected Context context;

    protected  DeviceOnlineValidateListener deviceOnlineValidateListener;

    public DeviceOnlineValidate(Context context, DeviceOnlineValidateListener deviceOnlineValidateListener) {

        this.context = context;
        this.deviceOnlineValidateListener = deviceOnlineValidateListener;

    }

    public DeviceOnlineValidate() {

    }

    public void startValidate(final String ksn, GetDeviceInfo temp){

        BusinessRequest businessRequest = BusinessRequest.obtainRequest( "v1.0/terminal/user/" + ksn, HttpRequest.RequestMethod.GET);
        businessRequest.getRequestParams().put("psamNo", ksn);
        if(temp != null){

            businessRequest.getRequestParams().put("mver", temp.getFirmwareVersion());
            businessRequest.getRequestParams().put("aver", temp.getAppVersion());
            businessRequest.getRequestParams().put("pver", temp.getCommandVersion());
        }
        ServiceResultCallback resultDataResponseHandler = new ServiceResultCallback() {

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

                onValidateEvent(DeviceValidateEvent.Error);
            }

            @Override
            public void onSuccess(ResultServices resultServices) {

                if("000000".equals(resultServices.retCode)){

                    try{

                        JSONObject data = new JSONObject(resultServices.retData);
                        int status = data.optInt("status",4);
//                        0未绑定，1当前用户绑定，2已绑定其他用户，3其他状态
                        switch (status){
                            case 0:
                                onValidateEvent(DeviceValidateEvent.Disable);
                                break;
                            case 1:
                                JSONArray tpks = data.optJSONArray("TPKs");
                                JSONArray piks = data.optJSONArray("PIKs");
                                JSONArray macs = data.optJSONArray("MACs");
                                TerminalKey.setTpkMap(tpks);
                                TerminalKey.setPikMap(piks);
                                TerminalKey.setMacMap(macs);
                                TerminalKey.setLineNo(ksn, data.optString("lineNo"));
                                onValidateEvent(DeviceValidateEvent.Enable);
                                break;
                            case 2:
                                onValidateEvent(DeviceValidateEvent.User_Conflict);
                                break;
                            default:
                                onValidateEvent(DeviceValidateEvent.Unusable);
                                break;
                        }

                        return;
                    }catch (JSONException e){
                        LogUtil.print(e);
                        onValidateEvent(DeviceValidateEvent.Error);
                    }

                }else{
                    onValidateEvent(DeviceValidateEvent.Error);
                }

            }
        };

        businessRequest.setResponseHandler(resultDataResponseHandler);

        businessRequest.execute();

    }

    private void  onValidateEvent(final DeviceValidateEvent  deviceValidateEvent){
        if(deviceOnlineValidateListener != null)
            deviceOnlineValidateListener.onEvent(deviceValidateEvent);
    }

    public DeviceOnlineValidateListener getDeviceOnlineValidateListener() {
        return deviceOnlineValidateListener;
    }

    public void setDeviceOnlineValidateListener(DeviceOnlineValidateListener deviceOnlineValidateListener) {
        this.deviceOnlineValidateListener = deviceOnlineValidateListener;
    }
}
