package com.lakala.elive.common.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.lakala.elive.R;

public class DialogUtil {


    public static Dialog createAlertDialog(Context context,
                                           String title,
                                           String message,
                                           String btnNegatitle,
                                           String btnPosititle,
                                           DialogInterface.OnClickListener mListener) {
        // 创建退出对话框
        AlertDialog mDialog = new AlertDialog.Builder(context).create();
        // 设置对话框标题
        mDialog.setTitle(title);
        // 设置对话框消息
        mDialog.setMessage(message);
        // 添加选择按钮并注册监听
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, btnNegatitle, mListener);
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, btnPosititle, mListener);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    public static Dialog createAlertDialog(Context context,
                                           String title,
                                           String message,
                                           String btnPosititle,
                                           DialogInterface.OnClickListener mListener) {
        // 创建退出对话框
        AlertDialog mDialog = new AlertDialog.Builder(context).create();
        // 设置对话框标题
        mDialog.setTitle(title);
        // 设置对话框消息
        mDialog.setMessage(message);
        // 添加选择按钮并注册监听
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, btnPosititle, mListener);
        mDialog.setCanceledOnTouchOutside(false);
        return mDialog;
    }

    public static void createAlertDialog(Context context,final OnClickForDialog onClickForDialog){
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog.setContentView(R.layout.dialog_check_task_layout);
        final View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.tv_dialog_check_task_continue:
                        if (onClickForDialog!=null){
                            onClickForDialog.continueClick();
                        }
                        break;
                    case R.id.tv_dialog_check_task_new:
                        if (onClickForDialog!=null){
                            onClickForDialog.toNewClick();
                        }
                        break;
                    case R.id.tv_dialog_check_task_cancel:
                        if (onClickForDialog!=null){
                            onClickForDialog.toCancelClick();
                        }
                        break;
                }
                if( alertDialog!= null && alertDialog.isShowing()){
                    alertDialog.dismiss();
                }
            }
        };
        alertDialog.findViewById(R.id.tv_dialog_check_task_continue).setOnClickListener(listener);
        alertDialog.findViewById(R.id.tv_dialog_check_task_new).setOnClickListener(listener);
        alertDialog.findViewById(R.id.tv_dialog_check_task_cancel).setOnClickListener(listener);

        alertDialog.setCanceledOnTouchOutside(false);
    }
    public interface OnClickForDialog{
        void continueClick();
        void toNewClick();
        void toCancelClick();
    }
}
