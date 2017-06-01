package com.lakala.platform.bean;

/**
 * Created by More on 15/12/8.
 */
public enum  UserAuthStatus {

//    true未通过,可以被修改(null，“REJECT”，“NONE" 可以提交)
//            *  false通过不可被修改( PASS，APPLY不能提交)

    REJECT(true),
    NONE(true),
    PASS(false),
    APPLY(false);

    boolean value;

    public boolean getValue() {
        return value;
    }

    UserAuthStatus(boolean value) {
        this.value = value;
    }
}
