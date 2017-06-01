package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/12.
 */
public class VerifyQuestionRequest extends BaseRequest {
    private String QuestionId;
    private String Answer;
    private String QuestionType;

    public String getQuestionId() {
        return QuestionId;
    }

    public VerifyQuestionRequest setQuestionId(String questionId) {
        QuestionId = questionId;
        return this;
    }

    public String getAnswer() {
        return Answer;
    }

    public VerifyQuestionRequest setAnswer(String answer) {
        Answer = answer;
        return this;
    }

    public String getQuestionType() {
        return QuestionType;
    }

    public VerifyQuestionRequest setQuestionType(String questionType) {
        QuestionType = questionType;
        return this;
    }
}
