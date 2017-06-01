package com.lakala.ui.component;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;


public class FitTextWatcher implements TextWatcher{
	private TextView mTextView;
	private Paint mFitPaint;
	private float maxTextSize;
	private float minTextSize;
	private static final int MAX_TEXT_SIZE = 14;
	private static final int MIN_TEXT_SIZE = 10;
	private Context mContext;
	
	public FitTextWatcher(){}
	public FitTextWatcher(Context context){
		this.mContext = context;
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		
		//System.out.println("after intwidth:"+this.mTextView.getWidth());
		reFitText(s.toString(), this.mTextView.getWidth());
	}
	
	public FitTextWatcher(Context mContext, TextView textView){
		this.mTextView = textView;
		this.mContext = mContext;
		mFitPaint = textView.getPaint();
		
		maxTextSize = this.mTextView.getTextSize();
		if(maxTextSize<MIN_TEXT_SIZE || maxTextSize>MAX_TEXT_SIZE)
		{
			maxTextSize = MAX_TEXT_SIZE;
		}
		minTextSize = MIN_TEXT_SIZE;
	}
	
	public void reFitText(String text,int realdWidth){
		
		if("".equals(text)){
			return;
		}
		
		if(realdWidth>0){
			final int availableWith = mTextView.getWidth()-mTextView.getPaddingLeft()-mTextView.getPaddingRight();
	//		System.out.println("avaliWidth:"+availableWith);
	//		System.out.println("maxTextSize:"+maxTextSize);
			float tempSize = maxTextSize;
			while(tempSize>minTextSize && mFitPaint.measureText(text)>=availableWith){//大于最小值，且确定该文字大小在默认的情况下会超出textview范围
				//System.out.println("Paint:"+mFitPaint.measureText(text)+":"+text);
				tempSize -= 1;
				//System.out.println("tempsize:"+tempSize);
				if(tempSize <= minTextSize){//文字不能再小了
					tempSize = minTextSize;
					break;
				}
				mFitPaint.setTextSize(sp2px(tempSize, mContext));//调整画笔的文字大小的
				//System.out.println("after Paint:"+mFitPaint.measureText(text)+":"+text);
			}
			mTextView.setTextSize(tempSize);
		}
	}


    public  int sp2px(float spValue, Context context) {
        float scale = getDensity(context);
        return (int) (spValue * scale + 0.5f);
    }
    public float getDensity(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }
	
}
