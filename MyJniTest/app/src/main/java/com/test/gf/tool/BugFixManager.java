package com.test.gf.tool;

import android.content.Context;
import android.util.Log;

import com.test.gf.okclass.MethodReplace;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexFile;

/**
 * Created by gaofeng on 2017/3/15.
 */
public class BugFixManager {
    private static BugFixManager ourInstance = new BugFixManager();

    static {
        System.loadLibrary("native-lib");
    }
    public static BugFixManager getInstance() {
        return ourInstance;
    }

    private BugFixManager() {
    }

    /**
     * 对repp.dex 进行加载和替换原有方法。
     * 该方法的步骤：
     * 1. 拷贝dex文件到内部存储器。
     * 2. 加载dex文件中正确的class到虚拟机。
     * 3. 根据dex文件中的class上的注解信息。找到需要修复的错误的类
     * 4.  将错误的方法与正确的方法，一起传给native层。完成方法替换
     * @param dexFile
     * @param context
     */
    public void fix(File dexFile,Context context){
        // copy
        File okDexFile = new File(context.getFilesDir(),dexFile.getName());
        try {

            DexFile dexFile1 = DexFile.loadDex(dexFile.getAbsolutePath(),okDexFile.getAbsolutePath(),Context.MODE_PRIVATE);

            Enumeration<String> entries = dexFile1.entries();

            while ( entries.hasMoreElements()){
                String key = entries.nextElement();
                Class<?> okClass = dexFile1.loadClass(key,getClass().getClassLoader());

                if(okClass ==null ){
                    continue;
                }
                findBugMethod(okClass);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据dex文件中的class的注解，找到需要修复的错误的类
     * @param okClass
     */
    private void findBugMethod(Class<?> okClass){
        // 获取该类中的所有方法。
        Method[] methods = okClass.getDeclaredMethods();
        for(Method method:methods){
            // 获取注解。已经指明注解类型。
            MethodReplace methodReplace = method.getAnnotation(MethodReplace.class);
            if(methodReplace == null){
                continue;
            }

            // 将要修复的错误类和错误方法
            String bugClassName = methodReplace.className();
            String bugMethodName = methodReplace.methodName();
            Log.i("g--","将要修复的bugclassname:" + bugClassName);
            Log.i("g--","将要修复的bugMethodname:" + bugMethodName);

            // 获取Bug Method 实例.也就是该方法的标识。不一定是实例参数.
            // 该方法在内存卡的映射地址
            try {
                Method bugMethod = Class.forName(bugClassName).getDeclaredMethod(bugMethodName,method.getParameterTypes());
                if(bugMethod != null){
                    replaceArt(bugMethod,method);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
    private native void replaceArt(Method bug,Method ok);
    public String tellMeCallMethod(){
        return "it is my java!";
    }
}
