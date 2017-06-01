package com.lakala.shoudan.loginservice;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.lakala.library.DebugConfig;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.activity.ErrorDialogActivity;
import com.lakala.platform.bean.Session;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.AppUpgradeController;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.TimeCounter;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.platform.dao.UserDao;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.login.LoginActivity;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.ui.dialog.AlertDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by More on 15/3/21.
 * <p/>
 * token刷新模块
 * 已登录 -> 刷新token -> 获取商户开通状态|获取商户信息  ->未开通去商户开通页(如果需要手势登录 去登录手势密码) -> 进入登录 (流程中断前往登录页面)
 * 未登录-> 去登录页
 */
public class TokenRefreshService extends Service implements LoginInt {


    private TokenRefreshCallback tokenRefreshCallback;

    private static TokenRefreshService tokenRefreshService;

    private MyBinder myBinder = new MyBinder();

    private boolean isLoginByPwd = false;
    private AlertDialog alertDialog;

    public boolean isLoginByPwd() {
        return isLoginByPwd;
    }

    public void setLoginByPwd(boolean isLoginByPwd) {
        this.isLoginByPwd = isLoginByPwd;
    }

    public class MyBinder extends Binder {

        public TokenRefreshService getService() {
            return TokenRefreshService.this;
        }
    }

    public TokenRefreshService() {

    }

    public static synchronized TokenRefreshService getInstance() {

        return tokenRefreshService;

    }

    private Timer tokenRefreshTimer = new Timer();

    @Override
    public void onCreate() {
        super.onCreate();
        tokenRefreshService = this;

    }

    private void initTokenRefreshTimer() {

        if (tokenRefreshTimer == null) {
            tokenRefreshTimer = new Timer();
        }

        try {

            if (!TextUtils.isEmpty(ApplicationEx.getInstance().getUser().getExpirein())) {
                long freshInterval = Long.parseLong(ApplicationEx.getInstance().getUser().getExpirein()) - 120;
                tokenRefreshTimer.schedule(new TokenRefreshTask(), freshInterval * 1000, freshInterval * 1000);
                ;
            }

        } catch (Exception e) {
            LogUtil.print(e);
        }

    }

    private class TokenRefreshTask extends TimerTask {
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    Session session = ApplicationEx.getInstance().getSession();
                    LogUtil.print("Time's up!" + session.isUserLogin());
                    if (session.isUserLogin()) {

                        start(true, ServiceType.REFRESH_TOKEN);

                    }

                }
            });
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    public void onDestroy() {
        super.onDestroy();
        tokenRefreshService = null;
        LogUtil.print(getClass().getName(), "Token service has destroyed");

    }

    @Override
    public IBinder onBind(Intent intent) {

        return myBinder;
    }

    private void onFinished() {

        if (tokenRefreshCallback != null) {
            tokenRefreshCallback.onFinish();
            tokenRefreshCallback = null;
        }

        TimeCounter.getInstance().saveTokenUpdateTime();

    }

    private void onInterrupted() {

        tokenRefreshTimer.cancel();
        if (tokenRefreshCallback != null) {
            tokenRefreshCallback.onInterrupted();
            tokenRefreshCallback = null;

        }
        LoginUtil.clearLoginSession();
    }

    public TokenRefreshCallback getTokenRefreshCallback() {
        return tokenRefreshCallback;
    }

    /**
     * 一次性,回调后自我赋值null
     *
     * @param tokenRefreshCallback
     */
    public void setTokenRefreshCallback(TokenRefreshCallback tokenRefreshCallback) {
        this.tokenRefreshCallback = tokenRefreshCallback;
    }

    public enum ServiceType {
        REFRESH_TOKEN,
        LOGIN,
    }

    private ServiceType serviceType = ServiceType.LOGIN;

    private void finishActivity() {
        if (loginActivity != null && !loginActivity.isFinishing()) {
            loginActivity.finish();
        }
    }

    /**
     * TokenRefreshService * token 刷新结束 进行登录
     */
    private void onUserChecked() {

        //定位
        LocationManager.getInstance().statLocating();
//        getUnreadMsgCount();

        if (serviceType == ServiceType.LOGIN) {

            ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Do_Home,ApplicationEx.getInstance().getActiveContext());
            //这个没有任何其他的 BusinessLauncher的应用被启动
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            initTokenRefreshTimer();
            finishActivity();

        }

        ApplicationEx.getInstance().getSession().setUserLogin(true);

        AppUpgradeController appUpgradeController = AppUpgradeController.getInstance();
        if(!appUpgradeController.isAppUpdate()){
            appUpgradeController.check(false, true);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (!audioJump) {
            return;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.startActivity(intent);
    }

    private boolean audioJump = true;

    public void setAudioJump(boolean audioJump) {
        this.audioJump = audioJump;
    }

    public boolean isAudioJump() {
        return audioJump;
    }

    private boolean isTokenValidate() {
        //距离上次刷新时间间隔
        return TimeCounter.getInstance().isTokenValidate();
    }

    private Activity loginActivity;

    /**
     * 用户开始刷新
     * 已登录用户 进行token刷新,商户信息获取等接口初始化
     */
    public void start(boolean hasLoginUser, ServiceType serviceType, Activity activity) {

        this.loginActivity = activity;

        //如果有上次登录用户信息，则取出登录用户信息
        this.serviceType = serviceType;
        if (hasLoginUser) {
            //
            final User user = UserDao.getInstance().getLoginUser();
            ApplicationEx.getInstance().getSession().setUser(user);
            isLoginByPwd = false;
            boolean isTokenValidate = isTokenValidate();
            LogUtil.print("isTokenValidate = " + isTokenValidate);
            if (isTokenValidate) {
                onUserChecked();
            } else {
                updateToken();
            }
        } else {
            //如果没有上次登录用户信息，则直接跳转到登录页面
            toLoginActivity();
            finishActivity();
        }

    }


    public void start(boolean hasLoginUser, ServiceType serviceType) {

        start(hasLoginUser, serviceType, null);

    }

    protected void updateToken() {

        final User user = UserDao.getInstance().getLoginUser();
        ApplicationEx.getInstance().getSession().setUser(user);
        String refreshToken = user.getRefreshToken();

        LoginRequestFactory.createRefreshTokenRequest(refreshToken, new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {

                if (resultServices.isRetCodeSuccess()) {

                    onTokenUpdateSucceed(resultServices);

                } else {
                    onTokenUpdateFailed();
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

                onTokenUpdateFailed();
            }
        }), null).execute();


    }

    private void toLoginActivity() {
        if (BusinessLauncher.getInstance().getActivities().size() != 0) {
            Activity lastActivity = (Activity) BusinessLauncher.getInstance().getActivities().getLast();
            if (lastActivity != null && (lastActivity instanceof LoginActivity)) {
                return;
            }
        }
        startActivity(new Intent(this, LoginActivity.class));
        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_4, this);
    }


    private void loginNeeded(String msg) {

        if (alertDialog == null) {
            alertDialog = new AlertDialog();
        }
        alertDialog.setMessage(msg);
        alertDialog.setButtons(new String[]{ApplicationEx.getInstance().getString(R.string.button_ok)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                dialog.dismiss();
                toLoginActivity();
            }
        });

        //Token RefreshService

        ErrorDialogActivity.startSelf(alertDialog);

    }


    public void getMerchantInfo() {

        LoginRequestFactory.createBusinessInfoRequest().setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                //商户信息请求成功
                if (resultServices.isRetCodeSuccess()) {

                    onMerchantInfoUpdateSucceed(resultServices);

                } else {
                    onMerchantInfoUpdateFailed();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                //商户信息获取失败, 需要重新登录
                onMerchantInfoUpdateFailed();
            }
        })).execute();
    }

    public interface getMerchantInfoLis{
        void onSuccess();
        void onEvent();
    }
    public void getMerchantInfo2(final getMerchantInfoLis lis){

        LoginRequestFactory.createBusinessInfoRequest().setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                //商户信息请求成功
                if(resultServices.isRetCodeSuccess()){

                    onMerchantInfoUpdateSucceed(resultServices);
                    lis.onSuccess();
                }else{
                    onMerchantInfoUpdateFailed();
                    lis.onEvent();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                //商户信息获取失败, 需要重新登录
                onMerchantInfoUpdateFailed();
                lis.onEvent();
            }
        })).execute();
    }

    public void checkMerchantInfo(ServiceType serviceType) {

        this.serviceType = serviceType;

        BusinessRequest businessRequest = LoginRequestFactory.createMerchantStatusRequest();
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                try {
                    if (ResponseCode.SuccessCode.equals(resultServices.retCode)) {

                        onMerchantStateChecked(true);
//                        "retData":[{"productCode":"1CE","productName":"资金归集","productIcon":"","productSubCode":"3I050000301","productSubName":"新疆","productSubIcon":null,"sort":1},
// {"productCode":"1F2","productName":"收款","productIcon":"","productSubCode":"3I060002601","productSubName":"平安收租宝","productSubIcon":null,"sort":1}]}

                        if (TextUtils.isEmpty(resultServices.retData)) {
                            return;
                        }

                        JSONArray jr = new JSONArray(resultServices.retData);

                        for (int i = 0; i < jr.length(); i++) {

                            String productSubCode = jr.getJSONObject(i).optString("productSubCode");

                            if ("3I060002601".equals(productSubCode)) {

                                ApplicationEx.getInstance().getUser().getAppConfig().setRentCollectionEnabled(true);

                            } else if ("3I050000301".equals(productSubCode)) {

                                ApplicationEx.getInstance().getUser().getAppConfig().setContributePaymentEnabled(true);

                            }

                        }

                    } else {
                        onMerchantStateCheckFailed();
                    }
                } catch (Exception e) {
                    LogUtil.print(e);
                    if (DebugConfig.DEBUG) {
                        throw new RuntimeException("Ridiculous Error");
                    }
                    onMerchantStateCheckFailed();
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                onMerchantStateCheckFailed();
            }
        })).execute();

    }

    @Override
    public void onTokenUpdateSucceed(ResultServices resultServices) {

        //Token刷新成功, 请求商户信息
        try {
            ApplicationEx.getInstance().getSession().getUser().updateUserToken(new JSONObject(resultServices.retData));
        } catch (Exception e) {
            LogUtil.print(e);
        }
        checkMerchantInfo(serviceType);

    }

    @Override
    public void onTokenUpdateFailed() {

        //Token 刷新异常需要重新登录
        loginNeeded("您的登录状态异常，请重新登录");
        onInterrupted();
    }

    @Override
    public void onMerchantStateChecked(boolean b) {
        getMerchantInfo();
    }

    @Override
    public void onMerchantStateCheckFailed() {
        onInterrupted();
        loginNeeded("网络连接异常，请重新登录");
    }

    @Override
    public void onMerchantInfoUpdateSucceed(ResultServices resultServices) {

        try {

            final User user = ApplicationEx.getInstance().getSession().getUser();
            JSONObject data = new JSONObject(resultServices.retData);
            user.initMerchantAttrWithJson(data);
            //登录成功保存登录信息
            ApplicationEx.getInstance().getUser().save();

        } catch (Exception e) {
            LogUtil.print(e);
        }

        onUserChecked();

        onFinished();

    }

    @Override
    public void onMerchantInfoUpdateFailed() {
        onInterrupted();
        loginNeeded("网络连接异常，请重新登录");

    }

    @Override
    public void onLoginFinished(ResultServices resultServices) {

    }

    @Override
    public void onLoginFailed() {

    }


}
