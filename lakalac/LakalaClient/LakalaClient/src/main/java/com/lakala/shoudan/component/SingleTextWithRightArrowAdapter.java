package com.lakala.shoudan.component;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.shoudan.R;

/**
 * Created by More on 14-6-27.
 */
public class SingleTextWithRightArrowAdapter extends BaseAdapter {

    private String[] itemNames;

    private Context context;

    @Override
    public int getCount() {
        return itemNames.length;
    }

    @Override
    public Object getItem(int i) {
        return itemNames[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if(view == null){
            view = LinearLayout.inflate(context, R.layout.single_text_with_right_arrow, null);
            holder = new ViewHolder();
            holder.item = (TextView)view.findViewById(R.id.item_name);
            holder.seperator = view.findViewById(R.id.seperator);
            holder.seperator1 = view.findViewById(R.id.seperator1);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }
        holder.item.setText(itemNames[i]);
        if (i == (itemNames.length - 1)){
            holder.seperator.setVisibility(View.GONE);
            holder.seperator1.setVisibility(View.VISIBLE);
        }else {
            holder.seperator.setVisibility(View.VISIBLE);
            holder.seperator1.setVisibility(View.GONE);
        }
        return view;
    }

    public SingleTextWithRightArrowAdapter(Context context, String[] itemNames) {
        this.context = context;
        this.itemNames = itemNames;

    }
    class ViewHolder{
        TextView item;
        View seperator;
        View seperator1;
    }
}
