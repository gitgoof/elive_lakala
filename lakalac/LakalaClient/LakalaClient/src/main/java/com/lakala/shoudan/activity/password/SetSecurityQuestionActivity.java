package com.lakala.shoudan.activity.password;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.exception.BaseException;
import com.lakala.library.net.HttpRequestParams;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.http.BusinessRequest;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.request.RequestFactory;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.platform.swiper.mts.paypwd.RegisterRequestFactory;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.safetymanagement.SafetyManagementActivity;
import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.activity.shoudan.finance.bean.Question;
import com.lakala.shoudan.activity.wallet.WalletTransferActivity;
import com.lakala.shoudan.activity.wallet.request.SubmitQuestAnsRequest;
import com.lakala.shoudan.activity.wallet.request.WalletBaseRequest;
import com.lakala.shoudan.activity.wallet.request.WalletServiceManager;
import com.lakala.shoudan.common.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**设置密保问题
 * Created by HJP on 2015/11/13.
 */
public class SetSecurityQuestionActivity extends AppBaseActivity {
    private static final int QUESTION_LIST = 0x2312;
    public static final String KEY_QUESTION_LIST = "KEY_QUESTION_LIST";
    public static final String KEY_QUESTION_TAG = "KEY_QUESTION_TAG";
    private TextView tvSelectQuestion;
    private TextView btnYes;//确定
    private EditText etAnswer;
    private List<Question> questions = new ArrayList<Question>();
    private Question current;
    private boolean tag=false;
    private String tagString;
    private boolean hasTag=false;
    private boolean isFromWallet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_userquestion);
        init();
    }
    public void init(){
        tagString=getIntent().getStringExtra(SetSecurityQuestionActivity.KEY_QUESTION_TAG);
        if(!TextUtils.isEmpty(tagString)){
            hasTag=true;
        }
        isFromWallet = getIntent().getBooleanExtra("isFromWallet",false);
        initUI();
        tvSelectQuestion=(TextView)findViewById(R.id.tv_question);
        btnYes=(TextView)findViewById(R.id.id_common_guide_button);
        etAnswer=(EditText)findViewById(R.id.et_answer);
        tvSelectQuestion.setOnClickListener(this);
        btnYes.setOnClickListener(this);
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("设置密保问题");
        if(hasTag){
            navigationBar.setBackBtnVisibility(View.GONE);
            return;
        }
    }

    @Override
    public void onBackPressed() {
        if(hasTag){
            navigationBar.setBackBtnVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.id_common_guide_button:
                if(btnYes.isEnabled()){
                    submitQuestAns();
                }
                break;
            case R.id.tv_question:
                toChooseQuestion();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == QUESTION_LIST && resultCode==RESULT_OK){
            current = (Question) data.getSerializableExtra(Parameters.SECURITY_QUESTION);
            tvSelectQuestion.setText((current.getQuestionContent()).toString());
            etAnswer.setHint((current.getAnswerNote().toString()));
            //1="姓名" 2="日期"
            etAnswer.setInputType("2".equals(current.getAnswerType()) ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT);
            etAnswer.addTextChangedListener(textWatcher);
        }
    }
    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            btnYes.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(TextUtils.isEmpty(etAnswer.getText().toString().trim())){
                btnYes.setEnabled(false);
                etAnswer.setHint((current.getAnswerNote().toString()));
            }else{
                btnYes.setEnabled(true);
            }
        }
    };

    /**
     * 获取密保问题列表
     */
    private void getSecurityQuestionList(){
        showProgressWithNoMsg();
        BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.GET_SECURITY_QUESTION_LIST);
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                if (resultServices.isRetCodeSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultServices.retData);
                        JSONArray array = jsonObject.optJSONArray("questionList");
                        questions.clear();
                        for (int i = 0; i < array.length(); i++) {
                            questions.add(new Question(array.optJSONObject(i)));
                        }
                        if (!questions.isEmpty()) {
                            toChooseQuestion();
                        } else {
                            ToastUtil.toast(SetSecurityQuestionActivity.this, "密保问题查询失败,请重试");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    toast(resultServices.retMsg);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(context,businessRequest);
    }

    /**
     * 选择问题
     */
    private void toChooseQuestion(){
        if (questions.isEmpty()) {
            getSecurityQuestionList();
        } else {
            Intent intent=new Intent(SetSecurityQuestionActivity.this,SelectSecurityQuestionActivity.class);
            intent.putExtra(SetSecurityQuestionActivity.KEY_QUESTION_LIST, (Serializable) questions);
            startActivityForResult(intent,QUESTION_LIST);
        }
    }

    /**
     * 提交问题与答案
     */
    private void submitQuestAns(){
        showProgressWithNoMsg();
        BusinessRequest businessRequest= RequestFactory.getRequest(this, RequestFactory.Type.SET_SECURITY_QUESTION);
        SubmitQuestAnsRequest params=new SubmitQuestAnsRequest(context);
        params.setQuestionId(current.getQuestionId());
        params.setQuestionContent(current.getQuestionContent());
        params.setQuestionType(current.getQuestionType());
        params.setAnswer(etAnswer.getText().toString());
        params.setMobile(ApplicationEx.getInstance().getUser().getLoginName());
        businessRequest.setResponseHandler(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                hideProgressDialog();
                WalletServiceManager.getInstance().setQuestionFlag("1");
                WalletServiceManager.getInstance().setNoPwdFlag("0");
                if (resultServices.isRetCodeSuccess()) {

                    ToastUtil.toast(context, "设置密保成功");
                    if (isFromWallet) {
                        WalletTransferActivity.isSetQuestion = true;
                        BusinessLauncher.getInstance().clearTop(WalletTransferActivity.class);
                    } else {

                        WalletServiceManager.getInstance().setTrsPasswordFlag("1");
                        BusinessLauncher.getInstance().clearTop(SafetyManagementActivity.class);
                    }

                } else {

                    ToastUtil.toast(context, "设置密保失败");
                    toast(resultServices.retMsg);

                    if (isFromWallet) {
                        BusinessLauncher.getInstance().clearTop(WalletTransferActivity.class);
                    } else {
                        WalletServiceManager.getInstance().setTrsPasswordFlag("0");
                        BusinessLauncher.getInstance().clearTop(SafetyManagementActivity.class);
                    }


                }

            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                hideProgressDialog();
                ToastUtil.toast(context, R.string.socket_fail);
                LogUtil.print(connectEvent.getDescribe());
            }
        });
        WalletServiceManager.getInstance().start(params, businessRequest);
    }

}
