package com.lakala.shoudan.activity.shoudan.webmall;

import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.component.SharePopupWindow;

/**
 * 
 * @author ZhangMY
 *
 */
public abstract class WebMallMouldActivity extends AppBaseActivity {

    protected WebView webView;
    protected SharePopupWindow sharePopupWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_mall);
        initUI();
    }
    
    protected abstract void initTitle();
    
    protected abstract void loadUrl();
    
    protected abstract void dealShouldOverrideUrlLoading(WebView view, String url);


    @Override
    protected void initUI() {
        super.initUI();
        initTitle();
        initWebView();
    }

    private void initWebView(){
        webView = (WebView)findViewById(R.id.web_mall_view);
        final WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);

        //WebView启用Javascript脚本执行
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        // settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setUseWideViewPort(false);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //映射Java对象到一个名为”js2java“的Javascript对象上
        //JavaScript中可以通过"window.js2java"来调用Java对象的方法

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

            	dealShouldOverrideUrlLoading(view, url);
                return false;
            }

        });

        webView.setWebChromeClient(new WebChromeClient());

        loadUrl();
    }

    protected void webViewBack(){
       if(webView.canGoBack()){
           webView.goBack();
       }else{
           finish();
       }
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    public void setBackgroundAlpha(float al){
        WindowManager.LayoutParams params=getWindow().getAttributes();
        params.alpha=al;
        getWindow().setAttributes(params);
    }
}
