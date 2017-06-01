package com.lakala.shoudan.activity.shoudan.finance.bean.request;

import com.lakala.shoudan.common.net.volley.BaseRequest;

/**
 * Created by LMQ on 2015/10/12.
 */
public class AddUserQuestionRequest extends BaseRequest {
    private String QuestionId;//密保问题id
    private String QuestionContent;//密保问题内容
    private String QuestionType;//密保问题答案类型，1：姓名，2：日期
    private String Answer;

    public String getAnswer() {
        return Answer;
    }

    public AddUserQuestionRequest setAnswer(String answer) {
        Answer = answer;
        return this;
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public AddUserQuestionRequest setQuestionId(String questionId) {
        QuestionId = questionId;
        return this;
    }

    public String getQuestionContent() {
        return QuestionContent;
    }

    public AddUserQuestionRequest setQuestionContent(String questionContent) {
        QuestionContent = questionContent;
        return this;
    }

    public String getQuestionType() {
        return QuestionType;
    }

    public AddUserQuestionRequest setQuestionType(String questionType) {
        QuestionType = questionType;
        return this;
    }
}
