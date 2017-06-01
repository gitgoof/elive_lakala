package com.lakala.shoudan.activity.shoudan.finance.bean;

import java.util.List;

/**
 * Created by LMQ on 2015/10/12.
 */
public class QuestionListReturnData {
    private List<Question> List;

    public java.util.List<Question> getList() {
        return List;
    }

    public QuestionListReturnData setList(java.util.List<Question> list) {
        List = list;
        return this;
    }
}
