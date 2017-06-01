package com.lakala.shoudan.component;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jyc on 2017/2/28.
 * 刷卡收款MCCpopupwindow
 */

public class MCCPopupwindow implements AdapterView.OnItemClickListener {
    private PopupWindow popupWindow;
    private McccListView listView;
    private List<String> list;
    Context context;
    public MCCPopupwindow(Context context,View view) {
        this.context=context;
        listView=new McccListView(context);
        list=new ArrayList<>();
        list.add("贷款1");
        list.add("贷款2");
        list.add("贷款3");
        list.add("贷款4");
        list.add("贷款5");
        listView.setData(list);
        listView.setOnItemClicker(this);
        popupWindow=new PopupWindow(listView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.white));
        popupWindow.setAnimationStyle(com.lakala.platform.R.style.more_popup_anim_style);
        popupWindow.showAsDropDown(view);

    }
    public void  show(){

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ToastUtil.toast(context,list.get(position));
    }
}
