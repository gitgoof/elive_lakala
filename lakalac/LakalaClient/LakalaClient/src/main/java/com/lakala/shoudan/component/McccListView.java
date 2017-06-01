package com.lakala.shoudan.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/2/28.
 */

public class McccListView extends LinearLayout {
    public  Context context;
    private ListView lv;
    List<String> list=new ArrayList<>();
    private Myadapter myAdapter;

    public McccListView(Context context) {
        super(context);
        this.context=context;
        initView(context);
    }

    public McccListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView(context);
    }

    private void initView(Context context) {
       View view=LayoutInflater.from(context).inflate(R.layout.popupwindow_mcclist,this,true);
        lv= (ListView) view.findViewById(R.id.mcc_lv);
        myAdapter=new Myadapter();
        lv.setAdapter(myAdapter);
    }
    public  void  setData(List<String> list){
        this.list.clear();
        this.list.addAll(list);
        myAdapter.notifyDataSetChanged();
    }
    public  void setOnItemClicker(AdapterView.OnItemClickListener listener){
        lv.setOnItemClickListener(listener);
    }
    public void  delData(int position){
        list.remove(position);
        myAdapter.notifyDataSetChanged();
    }

    private class Myadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Viewholder viewHolder=null;
            if (convertView==null){
                convertView=LayoutInflater.from(context).inflate(R.layout.popup_mcc_item,null);
                viewHolder=new Viewholder(convertView);
            }else {
                viewHolder= (Viewholder) convertView.getTag();
            }
            viewHolder.mcc_item_tv.setText(list.get(position));
            viewHolder.del.setVisibility(VISIBLE);
            viewHolder.plus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.toast(context,"plus");
                }
            });
            viewHolder.del.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.toast(context,"del");
                    delData(position);
                }
            });
            return convertView;
        }

    }
    public static class Viewholder{
        TextView mcc_item_tv;
        ImageView del,plus;
        public Viewholder(View  view) {
            mcc_item_tv= (TextView) view.findViewById(R.id.mcc_item_tv);
            del= (ImageView) view.findViewById(R.id.del);
            plus= (ImageView) view.findViewById(R.id.plus);
            view.setTag(this);
        }
    }
}
