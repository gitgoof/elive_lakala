package com.lakala.shoudan.activity.myaccount;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
/**
 * 银行卡号监听
 * @author zmy
 *
 */
public class BackAccountNoTextWatcher implements TextWatcher {

	private EditText bankAccountNo;

	String before;
	int cursorIndex ;

	public BackAccountNoTextWatcher(EditText bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
		cursorIndex = this.bankAccountNo.getSelectionEnd();
	}

	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i2,
			int i3) {
		before = charSequence.toString();
	}

	@Override
	public void onTextChanged(CharSequence charSequence, int start, int before,
			int count) {

	}

	@Override
	public void afterTextChanged(Editable editable) {

		StringBuffer str = new StringBuffer(editable.toString());
		if (str.toString().contains(" ")) {
			bankAccountNo.setText(str.toString().replace(" ", ""));
			bankAccountNo.setSelection(cursorIndex);
		}
		Pattern pattern = Pattern.compile("[\\d]");
		Matcher matcher = pattern.matcher(str);
		if (!"".equals(matcher.replaceAll("").trim())) {
			bankAccountNo.setText(before);
			bankAccountNo.setSelection(cursorIndex);
		}

	}
}
