package com.lakala.shoudan.activity.shoudan.finance.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lakala.platform.common.UILUtils;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.communityservice.transferremittance.commonbanklistbase.ImageUtils;
import com.lakala.shoudan.datadefine.OpenBankInfo;
import com.lakala.shoudan.util.ImageUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by fengx on 2015/10/20.
 */
public class BankChooseExpandAdapter extends BaseExpandableListAdapter{

    private List<String> parent;
    private Map<String, List<OpenBankInfo>> child;
    private Context context;

    public BankChooseExpandAdapter(List<String> parent, Map<String, List<OpenBankInfo>> child, Context context) {
        this.parent = parent;
        this.child = child;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return parent.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String key = parent.get(groupPosition);
        return child.get(key).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parent.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String key = parent.get(groupPosition);
        return child.get(key).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_parent,null);
            holder.tvParent = (TextView) convertView.findViewById(R.id.tv_parent);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvParent.setText(this.parent.get(groupPosition));
        holder.tvParent.getPaint().setFakeBoldText(true);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_children,null);
            holder.tvChild = (TextView) convertView.findViewById(R.id.tv_bank_name);
            holder.ivBank = (ImageView) convertView.findViewById(R.id.iv_bank);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        String key = this.parent.get(groupPosition);
        OpenBankInfo openBankInfo = child.get(key).get(childPosition);
        holder.tvChild.setText(openBankInfo.bankname);
        if (!openBankInfo.bankimg.equalsIgnoreCase("")){
            Drawable drawable = ImageUtil.getDrawbleFromAssets(context,openBankInfo.bankCode +
                                                                       ".png");
            if (drawable == null){
//                ImageUtils.getInstance(parent.getContext()).loadBitmap(openBankInfo.bankimg, "", holder.ivBank);
                UILUtils.display(openBankInfo.bankimg, holder.ivBank);
            }else{
                holder.ivBank.setImageBitmap(ImageUtils.drawableToBitmap(drawable));
            }
        }else {
            holder.ivBank.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder{
        TextView tvParent;
        TextView tvChild;
        ImageView ivBank;
    }
}
