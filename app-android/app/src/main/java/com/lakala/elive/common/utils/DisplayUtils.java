package com.lakala.elive.common.utils;

import android.content.Context;
import android.util.TypedValue;

import com.lakala.elive.EliveApplication;

/**
 * Created by wenhaogu on 2017/2/27.
 */

public class DisplayUtils {
    private DisplayUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static int dip2px(Context context, float dpvalueO) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpvalueO * scale + 0.5f);
    }

//    /**
//     * dp转px
//     *
//     * @return
//     */
//    public static int dpToPx(float dp, Resources resources) {
//        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//                resources.getDisplayMetrics());
//        return (int) px;
//    }

    /**
     * dp转px
     *
     * @return
     */
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, EliveApplication.getInstance().getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @return
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, EliveApplication.getInstance().getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param pxVal
     * @return
     */
    public static float px2dp(float pxVal) {
        final float scale = EliveApplication.getInstance().getResources()
                .getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public static float px2sp(float pxVal) {
        return (pxVal / EliveApplication.getInstance().getResources().getDisplayMetrics().scaledDensity);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
