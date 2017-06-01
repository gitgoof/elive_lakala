package com.lakala.shoudan.activity.shoudan.finance.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by LMQ on 2015/10/12.
 */
public class Question implements Serializable {
    private String AnswerNote;
    private String QuestionId;
    private String QuestionContent;
    private String QuestionType;
    private String AnswerType;
    private QuestionFlagEnum QuestionFlag = QuestionFlagEnum.NULL;

    public Question() {
    }

    public Question (JSONObject data){
        QuestionId = data.optString("questionId");
        QuestionContent    = data.optString("questionContent");
        QuestionType      = data.optString("questionType");
        AnswerType      = data.optString("answerType");
        AnswerNote      = data.optString("answerNote");
    }
    public String getAnswerNote() {
        return AnswerNote;
    }

    public Question setAnswerNote(String answerNote) {
        AnswerNote = answerNote;
        return this;
    }

    public String getQuestionId() {
        return QuestionId;
    }

    public Question setQuestionId(String questionId) {
        QuestionId = questionId;
        return this;
    }

    public String getQuestionContent() {
        return QuestionContent;
    }

    public Question setQuestionContent(String questionContent) {
        QuestionContent = questionContent;
        return this;
    }

    public String getQuestionType() {
        return QuestionType;
    }

    public Question setQuestionType(String questionType) {
        QuestionType = questionType;
        return this;
    }

    public String getAnswerType() {
        return AnswerType;
    }

    public Question setAnswerType(String answerType) {
        AnswerType = answerType;
        return this;
    }

    public QuestionFlagEnum getQuestionFlagEnum() {
        return QuestionFlag;
    }
    public int getQuestionFlag(){
        return QuestionFlag.ordinal();
    }

    public Question setQuestionFlag(int question) {
        QuestionFlag = QuestionFlagEnum.未设置;
        QuestionFlagEnum[] values = QuestionFlagEnum.values();
        if(question >=0 && question < values.length){
            QuestionFlag = values[question];
        }
        return this;
    }

    public enum QuestionFlagEnum{
        未设置,已设置,NULL
    }
}
