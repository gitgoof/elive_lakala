package com.test.gf.jni;

import java.lang.reflect.Method;

/**
 * Created by gaofeng on 2017/3/9.
 */

public class MyNativeMethod {
//
//    static {
//        System.loadLibrary("native-lib");
//    }
    public native String getAdd(int x,int y);
    private native void replaceArt(Method bug, Method ok);
}
