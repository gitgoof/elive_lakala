package com.lakala.shoudan.activity.shoudan.records;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.library.util.DateUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.datadefine.RecordDetail;

import java.util.List;

/**
 * Created by fengxuan on 2015/12/29.
 */
public class TradeAdapter extends BaseAdapter{

    private TradeQueryInfo tradeQueryInfo;
    private Context context;

    public TradeAdapter(Context context,
                                   TradeQueryInfo tradeQueryInfo) {
        this.tradeQueryInfo = tradeQueryInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tradeQueryInfo.getDealDetails().size();
    }

    @Override
    public Object getItem(int position) {

        return tradeQueryInfo.getDealDetails().get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder=null;
        if (convertView==null) {
            convertView= LinearLayout.inflate(context, R.layout.shoudan_recordlist_item, null);
            holder=new Holder();
            holder.amount=(TextView) convertView.findViewById(R.id.amount);

            holder.collectionState=(TextView) convertView.findViewById(R.id.collection_state);
            holder.time=(TextView) convertView.findViewById(R.id.time);
            holder.view = convertView.findViewById(R.id.seperator);
            convertView.setTag(holder);
        }else {
            holder=(Holder) convertView.getTag();
        }

        DealDetails dealDetails = tradeQueryInfo.getDealDetails().get(position);
        String amt = String.valueOf(dealDetails.getDealAmount());
        String formatamt= Util.formatAmount(amt);
        String fuhao="";
        int textColor= Color.BLACK;

        String reslt;
        if(dealDetails.getStatus().equals("SUCCESS"))
            reslt="成功";
        else {
            reslt="失败";
        }

        String transStatus = dealDetails.getStatus();
        String revocationStatus = dealDetails.getWithdrawInfo().getStatus();
        if("刷卡收款".equals(dealDetails.getDealTypeName()) || "扫码收款".equals(dealDetails.getDealTypeName())){

            if (transStatus.equals("SUCCESS")){
                //判断是否有进行撤销
                if (!revocationStatus.equals("null")){
                    if (revocationStatus.equals("SUCCESS")){
                        holder.collectionState.setText("收款成功,撤销成功");//R.string.revocation_success);
                        textColor=Color.GREEN;
                        fuhao="-";
                    }else {
                        holder.collectionState.setText("收款成功,撤销失败");
                        textColor=Color.RED;
                        fuhao="+";
                    }
                }else {
                    holder.collectionState.setText(R.string.collection_success);
                    textColor=Color.RED;
                    fuhao="+";
                }
            }else {
                holder.collectionState.setText(R.string.collection_fail);
            }

        }
        //大额收款
        else if ("大额收款".equals(dealDetails.getDealTypeName())){
            if (transStatus.equals("SUCCESS")){
                textColor=Color.RED;
                fuhao="+";
                holder.collectionState.setText(dealDetails.getDealTypeName()+""+reslt);
            }else {
                textColor = Color.BLACK;
                holder.collectionState.setText(dealDetails.getDealTypeName()+""+reslt);
            }


        }else {
            //便民统一黑色
           textColor = Color.BLACK;
            holder.collectionState.setText(dealDetails.getDealTypeName()+""+reslt);
        }

        holder.collectionState.setTextColor(Color.BLACK);
        holder.amount.setTextColor(textColor);
        holder.amount.setText( fuhao+formatamt);
        String tempDate = dealDetails.getDealStartDateTime();
        holder.time.setText(DateUtil.formatDateStr(tempDate));
        if (position == (tradeQueryInfo.getDealDetails().size() - 1)){
            holder.view.setVisibility(View.GONE);
        }else {
            holder.view.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
    private class Holder{
        public TextView amount;
        public TextView cardNo;
        public TextView collectionState;
        public TextView time;
        private View view;
    }
}
