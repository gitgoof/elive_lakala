package com.lakala.platform.swiper.mts.paypwd;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.ui.component.NormalCountdownInputBoxView;

/**
 * Created by lianglong on 14-6-5.
 */
public class PayPwdMessageValidActivity extends BaseActionBarActivity implements TextWatcher {
    private Button submitButton;
    private NormalCountdownInputBoxView messageView;

    /**
     * 验证验证码
     */
    private IHttpRequestEvents messageValidHandler = new IHttpRequestEvents(){
        @Override
        public void onSuccess(HttpRequest request) {
            super.onSuccess(request);
            startActivity(new Intent(PayPwdMessageValidActivity.this,PayPwdSetActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plat_activity_pay_pwd_message_check);
        navigationBar.setTitle(R.string.plat_password_security_find_pay_pwd_title);

        submitButton = (Button) findViewById(R.id.id_common_guide_button);
        submitButton.setText(R.string.com_next);
        submitButton.setOnClickListener(this);
        submitButton.setEnabled(false);

        messageView = (NormalCountdownInputBoxView) findViewById(R.id.message_view);
        messageView.setGetVerifyCodeListener(this);
        messageView.getVerifycodeEdit().addTextChangedListener(this);
        String mobile = ApplicationEx.getInstance().getUser().getLoginName();
        messageView.setTitle(String.format(getString(R.string.plat_phone_no_get_sms_code), mobile.substring(mobile.length() - 4)));
        messageView.startCountdown();
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        if (view == submitButton){
            CommonRequestFactory.createVerifySMSCode(this, ApplicationEx.getInstance().getUser().getLoginName(), messageView.getVerifyCodeText(), "0", "228202")
                    .setIHttpRequestEvents(messageValidHandler)
                    .execute();
        } else if (view == messageView.getVerfyCodeButton()){
            IHttpRequestEvents getSMS = new IHttpRequestEvents(){

                @Override
                public void onSuccess(HttpRequest request) {
                    super.onSuccess(request);
                    messageView.getVerifycodeEdit().setText("");
                    messageView.startCountdown();
                }
            };

            CommonRequestFactory.createGetSMSCode(this, ApplicationEx.getInstance().getUser().getLoginName(), "0", "228202")
                    .setProgressMessage(this.getString(R.string.plat_send_message_verifycode))
                    .setIHttpRequestEvents(getSMS)
                    .execute();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        submitButton.setEnabled(s != null && s.toString().length() == 6);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
