package com.lakala.shoudan.loginservice;

/**
 * Created by More on 15/3/21.
 */
public interface TokenRefreshCallback {

    void onFinish();

    void onInterrupted();

}
