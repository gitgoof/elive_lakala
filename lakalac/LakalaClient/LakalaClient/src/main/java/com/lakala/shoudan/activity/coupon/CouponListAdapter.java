package com.lakala.shoudan.activity.coupon;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.coupon.bean.CouponBean;
import com.lakala.shoudan.datadefine.Voucher;
import com.lakala.ui.common.CommonSelectListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by huangjp on 2016/6/6.
 */
public class CouponListAdapter extends ArrayAdapter<Voucher> {
    private Boolean flag = true;
    public CouponListAdapter(Context context, List<Voucher> data){
        super(context,-1,data);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        final Context context = parent.getContext();
        if (holder==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_listview_coupon_list,null);
            holder.amount=(TextView)convertView.findViewById(R.id.tv_amount);
            holder.date=(TextView)convertView.findViewById(R.id.tv_date);
            holder.note=(TextView)convertView.findViewById(R.id.tv_note);
            holder.num=(TextView)convertView.findViewById(R.id.tv_num);
            holder.couponTag=(TextView)convertView.findViewById(R.id.tv_coupon_tag);
            holder.priceTag=(TextView)convertView.findViewById(R.id.tv_price_tag);
            holder.more=(LinearLayout) convertView.findViewById(R.id.ll_more);
            holder.tvMore = (TextView)convertView.findViewById(R.id.tv_more);
            holder.ivMore = (ImageView)convertView.findViewById(R.id.iv_more);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }
        Voucher cou = getItem(position);
        final Voucher.StateEnum state = cou.getStateEnum();
        if (state != Voucher.StateEnum.未使用){
            holder.amount.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            holder.num.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            holder.couponTag.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            holder.priceTag.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
            holder.date.setText(cou.getState());
        }else{
            holder.amount.setTextColor(context.getResources().getColor(R.color.blue_text));
            holder.num.setTextColor(context.getResources().getColor(R.color.blue_text));
            holder.couponTag.setTextColor(context.getResources().getColor(R.color.blue_text));
            holder.priceTag.setTextColor(context.getResources().getColor(R.color.blue_text));
            //时间戳转为时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            long lcc_time = Long.valueOf(cou.getEnd_time());
            String re_StrTime = sdf.format(new Date(lcc_time * 1000L));
            holder.date.setText("有效期至"+re_StrTime);
        }
        holder.date.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
        holder.note.setTextColor(context.getResources().getColor(R.color.combination_title_text_color));

        holder.amount.setText(cou.getPrice());
        holder.note.setText(cou.getDescription());
        holder.num.setText("串码 "+cou.getVoucher_num());

//        holder.more.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LinearLayout linearLayout=(LinearLayout)v;
//                if (flag){
//                    flag=false;
//                    for (int i=0;i<linearLayout.getChildCount();i++){
//                        View view=linearLayout.getChildAt(i);
//                        if (view instanceof TextView){
//                            ((TextView) view).setEllipsize(null);
//                            ((TextView) view).setSingleLine(flag);
//                            if (state != Voucher.StateEnum.未使用){
//                                ((TextView) view).setTextColor(context.getResources().getColor(R.color.combination_title_text_color));
//                            }
//                        }
//                        if (view instanceof ImageView){
//
//                            if (state != Voucher.StateEnum.未使用){
//                                ((ImageView)view).setBackgroundResource(R.drawable.btn_arrow_disable_down_grey);
//                            }else{
//                                ((ImageView)view).setBackgroundResource(R.drawable.btn_arrow_down_blue);
//                            }
//                        }
//                    }
//                }else{
//                    flag=true;
//                    for (int i=0;i<linearLayout.getChildCount();i++){
//                        View view=linearLayout.getChildAt(i);
//                        if (view instanceof TextView){
//                            ((TextView) view).setEllipsize(TextUtils.TruncateAt.END);
//                            ((TextView) view).setSingleLine(flag);
//                        }
//                        if (view instanceof ImageView){
//                            ((ImageView)view).setBackgroundResource(R.drawable.btn_arrow_up_blue);
//                        }
//                    }
//                }
//            }
//        });

        TextPaint paint = holder.note.getPaint();
        float textWidth = paint.measureText(cou.getDescription());
        int tvWidth = holder.note.getWidth();
        if(textWidth < tvWidth){
            holder.more.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.note
                    .getLayoutParams();
            int bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,
                    parent.getResources()
                            .getDisplayMetrics());
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin,
                    bottomMargin);
        }else {
            holder.more.setVisibility(View.VISIBLE);
            if(holder.isExpand){
                holder.note.setMaxLines(Integer.MAX_VALUE);
                holder.note.setSingleLine(false);
                holder.tvMore.setText("收起");
            }else {
                holder.note.setMaxLines(1);
                holder.note.setSingleLine();
                holder.tvMore.setText("更多");
            }
        }
        final View view = convertView;
        View.OnClickListener expandListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder viewHolder = (ViewHolder)view.getTag();
                viewHolder.isExpand = !viewHolder.isExpand;
                notifyDataSetChanged();
            }
        };
        holder.note.setOnClickListener(expandListener);
        holder.more.setOnClickListener(expandListener);
        return convertView;
    }
    class ViewHolder{
        private TextView amount;//代金券金额
        private TextView date;//截止日期
        private TextView note;//描述
        private TextView num;//串码
        private TextView couponTag;//代金券标识
        private TextView priceTag;//金钱符号
        private TextView tvMore;//
        private ImageView ivMore;//
        private LinearLayout more;
        private boolean isExpand = false;
    }
}
