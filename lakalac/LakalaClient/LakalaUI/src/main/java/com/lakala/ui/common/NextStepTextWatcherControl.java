package com.lakala.ui.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

/**
 * 下一步跳转时，Edit位数限制，提交按钮设置为不可用状态
 *
 * Created by jerry on 14-2-25.
 */
public class NextStepTextWatcherControl {

    private int count;
    private View mCommitBtn;
    private Map<Integer,Integer> mLenRule = new HashMap<Integer, Integer>();
    private Map<Integer,Integer> mInputLen = new HashMap<Integer, Integer>();

    public NextStepTextWatcherControl(HashMap<EditText, Integer> editTexts,View commitBtn) {
        if (editTexts == null || editTexts.size() <= 0)
            return;

        this.mCommitBtn = commitBtn;
        mCommitBtn.setEnabled(false);

        count = 0;
        mLenRule.clear();

        //为Edit添加输入监听
        for (Map.Entry<EditText,Integer> entry : editTexts.entrySet()){
            EditText editText = entry.getKey();
            int lengthRule = entry.getValue();
            editText.addTextChangedListener(new EditTextWather(count));
            mLenRule.put(count, lengthRule);
            count ++;
        }
    }

    class EditTextWather implements TextWatcher{

        int id;

        EditTextWather(int id) {
            this.id = id;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mInputLen.put(id,s.toString().length());
            int ruleLen = mLenRule.size();
            for (int i = 0; i < ruleLen; i++) {

                if (!mInputLen.containsKey(i)){
                    mCommitBtn.setEnabled(false);
                    return;
                }

                if (mInputLen.containsKey(i)) {
                    if (mInputLen.get(i) < mLenRule.get(i)) {
                        mCommitBtn.setEnabled(false);
                        return;
                    }
                    if (i == ruleLen - 1) {
                        mCommitBtn.setEnabled(true);
                    }

                }
            }
        }
    }

}
