package com.lakala.shoudan.activity.shoudan.finance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.finance.bean.ValidprodApplyprodInfo;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by fengx on 2015/10/9.
 */
public class FinancialAssetsAdapter extends BaseAdapter{

    private Context context;
    private List<ValidprodApplyprodInfo> data;
    final int TYPE_CURRENT = 0;
    final int TYPE_REGULAR = 1;

    public FinancialAssetsAdapter(Context context, List<ValidprodApplyprodInfo> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        int prodType = data.get(position).getProdType();
        if (prodType == 0){
            return TYPE_CURRENT;
        }else{
            return TYPE_REGULAR;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHodler holder1 = null;
        ViewHodler holder2 = null;

        int type = getItemViewType(i);
        if (view == null){
            switch (type){
                case TYPE_CURRENT:
                    view = LayoutInflater.from(context).inflate(R.layout.financial_current_item,null);
                    holder1 = new ViewHodler();
                    holder1.productName = (TextView) view.findViewById(R.id.tv_product_name);
                    holder1.finance = (TextView) view.findViewById(R.id.tv_own_finance);
                    holder1.yestodayProfit = (TextView) view.findViewById(R.id.tv_yestoday_profit);
                    holder1.sevenProfit = (TextView) view.findViewById(R.id.tv_seven_profit_rate);
                    holder1.tenThousandProfit = (TextView) view.findViewById(R.id.tv_ten_thousand_profit);
                    view.setTag(holder1);
                    break;

                case TYPE_REGULAR:
                    view = LayoutInflater.from(context).inflate(R.layout.financial_regular_item,null);
                    holder2 = new ViewHodler();
                    holder2.productName = (TextView) view.findViewById(R.id.tv_product_name);
                    holder2.period = (TextView) view.findViewById(R.id.tv_period);
                    holder2.finance = (TextView) view.findViewById(R.id.tv_own_finance);
                    holder2.hopeProfit = (TextView) view.findViewById(R.id.tv_hope_profit);
                    holder2.hopeProfitRate = (TextView) view.findViewById(R.id.tv_hope_profit_rate);
                    holder2.limitTime = (TextView) view.findViewById(R.id.tv_limittime);
                    holder2.limitTimeTxt = (TextView) view.findViewById(R.id.tv_limittime_txt);
//                    holder2.tvNext = (ImageView) view.findViewById(R.id.tv_next);
                    view.setTag(holder2);
                    break;
            }
        }else{
            switch (type){
                case TYPE_CURRENT:
                    holder1 = (ViewHodler) view.getTag();
                    break;

                case TYPE_REGULAR:
                    holder2 = (ViewHodler) view.getTag();
                    break;
            }

        }
        ValidprodApplyprodInfo info = data.get(i);
        String patten = "#0.00";
        DecimalFormat format = new DecimalFormat();
        format.applyPattern(patten);
        switch (type){
            case TYPE_CURRENT:
                holder1.productName.setText(info.getProdName());
                holder1.finance.setText(String.valueOf(Util.formatTwo(info.getTotalAsset())));
                holder1.yestodayProfit.setText(String.valueOf(Util.formatTwo(info.getDayIncome())));
                holder1.sevenProfit.setText(String.valueOf(info.getGrowthRate()));
                holder1.tenThousandProfit.setText(String.valueOf(info.getIncomeW()));

                break;

            case TYPE_REGULAR:

                holder2.productName.setText(info.getProdName());
                holder2.period.setText(info.getPeriodName());
                holder2.finance.setText(String.valueOf(Util.formatTwo(info.getPrincipal())));
                holder2.hopeProfit.setText(String.valueOf(Util.formatTwo(info.getPredictIncome())));
                holder2.hopeProfitRate.setText(String.valueOf(info.getYearOrofit()));
//                holder2.tvNext.setVisibility(View.VISIBLE);
                holder2.limitTime.setTextColor(context.getResources().getColor(R.color.black));
                holder2.limitTimeTxt.setText("本期到期时间");

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                Date date=new Date();
                String today=simpleDateFormat.format(date);//今天的日期
                String EndDate=(info.getEndDate()).substring(0,10);
                try {
                    Date todayDate=simpleDateFormat.parse(today);
                    Date LiquiEndDate=simpleDateFormat.parse(EndDate);
                    int leftDay=Util.getGapCount(todayDate, LiquiEndDate);
                    if(leftDay<=0){
                        holder2.limitTime.setText("已到期，收益计算中");
                    }else{
                        String[] array = info.getEndDate().split(" ");
                        holder2.limitTime.setText(array[0]);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int prodState=info.getProdState();
                if(prodState==0 || prodState==1 || prodState==2){
                    holder2.limitTimeTxt.setText("产品状态");
                    holder2.limitTime.setText("暂未成立");
                    holder2.limitTime.setTextColor(context.getResources().getColor(R.color.amount_color));
//                    holder2.tvNext.setVisibility(View.INVISIBLE);
//                    view.setEnabled(false);
                }

                break;
        }

        return view;
    }

    class ViewHodler{
        TextView productName;
        TextView period;
        TextView finance;
        TextView hopeProfit;
        TextView yestodayProfit;
        TextView sevenProfit;
        TextView hopeProfitRate;
        TextView tenThousandProfit;
        TextView limitTime;
        TextView limitTimeTxt;
//        ImageView tvNext;
    }
}
