package com.lakala.shoudan.activity.shoudan.loan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LastApplyInfo;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LastApplyInfo.Unpaidamts;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Loan;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import java.io.Serializable;
import java.util.List;

/**
 * 借款详情
 * 
 * @author ldm
 * 
 */
public class LoanDetailActivity extends AppBaseActivity implements
		OnClickListener {
	
	public static final String LOAN = "loan";
	public static final String LAST_APPLY_LOAN = "last_apply_loan";
	
	private static final String REAPPLY = "重新申请";//重新申请
	private static final String IMMEDIATE_REPAYMENT = "立即还款";//立即还款
	
	private TextView contractNo;// 合同编号
	private TextView result;// 审核未通过
	private TextView reason;// 原因
	// first_column
	private TextView allBorrowMoney;// 借款金额
	private TextView amountMoney;// 代还金额
	private TextView dueDate;// 到期还款日
	// second_column
	private TextView overduePenalty;// 逾期罚息
	private TextView overdueFine;// 滞纳金

	private TextView borrowMoney;// 借款金额
	private TextView counterFee;// 手续费
	private TextView owingLoanCard;// 还贷储蓄卡
	private TextView loanInstalments;// 还贷分期
	private TextView owingLoanTime;// 到期还贷日
	private TextView btnSure;

	private InputMethodManager imm;
	private String[] types= new String[]{"审核中","放款未成功","已放款待还贷","已还清","已逾期","审核未通过"};
	//状态
	private enum ContractStatus implements Serializable{
	    AUDIT(1,"审核中"),
        LOAN_FAIL(2,"放款未成功"),
        LOAN_SUCCESS(3,"已放款待还贷"),
        REPAID(4,"已还清"),
        OVERDUE(5,"已逾期"),
        AUDIT_FAIl(6,"审核未通过");
                		
		public int type;
		public String typeDes;
		ContractStatus(int type,String typeDes){
			this.type = type;
			this.typeDes = typeDes;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payforyou_loandetails);
		initUI();
	}

	protected void initUI() {

		imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

		navigationBar.setTitle("交易详情");
		contractNo=(TextView) findViewById(R.id.contract_no);// 合同编号
		result=(TextView) findViewById(R.id.result);// 审核未通过
		reason=(TextView) findViewById(R.id.reason);// 原因
		allBorrowMoney=(TextView) findViewById(R.id.all_borrowmoney);// 借款金额
		amountMoney=(TextView) findViewById(R.id.amountmoney);// 代还金额 ?
		dueDate=(TextView) findViewById(R.id.due_date);// 到期还款日
		overduePenalty=(TextView) findViewById(R.id.overdue_penalty);// 逾期罚息
		overdueFine=(TextView) findViewById(R.id.overdue_fine);// 滞纳金
		borrowMoney = (TextView) findViewById(R.id.borrowmoney);// 借款金额
		counterFee = (TextView) findViewById(R.id.counter_fee);// 手续费
		owingLoanCard = (TextView) findViewById(R.id.owingloan_card);// 还贷储蓄卡
		loanInstalments = (TextView) findViewById(R.id.loan_instalments);// 还贷分期
		owingLoanTime = (TextView) findViewById(R.id.owingloantime);// 到期还贷日
		// 确定
		btnSure = (TextView) findViewById(R.id.next);
		btnSure.setOnClickListener(this);
		
		showViewAndData();
		//reason  显示情况：放款未成功，审核未通过
		//first_column  显示情况：已放款待还贷，已逾期，已还清
		//second_column 显示情况：已逾期
		//info  显示情况：已放款待还贷，已逾期
	}
	
	private LastApplyInfo mLastApplyInfo;
	private Loan mLoan;

	private void showViewAndData() {
		Intent intent = getIntent();
		if(intent.getSerializableExtra(LAST_APPLY_LOAN) == null || intent.getSerializableExtra(LOAN) == null){
			return;
		}
		mLastApplyInfo = (LastApplyInfo) intent.getSerializableExtra(LAST_APPLY_LOAN);
		mLoan = (Loan) intent.getSerializableExtra(LOAN);
		//第一行
		contractNo.setText("合同编号 "+mLastApplyInfo.getContractno());// 合同编号
		int contractStatusIndex = Integer.parseInt(mLastApplyInfo.getContractstatus());
		String contractstatus = types[contractStatusIndex-1];//状态
		result.setText(types[contractStatusIndex-1]);// 审核未通过
		//第二行 失败原因
		if(contractstatus.equals(ContractStatus.AUDIT_FAIl.typeDes) 
				|| contractstatus.equals(ContractStatus.LOAN_FAIL.typeDes)){
			showFailedReason(mLastApplyInfo);
		}
		//第三行
		if(contractstatus.equals(ContractStatus.LOAN_SUCCESS.typeDes) 
				|| contractstatus.equals(ContractStatus.REPAID.typeDes) 
				|| contractstatus.equals(ContractStatus.OVERDUE.typeDes)){
			showLoanStill(mLastApplyInfo);
		}
		//第三行
		if(contractstatus.equals(ContractStatus.OVERDUE.typeDes)){
			showOverdueInfo(mLastApplyInfo);
		}
		
		//第四行
		
		borrowMoney.setText(Util.fen2Yuan(mLastApplyInfo.getLoanamt())+"元");//借款金额
		counterFee.setText(Util.fen2Yuan(mLoan.getFee())+"元");//手续费
		owingLoanCard.setText(Util.formatCardNumberWithStar(mLastApplyInfo.getDebitcard()));//还贷储蓄卡
		loanInstalments.setText(mLoan.getPeriod()+"周");//分期
		if(mLastApplyInfo.getPaytime() != null){
			owingLoanTime.setText(mLastApplyInfo.getPaytime().substring(0, 4)+"年"
			                     +mLastApplyInfo.getPaytime().substring(4, 6)+"月"
			                     +mLastApplyInfo.getPaytime().substring(6, 8)+"日");
		}
		if(contractstatus.equals(ContractStatus.LOAN_SUCCESS.typeDes)
				|| contractstatus.equals(ContractStatus.OVERDUE.typeDes)){
			showInfo();
		}
		
		if(contractstatus.equals(ContractStatus.AUDIT_FAIl.typeDes) 
				|| contractstatus.equals(ContractStatus.LOAN_FAIL.typeDes)){
			btnSure.setText(REAPPLY);
			showBottomBtn();
		}
		
		if(contractstatus.equals(ContractStatus.OVERDUE.typeDes) 
				|| contractstatus.equals(ContractStatus.LOAN_SUCCESS.typeDes)){
			btnSure.setText(IMMEDIATE_REPAYMENT);
			showBottomBtn();
		}
		
		initActionBar(mLastApplyInfo);	
	}	
	
	//失败原因
	private void showFailedReason(LastApplyInfo mLastApplyInfo){
//        String msg = mLastApplyInfo.getFailreason();
//        if(!TextUtils.isEmpty(msg)){
//            reason.setVisibility(View.VISIBLE);
//            reason.setText(msg);// 原因
//        }
	}
	//提示语
	private void showInfo(){
		findViewById(R.id.info).setVisibility(View.VISIBLE);
	}
	//底部按钮
	private void showBottomBtn(){
		btnSure.setVisibility(View.VISIBLE);
	}
	//借款，待还金额,到期还款日
	private void showLoanStill(LastApplyInfo mLastApplyInfo){
		findViewById(R.id.first_column).setVisibility(View.VISIBLE);
		allBorrowMoney.setText(Util.fen2Yuan(mLastApplyInfo.getLoanamt()));// 借款金额
		amountMoney.setText(getStillPaymentAmount());// 代还金额
		if(mLastApplyInfo.getPaytime() != null ){
			dueDate.setText("到期还款日 "+mLastApplyInfo.getPaytime().substring(0, 4)+"/"
			               +mLastApplyInfo.getPaytime().substring(4, 6)+"/"
			               +mLastApplyInfo.getPaytime().substring(6, 8));
		}
	}
	
	private void showOverdueInfo(LastApplyInfo mLastApplyInfo){
		overdueFine.setText("0.00元");
		overduePenalty.setText("0.00元");//设置默认金额
		findViewById(R.id.second_column).setVisibility(View.VISIBLE);
		List<Unpaidamts> unpaidamts = mLastApplyInfo.getUnpaidamts();
		for(int i=0;i<unpaidamts.size();i++){
			Unpaidamts mUnpaidamts = unpaidamts.get(i);
			switch (Integer.parseInt(mUnpaidamts.getType())) {
			case 0://本金
				
				break;
			case 2://滞纳金
				overdueFine.setText(Util.fen2Yuan(mUnpaidamts.getCapital()));//
				break;
			case 3://罚息
				overduePenalty.setText(Util.fen2Yuan(mUnpaidamts.getCapital()));
				break;
				
			default:
				break;
			}
		}
	}
	
	/**
	 * 计算应还款金额
	 * @return
	 */
	private String getStillPaymentAmount(){
		int stillPaymentAmount = 0;
		List<Unpaidamts> unpaidamts = mLastApplyInfo.getUnpaidamts();
		for(int i=0;i<unpaidamts.size();i++){
			Unpaidamts mUnpaidamts = unpaidamts.get(i);
			switch (Integer.parseInt(mUnpaidamts.getType())) {
			case 0://本金
			case 2://滞纳金
			case 3://罚息
				stillPaymentAmount+=Integer.parseInt(mUnpaidamts.getCapital());
				break;
			default:
				break;
			}
		}
		return Util.fen2Yuan("" + stillPaymentAmount);
	}
	
	private void initActionBar(final LastApplyInfo lastApplyInfo){
		int contractstatusIndex = Integer.parseInt(lastApplyInfo.getContractstatus());
		final String contractstatus = types[contractstatusIndex-1];
		if(contractstatus.equals(ContractStatus.LOAN_SUCCESS.typeDes) 
				|| contractstatus.equals(ContractStatus.OVERDUE.typeDes)
				){
			//放款成功，已逾期 ，需要显示：还款记录，签约卡管理
			navigationBar.setActionBtnVisibility(View.VISIBLE);
			navigationBar.setActionBtnEnabled(true);
			navigationBar.setActionBtnBackground(R.drawable.btn_more_selector);
		}else if(contractstatus.equals(ContractStatus.REPAID.typeDes)){
			navigationBar.setActionBtnEnabled(true);
			navigationBar.setActionBtnVisibility(View.VISIBLE);
			navigationBar.setActionBtnText("还款记录");
		}
		
		navigationBar.setOnNavBarClickListener(new NavigationBar.OnNavBarClickListener() {
			
			@Override
			public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
				if(navBarItem == NavigationBar.NavigationBarItem.back){
					finish();
				}else if(navBarItem == NavigationBar.NavigationBarItem.action){
					if(contractstatus.equals(ContractStatus.LOAN_SUCCESS.typeDes) 
							|| contractstatus.equals(ContractStatus.OVERDUE.typeDes)
							){
						new PopupWindowFactory(LoanDetailActivity.this).initPopupwindow(navigationBar, mLastApplyInfo, getStillPaymentAmount());
					}else if(contractstatus.equals(ContractStatus.REPAID.typeDes)){
						loanRecord();
					}
				}
			}
		});
	}
	
	//还款记录
	private void loanRecord(){
		Intent intent = new Intent(LoanDetailActivity.this,LoanRecordActivity.class);
		intent.putExtra(LoanRecordActivity.CONTRACTNO, mLastApplyInfo.getContractno());
		intent.putExtra(LoanRecordActivity.AMOUNT_STILL, getStillPaymentAmount());
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.next:
            String text = ((TextView)v).getText().toString().trim();
			if(text.equals(REAPPLY)){
                BusinessLauncher.getInstance().clearTop(LoanTrailActivity.class);
			}else {
				Intent intent = getIntent().setClass(LoanDetailActivity.this,LoanActivity.class);
				intent.putExtra(LoanActivity.CONTRACT, mLoan.getContractno());
				intent.putExtra(LoanActivity.AMOUNTSTILL, getStillPaymentAmount());//待还金额
				startActivity(intent);
			}

			break;

		default:
			break;
		}
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
}
