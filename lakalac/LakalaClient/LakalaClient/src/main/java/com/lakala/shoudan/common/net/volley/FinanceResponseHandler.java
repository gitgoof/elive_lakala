package com.lakala.shoudan.common.net.volley;


import android.content.Context;
import android.view.View;

import com.lakala.core.http.HttpResponseHandler;
import com.lakala.library.DebugConfig;
import com.lakala.library.exception.HttpException;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.bean.Session;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.dialog.AlertDialog;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by More on 15/12/8.
 */
public class FinanceResponseHandler extends HttpResponseHandler {

    private HttpResponseListener httpResponseListener;

    public FinanceResponseHandler(HttpResponseListener httpResponseListener) {
        this.httpResponseListener = httpResponseListener;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public static void toLoginDialog(String msg){
        final Context context = ApplicationEx.getInstance();
        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setMessage(msg);
        alertDialog.setButtons(new String[]{context.getString(com.lakala.platform.R.string.ui_certain)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                try {
                    dialog.dismiss();
                    LoginUtil.loginOut(context);

                } catch (Exception e) {
                    LogUtil.print(e);
                    CrashlyticsUtil.logException(e);
                }

            }
        });
        LogUtil.print("handler ", "Result data response handler");
        ErrorDialogActivity.startSelf(alertDialog, "toLoginDialog");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        super.onSuccess(statusCode, headers, responseBody);
        String jsonStr = new String(responseBody);
        LogUtil.print(jsonStr);
        try {
            JSONObject response = new JSONObject(jsonStr);

            ReturnHeader returnHeader = new ReturnHeader(
                    response.optString("_ReturnCode"), response.optString("_ReturnMsg")
            );

            if (FinanceRequestManager.TOKEN_OUT_OF_DATE.equals(returnHeader.getRetCode())) {
                toLoginDialog(ApplicationEx.getInstance().getString(com.lakala.platform.R.string.plat_login_state_error));
                return;
            }else if(FinanceRequestManager.TOKEN_OUT_TO_REFRESH.equals(returnHeader.getRetCode())){
                refreshToken();
                return;
            }

            httpResponseListener.onFinished(returnHeader, response.optJSONObject("_ReturnData"));

        } catch (JSONException e) {
            LogUtil.print(e);
        }

    }
    /**
     * 刷新令牌  令牌调用
     */
    public static void refreshToken(){

        final Session session = ApplicationEx.getInstance().getSession();
        if(session.isUserLogin()){

            String refreshToken = session.getUser().getRefreshToken();

            LoginRequestFactory.createRefreshTokenRequest(refreshToken, new ResultDataResponseHandler(new ServiceResultCallback() {
                @Override
                public void onSuccess(ResultServices resultServices) {
                    try {

                        if (resultServices.isRetCodeSuccess()) {
                            session.getUser().updateUserToken(new JSONObject(resultServices.retData));
                        }

                    } catch (Exception e) {
                        LogUtil.print(e);
                    }
                }

                @Override
                public void onEvent(HttpConnectEvent connectEvent) {

                }
            }), null).execute();
        }

    }
    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        super.onFailure(statusCode, headers, responseBody, error);
        if(statusCode >=500 && statusCode < 600){
            //500 服务端错误处理
            toLoginDialog(ApplicationEx.getInstance().getString(com.lakala.platform.R.string.plat_http_server_error));
            return;
        }else{

            switch (statusCode){
                case HttpException.ERRCODE_REQUEST_CANNOT_CONNECT_TO_HOST:
                case HttpException.ERRCODE_REQUEST_CERTIFICATE_INVAILD:
                    //未发送数据
                    showToast(com.lakala.platform.R.string.network_fail);
                    break;
                case HttpException.ERRCODE_REQUEST_RESPONSE_ERROR:
                    showToast( com.lakala.platform.R.string.plat_http_004);
                    break;
                case HttpException.ERRCODE_REQUEST_TIMEOUT:
                case HttpException.ERRCODE_REQUEST_UNKNOWN_ERROR:
                    showToast(com.lakala.platform.R.string.network_fail);
                    break;
                case 401:
                    //重新登录
                    toLoginDialog(ApplicationEx.getInstance().getString(com.lakala.platform.R.string.plat_login_state_error));
                    return;
                case 403:
                    refreshToken();
                    break;
                case 404://服务器不响应
                    showToast(com.lakala.platform.R.string.plat_http_004 );
                    break;
                default:
                    showToast(com.lakala.platform.R.string.network_fail);
                    break;
            }

        }
        httpResponseListener.onErrorResponse();
        LogUtil.print("statues code = " + statusCode );
    }

    private void showToast(int msg){
        ToastUtil.toast(ApplicationEx.getInstance(), ApplicationEx.getInstance().getString(msg));
    }

}
