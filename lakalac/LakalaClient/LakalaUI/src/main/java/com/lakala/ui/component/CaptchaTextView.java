package com.lakala.ui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by LMQ on 2015/10/17.
 */
public class CaptchaTextView extends TextView {
    private String countDownFormat = "%s秒";
    public CaptchaTextView(Context context) {
        super(context);
    }

    public CaptchaTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CaptchaTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CaptchaTextView setCountDownFormat(String countDownFormat) {
        this.countDownFormat = countDownFormat;
        return this;
    }

    /**
     * 开始获取验证码倒计时
     * @param num
     */
    public void startCaptchaDown(final int num){
        setEnabled(false);
        Runnable r = new Runnable() {
            int count = num;
            @Override
            public void run() {
                if(isEnabled()){
                    //如果按钮可点击则不再倒计时
                    return;
                }
                if(count == 0){
                    resetCaptchaDown();
                    return;
                }else{
                    setText(String.format(countDownFormat,count));
                    count--;
                }
                postDelayed(this,1000);
            }
        };
        post(r);
    }

    public void resetCaptchaDown(){
        setEnabled(true);
        setText("重新获取");
    }

}
