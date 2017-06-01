package com.lakala.shoudan.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.avos.avoscloud.LogUtil;
import com.lakala.core.cordova.cordova.LOG;
import com.lakala.platform.common.CommonEncrypt;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.util.LoginUtil;

/**
 * Created by More on 15/3/3.
 */
public class AudioLoginActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_login);
        navigationBar.setVisibility(View.GONE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                audioLogin();
            }
        }, 2000);
    }

    private Handler handler = new Handler();

    private void audioLogin() {

        String userName = getIntent().getStringExtra("userName");

        String passWord = getIntent().getStringExtra("passWord");

        LoginUtil.login(userName, CommonEncrypt.loginEncrypt(passWord), AudioLoginActivity.this);//注册成功之后立即登录

    }

    @Override
    public void onBackPressed() {

    }
}
