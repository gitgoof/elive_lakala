package com.lakala.shoudan.activity.wallet;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.lakala.library.encryption.Digest;
import com.lakala.library.encryption.Mac;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.DeviceUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.bean.User;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.Utils;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.TerminalKey;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.bankitcard.bean.QuickCardBinBean;
import com.lakala.shoudan.util.XAtyTask;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by fengx on 2015/11/19.
 */
public class WalletMBConnectActivity extends AppBaseActivity{
    private WebView mWebView;
    private Button btnNext;
    private QuickCardBinBean quickCardBinBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmb_connect);
       // balance = getIntent().getStringExtra("balance");

        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        showProgressWithNoMsg();
        mWebView = (WebView) findViewById(R.id.webview_cmb);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(getIntent().getStringExtra("yhURL"));
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);          //支持缩放
       // settings.setBuiltInZoomControls(true);  //启用内置缩放装置
        settings.setJavaScriptEnabled(true);    //启用JS脚本
        //点击链接当前页面响应
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hideProgressDialog();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                    ToastUtil.toast(context,"加载失败");
            }
        });
        btnNext = (Button) findViewById(R.id.btn_back);
        btnNext.setOnClickListener(this);
        navigationBar.setTitle("银行卡签约");
        navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
            @Override
            public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
                if (navBarItem == NavigationBar.NavigationBarItem.back) {
                    checkQYTZ();
                }
            }
        });

        quickCardBinBean = (QuickCardBinBean) getIntent().getSerializableExtra(Constants.IntentKey.QUICK_CARD_BIN_BEAN);
    }

    @Override
    public void onClick(View view) {
        checkQYTZ();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            checkQYTZ();
        }

        return false;

    }
    /**
     *
     * 1..快捷跳转签约查询
     */
    private void checkQYTZ(){
        context.showProgressWithNoMsg(); BusinessRequest request = RequestFactory
                .getRequest(context, RequestFactory.Type.CHECK_QYTZ);
        HttpRequestParams params = request.getRequestParams();
       params.put("realName",quickCardBinBean.getCustomerName() );
        params.put("accountNo", quickCardBinBean.getAccountNo());
        params.put("accountType", quickCardBinBean.getAccountType());
        params.put("mobileNum", quickCardBinBean.getMobileInBank());
        params.put("idCardNo", quickCardBinBean.getIdentifier());
        params.put("bankCode", quickCardBinBean.getBankCode());
        params.put("bankName", quickCardBinBean.getBankName());
        if (quickCardBinBean.getAccountType().equals("2")) {
            params.put("cVN2", quickCardBinBean.getCVN2());
            params.put("cardExp", quickCardBinBean.getCardExp());
        }

        User user= ApplicationEx.getInstance().getSession().getUser();
        params.put("termid",ApplicationEx.getInstance().getSession().getUser().getTerminalId());
    //    params.put("chntype",BusinessRequest.CHN_TYPE);
        params.put("chntype","99999");
        params.put("series", Utils.createSeries());
        params.put("rnd", Mac.getRnd() );
    //    params.put("mac", DeviceUtil.getMac(context));
        params.put("tdtm", Utils.createTdtm());
        params.put("gesturePwd","0");
        params.put("platform","android" );
        params.put("refreshToken", user.getRefreshToken());
        params.put("subChannelId","10000027" );
        params.put("telecode", TerminalKey.getTelecode());
        params.put("timeStamp",ApplicationEx.getInstance().getSession().getCurrentKSN() );

        String deviceId = DeviceUtil.getDeviceId(context);
        Calendar calendar = Calendar.getInstance();
        //deviceid 年月日时分秒 5位随机数
        String guid = String.format("%s%tY%tm%td%tH%tM%tS%s",
                deviceId,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                calendar,
                StringUtil.getRandom(5)
        );
        String md5Value = Digest.md5(guid);
        params.put("guid",md5Value );
        params.put("mac", BusinessRequest.getMac(params));

        request.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                context.hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(resultServices.retData);
                        XAtyTask.getInstance().killAty(WalletProcessAuthActivity.class);
                        context.finish();
                    } catch (JSONException e) {
                        LogUtil.print(e);
                    }
                } else {
                    ToastUtil.toast(context, resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                context.hideProgressDialog();
                context.toastInternetError();
                XAtyTask.getInstance().killAty(WalletProcessAuthActivity.class);
                context.finish();
            }
        });
        request.execute();
    }
}
