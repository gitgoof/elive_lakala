package com.lakala.shoudan.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.GestrueType;
import com.lakala.platform.common.LKlPreferencesKey;
import com.lakala.platform.common.LklPreferences;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.login.GestrueActivity;
import com.lakala.shoudan.activity.login.LoginActivity;
import com.lakala.shoudan.activity.main.MainActivity;
import com.lakala.shoudan.common.ConstValues;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.shoudan.loginservice.TokenRefreshService;
import com.lakala.shoudan.util.LoginUtil;
import com.lakala.shoudan.util.WindowDialog;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.BaseDialog;
import com.lakala.ui.dialog.ProgressDialog;

import java.lang.reflect.Method;

/**
 * Created by xyz on 13-12-10.
 */
public abstract class AppBaseActivity extends BaseActionBarActivity {

    private boolean isStartLogin = false;//是否启动登录界面s
    /**
     * 是否禁止截屏
     */
    private boolean isForbidScreenshot = true;
    /**
     * 页面请求码
     */
    public static final int REQUEST_CODE_LOGIN = 32;
    public static final int REQUEST_CODE_LOGIN_GESTURE = 33;
    public static final int REQUEST_CODE_LOCK_SCREEN = 34;
    public static final int REQUEST_CODE_TO_PAY = 35;
    protected ProgressDialog progressDialog;
    protected AppBaseActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
//        if (!BuildConfig.DEBUG) {
//            setIsForbidScreenshot(isForbidScreenshot);
//        }
        ApplicationEx.getInstance().setActiveContext(this);
        if (isRequired2Login()) {
            login();
        } else {
            isStartLogin = false;
        }

    }

    /**
     * 打开一个Activity 默认 不关闭当前activity
     *
     * @param clz
     */
    public void gotoActivity(Class<?> clz) {
        gotoActivity(clz, false, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity) {
        gotoActivity(clz, isCloseCurrentActivity, null);
    }

    public void gotoActivity(Class<?> clz, boolean isCloseCurrentActivity, Bundle ex) {
        Intent intent = new Intent(this, clz);
        if (ex != null)
            intent.putExtras(ex);
        startActivity(intent);
        if (isCloseCurrentActivity) {
            finish();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        navigationBar.setActionBtnVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        TimeCounter.getInstance().may2Gesture(this);

        WindowDialog.getInstance().addViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WindowDialog.getInstance().removeViews();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
//        TimeCounter.getInstance().save2BackgroundTime(this);
    }


    public boolean isProgressisShowing() {
        if (progressDialog != null) {
            return progressDialog.isShowing();
        }
        return progressDialog.isShowing();
    }


    /**
     * 登陆--该功能一般供外部调用,在Session过期的时候重新登陆
     */
    public void login() {
        isStartLogin = true;

//        //支付调用
//        if (ApplicationEx.getInstance().getData() != null) {
//            startActivityForResult(new Intent(mContext, LoginActivity.class), REQUEST_CODE_TO_PAY);
//            return;
//        }


        if (isLoginOutMode()) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LOGIN);
            return;
        }

        //如果有上次登录用户信息，则取出登录用户信息
        User user = ApplicationEx.getInstance().getSession().getUser();

        if (TextUtils.isEmpty(user.getLoginName())) {
            //如果没有上次登录用户信息，则直接跳转到登录页面
            LoginUtil.clearSession2Login(this);
            return;
        }

        if (ApplicationEx.getInstance().getSession().isUserLogin()) {
            return;
        }

        if (user.isExistGesturePassword()) {

            if (TokenRefreshService.getInstance().isLoginByPwd()) {
                ApplicationEx.getInstance().getSession().setUserLogin(true);
                TokenRefreshService.getInstance().setLoginByPwd(false);
                return;
            }
            ApplicationEx.getInstance().getSession().setUserLogin(false);
            Intent intent = new Intent(AppBaseActivity.this, GestrueActivity.class);
            intent.putExtra(ConstValues.IntentKey.From, GestrueType.LOGIN_GESTRUE);
            startActivity(intent);
        } else if (!user.skipGesture()) {
            ApplicationEx.getInstance().getSession().setUserLogin(true);
            Intent intent = new Intent(AppBaseActivity.this, GestrueActivity.class);
            intent.putExtra(ConstValues.IntentKey.From, GestrueType.SET_GESTRUE);
            startActivity(intent);

        } else {
            ApplicationEx.getInstance().getSession().setUserLogin(true);
            //调用MainActivity中onNewIntent进行推送业务处理
//            BusinessLauncher.getInstance().start("home");
        }

    }

    /**
     * 是否为退出登录模式
     */
    protected boolean isLoginOutMode() {
        return LklPreferences.getInstance().getBoolean(LKlPreferencesKey.KEY_LOGIN_OUT, false);
    }

    /**
     * 当前Activity是否需要登陆的支持;
     * 如果不需要,则重写该方法返回false;
     *
     * @return boolean
     */
    protected boolean isRequired2Login() {
        return false;
    }


    /**
     * @return 判断是否为魅族手机
     */
    protected boolean hasSmartBar() {
        try {
            // 新型号可用反射调用Build.hasSmartBar()
            Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
            return (Boolean) method.invoke(null);
        } catch (Exception e) {
            LogUtil.print(e);
        }
        // 反射不到Build.hasSmartBar(),则用Build.DEVICE判断
        if (Build.DEVICE.equals("mx2")) {
            return true;
        } else if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }

    /**
     * 如果有第三方调用时，拦截返回按钮操作，要返回到第三方的回调地址页面.<br>
     * 否则，直接finish().
     *
     * @param channelCode 第三方渠道Id
     * @param callbackUrl 回调地址“package|class”
     * @see Util#interceptBackEvent(Activity, String, String)
     */
    protected void interceptBackEvent(String channelCode, String callbackUrl) {
        Util.interceptBackEvent(this, channelCode, callbackUrl);
    }

    /**
     * 根据魅族提供的方法设置actionbar tab放置到下面的smartBar中去
     */
    protected void setActionBarTabsShowAtBottom(ActionBar actionbar, boolean showAtBottom) {
        try {
            Method method = Class.forName("android.app.ActionBar").getMethod("setTabsShowAtBottom", new Class[]{boolean.class});
            method.invoke(actionbar, showAtBottom);
        } catch (Exception e) {
            LogUtil.print(e);
        }
    }

    /**
     * activity的跳转
     *
     * @param mContext
     * @param mClass
     */
    public void skipActivity(Context mContext, Class mClass) {
        Intent intent = new Intent(mContext, mClass);
        startActivity(intent);
    }


    public void showProgressWithNoMsg() {
        showProgressDialog("");
    }

    public void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

    }

    public void showNotOpenDialog() {
        DialogCreator.showMerchantNotOpenDialog(this);
    }

    protected Dialog showProgressDialog(int msgId) {
        String msg = getString(msgId);
        return showProgressDialog(msg);
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    protected Dialog showProgressDialog(String msg) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        progressDialog = DialogCreator.createDialogWithNoMessage(context);

        if (!progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
            progressDialog.show();
        }
        return progressDialog;
    }

    protected void initUI() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    /**
     * @param msg      要显示的消息
     * @param listener 按钮点击事件
     * @param btns     要显示的按钮
     * @return
     */
    protected BaseDialog showMessage(String msg, AlertDialog.AlertDialogDelegate
            listener, String... btns) {
        AlertDialog dialog = new AlertDialog();
        dialog.setMessage(msg);
        dialog.setButtons(btns);
        dialog.setDialogDelegate(listener);
        dialog.show(getSupportFragmentManager());
        return dialog;
    }

    protected BaseDialog showMessage(String msg, AlertDialog.AlertDialogDelegate listener) {
        return showMessage(msg, listener, "确定");
    }

    public BaseDialog showMessage(String msg) {
        AlertDialog.AlertDialogDelegate listener = new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                dialog.dismiss();
            }
    };
        return showMessage(msg, listener);
    }

    protected BaseDialog showMessage(int msgId) {
        String msg = getString(msgId);
        return showMessage(msg);
    }

    protected BaseDialog showMessageAndBackToMain(String msg) {
        AlertDialog.AlertDialogDelegate listener = new AlertDialog.AlertDialogDelegate() {
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                callMainActivity();
            }
        };
        return showMessage(msg, listener);
    }

    public void forwardActivity(Class forwardActivity) {
        Intent intent = getIntent();
        intent.setClass(this, forwardActivity);
        startActivity(intent);
    }

    public void toast(String msg) {
        if (msg == null) {
            return;
        }
        ToastUtil.toast(this, msg);
    }

    public void toast(int msg) {
        ToastUtil.toast(this, msg);
    }

    public void toastInternetError() {
        ToastUtil.toast(this, getString(R.string.socket_fail));
    }

    protected void disableView(View view) {
        if (view != null && view.isEnabled()) {
            view.setEnabled(false);
            view.setPressed(true);
        }
    }

    protected void enableView(View view) {
        if (view != null && !view.isEnabled()) {
            view.setEnabled(true);
            view.setPressed(false);
        }
    }


    protected void callMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void setBackgroundAlpha(float al) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = al;
        getWindow().setAttributes(params);
    }

    /**
     * 设置是否禁止截屏
     *
     * @param isForbidScreenshot
     */
    public void setIsForbidScreenshot(boolean isForbidScreenshot) {
        this.isForbidScreenshot = isForbidScreenshot;
        if (isForbidScreenshot) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}
