package com.lakala.platform.response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.exception.HttpException;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.bean.Session;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.ui.dialog.AlertDialog;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by ZhangMY on 2015/2/4.
 */
public class ResultDataResponseHandler extends HttpResponseHandler {

    private boolean audioToast = true;

    private ServiceResultCallback serviceResultCallback;
    private static final String TAG = ResultDataResponseHandler.class.getName();

    public ResultDataResponseHandler(ServiceResultCallback serviceResultCallback) {
        this.serviceResultCallback = serviceResultCallback;
    }

    public ServiceResultCallback getServiceResultCallback() {
        return serviceResultCallback;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        super.onSuccess(statusCode, headers, responseBody);
        ResultServices result = new ResultServices();
//        000103	Token失效	刷新就可以了
//        000104	RefreshToken失效	重新登录

        try {
            LogUtil.print(TAG, "fullUrl:" + getRequestURI());
            LogUtil.print(TAG, "statusCode:" + String.valueOf(statusCode));
            LogUtil.print(TAG, "responseString:" + getResponseString(responseBody, getCharset()));
            JSONObject jsonObject = new JSONObject(getResponseString(responseBody, getCharset()));
            result.retCode = jsonObject.optString("retCode");
            result.retMsg = jsonObject.optString("retMsg");
            result.retData = jsonObject.optString("retData");


            if (BusinessRequest.REFRESH_TOKEN_ERROR.equals(result.retCode)) {
                refreshToken(null);
            } else if (BusinessRequest.TOKEN_ERROR.equals(result.retCode)) {
                toLoginDialog("您的登录状态异常，请重新登录");
                return;
            }
            if (serviceResultCallback != null) {
                serviceResultCallback.onSuccess(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (serviceResultCallback != null) {
                serviceResultCallback.onEvent(HttpConnectEvent.RESPONSE_ERROR);
            }
        }
    }

    @Override
    public void onFailure(int s, Header[] headers, byte[] responseBody, Throwable error) {
        super.onFailure(s, headers, responseBody, error);
        HttpConnectEvent connectEvent = HttpConnectEvent.EXCEPTION;

        if (LogUtil.DEBUG) {
            LogUtil.print(getClass().getName(), "Request url\n" + getRequestURI() +
                    "\n status code = " + statusCode);
        }

        if (statusCode >= 500 && statusCode < 600) {
            //500 服务端错误处理

            toLoginDialog(ApplicationEx.getInstance().getString(R.string.plat_http_server_error));
            connectEvent = HttpConnectEvent.RESPONSE_ERROR;
            return;
        } else {

            switch (statusCode) {
                case HttpException.ERRCODE_REQUEST_CANNOT_CONNECT_TO_HOST:
                case HttpException.ERRCODE_REQUEST_CERTIFICATE_INVAILD:
                    //未发送数据
                    connectEvent = HttpConnectEvent.ERROR;
                    showToast(R.string.network_fail);
                    break;
                case HttpException.ERRCODE_REQUEST_RESPONSE_ERROR:
                    connectEvent = HttpConnectEvent.RESPONSE_ERROR;
                    showToast(R.string.plat_http_004);
                    break;
                case HttpException.ERRCODE_REQUEST_TIMEOUT:
                case HttpException.ERRCODE_REQUEST_UNKNOWN_ERROR:
                    showToast(R.string.network_fail);
                    connectEvent = HttpConnectEvent.EXCEPTION;
                    break;
                case 401:
                    //重新登录
                    toLoginDialog(ApplicationEx.getInstance().getString(R.string.plat_login_state_error));
                    connectEvent = HttpConnectEvent.RESPONSE_ERROR;
                    return;
                case 403:
                    refreshToken(serviceResultCallback);
                    connectEvent = HttpConnectEvent.RESPONSE_ERROR;
                    return;
                case 404://服务器不响应
                    showToast(R.string.plat_http_004);
                    connectEvent = HttpConnectEvent.RESPONSE_ERROR;
                    break;
                default:
                    showToast(R.string.network_fail);
                    connectEvent = HttpConnectEvent.EXCEPTION;
                    break;
            }

        }
        if (serviceResultCallback != null) {
            serviceResultCallback.onEvent(connectEvent);
        }

    }

    private void showToast(int msg) {

        if (audioToast)
            ToastUtil.toast(ApplicationEx.getInstance(), ApplicationEx.getInstance().getString(msg));

    }

    public boolean isAudioToast() {
        return audioToast;
    }

    public ResultDataResponseHandler setAudioToast(boolean audioToast) {
        this.audioToast = audioToast;
        return this;
    }

    public static void toLoginDialog(String msg){

        final Context context = ApplicationEx.getInstance();
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setMessage(msg);
        alertDialog.setButtons(new String[]{context.getString(R.string.ui_certain)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                try {
                    dialog.dismiss();
                    ApplicationEx.getInstance().loginOut();
                    Activity activity = BusinessLauncher.getInstance().getTopActivity();
                    if(activity == null){

                        Class loginClass = Class.forName("com.lakala.shoudan.activity.login.LoginActivity");
                        Intent intent = new Intent(context, loginClass);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }else{

                        Class loginClass = Class.forName("com.lakala.shoudan.activity.login.LoginActivity");
                        Intent intent = new Intent(activity, loginClass);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                } catch (Exception e) {
                    LogUtil.print(e);
                    CrashlyticsUtil.logException(e);
                }

            }
        });
        LogUtil.print("handler ", "Result data response handler");

        ErrorDialogActivity.startSelf(alertDialog,"toLoginDialog");
    }

    /**
     * 刷新令牌  令牌调用
     */
    public static void refreshToken(final ServiceResultCallback serviceResultCallback) {

        Activity activity = BusinessLauncher.getInstance().getTopActivity();

        final Session session = ApplicationEx.getInstance().getSession();
        if(session.isUserLogin()){

            String refreshToken = session.getUser().getRefreshToken();

            LoginRequestFactory.createRefreshTokenRequest(refreshToken, new ResultDataResponseHandler(new ServiceResultCallback() {
                @Override
                public void onSuccess(ResultServices resultServices) {
                    try {

                        if (resultServices.isRetCodeSuccess()) {
                            session.getUser().updateUserToken(new JSONObject(resultServices.retData));
                            serviceResultCallback.onEvent(HttpConnectEvent.RESPONSE_ERROR);
                        }

                    } catch (Exception e) {
                        LogUtil.print(e);
                    }
                }

                @Override
                public void onEvent(HttpConnectEvent connectEvent) {


                }
            }), activity == null ? null : (activity instanceof FragmentActivity ? (FragmentActivity) activity : null)).execute();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }
}
