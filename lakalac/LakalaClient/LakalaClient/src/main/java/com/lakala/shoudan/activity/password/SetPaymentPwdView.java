package com.lakala.shoudan.activity.password;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.WindowManager;

import com.lakala.platform.common.securitykeyboard.SecurityEditText;
import com.lakala.platform.common.securitykeyboard.SecurityKeyboardManager;
import com.lakala.platform.common.securitykeyboard.SecurityKeyboardUtil;
import com.lakala.shoudan.R;
import com.lakala.ui.common.Dimension;

/**
 * Created by lianglong on 14-6-10.
 */
public class SetPaymentPwdView extends SecurityEditText {
    private static final short MAX_LENGTH       = 6;
    private static final short KEYBOARD_TYPE    = 2;

    private final Bitmap dot            = BitmapFactory.decodeResource(this.getResources(), R.drawable.ui_pw_is_set);

    private final String key        = String.valueOf(System.currentTimeMillis());
    private float horizontalSpace   = 0;
    private Paint paint             = new Paint();
    int w,h;                //屏幕宽高
    int etWidth,etHeight,width;      //输入框的宽度，高度，每一格的宽度

    private SecurityKeyboardManager manager;

    public SetPaymentPwdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array    = getContext().obtainStyledAttributes(attrs, R.styleable.PayPwdInputView);
        horizontalSpace = array.getFloat(com.lakala.platform.R.styleable.PayPwdInputView_horizontalSpace, 12);
        horizontalSpace     = Dimension.dip2px(horizontalSpace, this.getContext());

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        w = wm.getDefaultDisplay().getWidth();
        h = wm.getDefaultDisplay().getHeight();
        etWidth = (int) getResources().getDimension(R.dimen.security_edittext_width);
        etHeight = (int) getResources().getDimension(R.dimen.security_edittext_height);
        width = etWidth / 6;
        init();
    }

    private void init() {
//        setBackgroundColor(Color.TRANSPARENT);
        manager = SecurityKeyboardUtil.customKeyboard(this, KEYBOARD_TYPE, (short) 0, MAX_LENGTH, MAX_LENGTH, (short) 0, false, key);
        setSecurityManager(manager);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.onDestory();
    }

    /**
     * @return  pinkey得到明文密码
     */
    public String getPassword() {
        return this.getText(key);
    }

    public String getPrivatePwd(){
        return manager.getText(key);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int padding = (w-etWidth)/2;
        //绘制密码方格
        RectF rectF = new RectF(padding,0,etWidth + padding,etHeight);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.white));
        canvas.drawRect(rectF, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.black));
        canvas.drawRect(rectF, paint);

        //输入字符长度
        int textLen = getPassword().length();

        for (int i = 0; i < MAX_LENGTH; i++) {

            int x = (i + 1) * width + padding;
            //画分隔线
            if (i  != 5){
                canvas.drawLine(x, 0, x, etHeight, paint);
            }

            //绘制输入字符
            if (i < textLen) {
                paint.reset();
                canvas.drawBitmap(dot, x - width + (width/2 - dot.getWidth()/2), (etHeight / 2 - dot.getHeight()), paint);
            }
        }
    }
}
