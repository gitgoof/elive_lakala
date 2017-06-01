package com.test.gf.mygroupviewtest;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by gaofeng on 2017/4/24.
 */

public class MyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().getDecorView();

    }
}
