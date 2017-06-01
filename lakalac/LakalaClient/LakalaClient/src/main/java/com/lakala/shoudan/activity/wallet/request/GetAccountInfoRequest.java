package com.lakala.shoudan.activity.wallet.request;

import android.content.Context;

import com.lakala.shoudan.common.CommonBaseRequest;

/**
 * Created by fengxuan on 2015/12/19.
 * 提现查询收款人信息
 */
public class GetAccountInfoRequest extends CommonBaseRequest{
    private int signType = 1;

    public int getSignType() {
        return signType;
    }

    public void setSignType(int signType) {
        this.signType = signType;
    }

    public GetAccountInfoRequest(Context context) {
        super(context);
    }
}
