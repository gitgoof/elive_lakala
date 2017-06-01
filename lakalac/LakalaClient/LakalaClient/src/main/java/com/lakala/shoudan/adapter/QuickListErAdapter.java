package com.lakala.shoudan.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.library.util.ImageUtil;
import com.lakala.platform.bean.StlmRem2;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.quickArrive.AddBankCardActivity;
import com.lakala.shoudan.activity.quickArrive.ArriveDetileActivity;
import com.lakala.shoudan.activity.quickArrive.BankCards;

import java.util.List;

/**
 * Created by WangCheng on 2016/6/29.
 */
public class QuickListErAdapter extends BaseAdapter{

    private Context context;
    private List<StlmRem2> list;
    private int count;

    public QuickListErAdapter(Context context, List<StlmRem2> list,int count){
        this.context=context;
        this.list=list;
        this.count=count;

    }

    @Override
    public int getCount() {
        return list==null? 0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = null;
            if (null == view) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_quick_er, null);
                holder.time= (TextView) view.findViewById(R.id.tv_time);
                holder.num= (TextView) view.findViewById(R.id.tv_num);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, ArriveDetileActivity.class)
                            .putExtra("json",list.get(position).getJson()));
                }
            });
            String time=list.get(position).getRemTime();
            holder.time.setText(time.substring(8,10)+":"+
                    time.substring(10,12)+":"+
                    time.substring(12,14)
            );
            holder.num.setText(list.get(position).getRemAmt()+"元");
        return view;
    }
    class ViewHolder{
        TextView time,num;
    }

}
