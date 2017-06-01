package com.lakala.shoudan.activity.shoudan.loan;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.communityservice.creditpayment.CreditFormInputActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanBackShowInfo;

/**
 * 受理通知
 * 
 * @author ldm
 * 
 */
public class LoanNotificationActivity extends AppBaseActivity implements
		OnClickListener {

	private TextView btnSure;
	private TextView cancel;
	
	private ImageView result_icon;
	private TextView result;
	private LoanBackShowInfo backShowInfo;
	
	private String creditCardFromRemittaqnce = "";
	
	private TextView tvFailedReason;//失败原因

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payforyou_notification);
		initUI();
	}

	protected void initUI() {
		backShowInfo = (LoanBackShowInfo) getIntent().getSerializableExtra("backShowInfo");
		result=(TextView) findViewById(R.id.result);
		result_icon=(ImageView) findViewById(R.id.result_icon);
		tvFailedReason = (TextView) findViewById(R.id.notification12);
		// 确定
		btnSure = (TextView) findViewById(R.id.next);
		cancel = (TextView) findViewById(R.id.cancel);
        navigationBar.setBackBtnVisibility(View.GONE);
		if (backShowInfo.isPass()) {//成功
			navigationBar.setTitle(getString(R.string.remit_notification));
			cancel.setVisibility(View.GONE);
			result_icon.setBackgroundResource(R.drawable.sel_btn_selected);
			findViewById(R.id.scrollview1).setVisibility(View.VISIBLE);
			findViewById(R.id.scrollview2).setVisibility(View.GONE);
		} else {//失败
			navigationBar.setTitle(getString(R.string.info_notification));
			result.setText(getString(R.string.remit_fail_nav_title));
			result_icon.setBackgroundResource(R.drawable.busy);
			btnSure.setText(getString(R.string.apply_again));
			findViewById(R.id.scrollview2).setVisibility(View.VISIBLE);
			findViewById(R.id.scrollview1).setVisibility(View.GONE);
			cancel.setOnClickListener(this);
			cancel.setVisibility(View.VISIBLE);
			//显示失败原因
			String reason = getIntent().getStringExtra(Constants.IntentKey.FAILED_REASON);
			if(TextUtils.isEmpty(backShowInfo.getLoanLimits())){
				tvFailedReason.setText(reason);
			}else {
				//超额
				tvFailedReason.setText("您的申请已被受理，但根据您的信用记录，您能够申请的借款额度被限定在"+backShowInfo.getLoanLimits()+"元 以内。");
				findViewById(R.id.notification22).setVisibility(View.VISIBLE);
			}
			
		}

		// btnSure.setBtnTextRight();
		btnSure.setOnClickListener(this);
		creditCardFromRemittaqnce = getIntent().getStringExtra(Constants.IntentKey.CREDITCARD_PAYMENT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
			if (backShowInfo.isPass()) {
				//成功申请
                callMainActivity();
			}
			else{
				//重新申请
                BusinessLauncher.getInstance().clearTop(LoanTrailActivity.class);
			}
			break;
		case R.id.cancel:
			//取消申请
			if(!TextUtils.isEmpty(creditCardFromRemittaqnce)){//由信用卡还款过来的，返回到信用卡还款
                BusinessLauncher.getInstance().clearTop(CreditFormInputActivity.class);

			}else {
				//不是来自信用卡，返回到主界面
                callMainActivity();
			}
			break;

		default:
			break;
		}
	}

    @Override
    public void onBackPressed() {

    }
}
