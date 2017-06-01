package com.lakala.shoudan.activity.shoudan.finance.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.shoudan.finance.bean.ProductInfo;

import java.util.List;


/**
 * Created by HJP on 2015/9/15.
 */
public class ProductListRegularAdapter extends BaseAdapter{
    Context context;
    List<ProductInfo> financeProductList;
    int prodState;
    private int isIsAllow;
    public ProductListRegularAdapter (Context context,List<ProductInfo> financeProductList){
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

        ViewHolder viewHolder = null;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.shoudan_productlist_regular_item,null);
            viewHolder=new ViewHolder();
            viewHolder.tvRegularName=(TextView)view.findViewById(R.id.tv_regular_name);
            viewHolder.tvPeriod=(TextView)view.findViewById(R.id.tv_period);
            viewHolder.tvStartTime=(TextView)view.findViewById(R.id.tv_open_time);
            viewHolder.ivSoldOut=(ImageView)view.findViewById(R.id.iv_sold_out);
            viewHolder.ivArrowGo=(ImageView)view.findViewById(R.id.iv_arrow_go);

//            viewHolder.llRegularTop1=(LinearLayout)view.findViewById(R.id.ll_regular_top1);
//            viewHolder.llRegularTop2=(LinearLayout)view.findViewById(R.id.ll_regular_top2);
            viewHolder.tvGrowthRate=(TextView)view.findViewById(R.id.tv_growth_rate);
            viewHolder.tvGrowthRateTxt=(TextView)view.findViewById(R.id.tv_growth_rate_txt);
            viewHolder.tvRegualDays=(TextView)view.findViewById(R.id.tv_regual_days);
            viewHolder.tvRegualDaysTxt=(TextView)view.findViewById(R.id.tv_regual_days_txt);
            viewHolder.tvIsanytime=(TextView)view.findViewById(R.id.tv_isanytime);
            viewHolder.tvTxt2=(TextView)view.findViewById(R.id.tv_txt2);
            viewHolder.tvStartAmount=(TextView)view.findViewById(R.id.tv_start_amount);
            viewHolder.tvTxt4=(TextView)view.findViewById(R.id.tv_txt4);
            viewHolder.tvRisk=(TextView)view.findViewById(R.id.tv_risk);
            viewHolder.tvProdState=(TextView)view.findViewById(R.id.tv_prod_state);

            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }

        ProductInfo regulartFinanceProduct=financeProductList.get(i);
        prodState=regulartFinanceProduct.getProdState();


        viewHolder.tvRegularName.setText(regulartFinanceProduct.getProdName());
        viewHolder.tvPeriod.setText("["+regulartFinanceProduct.getPeriodName()+"]");
        String format="<big>%s</big>";
        String data=regulartFinanceProduct.getOpenTime();
        String a=data.substring(8, 10);
        String b=data.substring(11, 16);
        String opentimeString=a+"日"+b+"开售";
        String opentime=String.format(format,opentimeString);
        viewHolder.tvStartTime.setText(Html.fromHtml(opentime));
        viewHolder.tvStartTime.setTextSize(10);
        viewHolder.tvGrowthRate.setText(String.valueOf(regulartFinanceProduct.getGrowthRate()));
        viewHolder.tvRegualDays.setText(String.valueOf(regulartFinanceProduct.getLimitTime()));
        viewHolder.tvProdState.setText(String.valueOf(prodState));
        isIsAllow=regulartFinanceProduct.getToCash();
        if(isIsAllow==0){
            viewHolder.tvIsanytime.setText("不可变现");
        }else{
            viewHolder.tvIsanytime.setText("可变现");
        }
        int risk=regulartFinanceProduct.getRiskLevel();
        if(risk==0){
            viewHolder.tvRisk.setText("低风险");
        }else{
            viewHolder.tvRisk.setText("高风险");
        }
        viewHolder.tvStartAmount.setText(regulartFinanceProduct.getStartAmout() + "元起购");

//        viewHolder.llRegularTop1.setVisibility(View.VISIBLE);
//        viewHolder.llRegularTop2.setVisibility(View.GONE);
        viewHolder.ivSoldOut.setVisibility(View.GONE);
        viewHolder.tvStartTime.setVisibility(View.VISIBLE);
        viewHolder.ivArrowGo.setVisibility(View.VISIBLE);
        viewHolder.tvRegularName.setTextColor(context.getResources().getColor(R.color.has_input_edittext_color));
        viewHolder.tvPeriod.setTextColor(context.getResources().getColor(R.color.has_input_edittext_color));
        viewHolder.tvStartTime.setTextColor(context.getResources().getColor(R.color.has_input_edittext_color));
        viewHolder.tvGrowthRate.setTextColor(context.getResources().getColor(R.color.amount_color));
        viewHolder.tvGrowthRateTxt.setTextColor(context.getResources().getColor(R.color.combination_textview_color));
        viewHolder.tvRegualDaysTxt.setTextColor(context.getResources().getColor(R.color.combination_textview_color));
        viewHolder.tvRegualDays.setTextColor(context.getResources().getColor(R.color.amount_color));
        viewHolder.tvIsanytime.setTextColor(context.getResources().getColor(R.color.combination_textview_color));
        viewHolder.tvTxt2.setTextColor(context.getResources().getColor(R.color.amount_color));
        viewHolder.tvStartAmount.setTextColor(context.getResources().getColor(R.color.combination_textview_color));
        viewHolder.tvTxt4.setTextColor(context.getResources().getColor(R.color.amount_color));
        viewHolder.tvRisk.setTextColor(context.getResources().getColor(R.color.combination_textview_color));
        //已售罄时 显示效果全部字体设置为灰色
        if(prodState==3){
            viewHolder.tvRegularName.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvPeriod.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvStartTime.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvGrowthRate.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvGrowthRateTxt.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvRegualDaysTxt.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvRegualDays.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvIsanytime.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvTxt2.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvStartAmount.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvTxt4.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            viewHolder.tvRisk.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
//            viewHolder.llRegularTop1.setVisibility(View.GONE);
//            viewHolder.llRegularTop2.setVisibility(View.VISIBLE);
            viewHolder.ivSoldOut.setVisibility(View.VISIBLE);
            viewHolder.tvStartTime.setVisibility(View.GONE);
            viewHolder.ivArrowGo.setVisibility(View.INVISIBLE);
            view.setEnabled(false);

        }

        return view;
    }
    static class ViewHolder{
        TextView tvRegularName;
        TextView tvPeriod;

        TextView tvStartTime;
        ImageView ivSoldOut;
        ImageView ivArrowGo;

        /**
         * 预期年化收益
         */
        TextView tvGrowthRate;
        TextView tvGrowthRateTxt;
        TextView tvRegualDays;
        TextView tvRegualDaysTxt;
//        LinearLayout llRegularTop1;
//        LinearLayout llRegularTop2;
        TextView tvIsanytime;
        TextView tvTxt2;
        TextView tvStartAmount;
        TextView tvTxt4;
        TextView tvRisk;
        TextView tvProdState;

    }
}
