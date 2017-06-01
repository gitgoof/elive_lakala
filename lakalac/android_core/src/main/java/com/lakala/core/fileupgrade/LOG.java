package com.lakala.core.fileupgrade;

import android.util.Log;

/**
 * <p>Description  : Log</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/8.</p>
 * <p>Time         : 下午1:33.</p>
 */
/*package*/ class LOG {

//    private static final boolean DEBUG = Config.getInstance().isDebug();
    private static final boolean DEBUG = false;

    private static String TAG = Config.MODULE_NAME;


    /**
     * Debug log message.
     *
     * @param s
     */
    public static void d(String s) {
        if (DEBUG) Log.d(TAG, s);
    }

    /**
     * Info log message.
     *
     * @param s
     */
    public static void i(String s) {
        if (DEBUG) Log.i(TAG, s);
    }

    /**
     * Warning log message.
     *
     * @param s
     */
    public static void w(String s) {
        if (DEBUG) Log.w(TAG, s);
    }

    /**
     * Error log message.
     *
     * @param s
     */
    public static void e(String s) {
        if (DEBUG) Log.e(TAG, s);
    }

    /**
     * Verbose log message.
     *
     * @param s
     * @param e
     */
    public static void v(String s, Throwable e) {
        if (DEBUG) Log.v(TAG, s, e);
    }

    /**
     * Debug log message.
     *
     * @param s
     * @param e
     */
    public static void d(String s, Throwable e) {
        if (DEBUG) Log.d(TAG, s, e);
    }

    /**
     * Info log message.
     *
     * @param s
     * @param e
     */
    public static void i(String s, Throwable e) {
        if (DEBUG) Log.i(TAG, s, e);
    }

    /**
     * Warning log message.
     *
     * @param s
     * @param e
     */
    public static void w(String s, Throwable e) {
        if (DEBUG) Log.w(TAG, s, e);
    }

    /**
     * Error log message.
     *
     * @param s
     * @param e
     */
    public static void e(String s, Throwable e) {
        if (DEBUG) Log.e(TAG, s, e);
    }

    /**
     * Verbose log message with printf formatting.
     *
     * @param s
     * @param args
     */
    public static void v(String s, Object... args) {
        if (DEBUG) Log.v(TAG, String.format(s, args));
    }

    /**
     * Debug log message with printf formatting.
     *
     * @param s
     * @param args
     */
    public static void d(String s, Object... args) {
        if (DEBUG) Log.d(TAG, String.format(s, args));
    }

    /**
     * Info log message with printf formatting.
     *
     * @param s
     * @param args
     */
    public static void i(String s, Object... args) {
        if (DEBUG) Log.i(TAG, String.format(s, args));
    }

    /**
     * Warning log message with printf formatting.
     *
     * @param s
     * @param args
     */
    public static void w(String s, Object... args) {
        if (DEBUG) Log.w(TAG, String.format(s, args));
    }

    /**
     * Error log message with printf formatting.
     *
     * @param s
     * @param args
     */
    public static void e(String s, Object... args) {
        if (DEBUG) Log.e(TAG, String.format(s, args));
    }

}
