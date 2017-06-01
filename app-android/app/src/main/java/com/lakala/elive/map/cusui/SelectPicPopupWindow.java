package com.lakala.elive.map.cusui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lakala.elive.R;


/**
 * 选择图片弹出框
 * Created by coolwxb on 14-7-30.
 */
public class SelectPicPopupWindow extends PopupWindow {
    private Button btn_take_photo;
    private Button btn_pick_photo;
    private View mMenuView;


    /**
     * 设置监听
     */
    public void setClick(View.OnClickListener itemsOnClick) {
        //设置按钮监听
        btn_pick_photo.setOnClickListener(itemsOnClick);
        btn_take_photo.setOnClickListener(itemsOnClick);
    }

    /**
     * @param context
     * @param up_button
     * @param down_button
     * @param type        是否存在百度高德客户端 0:两个都有，1:只有百度，2：只有高德，3都没有
     */
    public SelectPicPopupWindow(Activity context, String up_button, String down_button, int type) {//能够自定义两个button的文案
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.dialog_daohang_layout, null);
        btn_take_photo = (Button) mMenuView.findViewById(R.id.btn_baidu);
        btn_pick_photo = (Button) mMenuView.findViewById(R.id.btn_gaode);

        switch (type) {
            case 0:
                break;
            case 1:
                btn_pick_photo.setVisibility(View.GONE);
                break;
            case 2:
                btn_take_photo.setVisibility(View.GONE);
                break;
            case 3:
                btn_pick_photo.setVisibility(View.GONE);
                break;
        }

        btn_take_photo.setText(up_button);
        btn_pick_photo.setText(down_button);

        Button btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
        //取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //销毁弹出框
                dismiss();
            }
        });

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
