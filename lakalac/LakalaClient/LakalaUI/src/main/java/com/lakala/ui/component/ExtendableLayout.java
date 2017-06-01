package com.lakala.ui.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.ui.R;


/**
 * 有展开收缩能力的容器，该容器只能有一个子元素。
 */
public class ExtendableLayout extends ViewGroup implements Handler.Callback{

    //布局大小
    private int layoutWidth;
    private int layoutHeight;

    //新布局大小
    private int newWidth;
    private int newHeight;

    //垂直展开方向常量
    private static final int EXTEND_ORIENTATION_VERTICAL   = 1;
    //水平方向展开
    private static final int EXTEND_ORIENTATION_HORIZONTAL = 2;

    //状态：展开的
    private static final int STATE_EXTENDED  = 0;
    //状态：展开中
    private static final int STATE_EXTENDING = 1;
    //状态：收缩的
    private static final int STATE_SHRANK    = 2;
    //状态：收缩中
    private static final int STATE_SHRINKING = 3;

    //展开方向
    private int     extendOrientation;
    //消息处理
    private Handler handler;
    //展开状态
    private int     state;
    //动画速度
    private int     speed;
    //是否需要重新测算大小
    private boolean isReMeasure;

    //在空闲时执行卷动任务
    private Runnable mExtendShrinkTask = new Runnable() {
        @Override
        public void run()
        {
            //展开中状态
            if (state == STATE_EXTENDING){
                if (extendOrientation == EXTEND_ORIENTATION_HORIZONTAL){
                    //水平方向展开
                    newWidth += speed;
                    if (newWidth >= layoutWidth){
                        //展开完成
                        newWidth = layoutWidth;
                        state = STATE_EXTENDED;
                    }
                }
                else {
                    //垂直方向展开
                    newHeight += speed;
                    if (newHeight >= layoutHeight){
                        //展开完成
                        newHeight = layoutHeight;
                        state = STATE_EXTENDED;
                    }
                }

                ExtendableLayout.this.requestLayout();
            }
            //收缩中状态
            else if (state == STATE_SHRINKING){
                if (extendOrientation == EXTEND_ORIENTATION_HORIZONTAL){
                    //水平方向收缩
                    newWidth -= speed;
                    if (newWidth <= 0){
                        //收缩完成
                        newWidth = 0;
                        state = STATE_SHRANK;
                    }
                }
                else {
                    //垂直方向收缩
                    newHeight -= speed;
                    if (newHeight <= 0){
                        //收缩完成
                        newHeight = 0;
                        state = STATE_SHRANK;
                    }
                }

                ExtendableLayout.this.requestLayout();
            }

            if (state == STATE_EXTENDING || state == STATE_SHRINKING)
                handler.postDelayed(this, 10);
        }
    };

    public ExtendableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExtendableLayout(Context context) {
        super(context);
        init(null);
    }

    /**
     * 判断布局是否已经展开
     */
    public boolean isExtend(){
        return state == STATE_EXTENDED;
    }

    /**
     * 展开
     */
    public void extend(){
        //已经展开了或正在展开中，不需要再次执行展开。
        if (state == STATE_EXTENDED || state == STATE_EXTENDING)
            return ;

        //取当前组件高宽
        newHeight = getHeight();
        newWidth  = getWidth();

        state = STATE_EXTENDING;
        isReMeasure = true;

        handler.removeCallbacks(mExtendShrinkTask);
        handler.postAtTime(mExtendShrinkTask, SystemClock.uptimeMillis() + 100);
    }

    /**
     * 收起
     */
    public void shrink(){
        //已经收缩了或正在收缩中，不需要再次执行展开。
        if (state == STATE_SHRANK || state == STATE_SHRINKING)
            return ;

        //取当前组件高宽
        newHeight = getHeight();
        newWidth  = getWidth();

        state = STATE_SHRINKING;
        isReMeasure = true;

        handler.removeCallbacks(mExtendShrinkTask);
        handler.postAtTime(mExtendShrinkTask, SystemClock.uptimeMillis() + 100);
    }

    public int getExtendOrientation() {
        return extendOrientation;
    }

    public void setExtendOrientation(int extendOrientation) {
        this.extendOrientation = extendOrientation;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 0){
            if (state == STATE_EXTENDED || state == STATE_SHRANK || isReMeasure){
                isReMeasure = false;

                layoutWidth  = MeasureSpec.getSize(widthMeasureSpec);
                layoutHeight = MeasureSpec.getSize(heightMeasureSpec);

                //通知子视图测量大小，如果子视图是ViewGroup子类，则必须执行此过程，
                //否则它的内部的视图将无法显示。
                View child = getChildAt(0);
                LayoutParams clp = child.getLayoutParams();
                int wMeasureSpec,hMeasureSpec;

                if (clp.width == LayoutParams.FILL_PARENT){
                    wMeasureSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.EXACTLY);
                }
                else{
                    wMeasureSpec = MeasureSpec.makeMeasureSpec(layoutWidth, MeasureSpec.UNSPECIFIED);
                }

                if (clp.height == LayoutParams.FILL_PARENT){
                    hMeasureSpec = MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.EXACTLY);
                }
                else{
                    hMeasureSpec = MeasureSpec.makeMeasureSpec(layoutHeight, MeasureSpec.UNSPECIFIED);
                }

                child.measure(wMeasureSpec, hMeasureSpec);

                //获取子视图测算结果
                int childHeight = child.getMeasuredHeight();
                int childWidth  = child.getMeasuredWidth();

                LayoutParams lp = getLayoutParams();
                if (lp != null){
                    //如果高度值设置的是根据内容大小，则在些尝试调整组件的高度。
                    int hMode = MeasureSpec.getMode(heightMeasureSpec);
                    if (lp.height == LayoutParams.WRAP_CONTENT){
                        switch (hMode){
                            case MeasureSpec.UNSPECIFIED: //未指定大小
                                layoutHeight = childHeight;
                                break;
                            case MeasureSpec.AT_MOST: //最多达到的大小
                                if (childHeight < layoutHeight){
                                    layoutHeight = childHeight;
                                }
                                break;
                        }
                    }

                    //如果高度值设置的是根据内容大小，则在些尝试调整组件的高度。
                    int wMode = MeasureSpec.getMode(widthMeasureSpec);
                    if (lp.width == LayoutParams.WRAP_CONTENT){
                        switch (wMode){
                            case MeasureSpec.UNSPECIFIED: //未指定大小
                                layoutWidth = childWidth;
                                break;
                            case MeasureSpec.AT_MOST: //最多达到的大小
                                if (childHeight < layoutHeight){
                                    layoutWidth = childWidth;
                                }
                                break;
                        }
                    }
                }
            }

            int w = layoutWidth;
            int h = layoutHeight;

            switch (state){
                case STATE_EXTENDED:
                    w = layoutWidth;
                    h = layoutHeight;
                    break;

                case STATE_EXTENDING:
                case STATE_SHRINKING:
                    if (extendOrientation == EXTEND_ORIENTATION_HORIZONTAL){
                        w = newWidth;
                    }
                    else {
                        h = newHeight;
                    }
                    break;

                case STATE_SHRANK:
                    if (extendOrientation == EXTEND_ORIENTATION_HORIZONTAL){
                        w = 0;
                    }
                    else {
                        h = 0;
                    }

                    break;
            }

            setMeasuredDimension(w,h);
        }
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            layoutWidth  = getMeasuredWidth();
            layoutHeight = getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();

        for (int i = 0; i < count; i++){
            View v = getChildAt(i);
            if (v == null)
                return;
            v.layout(0, 0, v.getMeasuredWidth(),  v.getMeasuredHeight());
        }
    }

    private void init(AttributeSet attrs){
        handler           = new Handler(this);
        extendOrientation = EXTEND_ORIENTATION_VERTICAL;
        state             = STATE_EXTENDED;
        isReMeasure       = false;

        if (attrs == null)
            return;

        int      iValue;
        boolean  bValue;


        //从 xml 布局文件读取控件属性设置
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ExtendableLayout);

        //属性 extendOrientation
        iValue = ta.getInt(R.styleable.ExtendableLayout_extendOrientation, EXTEND_ORIENTATION_VERTICAL);
        setExtendOrientation(iValue);

        //属性 speed
        iValue = ta.getInt(R.styleable.ExtendableLayout_speed, 10);
        setSpeed(iValue);

        //属性 extended
        bValue = ta.getBoolean(R.styleable.ExtendableLayout_extended,true);
        if (bValue){
            state = STATE_EXTENDED;
        }
        else {
            state = STATE_SHRANK;
        }

        ta.recycle();
    }
}
