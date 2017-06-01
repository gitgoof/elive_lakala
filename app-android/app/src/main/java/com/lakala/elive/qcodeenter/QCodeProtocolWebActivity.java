package com.lakala.elive.qcodeenter;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import com.lakala.elive.R;
import com.lakala.elive.merapply.activity.BaseActivity;

/**
 * Q码商户协议
 */
public class QCodeProtocolWebActivity extends BaseActivity {

    private ImageView back;
    private WebView  webView;

    @Override
    protected void setContentViewId() {
        setContentView(R.layout.activity_qcode_protocelweb);
    }

    @Override
    protected void bindView() {
        tvTitleName.setText("Q码收款服务协议");
        back = findView(R.id.btn_iv_back);
        back.setVisibility(View.VISIBLE);
        webView = (WebView) findViewById(R.id.qcode_protel_webView);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        loadWeb("QcodeRule.html");
    }

    private void loadWeb(String url){
        webView.loadUrl(" file:///android_asset/"+url);
    }

    @Override
    protected void bindEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void bindData() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_iv_back:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
