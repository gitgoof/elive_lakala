package com.lakala.shoudan.activity.loan;

import android.content.Context;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.TakePicture;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.common.ApplicationEx;

/**
 * Created by Administrator on 2016/12/26.
 */
public class TestBridge extends TakePicture {
    @Override
    public void handler(String data, CallBackFunction function, Context context) {
        super.handler(data, function, context);
        ToastUtil.toast(ApplicationEx.getInstance(), data);
    }

}
