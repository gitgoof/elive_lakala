package com.lakala.shoudan.activity.shoudan.loan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Loan;
import com.lakala.ui.component.NavigationBar;


public class LoanDetailNotLastActivity extends AppBaseActivity {

    private TextView contractNo;// 合同编号
    private TextView result;// 审核未通过
    private TextView borrowMoney;// 借款金额
    private TextView counterFee;// 手续费
    private TextView owingLoanCard;// 还贷储蓄卡
    private TextView loanInstalments;// 还贷分期
    private TextView owingLoanTime;// 到期还贷日

    private String[] types= new String[]{"审核中","放款未成功","已放款待还贷","已还清","已逾期","审核未通过"};//顺序不可变
    
    private Loan mLoan;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payforyou_loandetails_notlast);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();

        navigationBar.setTitle("交易详情");
        contractNo=(TextView) findViewById(R.id.contract_no);// 合同编号
        result=(TextView) findViewById(R.id.result);// 审核未通过        
        borrowMoney = (TextView) findViewById(R.id.borrowmoney);// 借款金额
        counterFee = (TextView) findViewById(R.id.counter_fee);// 手续费
        owingLoanCard = (TextView) findViewById(R.id.owingloan_card);// 还贷储蓄卡
        loanInstalments = (TextView) findViewById(R.id.loan_instalments);// 还贷分期
        owingLoanTime = (TextView) findViewById(R.id.owingloantime);// 到期还贷日
        showViewAndData();

    }

    private void showViewAndData() {
        Intent intent = getIntent();
        mLoan = (Loan) intent.getSerializableExtra(LoanDetailActivity.LOAN);
        //第一行
        contractNo.setText("合同编号 "+mLoan.getContractno());// 合同编号
        int contractStatusIndex = Integer.parseInt(mLoan.getContractstatus());
        result.setText(types[contractStatusIndex-1]);// 审核未通过
        borrowMoney.setText(Util.fen2Yuan(mLoan.getLoanamt()));//申请金额        
        counterFee.setText(Util.fen2Yuan(mLoan.getFee()));//手续费
        owingLoanCard.setText(Util.formatCardNumberWithStar(mLoan.getDebitcard()));//还贷储蓄卡
        loanInstalments.setText(mLoan.getPeriod()+"周");//分期
        owingLoanTime.setText(mLoan.getReturndate().substring(0, 4)+"年"
                             +mLoan.getReturndate().substring(4, 6)+"月"
                             +mLoan.getReturndate().substring(6, 8)+"日");   
        if(types[contractStatusIndex-1].equals("已还清")){
        	
        	initActionBar(mLoan);  
        }
    }   
    
    private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
		
		@Override
		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
			if(navBarItem == NavigationBar.NavigationBarItem.action){
				//立即还款
				Intent intent = new Intent(LoanDetailNotLastActivity.this,LoanRecordActivity.class);
				intent.putExtra(LoanRecordActivity.CONTRACTNO, mLoan.getContractno());
				intent.putExtra(LoanRecordActivity.AMOUNT_STILL, String.valueOf(0));
				startActivity(intent);
			}
			if(navBarItem == NavigationBar.NavigationBarItem.back){//返回键
				finish();
			}
		}
	};
    
    private void initActionBar(Loan mLoan){
        navigationBar.setActionBtnEnabled(true);
        navigationBar.setActionBtnVisibility(View.VISIBLE);
        navigationBar.setActionBtnText("还款记录");
        navigationBar.setOnNavBarClickListener(onNavBarClickListener);
    }
}
