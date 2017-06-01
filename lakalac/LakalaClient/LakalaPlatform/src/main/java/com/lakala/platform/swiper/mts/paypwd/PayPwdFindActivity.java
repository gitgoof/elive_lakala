package com.lakala.platform.swiper.mts.paypwd;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.exception.BaseException;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.ui.component.SingleLineTextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lianglong on 14-6-3.
 */
public class PayPwdFindActivity extends BaseActionBarActivity implements TextWatcher {
    private EditText etAnswer;
    private Button submitButton;
    private View forgetButton;

    private PayPwdQuestion question;

    /**
     * 验证密保答案
     */
    private IHttpRequestEvents submitHandler = new IHttpRequestEvents(){
        @Override
        public void onSuccess(HttpRequest request) {
            super.onSuccess(request);
            CommonRequestFactory.createGetSMSCode(mContext, ApplicationEx.getInstance().getUser().getLoginName(), "0", "228202")
                    .setIHttpRequestEvents(smsHandler)
                    .execute();
        }

        @Override
        public void onFailure(HttpRequest request, BaseException exception) {
            super.onFailure(request, exception);
        }
    };


    /**
     * 获取短信验证码
     */
    private IHttpRequestEvents smsHandler = new IHttpRequestEvents(){
        @Override
        public void onSuccess(HttpRequest request) {
            super.onSuccess(request);
            startActivity(new Intent(PayPwdFindActivity.this,PayPwdMessageValidActivity.class));
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        question   = getIntent().getParcelableExtra(PayPwdQuestion.class.getName());

        setContentView(R.layout.plat_activity_pay_pwd_find);
        navigationBar.setTitle(R.string.plat_password_security_find_pay_pwd_title);

        SingleLineTextView questionView = (SingleLineTextView) findViewById(R.id.pay_pwd_question);
        questionView.setLeftText(question.QuestionContent);

        etAnswer = (EditText) findViewById(R.id.et_answer);
//        etAnswer.addTextChangedListener(this);
        etAnswer.setHint(question.AnswerNote);

        submitButton = (Button) findViewById(R.id.id_common_guide_button);
        submitButton.setText(R.string.com_confirm);
        submitButton.setOnClickListener(this);
//        submitButton.setEnabled(false);

        forgetButton = findViewById(R.id.forget);
        forgetButton.setOnClickListener(this);
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        if (view == submitButton && isInputValid()){
            submit();
        } else if (view == forgetButton){
            forgetPayPwd();
        }
    }

    private boolean isInputValid(){
        Editable content = etAnswer.getText();
        if (content == null || content.toString().trim().length() == 0){
            ToastUtil.toast(this, R.string.plat_password_security_prompt4);
            return false;
        }
        return true;
    }

    /**
     * 提交密保问题答案
     */
    private void submit(){
        RegisterRequestFactory.verifyQuestion(this,
                ApplicationEx.getInstance().getUser().getLoginName(),
                etAnswer.getText().toString(),
                question.QuestionId).setIHttpRequestEvents(submitHandler).execute();
    }

    /**
     * 忘记答案
     */
    private void forgetPayPwd(){
        startActivity(new Intent(this,PayPwdForgetActivity.class));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (question == null || s == null) {
            return;
        }
        int length = s.length(), minLength = 2,maxLength = 16;
        String pattern  = "^[\u4e00-\u9fa5]*$";
        if ("2".equals(question.AnswerType)){
            minLength = maxLength = 8;
            pattern   = "^[0-9]*[1-9][0-9]*$";
        }
        if (length > maxLength){
            etAnswer.setText(s.toString().substring(0,maxLength));
            etAnswer.setSelection(maxLength);
            return;
        }
        Matcher matcher = Pattern.compile(pattern).matcher(s);
        submitButton.setEnabled(minLength <= length && length <= maxLength && matcher.find());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
