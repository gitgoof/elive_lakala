package com.lakala.shoudan.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by LMQ on 2015/12/22.
 */
public class HintFocusChangeListener implements View.OnFocusChangeListener {
    private String hint;
    private boolean useCustomKeyboard = false;

    public HintFocusChangeListener setUseCustomKeyboard(boolean useCustomKeyboard) {
        this.useCustomKeyboard = useCustomKeyboard;
        return this;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(v instanceof EditText){
            EditText editText = (EditText)v;
            if(hasFocus){
                hint = editText.getHint() == null?"":editText.getHint().toString();
                editText.setHint("");
                if(!useCustomKeyboard){
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText,InputMethodManager.SHOW_FORCED);
                }
            }else {
                editText.setHint(hint);
            }
        }
    }
}
