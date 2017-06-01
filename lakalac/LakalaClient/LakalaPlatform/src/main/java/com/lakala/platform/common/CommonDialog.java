package com.lakala.platform.common;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.lakala.platform.R;
import com.lakala.ui.dialog.AlertDialog;

/**
 * Created by More on 15/1/16.
 */
public class CommonDialog {


    public static void showMessageAndExit(String message, final Context context){

        AlertDialog alertDialog = new AlertDialog();
        alertDialog.setTitle(context.getString(R.string.ui_tip));
        alertDialog.setMessage(message);
        alertDialog.setButtons(new String[]{context.getString(R.string.ui_certain)});
        alertDialog.setDialogDelegate(new AlertDialog.AlertDialogDelegate(){
            @Override
            public void onButtonClick(AlertDialog dialog, View view, int index) {
                switch (index){
                    case 0:
                        ((Activity)context).finish();
                        break;

                }
            }
        });

        alertDialog.show(((FragmentActivity)context).getSupportFragmentManager());
    }



}
