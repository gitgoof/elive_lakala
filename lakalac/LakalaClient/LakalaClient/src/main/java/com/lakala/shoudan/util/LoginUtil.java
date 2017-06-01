package com.lakala.shoudan.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.CrashlyticsUtil;
import com.lakala.platform.common.LKlPreferencesKey;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.common.TimeCounter;
import com.lakala.platform.dao.UserDao;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.LoginRequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResponseCode;
import com.lakala.platform.response.ResultDataResponseHandler;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.login.AudioLoginActivity;
import com.lakala.shoudan.activity.login.LoginActivity;
import com.lakala.shoudan.activity.login.LoginSuccessNeedBindDeviceActivity;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.shoudan.loginservice.TokenRefreshCallback;
import com.lakala.shoudan.loginservice.TokenRefreshService;
import com.treefinance.sdk.GFDAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * app重新登录逻辑处理
 *
 * Created by jerry on 14-3-21.
 */
public class LoginUtil {

    private LoginUtil() {
    }

    /**
     * 退出登录
     */
    public static void loginOut(Context context){
        LklPreferences.getInstance().putBoolean(LKlPreferencesKey.KEY_LOGIN_OUT,true);
        clearSession2Login(context);
    }

    /**
     * 退出登录并且发送请求
     */
    public static void loginOutWithRequest(Context context){
        //退出登录
//        CommonRequestFactory.logout().execute();
        LklPreferences.getInstance().putBoolean(LKlPreferencesKey.KEY_LOGIN_OUT,true);
        clearSession2Login(context);
    }

    /**
     * 重新登录
     */
    public static void restartLogin(Context context){
        clearSession2Login(context);
    }

    public static void clearLoginSession(){
        try {
            //最后一次登录标致为false
            UserDao.getInstance().setAllUserLoginFalse();
            //保存当前登录用户名
            String loginName = ApplicationEx.getInstance().getUser().getLoginName();
            LklPreferences.getInstance().putString(LKlPreferencesKey.KEY_LOGIN_NAME, loginName);
        }catch (Exception e){
            CrashlyticsUtil.logException(e);
        }
        GFDAgent.getInstance().logout();//退出功夫贷
        //清除用户信息
        FinanceRequestManager.getInstance().clear();
        ApplicationEx.getInstance().getSession().clear();
    }

    /**
     * 清空session信息
     */
    public static void clearSession2Login(Context context) {
        clearLoginSession();
        //重新登录
        Intent intent = new Intent(context,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);

        if(context != null && context instanceof Activity  && !((Activity)context).isFinishing()){

            ((Activity)context).finish();

        }
    }

    /**
     * 用户信息是否失效
     */
    public static boolean userInvaild(String tradeCode){
        if (tradeCode.equals("A00045")//用户未登录
                || tradeCode.equals("A00046")//用户未注册(用户登陆在不同系统上相互踢人时的监听，表示不在监听列表内）
                || tradeCode.equals("A00047")//用户在远程登录
                || tradeCode.equals("A00048")//用户已签退
                || tradeCode.equals("F80003")//Token过期
                || tradeCode.equals("A00045")//用户未登录
                || tradeCode.equals("TS0003")//token校验失败
                || tradeCode.equals("R40000")//Mac校验失败
                || tradeCode.equals("C40025")//授信设备是否被其他设备删除
                || tradeCode.equals("A00069"))//Mac校验失败
        {
            return true;
        }
        return false;
    }

    public static void checkLoginOut(String code, Context context){
        if (userInvaild(code)){
            restartLogin(context);
        }
    }

    /**
     * 用户信息是否失效，弹框提示
     *
     * @param
     * @return
     */
//    public static boolean userInvalidConfirm(String tradeCode, final String msg, final FragmentActivity activity){
//        //用户在远程登录
//        if(tradeCode.equals("A00047")){
//            DialogController.getInstance().showAlertDialog(
//                    activity,
//                    "提示",
//                    StringUtil.isEmpty(msg) ? "您的账户已在另一设备登录，如需继续使用请重新登录；如果这不是您认可的操作，请注意账户安全，建议您修改登录密码。"
//                            : msg,
//                    new AlertDialog.Builder.AlertDialogClickListener() {
//                        private boolean isClick = false;
//
//                        @Override
//                        public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                            isClick = true;
//                            DialogController.getInstance().setBlocked(false);
//                            LoginUtil.loginOut(activity);
//                        }
//
//                        @Override
//                        public void onDestroy() {
//                            if(isClick){
//                                return;
//                            }
//                            LoginUtil.loginOut(activity);
//                            DialogController.getInstance().setBlocked(false);
//                        }
//                    });
//            DialogController.getInstance().setBlocked(true);
//            return true;
//        }
//        //授信设备是否被其他设备删除
//        if(tradeCode.equals("C40025")){
//            DialogController.getInstance().showAlertDialog(
//                    activity,
//                    "提示",
//                    StringUtil.isEmpty(msg) ? "您的登录设备授权已被另一设备删除，如果这不是您认可的操作，请注意账号安全，请重新登录并建议您修改登录密码。"
//                            : msg,
//                    new AlertDialog.Builder.AlertDialogClickListener() {
//                        private boolean isClick = false;
//
//                        @Override
//                        public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                            isClick = true;
//                            DialogController.getInstance().setBlocked(false);
//                            LoginUtil.loginOut(activity);
//                        }
//                        @Override
//                        public void onDestroy() {
//                            if(isClick){
//                                return;
//                            }
//                            LoginUtil.loginOut(activity);
//                            DialogController.getInstance().setBlocked(false);
//                        }
//                    });
//            DialogController.getInstance().setBlocked(true);
//            return true;
//        }
//        //Mac校验失败
//        if(tradeCode.equals("A00069")){
//            DialogController.getInstance().showAlertDialog(
//                    activity,
//                    "提示",
//                    StringUtil.isEmpty(msg) ? "系统繁忙，请稍候重新登录再试。"
//                            : msg,
//                    new AlertDialog.Builder.AlertDialogClickListener() {
//                        private boolean isClick = false;
//
//                        @Override
//                        public void clickCallBack(AlertDialog.Builder.ButtonTypeEnum typeEnum, AlertDialog alertDialog) {
//                            isClick = true;
//                            DialogController.getInstance().setBlocked(false);
//                            LoginUtil.loginOut(activity);
//                        }
//                        @Override
//                        public void onDestroy() {
//                            if(isClick){
//                                return;
//                            }
//                            LoginUtil.loginOut(activity);
//                            DialogController.getInstance().setBlocked(false);
//                        }
//                    });
//            DialogController.getInstance().setBlocked(true);
//            return true;
//        }
//        return false;
//    }

    /**
     *
     * @param mUserName
     * @param mPassword 加密后的密码
     * @param activity
     */
    public static void login(String mUserName,String mPassword,AppBaseActivity activity){
       login(mUserName, mPassword, "", "", activity);
    }

    public static void login(final String mUserName,final String mPassword,final String smsCode,String token,final AppBaseActivity activity){
        LoginRequestFactory.createLoginRequest(activity, mUserName, mPassword, smsCode, token, new ResultDataResponseHandler(new ServiceResultCallback(){

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                activity.hideProgressDialog();
                if(connectEvent != HttpConnectEvent.RESPONSE_ERROR){
                    return;
                }
                if(activity instanceof AudioLoginActivity){
                    //如果是从自动登陆页面过来的, 失败跳转登陆页面
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }
            }

            @Override
            public void onSuccess(ResultServices resultServices) {
                activity.hideProgressDialog();
                try{
                    if(ResponseCode.SuccessCode.equals(resultServices.retCode)){//登录成功
                        ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager.Login_2,activity);
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        String token = jsonObject.optString("token");
                        String refreshToken = jsonObject.optString("refreshtoken");
                        String expirein = jsonObject.optString("expirein");
                        User user = null;
                        if(UserDao.getInstance().isExistUser(mUserName)){
                            user = UserDao.getInstance().getLoginUser(mUserName);
                        }else{
                            user = new User();
                        }
                        //保存用户信息

                        user.setAccessToken(token);
                        user.setRefreshToken(refreshToken);
                        user.setLoginName(mUserName);
                        user.setExpirein(expirein);
                        //保存等了状态和用户信息  user和sessions
                        user.save();
                        ApplicationEx.getInstance().getSession().setUser(user);
                        TimeCounter.getInstance().saveTokenUpdateTime();
//                        ApplicationEx.getInstance().getSession().setUserLogin(true);//设置session  用户已经登录
                        loginSuccessHandler(activity);
                        if(LklPreferences.getInstance().getBoolean(LKlPreferencesKey.KEY_SAVE_LOGIN_NAME)){
                            LklPreferences.getInstance().putString(LKlPreferencesKey.KEY_LOGIN_NAME, mUserName);
                        }

                    }else if(ResponseCode.NoBindDeviceCode.equals(resultServices.retCode)){//设备未绑定

                        LoginRequestFactory.createDeviceCheckCode(mUserName, new ResultDataResponseHandler(new ServiceResultCallback() {
                            @Override
                            public void onSuccess(ResultServices resultServices) {

                                if(resultServices.isRetCodeSuccess()){

                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(resultServices.retData);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    String token = jsonObject.optString("btoken");
                                    Intent intent = new Intent(activity, LoginSuccessNeedBindDeviceActivity.class);
                                    intent.putExtra(ConstValues.IntentKey.LoingName, mUserName);
                                    intent.putExtra(ConstValues.IntentKey.Password, mPassword);//此密码是加密后的s
                                    intent.putExtra(ConstValues.IntentKey.BTOKEN, token);
                                    activity.startActivity(intent);
                                }else{
                                    activity.toast(resultServices.retMsg);
                                }

                            }

                            @Override
                            public void onEvent(HttpConnectEvent connectEvent) {
                                activity.toastInternetError();
                            }
                        }), activity).execute();


//                        activity.finish();
                    }else{

                        if(activity instanceof AudioLoginActivity){
                            //如果是从自动登陆页面过来的, 失败跳转登陆页面
                            ToastUtil.toast(activity, resultServices.retMsg);
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            activity.finish();
                        }
                        //您输入的用户名或密码不正确，请重新输入。 写死 by李翔宇
                        if(TextUtils.isEmpty(smsCode)){
                            ToastUtil.toast(activity, activity.getString(R.string.login_error));
                        }else{
                            ToastUtil.toast(activity, resultServices.retMsg);
                        }


                    }

                    LklPreferences.getInstance().putString(LKlPreferencesKey.KEY_LOGIN_NAME, mUserName);

                }catch(Exception e){
                    e.printStackTrace();
                }

            }


        }) ).execute();
    }


    /**
     * 登录成功后的处理
     */
    private static void loginSuccessHandler(final AppBaseActivity activity){
        final User user = ApplicationEx.getInstance().getUser();

        //登录成功 保存用户新到数据库
        String gesturePwd = UserDao.getInstance().getUserGesturePwd(user.getLoginName());
        user.setGesturePwd(gesturePwd);

        activity.showProgressWithNoMsg();
        TokenRefreshService tokenRefreshService = TokenRefreshService.getInstance();


        tokenRefreshService.setTokenRefreshCallback(new TokenRefreshCallback() {
            @Override
            public void onFinish() {
                activity.finish();
            }

            @Override
            public void onInterrupted() {
                activity.hideProgressDialog();
            }
        });
        tokenRefreshService.checkMerchantInfo(TokenRefreshService.ServiceType.LOGIN);



//        //1.有本地登录历史，登录
//        //2.无本地登录历史，手势设置
//        //没有手势密码,且没有忽略手势密码
//        if(!user.isExistGesturePassword()){
//            Intent intent = new Intent(activity,GestrueActivity.class);
//            intent.putExtra(ConstValues.IntentKey.From, GestrueType.SET_GESTRUE);
//            activity.startActivity(intent);
//            activity.finish();
//        }else {//登陆成功
//            Intent intent = new Intent(activity, HomeActivity.class);
//            activity.startActivity(intent);
//
//        }
    }

    /**
     * 刷新令牌  令牌调用
     */
    public static void refreshToken(FragmentActivity mFragmentActivity){
        String refreshToken = ApplicationEx.getInstance().getUser().getRefreshToken();
        LoginRequestFactory.createRefreshTokenRequest(refreshToken,new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                try{
                    if(ResponseCode.SuccessCode.equals(resultServices.retCode)){//令牌刷新成功成功
                        LogUtil.print(resultServices.retMsg);
                        JSONObject jsonObject = new JSONObject(resultServices.retData);

                        String token = jsonObject.optString("token");
                        String refreshToken = jsonObject.optString("refreshtoken");
                        String expirein = jsonObject.optString("expirein");

                        User user = ApplicationEx.getInstance().getUser();
                        user.setAccessToken(token);
                        user.setRefreshToken(refreshToken);
                        user.setExpirein(expirein);

                        ApplicationEx.getInstance().getSession().setUser(user);
                        user.save();
                    }
                }catch(Exception e){
                    LogUtil.print(e);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                if(connectEvent != HttpConnectEvent.RESPONSE_ERROR)
                    //连接异常处理
                    return;
            }
        }),mFragmentActivity).execute();
    }

    /**
     * 4.3.5.商户绑定查询
     */
    public static void checkMerchantBind(){
        BusinessRequest businessRequest = LoginRequestFactory.createMerchantStateRequest();
        businessRequest.setResponseHandler(new ResultDataResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                try{
                    if(ResponseCode.SuccessCode.equals(resultServices.retCode)){
                        JSONArray jsonArray = new JSONArray(resultServices.retData);
                        if(jsonArray == null || jsonArray.length()<=0){
                            //没有开通的商户信息
                        }
                    }else {
                        //TODO 查询失败，如何处理
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    //TODO 查询失败，如何处理
                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

            }
        })).execute();

    }

    public static interface CheckResultListener{
        void onPass();
        void onDeny();
    }



}
