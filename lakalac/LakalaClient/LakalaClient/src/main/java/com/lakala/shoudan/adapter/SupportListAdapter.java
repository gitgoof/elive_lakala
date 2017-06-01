package com.lakala.shoudan.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.platform.common.UILUtils;
import com.lakala.shoudan.R;
import com.lakala.shoudan.util.ImageUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by LMQ on 2015/10/27.
 */
public class SupportListAdapter extends BaseAdapter {
    private List<JSONObject> data = null;

    public SupportListAdapter(List<JSONObject> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public JSONObject getItem(int position) {
        if(position >= 0 && position < data.size()){
            return data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_finance_support_banks,null);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView)convertView.findViewById(R.id.iv_icon);
            holder.tvText = (TextView)convertView.findViewById(R.id.tv_text);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        JSONObject item = getItem(position);
        holder.tvText.setText(item.optString("BankName", ""));
        String assetsUri = new StringBuilder().append(item.optString("BankCode", "")).append(".png")
                                              .toString();
        Drawable iconDrawable = ImageUtil.getDrawbleFromAssets(parent.getContext(),assetsUri);
        if(iconDrawable != null){
//            holder.ivIcon.setImageDrawable(iconDrawable);
            holder.ivIcon.setBackgroundDrawable(iconDrawable);
        }else{
//            UILUtils.display(item.optString("BankImg"),holder.ivIcon);
            UILUtils.displayBackground(item.optString("BankImg"), holder.ivIcon);
        }
        return convertView;
    }
    class ViewHolder{
        ImageView ivIcon;
        TextView tvText;
    }
}
