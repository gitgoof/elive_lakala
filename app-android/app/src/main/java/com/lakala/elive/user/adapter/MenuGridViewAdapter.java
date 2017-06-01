package com.lakala.elive.user.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lakala.elive.R;
import com.lakala.elive.beans.FunctionMenuInfo;
import com.lakala.elive.beans.NoticeResp;
import com.lakala.elive.common.utils.ImageTools;

import java.util.ArrayList;
import java.util.List;

public class MenuGridViewAdapter extends BaseAdapter {
    private Context mContxt;
    private List<FunctionMenuInfo> menuInfoList = new ArrayList<FunctionMenuInfo>();

    public MenuGridViewAdapter(Context contxt, List<FunctionMenuInfo> menuInfoList) {
        this.menuInfoList = menuInfoList;
        this.mContxt = contxt;
    }

    @Override
    public int getCount() {
        return menuInfoList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView itemView = null;// 声明组件封装对象 初始为null
        if (convertView == null) {
            convertView = View.inflate(mContxt, R.layout.grid_item_main_menu, null);
            itemView = new ItemView();
            itemView.ivMenuLogo = (ImageView) convertView.findViewById(R.id.iv_menu_icon);
            itemView.tvMenuName = (TextView) convertView.findViewById(R.id.tv_menu_name);
            itemView.tvCornerMark = (TextView) convertView.findViewById(R.id.tv_grid_item_main_corner_mark);
            convertView.setTag(itemView);
        } else {// 存在缓存
            itemView = (ItemView) convertView.getTag();
        }
        FunctionMenuInfo menuInfo = menuInfoList.get(position);
        //根据菜单ID获取菜单对应的本地图片
        Bitmap bitmap = ImageTools.getImageFromAssetsFile(mContxt, "menu/menu_" + menuInfo.getMenuId() + ".png");
        if (bitmap != null) {
            itemView.ivMenuLogo.setImageBitmap(bitmap);
        }
        String menuName = menuInfo.getMenuName();

        itemView.tvMenuName.setText(menuName);
        if(menuInfo.isShowCornerMark()){
            itemView.tvCornerMark.setVisibility(View.VISIBLE);
            itemView.tvCornerMark.setText(menuInfo.getCornerMarkNum() + "");
        } else {
            itemView.tvCornerMark.setVisibility(View.GONE);
        }
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return menuInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    private class ItemView {
        TextView tvMenuName; //名称
        ImageView ivMenuLogo; //Logo
        RelativeLayout rlRound; //Logo
        TextView tvRound; //Logo
        TextView tvCornerMark;
    }

    public List<FunctionMenuInfo> getFunctionInfoList() {
        return menuInfoList;
    }

    public void setFunctionInfoList(List<FunctionMenuInfo> menuInfoList) {
        this.menuInfoList = menuInfoList;
    }

}
