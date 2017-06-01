package com.lakala.shoudan.activity.shoudan.finance.question;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.VolleyError;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;
import com.lakala.shoudan.common.util.Util;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.VerifyQuestionRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import android.widget.Button;

import org.json.JSONObject;

/**
 * Created by LMQ on 2015/10/13.
 */
public class VerifyQuestionActivity extends AppBaseActivity {
    protected TextView tvQuestion;
    protected EditText etAnswer;
    protected TextView tvForget;
    private TextView idCommonGuideButton;
    private Question question;

    public static void open(Context context,Question question){
        Intent intent = new Intent(context,VerifyQuestionActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO, JSON.toJSONString(question));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_question);
        String json = getIntent().getStringExtra(Constants.IntentKey.TRANS_INFO);
        question = JSON.parseObject(json,Question.class);
        initUI();
        setQuestion2View(question);
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("密保问题");
        initView();
    }

    private void setQuestion2View(Question question) {
        if(question==null){
            LogUtil.print("question is null");
            return;
        }
        if (TextUtils.isEmpty(question.getQuestionContent()) || TextUtils.isEmpty(question.getAnswerNote())){
            LogUtil.print("question's content is null");
            return;
        }else{
            tvQuestion.setText(question.getQuestionContent());
            etAnswer.setHint(question.getAnswerNote());
        }

    }

    private void initView() {
        tvQuestion = (TextView) findViewById(R.id.tv_question);
        etAnswer = (EditText) findViewById(R.id.et_answer);
        tvForget = (TextView) findViewById(R.id.tv_forget);
        tvForget.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,ForgetAnswerActivity.class);
                        startActivity(intent);
                    }
                }
        );
        idCommonGuideButton = (TextView) findViewById(R.id.id_common_guide_button);
        idCommonGuideButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String answer = etAnswer.getText().toString();
                        verifyQuestion(question, answer);
                    }
                }
        );
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                idCommonGuideButton.setEnabled(s.length() != 0);
            }
        };
        etAnswer.addTextChangedListener(watcher);
    }

    protected void verifyQuestion(Question question, String answer) {
        if(TextUtils.isEmpty(question.getQuestionId())){
            return;
        }
        VerifyQuestionRequest request = new VerifyQuestionRequest();
        request.setQuestionId(question.getQuestionId());
        request.setAnswer(answer);
        request.setQuestionType(request.getQuestionType());
        HttpResponseListener listener = new HttpResponseListener() {
            @Override
            public void onStart() {
                showProgressWithNoMsg();
            }

            @Override
            public void onFinished(ReturnHeader returnHeader,
                                   JSONObject responseData) {
                hideProgressDialog();
                if(returnHeader.isSuccess()){
                    CaptchaPayActivity.open(context);
                    finish();
                }else{
                    ToastUtil.toast(context, returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();
                ToastUtil.toast(context,R.string.socket_fail);

            }
        };
        FinanceRequestManager.getInstance().verifyQuestion(request,listener);
    }
}
