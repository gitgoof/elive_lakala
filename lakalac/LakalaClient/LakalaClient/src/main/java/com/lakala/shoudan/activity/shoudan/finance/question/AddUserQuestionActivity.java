package com.lakala.shoudan.activity.shoudan.finance.question;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lakala.library.util.ToastUtil;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.InputPayPwdDialogActivity;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.AddUserQuestionRequest;
import com.lakala.shoudan.activity.shoudan.finance.bean.request.FundSignUpRequest;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.common.net.volley.HttpResponseListener;
import com.lakala.shoudan.common.net.volley.ReturnHeader;

import org.json.JSONObject;

/**
 * 新增密保问题
 * Created by LMQ on 2015/10/12.
 */
public class AddUserQuestionActivity extends AppBaseActivity {
    private static final int QUESTION_LIST = 0x2312;
    private static final int INPUT_PWD_REQUEST = 0x1243;
    private TextView tvQuestion;
    private EditText etAnswer;
    private Button idCommonGuideButton;
    private FundSignUpRequest signUpRequest = null;
    private TextWatcher answerTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(TextUtils.isEmpty(s.toString())||s.toString().length()<2){
                idCommonGuideButton.setEnabled(false);
            }else {
                idCommonGuideButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public static void open(Context context,FundSignUpRequest request){
        Intent intent = new Intent(context,AddUserQuestionActivity.class);
        intent.putExtra(Constants.IntentKey.TRANS_INFO,JSON.toJSONString(request));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_userquestion);
        String json = getIntent().getStringExtra(Constants.IntentKey.TRANS_INFO);
        signUpRequest = JSON.parseObject(json,FundSignUpRequest.class);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setBackBtnVisibility(View.GONE);
        navigationBar.setTitle("设置密保问题");
        initView();
    }

    @Override
    public void onBackPressed() {
    }

    private void initView() {
        tvQuestion = (TextView) findViewById(R.id.tv_question);
        etAnswer = (EditText) findViewById(R.id.et_answer);
        idCommonGuideButton = (Button) findViewById(R.id.id_common_guide_button);
        tvQuestion.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QuestionListQryActivity.startForResult(context, QUESTION_LIST);
                    }
                }
        );
        idCommonGuideButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object obj = tvQuestion.getTag();
                        boolean isQuestion = false;
                        if (obj instanceof Question) {
                            isQuestion = true;
                        }
                        if (!isQuestion) {
                            ToastUtil.toast(context, "请选择密保问题");
                            return;
                        }
                        String answer = etAnswer.getText().toString();
                        if (TextUtils.isEmpty(answer)) {
                            ToastUtil.toast(context,"请输入密保答案");
                            return;
                        }
                        Question question = (Question) obj;
                        toSetQuestionAnswer(answer, question);
                    }
                }
        );
        etAnswer.addTextChangedListener(answerTextWatcher);
    }

    private void toSetQuestionAnswer(String answer, Question question) {
        AddUserQuestionRequest request = new AddUserQuestionRequest();
        request.setQuestionId(question.getQuestionId());
        request.setQuestionContent(question.getQuestionContent());
        request.setQuestionType(question.getQuestionType());
        request.setAnswer(answer);
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

                    String pwd = signUpRequest.getTrsPassword();
                    if(TextUtils.isEmpty(pwd)){
                        Intent intent = new Intent(context,InputPayPwdDialogActivity.class);
                        startActivityForResult(intent,INPUT_PWD_REQUEST);
                    }else{
                        FinanceRequestManager.getInstance().startSignUp(AddUserQuestionActivity.this,
                                                                        signUpRequest);
                    }
                }else{
                    ToastUtil.toast(context,returnHeader.getErrMsg());
                }
            }

            @Override
            public void onErrorResponse() {
                hideProgressDialog();

                ToastUtil.toast(context,R.string.socket_fail);
            }
        };
        FinanceRequestManager.getInstance().addUserQuestion(request,listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == QUESTION_LIST && resultCode == RESULT_OK){
            String json = data.getStringExtra(Constants.IntentKey.TRANS_INFO);
            Question question = JSON.parseObject(json,Question.class);
            tvQuestion.setTag(question);
            tvQuestion.setText(question.getQuestionContent());
        }else if(requestCode == INPUT_PWD_REQUEST && resultCode == RESULT_OK){
            String pwd = data.getStringExtra(Constants.IntentKey.PASSWORD);
            signUpRequest.setTrsPassword(pwd);
            FinanceRequestManager.getInstance().startSignUp(AddUserQuestionActivity.this,
                                                            signUpRequest);
        }
    }
}
