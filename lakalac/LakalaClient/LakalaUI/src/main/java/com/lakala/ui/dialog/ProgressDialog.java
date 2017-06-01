package com.lakala.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;


/**
 * Created by More on 16/5/13.
 */
public class ProgressDialog extends Dialog {
    private String msg = "";
    public ProgressDialog(Context context) {
        super(context, R.style.loading_dialog);
        initDefault(context);
    }

    private void initDefault(Context context){

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_progress, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.rotate_anim);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息
        setCancelable(false);
        setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        getWindow().setGravity(Gravity.CENTER);

    }

    TextView tipTextView;

    public void setProgressMessage(String message){
        setMessage(message);
    }
    public String getProgressMessage(){
        return getMessage();
    }

    public void setMessage(String message){
        if(tipTextView != null){
            if(TextUtils.isEmpty(message)){
                tipTextView.setVisibility(View.GONE);
            }
            tipTextView.setText(message);
        }
        this.msg = message;
    }
    public String getMessage(){
        if(tipTextView != null){
            return tipTextView.getText().toString();
        }else {
            return this.msg;
        }
    }

}
