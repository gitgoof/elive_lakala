package com.lakala.platform.swiper.mts.paypwd;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.lakala.platform.R;
import com.lakala.platform.common.securitykeyboard.SecurityEditText;
import com.lakala.platform.common.securitykeyboard.SecurityKeyboardManager;
import com.lakala.platform.common.securitykeyboard.SecurityKeyboardUtil;
import com.lakala.ui.common.Dimension;

/**
 * Created by lianglong on 14-6-10.
 */
public class PayPwdInputView extends SecurityEditText {
    private static final short MAX_LENGTH       = 6;
    private static final short KEYBOARD_TYPE    = 2;

    private final Bitmap dot            = BitmapFactory.decodeResource(this.getResources(), R.drawable.ui_pw_is_set);
    private final Bitmap inputLeft      = BitmapFactory.decodeResource(this.getResources(), R.drawable.ui_input_left);
    private final Bitmap inputMiddle    = BitmapFactory.decodeResource(this.getResources(), R.drawable.ui_input_middle);
    private final Bitmap inputRight     = BitmapFactory.decodeResource(this.getResources(), R.drawable.ui_input_right);
    private final Bitmap inputBox       = BitmapFactory.decodeResource(this.getResources(), R.drawable.ui_input_box);

    private final String key        = String.valueOf(System.currentTimeMillis());
    private float horizontalSpace   = 0;
    private Paint paint             = new Paint();

    private SecurityKeyboardManager manager;

    public PayPwdInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array    = getContext().obtainStyledAttributes(attrs, R.styleable.PayPwdInputView);
        horizontalSpace     = array.getFloat(com.lakala.platform.R.styleable.PayPwdInputView_horizontalSpace, 12);
        horizontalSpace     = Dimension.dip2px(horizontalSpace, this.getContext());
        init();
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        manager = SecurityKeyboardUtil.customKeyboard(this, KEYBOARD_TYPE, (short) 0, MAX_LENGTH, MAX_LENGTH, (short) 1, false, key);
        setSecurityManager(manager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.onDestory();
    }

    public void setHorizontalSpace(float horizontalSpace) {
        this.horizontalSpace = Dimension.dip2px(horizontalSpace, getContext());
    }


    /**
     * @return  pinkey加密后的密码
     */
    public String getPassword() {
        return this.getText(key);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //输入框宽度
        float disWidth      = getWidth();
        //输入框高度
        float disHeight     = getHeight();
        //所有字符n,间隔数n-1
        int spacingCount    = MAX_LENGTH - 1;
        //密码框的宽度
        float pwdWidth      = (disWidth - horizontalSpace * spacingCount) / 6;
        //输入字符长度
        int textLen         = getPassword().length();
        //图像绘制宽度缓存
        float cacheWidth = 0;

        boolean isHaveSpace = false;
        if (horizontalSpace > 0) {
            isHaveSpace = true;
        }

        for (int i = 0; i < MAX_LENGTH; i++) {

            //绘制密码方格
            RectF rectRim = new RectF(cacheWidth, 0, cacheWidth + pwdWidth, disHeight);
            paint.setColor(getResources().getColor(R.color.color_gray_d3e0e3));
            if (isHaveSpace) {
                canvas.drawBitmap(inputBox, null, rectRim, paint);
            } else {
                if (i == 0) {
                    canvas.drawBitmap(inputLeft, null, rectRim, paint);
                } else if (i == MAX_LENGTH - 1) {
                    canvas.drawBitmap(inputRight, null, rectRim, paint);
                } else {
                    canvas.drawBitmap(inputMiddle, null, rectRim, paint);
                }
            }


            //绘制输入字符
            if (i < textLen) {
                paint.reset();
                canvas.drawBitmap(dot, pwdWidth / 2 - dot.getWidth() / 2 + cacheWidth, disHeight / 2 - dot.getHeight() / 2, paint);
            }
            //缓存已绘制宽度
            cacheWidth += (pwdWidth + horizontalSpace);
        }
    }
}
