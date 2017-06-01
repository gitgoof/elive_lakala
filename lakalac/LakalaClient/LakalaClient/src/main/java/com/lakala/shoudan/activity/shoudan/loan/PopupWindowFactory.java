package com.lakala.shoudan.activity.shoudan.loan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.statistic.PublicToEvent;
import com.lakala.platform.statistic.ShoudanStatisticManager;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.protocal.ProtocalActivity;
import com.lakala.shoudan.activity.protocal.ProtocalType;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.loan.datafine.LastApplyInfo;
/**
 * 替你还  popupwindow
 * @author ZhangMY
 *
 */
public class PopupWindowFactory {
	
	private Context context;
	private PopupWindow popupWindow;
	private View view;
	
	public PopupWindowFactory(Context context){
		this.context = context;
	}

	public void initPopupwindow(View v){
		view=v;
		this.initPopupwindow(v, null, null);
	}
	
	public void initPopupwindow(View v,final LastApplyInfo mLastApplyInfo,final String stillAmount){
		popupWindow = new PopupWindow(v,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(false);
		final View contentView = LayoutInflater.from(context).inflate(R.layout.layout_popupwindow_loan_detail, null);
		
		if(null == mLastApplyInfo){//显示的时历史记录和业务说明
			TextView tvLoanRecord = (TextView) contentView.findViewById(R.id.tv_loan_record);
			TextView tvSignedCardManager = (TextView) contentView.findViewById(R.id.tv_signed_card_manager);
			tvLoanRecord.setText(context.getResources().getString(R.string.loan_history_record));
			tvSignedCardManager.setText(context.getResources().getString(R.string.description_of_business));
			
			ImageView imgFirst = (ImageView) contentView.findViewById(R.id.img_first);
			ImageView imgSecond = (ImageView) contentView.findViewById(R.id.img_second);
			imgFirst.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_history));
			imgSecond.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_business_description));
			
		}
		final View layoutFirst = contentView.findViewById(R.id.layout_first);
		final View layoutSecond = contentView.findViewById(R.id.layout_second);
		layoutFirst.post(new Runnable() {

			@Override
			public void run() {
				layoutFirst.setLayoutParams(new LinearLayout.LayoutParams(Util.dip2px(123,context),Util.dip2px(42,context)));
//				LogUtil.print("popupWindow","\nlayoutSecond.getWidth():"+layoutSecond.getWidth()
//				+"\nlayoutSecond.getHeight():"+layoutSecond.getHeight()
//				+"\nlayoutFirst.getWidth():"+layoutFirst.getWidth()
//				+"\nlayoutFirst.getHeight():"+layoutFirst.getHeight()
//				+"\n"+Util.px2dip(layoutFirst.getWidth(),context)
//				+"\n"+Util.px2dip(layoutFirst.getHeight(),context));
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutSecond.getWidth(), Util.dip2px(1, context));
				contentView.findViewById(R.id.view_sep_first).setLayoutParams(params);
				contentView.findViewById(R.id.view_sep_second).setLayoutParams(params);

			}
		});
		layoutFirst.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				PublicToEvent.LoansEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Money_Record, context);
				if(null != mLastApplyInfo && null != stillAmount){
					loanRecord(mLastApplyInfo, stillAmount);
				}else {
                    LoanHistoryActivity.open((AppBaseActivity) context);
					
				}
			}
		});
		layoutSecond.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				PublicToEvent.LoansEvent(ShoudanStatisticManager.Loan_Pay_Yor_Yo_Money_Info, context);
				//使用帮助
				if(null != mLastApplyInfo){
					signedCardManager(mLastApplyInfo);
				}else {
                    ProtocalActivity.open(context, ProtocalType.LOAN_HELP);
				}
			}
		});
		popupWindow.setAnimationStyle(R.style.popup_anim_style);
		popupWindow.setContentView(contentView);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.white));
		popupWindow.setOutsideTouchable(true);

	}
	public void show(){
		popupWindow.showAsDropDown(view, view.getRight(), 0);
	}
	//还款记录
	private void loanRecord(LastApplyInfo mLastApplyInfo,String stillPaymentAmount){
		Intent intent = new Intent(context,LoanRecordActivity.class);
		intent.putExtra(LoanRecordActivity.CONTRACTNO, mLastApplyInfo.getContractno());
		intent.putExtra(LoanRecordActivity.AMOUNT_STILL, stillPaymentAmount);
		context.startActivity(intent);
	}
	//签约卡管理
	private void signedCardManager(LastApplyInfo mLastApplyInfo){
		Intent intent = new Intent(context,SignedCardManagerActivity.class);
		intent.putExtra(SignedCardManagerActivity.CONTRACTNO, mLastApplyInfo.getContractno());
		context.startActivity(intent);
	}
}
