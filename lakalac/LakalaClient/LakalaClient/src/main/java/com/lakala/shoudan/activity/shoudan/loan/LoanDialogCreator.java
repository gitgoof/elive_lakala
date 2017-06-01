package com.lakala.shoudan.activity.shoudan.loan;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.lakala.shoudan.R;

/**
 * Created by LMQ on 2015/12/7.
 */
public class LoanDialogCreator {
    public static Dialog showLoanSampleDialog(FragmentActivity context){
        final Dialog dialog = new Dialog(context, R.style.dialog_loan_example_with_bg);

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        View rootView = LayoutInflater.from(context).inflate(R.layout.layout_loan_example, null);
        dialog.setContentView(rootView);
        Button btnSure = (Button) rootView.findViewById(R.id.btn_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        return dialog;
    }
}
