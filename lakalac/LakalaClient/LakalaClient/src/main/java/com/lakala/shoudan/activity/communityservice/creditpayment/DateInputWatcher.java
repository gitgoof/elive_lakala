package com.lakala.shoudan.activity.communityservice.creditpayment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.lakala.library.util.LogUtil;

/**
 * 日期输入检测，限制输入1-28号之间的日期
 * @author SeaZhang
 *
 */
public class DateInputWatcher implements TextWatcher{
  CharSequence temp;
  int editStart;
  int editEnd;
  private EditText editText;
  
  public DateInputWatcher(EditText inputEdit) {
    this.editText = inputEdit;
  }
  
  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    temp = s;
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {}

  @Override
  public void afterTextChanged(Editable s) {
    try {
      // 判断输入的日期范围1-28
      editStart = editText.getSelectionStart();
      editEnd = editText.getSelectionEnd();

      if (!temp.toString().equals("")) {
        if (Integer.valueOf(temp.toString()) > 28) {
          s.delete(editStart - 1, editEnd);
          int tempSelection = editStart;
          editText.setText(s);
          editText.setSelection(tempSelection);
        }
      }
    } catch (Exception e) {
      LogUtil.e("TiXingService1_AddActivity.afterTextChanged()", e.getMessage());
      LogUtil.print(e);
    }
  }
}
