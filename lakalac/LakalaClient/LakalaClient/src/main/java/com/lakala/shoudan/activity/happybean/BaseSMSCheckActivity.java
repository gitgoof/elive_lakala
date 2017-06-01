package com.lakala.shoudan.activity.happybean;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.util.HintFocusChangeListener;
import com.lakala.ui.component.CaptchaTextView;

/**
 * Created by huangjp on 2016/5/18.
 */
public  abstract class BaseSMSCheckActivity extends AppBaseActivity {
    private TextView tips;
    private TextView tvNext;
    private EditText etInputCaptcha;
    protected CaptchaTextView tvGetCaptcha;
    private String tipsString = "为保证交易安全,已经将验证码发送至手机:%s,请等待,过期时间30分钟!";
    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("输入验证码");
    }
    protected void baseInit(){
        tips = (TextView)findViewById(R.id.tips);
        tvNext=(TextView)findViewById(R.id.tv_next);
        etInputCaptcha = (EditText)findViewById(R.id.et_input_captcha);
        etInputCaptcha.setOnFocusChangeListener(new HintFocusChangeListener());
        tvGetCaptcha = (CaptchaTextView)findViewById(R.id.tv_get_captcha);
        tvGetCaptcha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCaptcha();
                    }
                }
        );

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etInputCaptcha.getText())){
                    trade();
                }else{
                    ToastUtil.toast(context,"验证码不能为空！");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_sms);
        baseInit();
        initPreData();

    }

    /**初始化前一个页面传过来的数据*/
    abstract void initPreData();

    /**获取验证码*/
    abstract void getCaptcha();

    /**获取验证码后的处理事件*/
    abstract void trade();

}
