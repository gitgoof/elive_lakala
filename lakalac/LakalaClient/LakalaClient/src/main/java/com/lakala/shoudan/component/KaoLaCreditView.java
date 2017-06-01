package com.lakala.shoudan.component;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;


/**
 * 考拉信用分
 */
public class KaoLaCreditView extends FrameLayout {
    /**
     * 几次出现一次长刻度
     */
    private int mDialPer = 50;
    /**
     * 刻度线的数量
     */
    private int mDialCount = 0;
    /**
     * 长刻度的长度
     */
    private int mDialLongLength = 0;
    /**
     * 短刻度的长度
     */
    private int mDialShortLength = 0;
    /**
     * 刻度线距离圆心的距离
     */
    private int mDialRadius = 0;
    /**
     * 刻度盘距离外面的距离
     */
    private int mDialOutCircleDistance = 0;
    /**
     * 内环的半径
     */
    private int mInCircleRedius = 0;
    /**
     * View 的实际宽度
     */
    private int mViewWidth;
    /**
     * View 的实际高度
     */
    private int mViewHeight;
    /**
     * 画板的实际宽度
     */
    private int mCanvaWidth;
    /**
     * 画板的中心坐标
     */
    private int[] mCenterPoint = new int[2];
    /**
     * 开始角度
     */
    private int mStartAngle = 0;
    /**
     * 总角度
     */
    private int mAngle = 0;
    /**
     * 缩放比
     */
    private float mScaleSize = 1.0f;


    /**
     * 默认表盘Paint
     */
    private Paint mDefaultDialPaint;
    /**
     * 默认表盘上的数值Paint
     */
    private Paint mDefaultFigurePaint;
    /**
     * 默认表盘上的等级Paint
     */
    private Paint mDefaultLevelPaint;
    /**
     * 默认表盘刻度的颜色值
     */
    private String mDefaultColor = "#656565";
    /**
     * 刻度盘上数值显示的数量
     */
    private int mFigureCount = 0;
    /**
     * 数值集合
     */
    private ArrayList<Integer> mFigureDatas;
    /**
     * 刻度盘上等级显示的数量
     */
    private int mLevelCount = 0;
    /**
     * 等级集合
     */
    private ArrayList<String> mLevelDatas;

    /**
     * 颜色
     */
    private Paint mColorDialPaint;
    /**
     * 颜色渐变插值器 Api-11 up
     */
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();

    private String mLable = "信用等级";


    private Paint mCenterFigurePaint;
    private Paint mCenterLevelPaint;
    private String mCneterFigureColor = "#000000";
    private String mCneterLevelColor = "#656565";


    private DefultView mDefultView;
    private ColorView mColorView;


    private int currentFigure = 0;
    private int currentDial = 0;
    private String currentLevelStr = "信用等级";


    public KaoLaCreditView(Context context) {
        this(context, null);
    }

    public KaoLaCreditView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDefultView = new DefultView(context, attrs);
        addView(mDefultView, lp);
        mColorView = new ColorView(context, attrs);
        addView(mColorView, lp);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取View的真实宽高
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
        //比较宽高取其一
        mCanvaWidth = mViewWidth > mViewHeight ? mViewHeight : mViewWidth;
        //用实际宽度计算缩放比
        mScaleSize = mCanvaWidth * 1f / getScreenWidth();
        //根据真实宽高 获取中心点
        mCenterPoint[0] = mViewWidth / 2;
        mCenterPoint[1] = mViewHeight / 2;
        //
        mDialLongLength = (int) (100 * mScaleSize);
        mDialShortLength = (int) (70 * mScaleSize);
        //刻度盘距离外边的距离
        mDialOutCircleDistance = (int) (mCanvaWidth / 18.7);
        //内环的半径
        mInCircleRedius = (mCanvaWidth - mDialLongLength * 2) / 2;
        //刻度线距离圆心的距离
        mDialRadius = mInCircleRedius - mDialOutCircleDistance;
        mStrockWidth = 10f * mScaleSize;
    }

    /**
     * 根据传入数值刷新
     *
     * @param figure
     */
    public void refresh(int figure) {
        mFigure = figure;
        if (null == mFigureDatas || mFigureDatas.size() <= 0)
            return;
        invalidate();
        currentDial = 0;
        currentLevelStr = mLable;
        currentFigure = 0;
        int maxDial = getDialMaxCount(figure);

        ValueAnimator dialAnim = ValueAnimator.ofInt(currentDial, maxDial);
        dialAnim.setInterpolator(new BounceInterpolator());
        dialAnim.setDuration(4000);
        dialAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentDial = (int) valueAnimator.getAnimatedValue();

                mColorView.postInvalidate();

            }
        });

        ValueAnimator figureAnim = ValueAnimator.ofInt(currentFigure, figure);
        figureAnim.setDuration(4000);
        figureAnim.setInterpolator(new LinearInterpolator());
        figureAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentFigure = (int) valueAnimator.getAnimatedValue();
                for (int i = 0; i < mFigureDatas.size() - 1; i++) {
                    if (currentFigure >= mFigureDatas.get(i) && currentFigure <= mFigureDatas.get(i + 1)) {
                        currentLevelStr = mLable + " " + mLevelDatas.get(i);
                        break;
                    }
                }
                mColorView.postInvalidate();

            }
        });

        dialAnim.start();
        figureAnim.start();
    }

    private int mFigure = 0;

    /**
     * 根据传入的数值 计算需要绘制的刻度数
     *
     * @param figure
     * @return
     */
    private int getDialMaxCount(int figure) {
        int maxDial;
        if (figure <= mFigureDatas.get(0)) {
            maxDial = 0;
        } else if (figure >= mFigureDatas.get(mFigureCount - 1)) {
            maxDial = mDialCount;
        } else {
            int temp = 0;
            for (int i = 0; i < mFigureCount - 1; i++) {
                if (figure > mFigureDatas.get(i)) {
                    temp = i;
                }
            }
            int diff = figure - mFigureDatas.get(temp);
            int d = (mFigureDatas.get(temp + 1) - mFigureDatas.get(temp)) / mDialPer;
            int count = diff / d;
            maxDial = mDialPer * (temp) + count;
        }
        return maxDial;
    }

    /**
     * 初始化
     */
    private void initDefaultPaint() {
        //刻度
        mDefaultDialPaint = new Paint();
        mDefaultDialPaint.setStrokeWidth(mStrockWidth);
        mDefaultDialPaint.setColor(Color.parseColor(mDefaultColor));
        mDefaultDialPaint.setAntiAlias(true);
        mDefaultDialPaint.setStyle(Paint.Style.STROKE);
        mDefaultDialPaint.setStrokeCap(Paint.Cap.ROUND);

        //数值
        mDefaultFigurePaint = new Paint();
        mDefaultFigurePaint.setAntiAlias(true);
        mDefaultFigurePaint.setTextAlign(Paint.Align.CENTER);
        mDefaultFigurePaint.setStyle(Paint.Style.STROKE);
        mDefaultFigurePaint.setColor(Color.parseColor(mDefaultColor));
        mDefaultFigurePaint.setStrokeCap(Paint.Cap.ROUND);

        //等级
        mDefaultLevelPaint = new Paint();
        mDefaultLevelPaint.setAntiAlias(true);
        mDefaultLevelPaint.setTextAlign(Paint.Align.CENTER);
        mDefaultLevelPaint.setStyle(Paint.Style.STROKE);
        mDefaultLevelPaint.setColor(Color.parseColor(mDefaultColor));
        mDefaultLevelPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    /**
     *
     */
    private void initColorPaint() {
        //刻度
        mColorDialPaint = new Paint();
        mColorDialPaint.setStrokeWidth(mStrockWidth);
        mColorDialPaint.setAntiAlias(true);
        mColorDialPaint.setStyle(Paint.Style.STROKE);
        mColorDialPaint.setStrokeCap(Paint.Cap.ROUND);

        //数值
        mCenterFigurePaint = new Paint();
        mCenterFigurePaint.setAntiAlias(true);
        mCenterFigurePaint.setTextAlign(Paint.Align.CENTER);
        mCenterFigurePaint.setStyle(Paint.Style.STROKE);
        mCenterFigurePaint.setColor(Color.parseColor(mCneterFigureColor));
        mCenterFigurePaint.setStrokeCap(Paint.Cap.ROUND);
        mCenterFigurePaint.setTextSize(sp2px(28 * mScaleSize));

        //等级文字
        mCenterLevelPaint = new Paint();
        mCenterLevelPaint.setAntiAlias(true);
        mCenterLevelPaint.setTextAlign(Paint.Align.CENTER);
        mCenterLevelPaint.setStyle(Paint.Style.STROKE);
        mCenterLevelPaint.setColor(Color.parseColor(mCneterLevelColor));
        mCenterLevelPaint.setStrokeCap(Paint.Cap.ROUND);
        mCenterLevelPaint.setTextSize(sp2px(15 * mScaleSize));


    }


    public void setLevelDatas(ArrayList<String> levelDatas) {
        this.mLevelDatas = levelDatas;
        this.mLevelCount = (null == mLevelDatas ? 0 : mLevelDatas.size());
    }

    public void setFigureDatas(ArrayList<Integer> figureDatas) {
        this.mFigureDatas = figureDatas;
        this.mFigureCount = (null == mFigureDatas ? 0 : mFigureDatas.size());
    }


    public void setAngle(int angle) {
        this.mAngle = angle;
    }

    public void setStartAngle(int startAngle) {
        this.mStartAngle = startAngle;
    }

    public void setDialPer(int dialPer) {
        this.mDialPer = dialPer;
        this.mDialCount = mDialPer * mLevelCount;
    }

    public void setLable(String lable) {
        this.mLable = lable;
    }


    /**
     * 刻度渲染
     */
    private class ColorView extends View {

        public ColorView(Context context) {
            this(context, null);
        }

        public ColorView(Context context, AttributeSet attrs) {
            super(context, attrs);
//            post(new Runnable() {
//                @Override
//                public void run() {
//                    initColorPaint();
//                }
//            });
            initColorPaint();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mDialCount == 0)
                return;

            //绘制刻度

            drawDial(mStartAngle, mAngle * 1.0f / mDialCount * currentDial, currentDial, mDialPer, mDialLongLength, mDialShortLength, mDialRadius, canvas);
//          //
            mCenterLevelPaint.setTextSize(sp2px(15 * mScaleSize));
            mCenterFigurePaint.setTextSize(sp2px(28 * mScaleSize));
            canvas.save();
            canvas.rotate(360, mCenterPoint[0], mCenterPoint[1]);
            canvas.drawText(currentFigure + "", mCenterPoint[0], mCenterPoint[1] - 30 * mScaleSize, mCenterFigurePaint);
            canvas.drawText(currentLevelStr, mCenterPoint[0], mCenterPoint[1] + 30 * mScaleSize, mCenterLevelPaint);
            canvas.restore();
        }

        /***
         * 渐变色
         */
        private int[] colors = new int[]{
                Color.parseColor("#B3261C"), Color.parseColor("#D97C3A"), Color.parseColor("#D8CB48"), Color.parseColor("#52B737")
        };

        /**
         * 画刻度盘
         *
         * @param startAngle  开始画的角度
         * @param allAngle    总共划过的角度
         * @param dialCount   总共的线的数量
         * @param per         每隔几个出现一次长线
         * @param longLength  长刻度的长度
         * @param shortLength 短刻度的长度
         * @param radius      距离圆心最远的地方的半径
         */
        private void drawDial(int startAngle, float allAngle, int dialCount, int per, int longLength, int shortLength, int radius, Canvas canvas) {
            int length;
            float angle;
            float shade;
            float temp = mDialCount / 3 * 1f;
            for (int i = 0; i <= dialCount; i++) {
                if (allAngle == 0)
                    angle = startAngle;
                else
                    angle = ((allAngle) / (dialCount * 1f) * i) + startAngle;
                if (i % per == 0) {
                    length = longLength;
                } else {
                    length = shortLength;
                }


                //渐变色
                if (i >= 0 && i <= mDialCount / 3) {
                    shade = i / temp;
                    Integer color = (Integer) mArgbEvaluator.evaluate(shade, colors[0], colors[1]);//颜色插值器（level 11以上才可以用）
                    mColorDialPaint.setColor(color);
                } else if (i >= mDialCount / 3 + 1 && i <= mDialCount / 3 * 2) {
                    shade = (i - temp) / temp;
                    Integer color = (Integer) mArgbEvaluator.evaluate(shade, colors[1], colors[2]);//颜色插值器（level 11以上才可以用）
                    mColorDialPaint.setColor(color);
                } else {
                    shade = (i - temp * 2) / temp;
                    Integer color = (Integer) mArgbEvaluator.evaluate(shade, colors[2], colors[3]);//颜色插值器（level 11以上才可以用）
                    mColorDialPaint.setColor(color);
                }
                drawSingleDial(angle, length, radius + length, canvas, mColorDialPaint);


            }
        }


    }

    private float mStrockWidth = 10f;

    /**
     * 默认刻度
     * 数值
     * 等级
     */
    private class DefultView extends View {


        public DefultView(Context context) {
            this(context, null);
        }

        public DefultView(Context context, AttributeSet attrs) {
            super(context, attrs);
//            post(new Runnable() {
//                @Override
//                public void run() {
//                    initDefaultPaint();
//                }
//            });
            initDefaultPaint();
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (mDialCount == 0)
                return;

            //默认刻度
            drawDial(mStartAngle, mAngle, mDialCount, mDialPer, mDialLongLength, mDialShortLength, mDialRadius, canvas);
            //分数
            drawFigure(canvas, mFigureCount);
            //级别
            drawLevel(canvas, mLevelCount);

        }

        /**
         * 级别
         *
         * @param canvas
         * @param count  级别的个数
         */
        private void drawLevel(Canvas canvas, int count) {
            mDefaultLevelPaint.setTextSize(sp2px(13 * mScaleSize));
            float angle;
            for (int i = 0; i < count; i++) {
                angle = mAngle / count * i + mStartAngle + mAngle / count / 2;
                int[] pointFromAngleAndRadius = getPointFromAngleAndRadius(angle, mInCircleRedius + mDialShortLength);

                canvas.save();
                canvas.rotate(360, pointFromAngleAndRadius[0], pointFromAngleAndRadius[1]);
                canvas.drawText(mLevelDatas.get(i).toString(), pointFromAngleAndRadius[0], pointFromAngleAndRadius[1], mDefaultLevelPaint);
                canvas.restore();
            }
        }

        /**
         * 分数
         *
         * @param canvas
         * @param count  分数的个数
         */
        private void drawFigure(Canvas canvas, int count) {
            mDefaultFigurePaint.setTextSize(sp2px(13 * mScaleSize));
            float angle;
            for (int i = 0; i < count; i++) {
                angle = ((mAngle) / ((count - 1) * 1f) * i) + mStartAngle;
                int[] pointFromAngleAndRadius = getPointFromAngleAndRadius(angle, mInCircleRedius - mDialLongLength);

                canvas.save();
                canvas.rotate(360, pointFromAngleAndRadius[0], pointFromAngleAndRadius[1]);
                canvas.drawText(mFigureDatas.get(i).toString(), pointFromAngleAndRadius[0], pointFromAngleAndRadius[1], mDefaultFigurePaint);
                canvas.restore();
            }
        }

        /**
         * 画刻度盘
         *
         * @param startAngle  开始画的角度
         * @param allAngle    总共划过的角度
         * @param dialCount   总共的线的数量
         * @param per         每隔几个出现一次长线
         * @param longLength  长仙女的长度
         * @param shortLength 短线的长度
         * @param radius      距离圆心最远的地方的半径
         */
        private void drawDial(int startAngle, int allAngle, int dialCount, int per, int longLength, int shortLength, int radius, Canvas canvas) {

            int length;
            float angle;
            for (int i = 0; i <= dialCount; i++) {
                angle = ((allAngle) / (dialCount * 1f) * i) + startAngle;
                if (i % per == 0) {
                    length = longLength;
                } else {
                    length = shortLength;

                }
                drawSingleDial(angle, length, radius + length, canvas, mDefaultDialPaint);

            }
        }

    }


    /**
     * 画刻度中的一条线
     *
     * @param angle  所处的角度
     * @param length 线的长度
     * @param radius 距离圆心最远的地方的半径
     */
    private void drawSingleDial(float angle, int length, int radius, Canvas canvas, Paint paint) {
        int[] startP = getPointFromAngleAndRadius(angle, radius);
        int[] endP = getPointFromAngleAndRadius(angle, radius - length);
        canvas.drawLine(startP[0], startP[1], endP[0], endP[1], paint);
    }


    /**
     * 根据角度和半径，求一个点的坐标
     *
     * @param angle
     * @param radius
     * @return
     */
    private int[] getPointFromAngleAndRadius(float angle, int radius) {
        double x = radius * Math.cos(angle * Math.PI / 180) + mCenterPoint[0];
        double y = radius * Math.sin(angle * Math.PI / 180) + mCenterPoint[1];
        return new int[]{(int) x, (int) y};
    }


    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽px
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
        windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
        return dm.widthPixels;
    }

    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    private int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
