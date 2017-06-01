package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by huangjp on 2015/12/18.
 */
public class VerifyUserQuestionRequest extends CommonBaseRequest {
    private String questionId;
    private String answer;
    private String mobile;//手机号
    public VerifyUserQuestionRequest(Context context) {
        super(context);
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
