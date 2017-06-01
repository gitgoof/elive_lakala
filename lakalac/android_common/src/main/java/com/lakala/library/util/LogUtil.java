package com.lakala.library.util;

import com.lakala.library.DebugConfig;

import java.io.IOException;

/**
 * Created by LL on 13-12-12.
 */
public class LogUtil {
    /** TAG */
    private static final String TAG = "LakalaDebug";
    /** debug开关 */
//    public static final boolean DEBUG = true;
    public static final boolean DEBUG = DebugConfig.DEBUG;

    /**
     * 一个参数的打印方法
     *
     * @param msg 需要打印的信息
     */
    public static void print(String msg) {
        print(null, msg, null);
    }
    public static void w(String msg) {
        w(null, msg, null);
    }
    public static void i(String msg) {
        i(null, msg, null);
    }
    public static void d(String msg) {
        d(null, msg, null);
    }
    public static void e(String msg) {
        e(null, msg, null);
    }
    public static void v(String msg) {
        v(null, msg, null);
    }

    /**
     * 打印异常
     * @param throwable 异常信息
     */
    public static void print(Throwable throwable){
        print(null,null,throwable);
    }
    public static void w(Throwable throwable){
        w(null,null,throwable);
    }
    public static void i(Throwable throwable){
        i(null,null,throwable);
    }
    public static void d(Throwable throwable){
        d(null,null,throwable);
    }
    public static void e(Throwable throwable){
        e(null,null,throwable);
    }
    public static void v(Throwable throwable){
        v(null,null,throwable);
    }


    /**
     * 自定义TAG打印
     * @param tag  自定义tag,为null则默认为 TAG
     * @param msg  需要打印的信息
     */
    public static void print(String tag,String msg){
        print(tag,msg,null);
    }
    public static void w(String tag,String msg){
        w(tag,msg,null);
    }
    public static void i(String tag,String msg){
        i(tag,msg,null);
    }
    public static void d(String tag,String msg){
        d(tag,msg,null);
    }
    public static void e(String tag,String msg){
        e(tag,msg,null);
    }
    public static void v(String tag,String msg){
        v(tag,msg,null);
    }

    /**
     * 自定义TAG打印
     * @param tag 自定义tag,为null则默认为 TAG
     * @param throwable 异常信息
     */
    public static void print(String tag,Throwable throwable){
        print(tag,null,throwable);
    }
    public static void w(String tag,Throwable throwable){
        w(tag,null,throwable);
    }
    public static void i(String tag,Throwable throwable){
        i(tag,null,throwable);
    }
    public static void d(String tag,Throwable throwable){
        d(tag,null,throwable);
    }
    public static void e(String tag,Throwable throwable){
        e(tag,null,throwable);
    }
    public static void v(String tag,Throwable throwable){
        v(tag,null,throwable);
    }


    /**
     *  String.format(pattern,args);
     * @param pattern  pattern
     * @param args     args
     */
    public static void print(String pattern,Object...args){
        print(null,String.format(pattern,args),null);
    }
    public static void w(String pattern,Object...args){
        w(null,String.format(pattern,args),null);
    }
    public static void i(String pattern,Object...args){
        i(null,String.format(pattern,args),null);
    }
    public static void d(String pattern,Object...args){
        d(null,String.format(pattern,args),null);
    }
    public static void e(String pattern,Object...args){
        e(null,String.format(pattern,args),null);
    }
    public static void v(String pattern,Object...args){
        v(null,String.format(pattern,args),null);
    }

    /**
     * 三个参数的打印方法
     *
     * @param tag       自定义tag,为null则默认为 TAG
     * @param msg       打印信息
     * @param throwable 异常信息
     */
    public static void print(String tag, String msg, Throwable throwable) {
        if (!DEBUG)
            return;
        //设置TAG
        String t = TAG;
        if (tag != null){
            t = tag;
        }
        String threadName = Thread.currentThread().getName();
        String lineIndicator = getLineIndicator();
        //打印输出
        if (msg != null){
            android.util.Log.e(t, lineIndicator + " " + msg,throwable);
        }
        if (throwable != null){
            android.util.Log.e(t, "Attention Catch A Exception ", throwable);
        }
    }

    public static void w(String tag, String msg, Throwable throwable) {
        if (!DEBUG)
            return;
        //设置TAG
        String t = TAG;
        if (tag != null){
            t = tag;
        }
        String threadName = Thread.currentThread().getName();
        String lineIndicator = getLineIndicator();
        //打印输出
        if (msg != null){
            android.util.Log.w(t,lineIndicator + " " + msg,throwable);
        }
        if (throwable != null){
            android.util.Log.w(t, "Attention Catch A Exception ", throwable);
        }
    }
    public static void i(String tag, String msg, Throwable throwable) {
        if (!DEBUG)
            return;
        //设置TAG
        String t = TAG;
        if (tag != null){
            t = tag;
        }
        String threadName = Thread.currentThread().getName();
        String lineIndicator = getLineIndicator();
        //打印输出
        if (msg != null){
            android.util.Log.i(t,lineIndicator + " " + msg,throwable);
        }
        if (throwable != null){
            android.util.Log.i(t, "Attention Catch A Exception ", throwable);
        }
    }
    public static void d(String tag, String msg, Throwable throwable) {
        if (!DEBUG)
            return;
        //设置TAG
        String t = TAG;
        if (tag != null){
            t = tag;
        }
        String threadName = Thread.currentThread().getName();
        String lineIndicator = getLineIndicator();
        //打印输出
        if (msg != null){
            android.util.Log.d(t,lineIndicator + " " + msg,throwable);
        }
        if (throwable != null){
            android.util.Log.d(t, "Attention Catch A Exception ", throwable);
        }
    }
    public static void e(String tag, String msg, Throwable throwable) {
        if (!DEBUG)
            return;
        //设置TAG
        String t = TAG;
        if (tag != null){
            t = tag;
        }
        String threadName = Thread.currentThread().getName();
        String lineIndicator = getLineIndicator();
        //打印输出
        if (msg != null){
            android.util.Log.e(t,lineIndicator + " " + msg,throwable);
        }
        if (throwable != null){
            android.util.Log.e(t, "Attention Catch A Exception ", throwable);
        }
    }
    public static void v(String tag, String msg, Throwable throwable) {
        if (!DEBUG)
            return;
        //设置TAG
        String t = TAG;
        if (tag != null){
            t = tag;
        }
        String threadName = Thread.currentThread().getName();
        String lineIndicator = getLineIndicator();
        //打印输出
        if (msg != null){
            android.util.Log.v(t,lineIndicator + " " + msg,throwable);
        }
        if (throwable != null){
            android.util.Log.v(t, "Attention Catch A Exception ", throwable);
        }
    }

    //获取行所在的方法指示
    //获取行所在的方法指示
    private static String getLineIndicator() {
        //3代表方法的调用深度：0-getLineIndicator，1-performPrint，2-print，3-调用该工具类的方法位置
        StackTraceElement element = ((new Exception()).getStackTrace())[3];
        StringBuffer sb = new StringBuffer("(")
                .append(element.getFileName()).append(":")
                .append(element.getLineNumber()).append(").")
                .append(element.getMethodName()).append(":");
        return sb.toString();
    }

    /**
     * 写日志
     * @param filePath 日志文件路径
     * @param msg      日志内容
     * @return
     */
    public synchronized static boolean write(String filePath, String msg){
        return write(filePath, msg,false);
    }

    /**
     * 写日志
     * @param filePath 日志文件路径
     * @param msg      日志内容
     * @param isAsync   是否需要异步写
     *
     * @return 写入成功 or 失败,异步写文件视为成功
     */
    public synchronized static boolean write(String filePath, String msg, boolean isAsync) {
        if (isAsync) {
            writeAsync(filePath, msg);
            return true;
        }

        try {
            return FileUtil.appendConentFile(msg, filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 异步写日志
     *
     * @param filePath 日志文件名
     * @param msg      日志路径
     */
    private static void writeAsync(final String filePath, final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileUtil.appendConentFile(msg, filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
