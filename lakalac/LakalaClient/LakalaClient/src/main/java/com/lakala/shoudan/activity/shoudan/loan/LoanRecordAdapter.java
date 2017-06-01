package com.lakala.shoudan.activity.shoudan.loan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Payment;
/**
 * 还款记录adapter
 * @author zmy
 *
 */
class LoanRecordAdapter extends BaseAdapter{
	private List<Payment> payments;
	
	private Map<String,String> payTypes = new HashMap<String, String>();
	
	{
		payTypes.put("PR", "刷卡还款");
		payTypes.put("DS", "在线还款");
		payTypes.put("ZX", "钱包余额还款");
		payTypes.put("ZJ", "积分还款");
		payTypes.put("ZH", "红包还款");
		payTypes.put("XJ", "组合还款(余额+积分)");
		payTypes.put("XH", "组合还款(余额+红包)");
		payTypes.put("WK", "无卡还款");
		payTypes.put("BD", "绑定银行卡还款");

	}
	
	public LoanRecordAdapter(List<Payment> payments){
		this.payments = payments;
	}

	@Override
	public int getCount() {
		return payments.size();
	}

	@Override
	public Payment getItem(int position) {
		return payments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(null == convertView){
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loanrecord_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvFirstLeft = (TextView) convertView.findViewById(R.id.tv_first_left);
			viewHolder.tvFirstRight = (TextView) convertView.findViewById(R.id.tv_first_right);
			viewHolder.tvSecondLeft = (TextView) convertView.findViewById(R.id.tv_second_left);
			viewHolder.tvSecondRight = (TextView) convertView.findViewById(R.id.tv_second_right);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Payment payment = getItem(position);
		viewHolder.tvFirstLeft.setText(payTypes.get(payment.getPaytype()));
		viewHolder.tvFirstRight.setText(Util.fen2Yuan(payment.getPayamt()));
		viewHolder.tvSecondLeft.setText(Util.formateDateSpecial(payment.getPaytime(),"yyyy/MM/dd","yyyy-MM-dd"));
		viewHolder.tvSecondRight.setText("成功");
		
		return convertView;
	}
	
	class ViewHolder{
		public TextView tvFirstLeft;
		public TextView tvFirstRight;
		public TextView tvSecondLeft;
		public TextView tvSecondRight;
	}
	
}
