package com.lakala.shoudan.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.lakala.shoudan.activity.shoudan.Constants;
import com.lakala.shoudan.component.DialogCreator;
import com.lakala.ui.dialog.ProgressDialog;

public class BaseFragment extends Fragment {

    boolean isAlive = false;
    protected FragmentActivity context;
    private ProgressDialog progressDialog;
    protected FragmentManager manager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = ((FragmentActivity) activity);
        manager = context.getSupportFragmentManager();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isAlive = true;
    }

    @Override
    public void onDestroy() {
        isAlive = false;
        hideProgressDialog();
        super.onDestroy();
    }

    /**
     * Activity 跳转
     *
     * @param targetActivity
     */
    protected void startActivity(Class targetActivity) {
        Intent intent = getActivity().getIntent();
        intent.setClass(getActivity(), targetActivity);
        intent.putExtra(Constants.IntentKey.MODIFY_ACCOUNTINFO, true);
        startActivity(intent);
    }

    protected void showMessage(String content) {
        if (!isAlive) {
            return;
        }
        DialogCreator.createOneConfirmButtonDialog(context, "确定", content, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    protected void showProgressWithNoMsg() {
        if (!isAlive) {
            return;
        }
        showProgressDialog("");

    }

    /**
     * 隐藏进度条
     */
//    public void hideProgressDialog(){
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(progressDialog != null && progressDialog.isShowing()){
//                    progressDialog.dismiss();
//                    progressDialog = null;
//                }
//            }
//        }, 800);
//
//    }
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /**
     * 显示一个进度条
     */
    public void showProgressDialog(String content) {
        if (!isAlive) {
            return;
        }
        if (progressDialog != null && TextUtils.equals(content, progressDialog.getMessage())) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = DialogCreator.createProgressDialog(getActivity(), content);
        progressDialog.show();
    }
}
