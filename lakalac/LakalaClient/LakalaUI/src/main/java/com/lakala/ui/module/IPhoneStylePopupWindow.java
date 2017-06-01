package com.lakala.ui.module;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lakala.ui.R;


/**
 * Created by fengx on 2015/12/2.
 */
public class IPhoneStylePopupWindow extends PopupWindow {

    private int i;
    private String cancelText="";
    private String[] items;
    private Context context;
    private LinearLayout layout;

    public interface ItemClickListener{

        /**
         *
         * @param itemName 点击的item的名字
         */
        void onItemClick(String itemName);

    }

    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    /**
     *
     * @param context
     * @param items 需要展示的列表
     */
    public IPhoneStylePopupWindow(Context context, String[] items) {
        this.context = context;
        layout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(context.getResources().getColor(R.color.gray_666666));
        layout.setGravity(Gravity.BOTTOM);
        layout.getBackground().setAlpha(150);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IPhoneStylePopupWindow.this.dismiss();
            }
        });

        this.setContentView(layout);
        this.items = items;
        this.setFocusable(true);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOutsideTouchable(true);

        addView(context, layout);
    }

    public void setCancelButtonText(String str){
        cancelText = str;
    }

    private void addView(Context context,LinearLayout layout) {

        //添加业务功能按钮
        for (i=0;i<items.length;i++) {
            final TextView textView = new TextView(context);
            textView.setText(items[i]);
            textView.setHeight((int) context.getResources().getDimension(R.dimen.dimen_88));
            textView.setTextColor(context.getResources().getColor(R.color.color_blue_50bdef));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setBackgroundResource(R.drawable.ui_common_item_selector);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int j=0;j<items.length;j++){
                        if (textView.getText().toString().equals(items[j])){
                            itemClickListener.onItemClick(items[j]);
                        }
                    }
                }
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,(int)context.getResources().getDimension(R.dimen.dimen_2),0,0);
            layout.addView(textView, params);
        }

        //添加取消按钮
        TextView textView = new TextView(context);
        textView.setHeight((int)context.getResources().getDimension(R.dimen.dimen_88));
        if (cancelText.equals("")){
            cancelText = "取消";
        }
        textView.setText(cancelText);
        textView.setTextColor(context.getResources().getColor(R.color.color_blue_50bdef));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        textView.setBackgroundResource(R.drawable.ui_common_item_selector);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IPhoneStylePopupWindow.this.dismiss();
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, (int) context.getResources().getDimension(R.dimen.dimen_24), 0, 0);
        layout.addView(textView, params);

    }

    public void showPop(View parent) {

        this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        this.update();
    }
}
