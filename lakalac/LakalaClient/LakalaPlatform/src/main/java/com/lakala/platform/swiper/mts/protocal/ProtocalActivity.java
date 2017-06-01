package com.lakala.platform.swiper.mts.protocal;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.FileUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONObject;

/**
 * 服务协议
 */
public class ProtocalActivity extends BaseActionBarActivity {
    /**
     * web data key
     */
    public static final String DATA = "data";
    /**
     * 协议title
     */
    public static final String PROTOCAL_TITLE = "title";

    /**
     * 协议URL
     */
    public static final String PROTOCAL_URL = "url";
    /**
     * 跳转业务时，通过intent传递数据的key
     */
    public static final String BUSINESS_BUNDLE_KEY = "BUSINESS_BUNDLE_KEY";

    protected WebView webView = null;
    private TextView txtContent;
    private LinearLayout layoutText;
    private EProtocalType protocalType;
    private static final String ENCODING = "UTF-8";
    private static final String MINE_TYPE = "text/html";

    /** 页面来源标示 */
    private String mAction;
    /** 显示同意按钮 */
    public static final String ACTION_SHOW_BUTTOM = "action.show.button";

    /**
     * Key值定义
     */
    public static final String PROTOCAL_KEY = "protocalKey";
    public static final String KEY_WEB_TITLE = "key_web_title";
    public static final String KEY_WEB_URL = "key_web_url";
    public static final String KEY_WEB_HTML = "key_web_html";
    public static final String KEY_TYPE = "key_type";

    private WebChromeClient webChromeClient = new WebChromeClient() {

        public void onReceivedTitle(WebView view, String title) {
            //设置标题
            if (StringUtil.isNotEmpty(title)){
                navigationBar.setTitle(title);
            }else {
                setTitle(protocalType);
            }
        }
    };

    private WebViewClient procalWebClient  =new WebViewClient(){

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plat_activity_protocal1_list);
        initUI();
        setListener();
        initData();
    }

    /**
     * 初始化UI
     */
    private void initUI(){
        //使用webview加载协议内容
        webView = (WebView) findViewById(R.id.id_webview);
        webView.getSettings().setDefaultTextEncodingName(ENCODING);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setBackgroundColor(Color.parseColor("#eceff6"));

        txtContent = (TextView) findViewById(R.id.id_text_context);
        layoutText = (LinearLayout)findViewById(R.id.id_text_context_layout);
        layoutText.setVisibility(View.GONE);

        WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
    }

    /**
     * 设置监听事件
     */
    private void setListener(){
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(procalWebClient);
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    back();
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //确定取消按钮是否显示
        LinearLayout bottomLayout = (LinearLayout) findViewById(R.id.id_bottom_btn_layout);
        Button cancelBtn = (Button) findViewById(R.id.id_cancel_btn);
        Button confirmBtn = (Button) findViewById(R.id.id_confim_btn);
        mAction = getIntent().getAction();
        if (mAction != null && mAction.equals(ACTION_SHOW_BUTTOM)){
            //只有从登陆过程调用此页面时，不需要走锁屏逻辑
            navigationBar.setBackBtnVisibility(View.GONE);
            bottomLayout.setVisibility(View.VISIBLE);
            cancelBtn.setOnClickListener(this);
            confirmBtn.setOnClickListener(this);
        }


        //获取协议类型
        protocalType = (EProtocalType) getIntent().getSerializableExtra(PROTOCAL_KEY);
        if (protocalType != null) {

            //根据协议类型填充webView内容
            String localpath = ProtocalUtil.getServerUrl(true,protocalType.getValue());
            if (FileUtil.isExist(localpath)){
                webView.loadUrl("file://"+localpath);
            }else {
                String remoteUrl = ProtocalUtil.getServerUrl(false,protocalType.getValue());
                webView.loadUrl(remoteUrl);
            }
            return ;
        }

        //通过BusinessLauncher调用的页面
        if(getIntent().hasExtra(BUSINESS_BUNDLE_KEY)){
            Bundle bundle = getIntent().getBundleExtra(BUSINESS_BUNDLE_KEY);
            if(null !=bundle && bundle.containsKey(DATA)){
                try{
                    JSONObject jsonObject = new JSONObject(bundle.getString(DATA));
                    navigationBar.setTitle(jsonObject.optString(PROTOCAL_TITLE));
                    webView.loadUrl("file://"+ProtocalUtil.getServerUrl(true,jsonObject.optString(PROTOCAL_URL)));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return ;
        }

        Intent intent = getIntent();
        String title = intent.getStringExtra(KEY_WEB_TITLE);
        String url = intent.getStringExtra(KEY_WEB_URL);
        String contentHTML = intent.getStringExtra(KEY_WEB_HTML);
        int type = intent.getIntExtra(KEY_TYPE,0);

        if (StringUtil.isNotEmpty(title)){
            navigationBar.setTitle(title);
        }
        switch(type){
            case 1:
                layoutText.setVisibility(View.VISIBLE);
                txtContent.setText(contentHTML);
                webView.setVisibility(View.GONE);
                break;
            default:
                if (StringUtil.isNotEmpty(url)){
                    webView.loadUrl(url);
                }

                if (StringUtil.isNotEmpty(contentHTML)){
                    webView.loadDataWithBaseURL("",contentHTML,MINE_TYPE,ENCODING,"");
                }
                break;
        }
    }


    /**
     * 根据type来填充协议
     *
     * @param type EProtocalType 枚举类型
     */
    private void setTitle(EProtocalType type) {
        if (type==null) return;
        switch (type) {
            case SERVICE_PROTOCAL://拉卡拉服务协议
                navigationBar.setTitle("拉卡拉服务协议");
                break;
            case ZDGL_PROTOCAL://账单管理使用协议
                navigationBar.setTitle("信用卡账单用户使用协议");
                break;
            case CHANGE_MOBILE_PROTOCAL://更换登陆手机号协议
                navigationBar.setTitle("更换登录手机号说明");
                break;
            case SWIPE_INTRODUCE://刷卡器使用说明
                navigationBar.setTitle("刷卡器使用帮助");
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setSupportZoom(true);
                break;
            case QUICK_PAYMENT://快捷支付协议
                navigationBar.setTitle("快捷支付协议");
                break;
            case REAL_NAME_AUTH://实名认证说明
                navigationBar.setTitle("实名认证说明");
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *处理返回逻辑
     * @return
     */
    private void back(){
        if (mAction != null && mAction.equals(ACTION_SHOW_BUTTOM))
            return;

        if (webView.canGoBack()) {
            webView.goBack();
        }else{
            finish();
        }
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        int i = view.getId();
        if (i == R.id.id_cancel_btn) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (i == R.id.id_confim_btn) {
            setResult(RESULT_OK);
            finish();
        }
    }

}
