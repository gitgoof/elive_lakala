package com.lakala.shoudan.activity.merchant.upgrade;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalUrl;

import java.io.Serializable;

/**
 * web+btn说明界面
 * @author Administrator
 * 升级之前需要加载和判断身份证照片
 */
public class DescriptionActivity extends AppBaseActivity implements OnClickListener{
	
	private WebView mWebView;
	private Button btnNext;
	private Button btnMer;
	private final static int REQ_CODE=1;
	
	public static final String DESCRIPTION_TYPE = "description_type";
	
	private boolean isUpToA2=true;
	
	public enum DescriptionType implements Serializable{
		MERCHANT_UP_LEVEL,//商户升档
		REMITTANCE_OPEN_APPLY,//招行大额申请
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remittance_tips);
		
		initUI();
		showDescription();
	}
	
	@Override
	protected void initUI() {
		super.initUI();
		mWebView = (WebView) findViewById(R.id.webview_tips);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		//点击链接当前页面响应
		mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
		btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(this);
		btnMer = (Button) findViewById(R.id.btn_mer);
		btnMer.setOnClickListener(this);
	}
	
	private void showDescription(){
		DescriptionType descriptionType = (DescriptionType) getIntent().getSerializableExtra(DESCRIPTION_TYPE);
		switch (descriptionType) {
		case MERCHANT_UP_LEVEL:
			navigationBar.setTitle("商户升级说明");

			mWebView.loadUrl(ProtocalUrl.DES_MERCHANT_UP_LEVEL);
			btnNext.setText("我要升档");
			//2.5.3去掉该需求
//			if(!TextUtils.isEmpty(merchantLevel) && !"CR0001".equals(merchantLevel)){//不是A1级别 
//				btnNext.setVisibility(View.GONE);
//			}
//			btnMer.setText("我要成为商户");
			btnMer.setVisibility(View.GONE);
			break;
		case REMITTANCE_OPEN_APPLY:
			navigationBar.setTitle("申请大额说明");
			 mWebView.loadUrl(ProtocalUrl.DES_REMITTANCE_OPEN_APPLY);
			btnNext.setText("我要申请");
			btnMer.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_next){
			DescriptionType descriptionType = (DescriptionType) getIntent().getSerializableExtra(DESCRIPTION_TYPE);
			switch (descriptionType) {
			case MERCHANT_UP_LEVEL://商户升档
				isUpToA2 = true;
				modifyModeInit();
				break;
			case REMITTANCE_OPEN_APPLY://招行大额
//				 Intent remittanceOpenApply = new Intent(DescriptionActivity.this,RemittanceOpenApplyActivity.class);
//				 startActivity(remittanceOpenApply);
//				 finish();
				break;
			default:
				break;
			}
		}
		if(v.getId()== R.id.btn_mer){
			isUpToA2 = false;
			modifyModeInit();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			if(requestCode==REQ_CODE){
				setResult(RESULT_OK);
				finish();
			}
		}
	}
	
	 /**
     * 先从服务器获取身份证照片
     */
    private void modifyModeInit(){
        Intent merchantUpLevel = null;
        if(isUpToA2){//升档A2
            merchantUpLevel = new Intent(DescriptionActivity.this,UpgradeResultActivity.class);
        }else {//升档A3
            merchantUpLevel = new Intent(DescriptionActivity.this,UpgradeMerchantActivity.class);
        }
        startActivity(merchantUpLevel);
    }



	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
		
}
