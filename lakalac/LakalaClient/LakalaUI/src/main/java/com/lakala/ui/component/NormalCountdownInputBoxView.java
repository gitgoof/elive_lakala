package com.lakala.ui.component;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.ui.R;

public class NormalCountdownInputBoxView extends LinearLayout {

    private Context context;
    private TextView inputVerifyCodeLable;
    private EditText verifycodeEdit;
    private Button getVerfyCodeButton;

    /** 计时器实例 */
    private TimeCount timeCount;

    /**
     * 构造方法
     * @param context
     */
    public NormalCountdownInputBoxView(Context context) {
        this(context, null);
        this.context = context;
    }

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public NormalCountdownInputBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    /**
     * 初始化View
     */
    private void initView(){
        LayoutInflater.from(context).inflate(R.layout.ui_normal_cutdown_eidt,this);

        inputVerifyCodeLable = (TextView)findViewById(R.id.plat_activity_input_verifycode_lable_textview);
        verifycodeEdit       = (EditText)findViewById(R.id.plat_activity_input_verifycode_edittext);
        getVerfyCodeButton   = (Button)findViewById(R.id.plat_activity_input_get_verifycode_button);

        setEditHint(R.string.ui_SMS_verifyCode);
        setEditLength(6);
        setEditInputType(InputType.TYPE_CLASS_NUMBER);
    }

    /**
     * 清空输入框
     */
    public void clearEditText(){
        verifycodeEdit.setText("");
    }

    public void setEditTextChangeListener(TextWatcher watcher){
        verifycodeEdit.addTextChangedListener(watcher);
    }
    /**
     * 获取View实例
     */
    public EditText getVerifycodeEdit() {
        return verifycodeEdit;
    }

    /**
     * 设置标题
     */
    public void setTitle(int title){
        inputVerifyCodeLable.setText(title);
    }

    public void setTitle(CharSequence title){
        inputVerifyCodeLable.setText(title);
    }

    /**
     * 设置编辑框hint
     */

    public void setEditHint(CharSequence hint){
        verifycodeEdit.setHint(hint);
    }

    public void setEditHint(int hint){
        verifycodeEdit.setHint(hint);
    }

    /**
     * 设置编辑框输入长度限制
     */
    public void setEditLength(int length){
        verifycodeEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    /**
     * 设置输入框类型
     */
    public void setEditInputType(int type){
        verifycodeEdit.setInputType(type);
    }

    /**
     * 获取View实例
     */
    public Button getVerfyCodeButton(){
        return getVerfyCodeButton;
    }

    /**
     * 设置提交按钮点击事件
     * @param listener
     */
    public void setGetVerifyCodeListener(OnClickListener listener){
        if (listener == null) return;
        getVerfyCodeButton.setOnClickListener(listener);
    }

    /**
     * 获取验证码内容
     */
    public String getVerifyCodeText(){
        return verifycodeEdit.getText().toString().trim();
    }

    /**
     * 重启倒计时
     */
    public void startCountdown(){
        if(null == timeCount){
            timeCount =new TimeCount(60*1000,1000);
        }
        if(timeCount.isCalculating()){
            cancleTime();
        }else{
            start();
        }
    }

    /**
     * 开始计时
     */
    private void start(){

        getVerfyCodeButton.setEnabled(false);

        if(null != timeCount){
            clearEditText();
            timeCount.setIsCalcualting(true);
            timeCount.start();
        }
    }

    /**
     * 停止倒计时
     */
    public void cancleTime(){
        if(null != timeCount){
            if(timeCount.isCalculating){
                timeCount.setIsCalcualting(false);
                timeCount.restartCountDownTimer();
                timeCount.cancel();
                timeCount = null;
            }
        }
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {

        private boolean isCalculating = false;

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        public void setIsCalcualting(boolean isCalcualting){
            this.isCalculating = isCalcualting;
        }

        public boolean isCalculating(){
            return isCalculating;
        }

        @Override
        public void onFinish() {//计时完毕时触发
            restartCountDownTimer();
        }

        /** 重新开启计时器 */
        private void restartCountDownTimer() {
            getVerfyCodeButton.setEnabled(true);
            getVerfyCodeButton.setText(context.getResources().getString(R.string.again_send));
            //getVerfyCodeButton.setText(context.getResources().getString(R.string.ui_get_verifycode));
            isCalculating = false;
        }

        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            getVerfyCodeButton.setText(String.format(context.getString(R.string.ui_count_prompt2),
                    millisUntilFinished / 1000));
        }
    }

}
