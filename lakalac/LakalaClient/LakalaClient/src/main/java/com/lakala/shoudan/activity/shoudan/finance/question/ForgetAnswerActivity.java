package com.lakala.shoudan.activity.shoudan.finance.question;

import android.os.Bundle;

import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;

/**
 * Created by LMQ on 2015/10/14.
 * 忘记答案
 */
public class ForgetAnswerActivity extends AppBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_answer);
        initUI();
    }

    @Override
    protected void initUI() {
        super.initUI();
        navigationBar.setTitle("忘记密保答案");
    }
}
