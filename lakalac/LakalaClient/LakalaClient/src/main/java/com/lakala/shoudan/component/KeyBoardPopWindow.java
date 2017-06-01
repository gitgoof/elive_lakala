package com.lakala.shoudan.component;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lakala.shoudan.R;

/**
 * Created by LMQ on 2015/10/10.
 */
public class KeyBoardPopWindow extends PopupWindow implements View.OnClickListener {
    private final View contentView;
    private Activity context;
    private OnKeyClickListener listener;
    private int[] 	 button_number_ids = {
            R.id.id_keypad_0,
            R.id.id_keypad_1,
            R.id.id_keypad_2,
            R.id.id_keypad_3,
            R.id.id_keypad_4,
            R.id.id_keypad_5,
            R.id.id_keypad_6,
            R.id.id_keypad_7,
            R.id.id_keypad_8,
            R.id.id_keypad_9,
    };
    private ImageView comma;
    private boolean commaVisible = false;
    private View completeView;
    private boolean completeGone = true;

    public boolean isCompleteGone() {
        return completeGone;
    }

    public KeyBoardPopWindow setCompleteGone(boolean completeGone) {
        this.completeGone = completeGone;
        if(completeView == null){
            return this;
        }
        if(completeGone){
            completeView.setVisibility(View.GONE);
        }else{
            completeView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public boolean isCommaVisible() {
        return commaVisible;
    }

    public KeyBoardPopWindow setCommaVisible(boolean commaVisible) {
        this.commaVisible = commaVisible;
        if(comma == null){
            return this;
        }
        comma.setEnabled(commaVisible);
        return this;
    }

    public KeyBoardPopWindow setOnKeyClickListener(OnKeyClickListener listener) {
        this.listener = listener;
        return this;
    }

    public KeyBoardPopWindow(Activity context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        contentView = inflater.inflate(R.layout.layout_keyboard,null);
        contentView.findViewById(R.id.id_keypad_root_layout).setVisibility(View.VISIBLE);
        contentView.findViewById(R.id.key_title).setVisibility(View.VISIBLE);
        setContentView(contentView);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setAnimationStyle(R.style.more_popup_anim_style);
        initView();
    }

    private void initView() {
        completeView = findV(R.id.id_keypad_hide);
        setCompleteGone(completeGone);
        completeView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null && !isCompleteGone()) {
                            listener.onComplete();
                        }
                    }
                }
        );
        View deleteView = findV(R.id.id_keypad_del);
        deleteView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onDelete();
                        }
                    }
                }
        );
        for(int i = 0;i<button_number_ids.length;i++){
            int id = button_number_ids[i];
            View v = findV(id);
            v.setTag(String.valueOf(i));
            v.setOnClickListener(this);
        }
        comma = findV(R.id.id_keypad_comma);
        comma.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCommaVisible() && listener != null) {
                            listener.onKeyClicked(KeyBoardPopWindow.this, v, ".");
                        }
                    }
                }
        );
    }

    public void show(){
        showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }
    public void show(View parent){
        showAtLocation(parent,Gravity.BOTTOM,0,0);
    }

    @Override
    public void onClick(View v) {
        if(listener != null){
            listener.onKeyClicked(this,v,String.valueOf(v.getTag()));
        }
    }

    public interface OnKeyClickListener{
        void onKeyClicked(PopupWindow window, View view, String text);
        void onDelete();
        void onComplete();
    }
    private <T extends View> T findV(int id){
        View view = contentView.findViewById(id);
        return view == null?null:(T)view;
    }
    //设置键盘按钮点击无效果
    public void setNoPressedKeyBoardStyle(){
        for(int i = 0;i<button_number_ids.length;i++){
            int id = button_number_ids[i];
            View v = findV(id);
            v.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }
}
