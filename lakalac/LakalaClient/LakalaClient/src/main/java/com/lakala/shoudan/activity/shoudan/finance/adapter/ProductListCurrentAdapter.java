package com.lakala.shoudan.activity.shoudan.finance.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;

import java.util.List;

/**
 * Created by HJP on 2015/9/15.
 */
public class ProductListCurrentAdapter extends BaseAdapter{
    Context context;
    List<ProductInfo> financeProductList;
    public ProductListCurrentAdapter(Context context,List<ProductInfo>financeProductList){
        this.context=context;
        this.financeProductList=financeProductList;
    }
    @Override
    public int getCount() {
        return financeProductList==null?0:financeProductList.size();
    }

    @Override
    public ProductInfo getItem(int i) {
        return financeProductList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view ==null){
            view= LayoutInflater.from(context).inflate(R.layout.shoudan_productlist_current_item,null);
            viewHolder=new ViewHolder();
            viewHolder.tvCurrentName=(TextView)view.findViewById(R.id.tv_current_name);
            viewHolder.tvMillionCopiesIncome=(TextView)view.findViewById(R.id.tv_million_copies_of_income);
            viewHolder.tvGrowthRate=(TextView)view.findViewById(R.id.tv_growth_rate);

            viewHolder.tvIsanytime=(TextView)view.findViewById(R.id.tv_isanytime);
            viewHolder.tvStartAmount=(TextView)view.findViewById(R.id.tv_start_amount);
            viewHolder.tvRisk=(TextView)view.findViewById(R.id.tv_risk);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }

        ProductInfo currentFinanceProduct=financeProductList.get(i);
        viewHolder.tvCurrentName.setText(currentFinanceProduct.getProdName());
        viewHolder.tvMillionCopiesIncome.setText(String.valueOf(currentFinanceProduct.getIncomeW()));
        viewHolder.tvGrowthRate.setText(String.valueOf(currentFinanceProduct.getGrowthRate()));

        int isAnytime=currentFinanceProduct.getIsAnytime();
        if(isAnytime==1){
            viewHolder.tvIsanytime.setText("随存随取");
            viewHolder.tvIsanytime.setVisibility(View.VISIBLE);
        }
        int risk=currentFinanceProduct.getRiskLevel();
        if(risk==0){
            viewHolder.tvRisk.setText("低风险");
        }
        viewHolder.tvStartAmount.setText(currentFinanceProduct.getStartAmout()+"元起购");

        return view;
    }
    static class ViewHolder{
        TextView tvCurrentName;
        TextView tvMillionCopiesIncome;
        TextView tvGrowthRate;
        TextView tvIsanytime;
        TextView tvStartAmount;
        TextView tvRisk;
    }
}
