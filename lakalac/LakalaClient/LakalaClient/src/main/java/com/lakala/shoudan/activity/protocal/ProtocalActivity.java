package com.lakala.shoudan.activity.protocal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.platform.common.map.LocationManager;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.statistic.PublicEnum;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.merchant.register.MerchantRegisterActivity;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.component.NavigationBar;

/**
 * Created by HUASHO on 2015/1/28.
 * 服务协议
 */
public class ProtocalActivity extends AppBaseActivity {
    private TextView sure;
    private WebView webView = null;
    public static final String PROTOCAL_KEY = "protocalKey";//key
    public static final String PROTOCAL_TITIL = "protocal_title";//标题
    public static final String PROTOCAL_URL = "protocal_url";//url
    private String title = "";
    private String url;
    private ProtocalType protocalType;

    public static void open(Context context, ProtocalType type) {
        Intent intent = new Intent(context, ProtocalActivity.class);
        intent.putExtra(PROTOCAL_KEY, type);
        context.startActivity(intent);
    }

    public static void open(Context context, String title, String url) {
        Intent intent = new Intent(context, ProtocalActivity.class);
        intent.putExtra(PROTOCAL_KEY, ProtocalType.CUSTOM_PROTOCAL);
        intent.putExtra(PROTOCAL_TITIL, title);
        intent.putExtra(PROTOCAL_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocal);
        initUI();
    }

    protected void initUI() {
        protocalType = (ProtocalType) getIntent().getSerializableExtra(PROTOCAL_KEY);
        if (protocalType == null || protocalType == ProtocalType.CUSTOM_PROTOCAL) {
            title = getIntent().getStringExtra(PROTOCAL_TITIL);
            url = getIntent().getStringExtra(PROTOCAL_URL);
        } else {
            title = protocalType.getTitle();
            url = protocalType.getUrl();
        }
        if (TextUtils.isEmpty(title)) {
            navigationBar.setTitle(R.string.service_protocal);
        } else {
            navigationBar.setTitle(title);
        }
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    back();
                }
            }
        });
        sure = (TextView) findViewById(R.id.button);
        webView = (WebView) findViewById(R.id.webview_protocal);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setAppCacheEnabled(false);
        //点击链接当前页面响应
        webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                }
        );
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
        switch (protocalType) {
            case BUY_LAKALA_SWIPE: {
                setResult(RESULT_OK);
                // 购买拉卡拉刷卡器
                sure.setVisibility(View.VISIBLE);
                sure.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                getPhone();

                            }
                        }
                );
                break;
            }
            case GPS_PERMISSION:
                sure.setVisibility(View.VISIBLE);
                sure.setText("确定");
                sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final LocationManager locationManager = LocationManager.getInstance();
                        locationManager.setFirst(false);
                        locationManager.startLocation(new LocationManager.LocationListener() {
                            @Override
                            public void onLocate() {
                                Intent intent = new Intent(context, MerchantRegisterActivity.class);
                                context.startActivity(intent);
                                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                                        .Merchant_Info_Home, context);
                                ShoudanStatisticManager.getInstance().onEvent(ShoudanStatisticManager
                                        .Merchant_Info_Add, context);
                            }

                            @Override
                            public void onFailed() {
                                openGPS(context);
                            }
                        });
                    }
                });
                break;
        }
    }

    private void getPhone() {
        showProgressWithNoMsg();
        ShoudanService.getInstance().queryLakalaService(new ShoudanService.PhoneQueryListener() {
            @Override
            public void onSuccess(String phoneStr) {
                hideProgressDialog();
                if (!TextUtils.isEmpty(phoneStr)) {
                    PhoneNumberUtil.callPhone(ProtocalActivity.this, phoneStr);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                toastInternetError();
            }
        });


    }

    /**
     * 导航栏返回键
     */
    private void back() {
        if (protocalType != ProtocalType.NIGHT_HELP) {
            finish();
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            } else {
                finish();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (protocalType != ProtocalType.NIGHT_HELP) {
                finish();
            } else {
                if (webView.canGoBack()) {
                    webView.goBack();
                    return false;
                } else {
                    finish();
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 提示用户打开GPS
     *
     * @param context
     */
    public final void openGPS(Context context) {
        DialogCreator.createConfirmDialog((FragmentActivity) context, "确定",
                "定位服务未开启，请进入系统设置中打开定位服务，并允许收款宝使用定位服务"
        ).show();
    }
}
