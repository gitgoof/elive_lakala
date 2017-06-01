package com.lakala.platform.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.R;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.ui.dialog.AlertDialog;
import com.lakala.ui.dialog.BaseDialog;

/**
 * Created by More on 15/3/20.
 */
public class ErrorDialogActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static BaseDialog alertDialog;

    public static void startSelf(final BaseDialog dialog,String tag){
        alertDialog = dialog;
        FragmentActivity context = ApplicationEx.getInstance().getActiveContext();
        if(BusinessLauncher.getInstance().getTopActivity() instanceof FragmentActivity){
            context = (FragmentActivity)BusinessLauncher.getInstance().getTopActivity();
        }
        if(context != null ){

            if(context.isFinishing()){

                Handler handler = new Handler(Looper.getMainLooper());

                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startSelf(dialog);
                                        }
                                    }, 1000);

            }else{

                try{

                    if(TextUtils.isEmpty(tag)){
                        alertDialog.show(context.getSupportFragmentManager());
                    }else{
                        FragmentManager manager = context.getSupportFragmentManager();
                        BaseDialog baseDialog = (BaseDialog)manager.findFragmentByTag(tag);
                        if(baseDialog == null || !baseDialog.isShowing()){
                            alertDialog.show(context.getSupportFragmentManager(),tag);
                        }
                    }

                }catch (Exception e){
                    LogUtil.print("Attendtion   Unacceptable Excetpion" ,e);
                }
            }

        }
    }

    public static void startSelf(final BaseDialog dialog){
        startSelf(dialog,null);
    }


    @Override
    public void onBackPressed() {

    }
}
