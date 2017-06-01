package com.lakala.platform.swiper.mts.paypwd;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.ui.component.SingleLineTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lianglong on 14-6-3.
 */
public class PayPwdSetQuestionActivity extends BaseActionBarActivity implements TextWatcher {
    private View containerAnswer;

    private SingleLineTextView questionView;

    private EditText etAnswer;

    private Button submitButton;

    private PayPwdQuestion current;

    private ArrayList<PayPwdQuestion> questions = new ArrayList<PayPwdQuestion>();

    /**
     * 获取密保问题列表
     */
    private final IHttpRequestEvents questionHandler = new IHttpRequestEvents(){
        @Override
        public void onSuccess(HttpRequest request) {
            super.onSuccess(request);
            JSONObject response = (JSONObject) request.getResponseHandler().getResultData();
            JSONArray array = response.optJSONArray("List");
            questions.clear();
            for (int i = 0; i < array.length(); i++) {
                questions.add(new PayPwdQuestion(array.optJSONObject(i)));
            }
            if (!questions.isEmpty()) {
                toChooseQuestion();
            } else {
                ToastUtil.toast(PayPwdSetQuestionActivity.this, "密保问题查询失败,请重试");
            }
        }
    };

    /**
     * 提交密保问题
     */
    private IHttpRequestEvents submitHandler = new IHttpRequestEvents(){
        @Override
        public void onSuccess(HttpRequest request) {
            super.onSuccess(request);
            ToastUtil.toast(PayPwdSetQuestionActivity.this, "密保问题设置成功");
            ApplicationEx.getInstance().getUser().setQuestionFlag(true);
            ApplicationEx.getInstance().getUser().save();
            //判断是否是找回支付密码的中间流程,如果是 直接到发送短信验证码的步骤即可
            if (getIntent().getStringExtra(PayPwdSetQuestionActivity.class.getName()) != null){
                toFindPayPwd();
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    };

    /**
     * 获取短信验证码
     */
    private IHttpRequestEvents messageHandler = new IHttpRequestEvents(){
        @Override
        public void onSuccess(HttpRequest request) {
            super.onSuccess(request);
            startActivity(new Intent(PayPwdSetQuestionActivity.this,PayPwdMessageValidActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plat_activity_pay_pwd_set_question);
        navigationBar.setTitle(R.string.plat_password_security_title);

        questionView = (SingleLineTextView) findViewById(R.id.pay_pwd_question);
        questionView.setOnClickListener(this);

        containerAnswer = findViewById(R.id.container_answer);

        etAnswer = (EditText) findViewById(R.id.et_answer);
        etAnswer.addTextChangedListener(this);

        submitButton = (Button) findViewById(R.id.id_common_guide_button);
        submitButton.setText(R.string.com_confirm);
        submitButton.setOnClickListener(this);
        submitButton.setEnabled(false);
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        if (view == questionView){
            toChooseQuestion();
        } else if (view == submitButton && isInputValid()){
            submit();
        }
    }

    private boolean isInputValid(){
        Editable content = etAnswer.getText();
        if (content == null || content.toString().trim().length() == 0){
            ToastUtil.toast(this, "请输入答案");
            return false;
        }
        return true;
    }

    /**
     * 提交问题与答案
     */
    private void submit(){
        BusinessRequest request = RegisterRequestFactory.addUserQuestion(
                this,
                ApplicationEx.getInstance().getUser().getLoginName(),
                current.QuestionId,
                current.QuestionContent,
                current.QuestionType,
                etAnswer.getText().toString());
        request.setIHttpRequestEvents(submitHandler).execute();
    }

    /**
     * 选择问题
     */
    private void toChooseQuestion(){
        if (questions.isEmpty()) {
            RegisterRequestFactory.queryQuestionList(this).setIHttpRequestEvents(questionHandler).execute();
        } else {
            Intent intent = new Intent(this,PayPwdQuestionListActivity.class);
            intent.putExtra(PayPwdQuestionListActivity.KEY_QUESTION_LIST, questions);
            if (current != null) {
                intent.putExtra(PayPwdQuestion.class.getName(), current);
            }
            startActivityForResult(intent, REQUEST);
        }
    }

    /**
     * 跳转到找回支付密码的验证短信验证码环节
     */
    private void toFindPayPwd(){
        CommonRequestFactory.createGetSMSCode(this, ApplicationEx.getInstance().getUser().getLoginName(), "0", "228202")
                .setIHttpRequestEvents(messageHandler).execute();
    }

    private final int REQUEST = 1123;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }
        if (requestCode == REQUEST){
            submitButton.setEnabled(false);
            containerAnswer.setVisibility(View.VISIBLE);

            current = data.getParcelableExtra(PayPwdQuestion.class.getName());

            questionView.setLeftText(current.QuestionContent);

            etAnswer.setText(null);
            etAnswer.setHint(current.AnswerNote);
            //1="姓名" 2="日期"
            etAnswer.setInputType("2".equals(current.AnswerType) ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (current == null || s == null) {
            return;
        }
        int length = s.length(), minLength = 2,maxLength = 16;
        String pattern  = "^[\u4e00-\u9fa5]*$";
        if ("2".equals(current.AnswerType)){
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
