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

import com.alibaba.fastjson.JSONArray;
import com.lakala.library.util.ImageUtil;
import com.lakala.library.util.LogUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.quickArrive.AddBankCardActivity;
import com.lakala.shoudan.activity.quickArrive.BankCards;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WangCheng on 2016/6/29.
 */
public class CardListAdapter extends BaseAdapter{

    private Context context;
    private List<BankCards> list;

    public CardListAdapter(Context context, List<BankCards> list){
        this.context=context;
        this.list=list;

    }

    @Override
    public int getCount() {
        int num=list==null? 0:list.size();
        return num+1;
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
        if(position<getCount()-1){
            if (null == view) {
                holder = new ViewHolder();
                view = LayoutInflater.from(context).inflate(R.layout.item_card_list, null);
                holder.icon= (ImageView) view.findViewById(R.id.iv_bank);
                holder.name= (TextView) view.findViewById(R.id.tv_bank_name);
                holder.type= (TextView) view.findViewById(R.id.tv_bank_type);
                holder.num= (TextView) view.findViewById(R.id.tv_bank_num);
                holder.ll_content=view.findViewById(R.id.ll_content);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.name.setText(list.get(position).getBankname());
            if("0002".equals(list.get(position).getCardtype())){
                holder.type.setText("信用卡");
            }else if ("0001".equals(list.get(position).getCardtype())){
                holder.type.setText("储蓄卡");
            }
            holder.num.setText(list.get(position).getD0bindcardno());
            Bitmap bitmap = ImageUtil.getBitmapInAssets(context,
                    "bank_icon/"+list.get(position).getBankcode()+ "" +
                            ".png");
            if(bitmap!=null){
                holder.icon.setImageBitmap(bitmap);
            }
        }else {//最后一个添加银行卡item
            view = LayoutInflater.from(context).inflate(R.layout.item_card_list_faile, null);
            view.findViewById(R.id.ll_content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, AddBankCardActivity.class));
                }
            });
        }
        return view;
    }
    class ViewHolder{
        ImageView icon;
        TextView name,type,num;
        View ll_content;
    }

}
