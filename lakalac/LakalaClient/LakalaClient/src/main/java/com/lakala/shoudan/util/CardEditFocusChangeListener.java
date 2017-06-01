package com.lakala.shoudan.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
 * Created by LMQ on 2015/12/18.
 */
public class CardEditFocusChangeListener implements View.OnFocusChangeListener {
    private Map<String,OpenBankInfo> binResultMap = new HashMap<>();
    private List<OpenBankInfoCallback> bankInfoCallbacks = new ArrayList<>();

    /**
     * 自定义卡bin查询条件，true时查询，false时不查询
     *
     */
    private boolean customFlag = true;
    private BankBusid bankBusid = BankBusid.TRANSFER;

    public CardEditFocusChangeListener setBankBusid(BankBusid bankBusid) {
        this.bankBusid = bankBusid;
        return this;
    }

    public CardEditFocusChangeListener setCustomFlag(boolean customFlag) {
        this.customFlag = customFlag;
        return this;
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus && v instanceof EditText){
            EditText editText = ((EditText) v);
            String text = editText.getText().toString();
            if(TextUtils.isEmpty(text)){
                return;
            }
            if(customFlag){
                toCheckCardBin(text);
            }
        }
    }
    public boolean isBankInfoValid(String cardNumber,OpenBankInfo openBankInfo){
        OpenBankInfo info = getOpenBankInfo(cardNumber);
        return isBankInfoValid(openBankInfo, info);
    }

    public boolean isBankInfoValid(OpenBankInfo openBankInfo, OpenBankInfo info) {
        boolean isValid = true;
        if(openBankInfo == null || info == null){
            isValid = false;
        }else if(!TextUtils.equals(info.bankCode, openBankInfo.bankCode)){
            isValid = false;
        }else if(!TextUtils.equals(info.bankname,openBankInfo.bankname)){
            isValid = false;
        }else if(!TextUtils.equals(info.acccountType,openBankInfo.acccountType)){
            isValid = false;
        }
        return isValid;
    }

    public OpenBankInfo getOpenBankInfo(String cardNumber){
        if(TextUtils.isEmpty(cardNumber)){
            return null;
        }
        return binResultMap.get(cardNumber);
    }
    private void toCheckCardBin(String text) {
        String cardNumber = text.trim();
        if (text.length() >= 14 && text.length() <= 19) {
            if(binResultMap.containsKey(cardNumber)){
                OpenBankInfo bankInfo1 = binResultMap.get(cardNumber);
                for (OpenBankInfoCallback callback : bankInfoCallbacks) {
                    callback.onSuccess(bankInfo1, "");
                }
                return;
            }
            checkBinTask(cardNumber);
        }
    }
    public CardEditFocusChangeListener addCheckCardCallback(SimpleOpenBankInfoCallback callback){
        if(callback == null){
            return this;
        }
        bankInfoCallbacks.add(callback);
        return this;
    }
    public void checkBinTask(String cardNumber, final OpenBankInfoCallback callback){
        CommonServiceManager.getInstance().getCardBIN(bankBusid, cardNumber, callback);
    }
    public void checkBinTask(final String cardNumber) {
        CommonServiceManager.getInstance().getCardBIN(bankBusid, cardNumber, new OpenBankInfoCallback(){
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
