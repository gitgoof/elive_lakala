package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by huangjp on 2015/12/17.
 */
public class SubmitQuestAnsRequest extends CommonBaseRequest {
    public SubmitQuestAnsRequest(Context context) {
        super(context);
    }
    private String questionId;//密保问题Id
    private String questionContent;//密保问题内容
    private String questionType;//密保问题类型
    private String answer;//密保问题答案
    private String mobile;//手机号
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
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
