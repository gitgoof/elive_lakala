package com.lakala.shoudan.adapter;

import android.content.Context;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.shoudan.R;
import com.lakala.shoudan.component.CustomTextView;
import com.lakala.shoudan.datadefine.VoucherExplain;

import java.util.List;

/**
 * Created by linmq on 2016/6/12.
 */
public class IntegralListItemAdapter extends ArrayAdapter<VoucherExplain> {
    private OnExchangeClickeListener onExchangeClickeListener;

    public IntegralListItemAdapter setOnExchangeClickeListener(
            OnExchangeClickeListener onExchangeClickeListener) {
        this.onExchangeClickeListener = onExchangeClickeListener;
        return this;
    }

    public IntegralListItemAdapter(Context context, List<VoucherExplain> objects) {
        super(context, -1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.layout_integral_listitem, null);
            holder = new ViewHolder();
            CustomTextView text = (CustomTextView) convertView.findViewById(R.id.tv_amount);
            holder.tvAmount = text;
            holder.tvIntegral = (TextView) convertView.findViewById(R.id.tv_integral);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.tv_note);
            holder.tvExchange = (TextView) convertView.findViewById(R.id.tv_exchange);
            holder.llMore = convertView.findViewById(R.id.ll_more);
            holder.tvMore = (TextView)convertView.findViewById(R.id.tv_more);
            holder.ivMore = (ImageView)convertView.findViewById(R.id.iv_more);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        VoucherExplain data = getItem(position);
        holder.tvAmount.setText(data.getPrice());
        holder.tvIntegral.setText(data.getNotes());
        holder.tvDescription.setText(data.getDescription());
        TextPaint paint = holder.tvDescription.getPaint();
        float textWidth = paint.measureText(data.getDescription());
        int tvWidth = holder.tvDescription.getWidth();
        if(textWidth < tvWidth){
            holder.llMore.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tvDescription
                    .getLayoutParams();
            int bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11,
                                                               parent.getResources()
                                                                     .getDisplayMetrics());
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin,
                              bottomMargin);
        }else {
            holder.llMore.setVisibility(View.VISIBLE);
            if(holder.isExpand){
                holder.tvDescription.setMaxLines(Integer.MAX_VALUE);
                holder.tvDescription.setSingleLine(false);
                holder.tvMore.setText("收起");
            }else {
                holder.tvDescription.setMaxLines(1);
                holder.tvDescription.setSingleLine();
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
        holder.tvDescription.setOnClickListener(expandListener);
        holder.llMore.setOnClickListener(expandListener);
        if(onExchangeClickeListener != null){
            onExchangeClickeListener.setVoucherExplain(data);
            holder.tvExchange.setOnClickListener(onExchangeClickeListener);
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tvAmount, tvIntegral, tvDescription, tvExchange,tvMore;
        private View llMore;
        private ImageView ivMore;
        private boolean isExpand = false;
    }
    public static abstract class OnExchangeClickeListener implements View.OnClickListener{
        private VoucherExplain voucherExplain;

        public OnExchangeClickeListener setVoucherExplain(
                VoucherExplain voucherExplain) {
            this.voucherExplain = voucherExplain;
            return this;
        }

        public abstract void onExchangeClick(VoucherExplain voucherExplain);

        @Override
        public void onClick(View v) {
            onExchangeClick(voucherExplain);
        }
    }
}
