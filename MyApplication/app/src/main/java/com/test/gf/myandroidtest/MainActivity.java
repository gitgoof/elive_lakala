package com.test.gf.myandroidtest;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.TextView;

import com.test.gf.jni.JniMethod;
import com.test.gf.math.MyMathTool;
import com.test.gf.tool.BugFixManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ThreadLocal threadLocal;
        SparseArray<String> stringSparseArray;

        TextView textView = new TextView(this);
        textView.setText("my test!");
        getApplication();
        getApplicationContext();
//        overridePendingTransition();
        mTvNativeResult = (TextView) findViewById(R.id.tv_native_result_show);



    }
    private TextView mTvNativeResult;

    public void clickNative(View view){
        StringBuilder stringBuilder = new StringBuilder("结果：");
        // TODO 调用native方法
        stringBuilder.append("目前还没处理");
        JniMethod jniMethod = new JniMethod(this);
        String string = jniMethod.jniAdd(1,2);
        stringBuilder.append(string);
        mTvNativeResult.setText(stringBuilder.toString());
    }

    public void clickReturnJava(View view){
        StringBuilder stringBuilder = new StringBuilder("c--> java result：");
        // TODO 调用native方法
//        stringBuilder.append("目前还没处理");
        JniMethod jniMethod = new JniMethod(this);
        String string = jniMethod.jniAdd(1,2);
        stringBuilder.append(string);
        mTvNativeResult.setText(stringBuilder.toString());
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);

    }

    /**
     * 这里进行替换，替换 有问题的方法。
     * @param view
     */
    public void toReplace(View view){
        // 进行替换

        File dexFile =new File(Environment.getExternalStorageDirectory(),"repp.dex");
        BugFixManager.getInstance().fix(dexFile,this);

//        toCopyDex();
    }

    private void toCopyDex(){

        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            inputStream = this.getAssets().open("rep.dex");
            File file = new File(Environment.getExternalStorageDirectory(),"repp.dex");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            int length = 0;
            byte[] buffer = new byte[1024];
            while((length = inputStream.read(buffer)) > 0){
                fileOutputStream.write(buffer,0,length);
            }
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fileOutputStream !=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 调用计算。该计算器原始程序有bug。会报异常，这里进行热替换
     * @param view
     */
    public void toMath(View view){
        // 进行计算
        MyMathTool myMathTool = new MyMathTool();
        int v = myMathTool.calc();
        Log.i("g--","我们的计算结果:" + v);
    }

    public void toListAct(View view){
        startActivity(new Intent(MainActivity.this,ListViewActivity.class));
    }
}
