package com.lakala.platform.swiper.mts.paypwd;

import android.os.Bundle;
import android.view.View;

import com.lakala.library.util.PhoneNumberUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;

/**
 * Created by lianglong on 14-6-6.
 */
public class PayPwdForgetActivity extends BaseActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plat_activity_pay_pwd_forget);
        navigationBar.setTitle(R.string.plat_password_security_miss_text);
        findViewById(R.id.id_btn_servicephone).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //拨打客服电话
                PhoneNumberUtil.callPhone(PayPwdForgetActivity.this, "4007666666");
            }
        });
    }

}
