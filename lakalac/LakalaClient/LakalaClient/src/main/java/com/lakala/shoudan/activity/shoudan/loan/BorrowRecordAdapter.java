package com.lakala.shoudan.activity.shoudan.loan;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Loan;
/**
 * 借款记录adapter
 * @author zmy
 *
 */
public class BorrowRecordAdapter extends BaseAdapter{

	private List<Loan> loans;
	
	public BorrowRecordAdapter(List<Loan> loans){
		this.loans = loans;
	}
	
	@Override
	public int getCount() {
		return loans.size();
	}

	@Override
	public Loan getItem(int position) {
		return loans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(null == convertView){
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_borrow_record_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvFirstLeft = (TextView) convertView.findViewById(R.id.layout_loan).findViewById(R.id.tv_first_left);
			viewHolder.tvFirstRight = (TextView) convertView.findViewById(R.id.layout_loan).findViewById(R.id.tv_first_right);
			viewHolder.tvSecondLeft = (TextView) convertView.findViewById(R.id.layout_loan).findViewById(R.id.tv_second_left);
			viewHolder.tvSecondRight = (TextView) convertView.findViewById(R.id.layout_loan).findViewById(R.id.tv_second_right);
		
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Loan loan = getItem(position);
		viewHolder.tvFirstLeft.setText(loan.getContractno());//只显示合同号
		viewHolder.tvFirstRight.setText("借款 "+Util.fen2Yuan(loan.getLoanamt()));
		
//		viewHolder.tvSecondLeft.setText(Util.formateDateSpecial(loan.getApplytime(),"yyyyMMddhhmmss","MM-dd"));
		viewHolder.tvSecondLeft.setText(Util.formateDateSpecial(loan.getApplytime(),"yyyyMMddhhmmss","yyyy-MM-dd"));
		//1.审核中2.放款失败3.放款成功4.已还清5.已逾期6.审核失败
		int contractsStatus = Integer.parseInt(loan.getContractstatus());
		viewHolder.tvSecondRight.setText(types[contractsStatus-1]);
		
		return convertView;
	}
	
	private String[] types= new String[]{"审核中","放款未成功","已放款待还贷","已还清","已逾期","审核未通过"};
	class ViewHolder{
		public TextView tvFirstLeft;
		public TextView tvFirstRight;
		public TextView tvSecondLeft;
		public TextView tvSecondRight;
	}
	
}
