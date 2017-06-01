package com.lakala.shoudan.activity.webbusiness;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by More on 15/12/18.
 */
public class BusWebView extends WebView{

    private BusCallerBack busCallerBack;

    public BusWebView(Context context) {
        super(context);
    }

    public BusWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BusWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initBus(){
        WebSettings settings = getSettings();
//        settings.setSupportZoom(true);

        //WebView启用Javascript脚本执行
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("gb2312");
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
//        settings.setDatabaseEnabled(true);
//        settings.setDomStorageEnabled(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setSupportZoom(false);
//        settings.setUseWideViewPort(false);
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        setWebChromeClient(new WebChromeClient());


    }

    public BusCallerBack getBusCallerBack() {
        return busCallerBack;
    }

    public void setBusCallerBack(BusCallerBack busCallerBack) {
        this.busCallerBack = busCallerBack;
        setWebViewClient(new BusWebClient(busCallerBack));
    }
}
