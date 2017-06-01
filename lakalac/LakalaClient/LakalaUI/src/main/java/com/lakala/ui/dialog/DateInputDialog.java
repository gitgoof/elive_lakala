package com.lakala.ui.dialog;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.lakala.library.util.LogUtil;
import com.lakala.ui.R;

/**
 * Created by LMQ on 2015/3/9.
 */
public class DateInputDialog extends AlertDialog {
    private EditText txtDateContent;

    public String getDateText(){
        return txtDateContent.getText().toString();
    }
    @Override
    View middleView() {
        if (middle != null) {
            return middle;
        }
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        middle_root = inflater.inflate(R.layout.common_tixing_date_dialog, null);
        middle_layout = (FrameLayout) middle_root.findViewById(R.id.middle_layout);
        txtDateContent = (EditText) middle_root.findViewById(R.id.id_tixing_time_content);
        // 输入时间的时间
        txtDateContent.addTextChangedListener(new TextWatcher() {
            CharSequence temp;
            int editStart;
            int editEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    // 判断输入的日期范围1-28
                    editStart = txtDateContent.getSelectionStart();
                    editEnd = txtDateContent.getSelectionEnd();

                    if (!temp.toString().equals("")) {
                        if (Integer.valueOf(temp.toString()) > 28 || Integer
                                .valueOf(temp.toString()) <= 0) {
                            s.delete(editStart - 1, editEnd);
                            int tempSelection = editStart;
                            txtDateContent.setText(s);
                            txtDateContent.setSelection(tempSelection);
                        }
                    }
                } catch (Exception e) {
                    LogUtil.print("afterTextChanged", e);
                }
            }
        });
        return middle_root;
    }
}
