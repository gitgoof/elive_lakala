package com.lakala.elive.market.base;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.lakala.elive.common.net.ApiRequestListener;

public class BaseFragmentActivity extends FragmentActivity implements View.OnClickListener,ApiRequestListener {

    //进度条
    protected ProgressDialog mProgressDialog;

    @Override
    public void onSuccess(int method, Object obj) {

    }

    @Override
    public void onError(int method, String statusCode) {

    }

    @Override
    public void onClick(View v) {

    }


    public void showProgressDialog(String message) {
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }


    public void closeProgressDialog() {
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

}
