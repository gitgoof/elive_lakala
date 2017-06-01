package com.lakala.platform.swiper.mts.paypwd;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lakala.core.http.HttpRequest;
import com.lakala.core.http.IHttpRequestEvents;
import com.lakala.library.exception.BaseException;
import com.lakala.library.util.StringUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;
import com.lakala.platform.activity.BaseActionBarActivity;

/**
 * Created by lianglong on 14-6-3.
 */
public class PayPwdChangeActivity extends BaseActionBarActivity implements TextWatcher {
    private PayPwdInputView mPasswordEditText;

    private Button submitButton;

    /**
     * 提交新的交易密码
     */
    private IHttpRequestEvents responseHandler = new IHttpRequestEvents(){
        @Override
        public void onSuccess(HttpRequest request) {
            super.onSuccess(request);
            Intent intent =new Intent(PayPwdChangeActivity.this,PayPwdSetActivity.class);
            intent.putExtra("oldPassword",mPasswordEditText.getPassword().trim());
            startActivity(intent);
            finish();
        }

        @Override
        public void onFailure(HttpRequest request, BaseException exception) {
            super.onFailure(request, exception);
            String response = request.getResponseHandler().getResultMessage();
            ToastUtil.toast(mContext, response);
        }

        @Override
        public void onFinish(HttpRequest request) {
            super.onFinish(request);
            mPasswordEditText.clear();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plat_activity_pay_pwd_set);
        navigationBar.setTitle("修改拉卡拉支付密码");

        //todo PRD上无Button,此处预留一个button,以防变更需求
        submitButton = (Button) findViewById(R.id.id_common_guide_button);
        submitButton.setText(R.string.com_next);
        submitButton.setOnClickListener(this);
        submitButton.setEnabled(false);
        //调整ui与ios相同
        submitButton.setVisibility(View.GONE);

        TextView tvNotice = (TextView) findViewById(R.id.notice);
        tvNotice.setText("请输入当前支付密码");

        mPasswordEditText   = (PayPwdInputView) findViewById(R.id.id_register_input_pay_pwd);
        mPasswordEditText.addTextChangedListener(this);
        mPasswordEditText.openCKbd();
    }

    @Override
    protected void onViewClick(View view) {
        super.onViewClick(view);
        if (!isInputValid() || view.getId() != R.id.id_common_guide_button) {
            return;
        }
        submit();
    }

    /**
     * 输入是否合法
     */
    private boolean isInputValid() {
        String text = mPasswordEditText.getPassword();
        if (text == null || StringUtil.isEmpty(text)) {
            return false;
        }
        if (text.length() != 6) {
            ToastUtil.toast(this, R.string.plat_input_pay_password_error);
            return false;
        }
        return true;
    }

    /**
     * 提交请求验证
     */
    private void submit(){
        RegisterRequestFactory.checkPayPwd(this, mPasswordEditText.getPassword()).setIHttpRequestEvents(responseHandler).execute();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean enable = s != null && s.toString().trim().length() == 6;
        submitButton.setEnabled(enable);
        //调整ui与ios相同
        if (enable){
            mPasswordEditText.closeCKbd();
            onViewClick(submitButton);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

