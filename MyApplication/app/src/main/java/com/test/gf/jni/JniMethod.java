package com.test.gf.jni;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gaofeng on 2017/3/9.
 */

public class JniMethod {
    private Context mContext;
    public JniMethod(Context context){
        mContext=context;
    }
static {
    System.loadLibrary("JniTest");
}

    public native String jniAdd(int x, int y);

    public native void getName();

    public String getJavaData(){
        if(mContext.getResources()==null){
            Log.i("g---","getResources==null");
        } else {
            Log.i("g---","getResources!=null");
        }
        Toast.makeText(mContext,"弹出框",Toast.LENGTH_SHORT).show();
        return "it is my java!";
    }
    public static String getApplicationName(){
        return "MyApplication---doit";
    }
}
