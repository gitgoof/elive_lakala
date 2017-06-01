package com.lakala.platform.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.ClipboardManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.config.Config;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.ui.component.NavigationBar;
import com.lakala.ui.map.MapUtil;

import org.apache.http.protocol.HTTP;


/**
 * 通用webview页面
 * <p/>
 * 特别说明
 *
 * @author ssss
 */
public class CommonWebViewActivity extends BaseActionBarActivity {

    /**默认错误页面*/
    private static final String ERROR_PAGE = "file:///android_asset/error_page/error_page.html";

    private WebView webView;
    /**跳转过来的第一个页面*/
    private ClipboardManager cmb;
    private PopupWindow menuPopup;
    private LinearLayout bottomLayout;
    private RelativeLayout rootLayout;
    private ImageView webBack, webGoAhead, webRefresh, webMore;
    private View browseText, copyText, cancelText;
    private String preUrl = "";
    private LoadStatus loadStatus;
    private FragmentActivity _this;

    private TextView error_txt;
    private Bundle bundle;

    private DownloadListener downloadListener = new DownloadListener() {//Webview添加调用系统浏览器来提供文件下载支持

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_webview);
        init();
        loadPage(getIntent());



    }

    protected void init() {
        _this = this;
        error_txt = (TextView) findViewById(R.id.error_txt);
        cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        rootLayout = (RelativeLayout) findViewById(R.id.id_root_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.id_webview_handle_layout);
        webBack = (ImageView) findViewById(R.id.id_web_back);
        webGoAhead = (ImageView) findViewById(R.id.id_web_goahead);
        webRefresh = (ImageView) findViewById(R.id.id_web_refresh);
        webMore = (ImageView) findViewById(R.id.id_web_more);

        webBack.setOnClickListener(this);
        webGoAhead.setOnClickListener(this);
        webRefresh.setOnClickListener(this);
        webMore.setOnClickListener(this);
        error_txt.setOnClickListener(this);

        //WebView
        webView = (WebView) findViewById(R.id.webView1);
        webView.setDownloadListener(downloadListener);
        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName(HTTP.UTF_8);//设置页面默认编码为utf-8

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            Level16Apis.enableUniversalAccess(settings);
        String databasePath = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(databasePath);
        settings.setDomStorageEnabled(true);

        settings.setGeolocationDatabasePath(databasePath);
        settings.setGeolocationEnabled(true);

        settings.setAppCacheMaxSize(5 * 1048576);
        String pathToCache = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setAppCachePath(pathToCache);
        settings.setAppCacheEnabled(true);

        settings.setAllowFileAccess(true);

        settings.setCacheMode(MapUtil.isNetworkAvailable(this) ? WebSettings.LOAD_DEFAULT : WebSettings.LOAD_CACHE_ELSE_NETWORK);

        //PopMenu
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.common_webview_more_popup, null);
        browseText = layout.findViewById(R.id.id_more_browse);
        copyText = layout.findViewById(R.id.id_more_copy);
        cancelText = layout.findViewById(R.id.id_more_cancel);
        browseText.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        copyText.setOnClickListener(this);

        menuPopup = new PopupWindow(this);
        menuPopup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        menuPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        menuPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        menuPopup.setAnimationStyle(R.style.more_popup_anim_style);
        menuPopup.setContentView(layout);
        menuPopup.setTouchable(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        loadPage(intent);
    }

    @Override
    public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
        if (navBarItem == NavigationBar.NavigationBarItem.back) {
            if (menuPopup.isShowing()){
                menuPopup.dismiss();
            }
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
        }
    }

    private void loadPage(Intent intent) {
        rootLayout.setVisibility(View.VISIBLE);
        error_txt.setVisibility(View.GONE);
        if (intent != null){
            bottomLayout.setVisibility(intent.getBooleanExtra(BusinessLauncher.INTENT_PARAM_KEY_SHOWCONTROLBAR,true) ? View.VISIBLE : View.GONE);
            String url = intent.getStringExtra(BusinessLauncher.INTENT_PARAM_KEY_URL);
            webView.loadUrl(formatUrl(url));
        }
    }

    /**
     * 处理操作按钮状态
     */
    private void handleButtonStatus() {
        if (webView.canGoBack()) {
            webBack.setEnabled(true);
            webBack.setImageResource(R.drawable.web_left_selector);
        } else {
            webBack.setEnabled(false);
            webBack.setImageResource(R.drawable.web_left_no);
        }

        if (webView.canGoForward()) {
            webGoAhead.setEnabled(true);
            webGoAhead.setImageResource(R.drawable.web_right_selector);
        } else {
            webGoAhead.setEnabled(false);
            webGoAhead.setImageResource(R.drawable.web_right_no);
        }

        if (loadStatus == LoadStatus.loading) {
            webRefresh.setImageResource(R.drawable.web_stop_renovate_selector);
        } else {
            webRefresh.setImageResource(R.drawable.web_renovate_selector);
        }
    }

    private WebChromeClient webChromeClient = new WebChromeClient() {
        public void onReceivedTitle(WebView view, String title) {
            if (StringUtil.isNotEmpty(title)){
                navigationBar.setTitle(title);
            }
        }
    };

    /**
     * webview加载状态
     */
    private enum LoadStatus {
        loading,
        loadComplete
    }

    @TargetApi(16)
    private static class Level16Apis {
        static void enableUniversalAccess(WebSettings settings) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
    }

    private static String formatUrl(String url){
        if (url == null){
            return "";
        }

        url = BusinessRequest.replaceHostByUrlSecheme(url);

        if (!url.contains("://")){
            //URL 不是绝对路径则，在前面添加主机头。
            url = Config.getBaseRequestUrl().concat(url);
        }

        return url;
    }

    /**
     * webViewClient,webView设置此对象时，网页中的链接不会通过浏览器打开
     */
    private WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            loadStatus = LoadStatus.loadComplete;
            handleButtonStatus();
            navigationBar.hideRightProgress();
            rootLayout.setVisibility(View.GONE);
            error_txt.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            LogUtil.print("onPageFinished = " + url);
            loadStatus = LoadStatus.loadComplete;
            handleButtonStatus();
            navigationBar.hideRightProgress();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            LogUtil.print("onPageStarted = " + url);
            loadStatus = LoadStatus.loading;
            navigationBar.showRightProgress();
            if (!url.equals(ERROR_PAGE)) {
                preUrl = url;
            }
            handleButtonStatus();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.print("shouldOverrideUrlLoading = " + url);

            if (url.contains("tel:")) {//截获电话页面，调整拨打电话
                PhoneNumberUtil.callPhone(CommonWebViewActivity.this, url);
                webView.goBack();
            }
            else if (url.startsWith("koala://pay")) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                webView.goBack();
            }
            else {
                view.loadUrl(formatUrl(url));
            }

            return true;
        }
    };

    /**
     * @param type   来源专题（推送）类型 0-便利特惠 商品 1-url链接形式 2-其他业务
     * @param params url后附带的参数
     * @param pCode  数据统计使用的pCode,表明页面来源
     * @method doType
     */
    private void doType(String type, String params, String pCode) {
        if (type.equals("1")) {
            Intent intent = new Intent(CommonWebViewActivity.this, CommonWebViewActivity.class);
            intent.putExtra("sub_url", params);
            startActivity(intent);
        }
    }

    private String dopCode(String params) {
        String pCode1 = "";
        if (params.contains("&pCode=")) {
            //寻找的参数名称
            if (params.contains("&")) { //title不是最后一个参数，还包括其他参数的情况
                if (params.contains("&pCode=")) {
                    int second = params.lastIndexOf("&");
                    pCode1 = params.substring(second, params.length()).replace("&pCode=", "");
                }
            }
        }
        return pCode1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (menuPopup.isShowing()) {
            menuPopup.dismiss();
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onViewClick(View v) {
        //后退
        if (v.equals(webBack)){
            if (menuPopup.isShowing()) {
                menuPopup.dismiss();
            }
            if (webView.canGoBack()) {
                webView.goBack();
            }
        }
        //前进
        else if (v.equals(webGoAhead)){
            if (menuPopup.isShowing()) {
                menuPopup.dismiss();
            }
            if (webView.canGoForward()) {
                webView.goForward();
            }
        }
        //刷新
        else if (v.equals(webRefresh)){
            if (menuPopup.isShowing()) {
                menuPopup.dismiss();
            }
            if (loadStatus == LoadStatus.loading) {//加载中时点击按钮执行停止操作
                webView.stopLoading();
            }
            if (loadStatus == LoadStatus.loadComplete) {//加载完成后点击按钮执行刷新操作
                webView.loadUrl(preUrl);
            }
        }
        //更多操作
        else if (v.equals(webMore)){
            if (!menuPopup.isShowing()) {
                menuPopup.showAtLocation(rootLayout, Gravity.BOTTOM, 0, 0);
                menuPopup.update();
            } else {
                menuPopup.dismiss();
            }
        }
        else if (v.equals(error_txt)){
            //todo 错误提示页面点击事件
        }
        //手机浏览器打开
        else if (v.equals(browseText)){
            if (menuPopup.isShowing()) {
                menuPopup.dismiss();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(preUrl));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        //复制链接
        else if (v.equals(copyText)){
            if (menuPopup.isShowing()) {
                menuPopup.dismiss();
            }
            cmb.setText(preUrl);
            ToastUtil.toast(CommonWebViewActivity.this, "已复制到剪贴板");
        }
        else if (v.equals(cancelText)){
            menuPopup.dismiss();
        }
    }
}
