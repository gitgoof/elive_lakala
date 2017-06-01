package com.lakala.platform.bean;

/**
 * Created by LMQ on 2015/3/30.
 */
public enum ApplicationStatus {
    FAILURE("已驳回"),
    COMPLETED("已完成"),
    PROCESSING("已受理"),
    APPLYING("已提交"),
    NULL("");
    private String explanation;
    ApplicationStatus(String explanation){
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }
}
