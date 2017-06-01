package com.lakala.library.exception;

import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import com.lakala.library.util.FileSynchronizedHandle;
import com.lakala.library.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

/**
 * 程序Crash处理类,当程序Crash时记录相关的日志
 *
 * Created by jerry on 13-12-14.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";

    /**
     * 保存crashHandler实例
     */
    private static CrashHandler mInstance = null;

    /**
     * 程序未捕获异常处理类
     */
    private UncaughtExceptionHandler defaultHandler = null;

    /**
     * 未捕获程序异常监听实例
     */
    private OnHandleCrashExceptionListener mHandleCrashExceptionListener;

    /**
     * 文件IO同步操作类
     */
    private FileSynchronizedHandle mFileSynchronizedHandle;

    /**
     * 崩溃日志文件
     */
    private File mCrashFile;

    /**
     * 监听程序处理未捕获异常
     */
    public interface OnHandleCrashExceptionListener {
        void handleException(Throwable ex);
    }

    /**
     * 初始化,在全局程序 ApplicationExtension 调用
     */
    public synchronized static void init(OnHandleCrashExceptionListener mHandleCrashExceptionListener,File crashFile) {
        if (mInstance == null) {
            mInstance = new CrashHandler(mHandleCrashExceptionListener,crashFile);
        }
    }

    private CrashHandler(OnHandleCrashExceptionListener mHandleCrashExceptionListener,File crashFile) {
        this.mHandleCrashExceptionListener = mHandleCrashExceptionListener;
        this.mCrashFile = crashFile;
        //文件IO同步操作类
//        mFileSynchronizedHandle = new FileSynchronizedHandle(crashFile);
        //初始化捕获异常类
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //程序未捕获异常崩溃时触发异常

        LogUtil.print("Attention #$%$%$",ex);

        if (mHandleCrashExceptionListener != null) {
            mHandleCrashExceptionListener.handleException(ex);
        }
        if (defaultHandler != null)
            defaultHandler.uncaughtException(thread, ex);
    }

    /**
     * 程序崩溃时，收集相关信息并写入文件
     *
     * @param throwable 异常
     */
    public void writeFileAsyn4CrashInfo(
                                     final Throwable throwable,
                                     final PackageInfo pi,
                                     final String date,
                                     final String userName,
                                     final String versionName) throws BaseException, IOException, IllegalAccessException {

        if (throwable == null || pi == null ||
                date == null || userName == null || versionName == null )
            return;
        //异步写入奔溃日志
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    writeFile4CrashInfo(throwable,pi,date,userName,versionName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    /**
     * 程序崩溃时，收集相关信息并写入文件
     *
     * @param throwable 异常
     */
    private void writeFile4CrashInfo(Throwable throwable,
                                    PackageInfo pi,
                                    String date,
                                    String userName,
                                    String versionName) throws BaseException, IOException, IllegalAccessException {

        //组装日志信息
        StringBuffer sb = new StringBuffer();
        sb.append("\n").append(date).append("\n")
                .append("userName:")
                .append(userName).append("\n")
                .append("versionName:")
                .append(versionName).append("\n");

        //记录崩溃日志基本信息
        mFileSynchronizedHandle.writeAppendContentSynchronized(sb.toString());
        //收集用户信息
        Properties deviceCrashInfo = new Properties();
        if (pi != null) {
            deviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
            deviceCrashInfo.put(VERSION_CODE, pi.versionCode + "");
        }
        mFileSynchronizedHandle.collectCrashDeviceInfo(deviceCrashInfo, sb.toString());
        //追加崩溃错误信息
        mFileSynchronizedHandle.writeAppendThrowbleInfoSynchronized(throwable);
    }
}
