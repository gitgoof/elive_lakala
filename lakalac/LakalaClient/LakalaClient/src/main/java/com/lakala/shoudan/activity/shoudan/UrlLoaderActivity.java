package com.lakala.shoudan.activity.shoudan;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lakala.library.util.LogUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.webbusiness.BusWebView;
import com.lakala.shoudan.activity.webbusiness.WebTransInfo;

/**
 * Created by More on 15/7/6.
 */
public class UrlLoaderActivity extends AppBaseActivity {

    protected BusWebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_loader);
        initUI();
    }



    protected void initUI() {

        navigationBar.setTitle(getIntent().getStringExtra("title"));
        webView = (BusWebView)findViewById(R.id.webview);
        //使用webview加载协议内容
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        //点击链接当前页面响应
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        LogUtil.print("------>","AdUrl:"+getIntent().getStringExtra("url"));
        if(!TextUtils.isEmpty(getIntent().getStringExtra("url"))){
            webView.loadUrl(getIntent().getStringExtra("url"));
        }
    }
}
