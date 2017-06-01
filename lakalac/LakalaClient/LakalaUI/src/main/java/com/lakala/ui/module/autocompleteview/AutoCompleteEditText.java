package com.lakala.ui.module.autocompleteview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * 增加Dissmiss监听，支持API17以下，API17以上请使用android提供方法
 * onTouchOutSide事件需要重写activity的onTouch事件
 *
 * Created by jerry on 14-1-14.
 */
public class AutoCompleteEditText extends AutoCompleteTextView{

    private OnDropDownDissmissListener mDissmissListener;

    /** 监听下拉框是否消失 */
    public interface OnDropDownDissmissListener{
        void onDropDownDissmiss();
    }

    public AutoCompleteEditText(Context context) {
        super(context);
    }

    public AutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDropDownDissmissListener(OnDropDownDissmissListener mDissmissListener) {
        this.mDissmissListener = mDissmissListener;
    }

    @Override
    public void dismissDropDown() {
        super.dismissDropDown();
        if (mDissmissListener != null){
            mDissmissListener.onDropDownDissmiss();
        }
    }

    @Override
    public void onFilterComplete(int count) {
        super.onFilterComplete(count);
        if (count <= 0 && mDissmissListener != null) {
            mDissmissListener.onDropDownDissmiss();
        }
    }


}
