package com.lakala.shoudan.activity.merchant.register;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.main.MainActivity;

public class RegisterSuccessActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoudan_register_success_tips);
        initUI();
    }

    protected void initUI() {
        navigationBar.setBackBtnVisibility(View.GONE);
        navigationBar.setTitle("商户开通");
        TextView buttonLeft = (TextView) findViewById(R.id.button_left);
        buttonLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BusinessLauncher.getInstance().clearTop(MainActivity.class);
            }
        });

    }


    @Override
    public void onBackPressed() {
        BusinessLauncher.getInstance().clearTop(MainActivity.class);
    }

}
