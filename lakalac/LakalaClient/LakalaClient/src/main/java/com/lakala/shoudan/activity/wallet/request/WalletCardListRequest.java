package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

/**
 * Created by fengxuan on 2015/12/18.
 */
public class WalletCardListRequest extends WalletBaseRequest{

    private int signType = 1;

    public int getSignType() {
        return signType;
    }

    public void setSignType(int signType) {
        this.signType = signType;
    }

    public WalletCardListRequest(Context context) {
        super(context);
    }
}
