package com.lakala.shoudan.activity.shoudan.loan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Payment;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.common.util.Util;
import com.lakala.ui.component.NavigationBar;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 替你还  还款记录
 * @author Zhangmy
 *
 */
public class LoanRecordActivity extends AppBaseActivity{
	
	public static final String CONTRACTNO = "contractno";
	public static final String AMOUNT_STILL = "amouont_still";
	
	private TextView tvStillAmount;//待还金额
	private View layoutNoResult;//无记录
	private ListView repaymentRecordList;//
	
	private String contractno;//合同编号
	private String stillAmount;//待还金额
	private List<Payment> payments = new ArrayList<Payment>();
	private LoanRecordAdapter mLoanRecordAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_repayment_record);
		initUI();
		getLoanDetails();
	}
	
	protected void initUI() {
		PublicToEvent.LoansEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Money_Detail, this);
		Intent intent = getIntent();
		contractno = intent.getStringExtra(CONTRACTNO);
		stillAmount = intent.getStringExtra(AMOUNT_STILL);
		
		navigationBar.setTitle("还款记录");
		navigationBar.setActionBtnText("立即还款");
		navigationBar.setOnNavBarClickListener(onNavBarClickListener);
		
		tvStillAmount = (TextView) findViewById(R.id.tv_still_amount);
		layoutNoResult = findViewById(R.id.layout_no_record_result);
		repaymentRecordList = (ListView) findViewById(R.id.lv_record);
		mLoanRecordAdapter = new LoanRecordAdapter(payments);
		repaymentRecordList.setAdapter(mLoanRecordAdapter);
		
		tvStillAmount.setText(Util.formatAmount(stillAmount));
	}
	
	private void showNoResult(){
		layoutNoResult.setVisibility(View.VISIBLE);
		repaymentRecordList.setVisibility(View.GONE);
	}
	
	private void showResults(){
		layoutNoResult.setVisibility(View.GONE);
		repaymentRecordList.setVisibility(View.VISIBLE);
	}
	
	private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
		
		@Override
		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
			if(navBarItem == NavigationBar.NavigationBarItem.action){
				//立即还款
				Intent intent = new Intent(LoanRecordActivity.this,LoanActivity.class);
				intent.putExtra(LoanActivity.CONTRACT, contractno);
				intent.putExtra(LoanActivity.AMOUNTSTILL, stillAmount);
				startActivity(intent);
			}
			if(navBarItem == NavigationBar.NavigationBarItem.back){//返回键
				finish();
			}
		}
	};
	
	private void getLoanDetails(){
        showProgressWithNoMsg();
        ServiceResultCallback callback = new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if(resultServices.isRetCodeSuccess()){
                    try {
                        JSONObject jsonData = new JSONObject(resultServices.retData);
                        List<Payment> list = Payment.unpackPayments(jsonData.getJSONArray("payments"));
                        payments.addAll(list);
                        if(payments.size()>0){
                            showResults();
                        }else {
                            showNoResult();
                        }

                        if(!TextUtils.isEmpty(stillAmount)){//是否需要显示“立即还款”
                            if(new BigDecimal(stillAmount).compareTo(new BigDecimal(0))==1){
                                navigationBar.setActionBtnVisibility(View.VISIBLE);
                            }else {
                                navigationBar.setActionBtnVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    showMessage(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
            }
        };
        ShoudanService.getInstance().getLoanDetails(contractno, callback);
	}
}
