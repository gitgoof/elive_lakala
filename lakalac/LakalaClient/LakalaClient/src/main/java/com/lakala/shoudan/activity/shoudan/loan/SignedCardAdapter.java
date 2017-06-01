package com.lakala.shoudan.activity.shoudan.loan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.loan.datafine.Debit;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.component.ScrollBitmap;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 签约卡管理 adapter
 * @author zmy
 *
 */
public class SignedCardAdapter extends BaseAdapter{
	public List<Debit> debits;//
	private ScrollBitmap mScrollBitmap;
	private List<OpenBankInfo> openBankInfos = new ArrayList<OpenBankInfo>();

	public SignedCardAdapter(List<Debit> list,ScrollBitmap mScrollBitmap){
		this.debits = list;
		this.mScrollBitmap = mScrollBitmap;
	}

	@Override
	public int getCount() {
		return debits.size();
	}

	@Override
	public Debit getItem(int position) {
		return debits.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(null == convertView){
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list_item, null);
			viewHolder = new ViewHolder();
			convertView.findViewById(R.id.view_top).setVisibility(View.GONE);
			viewHolder.imgBank = (ImageView) convertView.findViewById(R.id.left_icon);
			viewHolder.tvFisrtLine = (TextView) convertView.findViewById(R.id.first_line_text);
			viewHolder.tvSecondLine = (TextView) convertView.findViewById(R.id.second_line_text);
			viewHolder.tvRightText = (TextView) convertView.findViewById(R.id.right_text);
			
			if(position != getCount()){
				convertView.findViewById(R.id.view_last).setVisibility(View.GONE);
			}
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Debit debit = getItem(position);
		
		showCorrespodingOpenBanInfo(parent.getContext(), debit, viewHolder);
		return convertView;
	}
	
	private void showCorrespodingOpenBanInfo(Context context, Debit debit, ViewHolder viewHolder){
		for(int i=0;i<openBankInfos.size();i++){
			
			OpenBankInfo openBankInfo = openBankInfos.get(i);
			if(debit.getDebitbank().equals(openBankInfo.bankCode)){
				Drawable drawable = ImageUtil.getDrawbleFromAssets(context,openBankInfo
                        .bankCode +
                        ".png");
				if (drawable == null){
					mScrollBitmap.loadImage(viewHolder.imgBank, openBankInfo.bankimg,true);
				}else{
					viewHolder.imgBank.setBackgroundDrawable(drawable);
                }
				viewHolder.tvFisrtLine.setText(openBankInfo.bankname+" 储蓄卡");
				viewHolder.tvSecondLine.setText(Util.formatCardNumberWithStar(debit.getDebitcard()));
				viewHolder.tvRightText.setText(status[Integer.parseInt(debit.getContractstatus())]);
				break;
			}
		}
	}
	
	public void setOpenBankInfos(List<OpenBankInfo> openBankInfos) {
		this.openBankInfos.clear();
		this.openBankInfos.addAll(openBankInfos);
	}
	
	private String[] status = {"处理中","生效","更换失败","失效"};
	
	class ViewHolder{
		public ImageView imgBank;
		public TextView tvFisrtLine;
		public TextView tvSecondLine;
		public TextView tvRightText;
	}
	
}
