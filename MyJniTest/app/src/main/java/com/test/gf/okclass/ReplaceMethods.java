package com.test.gf.okclass;

/**
 * Created by gaofeng on 2017/3/15.
 */

public class ReplaceMethods {

    @MethodReplace(className = "com.test.gf.math.MyMathTool",methodName = "calc")
    public int calc(){
        int a=30;
        int b=5;
        return a/b;
    }
}
