package com.lakala.shoudan.activity.merchant.upgrade;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

import android.widget.TextView;

public class UpgradeInfoActivity extends AppBaseActivity implements View.OnClickListener{
	private TextView next;
	private final static int REQ_CODE=1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_merchant_upgradeinfo);
		initUI();

	}

	protected void initUI() {
		navigationBar.setTitle("商户升档说明");
		next = (TextView) findViewById(R.id.infonext);
		next.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		//页面只有一个按钮
		startActivityForResult(new Intent(UpgradeInfoActivity.this,UpgradeResultActivity.class), REQ_CODE);
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
}
