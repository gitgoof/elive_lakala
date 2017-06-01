package com.lakala.elive.map.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.lakala.elive.R;

/**
 * Created by gaofeng on 2017/3/30.
 * 自定义选项卡
 * 适用范围: 只适用于文字的多选，不是适应与转行的文字
 */
public class MapChooseViewGroup extends ViewGroup {

    public MapChooseViewGroup(Context context) {
        super(context);
    }

    public MapChooseViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapChooseViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
     * 向右的间隔距离
     */
    private final int MARCH_SPACE_LEFT = 30;
    private final int MARCH_SPACE_TOP = 20;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        int startWidth = 0;
        int startHeight = 0;
        int row = 0;
        final int rootWidth = getMeasuredWidth();
        for(int i = 0;i < count;i++){
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();
            startWidth += width;
            if( startWidth > rootWidth){
                row++;
                startWidth = width;
                startHeight +=MARCH_SPACE_TOP;
            }
            startHeight = row*(MARCH_SPACE_TOP+height);

            child.layout(startWidth-width,startHeight,startWidth,startHeight + height);
            startWidth += MARCH_SPACE_LEFT;

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthRoot = MeasureSpec.getSize(widthMeasureSpec);
        final int count = getChildCount();

        int startWidth = 0;
        int startHeight = 0;
        int row = 0;
        final int rootWidth = getMeasuredWidth();
        int lastChildHeight = 0;
        for(int i = 0;i < count;i++){
            final View child = getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();
            lastChildHeight = height;
            startWidth += width;
            if( startWidth > rootWidth){
                row++;
                startWidth = width;
                startHeight+=MARCH_SPACE_TOP;
            }
            startHeight = row*(MARCH_SPACE_TOP+height);
            startWidth +=MARCH_SPACE_LEFT;
        }
        setMeasuredDimension(widthRoot,startHeight+lastChildHeight);
    }

    private int mCheckedIndex = -1;
    private CheckedTextView currentChecked;
    public void addChildView(final String[] titles, OnSlefItemClickListener listener,int checkedIndex){
        mCheckedIndex = checkedIndex;
        onSlefItemClickListener = listener;
        if(this.getChildCount() != 0){
            this.removeAllViews();
        }
        final int length = titles.length;
        for(int i =0 ;i < length;i++){
            final int postion = i;
            CheckedTextView checkedTextView = new CheckedTextView(getContext());
            checkedTextView.setText(titles[postion]);
            checkedTextView.setPadding(3,3,3,3);
            checkedTextView.setTextColor(ContextCompat.getColorStateList(getContext(),R.color.text_checked_color));
            if(checkedIndex == postion){
                checkedTextView.setChecked(true);
                currentChecked = checkedTextView;
            } else {
                checkedTextView.setChecked(false);
            }
            addView(checkedTextView,i);
            checkedTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(onSlefItemClickListener != null)
                        onSlefItemClickListener.onClickView(postion,titles[postion]);
                    if(mCheckedIndex != postion){
                        mCheckedIndex = postion;
                    }
                    if(v != currentChecked){
                        if(currentChecked != null)
                        currentChecked.setChecked(false);
                        if(v instanceof CheckedTextView){
                            currentChecked = (CheckedTextView) v;
                            currentChecked.setChecked(true);
                        }
                    }
                }
            });
        }
    }

    /**
     * 设置第几项被选中
     * @param index
     */
    public void setmCheckedIndex(int index){
        if(mCheckedIndex == index)return;
        final int count = getChildCount();
        if(mCheckedIndex > count)return;
        mCheckedIndex = index;
        initChildView();
    }

    /**
     * 获取选中的项
     * @return
     */
    public int getCheckedIndex(){
        return mCheckedIndex;
    }
    private void initChildView(){
        currentChecked.setChecked(false);
        if(mCheckedIndex == -1)return;
        final View view = getChildAt(mCheckedIndex);
        if(view instanceof CheckedTextView){
            final CheckedTextView textView = (CheckedTextView) view;
            currentChecked = textView;
            currentChecked.setChecked(true);
        }
    }
    private OnSlefItemClickListener onSlefItemClickListener;

    /**
     * 销毁掉回调函数。
     */
    public void removeSelfListener(){
        onSlefItemClickListener = null;
    }
    public interface OnSlefItemClickListener{
        void onClickView(int position,String string);
    }

}
