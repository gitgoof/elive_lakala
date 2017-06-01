package com.lakala.shoudan.util;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.lakala.library.util.StringUtil;
import com.lakala.platform.consts.BankBusid;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.shoudan.bll.service.CommonServiceManager;
import com.lakala.shoudan.common.OpenBankInfoCallback;
import com.lakala.shoudan.datadefine.OpenBankInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LMQ on 2015/12/1.
 */
public class SimpleCardNumberTextWatcher implements TextWatcher {
    private boolean checkCardBin = false;
    private Map<String,OpenBankInfo> binResultMap = new HashMap<>();
    private List<OpenBankInfoCallback> bankInfoCallbacks = new ArrayList<>();

    public SimpleCardNumberTextWatcher setCheckCardBin(boolean checkCardBin) {
        this.checkCardBin = checkCardBin;
        return this;
    }
    public SimpleCardNumberTextWatcher addCheckCardCallback(SimpleOpenBankInfoCallback callback){
        if(callback == null){
            return this;
        }
        bankInfoCallbacks.add(callback);
        return this;
    }
    public OpenBankInfo getOpenBankInfo(String cardNumber){
        if(TextUtils.isEmpty(cardNumber)){
            return null;
        }
        return binResultMap.get(cardNumber);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if(checkCardBin){
            toCheckCardBin(s);
        }
    }

    private void toCheckCardBin(Editable s) {
        String cardNumber = StringUtil.trim(s.toString());
        if (cardNumber.length() == 16 || cardNumber.length() == 19) {
            if(binResultMap.containsKey(cardNumber)){
                OpenBankInfo bankInfo1 = binResultMap.get(cardNumber);
                for (OpenBankInfoCallback callback : bankInfoCallbacks) {
                    callback.onSuccess(bankInfo1, "");
                }

            }
            checkBinTask(cardNumber);
        }
    }

    private void checkBinTask(final String cardNumber) {
        CommonServiceManager.getInstance().getCardBIN(BankBusid.TRANSFER, cardNumber, new OpenBankInfoCallback(){
            @Override
            public void onEvent(HttpConnectEvent connectEvent) {
                for (OpenBankInfoCallback callback : bankInfoCallbacks) {
                    callback.onEvent(connectEvent);
                }
            }

            @Override
            public void onSuccess(@Nullable OpenBankInfo openBankInfo,
                                  @Nullable String errMsg) {
                if(openBankInfo != null){
                    binResultMap.put(cardNumber,openBankInfo);
                }
                for (OpenBankInfoCallback callback : bankInfoCallbacks) {
                    callback.onSuccess(openBankInfo, errMsg);
                }
            }
        });
    }

    public static class SimpleOpenBankInfoCallback extends OpenBankInfoCallback {

        @Override
        public void onSuccess(@Nullable OpenBankInfo openBankInfo, @Nullable String errMsg) {

        }

        @Override
        public void onEvent(HttpConnectEvent connectEvent) {

        }
    }
}
