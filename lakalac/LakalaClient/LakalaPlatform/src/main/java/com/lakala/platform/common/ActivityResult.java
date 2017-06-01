package com.lakala.platform.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by wangchao on 14-2-16.
 * 提供setResult方法，主要为b2c或c2c时返回数据时调用
 */
public class ActivityResult {

    private static final String RESULT_DATA_KEY = "result_bundle";

    private static Intent intent = new Intent();

    /**
     * setResult
     *
     * @param activity
     * @param resultCode
     * @param data       返回的数据
     */
    public static void setResult(Activity activity, int resultCode, Object data) {
        if (data instanceof Bundle) {
            intent.putExtra(RESULT_DATA_KEY, (Bundle) data);
        } else if (data instanceof String) {
            intent.putExtra(RESULT_DATA_KEY, String.valueOf(data));
        }
        activity.setResult(resultCode, data == null ? null : intent);
        activity.finish();
    }

    /**
     * setResult
     *
     * @param activity
     * @param data
     */
    public static void setResult(Activity activity, Object data) {
        setResult(activity, Activity.RESULT_OK, data);
    }

    /**
     * setResult
     *
     * @param activity
     * @param resultCode
     */
    public static void setResult(Activity activity, int resultCode) {
        setResult(activity, resultCode, null);
    }

    /**
     * setResult
     *
     * @param activity
     */
    public static void setResult(Activity activity) {
        setResult(activity, Activity.RESULT_OK, null);
    }

    /**
     * 获取返回的Bundle数据
     *
     * @param intent
     */
    public static Bundle getResultBundle(Intent intent) {
        return getResultData(intent, Type.Bundle) == null ? null : (Bundle)getResultData(intent, Type.Bundle) ;
    }

    /**
     * 获取返回的String数据
     *
     * @param intent
     */
    public static String getResultString(Intent intent) {
        return getResultData(intent, Type.Bundle) == null ? null : String.valueOf(getResultData(intent, Type.String));
    }

    /**
     * 获取返回数据
     *
     * @param intent
     * @param type   类型
     */
    private static Object getResultData(Intent intent, Type type) {
        switch (type) {
            case Bundle:
                return intent.getBundleExtra(RESULT_DATA_KEY);
            case String:
                return intent.getStringExtra(RESULT_DATA_KEY);
        }
        return null;
    }

    private enum Type {
        Bundle, String;
    }
}
