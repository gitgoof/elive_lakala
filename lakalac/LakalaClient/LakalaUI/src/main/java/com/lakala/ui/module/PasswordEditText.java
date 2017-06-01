package com.lakala.ui.module;

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
import android.widget.EditText;

import com.lakala.ui.R;
import com.lakala.ui.common.Dimension;

/**
 * [*] [*] [*] [*] [*] [*]
 * 密码输入框(默认六位)
 *
 * Created by jerry on 14-1-20.
 */
public class PasswordEditText extends EditText{

    private Context mContext;
    private float mSpacingWidth;
    private int mMaxCharact;
    private Bitmap mPwdImg,inputLeft,inputMiddle,inputRight,inputBox;

    /** 画笔初始化 */
    private Paint mPaint = new Paint();

    public PasswordEditText(Context context) {
        this(context, null);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mSpacingWidth = Dimension.dip2px(12,context);
        setBackgroundColor(Color.TRANSPARENT);
        setMaxCharacter(6);
        setSingleLine(true);
        setCursorVisible(false);
        setLongClickable(false);
        setInputType(InputType.TYPE_CLASS_NUMBER);
        mPwdImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.ui_pw_is_set);
        inputLeft = BitmapFactory.decodeResource(context.getResources(), R.drawable.ui_input_left);
        inputMiddle = BitmapFactory.decodeResource(context.getResources(), R.drawable.ui_input_middle);
        inputRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.ui_input_right);
        inputBox = BitmapFactory.decodeResource(context.getResources(), R.drawable.ui_input_box);
    }

    /**
     * 设置密码输入框间隔
     * @param mSpacingLen
     */
    public void setSpacingWidth(float mSpacingLen) {
        this.mSpacingWidth = Dimension.dip2px(mSpacingLen, mContext);
    }

    /**
     * 设置最多输入几位密码
     * @param maxCharacter
     */
    public void setMaxCharacter(int maxCharacter) {
        this.mMaxCharact = maxCharacter;
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxCharacter)});
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //输入框宽度
        float disWidth  = getWidth();
        //输入框高度
        float disHeight = getHeight();
        //所有字符n,间隔数n-1
        int spacingCount = mMaxCharact -1;
        //密码框的宽度
        float pwdWidth = (disWidth - mSpacingWidth * spacingCount) / 6;
        //输入字符长度
        int textLen = getText().toString().trim().length();
        //图像绘制宽度缓存
        float cacheWidth = 0;
        boolean isHaveSpace = false;
        if (mSpacingWidth > 0){
            isHaveSpace = true;
        }

        for (int i = 0; i < mMaxCharact; i++) {
            //绘制密码方格
            RectF rectRim = new RectF(cacheWidth,0,cacheWidth + pwdWidth,disHeight);
            mPaint.setColor(getResources().getColor(R.color.color_gray_d3e0e3));
            if (isHaveSpace){
                canvas.drawBitmap(inputBox,null,rectRim, mPaint);
            }else {
                if (i==0){
                    canvas.drawBitmap(inputLeft,null,rectRim, mPaint);
                }else if (i==mMaxCharact-1){
                    canvas.drawBitmap(inputRight,null,rectRim, mPaint);
                }else {
                    canvas.drawBitmap(inputMiddle,null,rectRim, mPaint);
                }
            }


            //绘制输入字符
            if (i < textLen){
                mPaint.reset();
                canvas.drawBitmap(mPwdImg,pwdWidth/2 - mPwdImg.getWidth()/2 + cacheWidth,
                        disHeight/2 - mPwdImg.getHeight()/2, mPaint);
            }
            //缓存已绘制宽度
            cacheWidth += (pwdWidth + mSpacingWidth);
        }
    }
}
