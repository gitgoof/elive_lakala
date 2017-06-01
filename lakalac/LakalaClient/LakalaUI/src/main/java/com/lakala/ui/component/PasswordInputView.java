package com.lakala.ui.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lakala.ui.R;

/**
 * 自定义密码输入组件
 * 
 * @author Administrator
 * 
 */
public class PasswordInputView extends EditText {

	private int start;
	private String text;

	private Bitmap bmp;
	private int maxLength = 6;
	private int bgColor = Color.GRAY;
	private int dividerColor = Color.GRAY;

	public PasswordInputView(Context context, AttributeSet attrs) {
		super(context, attrs);

		bmp = BitmapFactory.decodeResource(context.getResources(),R.drawable.ui_pw_is_set);
		this.setSingleLine(true);
		this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        this.setLongClickable(false);
        this.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

    /***
     * 设置密码输入框颜色
     */
	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

    /***
     * 设置密码输入框内分割线颜色
     */
	public void setDividerColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public Bitmap getBmp() {
		return bmp;
	}

	/***
	 * 设置密码覆盖图片
	 * 
	 * @param bmp
	 */
	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * 设置密码的最大长度
	 * 
	 * @param maxLength
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int disWidth = getWidth();
		int disHeight = getHeight();

		int paddingLeft = getPaddingLeft();
		int paddingRight = getPaddingRight();
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();

		RectF rect = new RectF(0, 0, disWidth, disHeight);
		RectF rectIn = new RectF(rect.left + 1, rect.top + 1, rect.right - 2,rect.bottom - 2);
		Paint paint = getPaint();
		int oldColor = paint.getColor();
		paint.setColor(bgColor);
		canvas.drawRoundRect(rect, 10, 10, paint);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(rectIn, 10, 10, paint);
		paint.setColor(dividerColor);

		for (int i = 1; i < maxLength; i++) {
			canvas.drawLine(paddingLeft+ (disWidth - paddingLeft - paddingRight) * i / maxLength,
							paddingTop, paddingLeft+ (disWidth - paddingLeft - paddingRight) * i/ maxLength,
							disHeight - paddingBottom, paint);
		}

		paint.setColor(oldColor);
		int textLeftPading = (int) (((disWidth - paddingLeft - paddingRight)
							/ maxLength - bmp.getWidth()) / 2);

		if (start <= 0 && text != null && text.length() > 0) {
			canvas.drawBitmap(bmp, paddingLeft+ (disWidth - paddingLeft - paddingRight) * start
					/ maxLength + textLeftPading,(disHeight - bmp.getHeight()) / 2, paint);
		} else {
			for (int i = 0; i <= start; i++) {
				if (text != null && text.length() > start)
					canvas.drawBitmap(bmp, paddingLeft+ (disWidth - paddingLeft - paddingRight) * i
					/ maxLength + textLeftPading,(disHeight - bmp.getHeight()) / 2, paint);
			}
		}
		// super.onDraw(canvas);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start,int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		this.text = text.toString();
		this.start = lengthBefore > lengthAfter ? --start : start;
		invalidate();
	}
	
}