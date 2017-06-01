package com.lakala.platform.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.lakala.core.base.LKLActivityDelegate;
import com.lakala.core.cordova.cordova.CordovaActivity;
import com.lakala.core.cordova.cordova.CordovaChromeClient;
import com.lakala.core.cordova.cordova.CordovaWebView;
import com.lakala.core.launcher.ActivityLauncher;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.TypeConvertionUtil;
import com.lakala.platform.R;
import com.lakala.platform.bean.PluginObj;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.ConfigFileManager;
import com.lakala.platform.common.TimeCounter;
import com.lakala.platform.config.Config;
import com.lakala.platform.cordovaplugin.BusinessParameter;
import com.lakala.platform.cordovaplugin.Navigation;
import com.lakala.platform.cordovaplugin.SwipePluginListener;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.swiper.mts.CardInfo;
import com.lakala.platform.swiper.mts.DialogController;
import com.lakala.platform.swiper.mts.SetSwipeTypeActivity;
import com.lakala.platform.swiper.mts.SwipeDefine;
import com.lakala.platform.swiper.mts.SwipeLauncher;
import com.lakala.platform.swiper.mts.SwipeListener;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONObject;

import java.lang.reflect.Method;

import butterknife.ButterKnife;

/**
 * Created by Vinchaos api on 13-12-28.
 * 对CordovaActivity进行公共处理
 */
public class BaseCordovaWebActivity extends CordovaActivity implements  NavigationBar.OnNavBarClickListener, SwipeListener{

    //action
    private String action;
    //requestCode
    private int requestCode;

    protected NavigationBar navigationBar;

    /**
     * 我们针对第三方业务使用我们的插件做了过滤，需要在pluginfilter.config文件中进行配置，具体怎样配置可以
     * 查看Tower上插件过滤文档。在测试环境下为了测试，默认不进行插件过滤。如果生产上线，务必将
     * needFilterPlugin 设置为true,否则插件过滤不起作用。
     */
    private boolean needFilterPlugin = false;


    @Override
    protected LKLActivityDelegate delegate() {
        return BusinessLauncher.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.init();
        /**
         * modify lvyongang  读取插件过滤配置文件
         */
        makePluginFilter();

        super.postMessage("spinner", "stop");

        super.clearCache();

        Intent intent = getIntent();
        loadSencha(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimeCounter.getInstance().may2Gesture(this);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        TimeCounter.getInstance().save2BackgroundTime(this);
    }

    @Override
    protected ViewGroup setRootView() {
        setContentView(R.layout.plat_activity_base);

        //初始化导航栏
        navigationBar = (NavigationBar) findViewById(R.id.navigation_bar);
        navigationBar.setOnNavBarClickListener(this);

        if (getIntent() != null){
            String title = getIntent().getStringExtra(ActivityLauncher.INTENT_PARAM_KEY_TITLE);
            if (title != null){
                navigationBar.setTitle(title);
            }
        }

        ButterKnife.bind(this);
        return (FrameLayout)findViewById(R.id.base_container);
    }

    @Override
    protected void onBack() {
        this.appView.loadUrl(
                "javascript:try{" +
                        "if(!cordova._native.navigation.isWebGoBack()){" +
                        "cordova._native.navigation.goBack();" +
                        "}" +
                        "}catch(e){" +
                        "console.log('ExecuteNativeCmd://goBack');" +
                        "};"
        );
    }

    @Override
    protected void showSpinner(String title, String message, boolean isCancel) {
        //todo show dialog
    }

    @Override
    protected void dismissSpinner() {
        //todo dismiss dialog
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Object handleData(String id){

        if(id.equalsIgnoreCase(BusinessParameter.SEND_ACTION)){
            return action;
        }
        else if(id.equalsIgnoreCase(BusinessParameter.SEND_REQUEST_CODE)){
            return requestCode;
        }
        else if(id.equalsIgnoreCase(BusinessParameter.SEND_DATA)){
            if (getIntent() != null){
                return TypeConvertionUtil.bundle2Json(getIntent().getExtras());
            }
            else{
                return new JSONObject();
            }
        }
        else if(id.equalsIgnoreCase(Navigation.GET_NAVIGATION)){
            return navigationBar;
        }

        return  null;
    }

    /**
     * 返回我们自定义的 ChromeClient，用于拦截处理一些消息。
     * @param webView 当前的 WebView 对象。
     * @return CordovaChromeClient 实例
     */
    @Override
    protected CordovaChromeClient makeChromeClient(CordovaWebView webView) {
        return new CordovaWebActivityChromeClient(this, webView);
    }

    /**
     * 获取要加载的 URL
     *
     * @param anchor
     * @return
     */
    protected String getUrl(String anchor){
        //http://10.7.111.198/merchant/index-android.html
//        return "http://10.7.111.198/merchant/index-android.html#" + anchor;
//           return "http://192.168.191.1:8080/webapp/trunk/index-android.html#" + anchor;
        return Config.getWebAppIndexPage() + anchor;
//        return "file:///android_asset/webapp/index-android.html#" + anchor;
    }

    /**
     * 加载Sencha业务
     */
    private void loadSencha(Intent intent) {
        String action = getAction(intent);
        sendAction(action);
        sendRequestCode(getRequestCode(intent));

        String anchor = StringUtil.isEmpty(action) ? "" : action.substring(action.indexOf(":") + 1, action.length());

        super.loadUrl(getUrl(anchor));
    }

    /**
     * 获取action
     *
     * @param intent
     * @return
     */
    private String getAction(Intent intent) {
        return intent.getStringExtra(BusinessLauncher.INTENT_PARAM_KEY_ACTION);
    }

    /**
     * 获取 requestCode
     *
     * @param intent
     * @return
     */
    private int getRequestCode(Intent intent) {
        return intent.getIntExtra(BusinessLauncher.INTENT_PARAM_KEY_REQUEST_CODE, 0);
    }

    /**
     * 发送action给插件
     *
     * @param acton
     */
    private void sendAction(String acton) {
        this.action = acton;
        this.appView.postMessage(BusinessParameter.SEND_ACTION, acton);
    }

    /**
     * 发送code给插件
     *
     * @param code
     */
    private void sendRequestCode(int code) {
        this.requestCode = code;
        this.appView.postMessage(BusinessParameter.SEND_REQUEST_CODE, code);
    }


    @Override
    public Object onMessage(String id, Object data) {
        if (id.equals("requestShowDialog")) {
            PluginObj obj = (PluginObj) data;
            if (obj.getId().equals("show")) {
                super.spinnerStart("", obj.getContent(), obj.isCancel());
            } else if (obj.getId().equals("hide")) {
                super.spinnerStop();
            }
        }
        return super.onMessage(id, data);
    }


    @Override
    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(appView.getWindowToken(), 0);
        if (navBarItem == NavigationBar.NavigationBarItem.back) {
            onBack();
        } else if (navBarItem == NavigationBar.NavigationBarItem.action) {
            appView.sendJavascript("cordova._native.navigation.actionClick();");
        }
    }

    /**
     * 读取插件过滤配置文件
     */
    private void makePluginFilter(){
        /**
         * modify lvyonggang  插件过滤逻辑
         */
        this.appView.setNeedFilter(isNeedFilterPlugin());
        if(isNeedFilterPlugin()){
            this.appView.setFilterConfig(ConfigFileManager.getInstance().readPluginFilterConfig());
        }
    }

    /**
     * 返回 isNeedFilterPlugin 的值
     *
     * @return
     */
    protected boolean isNeedFilterPlugin() {
        return needFilterPlugin;
    }

    private SwipePluginListener swipePluginListener;

    public void setSwipePluginListener(SwipePluginListener listener) {
        this.swipePluginListener = listener;
    }


    @Override
    public void onCancel(SwipeLauncher.CancelMode mode) {
        if (mode == SwipeLauncher.CancelMode.SET_SWIPE) {
            DialogController.getInstance().showProgress(this);
            DialogController.getInstance().dismiss();
        } else {
            String jsonString = "{\"status\":\"" + mode.toString() + "\"}";
            if (swipePluginListener == null) return;
            swipePluginListener.onCancel(jsonString);
        }
    }

    @Override
    public void reLogin() {
        //重新登录
        SwipeLauncher.getInstance().stopSwipe();
        try {
            Class cls = Class.forName("com.lakala.shoudan.util.LoginUtil");
            Method method = cls.getDeclaredMethod("restartLogin", Context.class);
            method.invoke(null, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPin(String maskPan) {
        if (swipePluginListener == null) return;
        swipePluginListener.onRequestPin(maskPan);
    }

    @Override
    public void onReadCardPin(CardInfo cardInfo, SwipeDefine.SwipeCardType swipeCardType) {
        if (swipePluginListener == null) return;
        swipePluginListener.onReadCardPin(cardInfo, swipeCardType);
    }

    @Override
    public void onFinish(boolean isSuccess, JSONObject tc) {
        if (swipePluginListener == null) return;
        swipePluginListener.onFinish(isSuccess, tc);
    }

    @Override
    public void onSecondIssuanceFail() {
        if (swipePluginListener == null) return;
        swipePluginListener.onSecondIssuanceFail();
    }

    @Override
    public void onStartSwiper() {
        if (swipePluginListener == null) return;
        swipePluginListener.onStartSwiper();
    }

    @Override
    public void onWaitInputPinTimeout() {
        if (swipePluginListener == null) return;
        swipePluginListener.onWaitInputPinTimeout();
    }
    @Override
    public void onDestroy() {
        try {
            SwipeLauncher.getInstance().stopSwipe();
        } catch (Exception e) {
            LogUtil.print(e);
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == SetSwipeTypeActivity.CREDIT_RESULT_CODE){
            if (swipePluginListener != null){
                swipePluginListener.handleAction(resultCode);
            }
        }
    }
}
