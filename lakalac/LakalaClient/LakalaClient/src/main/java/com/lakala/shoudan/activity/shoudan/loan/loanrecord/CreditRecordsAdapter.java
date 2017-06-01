package com.lakala.shoudan.activity.shoudan.loan.loanrecord;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.platform.adapter.MyBasesAdapter;
import com.lakala.platform.common.UILUtils;
import com.lakala.shoudan.R;
import com.lakala.ui.common.SuperViewHolder;

/**
 * Created by Administrator on 2016/10/26 0026.
 */

public class CreditRecordsAdapter extends MyBasesAdapter<CreditRecordsModel> {
    private   CreditRecordsConstract.Presenter presenter;

    public CreditRecordsAdapter(Context mCotext,   CreditRecordsConstract.Presenter mData, int mItemLayoutId) {
        super(mCotext, mData.getData(), mItemLayoutId);
        UILUtils.init(mCotext);
        this.presenter=mData;
    }

    @Override
    public void convert(SuperViewHolder viewHolder, CreditRecordsModel item) {
        final ImageView iv_user=viewHolder.getView(R.id.iv_user);
        TextView tv_name=viewHolder.getView(R.id.tv_name);
        TextView tv_1=viewHolder.getView(R.id.tv_1);
        TextView tv_2=viewHolder.getView(R.id.tv_2);
        LinearLayout applay=viewHolder.getView(R.id.applay);
        LinearLayout scan=viewHolder.getView(R.id.scan);
        TextView tv_growth_rate=viewHolder.getView(R.id.tv_growth_rate);
        TextView tv_million_copies_of_income=viewHolder.getView(R.id.tv_million_copies_of_income);
        TextView tv_type=viewHolder.getView(R.id.tv_type);

        tv_million_copies_of_income.setText(item.getLoanPeriod());
        tv_growth_rate.setText(item.getApplyAmount());
        tv_2.setText(item.getApplyDate());
        tv_name.setText(item.getLoanName());
        UILUtils.display(item.getLoanLogo(), iv_user);
        String loanStatus=item.getLoanStatus();
        if (loanStatus.equals("1")){//1审核中、2已放贷、3浏览
            tv_1.setText("申请日期：");
            applay.setVisibility(View.VISIBLE);
            scan.setVisibility(View.GONE);
            setTv(tv_type,"申请中",mCotext.getResources().getColor(R.color.shape_loan_apply),mCotext.getResources().getDrawable(R.drawable.shape_credit_records_label_applayed));
        }else if (loanStatus.equals("2")){
            tv_1.setText("放贷日期：");
            applay.setVisibility(View.VISIBLE);
            scan.setVisibility(View.GONE);
            setTv(tv_type,"已放贷",mCotext.getResources().getColor(R.color.color_31b8f3),mCotext.getResources().getDrawable(R.drawable.shape_credit_records_label_loaned));
        }else if (loanStatus.equals("3")){
            tv_1.setText("浏览日期：");
            applay.setVisibility(View.GONE);
            scan.setVisibility(View.VISIBLE);
            setTv(tv_type,"浏览过",mCotext.getResources().getColor(R.color.color_80aa16),mCotext.getResources().getDrawable(R.drawable.shape_credit_records_label_skiped));
        }
    }

    private void setTv(TextView tv_type, String str, int color, Drawable drawable) {
        tv_type.setText(str);
        tv_type.setTextColor(color);
        tv_type.setBackgroundDrawable(drawable);
    }
}
