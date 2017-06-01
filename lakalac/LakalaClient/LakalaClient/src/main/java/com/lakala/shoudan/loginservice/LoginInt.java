package com.lakala.shoudan.loginservice;

import com.lakala.platform.response.ResultServices;

/**
 * Created by More on 15/3/21.
 */
public interface LoginInt {

    void onLoginFinished(ResultServices resultServices);

    void onLoginFailed();

    void onTokenUpdateSucceed(ResultServices resultServices);

    void onTokenUpdateFailed();

    void onMerchantStateChecked(boolean b);

    void onMerchantStateCheckFailed();

    void onMerchantInfoUpdateSucceed(ResultServices resultServices);

    void onMerchantInfoUpdateFailed();
}
