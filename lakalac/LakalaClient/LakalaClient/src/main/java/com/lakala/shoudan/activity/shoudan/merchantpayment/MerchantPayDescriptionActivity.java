package com.lakala.shoudan.activity.shoudan.merchantpayment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import android.widget.Button;
/**
 * 缴款业务说明
 * @author zmy
 *
 */
public class MerchantPayDescriptionActivity extends AppBaseActivity implements OnClickListener{
	
	private WebView mWebView;
	private Button btnNext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchantpay_decription);
		initUI();
	}
    private final String TEYUEJIAOFEI_DESCRIPTION = "https://download.lakala.com/lklmbl/html/skb_teyue.html";

	protected void initUI() {
		navigationBar.setTitle("特约商户缴费");
		
		mWebView = (WebView) findViewById(R.id.webview_description);
		btnNext = (Button) findViewById(R.id.next);
		btnNext.setOnClickListener(this);
		
		mWebView.loadUrl(TEYUEJIAOFEI_DESCRIPTION);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next://下一步//
			Intent intent = new Intent(MerchantPayDescriptionActivity.this,MerchantPayQueryInstBillActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
}
