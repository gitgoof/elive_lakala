package com.lakala.shoudan.component;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by HUASHO on 2015/1/20.
 * 金额输入检测，限制小数点后边只能输入两个数字
 */
public class MoneyInputWatcher implements TextWatcher{
    /** 是否允许输入0      */
    private static boolean isAllowZero = false;

    public MoneyInputWatcher() {
        isAllowZero = false;
    }
    public  void init() {
        isAllowZero = true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String input = s.toString();
        if (isAllowZero) {
            if(input.startsWith("0")){
                s.delete(0, 1);
            }
        }
        if (input.length() == 1 && ".".equals(input)) {
            s.delete(0, 1);
        }
        //计算小数点前有效位数
        int length = (input.contains(".")) ? input.indexOf(".") : input.length();

        //如果小数点前的位数大于8，则删除小数点前的那一位数字
        if (length > 9) {
            s.delete(length-1, length);
        }
        //小数点后只能输入两位
        int indexOfDot = input.indexOf(".");
        if (indexOfDot <= 0)
            return;
        if (input.length() - indexOfDot - 1 > 2) {
            s.delete(indexOfDot + 3, indexOfDot + 4);
        }
    }
}
