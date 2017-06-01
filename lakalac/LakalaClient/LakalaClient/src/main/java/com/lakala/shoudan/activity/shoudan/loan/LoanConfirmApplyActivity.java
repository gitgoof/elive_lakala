package com.lakala.shoudan.activity.shoudan.loan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanBackShowInfo;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.CaptchaTextView;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 确认申请
 * 
 * @author ldm
 * 
 */
public class LoanConfirmApplyActivity extends AppBaseActivity implements
		OnClickListener {

	private TextView applyName;// 申请人姓名
	private TextView creditCard;// 代还信用卡
	private TextView borrowMoney;// 借款金额
	private TextView counterFee;// 手续费
	private TextView owingLoanCard;// 还贷储蓄卡
	private TextView loanInstalments;// 还贷分期
	private TextView owingLoanTime;// 到期还贷日
	private TextView btnSure;

	/** 用户协议 */
	private CheckBox checkProtocol;
	private TextView agreetex;
	
	private InputMethodManager imm;
	private String mySmsCode;
	private SharedPreferences sp;
	private CaptchaTextView btn;
	private Dialog dialog;
	private LoanBackShowInfo backShowInfo;

	private String creditCardFromRemittaqnce = "";
    private EditText smsCode;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payforyou_confirmapply);
		initUI();
		sp = PreferenceManager.getDefaultSharedPreferences(context);
	}

	protected void initUI() {

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		navigationBar.setTitle(getString(R.string.confirm_apply));
		navigationBar.setActionBtnBackground(R.drawable.btn_more_selector);
		navigationBar.setActionBtnVisibility(View.VISIBLE);
		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
		// 申请人姓名
		applyName = (TextView) findViewById(R.id.apply_name);
		// 代还信用卡
		creditCard = (TextView) findViewById(R.id.credit_card);
		// 借款金额
		borrowMoney = (TextView) findViewById(R.id.borrowmoney);
		// 手续费
		counterFee = (TextView) findViewById(R.id.counter_fee);
		// 还贷储蓄卡
		owingLoanCard = (TextView) findViewById(R.id.owingloan_card);
		// 还贷分期
		loanInstalments = (TextView) findViewById(R.id.loan_instalments);
		// 到期还贷日
		owingLoanTime = (TextView) findViewById(R.id.owingloantime);
		
		agreetex = (TextView) findViewById(R.id.scanagreement);
		agreetex.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		agreetex.getPaint().setAntiAlias(true);//防锯齿
		checkProtocol = (CheckBox) findViewById(R.id.protocol_lakala_payforyou);
		agreetex.setOnClickListener(this);
		
		setCheckListeners();
		
		// 确定
		btnSure = (TextView) findViewById(R.id.next);
		// btnSure.setBtnTextRight();
		btnSure.setOnClickListener(this);
		backShowInfo=(LoanBackShowInfo) getIntent().getSerializableExtra(Constants.IntentKey.BACK_SHOW_INFO);
		initData();
		
		creditCardFromRemittaqnce = getIntent().getStringExtra(Constants.IntentKey.CREDITCARD_PAYMENT);
	}
	

	private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {

		@Override
		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
			if (navBarItem == NavigationBar.NavigationBarItem.action) {
				new PopupWindowFactory(LoanConfirmApplyActivity.this).initPopupwindow(navigationBar);
			}
			if (navBarItem == NavigationBar.NavigationBarItem.back) {// 返回键
				finish();
			}
		}
	};

	/**
	 * 显示信息
	 */
	private void initData() {
		applyName.setText(backShowInfo.getApplicant());
		creditCard.setText(Util.formatCardNumberWithStar(backShowInfo.getCreditcard()));
		borrowMoney.setText(Util.fen2Yuan(backShowInfo.getApplyamt()+"")+"元");
		counterFee.setText(Util.fen2Yuan(""+backShowInfo.getRatefee())+"元");
		owingLoanCard.setText(Util.formatCardNumberWithStar(backShowInfo.getDebitcard()));
		loanInstalments.setText(backShowInfo.getPeriod()+"周");
		owingLoanTime.setText(backShowInfo.getDueTimeFormat2());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
			if (isInputValid()) {
				showSmsDialog();
			}
			break;
		case R.id.scanagreement:
			//协议
            ProtocalActivity.open(context, ProtocalType.LOAN_APPLY);
			break;
		default:
			break;
		}
	}

	/**
	 * 显示输入验证码的对话框
	 */
	private void showSmsDialog() {
		if (dialog == null) {
			dialog = new Dialog(this, R.style.dianyingpiao_progress_dialog_with_bg);
			LinearLayout layout = (LinearLayout) LinearLayout.inflate(context,R.layout.activity_loan_smscode, null);
			LengthFilter[] filters = { new LengthFilter(6) };
			smsCode = (EditText) layout.findViewById(R.id.code_edit);
			smsCode.setFilters(filters);
			btn = (CaptchaTextView) layout.findViewById(R.id.get_btn);
            TextView btnCancel = (TextView) layout.findViewById(R.id.btn_cancel);
			btnCancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();	
					btn.resetCaptchaDown();
				}
			});
			btnCancel.setText("取消");

            TextView btnSure = (TextView) layout.findViewById(R.id.btn_sure);
			btnSure.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String smsCodeValue = smsCode.getText().toString().trim();
					if (TextUtils.isEmpty(smsCodeValue)) {
						ToastUtil.toast(context,R.string.code_null);
						return;
					} else if (smsCodeValue.length() != 6) {
                        ToastUtil.toast(context,R.string.vercode_error);
						return;
					}
					btn.resetCaptchaDown();
					mySmsCode = smsCode.getText().toString().trim();
					dialog.dismiss();
					startApply();
//					mHandler.sendEmptyMessage(START_APPLY);
				}
			});
			btnSure.setText("确定");
			TextView info = (TextView) layout.findViewById(R.id.info);
			info.setText("已向 " + Util.formatPhoneStart3End4(ApplicationEx.getInstance().getUser().getLoginName()) + " 发送短信验证码，请填写正确的验证码，有效期10分钟");
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					getSmsCode();
				}
			});
		
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(layout);
		}else{
            if(smsCode != null){
                smsCode.setText("");
            }
        }
		getSmsCode();
//		counter = new TimeCounter(60000, 1000);
//		counter.start();
		dialog.show();
	}
	
	/**
	 * 获取验证码
	 */
	protected void getSmsCode() {
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                if (resultServices.isRetCodeSuccess()) {
                    ToastUtil.toast(context,"验证码发送成功");
                } else {
                    ToastUtil.toast(context,resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                LogUtil.print(connectEvent.getDescribe());
                ToastUtil.toast(context,"验证码获取失败，请重新获取");
            }
        };
        ShoudanService.getInstance().getSmsCode(backShowInfo.getOrderno(), callback);// 订单号
        if(btn != null){
            btn.startCaptchaDown(60);
        }
	}

	/**
	 * 提交申请
	 */
	private void startApply() {
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    String orderno = null;
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        orderno = jsonObject.getString("orderno");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    backShowInfo.setOrderno(orderno);
//						backShowInfo.setApplyErr(false);//申请成功
                    backShowInfo.setPass(true);
                } else {
//						backShowInfo.setApplyErr(true);//申请失败
                    backShowInfo.setPass(false);//申请失败
                    showMessage(resultServices.retMsg);
                }
                final String errMsg = resultServices.retMsg;
                Intent intent=new Intent(LoanConfirmApplyActivity.this,LoanNotificationActivity.class);
                intent.putExtra(Constants.IntentKey.BACK_SHOW_INFO,backShowInfo);
                intent.putExtra(Constants.IntentKey.CREDITCARD_PAYMENT, creditCardFromRemittaqnce);
                intent.putExtra(Constants.IntentKey.FAILED_REASON, errMsg);
                startActivity(intent);
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        };
        ShoudanService.getInstance()
                      .loanApply(backShowInfo.getOrderno(), "" + backShowInfo.getApplyamt(),
                                 "" + backShowInfo.getPeriod(), mySmsCode,callback
                      );
	}

	protected boolean isInputValid() {
		if(!checkProtocol.isChecked()){
			ToastUtil.toast(context,"请查看并同意协议");
			return false;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 为checkbox设置监听器
	 */
	private void setCheckListeners(){
		checkProtocol.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {// 是否同意拉卡拉服务协议及是否可以点击下一步
				if (isChecked) {
					btnSure.setEnabled(true);
//					btnSure.setBackgroundResource(R.drawable.orange_btn_bg);
				} else {
					btnSure.setEnabled(false);
//					btnSure.setBackgroundResource(R.drawable.disable_btn_bg);
				}
			}
		});
	}
	
//	public String getMobileNo() {
//		return sp.getString(UploadKey.MOBILE_NO, "");
//	}
}
