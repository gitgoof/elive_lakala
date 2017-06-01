package com.lakala.elive.user.base;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.elive.Session;
import com.lakala.elive.common.net.ApiRequestListener;

public abstract class BaseFragment extends Fragment implements ApiRequestListener,View.OnClickListener{

    protected  View  mView;
    protected  int   viewLayoutId;
    protected Session mSession;

    //进度条
    protected ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setViewLayoutId();
        mView = inflater.inflate(viewLayoutId, container, false);
        mSession = Session.get(getActivity());
        bindViewIds();
        return mView;
    }

    //设置页面布局ID
    protected abstract void setViewLayoutId();

    //绑定初始化页面组件
    protected abstract void bindViewIds();

    //加载初始化数据
    protected abstract void initData();


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    //刷新主界面
    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

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
            mProgressDialog = new ProgressDialog(getActivity());
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

    protected <VT extends View> VT findView(@IdRes int id) {
        return (VT) getActivity().findViewById(id);
    }


}
