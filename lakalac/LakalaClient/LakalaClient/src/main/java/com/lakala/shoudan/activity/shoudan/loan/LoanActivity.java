package com.lakala.shoudan.activity.shoudan.loan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.keyboard.BasePwdAndNumberKeyboardActivity;
import com.lakala.shoudan.activity.keyboard.CustomNumberKeyboard;
import com.lakala.platform.swiper.devicemanager.controller.TransactionType;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LoanRepayTransInfo;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.MoneyInputWatcher;

/**
 * 还借款
 * @author Zhangmy
 *
 */
public class LoanActivity extends BasePwdAndNumberKeyboardActivity implements OnClickListener{
	
	public static final String CONTRACT = "contract";
	public static final String AMOUNTSTILL = "amount_still";
	
	private EditText editContract;//合同编号
	private EditText editAmountStill;//待还金额
	private EditText editRepaymentAmount;//还款金额
	private TextView btnSure;
	
	private String contractno;
	private String amountStill;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loan);
		initUI();
	}
	
	protected void initUI() {
		Intent intentData = getIntent();
		contractno = intentData.getStringExtra(CONTRACT);
		amountStill = intentData.getStringExtra(AMOUNTSTILL);

		navigationBar.setTitle("还借款");
		((TextView)findViewById(R.id.layout_contract).findViewById(R.id.id_combinatiion_text_edit_text)).setText("合同编号");
		editContract = (EditText)findViewById(R.id.layout_contract).findViewById(R.id.id_combination_text_edit_edit);
		editContract.setFocusable(false);
		editContract.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		editContract.setText(contractno);
		
		((TextView)findViewById(R.id.layout_till_amount).findViewById(R.id.id_combinatiion_text_edit_text)).setText("待还金额");
		editAmountStill = (EditText)findViewById(R.id.layout_till_amount).findViewById(R.id.id_combination_text_edit_edit);
		editAmountStill.setFocusable(true);
		editAmountStill.setEnabled(false);
		editAmountStill.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		editAmountStill.setText(amountStill);
		
		
		((TextView)findViewById(R.id.layout_loan_amount).findViewById(R.id.id_combinatiion_text_edit_text)).setText("还款金额");
		editRepaymentAmount = (EditText)findViewById(R.id.layout_loan_amount).findViewById(R.id.id_combination_text_edit_edit);
		editRepaymentAmount.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		editRepaymentAmount.setTag("amount");
		editRepaymentAmount.setHint("输入金额");
	    // 如果输入的内容是金额，则设置TextWatcher，控制小数点后只能输入两位
		editRepaymentAmount.addTextChangedListener(new MoneyInputWatcher());
		editRepaymentAmount.setOnFocusChangeListener(new OnFocusChangeListener() {
	            @Override
	            public void onFocusChange(View v, boolean hasFocus) {
	                if (v.getTag().equals("amount")) {
	                    EditText editText = (EditText) v;
	                    String amount = editText.getText().toString();
	                    if (hasFocus == false && amount.contains(".")) {
	                        amount = Util.formatAmount(amount).replace(",", "");
	                        editText.setText(amount);
	                    }else if(hasFocus == false) {
                            hideNumberKeyboard();
	                    }
	                }
	            }
	        });
        initNumberKeyboard();
        initNumberEdit(editRepaymentAmount, CustomNumberKeyboard.EDIT_TYPE_FLOAT,30);
		//确定
		btnSure = (TextView) findViewById(R.id.btn_sure);
		btnSure.setText(getResources().getString(R.string.ok));
		btnSure.setOnClickListener(this);
	}

    @Override
    protected void onNumberKeyboardVisibilityChanged(boolean isShow, int height) {
        resizeScrollView((ScrollView)findViewById(R.id.layout_scroll));
    }

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sure:
			if(validate()){
				loan();//走收款流程?
			}
			
			break;

		default:
			break;
		}
	}
	
	/**
	 * 还借款
	 */
	private void loan(){

        LoanRepayTransInfo loanRepayTransInfo = new LoanRepayTransInfo();
        loanRepayTransInfo.setAmountStill(amountStill);
        loanRepayTransInfo.setAmount(editRepaymentAmount.getText().toString().trim());
        loanRepayTransInfo.setContractNo(contractno);

        Intent intent = getIntent().setClass(this, LoanRepayPaymentActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, loanRepayTransInfo);
        intent.putExtra(Constants.IntentKey.TRANS_STATE, TransactionType.RepayForYou);
        startActivity(intent);

	}
	
	private boolean validate(){
		String repaymentAmount = editRepaymentAmount.getText().toString().trim();
		if(TextUtils.isEmpty(repaymentAmount)){
			ToastUtil.toast(context,"请输入金额");
			return false;
		}
		if(Double.parseDouble(repaymentAmount)<=0){
            ToastUtil.toast(context,"请输入金额");
			return false;
		}
		String amountStill = editAmountStill.getText().toString().trim();
		if(Double.parseDouble(amountStill)<Double.parseDouble(repaymentAmount)){
            ToastUtil.toast(context,"输入金额应小于等于待还金额");
            return false;
		}
		return true;
	}
}
