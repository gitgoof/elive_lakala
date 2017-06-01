package com.lakala.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lakala.ui.R;

/**
 * Created by More on 15/3/11.
 */
public class AlertInputDialog extends AlertDialog {

    private EditText editInput;

    @Override
    View middleView() {

        if (middle != null) {
            return middle;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        middle_root = inflater.inflate(R.layout.ui_alert_input_dialog, null);
        editInput = (EditText)middle_root.findViewById(R.id.edit_input);

        return middle_root;

    }

    public EditText getEditInput() {
        return editInput;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(editInput != null){
            editInput.setFocusable(true);
            editInput.setFocusableInTouchMode(true);
            editInput.requestFocus();
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams
            .SOFT_INPUT_STATE_VISIBLE);
//            InputMethodManager inManager = (InputMethodManager)editInput.getContext().getSystemService(
//                    Context.INPUT_METHOD_SERVICE);
//            inManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
