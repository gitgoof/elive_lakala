package com.test.gf.myjnitest;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.test.gf.math.MyMathTool;
import com.test.gf.tool.BugFixManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    static int sdfsafda=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    TextView tv = (TextView) findViewById(R.id.sample_text);
    }
    public void onclick(View view){
    }

    public void toReplace(View view){
        File dexFile =new File(Environment.getExternalStorageDirectory(),"repp.dex");
        BugFixManager.getInstance().fix(dexFile,this);
    }

    public void toMath(View view){
        // 进行计算
        MyMathTool myMathTool = new MyMathTool();
        int v = myMathTool.calc();
        Log.i("g--","我们的计算结果:" + v);

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
}
